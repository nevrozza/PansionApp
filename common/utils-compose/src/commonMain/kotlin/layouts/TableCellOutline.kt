package layouts

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFirst
import eu.wewox.minabox.ScrollbarData


private object LayoutIds {
    const val CONTENT = "content"
    const val X_DIVIDER = "xDivider"
    const val Y_DIVIDER = "yDivider"
}

val defaultScrollbarData: ScrollbarData
    @Composable
    get() = ScrollbarData(
        color = MaterialTheme.colorScheme.surfaceColorAtElevation((10).dp),
        hoveredColor = MaterialTheme.colorScheme.surfaceColorAtElevation(40.dp),
        padding = 5.dp,
        thickness = 8.dp,
        shapeRadius = 16.dp,
        isOuterTable = true
    )



val defaultMinaBoxTableModifier: Modifier
    @Composable
    get() = Modifier.border(
        width = 1.dp,
        color = MaterialTheme.colorScheme.outline.copy(alpha = .4f),
        RoundedCornerShape(16.dp)
    ).clip(RoundedCornerShape(16.dp))

@Composable
fun TableCellOutline(
    color: Color = MaterialTheme.colorScheme.outline.copy(alpha = .4f),
    backgroundColor: Color = Color.Transparent,
    xThickness: Dp = 1.dp,
    yThickness: Dp = 1.dp,
    contentPadding: PaddingValues = PaddingValues((1).dp),
    content: @Composable () -> Unit
) {
    Layout(
        {
            Box(Modifier.layoutId(LayoutIds.CONTENT).padding(contentPadding)) {
                content()
            }
            VerticalDivider(
                modifier = Modifier.layoutId(LayoutIds.Y_DIVIDER), //.fillMaxHeight()
                thickness = yThickness,
                color = color
            )
            HorizontalDivider(
                modifier = Modifier.layoutId(LayoutIds.X_DIVIDER),
                thickness = xThickness,
                color = color
            ) // .fillMaxWidth()
        },
        modifier = Modifier.background(backgroundColor)
    ) { measurables, constraints ->
        val xThicknessPx = xThickness.roundToPx()
        val yThicknessPx = yThickness.roundToPx()

        val xDividerPlaceable =
            measurables
                .fastFirst { it.layoutId == LayoutIds.X_DIVIDER }
                .measure(
                    constraints.copy(
                        minWidth = constraints.maxWidth,
                        minHeight = xThicknessPx,
                        maxHeight = xThicknessPx
                    )
                )

        val yDividerPlaceable =
            measurables
                .fastFirst { it.layoutId == LayoutIds.Y_DIVIDER }
                .measure(
                    constraints.copy(
                        minWidth = yThicknessPx,
                        maxWidth = yThicknessPx,
                        minHeight = constraints.maxHeight
                    )
                )
        val maxContentWidth =
            (constraints.maxWidth)
                .coerceAtLeast(0)
        val maxContentHeight =
            (constraints.maxHeight)
                .coerceAtLeast(0)


        val contentPlaceable =
            measurables
                .fastFirst { it.layoutId == LayoutIds.CONTENT }
                .measure(
                    constraints.copy(
                        minWidth = 0,
                        maxWidth = maxContentWidth,
                        minHeight = 0,
                        maxHeight = maxContentHeight
                    )
                )

        layout(constraints.maxWidth, constraints.maxHeight) {
            xDividerPlaceable.placeRelative(
                x = 0,
                y = constraints.maxHeight - xThicknessPx,
            )
            yDividerPlaceable.placeRelative(
                x = constraints.maxWidth - yThicknessPx,
                y = 0,
            )
            contentPlaceable.placeRelative(
                x = 0,
                y = 0,
            )
        }
    }

}