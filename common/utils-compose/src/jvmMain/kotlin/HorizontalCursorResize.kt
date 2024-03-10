import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import org.jetbrains.skiko.Cursor

actual fun Modifier.cursorForHorizontalResize(): Modifier =
    pointerHoverIcon(PointerIcon(Cursor(Cursor.E_RESIZE_CURSOR)))