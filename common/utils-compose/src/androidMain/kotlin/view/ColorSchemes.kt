package view

import PlatformConfiguration
import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi
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
    return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        try {
            dynamicDarkColorScheme(platformConfiguration.androidContext)
        } catch (e: Throwable) {
            null
        }
    } else {
        null
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
actual fun dynamicLightScheme(): ColorScheme? {
    val platformConfiguration: PlatformConfiguration = Inject.instance()
    return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        try {
            dynamicLightColorScheme(platformConfiguration.androidContext)
        } catch (e: Throwable) {
            null
        }
    } else {
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
            darkIcons = viewManager.tint.value != ThemeTint.Dark
        )

    }
}