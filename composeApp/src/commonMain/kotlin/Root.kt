import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.Modifier
class Root

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
fun Root(
    root: RootComponent,
    device: WindowType = WindowType.Phone,
    isJs: Boolean = false,
    PCChange: ((String, Boolean) -> Unit)? = null
) {
    Text("sad")
//    println("1")
    val settingsRepository: SettingsRepository = Inject.instance()
    Text("sad1")
    tintInit(settingsRepository)
    Text("sad12")
    colorInit(settingsRepository)
    Text("sad123")
    val viewManager = remember {
        ViewManager(
            tint = mutableStateOf(settingsRepository.fetchTint()),
            color = mutableStateOf(settingsRepository.fetchColor())
        )
    }
    Text("sad1234")
//    println("2")
    viewManager.isDark.value =
        if (viewManager.tint.value == ThemeTint.Auto.name) isSystemInDarkTheme()
        else viewManager.tint.value == ThemeTint.Dark.name
    val colorScheme = colorSchemeGetter(isDark = viewManager.isDark.value, color = viewManager.color.value)
    if (PCChange != null) {
        LaunchedEffect(viewManager.color.value, key2 = viewManager.tint.value) {
            PCChange(viewManager.color.value, viewManager.isDark.value)
        }
    }
//    println("3")
////        if (viewManager.isDynamic.value) {
////            if (viewManager.isDark.value) {
////                dynamicDarkScheme()!!
////            } else {
////                dynamicLightScheme()!!
////            }
////        }
////        else if(themeManager.color.value == ThemeColors.Dynamic.name && !isCanInDynamic()) {
////            schemeChooser(themeManager.isDark.value, ThemeColors.Default.name)
////        }
////        else {
////            schemeChooser(themeManager.isDark.value, themeManager.color.value)
////        }
//
    BoxWithConstraints() {
        viewManager.size = this
        viewManager.orientation.value =
            WindowCalculator.calculateScreen(size = DpSize(this.maxWidth, this.maxHeight), device)
//        println("4")
        CompositionLocalProvider(
            LocalViewManager provides viewManager
        ) {
//            println("5")
            AppTheme(colorScheme = colorScheme) {
//                println("6")
                Surface(Modifier.fillMaxSize()) {
//                    println("7")
                    StatusBarColorFix()
//                    println("8")
                    RootContent(root, isJs)
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


