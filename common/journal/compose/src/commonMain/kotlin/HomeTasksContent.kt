
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.AppBar
import components.CLazyColumn
import components.CustomTextButton
import components.GetAvatar
import components.cClickable
import components.dashedBorder
import components.networkInterface.NetworkState
import dev.chrisbanes.haze.HazeState
import homeTasks.HomeTasksComponent
import homeTasks.HomeTasksStore
import homework.ClientHomeworkItem
import homework.CutedDateTimeGroup
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import view.LocalViewManager
import view.rememberImeState

@OptIn(ExperimentalFoundationApi::class)
@ExperimentalMaterial3Api
@ExperimentalLayoutApi
@Composable
fun HomeTasksContent(
    component: HomeTasksComponent,
    isVisible: Boolean
) {
    val model by component.model.subscribeAsState()
    val nInitModel by component.nInitInterface.networkModel.subscribeAsState()
    val lazyListState = rememberLazyListState()
    val hazeState = remember { HazeState() }

    LaunchedEffect(model.dates.size >= 1) {
        if (model.dates.size >= 1) {
            lazyListState.scrollToItem(index = model.dates.size - 1)
        }
    }

    Scaffold(
        Modifier.fillMaxSize(),
        topBar = {
            AppBar(
                navigationRow = {
                    IconButton(
                        onClick = { component.onOutput(HomeTasksComponent.Output.Back) }
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
                hazeState = hazeState
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize()) {
            Crossfade(nInitModel.state) { state ->
                when (state) {
                    NetworkState.None -> CLazyColumn(padding = padding, state = lazyListState, hazeState = hazeState) {
                        itemsIndexed(items = model.dates) { i, date ->
                            DateTasksItem(
                                date = date,
                                tasks = model.homeTasks.filter { it.date == date },
                                groups = model.groups,
                                subjects = model.subjects,
                                component = component,
                                model = model
                            )
                            if (i == model.dates.size - 1) {
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
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(nInitModel.error)
                            Spacer(Modifier.height(7.dp))
                            CustomTextButton("Попробовать ещё раз") {
                                nInitModel.onFixErrorClick()
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
    model: HomeTasksStore.State,
    component: HomeTasksComponent
) {

    val nModel by component.nInterface.networkModel.subscribeAsState()

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
                        if (isOpened.value) {
                            component.onEvent(HomeTasksStore.Intent.OpenDateItem(date = date))
                        }
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
            AnimatedVisibility(isOpened.value, modifier = Modifier.fillMaxWidth()) {
                Crossfade(model.loadingDate == date) {
                    if (!it) {
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
                    } else {
                        if (nModel.state is NetworkState.Loading) {
                            Box(
                                Modifier.fillMaxWidth().padding(vertical = 5.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(Modifier.size(20.dp))
                            }
                        } else if (nModel.state is NetworkState.Error) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
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
                if (stupsCount != 0) {
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
                groupTime = g.localDateTime?.toInstant(TimeZone.of("UTC+3"))?.toEpochMilliseconds(),
                component = component
            )
        }
    }
}

@Composable
private fun GroupTaskItems(
    groupName: String,
    groupTime: Long?,
    groupTasks: List<ClientHomeworkItem>,
    component: HomeTasksComponent
) {
    val isDone = false !in groupTasks.map { it.done }
    val currentTime = remember { Clock.System.now().toEpochMilliseconds() }
    val remainingTime = (((groupTime ?: 0) - currentTime) / 60000)
    val remainingTimeDays = (remainingTime / 60 /24)
    val remainingTimeHours = (remainingTime - (remainingTimeDays * 24 * 60)) / 60
    val remainingTimeMinutes = remainingTime - (remainingTimeDays * 24 * 60) - (remainingTimeHours * 60)
    val time =
       if(remainingTimeDays > 0) "$remainingTimeDays д $remainingTimeHours ч" else if (remainingTimeHours > 0) "$remainingTimeHours ч $remainingTimeMinutes мин" else if (remainingTimeMinutes > 0) "$remainingTimeMinutes мин" else ""
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
                    if (time != "") {
                        append(" · $time")
                    }
                }
            }, fontWeight = FontWeight.SemiBold,
            textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None,
            maxLines = 1
        )
        Spacer(Modifier.height(5.dp))
        groupTasks.sortedBy { it.id }.forEachIndexed { i, t ->
            TaskItem(task = t, component = component)
            Spacer(Modifier.height(5.dp))
            if (i != groupTasks.size - 1) {
                Spacer(Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun TaskItem(
    task: ClientHomeworkItem,
    component: HomeTasksComponent
) {
    Row(Modifier.cClickable {
        component.onEvent(
            HomeTasksStore.Intent.CheckTask(
                taskId = task.id,
                isCheck = !task.done,
                doneId = task.doneId
            )
        )
    }) {
        CustomCheckBox(
            checked = task.done,
            modifier = Modifier
                .padding(horizontal = 5.dp).padding(end = 2.dp)
                .size(25.dp),
            isDashedBorder = !task.isNec
        )
        Text(
            text = buildAnnotatedString {
                append(task.text)
                if (task.stups != 0) {
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
    isDashedBorder: Boolean,
    modifier: Modifier = Modifier,
) {

    Box(
        modifier
            .alpha(if (checked) 1f else .5f).then(
                Modifier.size(25.dp).border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(if (!isDashedBorder) 1f else 0f),
                    shape = RoundedCornerShape(40)
                ).clip(RoundedCornerShape(40)).then(
                    if (isDashedBorder) Modifier.dashedBorder(
                        3.dp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        cornerRadiusDp = 16.dp
                    ) else Modifier
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(checked,
            enter = fadeIn(),
            exit = fadeOut()) {
            Icon(
                imageVector = Icons.Rounded.Done,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
//    Box(modifier = modifier, contentAlignment = Alignment.Center) {
//
//        OutlinedCard(
//            modifier = Modifier
//                .fillMaxSize()
//                .alpha(if (checked) 1f else .5f),
//            shape = AbsoluteRoundedCornerShape(40),
//            border = BorderStroke(
//                color =  MaterialTheme.colorScheme.onSurfaceVariant,// if (isDashedBorder) Color.Transparent else MaterialTheme.colorScheme.onSurfaceVariant, //if (checked) MaterialTheme.colorScheme.surface else
//                width = 1.dp//if (isDashedBorder) 0.dp else 1.dp
//            ),
//            onClick = {
//
//            }) {
//
//        }
//    }
}