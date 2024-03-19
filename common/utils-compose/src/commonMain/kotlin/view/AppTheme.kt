package view

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily

@Composable
fun AppTheme(colorScheme: ColorScheme, content: @Composable () -> Unit) {
    val font = FontFamily.SansSerif
    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
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
            )
    )
}

@Composable
fun colorSchemeGetter(isDark: Boolean, color: String): ColorScheme {
    return if (isDark) {
        when (color) {
            ThemeColors.Default.name -> defaultDarkPalette()
            ThemeColors.Green.name -> greenDarkPalette()
            ThemeColors.Red.name -> redDarkPalette()
            ThemeColors.Yellow.name -> yellowDarkPalette()
            ThemeColors.Dynamic.name -> dynamicDarkScheme() ?: defaultDarkPalette()
            else -> defaultDarkPalette()

        }
    } else {
        when (color) {
            ThemeColors.Default.name -> defaultLightPalette()
            ThemeColors.Green.name -> greenLightPalette()
            ThemeColors.Red.name -> redLightPalette()
            ThemeColors.Yellow.name -> yellowLightPalette()
            ThemeColors.Dynamic.name -> dynamicLightScheme() ?: defaultLightPalette()
            else -> defaultLightPalette()
        }
    }
}