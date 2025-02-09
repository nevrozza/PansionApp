package lessonReportUtils

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import components.GetAsyncIcon
import lessonReport.ColumnTypes
import lessonReport.ReportColumn
import resources.RIcons
import server.st

@Composable
fun LessonReportTableHeader(
    column: ReportColumn,
    firstColumnPadding: Dp
) {
    Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
        if (column.type == ColumnTypes.PRISUT) {
            Spacer(Modifier.width(firstColumnPadding))
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (column.type.st in listOf("!dz", "!st", "!ds")) {
                GetAsyncIcon(
                    path = when(column.type.st) {
                        "!dz" -> RIcons.HOME
                        "!st" -> RIcons.STAR
                        "!ds" -> RIcons.SHIELD
                        else -> RIcons.QUESTION_CIRCLE
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
