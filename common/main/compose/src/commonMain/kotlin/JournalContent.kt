import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowForwardIos
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.AlphaTestZatichka
import components.AppBar
import components.CLazyColumn
import components.CustomTextButton
import components.ReportTitle
import components.listDialog.ListDialogStore
import components.networkInterface.NetworkState
import decomposeComponents.CAlertDialogContent
import decomposeComponents.listDialogComponent.ListDialogDesktopContent
import decomposeComponents.listDialogComponent.ListDialogMobileContent
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import journal.JournalComponent
import journal.JournalStore
import pullRefresh.PullRefreshIndicator
import pullRefresh.pullRefresh
import pullRefresh.rememberPullRefreshState
import server.Moderation
import server.Roles
import view.LocalViewManager
import view.WindowScreen
import view.handy
import view.rememberImeState

@ExperimentalMaterial3Api
@ExperimentalLayoutApi
@Composable
fun JournalContent(
    component: JournalComponent,
    role: String,
    moderation: String,
    isNotMinimized: Boolean = true,
    onRefresh: () -> Unit
) {
    if (moderation != Moderation.nothing || role == Roles.teacher) {
        TrueJournalContent(component, isNotMinimized, onRefresh)
    } else {
        AlphaTestZatichka(
            onSettingsClick = {
                component.onOutput(JournalComponent.Output.NavigateToSettings)
            }
        ) { }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalHazeMaterialsApi::class)
@Composable
private fun TrueJournalContent(
    component: JournalComponent,
    isNotMinimized: Boolean,
    onRefresh: () -> Unit
) {
    val model by component.model.subscribeAsState()
    val nModel by component.nInterface.networkModel.subscribeAsState()
    val nORModel by component.nOpenReportInterface.networkModel.subscribeAsState()

    val focusManager = LocalFocusManager.current
    val viewManager = LocalViewManager.current
//    val scrollState = rememberScrollState()
    val imeState = rememberImeState()
    val lazyListState = rememberLazyListState()

    val isExpanded = viewManager.orientation.value == WindowScreen.Expanded && isNotMinimized

    if (model.creatingReportId != -1) {
        component.createReport()
        component.onEvent(JournalStore.Intent.ResetCreatingId)
    } else if (model.openingReportData != null) {
        component.openReport(model.openingReportData!!)
        component.onEvent(JournalStore.Intent.ResetReportData)
    }

    val refreshState = rememberPullRefreshState(
        nModel.state == NetworkState.Loading && model.headers.isNotEmpty(),
        { component.onEvent(JournalStore.Intent.Refresh) })

    Scaffold(
        Modifier.fillMaxSize()
            .onKeyEvent {
                if (it.key == Key.F5 && it.type == KeyEventType.KeyDown) {
                    onRefresh()
                }
                false
            },
        topBar = {
            AppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Журнал",
                            modifier = Modifier.padding(start = 10.dp),
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Black,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        AnimatedVisibility(nORModel.state == NetworkState.Loading) {
                            Row() {
                                Spacer(Modifier.width(10.dp))
                                CircularProgressIndicator(Modifier.size(25.dp))
                            }
                        }
                    }
                },
                actionRow = {
//                    var x by remember { mutableStateOf(0.0f) }
                    Box() {
                        IconButton(

                            onClick = {
                                component.groupListComponent.onEvent(ListDialogStore.Intent.ShowDialog)
                            }
                        ) {
                            Icon(
                                Icons.Rounded.Add, null
                            )
                        }
                        ListDialogDesktopContent(
                            component = component.groupListComponent
                        )
                    }
                    IconButton(
                        onClick = { if(isExpanded) onRefresh() else component.onEvent(JournalStore.Intent.Refresh) }
                    ) {
                        Icon(
                            Icons.Filled.Refresh, null
                        )
                    }
                    if (isExpanded) {
                        IconButton(
                            onClick = {
                                component.onOutput(JournalComponent.Output.NavigateToSettings)
                            }
                        ) {
                            Icon(
                                Icons.Rounded.Settings, null
                            )
                        }
                    }

                },
                containerColor = Color.Transparent,
                isHaze = true
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            val newState = when {
                NetworkState.Error in listOf(nModel.state, nORModel.state)  -> "Error"
                nModel.state == NetworkState.Loading && model.headers.isNotEmpty() -> "None"
                nModel.state == NetworkState.None -> "None"
                nModel.state == NetworkState.Loading -> "Loading"
                else -> "Error"
            }
            Crossfade(nModel.state, modifier = Modifier) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    when (newState) {
                        "None" -> {
                            CLazyColumn(
                                padding = padding,
                                modifier = Modifier.pullRefresh(refreshState),
                                isBottomPaddingNeeded = true
                            ) {
                                items(model.headers.sortedBy { it.reportId }.reversed()) { item ->
                                    JournalItemCompose(
                                        subjectName = item.subjectName,
                                        groupName = item.groupName,
                                        lessonReportId = item.reportId,
                                        date = item.date,
                                        teacher = item.teacherName,
                                        time = item.time,
                                        isEnabled = true,
                                        isActive = true
                                    ) {
                                        component.onEvent(JournalStore.Intent.FetchReportData(item))
                                    }
                                }
                            }
                        }

                        "Loading" -> {
                            CircularProgressIndicator()
                        }

                        "Error" -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text( if(nModel.state == NetworkState.Error) nModel.error else nORModel.error)
                                Spacer(Modifier.height(7.dp))
                                CustomTextButton("Печально") {
                                    if(nModel.state == NetworkState.Error) nModel.onFixErrorClick() else nORModel.onFixErrorClick()
                                }
                            }
                        }
                    }
                }
            }


            PullRefreshIndicator(
                modifier = Modifier.align(alignment = Alignment.TopCenter),
                refreshing = nModel.state == NetworkState.Loading && model.headers.isNotEmpty(),
                state = refreshState,
            )
            ListDialogMobileContent(component.groupListComponent)
            StudentsPreviewDialog(
                component, model
            )
        }

    }
}

