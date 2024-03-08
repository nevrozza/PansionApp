package components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
expect fun ScrollBaredBox(
    vState: LazyListState,
    hState: ScrollState,
    modifier: Modifier = Modifier.fillMaxSize(),

    height: MutableState<Dp> = mutableStateOf(0.dp),
    width: MutableState<Dp> = mutableStateOf(0.dp),

    content: @Composable () -> Unit,
)