package ui.page.configurator

import Project
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.jetbrains.skia.EncodedImageFormat
import render.ImageCreator
import java.io.File
import java.time.Instant
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@Composable
fun ExportPage(project: Project, modifier: Modifier) {
    var exportProgress by remember { mutableStateOf<Double?>(null) }
    var exportDuration by remember { mutableStateOf<Duration?>(null) }

    Column(modifier = modifier) {
        Text("Export")
        Button(onClick = {
            testExport(project, "/home/armin/surviz_test", progressCallback = {
                exportProgress = it
            }, doneCallback = {
                exportProgress = null
                exportDuration = it
            })
        }) {
            Text("Test export speed")
        }
        exportDuration?.let {
            Text("Exported in ${it.inWholeSeconds}.${(it.inWholeMilliseconds%1000) / 100}s")
        }
    }

    exportProgress?.let {
        val progress by animateFloatAsState(it.toFloat(), tween(50, easing = EaseOutCirc))
        Dialog(visible = true, onCloseRequest = {}, title = "Exporting") {
            Surface(
                modifier = Modifier.padding(32.dp)
            ) {
                LinearProgressIndicator(progress, modifier = Modifier.width(256.dp))
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
fun testExport(
    project: Project,
    basePath: String,
    progressCallback: (Double) -> Unit,
    doneCallback: (Duration) -> Unit
) {
    val imageCreator = ImageCreator()


    val thread = Thread {
        println("exporting images")

        val time = measureTime {
            for (i in 0..20) {
                val input = project.getImageInput(0, 0)
                for ((idx, option) in input!!.withIndex()) {
                    val image = imageCreator.createImage(option)
                    val path = "${basePath}/img_${i}_${idx}.png"
                    saveImage(image, path)
                }

                progressCallback(i / 20.0)
            }
        }

        println("finished in ${time.inWholeMilliseconds}ms")
        doneCallback(time)
    }

    thread.start()
}

/*
    ERGEBNISSE:
    Mit 3 Auswahloptionen, 2 Einzelwertattributen und max 3 Zeitattributen
    Für 100 Situationen: 13281 ms = 13s

    Erste Zeile aus der Beispieldatei:
    100x exportiert: 26929 ms = 26 s
 */

fun saveImage(image: ImageBitmap, pathname: String): Boolean {
    val pixels = image.asSkiaBitmap()

    val skiaImage = org.jetbrains.skia.Image.makeFromBitmap(pixels);
    val data = skiaImage.encodeToData(EncodedImageFormat.PNG);

    return if (data != null) {
        File(pathname).writeBytes(data.bytes)
        true
    } else {
        false
    }
}