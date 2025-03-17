package components.foundation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import view.colorScheme
import view.viewManager


data object TonalCardDefaults {
    val containerColor: Color
        @Composable
        get() = if (viewManager.isDark.value && !viewManager.isAmoled.value) Color.Gray.copy(alpha = .05f)
        else Color.Gray.copy(alpha = .1f)
}

@Composable
fun TonalCard(
    modifier: Modifier = Modifier,
    shape: Shape = CardDefaults.elevatedShape,
    containerColor: Color = TonalCardDefaults.containerColor,
    contentColor: Color = colorScheme.onSurface,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    Surface(
        modifier = modifier
            .then(
                if (onClick != null) Modifier.clip(shape).clickable {
                    onClick()
                }
                else Modifier
            ),
        shape = shape,
        color = containerColor,
        contentColor = contentColor
    ) {
        Box {
            content()
        }
    }
}

