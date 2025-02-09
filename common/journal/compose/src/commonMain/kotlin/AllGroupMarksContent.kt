@file:OptIn(
    ExperimentalLayoutApi::class, ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class
)

import allGroupMarks.AllGroupMarksComponent
import allGroupMarks.AllGroupMarksStore
import allGroupMarks.DatesFilter
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.rememberTooltipState
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.foundation.AppBar
import components.journal.BorderStup
import components.foundation.CFilterChip
import components.foundation.CLazyColumn
import components.foundation.CTextButton
import components.foundation.DefaultErrorView
import components.foundation.DefaultErrorViewPos
import components.GetAsyncIcon
import components.journal.MarkContent
import components.journal.MarkTable
import components.MarkTableItem
import components.journal.StupsButtons
import components.cAlertDialog.CAlertDialogStore
import components.networkInterface.NetworkState
import components.networkInterface.isLoading
import decomposeComponents.CAlertDialogContent
import homeTasksDialog.HomeTasksDialogStore
import report.UserMarkPlus
import resources.RIcons
import server.fetchReason
import server.getLocalDate
import server.roundTo
import androidx.compose.desktop.ui.tooling.preview.utils.esp
import utils.cursor.handy
import androidx.compose.desktop.ui.tooling.preview.utils.popupPositionProvider
import androidx.compose.material3.LocalTextStyle

