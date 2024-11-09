package components

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeChild
import view.LocalViewManager
import view.WindowScreen

@Composable
fun CLazyColumn(
    padding: PaddingValues,
    modifier: Modifier = Modifier,
//    isCustomExpanded: Boolean? = null,
    isBottomPaddingNeeded: Boolean = false,
    state: LazyListState = rememberLazyListState(),
    hazeState: HazeState?,
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
            .hazeUnder(viewManager, hazeState).then(modifier),
        state = state
    ) {
        item {
            Spacer(Modifier.height(padding.calculateTopPadding()))
        }
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