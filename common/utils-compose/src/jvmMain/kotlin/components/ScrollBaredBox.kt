package components

import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
actual fun ScrollBaredBox(
    vState: LazyListState,
    hState: ScrollState,
    modifier: Modifier,
    height: MutableState<Dp>,
    width: MutableState<Dp>,
    content: @Composable () -> Unit,
) {
    val l = LocalScrollbarStyle.current
    val style = ScrollbarStyle(
        minimalHeight = l.minimalHeight,
        thickness = l.thickness,
        shape = l.shape,
        hoverDurationMillis = l.hoverDurationMillis,
        unhoverColor = MaterialTheme.colorScheme.surfaceColorAtElevation((0.5).dp),
        hoverColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
    )
    Box(modifier) {
        Box(
            Modifier.padding(end = 14.dp, bottom = 14.dp).clip(RoundedCornerShape(15.dp)).border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = .4f),
            RoundedCornerShape(15.dp)
        )) {
            content()
        }
        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd)
                .height(height.value).padding(end = 2.dp),
            adapter = rememberScrollbarAdapter(vState),
            style = style
        )
        HorizontalScrollbar(
            modifier = Modifier.align(Alignment.BottomStart)
                .width(width.value)
                .padding(bottom = 2.dp),
            adapter = rememberScrollbarAdapter(hState),
            style = style
        )
    }
}