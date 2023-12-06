import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProviderAtPosition


@Composable
fun ColorField(
    color: Color,
    onValueChange: (Color) -> Unit,
    modifier: Modifier = Modifier,
    label: (@Composable () -> Unit)? = null
) {
    var popupOpen by remember { mutableStateOf(false) }
    var hexColor by remember(color) { mutableStateOf(colorToHex(color)) }

    TextField(
        hexColor,
        onValueChange = {
            hexColor = it
            println("set color to $it")
        },
        singleLine = true,
        label = label,
        trailingIcon = {
            Button(
                onClick = {
                    popupOpen = true
                },
                modifier = Modifier.size(32.dp),
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(color),
                contentPadding = PaddingValues(0.dp),
                elevation = null
            ) {
                Text("V")
            }
            if (popupOpen) {
                Popup(
                    onDismissRequest = {
                        popupOpen = false
                    },
                    alignment = Alignment.TopStart,
                    focusable = true,
                    offset = IntOffset(0, 32)
                ) {
                    Card(elevation = 8.dp) {
                        Column(
                            modifier = Modifier.padding(4.dp).width(256.dp)
                        ) {
                            Slider(
                                color.red,
                                { onValueChange(color.copy(red = it)) },
                                valueRange = 0F..1F,
                                colors = SliderDefaults.colors(Color.Red, activeTrackColor = Color.Red)
                            )
                            Slider(
                                color.green,
                                { onValueChange(color.copy(green = it)) },
                                valueRange = 0F..1F,
                                colors = SliderDefaults.colors(Color.Green, activeTrackColor = Color.Green)
                            )
                            Slider(
                                color.blue,
                                { onValueChange(color.copy(blue = it)) },
                                valueRange = 0F..1F,
                                colors = SliderDefaults.colors(thumbColor = Color.Blue, activeTrackColor = Color.Blue)
                            )
                            Slider(
                                color.alpha,
                                { onValueChange(color.copy(alpha = it)) },
                                valueRange = 0F..1F,
                                colors = SliderDefaults.colors(Color.LightGray, activeTrackColor = Color.LightGray)
                            )
                        }
                    }
                }
            }
        }
    )
}

private fun colorToHex(color: Color): String {
    val red = (color.red * 255).toInt().toString(16).padStart(2, '0');
    val green = (color.green * 255).toInt().toString(16).padStart(2, '0');
    val blue = (color.blue * 255).toInt().toString(16).padStart(2, '0');
    return "$red$green$blue"
}