package base.layouts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFirst


private object ThreeComponentsLayoutIds {
    const val LEFT_SIDE = "leftSide"
    const val CENTER_SIDE = "centerSide"
    const val RIGHT_SIDE = "rightSide"
}


@Composable
fun ThreeComponentsLayout(
    modifier: Modifier = Modifier,
    isCenter: Boolean = false,
    paddingBetween: Dp = 0.dp,
    leftContent: @Composable RowScope.() -> Unit = {},
    centerContent: @Composable RowScope.() -> Unit = {},
    rightContent: @Composable RowScope.() -> Unit = {},
) {
    Layout(
        content = {
            Row(Modifier.layoutId(ThreeComponentsLayoutIds.LEFT_SIDE), verticalAlignment = Alignment.CenterVertically) {
                leftContent()
            }
            Row(Modifier.layoutId(ThreeComponentsLayoutIds.CENTER_SIDE)
                .padding(horizontal = paddingBetween)
                .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = if (isCenter) Arrangement.Center else Arrangement.Start
            ) {
                centerContent()
            }
            Row(
                Modifier.layoutId(ThreeComponentsLayoutIds.RIGHT_SIDE), verticalAlignment = Alignment.CenterVertically) {
                rightContent()
            }
        },
        modifier = modifier
    ) { measurables, constraints ->
        val leftSidePlaceable = measurables
            .fastFirst { it.layoutId == ThreeComponentsLayoutIds.LEFT_SIDE }
            .measure(
                constraints.copy(
                    minWidth = 0,
                    maxWidth = constraints.maxWidth
                )
            )

        val rightPlaceable = measurables
            .fastFirst { it.layoutId == ThreeComponentsLayoutIds.RIGHT_SIDE }
            .measure(
                constraints.copy(
                    minWidth = 0,
                    maxWidth = constraints.maxWidth
                )
            )


        val centerPlaceable = measurables
            .fastFirst { it.layoutId == ThreeComponentsLayoutIds.CENTER_SIDE }
            .measure(
                constraints.copy(
                    minWidth = 0,
                    maxWidth = constraints.maxWidth - rightPlaceable.width - leftSidePlaceable.width
                )
            )
        val totalHeight = maxOf(
            leftSidePlaceable.height,
            rightPlaceable.height,
            centerPlaceable.height
        )

        layout(width = constraints.maxWidth, height = totalHeight) {
            leftSidePlaceable.placeRelative(
                x = 0,
                y = (totalHeight - leftSidePlaceable.height) / 2
            )
            centerPlaceable.placeRelative(
                x = (leftSidePlaceable.width + rightPlaceable.width) / 2,
                y = (totalHeight - centerPlaceable.height) / 2
            )
            rightPlaceable.placeRelative(
                x = constraints.maxWidth - rightPlaceable.width,
                y = (totalHeight - rightPlaceable.height) / 2
            )
        }

    }
}