package utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.UIKit.UIKeyboardWillHideNotification
import platform.UIKit.UIKeyboardWillShowNotification

@Composable
actual fun rememberImeState(): State<Boolean> {
    val imeState = remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        val keyboardWillShowObserver = NSNotificationCenter.defaultCenter.addObserverForName(
            name = UIKeyboardWillShowNotification,
            `object` = null,
            queue = NSOperationQueue.mainQueue
        ) { _ ->
            imeState.value = true
        }

        val keyboardWillHideObserver = NSNotificationCenter.defaultCenter.addObserverForName(
            name = UIKeyboardWillHideNotification,
            `object` = null,
            queue = NSOperationQueue.mainQueue
        ) { _ ->
            imeState.value = false
        }

        onDispose {
            NSNotificationCenter.defaultCenter.removeObserver(keyboardWillShowObserver)
            NSNotificationCenter.defaultCenter.removeObserver(keyboardWillHideObserver)
        }
    }

    return imeState
}