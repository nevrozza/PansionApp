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
import view.ThemeTint
import view.ViewManager
import view.WindowCalculator
import view.WindowType
import androidx.compose.ui.Modifier
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials

class Root

@OptIn(ExperimentalHazeMaterialsApi::class)
@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
fun Root(
    root: RootComponent,
    device: WindowType = WindowType.Phone,
    isJs: Boolean = false
) {
    val viewManager = LocalViewManager.current

    setHaze(viewManager)

    BoxWithConstraints() {
        viewManager.size = this
        viewManager.orientation.value =
            WindowCalculator.calculateScreen(size = DpSize(this.maxWidth, this.maxHeight), device)
        CompositionLocalProvider(
            LocalViewManager provides viewManager
        ) {
            AppTheme() {
                Surface(Modifier.fillMaxSize()) {
                    StatusBarColorFix()
                    RootContent(root, isJs)
                }
            }
        }
    }
}




