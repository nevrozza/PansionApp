import androidx.compose.ui.Modifier

actual fun Modifier.cursorForHorizontalResize(): Modifier = Modifier.then(this)