package components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import view.LocalViewManager

@Composable
fun AppBar(
    modifier: Modifier = Modifier.padding(top = 10.dp).padding(horizontal = 10.dp),
    title: @Composable RowScope.() -> Unit = {},
    navigationRow: @Composable () -> Unit = {},
    actionRow: @Composable () -> Unit = {},
    containerColor: Color = MaterialTheme.colorScheme.surface,
    hazeState: HazeState?,
    isTransparentHaze: Boolean = false,
    isTopPadding: Boolean = true,
//    isHazeActivated: Boolean
) {
    val viewManager = LocalViewManager.current
    Box(
        Modifier.fillMaxWidth().height(60.dp + if (isTopPadding) viewManager.topPadding else 0.dp)
            .hazeHeader(
                viewManager = viewManager,
                hazeState = hazeState,
                isTransparentHaze = isTransparentHaze,
//                isActivated = isHazeActivated
            ),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = modifier.fillMaxWidth().height(60.dp).padding(bottom = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(Modifier.offset(y = (1).dp)) {
                    navigationRow()
                }
                title()
            }

            Row(modifier = Modifier.wrapContentSize()) {
                actionRow()
            }
        }
    }
}

@Composable
fun CentreAppBar(
    modifier: Modifier = Modifier.padding(top = 10.dp).padding(horizontal = 10.dp),
    title: @Composable () -> Unit = {},
    navigationRow: @Composable () -> Unit = {},
    actionRow: @Composable () -> Unit = {},
    containerColor: Color = MaterialTheme.colorScheme.surface,
) {
    val viewManager = LocalViewManager.current
    Box(
        Modifier.fillMaxWidth().height(60.dp + viewManager.topPadding).background(containerColor),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = modifier.fillMaxWidth().padding(bottom = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(Modifier.weight(1f)) {
                navigationRow()
            }
            Row(
                Modifier.weight(1f),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                title()
            }

            Row(Modifier.weight(1f)) {
                actionRow()
            }
        }
    }
}
