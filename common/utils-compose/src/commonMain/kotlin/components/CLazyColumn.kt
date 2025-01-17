package components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.overscroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalScrollCaptureInProgress
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeChild
import pullRefresh.PullRefreshState
import pullRefresh.pullRefresh
import pullRefresh.pullRefreshContentTransform
import view.LocalViewManager
import view.WindowScreen

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CLazyColumn(
    padding: PaddingValues,
    modifier: Modifier = Modifier,
//    isCustomExpanded: Boolean? = null,
    isBottomPaddingNeeded: Boolean = false,
    state: LazyListState = rememberLazyListState(),
    refreshState: PullRefreshState? = null,
    content: LazyListScope.() -> Unit
) {
    val viewManager = LocalViewManager.current
    val isExpanded = viewManager.orientation.value == WindowScreen.Expanded

    LazyColumn(
        Modifier
            .padding(horizontal = 15.dp)
            .fillMaxSize()
            .consumeWindowInsets(padding)
            .imePadding()
            .hazeUnder(viewManager).then(modifier)
        ,
        state = state
    ) {
        item {
            Spacer(Modifier.pullRefreshContentTransform(refreshState).height(padding.calculateTopPadding()))
        }

//        item {
//            Spacer(modifier = Modifier)
//        }
        content()
        if(isBottomPaddingNeeded) {
            item {
                if (!isExpanded) {
                    Spacer(Modifier.height(padding.calculateBottomPadding() + 80.dp))
                }
            }
        }
    }
}