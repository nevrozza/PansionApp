package view

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import com.materialkolor.DynamicMaterialTheme
import com.materialkolor.PaletteStyle
import com.materialkolor.ktx.blend
import resources.GeologicaFont

@Composable
fun Color.blend(to: Color, amount: Float = .8f) = this.blend(to = to, amount = amount)

@Composable
fun AppTheme(content: @Composable () -> Unit) {

    val viewManager = LocalViewManager.current

    val font = when (viewManager.fontType.value) {
        FontTypes.Cursive.ordinal -> FontFamily.Cursive
        FontTypes.Default.ordinal -> FontFamily.Default
        FontTypes.Monospace.ordinal -> FontFamily.Monospace
        FontTypes.SansSerif.ordinal -> FontFamily.SansSerif
        FontTypes.Serif.ordinal -> FontFamily.Serif
        else -> GeologicaFont
    }
    viewManager.isDark.value = isThemeDark(isDarkPriority = false, tint = viewManager.tint.value)
    val fontMultiply = viewManager.fontSize.value

    DynamicMaterialTheme(
        seedColor = viewManager.seedColor.value,
        useDarkTheme = viewManager.isDark.value,
        style = PaletteStyle.Rainbow,
        shapes = Shapes(),
        typography = scaledTypography(font, fontMultiply),
        animate = true,
        withAmoled = viewManager.isAmoled.value
    ) {
        val newBackgroundColor =
            if (!viewManager.isAmoled.value) MaterialTheme.colorScheme.background.blend(
                MaterialTheme.colorScheme.primary,
                if (viewManager.isDark.value) 0.007f else 0.08f
            )
            else MaterialTheme.colorScheme.background
        MaterialTheme(
            colorScheme = MaterialTheme.colorScheme.copy(
                surface = newBackgroundColor,
                background = newBackgroundColor,
                )
        ) {
            CompositionLocalProvider(
                LocalTextStyle provides LocalTextStyle.current.copy(fontFamily = font),
                // fix xiaomi dark theme with dark color
                LocalContentColor provides colorScheme.onBackground
            ) {
                content()
            }
        }
    }
}
