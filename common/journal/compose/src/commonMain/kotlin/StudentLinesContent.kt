import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.*
import components.networkInterface.NetworkState
import components.networkInterface.isLoading
import dev.chrisbanes.haze.HazeState
import report.ClientStudentLine
import resources.RIcons
import studentLines.StudentLinesComponent
import studentLines.StudentLinesStore
import studentReportDialog.StudentReportDialogStore

@OptIn(ExperimentalFoundationApi::class)
@ExperimentalMaterial3Api
@ExperimentalLayoutApi
@Composable
fun StudentLinesContent(
    component: StudentLinesComponent
) {

    

    val model by component.model.subscribeAsState()
    val nModel by component.nInterface.networkModel.subscribeAsState()


    LaunchedEffect(Unit) {
        if (!nModel.isLoading) component.onEvent(StudentLinesStore.Intent.Init)
    }

    val hazeState = remember { HazeState() }

    Scaffold(
        Modifier.fillMaxSize(),
        topBar = {
            AppBar(
                navigationRow = {
                    IconButton(
                        onClick = { component.onOutput(StudentLinesComponent.Output.Back) }
                    ) {
                        GetAsyncIcon(
                            path = RIcons.ChevronLeft
                        )
                    }
                },
                title = {
                    Text(
                        "Прошедшие занятия",
//                        modifier = Modifier.padding(start = 10.dp),
                        fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                hazeState = hazeState
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize()) {
            Crossfade(nModel.state) { state ->
                when (state) {
                    NetworkState.None -> CLazyColumn(padding, hazeState = hazeState) {
                        itemsIndexed(items = model.studentLines, key = { i, sl -> i }) { i, sl ->

                            if (i == model.studentLines.indexOfFirst { it.date == sl.date }) {
                                if (i != 0) {
                                    Spacer(Modifier.height(15.dp))
                                }
                                Text(
                                    sl.date,
                                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(start = 10.dp)
                                )
                            }
                            ClientStudentLineContent(sl = sl) {
                                component.studentReportDialog.onEvent(
                                    StudentReportDialogStore.Intent.OpenDialog(
                                        login = model.login,
                                        reportId = sl.reportId
                                    )
                                )
                            }
                        }
                    }

                    NetworkState.Loading -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }

                    NetworkState.Error -> DefaultErrorView(nModel, DefaultErrorViewPos.CenteredFull)
                    
                }
            }
        }

    }

    StudentReportDialogContent(
        component = component.studentReportDialog,
        hazeState = hazeState
    )
}

@Composable
private fun ClientStudentLineContent(
    sl: ClientStudentLine,
    onClick: () -> Unit
) {

    Surface(
        shape = RoundedCornerShape(15.dp),
        tonalElevation = 3.dp,
        modifier = Modifier.fillMaxWidth().padding(horizontal = (2.5).dp).padding(top = 5.dp),
        onClick = { onClick() }
    ) {
        Column(Modifier.padding(horizontal = 10.dp, vertical = 5.dp)) {
            Text(
                buildAnnotatedString {
                    append(sl.subjectName)
                    append(" ")
                    withStyle(
                        SpanStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = MaterialTheme.typography.titleMedium.fontSize
                        )
                    ) {
                        append(sl.groupName)
                        withStyle(SpanStyle()) {
                            append(" в ${sl.time}")
                        }
                    }
                },
                fontWeight = FontWeight.Black, fontSize = MaterialTheme.typography.titleLarge.fontSize
            )
            Text(
                text = if (sl.topic.isNotBlank()) sl.topic else "Тема не выставлена"
            )
            Spacer(Modifier.height(2.dp))
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {

                if (sl.lateTime.isNotBlank() && sl.lateTime != "00 мин" && sl.lateTime != "0") {
                    GetAsyncIcon(
                        RIcons.HourglassBottom,
                        size = 18.dp
                    )
                    Spacer(Modifier.width(5.dp))
                    Text(sl.lateTime.removePrefix("0"))
                    Spacer(Modifier.width(10.dp))
                }
                if (sl.isLiked in listOf("t", "f")) {
                    GetAsyncIcon(
                        RIcons.Like,
                        modifier = Modifier.rotate(if (sl.isLiked != "t") 180f else 0f)
                    )
                    Spacer(Modifier.width(10.dp))
                }
                PrisutCheckBox(
                    modifier = Modifier.size(27.dp),
                    attendedType = sl.attended ?: "0",
                    reason = null,
                    enabled = false
                ) {}
            }
        }
    }
}
