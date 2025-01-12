package components.refresh

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.dp
import components.GetAsyncIcon
import pullRefresh.PullRefreshState
import resources.RIcons
import view.ViewManager

@Composable
fun RefreshButton(
    refreshState: PullRefreshState,
    viewManager: ViewManager,
    isNeccesary: Boolean = false
) {
    if (viewManager.isRefreshButtons.value || isNeccesary) {
        IconButton(
            onClick = refreshState.onRefreshState.value
        ) {
            GetAsyncIcon(
                path = RIcons.Refresh
            )
        }
    }
}

fun Modifier.keyRefresh(refreshState: PullRefreshState) =
    this.onKeyEvent {
        if (it.key == Key.F5 && it.type == KeyEventType.KeyDown) {
            refreshState.onRefreshState.value()
        }
        false
    }



@Composable
fun RowScope.RefreshWithoutPullCircle(
    refreshing: Boolean,
    position: Float,
    extraCondition: Boolean = true
) {
    AnimatedVisibility(refreshing && position == 0f && extraCondition) {
        Row() {
            Spacer(Modifier.width(10.dp))
            CircularProgressIndicator(Modifier.size(25.dp))
        }
    }
//    Text((refreshState.refreshing && refreshState.position == 0f && extraCondition).toString())
}
