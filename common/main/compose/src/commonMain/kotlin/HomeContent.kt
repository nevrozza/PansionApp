@file:OptIn(ExperimentalMaterial3Api::class)

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.progressSemantics
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.PlaylistAddCheckCircle
import androidx.compose.material.icons.rounded.ArrowForwardIos
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.AppBar
import components.CLazyColumn
import components.CustomTextButton
import components.DateButton
import components.LoadingAnimation
import components.cAlertDialog.CAlertDialogStore
import components.networkInterface.NetworkInterface
import components.networkInterface.NetworkState
import decomposeComponents.CAlertDialogContent
import dev.chrisbanes.haze.hazeChild
import home.HomeComponent
import home.HomeStore
import journal.JournalStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import pullRefresh.PullRefreshIndicator
import pullRefresh.pullRefresh
import pullRefresh.rememberPullRefreshState
import report.Grade
import server.Roles
import server.fetchReason
import server.getCurrentDayTime
import server.roundTo
import server.toMinutes
import server.weekPairs
import view.LocalViewManager
import view.WindowScreen
import view.handy
import view.rememberImeState

@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalLayoutApi
@Composable
fun HomeContent(
    component: HomeComponent
) {
    val model by component.model.subscribeAsState()
    if (model.role == Roles.student) {
        StudentHomeContent(component)
    } else if (model.role == Roles.teacher) {
        TeacherHomeContent(component)
    }
}

