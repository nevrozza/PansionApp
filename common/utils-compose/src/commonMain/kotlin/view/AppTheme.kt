package view

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

@Composable
fun AppTheme(colorScheme: ColorScheme, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

@Composable
fun colorSchemeGetter(isDark: Boolean, color: String): ColorScheme {
    return if(isDark) {
        when(color) {
            ThemeColors.Default.name -> defaultDarkPalette()
            ThemeColors.Green.name -> greenDarkPalette()
            ThemeColors.Red.name -> redDarkPalette()
            ThemeColors.Yellow.name -> yellowDarkPalette()
            ThemeColors.Dynamic.name -> dynamicDarkScheme() ?: defaultDarkPalette()
            else -> defaultDarkPalette()

        }
    } else {
        when(color) {
            ThemeColors.Default.name -> defaultLightPalette()
            ThemeColors.Green.name -> greenLightPalette()
            ThemeColors.Red.name -> redLightPalette()
            ThemeColors.Yellow.name -> yellowLightPalette()
            ThemeColors.Dynamic.name -> dynamicLightScheme() ?: defaultLightPalette()
            else -> defaultLightPalette()
        }
    }
}