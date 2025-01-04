import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.CustomTextButton
import components.DefaultErrorView
import components.DefaultErrorViewPos
import components.cAlertDialog.CAlertDialogStore
import components.networkInterface.NetworkState
import decomposeComponents.CAlertDialogContent
import homeTasksDialog.HomeTasksDialogComponent
import homework.ClientReportHomeworkItem
import server.fetchReason
import view.esp

@Composable
fun HomeTasksDialogContent(
    component: HomeTasksDialogComponent
) {
    val model by component.model.subscribeAsState()
    val nModel by component.nInterface.networkModel.subscribeAsState()
    CAlertDialogContent(
        component.dialogComponent,
        isCustomButtons = true,
        title = "ДЗ"
    ) {
        Crossfade(nModel.state) {
            when (it) {
                NetworkState.None -> if (model.homeTasks.isNotEmpty()) LazyColumn(Modifier.fillMaxSize()) {
                    items(model.homeTasks) { task ->
                        TaskView(task,
                            openReportFun = if (component.openReport != null) {
                                {
                                    component.dialogComponent.onEvent(CAlertDialogStore.Intent.HideDialog)
                                    component.openReport?.invoke(it)
                                }
                            } else {
                                null
                            })
                        Spacer(Modifier.height(5.dp))
                    }
                } else Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Здесь пусто =/")
                }

                NetworkState.Loading -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

                NetworkState.Error -> DefaultErrorView(
                    nModel,
                    DefaultErrorViewPos.CenteredFull
                )
            }
        }
    }
}

@Composable
private fun TaskView(
    task: ClientReportHomeworkItem,
    openReportFun: ((Int) -> Unit)?
) {
    Box(
        modifier = Modifier.padding(top = 6.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(15.dp))
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp))
            .animateContentSize()
    ) {
        Column(Modifier.padding(4.dp).padding(start = 4.dp).fillMaxWidth()) {
            Text(
                buildAnnotatedString {
                    withStyle(SpanStyle(fontSize = 18.esp, fontWeight = FontWeight.Bold)) {
                        append("Дата: ")
                    }
                    append("${task.date}")
                    withStyle(SpanStyle(fontSize = 18.esp, fontWeight = FontWeight.Bold)) {
                        append(" Время: ")
                    }
                    append("${task.time}")

                    withStyle(SpanStyle(fontSize = 18.esp, fontWeight = FontWeight.Bold)) {
                        append(" ")
                        append(if (task.studentLogins.isNullOrEmpty()) "Для всех" else "Для ${task.studentLogins?.size}")
                    }
                }
            )
            Text(
                buildAnnotatedString {
                    withStyle(SpanStyle(fontSize = 18.esp, fontWeight = FontWeight.Bold)) {
                        append("Тип: ")
                    }
                    append("${typesList[task.type] ?: "Не выбрано"}")
                    if (task.stups > 0) {
                        append(" (+${task.stups})")
                    }
                }
            )

            Text(task.text)
            if (openReportFun != null) {
                CustomTextButton(text = "Открыть отчёт", modifier = Modifier.align(Alignment.End)) {
                    openReportFun(task.id)
                }
            }
        }
    }
}

val typesList = mapOf(
    "!dz1" to fetchReason("!dz1"),
    "!dz2" to fetchReason("!dz2"),
    "!dz3" to fetchReason("!dz3"),
    "!dz4" to fetchReason("!dz4"),
    "!st1" to fetchReason("!st1"),
    "!st2" to fetchReason("!st2"),
    "!st3" to fetchReason("!st3"),
    "!st5" to fetchReason("!st5"),
)