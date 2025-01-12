package components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import view.handy

@Composable
fun RowScope.FeatureButton(
    text: String,
    decoration: @Composable (() -> Unit),
    isActive: Boolean,
    onClick: () -> Unit,
) {
    ElevatedCard(
        Modifier.fillMaxWidth().clip(CardDefaults.elevatedShape)
            .weight(1f)
            .handy()
            .clickable(
                enabled = !isActive
            ) { //enabled = !isExpanded
                onClick()
            },
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (isActive) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            Modifier.fillMaxHeight().padding(vertical = 10.dp, horizontal = 15.dp)
                .fillMaxWidth().defaultMinSize(minHeight = 80.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text,
                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                fontWeight = FontWeight.Bold
            )
            //                            Spacer(Modifier.height(5.dp))
            Spacer(Modifier.height(5.dp))
            Box(
                Modifier.fillMaxWidth()
                    .padding(end = 5.dp, bottom = 5.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                decoration()
            }
        }
    }
}

@Composable
fun RowScope.FeatureButton(
    text: String,
    decoration: String,
    isActive: Boolean,
    onClick: () -> Unit,

) {
    FeatureButton(
        text,
        decoration = {
            GetAsyncIcon(
                path = decoration,
                tint = MaterialTheme.colorScheme.secondary
            )
        },
        isActive,
        onClick
    )
}