import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.AlphaTestZatichka
import components.AppBar
import components.CLazyColumn
import components.CustomCheckbox
import components.DefaultErrorView
import components.DefaultErrorViewPos
import components.GetAsyncIcon
import components.TeacherTime
import components.cClickable
import components.listDialog.ListDialogStore
import components.networkInterface.NetworkState
import components.networkInterface.isLoading
import components.refresh.RefreshButton
import components.refresh.RefreshWithoutPullCircle
import components.refresh.keyRefresh
import decomposeComponents.CAlertDialogContent
import decomposeComponents.listDialogComponent.ListDialogDesktopContent
import decomposeComponents.listDialogComponent.ListDialogMobileContent
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import journal.JournalComponent
import journal.JournalStore
import pullRefresh.PullRefreshIndicator
import pullRefresh.pullRefresh
import pullRefresh.rememberPullRefreshState
import resources.RIcons
import server.Moderation
import server.Roles
import view.LocalViewManager
import view.WindowScreen
import view.handy

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

//    val focusManager = LocalFocusManager.current
    val viewManager = LocalViewManager.current
//    val scrollState = rememberScrollState()
//    val imeState = rememberImeState()
//    val lazyListState = rememberLazyListState()

    val isExpanded = viewManager.orientation.value == WindowScreen.Expanded && isNotMinimized

    if (model.creatingReportId != -1) {
        component.createReport()
        component.onEvent(JournalStore.Intent.ResetCreatingId)
    } else if (model.openingReportData != null) {
        component.openReport(model.openingReportData!!)
        component.onEvent(JournalStore.Intent.ResetReportData)
    }
    val refreshing = nModel.isLoading || nORModel.isLoading
    val refreshState = rememberPullRefreshState(
        refreshing,
        { if (isExpanded) onRefresh() else component.onEvent(JournalStore.Intent.Refresh) })

    Scaffold(
        Modifier.fillMaxSize().keyRefresh(refreshState),
        topBar = {
            AppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Журнал",
                            modifier = Modifier.padding(start = 10.dp),
                            fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                            fontWeight = FontWeight.Black,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        RefreshWithoutPullCircle(refreshing, refreshState.position, model.headers.isNotEmpty())
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
                            GetAsyncIcon(
                                RIcons.Add
                            )
                        }
                        ListDialogDesktopContent(
                            component = component.groupListComponent
                        )
                    }
                    RefreshButton(refreshState, viewManager)
                    if (isExpanded) {
                        IconButton(
                            onClick = {
                                component.onOutput(JournalComponent.Output.NavigateToSettings)
                            }
                        ) {
                            GetAsyncIcon(
                                RIcons.Settings
                            )
                        }
                    }

                }
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().pullRefresh(refreshState), contentAlignment = Alignment.Center) {
            val newState = when {
                NetworkState.Error in listOf(nModel.state, nORModel.state) -> "Error"
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
                                modifier = Modifier,
                                isBottomPaddingNeeded = true,
                                refreshState = refreshState
                            ) {
                                item {
                                    Row(
                                        modifier = Modifier.offset(y = (-6).dp).horizontalScroll(
                                            rememberScrollState()
                                        ), verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Spacer(Modifier.width(7.dp))
                                        Box() {
                                            val isPicked = model.filterTeacherLogin != null
                                            ElevatedAssistChip(
                                                onClick = {
                                                    component.fTeachersListComponent.onEvent(
                                                        ListDialogStore.Intent.ShowDialog
                                                    )
                                                },
                                                label = {
                                                    AnimatedContent(
                                                        if (!isPicked) "Учитель" else component.fTeachersListComponent.state.value.list.first { it.id == model.filterTeacherLogin }.text
                                                    ) {
                                                        Text(
                                                            it, maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis
                                                        )
                                                    }
                                                },
                                                colors = AssistChipDefaults.assistChipColors(
                                                    containerColor = MaterialTheme.colorScheme.primary,
                                                    labelColor = MaterialTheme.colorScheme.onPrimary,
                                                    trailingIconContentColor = MaterialTheme.colorScheme.onPrimary
                                                ),
                                                modifier = Modifier.animateContentSize(),
                                                trailingIcon = {
                                                    CloseButton(isShown = isPicked) {
                                                        component.onEvent(
                                                            JournalStore.Intent.FilterTeacher(
                                                                null
                                                            )
                                                        )
                                                    }
                                                }
                                            )
                                            ListDialogDesktopContent(component.fTeachersListComponent)
                                        }
                                        Spacer(Modifier.width(5.dp))
                                        Box() {
                                            val isPicked = model.filterGroupId != null
                                            InputChip(
                                                selected = true,
                                                onClick = {
                                                    component.fGroupListComponent.onEvent(
                                                        ListDialogStore.Intent.ShowDialog
                                                    )
                                                },
                                                label = {
                                                    AnimatedContent(
                                                        if (!isPicked) "Группа" else component.fGroupListComponent.state.value.list.first { it.id == model.filterGroupId.toString() }.text
                                                    ) {
                                                        Text(
                                                            it, maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis
                                                        )
                                                    }
                                                },
                                                modifier = Modifier.animateContentSize(),
                                                trailingIcon = {
                                                    CloseButton(isShown = isPicked) {
                                                        component.onEvent(
                                                            JournalStore.Intent.FilterGroup(
                                                                null
                                                            )
                                                        )
                                                    }
                                                }
                                            )
                                            ListDialogDesktopContent(component.fGroupListComponent)
                                        }
                                        Spacer(Modifier.width(5.dp))
                                        Box() {
                                            val isPicked = model.filterDate != null
                                            AssistChip(
                                                onClick = {
                                                    component.fDateListComponent.onEvent(
                                                        ListDialogStore.Intent.ShowDialog
                                                    )
                                                },
                                                label = {
                                                    AnimatedContent(
                                                        when {
                                                            !isPicked -> "Дата"
                                                            model.filterDate == "0" -> "За неделю"
                                                            model.filterDate == "1" -> "За прошлую неделю"
                                                            else -> model.filterDate.toString()
                                                        }
                                                    ) {
                                                        Text(
                                                            it, maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis
                                                        )
                                                    }
                                                },
                                                modifier = Modifier.animateContentSize(),
                                                trailingIcon = {
                                                    CloseButton(isShown = isPicked) {
                                                        component.onEvent(
                                                            JournalStore.Intent.FilterDate(
                                                                null
                                                            )
                                                        )
                                                    }
                                                }
                                            )
                                            ListDialogDesktopContent(component.fDateListComponent)
                                        }
                                        Spacer(Modifier.width(15.dp))
                                        if (model.isMentor) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.cClickable {
                                                    component.onEvent(
                                                        JournalStore.Intent.FilterMyChildren(
                                                            !model.filterMyChildren
                                                        )
                                                    )
                                                }) {
                                                CustomCheckbox(
                                                    checked = model.filterMyChildren
                                                )
                                                Text("Только мои классы")
                                            }
                                            Spacer(Modifier.width(15.dp))
                                        }
                                    }
                                }
                                val headers = model.headers
                                    .asSequence()
                                    .filter { if (model.filterStatus != null) it.status == model.filterStatus else true }
                                    .filter {
                                        if (model.filterDate == "0") it.date in model.weekDays
                                        else if (model.filterDate == "1") it.date in model.previousWeekDays
                                        else if (model.filterDate != null) it.date == model.filterDate else true
                                    }
                                    .filter { if (model.filterGroupId != null) it.groupId == model.filterGroupId else true }
                                    .filter { if (model.filterTeacherLogin != null) it.teacherLogin == model.filterTeacherLogin else true }
                                    .filter { if (model.filterMyChildren && model.childrenGroupIds.isNotEmpty()) it.groupId in model.childrenGroupIds else true }
                                    .sortedBy { it.reportId }
                                    .toList().reversed()
                                itemsIndexed(
                                    headers,
                                    key = { i, item -> item.reportId }) { i, item ->
                                    if (i == headers.indexOfFirst { it.date == item.date }) {
                                        if (i != 0) {
                                            Spacer(Modifier.height(5.dp))
                                        }
                                        Text(
                                            item.date,
                                            fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                            fontWeight = FontWeight.Black,
                                            modifier = Modifier.padding(start = 10.dp)
                                        )
                                        Spacer(Modifier.height(5.dp))
                                    }
                                    JournalItemCompose(
                                        subjectName = item.subjectName,
                                        groupName = item.groupName,
//                                        lessonReportId = item.reportId,
//                                        date = item.date,
                                        teacher = item.teacherName,
                                        time = item.time,
                                        isEnabled = true,
                                        isActive = true,
                                        isEnded = item.status,
                                        theme = item.theme,
                                        module = item.module
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
                            val errorModel = if (nModel.state == NetworkState.Error) nModel else nORModel
                            DefaultErrorView(
                                errorModel,
                                DefaultErrorViewPos.CenteredFull
                            )
                        }
                    }
                }
            }

            PullRefreshIndicator(
                state = refreshState,
                topPadding = padding.calculateTopPadding()
            )
            ListDialogMobileContent(component.groupListComponent)
            ListDialogMobileContent(component.fGroupListComponent)
            ListDialogMobileContent(component.fStatusListComponent)
            ListDialogMobileContent(component.fDateListComponent)
            ListDialogMobileContent(component.fTeachersListComponent)
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
                items(model.studentsInGroup.sortedBy { it.isDeleted }) {
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
                        "${it.p.fio.surname} ${it.p.fio.name} ${it.p.fio.praname}",
                        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp)
                            .alpha(if (it.isDeleted) .5f else 1f),
                        textAlign = TextAlign.Center,
                        textDecoration = if (it.isDeleted) TextDecoration.LineThrough else TextDecoration.None
                    )
