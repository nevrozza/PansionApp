import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
actual fun keyboardHeight(): Dp = 0.dp

@Composable
actual fun mutableKeyboardHeight(): MutableState<Dp> = mutableStateOf(0.dp)