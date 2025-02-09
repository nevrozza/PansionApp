package forks.splitPane

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

/**
 * Internal implementation of default splitter
 *
 * @param isHorizontal describes is it horizontal or vertical split pane
 * @param splitPaneState the state object to be used to control or observe the split pane state
 */
@OptIn(ExperimentalSplitPaneApi::class)
internal fun defaultSplitter(
    isHorizontal: Boolean,
    splitPaneState: SplitPaneState
): Splitter = Splitter(
    measuredPart = {},
    handlePart = {
        DesktopHandle(isHorizontal, splitPaneState)
    }
)

@OptIn(ExperimentalSplitPaneApi::class)
@Composable
private fun DesktopHandle(
    isHorizontal: Boolean,
    splitPaneState: SplitPaneState
) = Box(
    Modifier
        .run {
            val layoutDirection = LocalLayoutDirection.current
            pointerInput(splitPaneState) {
                detectDragGestures { change, _ ->
                    change.consume()
                    splitPaneState.dispatchRawMovement(
                        if (isHorizontal)
                            if (layoutDirection == LayoutDirection.Ltr) change.position.x else -change.position.x
                        else change.position.y
                    )
                }
            }
        }
        .run {
            if (isHorizontal) {
                this.width(8.dp)
                    .fillMaxHeight()
            } else {
                this.height(8.dp)
                    .fillMaxWidth()
            }
        }
)