//                            }


                }
            }
        }
    }
}

@Composable
private fun CloseButton(isShown: Boolean, onClick: () -> Unit) {
    AnimatedVisibility(isShown) {
        IconButton(
            onClick = { onClick() },
            modifier = Modifier.size(20.dp),
        ) {
            GetAsyncIcon(
                RIcons.Close
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalItemCompose(
    subjectName: String,
    groupName: String,
//    lessonReportId: Int,
//    date: String,
    teacher: String,
    time: String,
    isEnabled: Boolean,
    isActive: Boolean,
    isEnded: Boolean,
    theme: String,
    module: String,
    onClick: () -> Unit
) {
//    val tState = rememberTooltipState(isPersistent = false)
//    TooltipBox(
//        state = tState,
//        tooltip = {
//            if (theme != "") {
//                PlainTooltip(modifier = Modifier.clickable {}) {
//                    Text(
//                        theme,
//                        textAlign = TextAlign.Center
//                    )
//                }
//            }
//        },
//        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider()
//    ) {
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
        Column(Modifier.padding(horizontal = 2.dp).padding(top = 5.dp, bottom = 2.5.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val bigTextSize = MaterialTheme.typography.titleLarge.fontSize// if (!isLarge) else 40.sp
                val smallTextSize = MaterialTheme.typography.titleSmall.fontSize//if (!isLarge)  else 28.sp
                val startPadding = 0.dp//if (!isLarge)  else 5.dp
//                val isFullView = true
                Box() {
                    if (isEnded) {
                        Box(
                            Modifier.offset(x = (-8).dp, y = (-10).dp)
                                .align(Alignment.CenterStart).size(5.dp).clip(
                                    CircleShape
                                ).background(MaterialTheme.colorScheme.primary)
                        )
                    }
                    Text(
                        text = module,
                        modifier = Modifier.offset(x = (-8).dp).align(Alignment.CenterStart)
                    )
                    Box(
                        Modifier.padding(start = startPadding).clip(RoundedCornerShape(15.dp))
                    ) {
                        Column(
                            Modifier.padding(horizontal = 3.dp)
                        ) {
                            Row {
                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(
                                            SpanStyle(
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        ) {
                                            append(subjectName)
                                        }
                                        withStyle(
                                            SpanStyle(
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = smallTextSize,
                                                color = MaterialTheme.colorScheme.onSurface.copy(
                                                    alpha = .5f
                                                )
                                            )
                                        ) {
                                            append(" $groupName")
                                        }
                                    },
                                    overflow = TextOverflow.Ellipsis,
                                    fontSize = bigTextSize,
                                    maxLines = 1,
                                    style = LocalTextStyle.current.copy(
                                        lineHeightStyle = LineHeightStyle(
                                            alignment = LineHeightStyle.Alignment.Bottom,
                                            trim = LineHeightStyle.Trim.LastLineBottom
                                        )
                                    )
                                )
                            }
                            TeacherTime(
                                teacher,
                                time,
                                true,
                                separator = "  ",
                                yTextOffset = 1.dp
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(2.5.dp))
            Text(
                text = theme.ifBlank { "Тема не выставлена" },
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
//        }
    }
}