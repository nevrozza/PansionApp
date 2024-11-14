package components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import server.weekPairs

@Composable
fun DateButton(
    currentDate: String,
    dayOfWeek: Int,
    date: String,
    onClick: () -> Unit
) {
    FilledTonalButton(
        modifier = Modifier.height(50.dp).padding(end = 5.dp),
        shape = RoundedCornerShape(30),
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = if (date == currentDate) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceColorAtElevation(
                2.dp
            ),
            contentColor = if (date == currentDate) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface
        ),
        onClick = {
            onClick()
        },
        contentPadding = PaddingValues(3.dp)
    ) {
        Box(Modifier.fillMaxSize()) {
            Text(
                text = date.substring(0, 5),
                fontWeight = FontWeight.Bold,
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                modifier = Modifier.padding(bottom = 10.dp)
                    .align(Alignment.Center)
            )
            Text(
                text = weekPairs[dayOfWeek] ?: "null",
                fontWeight = FontWeight.SemiBold,
                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}