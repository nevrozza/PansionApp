import androidx.compose.ui.graphics.Color

fun Color.hv() = hoveredColor(this)

fun calculateBrightness(color: Color): Double {
    val r = color.red
    val g = color.green
    val b = color.blue

    // Formula for calculating brightness (Luma)
    return 0.299 * r + 0.587 * g + 0.114 * b
}

fun hoveredColor(color: Color, value: Float? = null): Color {
    val brightness = calculateBrightness(color)


    val hoveredColor = if (brightness > 0.5) {
        Color(
            red = ((color.red - (value ?: (13 / 100f)))).coerceIn(0f, 1f),
            green = ((color.green - (value ?: (13 / 100f)))).coerceIn(0f, 1f),
            blue = ((color.blue - (value ?: (13 / 100f)))).coerceIn(0f, 1f)
        )
    } else {
        Color(
            red = ((color.red + (value ?: (20 / 100f)))).coerceIn(0f, 1f),
            green = ((color.green + (value ?: (20 / 100f)))).coerceIn(0f, 1f),
            blue = ((color.blue + (value ?: (20 / 100f)))).coerceIn(0f, 1f)
        )
    }

    return hoveredColor
}