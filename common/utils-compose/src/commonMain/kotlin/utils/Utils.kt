package androidx.compose.desktop.ui.tooling.preview.utils

import androidx.compose.animation.core.EaseInQuart
import androidx.compose.animation.core.Easing
import androidx.compose.foundation.ScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TooltipDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.State
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupPositionProvider
import dev.chrisbanes.haze.HazeState
import view.LocalViewManager

@OptIn(ExperimentalMaterial3Api::class)
val popupPositionProvider: PopupPositionProvider
    @Composable
    get() = TooltipDefaults.rememberPlainTooltipPositionProvider()


val hazeMask =
    Brush.easedVerticalGradient(EaseInQuart)//Brush.verticalGradient(colors = listOf(Color.Magenta, Color.Magenta.copy(.82f), Color.Transparent))


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

    return linearGradient(colors = if (isReversed) colors.reversed() else colors, start = start, end = end)
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