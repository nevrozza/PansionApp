@file:OptIn(
    ExperimentalLayoutApi::class, ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class, ExperimentalHazeApi::class
)
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.desktop.ui.tooling.preview.utils.GlobalHazeState
import androidx.compose.desktop.ui.tooling.preview.utils.hazeMask
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.GetAsyncIcon
import components.foundation.AppBar
import components.foundation.CFilterChip
import components.foundation.CLazyColumn
import components.foundation.CTextButton
import components.foundation.DefaultErrorView
import components.foundation.DefaultErrorViewPos
import components.foundation.TonalCard
import components.journal.BorderStup
import components.journal.MarkTable
import components.journal.StupsButton
import components.journal.cMark
import components.networkInterface.NetworkState
import components.networkInterface.isLoading
import decomposeComponents.CAlertDialogContent
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeInputScale
import dev.chrisbanes.haze.LocalHazeStyle
import dev.chrisbanes.haze.hazeEffect
import dnevnikRuMarks.DnevnikRuMarkStore
import dnevnikRuMarks.DnevnikRuMarksComponent
import kotlinx.coroutines.CoroutineScope
import report.UserMark
import resources.RIcons
import server.fetchReason
import server.getLocalDate
import server.roundTo
import studentReportDialog.StudentReportDialogStore
import view.LocalViewManager