@Composable
fun StudentsPreviewDialog(
    component: JournalComponent,
    model: JournalStore.State
) {
    CAlertDialogContent(
        component.studentsInGroupCAlertDialogComponent,
        title = "Ученики",
        standardCustomButton = {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    component.onEvent(JournalStore.Intent.CreateReport)
                    //component.onOutput(JournalComponent.Output.NavigateToLessonReport(3))
//                            component.studentsInGroupCAlertDialogComponent.onEvent(CAlertDialogStore.Intent.HideDialog)
                }
            ) {
                Text("Создать")
            }
        },
        isCustomButtons = false
    ) {
        Column {
            LazyColumn {
                items(model.studentsInGroup) {
//                            TextButton(
//                                modifier = Modifier.fillMaxWidth(),
//                                onClick = {},
//                                enabled = false,
//                                colors = ButtonDefaults.textButtonColors(
//                                    disabledContentColor = MaterialTheme.colorScheme.onSurface
//                                ),
//                                shape = RoundedCornerShape(15.dp)
//                            ) {
                    Text(
                        "${it.fio.surname} ${it.fio.name} ${it.fio.praname}",
                        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
                        textAlign = TextAlign.Center
                    )
//                            }


                }
            }
        }
    }
}


@Composable
fun JournalItemCompose(
    subjectName: String,
    groupName: String,
    lessonReportId: Int,
    date: String,
    teacher: String,
    time: String,
    isEnabled: Boolean,
    isActive: Boolean,
    onClick: () -> Unit
) {
    FilledTonalButton(
        modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp).handy(),
        onClick = {
            onClick()
        },
        shape = RoundedCornerShape(30),
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = if (isEnabled && !isActive) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceColorAtElevation(
                2.dp
            ),
            contentColor = if (isEnabled && !isActive) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            Modifier.fillMaxWidth().padding(vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ReportTitle(
                subjectName = subjectName,
                groupName = groupName,
                lessonReportId = lessonReportId,
                date = date,
                teacher = teacher,
                time = time,
                isFullView = true,
                isStartPadding = false,
                onClick = null
            )
            Icon(Icons.Rounded.ArrowForwardIos, null)
        }
    }
}