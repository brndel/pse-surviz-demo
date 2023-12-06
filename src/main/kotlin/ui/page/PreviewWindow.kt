package ui.page

import Project
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import render.ImageCreator
import java.util.TimerTask
import java.util.concurrent.CountDownLatch

@Composable
fun PrevievWindow(project: Project, modifier: Modifier) {
    val imageInput = project.getImageInput(0, 0)
    Box(modifier = modifier.background(MaterialTheme.colors.surface).fillMaxHeight().padding(4.dp)) {
        if (imageInput != null) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val imageCreator = ImageCreator()
                for (input in imageInput) {
                    val image = imageCreator.createImage(input)
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Image(image, null, Modifier.padding(4.dp))
                    }
                }
            }
        } else {
            Text("???")
        }
    }
}