@ExperimentalMaterial3Api
@ExperimentalLayoutApi
@ExperimentalHazeApi
@Composable
fun DnevnikRuMarkContent(
    component: DnevnikRuMarksComponent
) {

    
    val model by component.model.subscribeAsState()
    val nModel by component.nInterface.networkModel.subscribeAsState()


    LaunchedEffect(Unit) {
        if(!nModel.isLoading) component.onEvent(DnevnikRuMarkStore.Intent.Init)
    }


    val coroutineScope = rememberCoroutineScope()
    val viewManager = LocalViewManager.current

    Scaffold(
        Modifier.fillMaxSize(),
//                .nestedScroll(scrollBehavior.nestedScrollConnection)
        topBar = {
            val isHaze = viewManager.hazeHardware.value
            Column(
                Modifier.then(
                    if (isHaze) Modifier.hazeEffect(
                        state = GlobalHazeState.current,
                        style = LocalHazeStyle.current
                    ) {
                        inputScale = HazeInputScale.Fixed(0.7f)
                        mask = hazeMask//Brush.verticalGradient(colors = listOf(Color.Magenta, Color.Transparent))
//                        progressive = hazeProgressive
                    }
                    else Modifier
                ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AppBar(
                    title = {

                        Text(
                            "Успеваемость",
                            //modifier = Modifier.padding(start = 10.dp),
                            fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                            fontWeight = FontWeight.Black,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    navigationRow = {
                        IconButton(
                            onClick = { component.onOutput(DnevnikRuMarksComponent.Output.Back) }
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
                                    DnevnikRuMarkStore.Intent.ChangeTableView(
                                        !model.isTableView
                                    )
                                )
                            }
                        ) {
                            GetAsyncIcon(
                                path = if (!model.isTableView) RIcons.TABLE else RIcons.CONTACT_BOOK
                            )
                        }
                    },
                    isTransparentHaze = isHaze
                )
                AnimatedVisibility(
                    model.isQuarters != null && !model.isTableView,
                ) {
                    BoxWithConstraints(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        SecondaryScrollableTabRow(
                            selectedTabIndex = (model.tabIndex ?: 0) - 1,
                            divider = {
                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.outline.copy(
                                        alpha = .4f
                                    )
                                )
                            },
                            containerColor = Color.Transparent,
                            edgePadding = 0.dp
                        ) {
                            for (i in 1..model.tabsCount) {
                                val tabText =
                                    "$i ${if (model.isQuarters == true) "модуль" else "полугодие"}"
                                Tab(
                                    selected = ((model.tabIndex ?: 0) - 1) == i,
                                    onClick = {
//                                    if (((model.tabIndex ?: 0) - 1) != i) {
                                        component.onEvent(DnevnikRuMarkStore.Intent.ClickOnTab(i))
//                                    }
                                    },
                                    text = {
                                        Text(
                                            tabText,
                                            overflow = TextOverflow.Ellipsis,
                                            maxLines = 2
                                        )
                                    },
                                    modifier = Modifier.width(
                                        ((this@BoxWithConstraints.maxWidth / model.tabsCount.toFloat()) - 1.dp).coerceAtLeast(
                                            100.dp
                                        )
                                    )
                                )
                            }
                        }
                    }
                }
            }
            //LessonReportTopBar(component, isFullView) //, scrollBehavior
        }
    ) { padding ->
        Crossfade(nModel.state) {
            when (it) {
                NetworkState.None -> Crossfade(model.isTableView) { crossfadeState ->
                    if (crossfadeState) {

                        Box(
                            Modifier.fillMaxSize().padding(padding),
                            contentAlignment = Alignment.Center
                        ) {
                            Column {
                                Row(Modifier.horizontalScroll(rememberScrollState())) {
                                    CFilterChip(
                                        label = "За неделю",
                                        isSelected = model.isWeekDays,
                                        state = nModel.state,
                                        coroutineScope = coroutineScope
                                    ) {
                                        component.onEvent(
                                            DnevnikRuMarkStore.Intent.OpenWeek
                                        )
                                    }
                                    Spacer(Modifier.width(5.dp))
                                    CFilterChip(
                                        label = "За прошлую неделю",
                                        isSelected = model.isPreviousWeekDays,
                                        state = nModel.state,
                                        coroutineScope = coroutineScope
                                    ) {
                                        component.onEvent(
                                            DnevnikRuMarkStore.Intent.OpenPreviousWeek
                                        )
                                    }
                                    Spacer(Modifier.width(5.dp))
                                    (1..model.tabsCount).forEach { modle ->
                                        CFilterChip(
                                            label = "За ${modle} ${if (model.isQuarters == true) "модуль" else "полугодие"}",
                                            isSelected = (model.tabIndex
                                                ?: 0) == modle && !model.isWeekDays && !model.isPreviousWeekDays,
                                            state = nModel.state,
                                            coroutineScope = coroutineScope
                                        ) {
                                            component.onEvent(
                                                DnevnikRuMarkStore.Intent.ClickOnTab(
                                                    modle
                                                )
                                            )
                                        }
                                        Spacer(Modifier.width(5.dp))
                                    }
                                }
                                MarkTable(
                                    fields = model.tableSubjects.associate { it.subjectId.toString() to it.subjectName },
                                    dms = model.mDateMarks,
                                    nki = model.tableSubjects.associate { it.subjectId.toString() to it.nki.filter {
                                        when {
                                            model.isWeekDays -> it.date in model.weekDays
                                            model.isPreviousWeekDays -> it.date in model.previousWeekDays
                                            else -> it.date in model.mDates
                                        }
                                    } },
                                    isDs1Init = component.settingsRepository.fetchIsShowingPlusDS()
                                )
                            }
                        }
                    } else {
                        CLazyColumn(
                            padding = PaddingValues(
                                top = padding.calculateTopPadding(),
                                bottom = padding.calculateBottomPadding()
                            )
                        ) {
                            items(model.subjects[(model.tabIndex ?: 0)] ?: listOf()) {
                                SubjectMarksItem(
                                    title = it.subjectName,
                                    marks = it.marks.sortedBy { getLocalDate(it.date).toEpochDays() }
                                        .reversed(),//.sortedBy { it.date }.reversed(),
                                    stupsCount = it.stups.filter {
                                        it.reason.subSequence(
                                            0,
                                            3
                                        ) != "!ds"
                                    }.sumOf { it.content.toIntOrNull() ?: 0 },
                                    coroutineScope = coroutineScope,
                                    component = component,
                                    subjectId = it.subjectId,
                                    isQuarters = model.isQuarters ?: true
                                )
                            }
                        }
                    }
                }

                //.map {
                //                                        Mark(
                //                                            value = it.content.toInt(),
                //                                            reason = it.reason,
                //                                            isGoToAvg = it.isGoToAvg,
                //                                            id = it.id,
                //                                            date = it.date
                //                                        )
                //                                    }

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


        CAlertDialogContent(
            component = component.stupsDialogComponent,
            title = "Ступени: ${model.subjects[model.tabIndex]?.firstOrNull { it.subjectId == model.pickedSubjectId }?.subjectName}",
            titleXOffset = 5.dp
        ) {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                model.subjects[model.tabIndex]?.firstOrNull { it.subjectId == model.pickedSubjectId }?.stups?.sortedBy {
                    getLocalDate(
                        it.date
                    ).toEpochDays()
                }?.reversed()?.forEach {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 1.dp)
                            .padding(horizontal = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(it.date)
                        Text(fetchReason(it.reason))
                        BorderStup(it.content, reason = it.reason)
                    }
                }
            }
        }
    }

    StudentReportDialogContent(
        component = component.studentReportDialog
    )


}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SubjectMarksItem(
    title: String,
    marks: List<UserMark>,
    stupsCount: Int,
    coroutineScope: CoroutineScope,
    isQuarters: Boolean,
    component: DnevnikRuMarksComponent,
    subjectId: Int
) {
    val isFullView = remember { mutableStateOf(false) }

    val rowModifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp)

    val AVGMarks = marks.filter { it.isGoToAvg }
    val value = (AVGMarks.sumOf { it.content.toInt() }) / (AVGMarks.size).toFloat()

    val modules = marks.map { it.module.toInt() }.toSet().sortedByDescending { it }

    TonalCard(
        Modifier.fillMaxWidth().padding(top = 10.dp)
    ) {
        Column(Modifier.padding(5.dp).padding(start = 5.dp).animateContentSize()) {
            Row(
                Modifier.fillMaxWidth().padding(end = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(2f, false)
                ) {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Medium,
                        fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(2f, false)
                    )
                    if (stupsCount != 0) {
                        Spacer(Modifier.width(5.dp))
                        Box(Modifier.weight(.4f, false)) {
                            StupsButton(
                                stupsCount
                            ) {
                                component.onEvent(
                                    DnevnikRuMarkStore.Intent.ClickOnStupsSubject(
                                        subjectId
                                    )
                                )
                            }
                        }
                    }
                }
                Text(
                    text = if (value.isNaN()) {
                        "NaN"
                    } else {
                        value.roundTo(2)
                    }, fontWeight = FontWeight.Bold, fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(.4f, false)
                )
            }
            //Pansion – StudentLinesContent.kt [Pansion.COMMON.journal.compose.commonMain]
            if (marks.isNotEmpty()) {
                if (!isFullView.value) {
                    LazyRow(rowModifier, userScrollEnabled = false) {
                        items(marks) {
                            cMark(it, coroutineScope = coroutineScope) {
                                component.studentReportDialog.onEvent(
                                    StudentReportDialogStore.Intent.OpenDialog(
                                        login = component.model.value.studentLogin,
                                        reportId = it.reportId
                                    )
                                )
                            }
                        }
                    }
                } else {
                    if (!isQuarters) {
                        modules.forEach { module ->
                            val m = marks.filter { it.module.toInt() == module }
                            Column {
                                ModuleRow(
                                    num = module,
                                    marks = m
                                )
                                FlowRow(rowModifier) {
                                    m.forEach {
                                        cMark(it, coroutineScope = coroutineScope) {
                                            component.studentReportDialog.onEvent(
                                                StudentReportDialogStore.Intent.OpenDialog(
                                                    login = component.model.value.studentLogin,
                                                    reportId = it.reportId
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        FlowRow(rowModifier) {
                            marks.forEach {
                                cMark(it, coroutineScope = coroutineScope) {
                                    component.studentReportDialog.onEvent(
                                        StudentReportDialogStore.Intent.OpenDialog(
                                            login = component.model.value.studentLogin,
                                            reportId = it.reportId
                                        )
                                    )
                                }
                            }
                        }
                    }

                }

                Box(
                    Modifier.fillMaxWidth().padding(end = 5.dp),
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
            }

        }

    }
}


@Composable
private fun ModuleRow(
    num: Int,
    marks: List<UserMark>
) {
    val value = (marks.sumOf { it.content.toInt() }) / (marks.size).toFloat()
    Row {
        Row(
            Modifier.fillMaxWidth().padding(end = 5.dp, start = 5.dp, top = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "$num модуль",
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
    }
}