@Composable
fun TeacherHomeContent(
    component: HomeComponent
) {
    if (component.journalComponent != null) {
        val model by component.model.subscribeAsState()
        val journalModel by component.journalComponent!!.model.subscribeAsState()
        val nJournalOpenReportModel by component.journalComponent!!.nOpenReportInterface.networkModel.subscribeAsState()
        val nTeacherModel by component.teacherNInterface.networkModel.subscribeAsState()
        val nScheduleModel by component.scheduleNInterface.networkModel.subscribeAsState()

        val focusManager = LocalFocusManager.current
        val viewManager = LocalViewManager.current
//    val scrollState = rememberScrollState()
        val imeState = rememberImeState()
        val lazyListState = rememberLazyListState()
        val coroutineScope = rememberCoroutineScope()

        val isMainView = lazyListState.firstVisibleItemIndex in listOf(0, 1)

        val refreshing =
            (nTeacherModel.state == NetworkState.Loading || nScheduleModel.state == NetworkState.Loading)
        val refreshState = rememberPullRefreshState(
            refreshing,
            { component.onEvent(HomeStore.Intent.Init) }
        )

        if (component.journalComponent != null) {
            if (viewManager.orientation.value != WindowScreen.Expanded) {
                if (journalModel.creatingReportId != -1) {
                    component.journalComponent!!.createReport()
                    component.journalComponent!!.onEvent(JournalStore.Intent.ResetCreatingId)
                } else if (journalModel.openingReportData != null) {
                    component.journalComponent!!.openReport(journalModel.openingReportData!!)
                    component.reportsDialog.onEvent(CAlertDialogStore.Intent.HideDialog)
                    component.journalComponent!!.onEvent(JournalStore.Intent.ResetReportData)
                }
            }

        }

        Scaffold(
            Modifier.fillMaxSize()
                .onKeyEvent {
                    if (it.key == Key.F5 && it.type == KeyEventType.KeyDown) {
                        component.onEvent(HomeStore.Intent.Init)
                    }
                    false
                },
            topBar = {
                val isHaze = viewManager.hazeStyle != null
                Column(
                    Modifier.then(
                        if (isHaze) Modifier.hazeChild(
                            state = viewManager.hazeState,
                            style = viewManager.hazeStyle!!.value
                        )
                        else Modifier
                    )
                ) {
                    AppBar(
                        containerColor = if (isHaze) Color.Transparent else MaterialTheme.colorScheme.surface,
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                println(lazyListState.firstVisibleItemIndex)
                                AnimatedContent(
                                    targetState = if (lazyListState.firstVisibleItemIndex !in listOf(
                                            0,
                                            1
                                        )
                                    ) "Расписание" else "Главная"
                                ) {
                                    Text(
                                        it,
                                        modifier = Modifier.padding(start = 10.dp),
                                        fontSize = 25.sp,
                                        fontWeight = FontWeight.Black,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                AnimatedContent(
                                    if (isMainView) ""
                                    else "${
                                        model.currentDate.second.substring(
                                            0,
                                            5
                                        )
                                    }, ${weekPairs[model.currentDate.first]}"
                                ) {
                                    Text(
                                        it,
                                        modifier = Modifier.padding(start = 7.dp).offset(y = 2.dp),
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                        fontWeight = FontWeight.Bold,
                                        lineHeight = 10.sp,
                                        maxLines = 2, overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        },
                        actionRow = {
                            AnimatedVisibility(
                                !isMainView
                            ) {
                                CalendarButton(component)
                            }
                            if (viewManager.orientation.value != WindowScreen.Expanded) {
                                IconButton(
                                    onClick = { component.onEvent(HomeStore.Intent.Init) }
                                ) {
                                    Icon(
                                        Icons.Filled.Refresh, null
                                    )
                                }


                                IconButton(
                                    onClick = {
                                        component.onOutput(HomeComponent.Output.NavigateToSettings)
                                    }
                                ) {
                                    Icon(
                                        Icons.Rounded.Settings, null
                                    )
                                }
                            }

                        }
                    )
                    AnimatedVisibility(model.isDatesShown && !isMainView) {
                        DatesLine(
                            component = component,
                            model = model
                        )
                    }
                }
            }
        ) { padding ->
            Box(Modifier.fillMaxSize()) {
                CLazyColumn(
                    modifier = Modifier
                        .pullRefresh(refreshState),
                    state = lazyListState,
                    padding = padding,
                    isBottomPaddingNeeded = true
                ) {
                    items(3) { num ->
                        if (num == 0) {
                            Text(
                                "Успеваемость учеников",
                                modifier = Modifier.fillMaxWidth(),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(Modifier.height(10.dp))
                            Crossfade(
                                nTeacherModel.state,
                                modifier = Modifier.animateContentSize()
                            ) { state -> //, modifier = Modifier.padding(top = 10.dp)
                                when (state) {
                                    NetworkState.None -> {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            model.teacherGroups.sortedBy { it.subjectId }.forEach {
                                                FilledTonalButton(
                                                    modifier = Modifier.fillMaxWidth()
                                                        .padding(bottom = 10.dp)
                                                        .padding(horizontal = 50.dp)
                                                        .handy(),
                                                    onClick = {
                                                        component.onOutput(
                                                            HomeComponent.Output.NavigateToAllGroupMarks(
                                                                subjectId = it.subjectId,
                                                                subjectName = it.subjectName,
                                                                groupId = it.cutedGroup.groupId,
                                                                groupName = it.cutedGroup.groupName
                                                            )
                                                        )
                                                    },
                                                    shape = RoundedCornerShape(30),
                                                    colors = ButtonDefaults.filledTonalButtonColors(
                                                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation( //if (isEnabled && !isActive) MaterialTheme.colorScheme.secondaryContainer else
                                                            2.dp
                                                        ),
                                                        contentColor = MaterialTheme.colorScheme.onSurface //if (isEnabled && !isActive) MaterialTheme.colorScheme.onSecondaryContainer else
                                                    )
                                                ) {
                                                    Row(
                                                        Modifier.fillMaxWidth()
                                                            .padding(vertical = 5.dp),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.SpaceBetween
                                                    ) {
                                                        Text(
                                                            buildAnnotatedString {
                                                                withStyle(
                                                                    SpanStyle(
                                                                        fontWeight = FontWeight.Bold
                                                                    )
                                                                ) {
                                                                    append(it.subjectName)
                                                                }
                                                                append(" ")
                                                                append(it.cutedGroup.groupName)
                                                            }
                                                        )
                                                        Icon(Icons.Rounded.ArrowForwardIos, null)
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    NetworkState.Loading -> {
                                        Box(
                                            Modifier.height(100.dp).fillMaxWidth(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            LoadingAnimation()
                                        }
                                    }

                                    NetworkState.Error -> {
                                        Column(
                                            Modifier.fillMaxWidth(),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(nTeacherModel.error)
                                            Spacer(Modifier.height(7.dp))
                                            CustomTextButton("Попробовать ещё раз") {
                                                nTeacherModel.onFixErrorClick()
                                            }
                                        }
                                    }
                                }
                            }
                        } else if (num == 1) {
                            RaspisanieTitleBox(
                                isMainView = isMainView,
                                model = model,
                                component = component
                            )
                        } else {
                            RaspisanieTable(
                                model = model,
                                nScheduleModel = nScheduleModel,
                                component = component,
                                journalModel = journalModel
                            )
                        }
                    }

                }
                PullRefreshIndicator(
                    modifier = Modifier.align(alignment = Alignment.TopCenter),
                    refreshing = refreshing,
                    state = refreshState,
                )
                if (viewManager.orientation.value != WindowScreen.Expanded)
                    StudentsPreviewDialog(
                        component.journalComponent!!, journalModel
                    )
                AnimatedVisibility(
                    nJournalOpenReportModel.state != NetworkState.None, modifier = Modifier.align(Alignment.Center),
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut()
                ) {
                    Crossfade(nJournalOpenReportModel.state) {
                        Surface(
                            color = MaterialTheme.colorScheme.background,
                            tonalElevation = 5.dp,
                            shadowElevation = 10.dp,
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            when(it) {
                                NetworkState.Error -> Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Не удалось загрузить отчёт")
                                    CustomTextButton("Закрыть") {
                                        component.journalComponent!!.nOpenReportInterface.goToNone()
                                    }
                                }

                                else -> CircularProgressIndicator(Modifier.padding(10.dp))
                            }
                        }
                    }
                }
                CAlertDialogContent(
                    component = component.reportsDialog,
                    title = "Отчёты"
                ) {
                    Crossfade(nJournalOpenReportModel.state) {
                        when(it) {
                            NetworkState.None -> Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                                model.someHeaders.sortedBy { it.reportId }.forEach { item ->
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
                                        component.journalComponent!!.onEvent(
                                            JournalStore.Intent.FetchReportData(
                                                item
                                            )
                                        )
                                    }
                                }
                            }
                            NetworkState.Loading -> CircularProgressIndicator()
                            NetworkState.Error -> Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Не удалось загрузить отчёт")
                                CustomTextButton("Выбрать ещё раз") {
                                    component.journalComponent!!.nOpenReportInterface.goToNone()
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
private fun RaspisanieTable(
    model: HomeStore.State,
    journalModel: JournalStore.State?,
    nScheduleModel: NetworkInterface.NetworkModel,
    component: HomeComponent
) {
    val items = model.items[model.currentDate.second]
    Crossfade(nScheduleModel.state) { st ->

        Column {
            when (st) {
                NetworkState.None -> {
                    if (items.isNullOrEmpty()) {
                        Text("Здесь пока ничего нет")
                    } else {
                        items.sortedBy { it.start }
                            .forEachIndexed { id, it ->
                                Lesson(
                                    num = id + 1,
                                    title = it.subjectName,
                                    group = it.groupName,
                                    start = it.start,
                                    end = it.end,
                                    cabinet = it.cabinet.toString(),
                                    fio = it.teacherFio,
                                    date = model.currentDate.second,
                                    today = model.today,
                                    role = model.role,
                                    component = component,
                                    groupId = it.groupId,
                                    model = model,
                                    journalModel = journalModel
                                )
                                Spacer(Modifier.padding(10.dp))
                            }
                    }

                }

                NetworkState.Loading -> CircularProgressIndicator()
                NetworkState.Error -> {
                    Text(nScheduleModel.error)
                    Spacer(Modifier.height(7.dp))
                    CustomTextButton("Попробовать ещё раз") {
                        nScheduleModel.onFixErrorClick()
                    }
                }

                else -> {}
            }

        }
    }
}

@Composable
fun StudentHomeContent(
    component: HomeComponent
) {
    val model by component.model.subscribeAsState()
    val nQuickTabModel by component.quickTabNInterface.networkModel.subscribeAsState()
    val nGradesModel by component.gradesNInterface.networkModel.subscribeAsState()
    val nScheduleModel by component.scheduleNInterface.networkModel.subscribeAsState()

    val focusManager = LocalFocusManager.current
    val viewManager = LocalViewManager.current
//    val scrollState = rememberScrollState()
    val imeState = rememberImeState()
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val isMainView = lazyListState.firstVisibleItemIndex in listOf(0, 1)

    val refreshing =
        (nQuickTabModel.state == NetworkState.Loading || nGradesModel.state == NetworkState.Loading
                || nScheduleModel.state == NetworkState.Loading)


    val refreshState = rememberPullRefreshState(
        refreshing,
        { component.onEvent(HomeStore.Intent.Init) }
    )

    Scaffold(
        Modifier.fillMaxSize()
            .onKeyEvent {
                if (it.key == Key.F5 && it.type == KeyEventType.KeyDown) {
                    component.onEvent(HomeStore.Intent.Init)
                }
                false
            },
        topBar = {
            val isHaze = viewManager.hazeStyle != null
            Column(
                Modifier.then(
                    if (isHaze) Modifier.hazeChild(
                        state = viewManager.hazeState,
                        style = viewManager.hazeStyle!!.value
                    )
                    else Modifier
                )
            ) {
                AppBar(
                    containerColor = if (isHaze) Color.Transparent else MaterialTheme.colorScheme.surface,
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            println(lazyListState.firstVisibleItemIndex)
                            AnimatedContent(
                                targetState = if (lazyListState.firstVisibleItemIndex !in listOf(
                                        0,
                                        1
                                    )
                                ) "Расписание" else "Главная"
                            ) {
                                Text(
                                    it,
                                    modifier = Modifier.padding(start = 10.dp),
                                    fontSize = 25.sp,
                                    fontWeight = FontWeight.Black,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            AnimatedContent(
                                if (isMainView && model.name == "Мария" && model.surname == "Губская" && model.praname == "Дмитриевна") "Всё получится!!!"
                                else if (isMainView) ""
                                else "${
                                    model.currentDate.second.substring(
                                        0,
                                        5
                                    )
                                }, ${weekPairs[model.currentDate.first]}"
                            ) {
                                Text(
                                    it,
                                    modifier = Modifier.padding(start = 7.dp)
                                        .offset(y = 2.dp),
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = 10.sp,
                                    maxLines = 2, overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    },
                    actionRow = {
                        AnimatedVisibility(
                            !isMainView
                        ) {
                            CalendarButton(component)
                        }
                        IconButton(
                            onClick = { component.onEvent(HomeStore.Intent.Init) }
                        ) {
                            Icon(
                                Icons.Filled.Refresh, null
                            )
                        }
                        if (viewManager.orientation.value != WindowScreen.Expanded) {
                            IconButton(
                                onClick = {
                                    component.onOutput(HomeComponent.Output.NavigateToSettings)
                                }
                            ) {
                                Icon(
                                    Icons.Rounded.Settings, null
                                )
                            }
                        }

                    }
                )
                AnimatedVisibility(model.isDatesShown && !isMainView) {
                    DatesLine(
                        component = component,
                        model = model
                    )
                }
            }
        }
    ) { padding ->
        Box(Modifier.fillMaxSize()) {
            CLazyColumn(
                modifier = Modifier
                    .pullRefresh(refreshState),
                state = lazyListState,
                padding = padding,
                isBottomPaddingNeeded = true
            ) {
                items(3) { num ->
                    if (num == 0) {
                        ElevatedCard(Modifier.fillMaxWidth()) {
                            Row(
                                Modifier.padding(10.dp)
                            ) {
                                GetAvatar(
                                    avatarId = model.avatarId,
                                    name = model.name
                                )
                                Spacer(Modifier.width(15.dp))
                                Column {
                                    Row(
                                        Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            model.name,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                        )

                                        CustomTextButton(
                                            text = when (model.period) {
                                                HomeStore.Period.WEEK -> "неделя"
                                                HomeStore.Period.MODULE -> "модуль"
                                                HomeStore.Period.HALF_YEAR -> "полугодие"
                                                HomeStore.Period.YEAR -> "год"
                                            },
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.secondary
                                        ) {

                                        }
                                    }
                                    Crossfade(nQuickTabModel.state) {
                                        Column {
                                            when (it) {
                                                NetworkState.Error -> {
                                                    Text("Ошибка")
                                                    CustomTextButton("Попробовать ещё раз") {
                                                        nQuickTabModel.onFixErrorClick()
                                                    }
                                                }

                                                else -> {
                                                    QuickTabItem(
                                                        "Средний балл",
                                                        value = model.averageGradePoint[model.period]
                                                    ) {

                                                    }
                                                    QuickTabItem(
                                                        "Ступени",
                                                        value =
                                                        if (model.ladderOfSuccess[model.period] != null) {
                                                            model.ladderOfSuccess[model.period]!!.first.toFloat()
                                                        } else {
                                                            null
                                                        },
                                                        dsValue = if (model.ladderOfSuccess[model.period] != null) {
                                                            model.ladderOfSuccess[model.period]!!.second
                                                        } else {
                                                            0
                                                        },
                                                    ) {
                                                        component.onOutput(
                                                            HomeComponent.Output.NavigateToDetailedStups(
                                                                model.login,
                                                                model.period.ordinal
                                                            )
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }

                                }
                            }
                        }
                        Spacer(Modifier.height(15.dp))
                        Row(Modifier.fillMaxWidth()) {
                            ElevatedCard(
                                Modifier.fillMaxWidth().clip(CardDefaults.elevatedShape)
                                    .weight(1f)
                                    .handy()
                                    .clickable() {
                                        component.onOutput(
                                            HomeComponent.Output.NavigateToDnevnikRuMarks(
                                                model.login
                                            )
                                        )
                                    }
                            ) {
                                Column(
                                    Modifier.padding(vertical = 10.dp, horizontal = 15.dp)
                                        .fillMaxWidth().defaultMinSize(minHeight = 80.dp),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        "Оценки",
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(Modifier.height(5.dp))
                                    Box(
                                        Modifier.fillMaxWidth()
                                            .padding(end = 5.dp, bottom = 5.dp),
                                        contentAlignment = Alignment.CenterEnd
                                    ) {
                                        Icon(
                                            Icons.Outlined.PlaylistAddCheckCircle,
                                            null,
                                            tint = MaterialTheme.colorScheme.secondary
                                        )
                                    }
                                }
                            }
                            Spacer(Modifier.width(15.dp))
                            ElevatedCard(
                                Modifier.fillMaxWidth().clip(CardDefaults.elevatedShape)
                                    .weight(1f)
                                    .clickable() {

                                    }
                            ) {
                                Column(
                                    Modifier.padding(vertical = 10.dp, horizontal = 15.dp)
                                        .fillMaxWidth().defaultMinSize(minHeight = 80.dp),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        "Домашние задания",
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(Modifier.height(5.dp))

                                    if (model.homeWorkEmoji != null) {
                                        Text(
                                            model.homeWorkEmoji.toString(),
                                            Modifier.fillMaxWidth()
                                                .padding(end = 5.dp, bottom = 5.dp),
                                            fontSize = 20.sp,
                                            textAlign = TextAlign.End
                                        )
                                    } else {
                                        Box(
                                            Modifier.fillMaxWidth()
                                                .padding(end = 5.dp, bottom = 5.dp),
                                            contentAlignment = Alignment.BottomEnd
                                        ) {
                                            LoadingAnimation(
                                                circleColor = MaterialTheme.colorScheme.onSurface,
                                                circleSize = 8.dp,
                                                spaceBetween = 5.dp,
                                                travelDistance = 3.5.dp
                                            )
//                                        DotsFlashing(Modifier)
                                        }
                                    }


                                }
                            }
                        }
                        Spacer(Modifier.height(15.dp))
                        Crossfade(nGradesModel.state, modifier = Modifier.animateContentSize()) {
                            Box(
                                Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                when (it) {
                                    NetworkState.None -> LazyRow(Modifier.fillMaxWidth()) {
                                        items(model.grades.sortedBy { it.date }
                                            .reversed()) {
                                            cGrade(it, coroutineScope)
                                        }
                                    }

                                    NetworkState.Loading -> {
                                        Box(
                                            Modifier.height(60.dp).offset(y = 5.dp).fillMaxWidth(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            LoadingAnimation()
                                        }
                                    }

                                    NetworkState.Error -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(nGradesModel.error)
                                        Spacer(Modifier.height(7.dp))
                                        CustomTextButton("Попробовать ещё раз") {
                                            nGradesModel.onFixErrorClick()
                                        }
                                    }
                                }
                            }
                        }

                    } else if (num == 1) {
                        RaspisanieTitleBox(
                            isMainView = isMainView,
                            model = model,
                            component = component
                        )
                    } else {
                        RaspisanieTable(
                            model = model,
                            nScheduleModel = nScheduleModel,
                            component = component,
                            journalModel = null
                        )
                    }
                }
            }

            PullRefreshIndicator(
                modifier = Modifier.align(alignment = Alignment.TopCenter),
                refreshing = refreshing,
                state = refreshState,
            )
        }

    }
}

@Composable
private fun RaspisanieTitleBox(
    isMainView: Boolean,
    model: HomeStore.State,
    component: HomeComponent
) {
    Box() {
        Column(Modifier.alpha(0f)) {
            Text(
                "Расписание",
                modifier = Modifier.padding(top = 8.dp, bottom = 5.dp),
                fontSize = 25.sp,
                fontWeight = FontWeight.Black
            )
            if (!isMainView) {
                AnimatedVisibility(model.isDatesShown) {
                    DatesLine(
                        component = component,
                        model = model
                    )
                }
            }
        }
        AnimatedVisibility(
            visible = isMainView,
            enter = slideInVertically(initialOffsetY = { -it / 2 }),
            exit = slideOutVertically(targetOffsetY = { -it / 2 }),
        ) {
            Column {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            "Расписание",
                            modifier = Modifier.padding(
                                top = 8.dp,
                                bottom = 5.dp
                            ),
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Black
                        )
                        AnimatedContent(
                            "${
                                model.currentDate.second.substring(
                                    0,
                                    5
                                )
                            }, ${weekPairs[model.currentDate.first]}"
                        ) {
                            Text(
                                text = it,
                                modifier = Modifier.padding(start = 7.dp)
                                    .offset(y = 4.dp),
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = 0.6f
                                ),
                                fontWeight = FontWeight.Bold,
                                lineHeight = 10.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    CalendarButton(component)
                }

                AnimatedVisibility(model.isDatesShown) {
                    DatesLine(
                        component = component,
                        model = model
                    )
                }
            }
        }
    }
}

@Composable
fun Lesson(
    num: Int,
    title: String,
    group: String,
    groupId: Int,
    start: String,
    end: String,
    cabinet: String,
    fio: FIO,
    date: String,
    today: String,
    role: String,
    component: HomeComponent,
    model: HomeStore.State,
    journalModel: JournalStore.State?
) {
    val todayParts = today.substring(0, 5).split(".")
    val dateParts = date.substring(0, 5).split(".")
    val currentTime = getCurrentDayTime().toMinutes()
    val isEnded = (currentTime >= end.toMinutes() && today == date) ||
            (todayParts[0].toInt() > dateParts[0].toInt() && todayParts[1].toInt() == dateParts[1].toInt()) ||
            (todayParts[1].toInt() > dateParts[1].toInt())
    val minutesOst = start.toMinutes() - currentTime
    Row(Modifier.padding(end = 10.dp)) {
        Box(Modifier.width(25.dp).height(50.dp), contentAlignment = Alignment.CenterStart) {
            Text(
                num.toString(),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (!isEnded) 1f else 0.5f),
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Text(buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (!isEnded) 1f else 0.5f)
                        )
                    ) {
                        append(title.capitalize())
                    }
                    withStyle(
                        SpanStyle(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (!isEnded) .6f else 0.3f)
                        )
                    ) {
                        append(" $cabinet")
                    }

                    withStyle(
                        SpanStyle(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 17.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (!isEnded) 1f else 0.5f)
                        )
                    ) {
                        append("\n")
                        append(group.capitalize())
                    }
                    withStyle(
                        SpanStyle(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 17.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (!isEnded) .6f else 0.3f)
                        )
                    ) {
                        append(
                            " ${fio.surname}"
                        )
                    }
                    withStyle(
                        SpanStyle(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (!isEnded) .6f else 0.3f)
                        )
                    ) {
                        append("\n")
                        append("$start-$end")
                    }

                }, lineHeight = 17.sp)
            }

            if (role == Roles.student) {
                if (
                    isEnded || today == date
                ) {
                    val notNow = minutesOst > 0 && !isEnded
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (notNow) {
                            Icon(Icons.Rounded.Schedule, null, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(5.dp))
                        }
                        Text(
                            if (notNow) "$minutesOst мин."
                            else if (!isEnded) "Начался"
                            else "Нет оценок",
                            lineHeight = 5.sp
                        )
                    }
                }
            } else if (journalModel != null) {
                if (isEnded || today == date) {
                    val headers = journalModel.headers.filter {
                        it.date == model.currentDate.second &&
                                it.groupId == groupId &&
                                (it.time.toMinutes() >= start.toMinutes() && it.time.toMinutes() < end.toMinutes())
                    }
                    val isCreated = headers.isNotEmpty()
                    FilledTonalButton(
                        onClick = {
                            with(component.journalComponent!!) {
                                if (isCreated) {
                                    if (headers.size == 1) {
                                        onEvent(JournalStore.Intent.FetchReportData(headers.first()))
                                    } else {
                                        component.onEvent(HomeStore.Intent.UpdateSomeHeaders(headers))
                                        component.reportsDialog.onEvent(CAlertDialogStore.Intent.ShowDialog)
                                    }
                                } else if (!isEnded) {
                                    onEvent(
                                        JournalStore.Intent.OnGroupClicked(
                                            groupId,
                                            start
                                        )
                                    )
                                }
                            }
                        },
                        enabled = !isEnded || isCreated,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
                    ) {
                        Text(
                            if (isCreated) "Посмотреть отчёт" else if (isEnded) "Отчёт не найден" else "Создать отчёт",
                            lineHeight = 10.sp
                        )
                    }
                }
            }
//            else if (mark != "0") {
//                ElevatedCard(
//                    Modifier.size(45.dp)
//                ) {
//                    Box(
//                        Modifier.fillMaxSize(),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Text("5", fontWeight = FontWeight.Black, fontSize = 20.sp, color = Color(185, 254, 179))
//                    }
//                }
//            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun cGrade(mark: Grade, coroutineScope: CoroutineScope) {
    val tState = rememberTooltipState(isPersistent = false)
    TooltipBox(
        state = tState,
        tooltip = {
            PlainTooltip(modifier = Modifier.clickable {}) {
                println(mark.reason)
                Text(
                    "${mark.date}\n${fetchReason(mark.reason)}",
                    textAlign = TextAlign.Center
                )
            }
        },
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider()
    ) {
        RecentMarkContent(
            mark.content,
            cutedReason = mark.reason.subSequence(0, 3).toString(),
            subjectName = mark.subjectName,
//            size = markSize,
//            textYOffset = yOffset,
            addModifier = Modifier.clickable {
                coroutineScope.launch {
                    tState.show()
                }
            }.handy()
                .pointerInput(PointerEventType.Press) {
                    println("asd")
                }
        )
    }
}

@Composable
fun CalendarButton(component: HomeComponent) {
    IconButton(
        onClick = { component.onEvent(HomeStore.Intent.ChangeIsDatesShown) }
    ) {
        Icon(
            Icons.Rounded.CalendarToday, null
        )
    }
}

@Composable
fun DatesLine(component: HomeComponent, model: HomeStore.State) {
    Column {
        Row(
            Modifier.height(50.dp).fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            model.dates.toList().forEach { item ->
                DateButton(
                    currentDate = model.currentDate.second,
                    dayOfWeek = item.first,
                    date = item.second
                ) {
                    component.onEvent(HomeStore.Intent.ChangeDate(item))
                    //component.onEvent(ScheduleStore.Intent.ChangeCurrentDate(item))
                }

            }
        }
        Spacer(Modifier.height(5.dp))
    }
}

@Composable
fun RecentMarkContent(
    mark: String,
    cutedReason: String,
    subjectName: String,
    addModifier: Modifier = Modifier,
//    offset: DpOffset = DpOffset(0.dp, 0.dp),
//    paddingValues: PaddingValues = ,
////    size: Dp = 25.dp,
//    textYOffset: Dp = 0.dp
) {
    Box(
        Modifier.padding(PaddingValues(start = 5.dp, top = 5.dp))
            .border(
                width = if (cutedReason !in listOf("!ds", "!st")) 0.dp else 1.dp,
                color = if (cutedReason !in listOf(
                        "!ds",
                        "!st"
                    )
                ) Color.Transparent else MaterialTheme.colorScheme.outline.copy(1f),
                shape = RoundedCornerShape(30)
            )
            .clip(RoundedCornerShape(percent = 30))
            .background(
                if (cutedReason !in listOf(
                        "!ds",
                        "!st"
                    )
                ) MaterialTheme.colorScheme.primary.copy(
                    alpha = .2f
                ) else MaterialTheme.colorScheme.surface
            )
            .then(addModifier),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(5.dp).padding(horizontal = 2.dp).offset(y = -2.dp)
        ) {
            Text(
                (if (cutedReason in listOf(
                        "!ds",
                        "!st"
                    ) && mark.toInt() > 0
                ) "+" else "") + mark,
                fontSize = 18.sp,
                modifier = Modifier.fillMaxWidth().offset(y = 4.dp),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Black
            )
            Text(
                subjectName
            )
        }
    }
}

@Composable
fun LoadingCircleAnimation(
    size: Dp = 32.dp,
    sweepAngle: Float = 90f,
    color: Color = MaterialTheme.colorScheme.primary,
    strokeWidth: Dp = ProgressIndicatorDefaults.CircularStrokeWidth
) {
    val transition = rememberInfiniteTransition()
    val currentArcStartAngle by transition.animateValue(
        0,
        360,
        Int.VectorConverter,
        infiniteRepeatable(
            animation = tween(
                durationMillis = 1100,
                easing = LinearEasing
            )
        )
    )

    val stroke = with(LocalDensity.current) {
        Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Square)
    }

    Canvas(
        Modifier
            .progressSemantics()
            .size(size)
            .padding(strokeWidth / 2)
    ) {
        drawCircle(Color.LightGray, style = stroke)
        drawArc(
            color,
            startAngle = currentArcStartAngle.toFloat() - 90,
            sweepAngle = sweepAngle,
            useCenter = false,
            style = stroke
        )
    }
}

@Composable
private fun QuickTabItem(
    title: String,
    value: Float?,
    dsValue: Int? = null,
    onClick: () -> Unit
) {


    Row(verticalAlignment = Alignment.CenterVertically) {
        val endValue = if (value != null) {

            if (dsValue == null) {
                if (value.isNaN()) {
                    "NaN"
                } else {
                    value.roundTo(2).toString()
                }
                //, fontWeight = FontWeight.Bold, fontSize = 25.sp

            } else {
                "${
                    value.toString().split(".")[0]
                } ${if (dsValue > 0) "+" else ""}${if (dsValue != 0) dsValue else ""}"
            }
        } else {
            ""
        }
        QuickTabNotNull(
            title = title,
            value = endValue
        ) {
            onClick()
        }
        if (value == null) {
            DotsFlashing(Modifier.padding(top = 5.dp))
        }
    }
}

@Composable
private fun QuickTabNotNull(
    title: String,
    value: String,
    onClick: () -> Unit
) {
    CustomTextButton(
        text = buildAnnotatedString {
            withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
                append("$title: ")
            }
            append(value)
        },
        color = MaterialTheme.colorScheme.onSurface
    ) {
        onClick()
    }
}

@Composable
fun GetAvatar(avatarId: Int, name: String, size: Dp = 70.dp, textSize: TextUnit = 30.sp) {
    val viewManager = LocalViewManager.current
    Box(
        modifier = Modifier.size(size).clip(CircleShape).background(
            brush = Brush.verticalGradient(
                colors = if (viewManager.isDark.value) listOf(
                    MaterialTheme.colorScheme.secondary,
                    MaterialTheme.colorScheme.primaryContainer
                ) else listOf(
                    MaterialTheme.colorScheme.primaryContainer,
                    MaterialTheme.colorScheme.secondary
                ),
                tileMode = TileMode.Decal
            )
        ),
        contentAlignment = Alignment.Center
    ) {
        if (avatarId == 0) {
            Text(
                name[0].toString(),
                fontSize = textSize,
                fontWeight = FontWeight.Normal,
                color = Color.White
            )
        } else if (avatarId == -1) {
            Icon(
                Icons.Rounded.Favorite, null
            )
        }

    }
}

val dotSize = 24.dp // made it bigger for demo
val delayUnit = 300 // you can change delay to change animation speed

@Composable
fun DotsPulsing() {

    @Composable
    fun Dot(
        scale: Float
    ) = Spacer(
        Modifier
            .size(dotSize)
            .scale(scale)
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            )
    )

    val infiniteTransition = rememberInfiniteTransition()

    @Composable
    fun animateScaleWithDelay(delay: Int) = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = delayUnit * 4
                0f at delay with LinearEasing
                1f at delay + delayUnit with LinearEasing
                0f at delay + delayUnit * 2
            }
        )
    )

    val scale1 by animateScaleWithDelay(0)
    val scale2 by animateScaleWithDelay(delayUnit)
    val scale3 by animateScaleWithDelay(delayUnit * 2)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        val spaceSize = 2.dp

        Dot(scale1)
        Spacer(Modifier.width(spaceSize))
        Dot(scale2)
        Spacer(Modifier.width(spaceSize))
        Dot(scale3)
    }
}

