package utils.cursor

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import org.jetbrains.skiko.Cursor

@OptIn(ExperimentalComposeUiApi::class)
actual fun Modifier.cursorForHorizontalResize(isHorizontal: Boolean): Modifier =
    pointerHoverIcon(PointerIcon(Cursor(if (isHorizontal) Cursor.E_RESIZE_CURSOR else Cursor.S_RESIZE_CURSOR)))
