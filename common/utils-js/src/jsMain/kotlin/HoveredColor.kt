import emotion.react.css
import mui.material.Box
import mui.material.Fab
import mui.material.FabColor
import mui.material.FabVariant
import mui.material.Typography
import mui.material.styles.createTheme
import mui.system.ThemeOptions
import mui.system.sx
import react.FC
import web.cssom.AccentColor
import web.cssom.AlignSelf
import web.cssom.BackgroundColor
import web.cssom.Color
import web.cssom.ColorProperty
import web.cssom.ColorScheme
import web.cssom.rgb
import web.prompts.alert

fun calculateBrightness(color: Color): Double {
    val kColor = kotlinx.css.Color(color.toString()).lighten(0).value
    val rgbList = kColor.removePrefix("rgba(").removeSuffix("1.0)").split(", ")
    val r = rgbList[0].toInt()
    val g = rgbList[1].toInt()
    val b = rgbList[2].toInt()

    // Формула для определения яркости (Luma)
    return 0.299 * r + 0.587 * g + 0.114 * b
}

fun Color.hv(value: Int? = null) = hoveredColor(this, value)

fun hoveredColor(color: Color, value: Int? = null): Color {
    val brightness = calculateBrightness(color)
    // Регулировка значения освещенности в зависимости от начальной яркости
    val kColor = if (brightness > 128) {
        kotlinx.css.Color(color.toString()).darken(value ?: 7).value
    } else {
        kotlinx.css.Color(color.toString()).lighten(value ?: 13).value
    }

    val rgbList = kColor.removePrefix("rgba(").removeSuffix("1.0)").split(", ")
    return rgb(rgbList[0].toInt(), rgbList[1].toInt(), rgbList[2].toInt())
}