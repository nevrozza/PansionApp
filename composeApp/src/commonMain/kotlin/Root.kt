
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.BringIntoViewSpec
import androidx.compose.foundation.gestures.LocalBringIntoViewSpec
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.LocalHazeStyle
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import root.RootComponent
import utils.HideKeyboardLayout
import utils.StatusBarColorFix
import utils.rememberImeState
import view.LocalViewManager
import view.WindowCalculator
import view.WindowScreen
import view.WindowType
import view.webPadding

@OptIn(ExperimentalHazeMaterialsApi::class, ExperimentalSharedTransitionApi::class)
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

    val density = LocalDensity.current


    // for hide keyboard button
    val ime by rememberImeState()
    val keyboardButtonShown = ime && viewManager.isHideKeyboardButtonShown.value
    val keyboardButtonHeight = remember { mutableStateOf(0) }

    BoxWithConstraints(
    ) {
        viewManager.size = this
        viewManager.orientation.value =
            if (viewManager.isLockedVerticalView.value == true) WindowScreen.Vertical
            else WindowCalculator.calculateScreen(
                size = DpSize(this.maxWidth, this.maxHeight),
                device
            )
        viewManager.imeInsetValue.value = if (viewManager.isHideKeyboardButtonShown.value)
            WindowInsets.ime.getBottom(density) else 0

        CompositionLocalProvider(
            LocalHazeStyle provides HazeMaterials.regular(),
            LocalBringIntoViewSpec provides object : BringIntoViewSpec {


                override fun calculateScrollDistance(
                    offset: Float,
                    size: Float,
                    containerSize: Float
                ): Float {
                    // without hideKeyboard: 75
                    return super.calculateScrollDistance(offset, size + with(density) {
                        (60.dp +
                                if (keyboardButtonShown) keyboardButtonHeight.value.toDp() else 0.dp
                                )
                            .toPx()
                    }, containerSize)
                }
            }
        ) {
            Surface(
                Modifier.fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface).webPadding()
            ) {
                StatusBarColorFix()
                RootContent(root)
            }
        }
    }

    HideKeyboardLayout(
        height = keyboardButtonHeight
    )
}




