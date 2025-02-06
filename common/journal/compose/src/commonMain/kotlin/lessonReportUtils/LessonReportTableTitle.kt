package lessonReportUtils

import LikeDislikeRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import lessonReport.LessonReportComponent
import lessonReport.LessonReportStore
import lessonReport.StudentLine
import view.blend

@Composable
fun LessonReportTableTitle(
    student: StudentLine,
    model: LessonReportStore.State,
    component: LessonReportComponent
) {
    val fioColor =
        MaterialTheme.colorScheme
            .onSurface.blend(
                when (student.login) {
                    in model.likedList -> Color.Green
                    in model.dislikedList -> Color.Red
                    else -> MaterialTheme.colorScheme
                        .onSurface
                }
            )
    Row(
        modifier = Modifier
            .padding(start = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = student.shortFio,
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            fontWeight = FontWeight.Normal,
            color = fioColor,
            modifier = Modifier
        )
        Spacer(Modifier.width(10.dp))
        LikeDislikeRow(
            component = component,
            student = student
        )
    }
}