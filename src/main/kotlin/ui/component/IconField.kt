package ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog


private val BUILTIN_ICONS = listOf(
    "icons/walk.svg",
    "icons/car.svg",
    "icons/bike.svg",
    "icons/cost.svg",
    "icons/schedule.svg",
    "icons/time.svg",
    "icons/oev.svg",
)
private val ICON_BUTTON_SIZE = 64.dp

@Composable
fun IconField(icon: MutableState<String?>, modifier: Modifier = Modifier) {
    var windowOpen by remember { mutableStateOf(false) }
    val iconValue by icon
    Button(
        onClick = { windowOpen = true }, modifier = modifier.size(ICON_BUTTON_SIZE),
        contentPadding = PaddingValues(4.dp)
    ) {
        if (iconValue != null) {
            Icon(painterResource(iconValue!!), null)
        } else {
            Text("X")
        }
    }
    if (windowOpen) {
        IconSelectWindow(icon) {
            windowOpen = false
        }
    }
}

@Composable
fun IconSelectWindow(iconState: MutableState<String?>, closeCallback: () -> Unit) {
    val selectedIcon: MutableState<String?> = remember { mutableStateOf(iconState.value) }

    Dialog(visible = true, onCloseRequest = { closeCallback() }) {
        Column {
            Row {
                for (icon in BUILTIN_ICONS) {
                    IconMenuButton(selectedIcon, icon)
                }
            }
            Button(onClick = {
                iconState.value = selectedIcon.value
                closeCallback()
            }) {
                Text("Auswählen")
            }
        }
    }
}

@Composable
private fun IconMenuButton(selectedIcon: MutableState<String?>, icon: String) {
    val selected = selectedIcon.value == icon
    OutlinedButton(
        onClick = {
            selectedIcon.value = icon
        },
        modifier = Modifier.size(ICON_BUTTON_SIZE),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.surface,
            contentColor = MaterialTheme.colors.primary
        ),
        elevation = null,
        border = if (selected) BorderStroke(2.dp, MaterialTheme.colors.primary) else null,
        contentPadding = PaddingValues(4.dp)
    ) {
        Icon(painterResource(icon), null)
    }
}