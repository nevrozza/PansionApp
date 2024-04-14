package view

import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon

fun Modifier.handy() = pointerHoverIcon(PointerIcon.Hand)