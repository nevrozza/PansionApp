package forks.colorPicker

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

fun Color.toHex(): String {
    val bBuf = this.toArgb().toUInt().toString(16)
    return bBuf.substring(bBuf.length - 6)
}