@OptIn(ExperimentalFoundationApi::class)
@ExperimentalMaterial3Api
@ExperimentalLayoutApi
@Composable
fun AllGroupMarksContent(
    component: AllGroupMarksComponent
) {
    val model by component.model.subscribeAsState()
    val nModel by component.nInterface.networkModel.subscribeAsState()
    val nOpenReportModel by component.nOpenReportInterface.networkModel.subscribeAsState()
    val coroutineScope = rememberCoroutineScope()

    if (model.reportData != null) {
        val reportData = model.reportData
        component.onEvent(AllGroupMarksStore.Intent.DeleteReport)
        component.onOutput(AllGroupMarksComponent.Output.OpenReport(reportData!!))
    }

    LaunchedEffect(Unit) {
        if(!nModel.isLoading) component.onEvent(AllGroupMarksStore.Intent.Init)
    }

    //PullToRefresh
//    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    BoxWithConstraints {
//        val isFullView by mutableStateOf(this.maxWidth > 600.dp)
        Scaffold(
            Modifier.fillMaxSize(),
//                .nestedScroll(scrollBehavior.nestedScrollConnection)
            topBar = {
                AppBar(
                    title = {
                        val bigTextSize =
                            MaterialTheme.typography.titleLarge.fontSize// if (!isLarge) else 40.sp
                        val smallTextSize =
                            MaterialTheme.typography.titleSmall.fontSize//if (!isLarge)  else 28.sp

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
                                            append(model.subjectName)
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
                            Text(

                                text = buildAnnotatedString {
                                    withStyle(
                                        SpanStyle(
                                            fontWeight = FontWeight.Normal
                                        )
                                    ) {
                                        append(model.groupName)
                                    }

                                },
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                style = LocalTextStyle.current.copy(
                                    fontSize = smallTextSize,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = .5f),
                                    lineHeightStyle = LineHeightStyle(
                                        alignment = LineHeightStyle.Alignment.Top,
                                        trim = LineHeightStyle.Trim.FirstLineTop
                                    )
                                )
                            )

                        }
                    },
                    navigationRow = {
                        IconButton(
                            onClick = { component.onOutput(AllGroupMarksComponent.Output.Back) }
                        ) {
                            GetAsyncIcon(
                                path = RIcons.CHEVRON_LEFT
                            )
                        }
                    },

                    actionRow = {

                        IconButton(
                            onClick = {
                                component.onEvent(
                                    AllGroupMarksStore.Intent.ChangeTableView(
                                        !model.isTableView
                                    )
                                )
                            }
                        ) {
                            GetAsyncIcon(
                                path = if (!model.isTableView) RIcons.TABLE else RIcons.CONTACT_BOOK
                            )
                        }

                        IconButton(
                            onClick = {
                                component.homeTasksDialogComponent.onEvent(
                                    HomeTasksDialogStore.Intent.Init
                                )
                                component.homeTasksDialogComponent.dialogComponent.onEvent(
                                    CAlertDialogStore.Intent.ShowDialog
                                )
                            }
                        ) {
                            GetAsyncIcon(
                                path = RIcons.HOMEWORK
                            )
                        }
                    }
                )
                //LessonReportTopBar(component, isFullView) //, scrollBehavior
            }
        ) { padding ->
            Column(Modifier.fillMaxSize()) {
                Crossfade(nModel.state) { state ->
                    when (state) {
                        NetworkState.None -> {
                            Crossfade(model.isTableView) { crossfadeState ->
                                if (crossfadeState) {
                                    Box(
                                        Modifier.fillMaxSize().padding(padding),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column {
                                            Row(Modifier.horizontalScroll(rememberScrollState())) {
                                                CFilterChip(
                                                    label = "За неделю",
                                                    isSelected = model.dateFilter is DatesFilter.Week,
                                                    state = nModel.state,
                                                    coroutineScope = coroutineScope
                                                ) {
                                                    component.onEvent(
                                                        AllGroupMarksStore.Intent.ChangeFilterDate(
                                                            DatesFilter.Week
                                                        )
                                                    )
                                                }
                                                Spacer(Modifier.width(5.dp))
                                                CFilterChip(
                                                    label = "За прошлую неделю",
                                                    isSelected = model.dateFilter is DatesFilter.PreviousWeek,
                                                    state = nModel.state,
                                                    coroutineScope = coroutineScope
                                                ) {
                                                    component.onEvent(
                                                        AllGroupMarksStore.Intent.ChangeFilterDate(
                                                            DatesFilter.PreviousWeek
                                                        )
                                                    )
                                                }
                                                Spacer(Modifier.width(5.dp))
                                                model.modules.forEach { module ->
                                                    CFilterChip(
                                                        label = "За ${module} модуль",
                                                        isSelected = (model.dateFilter is DatesFilter.Module) && module in (model.dateFilter as DatesFilter.Module).modules,
                                                        state = nModel.state,
                                                        coroutineScope = coroutineScope
                                                    ) {
                                                        component.onEvent(
                                                            AllGroupMarksStore.Intent.ChangeFilterDate(
                                                                DatesFilter.Module(
                                                                    listOf(module)
                                                                )
                                                            )
                                                        )
                                                    }
                                                    Spacer(Modifier.width(5.dp))
                                                }
                                            }
                                            val students = model.students.sortedBy { it.shortFIO }
                                            val filteredDates =
                                                model.dates.filter {
                                                    when (model.dateFilter) {
                                                        is DatesFilter.Week -> it.date in model.weekDays
                                                        is DatesFilter.PreviousWeek -> it.date in model.previousWeekDays
                                                        is DatesFilter.Module -> it.module in (model.dateFilter as DatesFilter.Module).modules
                                                        else -> false
                                                    }
                                                }
                                            MarkTable(
                                                fields = students.associate { it.login to it.shortFIO },
                                                dms = filteredDates.associate { y ->
                                                    y.date to (students.flatMap { x ->
                                                        (x.marks + x.stups).filter { it.mark.date == y.date }
                                                            .map {
                                                                MarkTableItem(
                                                                    content = it.mark.content,
                                                                    login = x.login,
                                                                    reason = it.mark.reason,
                                                                    reportId = it.mark.reportId,
                                                                    module = it.mark.module,
                                                                    date = it.mark.date,
                                                                    deployDate = it.deployDate,
                                                                    deployTime = it.deployTime,
                                                                    deployLogin = it.deployLogin,
                                                                    onClick = {
                                                                        component.onEvent(
                                                                            AllGroupMarksStore.Intent.OpenFullReport(
                                                                                reportId = it
                                                                            )
                                                                        )
                                                                    },
                                                                    isTransparent = it.deployLogin != model.login && model.isModer
                                                                )
                                                            }
                                                    })
                                                },
                                                nki = students.associate {
                                                    it.login to it.nki.filter {
                                                        when (model.dateFilter) {
                                                            is DatesFilter.Week -> it.date in model.weekDays
                                                            is DatesFilter.PreviousWeek -> it.date in model.previousWeekDays
                                                            is DatesFilter.Module -> it.date in filteredDates.map { it.date }
                                                            else -> false
                                                        }
                                                    }
                                                },
                                                isDs1Init = component.setingsRepository.fetchIsShowingPlusDS()
                                            )
                                        }
                                    }
                                } else {
                                    CLazyColumn(padding = padding) {
                                                               if (model.students.isNotEmpty()) {
                                            items(model.students) { s ->

                                                AllGroupMarksStudentItem(
                                                    title = s.shortFIO,
                                                    marks = s.marks.sortedBy { getLocalDate(it.mark.date).toEpochDays() }
                                                        .reversed(),
                                                    stups = s.stups,
                                                    firstHalfNums = model.firstHalfNums,
                                                    modifier = Modifier.padding(top = if (model.students.first() == s) 0.dp else 10.dp),
                                                    component = component,
                                                    login = model.login,
                                                    isModer = model.isModer
                                                ) {
                                                    component.onEvent(
                                                        AllGroupMarksStore.Intent.OpenDetailedStups(
                                                            s.login
                                                        )
                                                    )
                                                }
                                            }

                                        } else {
                                            item() {
                                                Text("Никто в этой группе не учится 0_0")
                                            }
                                        }
                                    }

                                }
                            }
                        }

                        NetworkState.Loading -> {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }

                        NetworkState.Error -> DefaultErrorView(
                            nModel,
                            DefaultErrorViewPos.CenteredFull
                        )
                    }
                }
            }


            val detailedStupsStudent =
                model.students.firstOrNull { it.login == model.detailedStupsLogin }

            CAlertDialogContent(
                component = component.stupsDialogComponent,
                title = "Ступени: ${detailedStupsStudent?.shortFIO ?: "null"}",
                titleXOffset = 5.dp
            ) {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    model.students.firstOrNull { it.login == model.detailedStupsLogin }?.stups?.sortedBy {
                        getLocalDate(
                            it.mark.date
                        ).toEpochDays()
                    }?.reversed()?.forEach {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 1.dp)
                                .padding(horizontal = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(it.mark.date)
                            Text(fetchReason(it.mark.reason))
                            BorderStup(it.mark.content, reason = it.mark.reason)
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            nOpenReportModel.state != NetworkState.None,
            modifier = Modifier.align(Alignment.Center),
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            Crossfade(nOpenReportModel.state) {
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    tonalElevation = 5.dp,
                    shadowElevation = 10.dp,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    when (it) {
                        NetworkState.Error -> DefaultErrorView(
                            component.nOpenReportInterface.networkModel.value,
                            DefaultErrorViewPos.CenteredNotFull,
                            modifier = Modifier.padding(10.dp),
                            text = "Не удалось загрузить отчёт",
                            buttonText = "Закрыть",
                            isCompact = true
                        ) {
                            component.nOpenReportInterface.goToNone()
                        }

                        else -> CircularProgressIndicator(Modifier.padding(10.dp))
                    }
                }
            }
        }


        HomeTasksDialogContent(
            component.homeTasksDialogComponent
        )
    }


}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AllGroupMarksStudentItem(
    title: String,
//    groupId: Int,
//    isQuarters: Boolean,
//    coroutineScope: CoroutineScope,
    marks: List<UserMarkPlus>,
    stups: List<UserMarkPlus>,
    firstHalfNums: List<Int>,
//    stupsCount: Int,
    modifier: Modifier = Modifier,
    component: AllGroupMarksComponent,
    login: String,
    isModer: Boolean,
    onClick: () -> Unit
) {
    val isFullView = remember { mutableStateOf(false) }


    val modules = marks.map { it.mark.module }.toSet().sorted().reversed()

    val usedHalfNum = if (modules.isNotEmpty()) if (modules.first()
            .toInt() in firstHalfNums
    ) 1 else 2 else 1

    ElevatedCard(
        modifier.fillMaxWidth()//.padding(horizontal = 10.dp)
            .animateContentSize().clip(CardDefaults.elevatedShape)
    ) {
//            .clickable {
//                isFullView.value = !isFullView.value
//            }) {
        Column(Modifier.padding(5.dp).padding(start = 8.dp)) {
            Row(
                Modifier.fillMaxWidth().padding(end = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Text(
                        title,
                        fontWeight = FontWeight.Medium,
                        fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                        modifier = Modifier.weight(2f, false),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(Modifier.weight(1.2f, true), horizontalArrangement = Arrangement.End) {
                        StupsButtons(
                            stups = stups.map {
                                Pair(it.mark.content.toInt(), it.mark.reason)
                            },
                            { onClick() }, { onClick() }
                        )
                    }
                }
            }

            if (modules.isNotEmpty()) {

                HalfYearRow(
                    num = usedHalfNum,
                    allMarks = marks,
                    firstHalfModules = firstHalfNums
                )

                ModuleView(
                    moduleNum = modules.first().toInt(),
                    marks.filter { it.mark.module == modules.first() },
                    component = component,
                    login = login,
                    isModer = isModer
                )
                Box(
                    Modifier.fillMaxWidth().padding(end = 5.dp),//.offset(y = -5.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    AnimatedContent(
                        if (isFullView.value) "Закрыть" else "Открыть все оценки",
                        transitionSpec = { fadeIn().togetherWith(fadeOut()) }
                    ) {
                        CTextButton(text = it) {
                            isFullView.value = !isFullView.value
                        }
                    }
                }
                AnimatedVisibility(isFullView.value) {
                    val firstFirstNum = modules.firstOrNull { it.toInt() in firstHalfNums }
                    Column {
                        (modules - modules.first()).forEach { x ->
                            if (usedHalfNum != 1 && firstFirstNum == x) {
                                HalfYearRow(
                                    num = 1,
                                    allMarks = marks,
                                    firstHalfModules = firstHalfNums
                                )
                            }
                            ModuleView(
                                moduleNum = x.toInt(),
                                marks = marks.filter { it.mark.module == x },
                                component = component,
                                login = login,
                                isModer = isModer
                            )
                        }
                    }
                }
            } else {
                Text("Пока нет оценок")
            }

//            FlowRow(rowModifier) {
//                marks.forEach {
//                    Box(Modifier.alpha(if (it.mark.groupId != groupId) .2f else 1f)) {
//                        cMark(it.mark, coroutineScope = coroutineScope)
//                    }
//                }
//            }

        }
    }
}

