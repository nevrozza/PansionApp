package view

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

@Immutable
class WindowCalculator private constructor(
    val width: WindowSize,
    val height: WindowSize
) {
    companion object {
        fun calculateFromSize(size: DpSize): WindowCalculator {
            val windowWidthSizeClass = fromWidth(size.width)
            val windowHeightSizeClass = fromHeight(size.height)
            return WindowCalculator(windowWidthSizeClass, windowHeightSizeClass)
        }

        fun calculateScreen(size: DpSize, device: WindowType = WindowType.Phone): WindowScreen {
            val windowWidthSizeClass = fromWidth(size.width)
            val windowHeightSizeClass = fromHeight(size.height)
            val windowSizeClass = WindowCalculator(windowWidthSizeClass, windowHeightSizeClass)
            //            return WindowSizeClass(windowWidthSizeClass, windowHeightSizeClass)
            return if (isExpanded(windowSizeClass, device)) WindowScreen.Expanded
            else if (isVertical(windowSizeClass)) WindowScreen.Vertical
            else WindowScreen.Horizontal
        }

        private fun isVertical(size: WindowCalculator): Boolean {
            return ((size.height == WindowSize.Expanded &&
                    (size.width == WindowSize.Medium
                            || size.width == WindowSize.Compact)) ||
                    (size.height == WindowSize.Medium && size.width == WindowSize.Compact)
                    )
        }

        private fun isExpanded(size: WindowCalculator, device: WindowType): Boolean {
            return ((size.width == WindowSize.Expanded &&
                    (size.height == WindowSize.Medium
                            || size.height == WindowSize.Expanded)) || (!isVertical(size) && size.width != WindowSize.TwoPane && device == WindowType.PC))
        }
    }
}

fun fromWidth(width: Dp): WindowSize {
    require(width >= 0.dp) { "Width must not be negative" }
    return when {
        width < 600.dp -> WindowSize.Compact
        width < 740.dp -> WindowSize.TwoPane
        width < 840.dp -> WindowSize.Medium
        else -> WindowSize.Expanded
    }
}

fun fromHeight(height: Dp): WindowSize {
    require(height >= 0.dp) { "Height must not be negative" }
    return when {
        height < 480.dp -> WindowSize.Compact
        height < 900.dp -> WindowSize.Medium
        else -> WindowSize.Expanded
    }
}