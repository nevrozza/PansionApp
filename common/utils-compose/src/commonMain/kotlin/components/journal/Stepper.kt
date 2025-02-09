package components.journal

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFirst
import components.GetAsyncIcon
import resources.RIcons


private object LayoutIds {
    const val COUNTER = "counter"
    const val MINUS = "minus"
    const val PLUS = "plus"
}

@Composable
fun Stepper(
    count: Int,
    isEditable: Boolean,
    maxCount: Int,
    minCount: Int,
    height: Dp = 25.dp,
    modifier: Modifier = Modifier,
    onChangeCount: (Int) -> Unit
) {


    val animatedAllAlpha by animateFloatAsState(if (count != 0) 1f else .2f, tween(600))
    Layout(
        {
//            Box(Modifier.size(height - 7.dp).layoutId(LayoutIds.MINUS))
//            Box(Modifier.size(height - 7.dp).layoutId(LayoutIds.PLUS))
            IconButton(
                onClick = {
                    onChangeCount(count - 1)
                },
                enabled = count != minCount,
                modifier = Modifier.layoutId(LayoutIds.MINUS)
            ) {
                GetAsyncIcon(
                    RIcons.MINUS,
                    size = height - 7.dp
                )
            }
            IconButton(
                onClick = {
                    onChangeCount(count + 1)
                },
                enabled = count != maxCount,
                modifier = Modifier.layoutId(LayoutIds.PLUS)
            ) {
                GetAsyncIcon(
                    RIcons.ADD,
                    size = height - 7.dp
                )
            }
            AnimatedContent(
                count,
                modifier = Modifier.layoutId(LayoutIds.COUNTER)
            ) {
                val mark = it.toString()
                Text(
                    (if (!mark.contains("-") && !mark.contains("+")
                    ) "+" else "") + mark
                )
            }
        },
        modifier = modifier.height(height).border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(if (count != 0) 1f else .2f),
            shape = RoundedCornerShape(30)
        ).clip(RoundedCornerShape(30)).graphicsLayer(alpha = animatedAllAlpha)
    ) { measurables, constraints ->
        val heightPx = height.roundToPx()
        val minusPlaceable =
            measurables
                .fastFirst { it.layoutId == LayoutIds.MINUS }
                .measure(constraints.copy(minWidth = 0))
        val plusPlaceable =
            measurables
                .fastFirst { it.layoutId == LayoutIds.PLUS }
                .measure(constraints.copy(minWidth = 0))

        val maxCounterWidth =
            if (constraints.maxWidth == Constraints.Infinity) {
                constraints.maxWidth
            } else {
                (constraints.maxWidth - minusPlaceable.width - plusPlaceable.width)
                    .coerceAtLeast(0)
            }
        val counterPlaceable =
            measurables
                .fastFirst { it.layoutId == LayoutIds.COUNTER }
                .measure(constraints.copy(minWidth = 0, maxWidth = maxCounterWidth))
        layout(
            height = heightPx,
            width = counterPlaceable.width + minusPlaceable.width + plusPlaceable.width
        ) {
            val tWidthPx = this.coordinates?.size?.width ?: 1
            // taken from AppBar.kt [TopAppBarLayout]
            val counterXLambda: () -> Int = {
                var baseX = (tWidthPx - counterPlaceable.width) / 2
                if (baseX < minusPlaceable.width) {
                    // May happen if the navigation is wider than the actions and the
                    // title is long. In this case, prioritize showing more of the title
                    // by
                    // offsetting it to the right.
                    baseX += (minusPlaceable.width - baseX)
                } else if (
                    baseX + counterPlaceable.width >
                    tWidthPx - plusPlaceable.width
                ) {
                    // May happen if the actions are wider than the navigation and the
                    // title
                    // is long. In this case, offset to the left.
                    baseX +=
                        ((tWidthPx - plusPlaceable.width) -
                                (baseX + counterPlaceable.width))
                }
                baseX
            }
            minusPlaceable.placeRelative(
                x = 0,
                y = (heightPx - minusPlaceable.height) / 2
            )


            val counterX = counterXLambda()

            counterPlaceable.placeRelative(
                x = counterX,
                y = (heightPx - counterPlaceable.height) / 2
            )

            plusPlaceable.placeRelative(
                x = tWidthPx - plusPlaceable.width,
                y = (heightPx - plusPlaceable.height) / 2
            )
        }

    }
}