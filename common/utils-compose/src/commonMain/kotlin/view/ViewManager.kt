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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot

class ViewManager(
    var tint: MutableState<String> = mutableStateOf(ThemeTint.Auto.name),
    var color: MutableState<String> = mutableStateOf(ThemeColors.Default.name),
    var isDark: MutableState<Boolean> = mutableStateOf(false),
    var size: BoxWithConstraintsScope? = null,
    var orientation: MutableState<WindowScreen> = mutableStateOf(WindowScreen.Vertical)
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