package components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DatesLine(dates: List<Pair<Int, String>>, currentDate: Pair<Int, String>, onClick: (Pair<Int, String>) -> Unit) {
    Column {
        Row(
            Modifier.height(50.dp).fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            dates.forEach { item ->
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
        Spacer(Modifier.height(5.dp))
    }
}