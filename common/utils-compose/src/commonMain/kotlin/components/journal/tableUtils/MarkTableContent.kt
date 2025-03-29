package components.journal.tableUtils

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import components.MarkTableItem
import components.journal.MarkTableUnit
import view.typography

@Composable
fun MarkTableContent(
    marks: List<MarkTableItem>,
    markSize: Dp,
    nka: String
) {
    Box(
        modifier = Modifier.fillMaxSize(), //25
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            //                                            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            marks//.filter { it.login == f.first }
                .forEach { mark ->
                    MarkTableUnit(
                        m = mark,
                        markSize = (markSize - 6.dp) //because of start padding
                    )
                }
        }
        Text(
            nka,
            fontSize = typography.bodyMedium.fontSize,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.TopEnd)
                .offset(y = (-30).dp)
        )
    }
}

