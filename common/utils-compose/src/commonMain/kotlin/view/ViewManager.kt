package view

import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
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

    val showAvatars: MutableState<Boolean> = mutableStateOf(false),
    val isAmoled: MutableState<Boolean> = mutableStateOf(false),
    val isRefreshButtons: MutableState<Boolean> = mutableStateOf(false),

    val hardwareStatus: MutableState<String> = mutableStateOf(""),

    val isLockedVerticalView: MutableState<Boolean?> = mutableStateOf(null),

    val imeInsetValue: MutableState<Int> = mutableStateOf(0),
    val isHideKeyboardButtonShown: MutableState<Boolean> = mutableStateOf(true)
)

val LocalViewManager: ProvidableCompositionLocal<ViewManager> = compositionLocalOf {
    error("No ViewManager provided")
}