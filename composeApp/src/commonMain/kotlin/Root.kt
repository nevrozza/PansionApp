
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import dev.chrisbanes.haze.LocalHazeStyle
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import root.RootComponent
import utils.StatusBarColorFix
import view.LocalViewManager
import view.WindowCalculator
import view.WindowScreen
import view.WindowType
import view.webPadding

@OptIn(ExperimentalHazeMaterialsApi::class)
@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
fun Root(
    root: RootComponent,
    device: WindowType = WindowType.Phone,
//    isJs: Boolean = false
) {
    val viewManager = LocalViewManager.current




    setViewManager(viewManager)


    BoxWithConstraints(
    ) {
        viewManager.size = this
        viewManager.orientation.value =
            if (viewManager.isLockedVerticalView.value == true) WindowScreen.Vertical
            else WindowCalculator.calculateScreen(
                size = DpSize(this.maxWidth, this.maxHeight),
                device
            )
        CompositionLocalProvider(
            LocalHazeStyle provides HazeMaterials.regular(),
        ) {
//            AppTheme() {
            Surface(Modifier.fillMaxSize()
                .background(MaterialTheme.colorScheme.surface).webPadding()) {
                StatusBarColorFix()
                RootContent(root)
            }
//            }
        }
    }
}




