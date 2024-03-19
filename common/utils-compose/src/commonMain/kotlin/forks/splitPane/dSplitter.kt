package forks.splitPane

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import cursorForHorizontalResize

@ExperimentalSplitPaneApi
fun SplitPaneScope.dSplitter() = splitter {
    visiblePart {
        Box(Modifier.fillMaxHeight(), contentAlignment = Alignment.Center) {
            Box(
                Modifier
                    .width(4.5.dp)
                    .height(25.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .background(
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(
                            alpha = .5f
                        )
                    )
            )
        }
    }
    handle {
        Box(
            Modifier
                .markAsHandle()
                .cursorForHorizontalResize()
                .width(9.dp)
                .fillMaxHeight()
        )
    }

}