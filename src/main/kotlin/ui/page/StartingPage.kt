package ui.page

import Project
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StartingPage(
    project: MutableState<Project?>
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        modifier = Modifier.fillMaxSize(),
    ) {
        Button(
            onClick = {
            },
            enabled = false
        ) {
            Text("Neues Projekt")
        }
        Button(
            onClick = {
                println("load project");
                project.value = Project.createExampleProject();
            }
        ) {
            Text("Projekt laden")
        }
    }
}