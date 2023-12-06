import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import ui.page.ConfiguratorPage
import ui.page.StartingPage

@Composable
@Preview
fun App() {
    val project: MutableState<Project?> = mutableStateOf(null);

    MaterialTheme(colors = lightColors(surface = Color(0xFFDFDFDF))) {
        Box(Modifier.background(MaterialTheme.colors.background).fillMaxSize()) {
            project.value.let { projectValue ->
                if (projectValue == null) {
                    StartingPage(project)
                } else {
                    ConfiguratorPage(projectValue)
                }
            }
        }
    }
}


fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "PSE Program",
        state = rememberWindowState(width = 1800.dp, height = 1000.dp),
    ) {
        App()
    }
}