@Composable
fun DotsElastic() {
    val minScale = 0.6f

    @Composable
    fun Dot(
        scale: Float
    ) = Spacer(
        Modifier
            .size(dotSize)
            .scale(scaleX = minScale, scaleY = scale)
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            )
    )

    val infiniteTransition = rememberInfiniteTransition()

    @Composable
    fun animateScaleWithDelay(delay: Int) = infiniteTransition.animateFloat(
        initialValue = minScale,
        targetValue = minScale,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = delayUnit * 4
                minScale at delay with LinearEasing
                1f at delay + delayUnit with LinearEasing
                minScale at delay + delayUnit * 2
            }
        )
    )

    val scale1 by animateScaleWithDelay(0)
    val scale2 by animateScaleWithDelay(delayUnit)
    val scale3 by animateScaleWithDelay(delayUnit * 2)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        val spaceSize = 2.dp

        Dot(scale1)
        Spacer(Modifier.width(spaceSize))
        Dot(scale2)
        Spacer(Modifier.width(spaceSize))
        Dot(scale3)
    }
}

@Composable
fun DotsFlashing(modifier: Modifier = Modifier) {
    val minAlpha = 0.1f

    @Composable
    fun Dot(
        alpha: Float
    ) = Spacer(
        Modifier
            .size(8.dp)
            .alpha(alpha)
            .background(
                color = MaterialTheme.colorScheme.onSurface,
                shape = CircleShape
            )
    )

    val infiniteTransition = rememberInfiniteTransition()

    @Composable
    fun animateAlphaWithDelay(delay: Int) = infiniteTransition.animateFloat(
        initialValue = minAlpha,
        targetValue = minAlpha,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = delayUnit * 4
                minAlpha at delay with LinearEasing
                1f at delay + delayUnit with LinearEasing
                minAlpha at delay + delayUnit * 2
            }
        )
    )

    val alpha1 by animateAlphaWithDelay(0)
    val alpha2 by animateAlphaWithDelay(delayUnit)
    val alpha3 by animateAlphaWithDelay(delayUnit * 2)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        val spaceSize = 5.dp

        Dot(alpha1)
        Spacer(Modifier.width(spaceSize))
        Dot(alpha2)
        Spacer(Modifier.width(spaceSize))
        Dot(alpha3)
    }
}

