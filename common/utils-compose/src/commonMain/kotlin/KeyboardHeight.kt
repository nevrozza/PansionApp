import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.unit.Dp

@Composable
expect fun KeyboardHeight(): Dp

@Composable
expect fun MutableKeyboardHeight(): MutableState<Dp>

//val mguComposeImage = mguImage.image.toPainter()