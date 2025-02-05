package lessonReportUtils

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun TableCellOutline(
    color: Color = MaterialTheme.colorScheme.outline.copy(alpha = .4f),
    xThickness: Dp = 1.dp,
    yThickness: Dp = 1.dp,
    contentPadding: PaddingValues = PaddingValues((1).dp),
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(Modifier.fillMaxWidth()) {
            Box(Modifier.padding(contentPadding)) {
                content()
            }
            VerticalDivider(
                modifier = Modifier.fillMaxHeight(),
                thickness = yThickness,
                color = color
            )
        }
        HorizontalDivider(modifier = Modifier.fillMaxWidth(), thickness = xThickness, color = color)
    }

}