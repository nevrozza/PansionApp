package utils

import PlatformConfiguration
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.view.ViewTreeObserver
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import di.Inject
import view.LocalViewManager
import view.ThemeTint

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

@Composable
actual fun rememberImeState(): State<Boolean> {
    val imeState = remember { mutableStateOf(false) }

    val view = LocalView.current

    DisposableEffect(key1 = view) {
        val listener = ViewTreeObserver.OnGlobalLayoutListener {
            val isKeyboardOpen = ViewCompat.getRootWindowInsets(view)
                ?.isVisible(WindowInsetsCompat.Type.ime()) ?: true
            imeState.value = isKeyboardOpen
        }
        view.viewTreeObserver.addOnGlobalLayoutListener(listener)
        onDispose {
            view.viewTreeObserver.removeOnGlobalLayoutListener(listener)
        }
    }
    return imeState
}

@Composable
actual fun LockScreenOrientation(orientation: Int) {
    val context = LocalContext.current
    DisposableEffect(orientation) {
        val activity = context.findActivity() ?: return@DisposableEffect onDispose {}
        val originalOrientation = activity.requestedOrientation
        activity.requestedOrientation = orientation
        onDispose {
            // restore original orientation when view disappears
            activity.requestedOrientation = originalOrientation
        }
    }
}

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null

    //ActivityInfo.SCREEN_ORIENTATION_PORTRAIT - 1
    //ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED  - -1
}