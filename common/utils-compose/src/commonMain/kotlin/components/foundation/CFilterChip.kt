package components.foundation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import components.networkInterface.NetworkState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CFilterChip(
    label: String,
    isSelected: Boolean,
    state: NetworkState,
    coroutineScope: CoroutineScope,
    onClick: () -> Unit
) {
    val bringIntoViewRequester =
        remember { BringIntoViewRequester() }
    LaunchedEffect(state) {
        if (isSelected) {
            coroutineScope.launch {
                bringIntoViewRequester.bringIntoView()
            }
        }
    }
    FilterChip(
        selected = isSelected,
        onClick = {
            onClick()
            coroutineScope.launch {
                bringIntoViewRequester.bringIntoView()
            }

        },
        label = { Text(label) },
        modifier = Modifier.bringIntoViewRequester(
            bringIntoViewRequester
        )
    )
}