package view

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TooltipDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.window.PopupPositionProvider
import com.materialkolor.DynamicMaterialTheme
import com.materialkolor.PaletteStyle
import com.materialkolor.ktx.blend
import resources.GeologicaFont


@OptIn(ExperimentalMaterial3Api::class)
val popupPositionProvider: PopupPositionProvider
    @Composable
    get() = TooltipDefaults.rememberPlainTooltipPositionProvider()

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

    viewManager.isDark.value =
        if (viewManager.tint.value == ThemeTint.Auto) isSystemInDarkTheme()
        else viewManager.tint.value == ThemeTint.Dark
    if (!viewManager.isDark.value) {
        viewManager.isAmoled.value = false
    }
    val fontMultiply = viewManager.fontSize.value
    //kill me
    DynamicMaterialTheme(
        seedColor = viewManager.seedColor.value,
        useDarkTheme = viewManager.isDark.value,
        style = PaletteStyle.Rainbow,
        typography = MaterialTheme.typography.copy(
            displayLarge = MaterialTheme.typography.displayLarge.copy(
                fontFamily = font,
                fontSize = MaterialTheme.typography.displayLarge.fontSize * fontMultiply,
                lineHeight = MaterialTheme.typography.displayLarge.lineHeight * fontMultiply
            ),
            displayMedium = MaterialTheme.typography.displayMedium.copy(
                fontFamily = font,
                fontSize = MaterialTheme.typography.displayMedium.fontSize * fontMultiply,
                lineHeight = MaterialTheme.typography.displayMedium.lineHeight * fontMultiply
            ),
            displaySmall = MaterialTheme.typography.displaySmall.copy(
                fontFamily = font,
                fontSize = MaterialTheme.typography.displaySmall.fontSize * fontMultiply,
                lineHeight = MaterialTheme.typography.displaySmall.lineHeight * fontMultiply
            ),
            headlineLarge = MaterialTheme.typography.headlineLarge.copy(
                fontFamily = font,
                fontSize = MaterialTheme.typography.headlineLarge.fontSize * fontMultiply,
                lineHeight = MaterialTheme.typography.headlineLarge.lineHeight * fontMultiply
            ),
            headlineMedium = MaterialTheme.typography.headlineMedium.copy(
                fontFamily = font,
                fontSize = MaterialTheme.typography.headlineMedium.fontSize * fontMultiply,
                lineHeight = MaterialTheme.typography.headlineMedium.lineHeight * fontMultiply
            ),
            headlineSmall = MaterialTheme.typography.headlineSmall.copy(
                fontFamily = font,
                fontSize = MaterialTheme.typography.headlineSmall.fontSize * fontMultiply,
                lineHeight = MaterialTheme.typography.headlineSmall.lineHeight * fontMultiply
            ),
            titleLarge = MaterialTheme.typography.titleLarge.copy(
                fontFamily = font,
                fontSize = MaterialTheme.typography.titleLarge.fontSize * fontMultiply,
                lineHeight = MaterialTheme.typography.titleLarge.lineHeight * fontMultiply
            ),
            titleMedium = MaterialTheme.typography.titleMedium.copy(
                fontFamily = font,
                fontSize = MaterialTheme.typography.titleMedium.fontSize * fontMultiply,
                lineHeight = MaterialTheme.typography.titleMedium.lineHeight * fontMultiply
            ),
            titleSmall = MaterialTheme.typography.titleSmall.copy(
                fontFamily = font,
                fontSize = MaterialTheme.typography.titleSmall.fontSize * fontMultiply,
                lineHeight = MaterialTheme.typography.titleSmall.lineHeight * fontMultiply
            ),
            bodyLarge = MaterialTheme.typography.bodyLarge.copy(
                fontFamily = font,
                fontSize = MaterialTheme.typography.bodyLarge.fontSize * fontMultiply,
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * fontMultiply
            ),
            bodyMedium = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = font,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize * fontMultiply,
                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * fontMultiply
            ),
            bodySmall = MaterialTheme.typography.bodySmall.copy(
                fontFamily = font,
                fontSize = MaterialTheme.typography.bodySmall.fontSize * fontMultiply,
                lineHeight = MaterialTheme.typography.bodySmall.lineHeight * fontMultiply
            ),
            labelLarge = MaterialTheme.typography.labelLarge.copy(
                fontFamily = font,
                fontSize = MaterialTheme.typography.labelLarge.fontSize * fontMultiply,
                lineHeight = MaterialTheme.typography.labelLarge.lineHeight * fontMultiply
            ),
            labelMedium = MaterialTheme.typography.labelMedium.copy(
                fontFamily = font,
                fontSize = MaterialTheme.typography.labelMedium.fontSize * fontMultiply,
                lineHeight = MaterialTheme.typography.labelMedium.lineHeight * fontMultiply
            ),
            labelSmall = MaterialTheme.typography.labelSmall.copy(
                fontFamily = font,
                fontSize = MaterialTheme.typography.labelSmall.fontSize * fontMultiply,
                lineHeight = MaterialTheme.typography.labelSmall.lineHeight * fontMultiply
            )
        ),
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
                LocalTextStyle provides LocalTextStyle.current.copy(fontFamily = font)
            ) {
                content()
            }
        }
    }
}


//@Composable
//fun colorSchemeGetter(isDark: Boolean, color: String): ColorScheme {
//    return if (isDark) {
//        when (color) {
//            ThemeColors.Default.name -> defaultDarkPalette()
//            ThemeColors.Green.name -> greenDarkPalette()
//            ThemeColors.Red.name -> redDarkPalette()
//            ThemeColors.Yellow.name -> yellowDarkPalette()
//            ThemeColors.Dynamic.name -> dynamicDarkScheme() ?: defaultDarkPalette()
//            else -> defaultDarkPalette()
//
//        }
//    } else {
//        when (color) {
//            ThemeColors.Default.name -> defaultLightPalette()
//            ThemeColors.Green.name -> greenLightPalette()
//            ThemeColors.Red.name -> redLightPalette()
//            ThemeColors.Yellow.name -> yellowLightPalette()
//            ThemeColors.Dynamic.name -> dynamicLightScheme() ?: defaultLightPalette()
//            else -> defaultLightPalette()
//        }
//    }
//}