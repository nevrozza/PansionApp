import admin.groups.Subject
import admin.groups.forms.CutedGroup
import allGroupMarks.AllGroupMarksComponent
import allGroupMarks.AllGroupMarksStore
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextDecorationLineStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.AppBar
import components.BorderStup
import components.CLazyColumn
import components.CustomTextButton
import components.GetAvatar
import components.networkInterface.NetworkState
import decomposeComponents.CAlertDialogContent
import homeTasks.HomeTasksComponent
import homeTasks.HomeTasksStore
import homework.ClientHomeworkItem
import homework.CutedDateTimeGroup
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import server.fetchReason
import view.LocalViewManager
import view.rememberImeState

@OptIn(ExperimentalFoundationApi::class)
@ExperimentalMaterial3Api
@ExperimentalLayoutApi
@Composable
fun HomeTasksContent(
    component: HomeTasksComponent
) {
    val model by component.model.subscribeAsState()
    val nModel by component.nInterface.networkModel.subscribeAsState()
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val viewManager = LocalViewManager.current
//    val scrollState = rememberScrollState()
    val imeState = rememberImeState()
    val lazyListState = rememberLazyListState()
    //PullToRefresh
//    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    Scaffold(
        Modifier.fillMaxSize(),
//                .nestedScroll(scrollBehavior.nestedScrollConnection)
        topBar = {
            AppBar(
                navigationRow = {
                    IconButton(
                        onClick = { component.onOutput(HomeTasksComponent.Output.BackToHome) }
                    ) {
                        Icon(
                            Icons.Rounded.ArrowBackIosNew, null
                        )
                    }
                },
                title = {
                    Text(
                        "Задания",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                actionRow = {
                    GetAvatar(
                        avatarId = model.avatarId,
                        name = model.name,
                        size = 35.dp,
                        textSize = 13.sp,
                        modifier = Modifier.padding(end = 10.dp)
                    )
                },
                isHaze = true
            )
            //LessonReportTopBar(component, isFullView) //, scrollBehavior
        }
    ) { padding ->
        Column(Modifier.fillMaxSize()) {
            Crossfade(nModel.state) { state ->
                when (state) {
                    NetworkState.None -> CLazyColumn(padding = padding) {
                        itemsIndexed(items = model.dates.reversed()) { i, date ->
                            DateTasksItem(
                                date = date,
                                tasks = model.homeTasks.filter { it.date == date },
                                groups = model.groups,
                                subjects = model.subjects,
                                component = component
                            )
                            if(i == model.dates.size-1) {
                                Spacer(Modifier.height(100.dp))
                            }
                        }
                    }

                    NetworkState.Loading -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }

                    NetworkState.Error -> {
                        Column(
                            Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(nModel.error)
                            Spacer(Modifier.height(7.dp))
                            CustomTextButton("Попробовать ещё раз") {
                                nModel.onFixErrorClick()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DateTasksItem(
    date: String,
    tasks: List<ClientHomeworkItem>,
    groups: List<CutedDateTimeGroup>,
    subjects: Map<Int, String>,
    component: HomeTasksComponent
) {
    val isCompleted = tasks.count { it.done } == tasks.size
    val isOpened = remember { mutableStateOf(!isCompleted) }
    ElevatedCard(
        Modifier.fillMaxWidth().padding(horizontal = 5.dp).padding(top = 2.dp, bottom = 5.dp)
    ) {
        Column(modifier = Modifier) { //.padding(8.dp)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.height(50.dp).fillMaxWidth().clip(CardDefaults.elevatedShape)
                    .clickable {
                        isOpened.value = !isOpened.value
                    }.padding(horizontal = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        date,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 6.dp)
                    )
                    if (isCompleted) {
                        Icon(
                            Icons.Rounded.Done,
                            null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                AnimatedContent(
                    if (isOpened.value) {
                        Icons.Rounded.ExpandLess
                    } else {
                        Icons.Rounded.ExpandMore
                    }
                ) {
                    Icon(it, null)
                }
            }
            AnimatedVisibility(isOpened.value) {
                Column {
                    subjects.forEach { s ->
                        val subjectTasks = tasks.filter { it.subjectId == s.key }
                        val subjectGroups =
                            groups.filter { it.id in subjectTasks.map { it.groupId } }
                        if (subjectTasks.isNotEmpty()) {
                            SubjectTaskItem(
                                subjectId = s.key,
                                subjectName = s.value,
                                subjectGroups = subjectGroups,
                                subjectTasks = subjectTasks,
                                component = component
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SubjectTaskItem(
    subjectId: Int,
    subjectName: String,
    subjectGroups: List<CutedDateTimeGroup>,
    subjectTasks: List<ClientHomeworkItem>,
    component: HomeTasksComponent
) {
    val isDone = false !in subjectTasks.map { it.done }
    val stupsCount = subjectTasks.sumOf { it.stups }
    Column(Modifier.padding(horizontal = 8.dp).padding(start = 4.dp, bottom = 5.dp)) {
        Text(
            buildAnnotatedString {
                append(subjectName)
                withStyle(
                    SpanStyle(
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = .5f),
                        fontSize = 16.sp
                    )
                ) {
                    append(" · кол-во заданий: ${subjectTasks.size}")
                }
                if(stupsCount != 0) {
                    withStyle(
                        SpanStyle(
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 16.sp
                        )
                    ) {
                        append(" +$stupsCount")
                    }
                }
            }, fontSize = 18.sp, fontWeight = FontWeight.Bold,
            textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None,
            maxLines = 1
        )
        subjectGroups.forEach { g ->
            val groupTasks = subjectTasks.filter { it.groupId == g.id }
            GroupTaskItems(
                groupName = g.name,
                groupTasks = groupTasks,
                groupTime = g.localDateTime.toInstant(TimeZone.of("UTC+3")).toEpochMilliseconds(),
                component = component
            )
        }
    }
}

@Composable
private fun GroupTaskItems(
    groupName: String,
    groupTime: Long,
    groupTasks: List<ClientHomeworkItem>,
    component: HomeTasksComponent
) {
    val isDone = false !in groupTasks.map { it.done }
    val currentTime = remember { Clock.System.now().toEpochMilliseconds() }
    val remainingTime = ((groupTime - currentTime) / 60000)
    val remainingTimeHours = (remainingTime / 60)
    val remainingTimeMinutes = remainingTime - (remainingTimeHours * 60)
    val time =
        if (remainingTimeHours > 0) "$remainingTimeHours ч" else if (remainingTimeMinutes > 0) "$remainingTimeMinutes мин" else ""
    Column(Modifier.padding(start = 5.dp, bottom = 2.dp)) {
        Text(
            buildAnnotatedString {
                append(groupName)
                withStyle(
                    SpanStyle(
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = .5f),
                        fontSize = 13.sp
                    )
                ) {
                    if(time != "") {
                        append(" · $time")
                    }
                }
            }, fontWeight = FontWeight.SemiBold,
            textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None,
            maxLines = 1
        )
        groupTasks.forEachIndexed { i, t ->
            TaskItem(task = t, component = component)
            if(i != groupTasks.size-1) {
                Spacer(Modifier.height(5.dp))
            }
        }
    }
}

@Composable
private fun TaskItem(
    task: ClientHomeworkItem,
    component: HomeTasksComponent
) {
    Row() {
        CustomCheckBox(
            checked = task.done,
            onCheckedChange = {
                component.onEvent(
                    HomeTasksStore.Intent.CheckTask(
                        taskId = task.id,
                        isCheck = !task.done
                    )
                )
            },
            modifier = Modifier
                .padding(horizontal = 5.dp).padding(end = 2.dp)
                .size(25.dp)
        )
        Text(
            text = buildAnnotatedString {
                append(task.text)
                if(task.stups != 0) {
                    withStyle(
                        SpanStyle(
                            color = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        append(" +${task.stups}")
                    }
                }
            },
            textDecoration = if (task.done) TextDecoration.LineThrough else TextDecoration.None,
        )
    }
}

@Composable
private fun CustomCheckBox(
    checked: Boolean,
    onCheckedChange: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        OutlinedCard(
            modifier = Modifier
                .fillMaxSize()
                .alpha(if (checked) 1f else .5f),
            shape = AbsoluteRoundedCornerShape(40),
            border = BorderStroke(
                color = MaterialTheme.colorScheme.onSurfaceVariant, //if (checked) MaterialTheme.colorScheme.surface else
                width = 1.dp
            ),
            onClick = {
                onCheckedChange()
            }) {
            AnimatedVisibility(checked) {
                Icon(
                    imageVector = Icons.Rounded.Done,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}