@Composable
fun DotsTyping() {
    val maxOffset = 10f

    @Composable
    fun Dot(
        offset: Float
    ) = Spacer(
        Modifier
            .size(dotSize)
            .offset(y = -offset.dp)
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            )
    )

    val infiniteTransition = rememberInfiniteTransition()

    @Composable
    fun animateOffsetWithDelay(delay: Int) = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = delayUnit * 4
                0f at delay with LinearEasing
                maxOffset at delay + delayUnit with LinearEasing
                0f at delay + delayUnit * 2
            }
        )
    )

    val offset1 by animateOffsetWithDelay(0)
    val offset2 by animateOffsetWithDelay(delayUnit)
    val offset3 by animateOffsetWithDelay(delayUnit * 2)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(top = maxOffset.dp)
    ) {
        val spaceSize = 2.dp

        Dot(offset1)
        Spacer(Modifier.width(spaceSize))
        Dot(offset2)
        Spacer(Modifier.width(spaceSize))
        Dot(offset3)
    }
}

@Composable
fun DotsCollision() {
    val maxOffset = 30f
    val delayUnit = 500 // it's better to use longer delay for this animation

    @Composable
    fun Dot(
        offset: Float
    ) = Spacer(
        Modifier
            .size(dotSize)
            .offset(x = offset.dp)
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            )
    )

    val infiniteTransition = rememberInfiniteTransition()

    val offsetLeft by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = delayUnit * 3
                0f at 0 with LinearEasing
                -maxOffset at delayUnit / 2 with LinearEasing
                0f at delayUnit
            }
        )
    )
    val offsetRight by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = delayUnit * 3
                0f at delayUnit with LinearEasing
                maxOffset at delayUnit + delayUnit / 2 with LinearEasing
                0f at delayUnit * 2
            }
        )
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(horizontal = maxOffset.dp)
    ) {
        val spaceSize = 2.dp

        Dot(offsetLeft)
        Spacer(Modifier.width(spaceSize))
        Dot(0f)
        Spacer(Modifier.width(spaceSize))
        Dot(offsetRight)
    }
}
