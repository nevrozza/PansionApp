package forks.splitPane

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import components.GetAsyncIcon
import resources.RIcons
import utils.cursor.cursorForHorizontalResize
import view.LocalViewManager

@ExperimentalSplitPaneApi
fun SplitPaneScope.dSplitter(
    isFullScreen: MutableState<Boolean>? = null
) = splitter {
    visiblePart {
        val viewManager = LocalViewManager.current
        Box(Modifier.fillMaxHeight()) {
            if (isFullScreen != null) {
                AnimatedContent(
                    if (isFullScreen.value) RIcons.MINIMIZE else RIcons.MAXIMIZE,
                    modifier = Modifier.size(25.dp).offset(x = 20.dp, y = viewManager.topPadding + 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = {
                            isFullScreen.value = !isFullScreen.value
                        }) {
                        GetAsyncIcon(
                            it,
                            size = 18.dp
                            )
                    }
                }
            }
            AnimatedVisibility(
                isFullScreen == null || (!isFullScreen.value),
                modifier = Modifier.width(4.5.dp).height(25.dp).align(Alignment.Center)
            ) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(50.dp))
                        .background(
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(
                                alpha = .5f
                            )
                        )
                )
            }
        }

    }
//    if(isFullScreen != null && !isFullScreen.value) {
    handle {

        Box(
            modifier = Modifier.width(9.dp)
                .fillMaxHeight().then(
                    if (isFullScreen == null || (!isFullScreen.value)) {

                        Modifier.markAsHandle()
                            .cursorForHorizontalResize(isHorizontal = true)
                    } else {
                        Modifier
                    }
                )
        )
    }
//    }

}