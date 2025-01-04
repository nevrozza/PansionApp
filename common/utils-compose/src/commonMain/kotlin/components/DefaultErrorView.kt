package components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import components.networkInterface.NetworkInterface
import resources.RIcons
import view.esp

sealed class DefaultErrorViewPos {
    data object Centered : DefaultErrorViewPos()
    data object CenteredNotFull : DefaultErrorViewPos()
    data object CenteredFull : DefaultErrorViewPos()
    data object Unspecified : DefaultErrorViewPos()
}

@Composable
fun DefaultErrorView(
    model: NetworkInterface.NetworkModel,
    pos: DefaultErrorViewPos = DefaultErrorViewPos.Centered,
    modifier: Modifier = Modifier,
    text: String = model.error,
    buttonText: String = "Попробовать ещё раз",
    isCompact: Boolean = false,
    onClick: () -> Unit = model.onFixErrorClick
) {
    val isErrorExpanded = remember { mutableStateOf(false) }
    val errorText = getErrorText(model.throwable)
    val textAlign = if (pos != DefaultErrorViewPos.Unspecified) TextAlign.Center else TextAlign.Unspecified
    Column(
        if (pos == DefaultErrorViewPos.CenteredFull) modifier.fillMaxSize()
        else if (pos == DefaultErrorViewPos.Centered) modifier.fillMaxWidth()
        else modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = if (pos != DefaultErrorViewPos.Unspecified) Alignment.CenterHorizontally else Alignment.Start
    ) {
        Row(Modifier.cClickable {
            isErrorExpanded.value = !isErrorExpanded.value
        }, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            Text(text, textAlign = textAlign)
            val chevronRotation = animateFloatAsState(if (isErrorExpanded.value) 90f else -90f)
            GetAsyncIcon(
                path = RIcons.ChevronLeft,
                modifier = Modifier.padding(start = 10.dp).rotate(chevronRotation.value),
                size = 15.dp
            )
        }
        AnimatedVisibility(isErrorExpanded.value) {
            Text(
                text = errorText,
                modifier = Modifier.alpha(.5f),
                textAlign = textAlign,
                fontSize = 10.esp,
                lineHeight = 10.esp
            )
        }
        if (buttonText.isNotBlank()) {
            if (!isCompact) Spacer(Modifier.height(7.dp))
            CustomTextButton(buttonText, textAlign = textAlign) {
                onClick()
            }
        }
    }
}

private fun getErrorText(throwable: Throwable): String {
    return if (throwable.message.toString().contains("Could not connect to the server")) {
        "Could not connect to the server"
    } else throwable.message.toString()
}