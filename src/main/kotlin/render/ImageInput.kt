package render

import LineType
import androidx.compose.ui.graphics.Color

data class ImageInput(
    val name: String,
    val color: Color,
    val singleValue: List<SingleValue?>,
    val timeline: List<TimeValue>
) {
    data class SingleValue(val icon: String?, val text: String)
    data class TimeValue(val icon: String?, val length: Double, val lineType: LineType)
}
