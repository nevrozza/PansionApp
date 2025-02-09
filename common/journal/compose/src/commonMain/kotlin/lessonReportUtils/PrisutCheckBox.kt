package lessonReportUtils

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import components.GetAsyncIcon
import resources.RIcons
import androidx.compose.desktop.ui.tooling.preview.utils.popupPositionProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrisutCheckBox(
    modifier: Modifier,
    attendedType: String,
    reason: String?,
    enabled: Boolean,
    onCheckedChange: (String) -> Unit
) {
    val tState = rememberTooltipState(isPersistent = true)
    TooltipBox(
        state = tState,
        tooltip = {
            if (reason != null) {
                PlainTooltip() {
                    Text(
                        reason.toString()
                    )
                }
            }
        },
        positionProvider = popupPositionProvider,
        enableUserInput = true
    ) {

        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            OutlinedCard(
                modifier = Modifier
                    .fillMaxSize(),//.alpha(if (checked) 1f else .5f),
                shape = AbsoluteRoundedCornerShape(40),
                border = BorderStroke(
                    color = if (attendedType == "0") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant, //if (checked) MaterialTheme.colorScheme.surface else
                    width = 1.dp
                ),
                onClick = {
                    //0-bil. 1-n. 2-Uv
                    if (enabled) {
                        val newValue = when (attendedType) {
                            "0" -> "1"
                            "1" -> "2"
                            else -> "0"
                        }
                        onCheckedChange(newValue)
                    }
                }) {
                AnimatedVisibility(attendedType == "0") {
                    GetAsyncIcon(
                        path = RIcons.CHECK,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.fillMaxSize()
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }
                AnimatedVisibility(attendedType == "1") {
                    Text(
                        text = "Н",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxSize(),
                        textAlign = TextAlign.Center
                    )
                }
                AnimatedVisibility(attendedType == "2") {
                    Text(
                        text = "УВ",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxSize(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

    }
}