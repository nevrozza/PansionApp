import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.unit.Dp

@Composable
expect fun keyboardHeight(): Dp

@Composable
expect fun mutableKeyboardHeight(): MutableState<Dp>

//val mguComposeImage = mguImage.image.toPainter()

