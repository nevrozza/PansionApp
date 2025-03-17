package components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.listDialog.ListComponent
import components.listDialog.ListDialogStore
import decomposeComponents.listDialogComponent.ListDialogDesktopContent

@Composable
fun PeriodButton(
    inActiveText: String,
    currentPeriod: String,
    isActive: Boolean,
    activeColor: Color = MaterialTheme.colorScheme.primaryContainer,
    component: ListComponent
) {

    val color = animateColorAsState(
        if (isActive) activeColor
        else Color.Transparent
    )
    val model by component.model.subscribeAsState()
    Box() {
        AssistChip(
            onClick = { component.onEvent(ListDialogStore.Intent.ShowDialog) },
            label = {
                AnimatedContent(
                    if (isActive) {
                        model.list.firstOrNull { it.id == currentPeriod }?.text
                                ?: "Загрузка.."
                    } else {inActiveText}
                ) {
                    Text(
                        it, maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            },
            modifier = Modifier.animateContentSize(),
            colors = AssistChipDefaults.assistChipColors(containerColor = color.value)
        )
        ListDialogDesktopContent(component, mobileView = true)
    }
}