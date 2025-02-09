package utils

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State

@Composable
expect fun dynamicDarkScheme(): ColorScheme?

@Composable
expect fun dynamicLightScheme(): ColorScheme?

@Composable
expect fun StatusBarColorFix()

@Composable
expect fun LockScreenOrientation(orientation: Int)

@Composable
expect fun rememberImeState(): State<Boolean>