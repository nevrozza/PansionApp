package view

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.materialkolor.DynamicMaterialTheme
import com.materialkolor.PaletteStyle
import resources.GeologicaFont

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    val font = GeologicaFont

    val viewManager = LocalViewManager.current

    val isDark =
        if (viewManager.tint.value == ThemeTint.Auto) isSystemInDarkTheme()
        else viewManager.tint.value == ThemeTint.Dark

    DynamicMaterialTheme(
        seedColor = viewManager.seedColor.value, //Black + Monochrome
        useDarkTheme = isDark,
        style = PaletteStyle.Rainbow,
        typography = MaterialTheme.typography.copy(
            displayLarge = MaterialTheme.typography.displayLarge.copy(fontFamily = font),
            displayMedium = MaterialTheme.typography.displayMedium.copy(fontFamily = font),
            displaySmall = MaterialTheme.typography.displaySmall.copy(fontFamily = font),
            headlineLarge = MaterialTheme.typography.headlineLarge.copy(fontFamily = font),
            headlineMedium = MaterialTheme.typography.headlineMedium.copy(fontFamily = font),
            headlineSmall = MaterialTheme.typography.headlineSmall.copy(fontFamily = font),
            titleLarge = MaterialTheme.typography.titleLarge.copy(fontFamily = font),
            titleMedium = MaterialTheme.typography.titleMedium.copy(fontFamily = font),
            titleSmall = MaterialTheme.typography.titleSmall.copy(fontFamily = font),
            bodyLarge = MaterialTheme.typography.bodyLarge.copy(fontFamily = font),
            bodyMedium = MaterialTheme.typography.bodyMedium.copy(fontFamily = font),
            bodySmall = MaterialTheme.typography.bodySmall.copy(fontFamily = font),
            labelLarge = MaterialTheme.typography.labelLarge.copy(fontFamily = font),
            labelMedium = MaterialTheme.typography.labelMedium.copy(fontFamily = font),
            labelSmall = MaterialTheme.typography.labelSmall.copy(fontFamily = font),
        ),
        animate = true
    ) {
        content()
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