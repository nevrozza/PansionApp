package components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import resources.RIcons

@Composable
fun Stepper(
    count: Int,
    isEditable: Boolean,
    maxCount: Int,
    minCount: Int,
    height: Dp = 25.dp,
    modifier: Modifier = Modifier,
    onChangeCount: (Int) -> Unit
) {
    val animatedAllAlpha by animateFloatAsState(if (count != 0) 1f else .2f)
    Row(
        modifier = modifier.sizeIn(minWidth = 50.dp).height(height).border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(if (count != 0) 1f else .2f),
            shape = RoundedCornerShape(30)
        ).clip(RoundedCornerShape(30)).alpha(animatedAllAlpha),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (isEditable) {
//            val animatedAlpha by animateFloatAsState(if (count != minCount) 1f else .2f)
            IconButton(
                onClick = {
                    onChangeCount(count - 1)
                },
                enabled = count != minCount
            ) {
                GetAsyncIcon(
                    RIcons.Minus,
                    size = height-7.dp
                )
            }
        }
        AnimatedContent(
            count
        ) {
            val mark = it.toString()
            Text(
                (if (!mark.contains("-") && !mark.contains("+")
                ) "+" else "") + mark
            )
        }
        if (isEditable) {
            IconButton(
                onClick = { onChangeCount(count + 1) },
                enabled = count != maxCount
            ) {
                GetAsyncIcon(
                    RIcons.Add,
                    size = height-7.dp
                )
            }
        }
    }
}