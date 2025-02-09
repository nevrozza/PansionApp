package utils

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
actual fun dynamicDarkScheme(): ColorScheme? = null

@Composable
actual fun dynamicLightScheme(): ColorScheme? = null

@Composable
actual fun StatusBarColorFix() {}

@Composable
actual fun LockScreenOrientation(orientation: Int) {}

@Composable
actual fun rememberImeState(): State<Boolean> {
    return remember { mutableStateOf(false) }
}