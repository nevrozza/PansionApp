package view

import PlatformConfiguration
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import di.Inject

@Composable
actual fun dynamicDarkScheme(): ColorScheme? {
    val platformConfiguration: PlatformConfiguration = Inject.instance()
    return try {
        dynamicDarkColorScheme(platformConfiguration.androidContext)
    } catch (e: Throwable) {
        null
    }
}

@Composable
actual fun dynamicLightScheme(): ColorScheme? {
    val platformConfiguration: PlatformConfiguration = Inject.instance()
    return try {
        dynamicLightColorScheme(platformConfiguration.androidContext)
    } catch (e: Throwable) {
        null
    }
}


@Composable
actual fun StatusBarColorFix() {
    val viewManager = LocalViewManager.current
    val systemUiController = rememberSystemUiController()
    if(!isSystemInDarkTheme()) {

        systemUiController.setStatusBarColor(
            color = Color.Transparent,
            darkIcons = viewManager.tint.value != ThemeTint.Dark.name
        )

    }
}