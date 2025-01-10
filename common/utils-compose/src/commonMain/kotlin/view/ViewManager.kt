package view

import androidx.compose.animation.core.*
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.LocalHazeStyle
import forks.splitPane.ExperimentalSplitPaneApi
import forks.splitPane.SplitPaneState

class ViewManager @OptIn(ExperimentalSplitPaneApi::class) constructor(
    val seedColor: MutableState<Color>,
    val tint: MutableState<ThemeTint> = mutableStateOf(ThemeTint.Auto),
    var isDark: MutableState<Boolean> = mutableStateOf(false),
    var topPadding: Dp = 0.dp,
    var hazeHardware: MutableState<Boolean> = mutableStateOf(false),
    var size: BoxWithConstraintsScope? = null,
    var orientation: MutableState<WindowScreen> = mutableStateOf(WindowScreen.Vertical),
    var colorMode: MutableState<String>,
    val splitPaneState: SplitPaneState,
    val isFullScreen: MutableState<Boolean> = mutableStateOf(true),
    val isTransitionsEnabled: MutableState<Boolean> = mutableStateOf(true),
    val fontSize: MutableState<Float> = mutableFloatStateOf(1f),
    val fontType: MutableState<Int> = mutableStateOf(5),
)
//
//@Composable
//fun updateHaze() {
//    val globalHazeState = GlobalHazeState.current
//    DisposableEffect(Unit) {
//        onDispose {
//            globalHazeState.value = HazeState()
//        }
//    }
//}


val LocalViewManager: ProvidableCompositionLocal<ViewManager> = compositionLocalOf {
    error("No ViewManager provided")
}

val GlobalHazeState: ProvidableCompositionLocal<HazeState> = compositionLocalOf {
    error("No HazeState provided")
}

val Int.esp: TextUnit
    @Composable
    get() {
        return this.toFloat().esp
    }

val Float.esp: TextUnit
    @Composable
    get() {
        return this.sp * LocalViewManager.current.fontSize.value
    }


fun Brush.Companion.easedGradient(
    easing: Easing,
    start: Offset = Offset.Zero,
    end: Offset = Offset.Infinite,
    numStops: Int = 8,
    isReversed: Boolean
): Brush {
    val colors = List(numStops) { i ->
        val x = i * 1f / (numStops - 1)
        Color.Black.copy(alpha = 1f - easing.transform(x))
    }

    return linearGradient(colors = if(isReversed) colors.reversed() else colors, start = start, end = end)
}

fun Brush.Companion.easedVerticalGradient(
    easing: Easing,
    startY: Float = 0.0f,
    endY: Float = Float.POSITIVE_INFINITY,
    numStops: Int = 8,
    isReversed: Boolean = false
): Brush = easedGradient(
    easing = easing,
    numStops = numStops,
    start = Offset(x = 0f, y = startY),
    end = Offset(x = 0f, y = endY),
    isReversed = isReversed
)


//val hazeProgressive = dev.chrisbanes.haze.HazeProgressive.verticalGradient(startIntensity = 1f, endIntensity = 0.0f, easing = FastOutLinearInEasing)
val hazeMask =
    Brush.easedVerticalGradient(EaseInQuart)//Brush.verticalGradient(colors = listOf(Color.Magenta, Color.Magenta.copy(.82f), Color.Transparent))

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
            scrollToPosition = coordinates.positionInRoot().y / divider
        }


}