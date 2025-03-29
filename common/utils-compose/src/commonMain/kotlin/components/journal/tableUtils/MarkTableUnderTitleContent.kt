package components.journal.tableUtils

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun MarkTableUnderTitleContent(
    avg: String,
    normStupsCount: Int,
    dsStupsCount: Int
) {
    Row() {
        Spacer(Modifier.width(20.dp))
        Text(
            (avg),
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.width(7.dp))
        Text(
            "${if (normStupsCount > 0) "+" else ""}$normStupsCount/${if (dsStupsCount > 0) "+" else ""}$dsStupsCount",
            fontWeight = FontWeight.Bold
        )
    }
}