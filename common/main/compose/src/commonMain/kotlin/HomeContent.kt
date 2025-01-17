@file:OptIn(ExperimentalMaterial3Api::class)

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.*
import components.cAlertDialog.CAlertDialogStore
import components.cBottomSheet.CBottomSheetStore
import components.refresh.RefreshButton
import components.refresh.keyRefresh
import components.networkInterface.NetworkInterface
import components.networkInterface.NetworkState
import components.networkInterface.isLoading
import decomposeComponents.CAlertDialogContent
import dev.chrisbanes.haze.LocalHazeStyle
import dev.chrisbanes.haze.hazeChild
import home.HomeComponent
import home.HomeStore
import homeComponents.*
import journal.JournalStore
import journal.init.TeacherGroup
import pullRefresh.PullRefreshIndicator
import pullRefresh.pullRefresh
import pullRefresh.rememberPullRefreshState
import report.Grade
import report.UserMark
import resources.RIcons
import server.*
import view.*
import dev.chrisbanes.haze.HazeInputScale
import kotlinx.coroutines.*
import school.SchoolStore


enum class HomeRoutings {
    Tasks, Dnevnik, Other
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@ExperimentalLayoutApi
@Composable
fun HomeContent(
    component: HomeComponent,
    pickedLogin: String = "",
    sharedTransitionScope: SharedTransitionScope,
    isSharedVisible: Boolean,
    currentRouting: HomeRoutings = HomeRoutings.Other
) {
    val model by component.model.subscribeAsState()
    val coroutineScope = rememberCoroutineScope()
    when (model.role) {
        Roles.student -> {
            StudentHomeContent(
                component = component,
                sharedTransitionScope = sharedTransitionScope,
                isSharedVisible = isSharedVisible,
                coroutineScope = coroutineScope,
                currentRouting = currentRouting
            )
        }

        Roles.teacher -> {
            TeacherHomeContent(
                component, pickedLogin,
                coroutineScope = coroutineScope,
                currentRouting = currentRouting
            )
        }

        else -> {
            OtherHomeContent(
                component = component,
                pickedLogin = pickedLogin,
                currentRouting = currentRouting
            )
        }
    }


    if (component.journalComponent != null) {
        val journalModel by component.journalComponent!!.model.subscribeAsState()
        if (journalModel.openingReportData != null) {
            component.journalComponent!!.openReport(journalModel.openingReportData!!)
            component.journalComponent!!.onEvent(JournalStore.Intent.ResetReportData)
        }
    }
    StudentReportDialogContent(
        component.studentReportDialog,
        openReport = if (model.isMentor) { item ->
            val header =
                component.journalComponent!!.model.value.headers.firstOrNull { it.reportId == item }
            if (header != null) {
                component.studentReportDialog.dialog.onEvent(CBottomSheetStore.Intent.HideSheet)
                component.journalComponent?.onEvent(
                    JournalStore.Intent.FetchReportData(
                        header
                    )
                )
            }
        } else null,
        changeToUV = if (model.isMentor) { reportId, login ->
            component.studentReportDialog.dialog.onEvent(CBottomSheetStore.Intent.HideSheet)
            component.onEvent(
                HomeStore.Intent.ChangeToUv(
                    reportId = reportId,
                    login = login,
                    isDeep = true
                )
            )
        } else null
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OtherHomeContent(
    component: HomeComponent,
    pickedLogin: String,
    currentRouting: HomeRoutings
) {
    val viewManager = LocalViewManager.current
    val model by component.model.subscribeAsState()
    val nGradesModel by component.gradesNInterface.networkModel.subscribeAsState()
    val nQuickTabModel by component.quickTabNInterface.networkModel.subscribeAsState()
    val nTeacherModel by component.teacherNInterface.networkModel.subscribeAsState()


    val refreshing =
        (
                nQuickTabModel.isLoading ||
                        nGradesModel.isLoading ||
                        nTeacherModel.isLoading
                )


    val refreshState = rememberPullRefreshState(
        refreshing,
        { component.onEvent(HomeStore.Intent.Init) }
    )

    Scaffold(
        Modifier.fillMaxSize().keyRefresh(refreshState),
        topBar = {
            AppBar(
                title = {
                    Text(
                        "Главная",
                        modifier = Modifier.padding(start = 10.dp),
                        fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                actionRow = {
                    if (viewManager.orientation.value != WindowScreen.Expanded) {
                        RefreshButton(refreshState, viewManager)


                        IconButton(
                            onClick = {
                                component.onOutput(HomeComponent.Output.NavigateToSettings)
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
        Box(
            Modifier.fillMaxSize()
                .pullRefresh(refreshState)
        ) {
            CLazyColumn(
                padding,
                isBottomPaddingNeeded = true,
                refreshState = refreshState
            ) {
                this.homeKidsContent(
                    model = model,
                    nGradesModel = nGradesModel,
                    component = component,
                    pickedLogin = pickedLogin
                )
                this.homeTeacherGroupsContent(
                    model = model,
                    component = component,
                    teacherNInterface = nTeacherModel
                )

                this.homeChildrenNotificationsContent(
                    model = model,
                    nQuickTabModel = nQuickTabModel,
                    viewManager = viewManager,
                    component = component
                )

            }

            PullRefreshIndicator(refreshState, padding.calculateTopPadding())
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TeacherHomeContent(
    component: HomeComponent,
    pickedLogin: String,
    coroutineScope: CoroutineScope,
    currentRouting: HomeRoutings
) {
    val nGradesModel by component.gradesNInterface.networkModel.subscribeAsState()
    val nQuickTabModel by component.quickTabNInterface.networkModel.subscribeAsState()



    if (component.journalComponent != null) {
        val model by component.model.subscribeAsState()
        val journalModel by component.journalComponent!!.model.subscribeAsState()
        val nJournalOpenReportModel by component.journalComponent!!.nOpenReportInterface.networkModel.subscribeAsState()
        val nTeacherModel by component.teacherNInterface.networkModel.subscribeAsState()
        val nScheduleModel by component.scheduleNInterface.networkModel.subscribeAsState()

        val viewManager = LocalViewManager.current
        val lazyListState = rememberLazyListState()

        val itHotsShouldBe = (model.isMentor || model.isParent)

        val groupsItems =
            if (!(model.teacherGroups.isEmpty() && nTeacherModel.state == NetworkState.None)) 2 + model.teacherGroups.size else 0

        val childrenNotsItems =
            2 + if (!(model.notChildren.isEmpty()
                        && nQuickTabModel.state == NetworkState.None)
                && itHotsShouldBe
            ) model.childrenNotifications.values.flatMap { it.map { it } }.size else 0

        val isMainView =
            lazyListState.firstVisibleItemIndex in (0..(groupsItems + childrenNotsItems + 1)).toList()

        val refreshing =
            (nTeacherModel.isLoading || nScheduleModel.isLoading)
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
            Modifier.fillMaxSize().keyRefresh(refreshState),
            topBar = {
                val isHaze = viewManager.hazeHardware.value
                Column(
                    Modifier.then(
                        if (isHaze) Modifier.hazeChild(
                            state = GlobalHazeState.current,
                            style = LocalHazeStyle.current
                        ) {
                            mask =
                                view.hazeMask//Brush.verticalGradient(colors = listOf(Color.Magenta, Color.Transparent))
//                            progressive = hazeProgressive
                        }
                        else Modifier
                    )
                ) {
                    AppBar(
                        containerColor = if (isHaze) Color.Transparent else MaterialTheme.colorScheme.surface,
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AnimatedContent(
                                    targetState = if (!isMainView
                                    ) {
                                        val str = model.currentDate.second.substring(
                                            0,
                                            5
                                        ).split(".")
                                        val dayOfMonth = str[0]
                                        "$dayOfMonth ${numToMonth[str[1].toInt()]}, ${weekPairs[model.currentDate.first]}"
                                    } else "Главная"
                                ) {
                                    Text(
                                        it,
                                        modifier = Modifier.padding(start = 10.dp),
                                        fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                                        fontWeight = FontWeight.Black,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
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
                                RefreshButton(refreshState, viewManager)


                                IconButton(
                                    onClick = {
                                        component.onOutput(HomeComponent.Output.NavigateToSettings)
                                    }
                                ) {
                                    GetAsyncIcon(
                                        RIcons.Settings
                                    )
                                }
                            }

                        },
                        isTransparentHaze = isHaze
                    )
                    AnimatedVisibility(model.isDatesShown && !isMainView) {
                        DatesLine(
                            dates = model.dates,
                            currentDate = model.currentDate,
                            onClick = {
                                component.onEvent(HomeStore.Intent.ChangeDate(it))
                            }
                        )
                    }
                }
            }
        ) { padding ->
            Box(
                Modifier.fillMaxSize()
                    .pullRefresh(refreshState)
            ) {
                CLazyColumn(
                    modifier = Modifier,
                    state = lazyListState,
                    padding = padding,
                    isBottomPaddingNeeded = true,
                    refreshState = refreshState
                ) {
                    this.homeTeacherGroupsContent(
                        model = model,
                        component = component,
                        teacherNInterface = nTeacherModel
                    )
                    this.homeKidsContent(
                        model = model,
                        nGradesModel = nGradesModel,
                        component = component,
                        pickedLogin = pickedLogin
                    )
                    this.homeChildrenNotificationsContent(
                        model = model,
                        nQuickTabModel = nQuickTabModel,
                        viewManager = viewManager,
                        component = component
                    )
                    item {
                        RaspisanieTitleBox(
                            isMainView = isMainView,
                            model = model,
                            component = component
                        )
                    }
                    item {
                        RaspisanieTable(
                            model = model,
                            nScheduleModel = nScheduleModel,
                            component = component,
                            journalModel = journalModel
                        )
                    }

                }
                PullRefreshIndicator(
                    state = refreshState,
                    topPadding = padding.calculateTopPadding()
                )
                if (viewManager.orientation.value != WindowScreen.Expanded)
                    StudentsPreviewDialog(
                        component.journalComponent!!, journalModel
                    )
                AnimatedVisibility(
                    nJournalOpenReportModel.state != NetworkState.None,
                    modifier = Modifier.align(Alignment.Center),
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
                            when (it) {
                                NetworkState.Error -> DefaultErrorView(
                                    component.journalComponent!!.nOpenReportInterface.networkModel.value,
                                    pos = DefaultErrorViewPos.CenteredNotFull,
                                    modifier = Modifier.padding(10.dp),
                                    text = "Не удалось загрузить отчёт",
                                    buttonText = "Закрыть",
                                    isCompact = true
                                ) {
                                    component.journalComponent!!.nOpenReportInterface.goToNone()
                                }

                                else -> CircularProgressIndicator(Modifier.padding(10.dp))
                            }
                        }
                    }
                }

                //WTF?
                CAlertDialogContent(
                    component = component.reportsDialog,
                    title = "Отчёты"
                ) {
                    Crossfade(nJournalOpenReportModel.state) {
                        when (it) {
                            NetworkState.None -> Column(
                                modifier = Modifier.verticalScroll(
                                    rememberScrollState()
                                )
                            ) {
                                model.someHeaders.sortedBy { it.reportId }.forEach { item ->
                                    JournalItemCompose(
                                        subjectName = item.subjectName,
                                        groupName = item.groupName,
                                        lessonReportId = item.reportId,
                                        date = item.date,
                                        teacher = item.teacherName,
                                        time = item.time,
                                        isEnabled = true,
                                        isActive = true,
                                        isEnded = item.status,
                                        theme = item.theme,
                                        module = item.module
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
                            NetworkState.Error -> DefaultErrorView(
                                component.journalComponent!!.nOpenReportInterface.networkModel.value,
                                pos = DefaultErrorViewPos.CenteredNotFull,
                                modifier = Modifier.padding(10.dp),
                                text = "Не удалось загрузить отчёт",
                                buttonText = "Выбрать ещё раз"
                            ) {
                                component.journalComponent!!.nOpenReportInterface.goToNone()
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
    val isLoadingTestMode = isTestMode && false
    val items = model.items[model.currentDate.second]
    val dots = remember { mutableStateOf(".") }
    LaunchedEffect(Unit) {
        while (true) {
            delay(700)
            if (dots.value.length < 3) dots.value += "."
            else dots.value = "."
        }
    }

    Crossfade(nScheduleModel.state) { st ->

        Column {
            when {
                st is NetworkState.None && !isLoadingTestMode -> {
                    if (items.isNullOrEmpty()) {
                        Text(
                            "Здесь пока ничего нет",
                            modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Spacer(Modifier.height(10.dp))

                        items
                            .forEach { it ->
                                Lesson(
                                    num = it.num,
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
                                    journalModel = journalModel,
                                    marks = it.marks,
                                    stupsSum = it.stupsSum,
                                    isSwapped = it.isSwapped,
                                    lessonIndex = it.lessonIndex,
                                    isMarked = it.isMarked
                                )
                                Spacer(Modifier.padding(10.dp))
                            }


                        Spacer(Modifier.height(5.dp))
                        Text(
                            text = "Обновлено: ${model.lastUpdate}",
                            modifier = Modifier.fillMaxWidth().alpha(.5f),
                            textAlign = TextAlign.Center,
                            fontSize = 10.esp
                        )
                    }

                }

                st is NetworkState.Loading || isLoadingTestMode -> {
                    Row {
                        Text("Загрузка")
                        AnimatedContent(
                            dots.value,
                            transitionSpec = {
                                fadeIn(animationSpec = tween(180))
                                    .togetherWith(fadeOut(animationSpec = tween(180)))
                            }

                        ) { text ->
                            Text("$text")
                        }
                    }
                }

                st is NetworkState.Error -> DefaultErrorView(
                    nScheduleModel,
                    pos = DefaultErrorViewPos.Unspecified
                )

                else -> {}
            }

        }
    }
}

val numToMonth = mapOf<Int, String>(
    1 to "Января",
    2 to "Февраля",
    3 to "Марта",
    4 to "Апреля",
    5 to "Мая",
    6 to "Июня",
    7 to "Июля",
    8 to "Августа",
    9 to "Сентября",
    10 to "Октября",
    11 to "Ноября",
    12 to "Декабря",
)

@OptIn(ExperimentalFoundationApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun StudentHomeContent(
    component: HomeComponent,
    sharedTransitionScope: SharedTransitionScope,
    isSharedVisible: Boolean,
    coroutineScope: CoroutineScope,
    currentRouting: HomeRoutings
) {
    val model by component.model.subscribeAsState()

    val schoolModel = component.schoolComponent.model.subscribeAsState()
    val kids = schoolModel.value.dutyKids.filterIndexed { index, dutyKid -> index + 1 <= schoolModel.value.dutyPeopleCount }

    val nQuickTabModel by component.quickTabNInterface.networkModel.subscribeAsState()
    val nGradesModel by component.gradesNInterface.networkModel.subscribeAsState()
    val nScheduleModel by component.scheduleNInterface.networkModel.subscribeAsState()
    val nTeacherModel by component.teacherNInterface.networkModel.subscribeAsState()

    val viewManager = LocalViewManager.current
    val lazyListState = rememberLazyListState()


    val isMainView = lazyListState.firstVisibleItemIndex in (0..model.notifications.size
                                                                + 1 //studentBar
                                                                + 1 //NotificationAboutDuty
                                                                + 1 //NotificationAboutDolg
            )

    val refreshing =
        (
                nQuickTabModel.isLoading ||
                        nGradesModel.isLoading ||
                        nScheduleModel.isLoading ||
                        nTeacherModel.isLoading
                )


    val refreshState = rememberPullRefreshState(
        refreshing,
        {
            component.onRefreshClick()
        }
    )

    Scaffold(
        Modifier.fillMaxSize().keyRefresh(refreshState),
        topBar = {
            val isHaze = viewManager.hazeHardware.value
            Column(
                Modifier.then(
                    if (isHaze) Modifier.hazeChild(
                        GlobalHazeState.current
                    ) {
                        inputScale = HazeInputScale.Fixed(0.7f)
                        mask = view.hazeMask//Brush.verticalGradient(colors = listOf(Color.Magenta, Color.Transparent))
//                        progressive = hazeProgressive
                    }
                    else Modifier
                )
            ) {
                AppBar(
                    containerColor = if (isHaze) Color.Transparent else MaterialTheme.colorScheme.surface,
                    navigationRow = {
                        if (component.onBackButtonPress != null) {
                            IconButton(
                                onClick = {
                                    GlobalScope.launch(Dispatchers.Main) {
                                        component.onBackButtonPress?.invoke()
                                    }
                                }
                            ) {
                                GetAsyncIcon(
                                    path = RIcons.ChevronLeft
                                )
                            }
                        }
                    },
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AnimatedContent(
                                targetState = if (!isMainView) {
                                    val str = model.currentDate.second.substring(
                                        0,
                                        5
                                    ).split(".")
                                    val dayOfMonth = str[0]
                                    "$dayOfMonth ${numToMonth[str[1].toInt()]}, ${weekPairs[model.currentDate.first]}"
                                } else "Главная"
                            ) {
                                Text(
                                    it,
                                    modifier = Modifier.padding(start = 10.dp),
                                    fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                                    fontWeight = FontWeight.Black,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            AnimatedVisibility(isMainView && component.onBackButtonPress != null) {
                                IconButton(onClick = {
                                    component.onOutput(HomeComponent.Output.NavigateToSchool)
                                }) {
                                    GetAsyncIcon(
                                        path = RIcons.School
                                    )
                                }
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

                            RefreshButton(
                                refreshState, viewManager
                            )
                            IconButton(
                                onClick = {
                                    component.onOutput(HomeComponent.Output.NavigateToSettings)
                                }
                            ) {
                                GetAsyncIcon(
                                    path = RIcons.Settings
                                )
                            }
                        }

                    },
                    isTransparentHaze = isHaze
                )
                AnimatedVisibility(model.isDatesShown && !isMainView) {
                    DatesLine(
                        dates = model.dates,
                        currentDate = model.currentDate,
                        onClick = {
                            component.onEvent(HomeStore.Intent.ChangeDate(it))
                        }
                    )
                }
            }
        }
    ) { padding ->



        Box(
            Modifier.fillMaxSize()
                .pullRefresh(refreshState)
        ) {
            CLazyColumn(
                modifier = Modifier
                    .animateContentSize(),
                state = lazyListState,
                padding = padding,
                isBottomPaddingNeeded = true,
                refreshState = refreshState
            ) {
                this.homeStudentBar(
                    model = model,
                    nGradesModel = nGradesModel,
                    nQuickTabModel = nQuickTabModel,
                    component = component,
                    coroutineScope = coroutineScope,
                    sharedTransitionScope = sharedTransitionScope,
                    isSharedVisible = isSharedVisible,
                    nTeacherModel = nTeacherModel,
                    currentRouting = currentRouting
                )
                this.homeStudentNotifications(
                    model = model,
                    component = component,
                    viewManager = viewManager,
                    kids = kids
                )
                items(2) { num ->
                    if (num == 0) {
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
                state = refreshState,
                topPadding = padding.calculateTopPadding()
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
                fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                fontWeight = FontWeight.Black
            )
            if (!isMainView) {
                AnimatedVisibility(model.isDatesShown) {
                    DatesLine(
                        dates = model.dates,
                        currentDate = model.currentDate,
                        onClick = {
                            component.onEvent(HomeStore.Intent.ChangeDate(it))
                        }
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
                            fontSize = MaterialTheme.typography.headlineSmall.fontSize,
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
                                fontSize = MaterialTheme.typography.titleSmall.fontSize,
                                color = MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = 0.6f
                                ),
                                fontWeight = FontWeight.Bold,
//                                lineHeight = 10.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    CalendarButton(component)
                }

                AnimatedVisibility(model.isDatesShown) {
                    DatesLine(
                        dates = model.dates,
                        currentDate = model.currentDate,
                        onClick = {
                            component.onEvent(HomeStore.Intent.ChangeDate(it))
                        }
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
    marks: List<UserMark>,
    stupsSum: Int,
    component: HomeComponent,
    model: HomeStore.State,
    journalModel: JournalStore.State?,
    isSwapped: Boolean,
    lessonIndex: Int,
    isMarked: Boolean
) {

    val isSurnameShown = remember { mutableStateOf(groupId == -6) }
    val viewManager = LocalViewManager.current
    val firstElement =
        model.items[date]?.sortedBy { it.start.toMinutes() }?.first { it.groupId == groupId }
    val isFirst = firstElement?.start == start
    val coroutineScope = rememberCoroutineScope()
    val todayParts = today.substring(0, 5).split(".")
    val dateParts = date.substring(0, 5).split(".")
    val currentTime = getCurrentDayTime().toMinutes()
    val isEnded = (currentTime >= end.toMinutes() && today == date) ||
            (todayParts[0].toInt() > dateParts[0].toInt() && todayParts[1].toInt() == dateParts[1].toInt()) ||
            (todayParts[1].toInt() > dateParts[1].toInt())
    val minutesOst = start.toMinutes() - currentTime

    val endOfZavtrak = "10:00".toMinutes()
    val endOfLunch = "11:00".toMinutes()
    val endOfObed = "15:20".toMinutes()
    val endOfPoldnik = "17:00".toMinutes()
    val text =
        if (groupId != -11) title.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        else when (start.toMinutes()) {
            in 0..endOfZavtrak -> "Завтрак"
            in endOfZavtrak..endOfLunch -> "Ланч"
            in endOfLunch..endOfObed -> "Обед"
            in endOfObed..endOfPoldnik -> "Полдник"
            else -> "Ужин"
        }

    if (groupId != -11) {
        Row(Modifier.padding(end = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.width(25.dp).height(50.dp), contentAlignment = Alignment.CenterStart) {
                if (groupId != -11) {
                    Text(
                        num.toString(),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (!isEnded) 1f else 0.5f),
                        fontSize = MaterialTheme.typography.titleMedium.fontSize,
                        fontWeight = FontWeight.SemiBold
                    )
                } else {
                    GetAsyncIcon(
                        path = RIcons.Restaraunt,
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = if (!isEnded) 1f else 0.5f)
                    )
                }
                if (role == Roles.teacher) {
                    val headers = journalModel!!.headers.filter {
                        it.date == model.currentDate.second &&
                                it.groupId == groupId &&
                                (it.time.toMinutes() >= start.toMinutes() && it.time.toMinutes() < end.toMinutes())
                    }
                    if (headers.isNotEmpty() && headers.first().status) {
                        Box(
                            Modifier.offset(x = 16.dp, y = (-14).dp).align(Alignment.CenterStart)
                                .size(5.dp).clip(
                                    CircleShape
                                ).background(MaterialTheme.colorScheme.primary)
                        )
                    }
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    AnimatedContent(
                        if (isSurnameShown.value) fio.surname else "",
                        transitionSpec = { fadeIn().togetherWith(fadeOut()) }
                    ) { surname ->
                        Text(
                            buildAnnotatedString {
                                withStyle(
                                    SpanStyle(
                                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (!isEnded) 1f else 0.5f)
                                    )
                                ) {
                                    append(text)
                                }
                                if (cabinet != "0") {
                                    withStyle(
                                        SpanStyle(
                                            fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (!isEnded) .6f else 0.3f)
                                        )
                                    ) {
                                        append(" $cabinet")
                                    }
                                }

                                if (groupId !in listOf(0, -11)) {
                                    withStyle(
                                        SpanStyle(
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (!isEnded) 1f else 0.5f)
                                        )
                                    ) {
                                        append("\n")
                                        append(group.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() })
                                    }
                                }
                                withStyle(
                                    SpanStyle(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (!isEnded) .6f else 0.3f)
                                    )
                                ) {
                                    append(
                                        " ${surname}${if (isSwapped) "*" else ""}"
                                    )
                                }
                                withStyle(
                                    SpanStyle(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.esp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (!isEnded) .6f else 0.3f)
                                    )
                                ) {
                                    append("\n")
                                    append("$start-$end")
                                }

                            }, lineHeight = MaterialTheme.typography.titleMedium.fontSize,
                            modifier = Modifier.clickable(
                                interactionSource = MutableInteractionSource(),
                                indication = null
                            ) { isSurnameShown.value = !isSurnameShown.value })
                    }
                }


                if (role == Roles.student) {
                    if (
                        isEnded || today == date
                    ) {
                        val notNow = minutesOst > 0 && !isEnded
                        if (notNow || !isEnded || (marks.isEmpty() && stupsSum == 0)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (notNow) {
                                    GetAsyncIcon(
                                        path = RIcons.Schedule,
                                        size = 20.dp
                                    )
                                    Spacer(Modifier.width(5.dp))
                                }
                                val hours = (minutesOst / 60)
                                val finalMinutes = (minutesOst - hours * 60)
                                Text(
                                    if (notNow) if (hours >= 1 && finalMinutes != 0) "$hours ч $finalMinutes мин" else "$minutesOst мин."
                                    else if (!isEnded) "Уже идёт!"
                                    else "",
                                    lineHeight = 5.esp,
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 1
                                )
                            }
                        } else if (isFirst) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                LazyRow {
                                    items(items = marks) {
                                        cMark(
                                            mark = it,
                                            coroutineScope = coroutineScope,
                                            showDate = false
                                        )
                                    }
                                }
                                if (stupsSum != 0) {
                                    Spacer(Modifier.width(7.dp))
                                    Box(modifier = Modifier.padding(top = 3.dp)) {
                                        BorderStup(stupsSum.toString(), "!st")
                                    }
                                }
                            }
                        }
                    }
                } else if (journalModel != null) {
                    if (isEnded || today == date) {
                        val headers = journalModel.headers.filter {
                            it.date == model.currentDate.second &&
                                    it.groupId == groupId &&
                                    (it.time.toMinutes() >= start.toMinutes() && it.time.toMinutes() < end.toMinutes())
                        }
                        val isCreated = headers.isNotEmpty() || isMarked
                        FilledTonalButton(
                            onClick = {
                                if (groupId > 0) {
                                    with(component.journalComponent!!) {
                                        if (isCreated) {
                                            if (headers.size == 1) {
                                                onEvent(JournalStore.Intent.FetchReportData(headers.first()))
                                            } else {
                                                component.onEvent(
                                                    HomeStore.Intent.UpdateSomeHeaders(
                                                        headers
                                                    )
                                                )
                                                component.reportsDialog.onEvent(CAlertDialogStore.Intent.ShowDialog)
                                            }
                                        } else if (!isEnded) {

                                            onEvent(
                                                JournalStore.Intent.OnGroupClicked(
                                                    groupId,
                                                    start,
                                                    date = date,
                                                    lessonId = lessonIndex
                                                )
                                            )
                                        }
                                    }
                                } else if (!isCreated && !isEnded) {
                                    component.onEvent(HomeStore.Intent.MarkLesson(lessonIndex))
                                }
                            },
                            enabled = !isEnded || isCreated,
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
                        ) {
                            Text(
                                if (groupId > 0) {
                                    if (isCreated) "Посмотреть отчёт" else if (isEnded) "Отчёт не найден" else "Создать отчёт"
                                } else {
                                    if (isCreated) "Готово" else if (isEnded) "Пропуск" else "Отметить"
                                },
                                lineHeight = 10.esp
                            )
                        }
                    }
                }
            }
        }
    } else {
        Box(
            Modifier.fillMaxWidth(),
        ) {
            GetAsyncIcon(
                path = RIcons.Restaraunt,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = if (!isEnded) 1f else 0.5f),
                modifier = Modifier.align(Alignment.CenterStart)
            )
            Text(
                text,
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (!isEnded) 1f else 0.5f),
                modifier = Modifier.align(Alignment.Center)
            )
            Text(
                "$start-$end",
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.esp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (!isEnded) .6f else 0.3f),
                modifier = Modifier.align(Alignment.CenterEnd)
            )

        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun cGrade(mark: Grade, coroutineScope: CoroutineScope, onClick: () -> Unit) {
    val tState = rememberTooltipState(isPersistent = false)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(PaddingValues(start = 5.dp))
    ) {
        Text(mark.date.subSequence(0, 5).toString(), fontSize = 10.esp, lineHeight = 10.esp)
        Spacer(Modifier.height(2.dp))
        TooltipBox(
            state = tState,
            tooltip = {
                PlainTooltip(modifier = Modifier.clickable {}) {
                    Text(
                        "${fetchReason(mark.reason)}",
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
                date = mark.date,
//            size = markSize,
//            textYOffset = yOffset,
                addModifier = Modifier.clickable {
                    coroutineScope.launch {
                        tState.show()
                    }
                    onClick()
                }.handy()
            )
        }


    }
}

@Composable
fun CalendarButton(component: HomeComponent) {
    IconButton(
        onClick = { component.onEvent(HomeStore.Intent.ChangeIsDatesShown) }
    ) {
        GetAsyncIcon(
            RIcons.Calendar
        )
    }
}

@Composable
fun TeacherGroupButton(component: HomeComponent, it: TeacherGroup, modifier: Modifier) {
    FilledTonalButton(
        modifier = modifier.fillMaxWidth()
            .padding(bottom = 10.dp)
            .handy(),
        onClick = {
            component.onOutput(
                HomeComponent.Output.NavigateToAllGroupMarks(
                    subjectId = it.subjectId,
                    subjectName = it.subjectName,
                    groupId = it.cutedGroup.groupId,
                    groupName = it.cutedGroup.groupName,
                    teacherLogin = it.teacherLogin
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
                        append(it.cutedGroup.groupName)
                    }
                    append(" ")
                    append("(${it.teacherLogin})")
                }
            )
            GetAsyncIcon(
                path = RIcons.ChevronLeft,
                modifier = Modifier.rotate(180f)
            )
        }
    }
}


@Composable
fun RecentMarkContent(
    mark: String,
    cutedReason: String,
    subjectName: String,
    addModifier: Modifier = Modifier,
    date: String
//    offset: DpOffset = DpOffset(0.dp, 0.dp),
//    paddingValues: PaddingValues = ,
////    size: Dp = 25.dp,
//    textYOffset: Dp = 0.dp
) {
    val viewManager = LocalViewManager.current

    val isNotStups = cutedReason !in listOf(
        "!ds",
        "!st"
    )

    val markColor = if (isNotStups)
        (if (viewManager.colorMode.value == "3") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.inversePrimary).getMarkColor(
            mark,
            viewManager,
            .2f
        )
    else MaterialTheme.colorScheme.surface

    val textColor = if (isNotStups && viewManager.colorMode.value in listOf(
            "3",
            "4"
        )
    ) markColor.blend(Color.White, amount = 1f)
    else MaterialTheme.colorScheme.onSurface

    Box(
        Modifier
            .border(
                width = if (isNotStups) 0.dp else 1.dp,
                color = if (isNotStups) Color.Transparent else MaterialTheme.colorScheme.outline.copy(
                    if (cutedReason != "!ds") 1f else 0f
                ),
                shape = RoundedCornerShape(30)
            )

            .clip(RoundedCornerShape(percent = 30))
            .background(
                markColor
            )
            .then(
                if (cutedReason == "!ds") Modifier.dashedBorder(
                    3.dp,
                    color = MaterialTheme.colorScheme.outline,
                    cornerRadiusDp = 16.dp
                ) else Modifier
            )

            .then(addModifier),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(5.dp).padding(horizontal = 2.dp).offset(y = -2.dp)
        ) {
            Text(
                (if (!isNotStups && mark.toInt() > 0 && !mark.contains("+")
                ) "+" else "") + mark,
                fontSize = 18.esp,
                modifier = Modifier.fillMaxWidth().offset(y = 4.dp),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Black,
                color = textColor
            )
            Text(
                subjectName,
                color = textColor
            )
        }
        if (viewManager.colorMode.value in listOf("0", "1") && (isNotStups || mark.toInt() < 0)) {
            val colors =
                if (viewManager.colorMode.value == "1") markColorsColored else markColorsMono
            Box(
                Modifier.padding(top = 10.dp, end = 10.dp).align(Alignment.TopEnd).size(5.dp).clip(
                    CircleShape
                ).background(
                    colors[mark]
                        ?: if (viewManager.colorMode.value == "1") Color.Black else Color.Red
                ) //MaterialTheme.colorScheme.primary
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
                0f at delay using LinearEasing
                1f at delay + delayUnit using LinearEasing
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
                minScale at delay using LinearEasing
                1f at delay + delayUnit using LinearEasing
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
                minAlpha at delay using LinearEasing
                1f at delay + delayUnit using LinearEasing
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
                0f at delay using LinearEasing
                maxOffset at delay + delayUnit using LinearEasing
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
                0f at 0 using LinearEasing
                -maxOffset at delayUnit / 2 using LinearEasing
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
                0f at delayUnit using LinearEasing
                maxOffset at delayUnit + delayUnit / 2 using LinearEasing
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