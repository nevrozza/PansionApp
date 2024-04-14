package components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.NoLiveLiterals
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
@NoLiveLiterals
@Composable
actual fun ScrollBaredBox(
    vState: LazyListState,
    hState: ScrollState,
    modifier: Modifier,
    height: MutableState<Dp>,
    width: MutableState<Dp>,
    content: @Composable () -> Unit,
) {
    Box(modifier) {
        Box(
            Modifier.clip(RoundedCornerShape(15.dp)).border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = .4f),
            RoundedCornerShape(15.dp)
        )) {
            content()
        }
    }
}