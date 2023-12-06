package ui.component

import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier


@Composable
fun FloatField(
    value: Float,
    onValueChange: (Float) -> Unit,
    label: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    var strValue by remember { mutableStateOf(value.toString()) }
    var invalidString by remember { mutableStateOf(true) }

    TextField(
        strValue,
        onValueChange = {
            strValue = it
            val floatValue = it.toFloatOrNull()
            invalidString = floatValue == null
            if (floatValue != null) {
                onValueChange(floatValue)
            }
        },
        isError = invalidString,
        singleLine = true,
        label = label,
        enabled = enabled,
        modifier = modifier
    )
}