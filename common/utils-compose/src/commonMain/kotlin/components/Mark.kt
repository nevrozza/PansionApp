package components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MarkContent(
    mark: String,
    background: Color = MaterialTheme.colorScheme.primary.copy(
        alpha = .2f
    ),
    addModifier: Modifier = Modifier,
    offset: DpOffset = DpOffset(0.dp, 0.dp),
    paddingValues: PaddingValues = PaddingValues(start = 5.dp, top = 5.dp),
    size: Dp = 25.dp,
    textYOffset: Dp = 0.dp
) {
    Box(
        Modifier.padding(paddingValues)
            .offset(offset.x, offset.y)
            .size(size)
            .clip(RoundedCornerShape(percent = 30))
            .background(
                background
            )
            .then(addModifier),
        contentAlignment = Alignment.Center
    ) {
        Text(
            mark,
            fontSize = size.value.sp/1.6f,
            modifier = Modifier.fillMaxSize().offset(y = textYOffset),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Black
        )
    }
}