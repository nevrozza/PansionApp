package utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import view.LocalBottomWebPadding


@Composable
actual fun rememberImeState(): State<Boolean> {
    val bottomWebPadding = LocalBottomWebPadding.current

    val imeState = remember { mutableStateOf(false) }

    DisposableEffect(bottomWebPadding.value) {
        imeState.value = bottomWebPadding.value > 10

        onDispose {
            imeState.value = false
        }
    }

    return imeState
}