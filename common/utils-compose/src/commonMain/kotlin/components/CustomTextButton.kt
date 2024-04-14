package components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import hv
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
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun CustomTextButton(
    text: String,
    modifier: Modifier = Modifier,
    fontWeight: FontWeight = FontWeight.SemiBold,
    color: Color = MaterialTheme.colorScheme.primary,
    fontSize: TextUnit = TextUnit.Unspecified,
    onClick: () -> Unit
) {
    CustomTextButton(text = buildAnnotatedString {
        withStyle(SpanStyle(fontWeight = fontWeight)) {
            append(
                text
            )
        }
    }, color = color, onClick = onClick, modifier = modifier, fontSize = fontSize)
}

@Composable
fun CustomTextButton(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    fontSize: TextUnit = TextUnit.Unspecified,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isDark =
        (interactionSource.collectIsHoveredAsState().value || interactionSource.collectIsPressedAsState().value)
    val color by animateColorAsState(
        animationSpec = spring(stiffness = Spring.StiffnessHigh),
        targetValue = if (isDark) color.hv() else color
    )
    Color(1.0f, 1.0f, 1.0f)
    Text(text, color = color, fontSize = fontSize,
        modifier = modifier.then(Modifier.hoverable(interactionSource)
            .clickable(interactionSource = interactionSource, indication = null) {
                onClick()
            }))
}