package view

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable

@Composable
fun isThemeDark(isDarkPriority: Boolean, tint: ThemeTint) = isDarkPriority ||
        if (tint == ThemeTint.Auto) isSystemInDarkTheme()
        else tint == ThemeTint.Dark