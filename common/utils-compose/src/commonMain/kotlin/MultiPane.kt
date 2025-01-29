import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.unit.dp
import forks.splitPane.ExperimentalSplitPaneApi
import forks.splitPane.HorizontalSplitPane
import forks.splitPane.dSplitter
import view.ViewManager

@ExperimentalMaterial3Api
@OptIn(ExperimentalLayoutApi::class, ExperimentalSplitPaneApi::class)
@Composable
fun DefaultMultiPane(
    isExpanded: Boolean,
    leftScreen: @Composable () -> Unit,
    isFullScreenSupport: Boolean = false,
    viewManager: ViewManager,
    secondScreen: @Composable () -> Unit
) {
    if (isExpanded) {
        if (isFullScreenSupport) {
            DisposableEffect(viewManager.isFullScreen.value) {
                viewManager.splitPaneState.dispatchRawMovement(-30000f)
                onDispose {
                    if (viewManager.isFullScreen.value) {
                        viewManager.splitPaneState.dispatchRawMovement((0.5f - viewManager.splitPaneState.positionPercentage) * 1500)
                    }
                }
            }
        }
        val x =
            animateDpAsState(if (viewManager.isFullScreen.value && isFullScreenSupport) 0.dp else 400.dp)
        HorizontalSplitPane(
            splitPaneState = viewManager.splitPaneState
        ) {
            first(minSize = x.value) {
                leftScreen()
            }

            dSplitter(isFullScreen = if (isFullScreenSupport) viewManager.isFullScreen else null)

            second(minSize = 500.dp) {
                secondScreen()
            }
        }
    } else {
        secondScreen()
    }
}