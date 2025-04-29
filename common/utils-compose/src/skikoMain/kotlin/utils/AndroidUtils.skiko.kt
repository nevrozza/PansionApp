package utils

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

@Composable
actual fun dynamicDarkScheme(): ColorScheme? = null

@Composable
actual fun dynamicLightScheme(): ColorScheme? = null

@Composable
actual fun StatusBarColorFix() {}

@Composable
actual fun LockScreenOrientation(orientation: Int) {}
