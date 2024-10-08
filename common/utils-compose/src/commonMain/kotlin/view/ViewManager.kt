package view

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.State
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.materials.HazeMaterials
import forks.splitPane.ExperimentalSplitPaneApi
import forks.splitPane.SplitPaneState
import forks.splitPane.rememberSplitPaneState

class ViewManager @OptIn(ExperimentalSplitPaneApi::class) constructor(
    val seedColor: MutableState<Color>,
    val tint: MutableState<ThemeTint> = mutableStateOf(ThemeTint.Auto),
    var isDark: MutableState<Boolean> = mutableStateOf(false),
    var topPadding: Dp = 0.dp,
    var hazeState: HazeState = HazeState(),
    var hazeStyle: MutableState<HazeStyle>? = null,
    var size: BoxWithConstraintsScope? = null,
    var orientation: MutableState<WindowScreen> = mutableStateOf(WindowScreen.Vertical),
    var colorMode: MutableState<String>,
    val splitPaneState: SplitPaneState,
    val isFullScreen: MutableState<Boolean> = mutableStateOf(true)
)
val LocalViewManager: ProvidableCompositionLocal<ViewManager> = compositionLocalOf {
    error("No ViewManager provided")
}

@Composable
expect fun rememberImeState(): State<Boolean>


fun Modifier.bringIntoView(
    scrollState: ScrollState,
    imeState: State<Boolean>,
    divider: Float = 1f
): Modifier = composed {
    var scrollToPosition by remember {
        mutableStateOf(0f)
    }
    LaunchedEffect(key1 = imeState.value) {
        if (imeState.value) {
            scrollState.animateScrollTo(scrollToPosition.toInt())
        }
    }
    this
        .onGloballyPositioned { coordinates ->
            scrollToPosition = coordinates.positionInRoot().y/divider
        }


}