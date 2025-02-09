package components.foundation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import utils.hv

@Composable
fun CTextButton(
    text: String,
    modifier: Modifier = Modifier,
    fontWeight: FontWeight = FontWeight.SemiBold,
    color: Color = MaterialTheme.colorScheme.primary,
    fontSize: TextUnit = TextUnit.Unspecified,
    textAlign: TextAlign = TextAlign.Center,
    isButtonEnabled: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    onClick: () -> Unit
) {
    CTextButton(text = buildAnnotatedString {
        withStyle(SpanStyle(fontWeight = fontWeight
        )) {
            append(
                text
            )
        }
    }, color = color, maxLines = maxLines, onClick = onClick, modifier = modifier, fontSize = fontSize, isButtonEnabled = isButtonEnabled, textAlign = textAlign)
}

@Composable
fun CTextButton(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    fontSize: TextUnit = TextUnit.Unspecified,
    textAlign: TextAlign = TextAlign.Center,
    isButtonEnabled: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isDark =
        (interactionSource.collectIsHoveredAsState().value || interactionSource.collectIsPressedAsState().value)
    val colorState by animateColorAsState(
        animationSpec = spring(stiffness = Spring.StiffnessHigh),
        targetValue = if (isDark) color.hv() else color
    )
//    Color(1.0f, 1.0f, 1.0f)
    Text(text,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        textAlign = textAlign,
        color = colorState.copy(alpha = if(isButtonEnabled) 1f else .5f), fontSize = fontSize,
        modifier = modifier.then(Modifier.hoverable(interactionSource)
            .clickable(interactionSource = interactionSource, indication = null, enabled = isButtonEnabled) {
                onClick()
            }))
}
