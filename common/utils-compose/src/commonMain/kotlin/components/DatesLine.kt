package components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun DatesLine(
    dates: List<Pair<Int, String>>,
    currentDate: Pair<Int, String>,
    firstItemWidth: Dp = 0.dp,
    onClick: (Pair<Int, String>) -> Unit
) {

    val lazyState = rememberLazyListState()

    Column {
        LazyRow(
            Modifier.height(50.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            state = lazyState
        ) {
            item {
                Spacer(Modifier.width(firstItemWidth))
            }
            items(dates, key = { it.second }) { item ->
                DateButton(
                    currentDate = currentDate.second,
                    dayOfWeek = item.first,
                    date = item.second
                ) {
                    onClick(item)
                    //component.onEvent(ScheduleStore.Intent.ChangeCurrentDate(item))
                }

            }
        }
        LaunchedEffect(Unit) {
            lazyState.animateScrollToItem(
                dates.indexOfFirst { it == currentDate }.coerceAtLeast(0)
            )
        }
        Spacer(Modifier.height(5.dp))
    }
}