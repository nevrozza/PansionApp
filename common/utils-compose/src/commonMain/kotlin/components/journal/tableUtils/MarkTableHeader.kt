package components.journal.tableUtils

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import server.cut

@Composable
fun MarkTableTableHeader(
    date: String,
    startColumnPadding: Dp
) {
    Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
        Spacer(Modifier.width(startColumnPadding))
        Text(
            text = date.cut(5),
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            softWrap = false
        )
    }
}