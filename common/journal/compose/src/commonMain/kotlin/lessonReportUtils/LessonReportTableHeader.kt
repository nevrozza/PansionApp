package lessonReportUtils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import components.GetAsyncIcon
import lessonReport.ColumnTypes
import lessonReport.ReportColumn
import resources.RIcons
import server.st

@Composable
fun LessonReportTableHeader(
    column: ReportColumn
) {
    Row(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).zIndex(4f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
        if (column.type == ColumnTypes.prisut) {
            Spacer(Modifier.width(50.dp))
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (column.type.st in listOf("!dz", "!st", "!ds")) {
                GetAsyncIcon(
                    path = when(column.type.st) {
                        "!dz" -> RIcons.Home
                        "!st" -> RIcons.Star
                        "!ds" -> RIcons.Shield
                        else -> RIcons.QuestionCircle
                    },
                    size = 14.dp,
                    modifier = Modifier.offset(
                        y = (0).dp,
                        x = (-1).dp
                    )
                )
            }
            Text(
                text = column.title.removePrefix("dz")
                    .removePrefix("cl")
                    .removePrefix("st").removePrefix("ds"),
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,

                overflow = TextOverflow.Ellipsis,
                softWrap = false
            )
        }
    }
}