@Composable
private fun HalfYearRow(
    num: Int,
    allMarks: List<UserMarkPlus>,
    firstHalfModules: List<Int>
) {
    val marks =
        allMarks.filter { (num == 2 && it.mark.module.toInt() !in firstHalfModules) || (num == 1 && it.mark.module.toInt() in firstHalfModules) }
    val value = (marks.sumOf { it.mark.content.toInt() }) / (marks.size).toFloat()
    Row {
        Row(
            Modifier.fillMaxWidth().padding(end = 2.dp, start = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "$num полугодие",
                fontWeight = FontWeight.Normal,
                fontSize = 21.esp
            ) //is Quarters None ${if(isQuarters) "модуль" else "полугодие"} TODO
            Text(
                text = if (value.isNaN()) {
                    "NaN"
                } else {
                    value.roundTo(2)
                }, fontWeight = FontWeight.SemiBold, fontSize = MaterialTheme.typography.titleLarge.fontSize
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ModuleView(
    moduleNum: Int,

//    isQuarters: Boolean,
    marks: List<UserMarkPlus>,
    component: AllGroupMarksComponent,
    login: String,
    isModer: Boolean
) {
    val rowModifier = Modifier.fillMaxWidth().padding(top = 5.dp, start = 2.dp)

    val AVGMarks = marks.filter { it.mark.isGoToAvg }
    val value = (AVGMarks.sumOf { it.mark.content.toInt() }) / (AVGMarks.size).toFloat()
    Column(Modifier.fillMaxWidth().padding(top = 5.dp)) {
        Row(
            Modifier.fillMaxWidth().padding(end = 5.dp, start = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "$moduleNum модуль",
                fontWeight = FontWeight.Normal,
                fontSize = MaterialTheme.typography.titleLarge.fontSize
            ) //is Quarters None ${if(isQuarters) "модуль" else "полугодие"} TODO
            Text(
                text = if (value.isNaN()) {
                    "NaN"
                } else {
                    value.roundTo(2)
                }, fontWeight = FontWeight.SemiBold, fontSize = MaterialTheme.typography.titleLarge.fontSize
            )
        }
        FlowRow(rowModifier) {
            marks.forEach {
                Box(Modifier.alpha(if (it.deployLogin != login && isModer) .2f else 1f)) {
                    cMarkPlus(it, component = component, isModer = isModer)
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun cMarkPlus(mark: UserMarkPlus, component: AllGroupMarksComponent, isModer: Boolean) {
    val markSize = 30.dp
    val tState = rememberTooltipState(isPersistent = true)
    TooltipBox(
        state = tState,
        tooltip = {
            PlainTooltip() {
                Text(
                    "${if (isModer) "Выставил ${mark.deployLogin}\nв ${mark.deployDate}-${mark.deployTime}\n" else ""}Об уроке:\n${mark.mark.date} №${mark.mark.reportId}\n${
                        fetchReason(
                            mark.mark.reason
                        )
                    }", textAlign = TextAlign.Center
                )
            }
        },
        positionProvider = popupPositionProvider,
        enableUserInput = true
    ) {
        MarkContent(
            mark.mark.content,
            size = markSize,
//            textYOffset = yOffset,
            addModifier = Modifier.handy().clickable {
                component.onEvent(AllGroupMarksStore.Intent.OpenFullReport(mark.mark.reportId))
            }
//                .combinedClickable(
//                    onDoubleClick = {
//                        println("Double Clicked")
//                    }
//                ) {
//                    coroutineScope.launch {
//                        tState.show()
//                    }
//                }
        )
    }
}

