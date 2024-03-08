package forks.splitPane

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp



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

@OptIn(ExperimentalSplitPaneApi::class)
internal actual fun defaultSplitter(
    isHorizontal: Boolean,
    splitPaneState: SplitPaneState
): Splitter = Splitter(
    measuredPart = {},
    handlePart = {
        DesktopHandle(isHorizontal, splitPaneState)
    }
)