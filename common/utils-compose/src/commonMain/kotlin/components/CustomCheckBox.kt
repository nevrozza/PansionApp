package components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import resources.RIcons

@Composable
fun CustomCheckbox(
    checked: Boolean,
    modifier: Modifier = Modifier.padding(end = 10.dp).size(25.dp)
) {
    val color by animateColorAsState(if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(1f))
    Box(
        modifier
            .then(
                Modifier.border(
                    width = 1.dp,
                    color = color,
                    shape = RoundedCornerShape(40)
                ).clip(RoundedCornerShape(40))
            ),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = checked,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Box(
                Modifier.clip(RoundedCornerShape(40)).fillMaxSize()
                    .background(MaterialTheme.colorScheme.primary)
            )
            GetAsyncIcon(
                path = RIcons.Check,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.fillMaxSize()
            )
        }
    }

}

@Composable
fun Modifier.cClickable(isEnabled: Boolean = true, onClick: () -> Unit) = Modifier.clip(RoundedCornerShape(30)).clickable(enabled = isEnabled) { onClick() }