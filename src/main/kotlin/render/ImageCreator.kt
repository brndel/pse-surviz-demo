package render

import LineType
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.useResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextPainter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.createFontFamilyResolver
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import org.jetbrains.skia.Data
import org.jetbrains.skia.svg.*

class ImageCreator {
    private val iconCache = mutableMapOf<String, ImageBitmap?>()


    fun createImage(input: ImageInput): ImageBitmap {
        val image = ImageBitmap(800, 128)
        val canvas = Canvas(image)

        drawText(canvas, input.name, input.color, Offset(0.0F, 0.0F), textType = TextType.Title)

        for ((index, singleValue) in input.singleValue.withIndex()) {
            if (singleValue != null) {
                val position = Offset(index * 68.0F, 28.0F)
                if (singleValue.icon != null) {
                    drawIcon(canvas, singleValue.icon, input.color, position)
                }
                drawText(canvas, singleValue.text, input.color, position + Offset(0.0F, 64.0F), width = 64)
            }
        }

        val endPosition = input.singleValue.size * 68.0F

        val linePaint = Paint().apply {
            style = PaintingStyle.Stroke
            strokeWidth = 1.0F
            color = input.color
        }

        canvas.drawLine(
            Offset(endPosition, 32.0F),
            Offset(endPosition, 120.0F),
            linePaint
        )

        var position = Offset(endPosition + 16.0F, 80.0F)
        for (timeSection in input.timeline) {
            if (timeSection.length == 0.0) {
                continue
            }
            val length = timeSection.length * 32.0F
            drawTimeSection(
                canvas,
                timeSection.icon,
                "${timeSection.length.toInt()} min",
                input.color,
                position,
                length.toFloat(),
                timeSection.lineType
            )
            position += Offset(length.toFloat(), 0.0F)
        }

        drawTimeDivider(canvas, input.color, position)

        return image
    }

    private fun drawIcon(canvas: Canvas, icon: String, color: Color, position: Offset) {
        val image = loadIcon(icon) ?: return
        val paint = Paint()
        paint.colorFilter = ColorFilter.colorMatrix(
            ColorMatrix(
                floatArrayOf(
                    0.0F, 0.0F, 0.0F, 0.0F, color.red,
                    0.0F, 0.0F, 0.0F, 0.0F, color.green,
                    0.0F, 0.0F, 0.0F, 0.0F, color.blue,
                    0.0F, 0.0F, 0.0F, color.alpha, 0.0F,
                )
            )
        )

        canvas.drawImage(image, position, paint)
    }

    private fun loadIcon(icon: String): ImageBitmap? {
        if (iconCache.containsKey(icon)) {
            return iconCache[icon]
        }

        val iconImage = loadSvgIcon(icon)
        iconCache[icon] = iconImage
        return iconImage
    }

    @OptIn(ExperimentalTextApi::class)
    private fun drawText(
        canvas: Canvas,
        text: String,
        color: Color,
        position: Offset,
        textType: TextType = TextType.Label,
        width: Int? = null
    ) {
        val style = TextStyle(
            color = color,
            fontSize = textType.fontSize,
            fontWeight = textType.fontWeight,
            textAlign = if (width != null) TextAlign.Center else null
        )

        val measurer = TextMeasurer(createFontFamilyResolver(), Density(1.0F), LayoutDirection.Ltr)

        val hello = measurer.measure(
            text,
            style,
            constraints = Constraints(minWidth = width ?: 0, maxWidth = width ?: Constraints.Infinity)
        )

        canvas.save()
        canvas.translate(position.x, position.y)
        TextPainter.paint(canvas, hello)
        canvas.restore()
    }

    private enum class TextType(val fontSize: TextUnit, val fontWeight: FontWeight) {
        Title(20.sp, FontWeight.Normal),
        Label(14.sp, FontWeight.Normal);
    }

    private fun drawTimeSection(
        canvas: Canvas,
        icon: String?,
        text: String,
        color: Color,
        position: Offset,
        length: Float,
        lineType: LineType
    ) {
        val centerPos = position + Offset(length / 2.0F, 0.0F)
        val endPos = position + Offset(length, 0.0F)

        drawTimeDivider(canvas, color, position)

        val paint = Paint()
        paint.style = PaintingStyle.Stroke
        paint.color = color
        paint.strokeWidth = 1.0F
        paint.pathEffect = lineType.pathEffect
        canvas.drawLine(position, endPos, paint)

        if (icon != null) {
            val iconPos = centerPos + Offset(-32.0F, -64.0F - 8.0F)
            drawIcon(canvas, icon, color, iconPos)
        }
        val textPos = centerPos + Offset(-32.0F, 0.0F + 8.0F)
        drawText(canvas, text, color, textPos, width = 64)
    }

    private fun drawTimeDivider(canvas: Canvas, color: Color, position: Offset) {
        val paint = Paint()
        paint.style = PaintingStyle.Stroke
        paint.color = color
        paint.strokeWidth = 1.0F
        canvas.drawLine(position + Offset(0.0F, -4.0F), position + Offset(0.0F, 4.0F), paint)
    }
}

private fun loadSvgIcon(icon: String): ImageBitmap? {
    val svg = try {
        useResource(icon) {
            SVGDOM(Data.makeFromBytes(it.readAllBytes()))
        }
    } catch (e: Exception) {
        println("Exception while loading resource $e")
        return null
    }

    val image = ImageBitmap(64, 64, hasAlpha = true)
    val canvas = Canvas(image)

    svg.root?.width = SVGLength(64.0F, SVGLengthUnit.PX)
    svg.root?.height = SVGLength(64.0F, SVGLengthUnit.PX)
    svg.root?.preserveAspectRatio = SVGPreserveAspectRatio(SVGPreserveAspectRatioAlign.XMID_YMID)
    svg.render(canvas.nativeCanvas)

    return image
}