@file:OptIn(ExperimentalComposeUiApi::class, ExperimentalComposeUiApi::class)

package components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch


@ExperimentalFoundationApi
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    text: String? = null,
    supText: String? = null,
    isEnabled: Boolean,
    passwordVisibleInit: Boolean? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    isLockIcon: Boolean = true,
    focusManager: FocusManager? = null,
    isMoveUpLocked: Boolean = false,
    onEnterClicked: (() -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.None,
    autoCorrect: Boolean = true,
    isDateEntry: Boolean = false,
    isSingleLine: Boolean = true,
    width: Dp = TextFieldDefaults.MinWidth
//    onEnterClicked: (() -> Unit)? = null,
//    onBackClicked: (() -> Unit)? = null,
) {
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()
    val focusLocalManager = LocalFocusManager.current



    var passwordVisible by rememberSaveable() { mutableStateOf(passwordVisibleInit ?: true) }
    OutlinedTextField(
        modifier = modifier.heightIn(min = 65.dp).width(width).onPreviewKeyEvent {
            if (focusManager != null) {
                onNextButtonClicked(it) {
                    focusManager.moveFocus(FocusDirection.Next)
                }
                if (onEnterClicked == null) {
                    onEnterButtonClicked(it) {
                        focusManager.moveFocus(FocusDirection.Next)
                    }
                }
            }


            if (focusManager != null && !isMoveUpLocked) {
                onBackButtonClicked(it) {
                    focusManager.moveFocus(FocusDirection.Previous)
                }
            }
            false
        }.onFocusEvent { fs ->
            if (fs.isFocused) {
                coroutineScope.launch {
                    bringIntoViewRequester.bringIntoView()
                }
            }
        }

        ,
        shape = RoundedCornerShape(15.dp),
        value = value,
        singleLine = isSingleLine,
        placeholder = { if(supText != null) Text(supText, modifier = Modifier.alpha(.7f)) },
        label = { if(text != null)Text(text) },
        onValueChange = {
            onValueChange(it)
        },
        keyboardActions = KeyboardActions(
            onDone = {
                if (onEnterClicked != null) {
                    onEnterClicked()
                }
            }
        ),
        visualTransformation = if (isDateEntry) DateTransformation() else if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = if (passwordVisibleInit != null) KeyboardType.Password else keyboardType,
            autoCorrect = (passwordVisibleInit == null && autoCorrect) || (autoCorrect),
            imeAction = if (imeAction != ImeAction.None) {
                imeAction
            } else {
                if (focusManager != null && onEnterClicked == null) {
                    ImeAction.Next
                } else {
                    ImeAction.Done
                }
            }
        ),
        enabled = isEnabled,
        trailingIcon = if (trailingIcon != null) {
            {
                trailingIcon()
            }
        } else if (passwordVisibleInit != null) {
            {
                val image = if (passwordVisible)
                    Icons.Rounded.Visibility
                else Icons.Rounded.VisibilityOff

                // Please provide localized description for accessibility services
                val description = if (passwordVisible) "Hide password" else "Show password"

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, description)
                }
            }
        } else null,
        leadingIcon = if (leadingIcon != null) {
            {
                leadingIcon()
            }
        } else if (passwordVisibleInit != null && isLockIcon) {
            {
                val image = Icons.Rounded.Lock

                // Please provide localized description for accessibility services
                val description = "Password"

                Icon(imageVector = image, description)
            }
        } else null,
    )
}

@ExperimentalComposeUiApi
fun onNextButtonClicked(it: KeyEvent, onNextClicked: () -> Unit) {
    if ((it.key.keyCode == Key.DirectionDown.keyCode) && it.type == KeyEventType.KeyDown) {
        onNextClicked()
    }
}

@ExperimentalComposeUiApi
fun onEnterButtonClicked(it: KeyEvent, onEnterClicked: () -> Unit) {
    if (it.key.keyCode == Key.Enter.keyCode && it.type == KeyEventType.KeyDown) {
        onEnterClicked()
    }
}


@ExperimentalComposeUiApi
fun onBackButtonClicked(it: KeyEvent, onUpClicked: () -> Unit) {
    if ((it.key.keyCode == Key.DirectionUp.keyCode) && it.type == KeyEventType.KeyDown) {
        onUpClicked()
    }
}

class DateTransformation() : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        return dateFilter(text)
    }
}


fun dateFilter(text: AnnotatedString): TransformedText {

    val trimmed = if (text.text.length >= 8) text.text.substring(0..7) else text.text
    var out = ""
    for (i in trimmed.indices) {
        out += trimmed[i]
        if (i % 2 == 1 && i < 4) out += "."
    }

    val numberOffsetTranslator = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            if (offset <= 1) return offset
            if (offset <= 3) return offset +1
            if (offset <= 8) return offset +2
            return 10
        }

        override fun transformedToOriginal(offset: Int): Int {
            if (offset <=2) return offset
            if (offset <=5) return offset -1
            if (offset <=10) return offset -2
            return 8
        }
    }

    return TransformedText(AnnotatedString(out), numberOffsetTranslator)
}