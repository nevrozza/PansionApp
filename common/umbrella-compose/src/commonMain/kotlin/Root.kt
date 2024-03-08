import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.DpSize
import di.Inject
import root.RootComponent
import view.AppTheme
import view.LocalViewManager
import view.StatusBarColorFix
import view.ThemeColors
import view.ThemeTint
import view.ViewManager
import view.WindowCalculator
import view.WindowType
import view.colorSchemeGetter
import view.dynamicDarkScheme
import view.dynamicLightScheme
import view.isCanInDynamic

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
fun Root(
    root: RootComponent,
    device: WindowType = WindowType.Phone,
    PCChange: ((String, Boolean) -> Unit)? = null
) {
    val settingsRepository: SettingsRepository = Inject.instance()
    tintInit(settingsRepository)
    colorInit(settingsRepository)
    val viewManager = remember {
        ViewManager(
            tint = mutableStateOf(settingsRepository.fetchTint()),
            color = mutableStateOf(settingsRepository.fetchColor())
        )
    }

    viewManager.isDark.value =
        if (viewManager.tint.value == ThemeTint.Auto.name) isSystemInDarkTheme()
        else viewManager.tint.value == ThemeTint.Dark.name

    val colorScheme = colorSchemeGetter(isDark = viewManager.isDark.value, color = viewManager.color.value)
    if (PCChange != null) {
        LaunchedEffect(viewManager.color.value, key2 = viewManager.tint.value) {
            PCChange(viewManager.color.value, viewManager.isDark.value)
        }
    }

//        if (viewManager.isDynamic.value) {
//            if (viewManager.isDark.value) {
//                dynamicDarkScheme()!!
//            } else {
//                dynamicLightScheme()!!
//            }
//        }
//        else if(themeManager.color.value == ThemeColors.Dynamic.name && !isCanInDynamic()) {
//            schemeChooser(themeManager.isDark.value, ThemeColors.Default.name)
//        }
//        else {
//            schemeChooser(themeManager.isDark.value, themeManager.color.value)
//        }

    BoxWithConstraints() {
        viewManager.size = this
        viewManager.orientation.value =
            WindowCalculator.calculateScreen(size = DpSize(this.maxWidth, this.maxHeight), device)

        CompositionLocalProvider(
            LocalViewManager provides viewManager
        ) {
            AppTheme(colorScheme = colorScheme) {
                Surface {
                    StatusBarColorFix()
                    RootContent(root)
                }
            }
        }
    }
}

fun tintInit(settingsRepository: SettingsRepository): String {
    return when {
        settingsRepository.fetchTint().isBlank() -> {
            settingsRepository.saveTint(ThemeTint.Auto.name)
            ThemeTint.Auto.name
        }

        else -> {
            settingsRepository.fetchTint()
        }
    }
}

fun colorInit(settingsRepository: SettingsRepository): String {
    return when {
        settingsRepository.fetchColor().isBlank() -> {
            settingsRepository.saveColor(ThemeColors.Default.name)
            ThemeColors.Default.name
        }

        settingsRepository.fetchColor().isBlank() && isCanInDynamic() -> {
            settingsRepository.saveColor(ThemeColors.Dynamic.name)
            ThemeColors.Dynamic.name
        }

        else -> {
            settingsRepository.fetchColor()
        }
    }
}


