import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import kotlin.math.absoluteValue

@Composable
actual fun keyboardHeight(): Dp {
    val density = LocalDensity.current
    val pxValue = WindowInsets.ime.getBottom(LocalDensity.current)
    return with(density) { pxValue.toDp() }
}

@Composable
actual fun mutableKeyboardHeight(): MutableState<Dp> {
    val density = LocalDensity.current
    val pxValue = WindowInsets.ime.getBottom(LocalDensity.current)
    val keyboardHeight = remember { mutableStateOf(with(density) { pxValue.toDp() }) }
    val ime = WindowInsets.ime
    LaunchedEffect(Unit) {
        snapshotFlow { ime.getBottom(density) }
            .collect { heightInPx ->
                keyboardHeight.value = with(density) { heightInPx.toDp() }
            }
    }

    return keyboardHeight
}