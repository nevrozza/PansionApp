@file:OptIn(
    ExperimentalLayoutApi::class, ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class
)

import allGroupMarks.AllGroupMarksComponent
import allGroupMarks.AllGroupMarksStore
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
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.AppBar
import components.BorderStup
import components.CLazyColumn
import components.CustomTextButton
import components.MarkContent
import components.StupsButtons
import components.networkInterface.NetworkState
import decomposeComponents.CAlertDialogContent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import lessonReport.LessonReportStore
import report.UserMark
import report.UserMarkPlus
import server.fetchReason
import server.getLocalDate
import server.roundTo
import view.LocalViewManager
import view.handy
import view.rememberImeState

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
    val focusManager = LocalFocusManager.current
    val viewManager = LocalViewManager.current
//    val scrollState = rememberScrollState()
    val imeState = rememberImeState()
    val lazyListState = rememberLazyListState()

    if(model.reportData != null) {
        val reportData = model.reportData
        component.onEvent(AllGroupMarksStore.Intent.DeleteReport)
        component.onOutput(AllGroupMarksComponent.Output.OpenReport(reportData!!))
    }



    //PullToRefresh
//    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    BoxWithConstraints {

        val isFullView by mutableStateOf(this.maxWidth > 600.dp)
        println(isFullView)
        Scaffold(
            Modifier.fillMaxSize(),
//                .nestedScroll(scrollBehavior.nestedScrollConnection)
            topBar = {
                AppBar(
                    navigationRow = {
                        IconButton(
                            onClick = { component.onOutput(AllGroupMarksComponent.Output.BackToHome) }
                        ) {
                            Icon(
                                Icons.Rounded.ArrowBackIosNew, null
                            )
                        }
                    },
                    title = {
                        val bigTextSize = 20.sp// if (!isLarge) else 40.sp
                        val smallTextSize = 14.sp//if (!isLarge)  else 28.sp

                        Column(
                            Modifier.padding(horizontal = 3.dp)
                        ) {
                            Row {
                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(
                                            SpanStyle(
                                                fontWeight = FontWeight.Bold
                                            )
                                        ) {
                                            append(model.subjectName)
                                        }
                                    },
                                    overflow = TextOverflow.Ellipsis,
                                    fontSize = bigTextSize,
                                    maxLines = 1,
                                    style = androidx.compose.material3.LocalTextStyle.current.copy(
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
                                            fontWeight = FontWeight.Bold
                                        )
                                    ) {
                                        append(model.groupName)
                                    }

                                },
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                style = androidx.compose.material3.LocalTextStyle.current.copy(
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
                    isHaze = true
                )
                //LessonReportTopBar(component, isFullView) //, scrollBehavior
            }
        ) { padding ->
            Column(Modifier.fillMaxSize()) {
                Crossfade(nModel.state) { state ->
                    when (state) {
                        NetworkState.None -> CLazyColumn(padding = padding) {
                            if (model.students.isNotEmpty()) {
                                items(model.students) { s ->

                                    AllGroupMarksStudentItem(
                                        title = s.shortFIO,
                                        groupId = model.groupId,
                                        marks = s.marks.sortedBy { getLocalDate(it.mark.date).toEpochDays() }
                                            .reversed(),
                                        stups = s.stups,
                                        isQuarters = s.isQuarters,
                                        modifier = Modifier.padding(top = if (model.students.first() == s) 0.dp else 10.dp),
                                        coroutineScope = coroutineScope,
                                        component = component,
                                        login = model.login
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
                            BorderStup(it.mark.content)
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
                        NetworkState.Error -> Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Не удалось загрузить отчёт")
                            CustomTextButton("Закрыть") {
                                component.nOpenReportInterface.goToNone()
                            }
                        }

                        else -> CircularProgressIndicator(Modifier.padding(10.dp))
                    }
                }
            }
        }
    }


}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AllGroupMarksStudentItem(
    title: String,
    groupId: Int,
    marks: List<UserMarkPlus>,
    stups: List<UserMarkPlus>,
    isQuarters: Boolean,
//    stupsCount: Int,
    coroutineScope: CoroutineScope,
    modifier: Modifier = Modifier,
    component: AllGroupMarksComponent,
    login: String,
    onClick: () -> Unit
) {
    val isFullView = remember { mutableStateOf(false) }


    val modules = marks.map { it.module }.toSet().sorted().reversed()


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
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(title, fontWeight = FontWeight.Bold, fontSize = 25.sp)

                    StupsButtons(
                        stups = stups.map {
                            Pair(it.mark.content.toInt(), it.mark.reason)
                        },
                        { onClick() }, { onClick() }
                    )
                }
            }

            if (modules.isNotEmpty()) {
                ModuleView(
                    moduleNum = modules.first().toInt(),
                    isQuarters = isQuarters,
                    marks.filter { it.module == modules.first() },
                    groupId = groupId,
                    coroutineScope = coroutineScope,
                    component = component,
                    login = login
                )
                Box(
                    Modifier.fillMaxWidth().padding(end = 5.dp),//.offset(y = -5.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    AnimatedContent(
                        if (isFullView.value) "Закрыть" else "Открыть все оценки",
                        transitionSpec = { fadeIn().togetherWith(fadeOut()) }
                    ) {
                        CustomTextButton(text = it) {
                            isFullView.value = !isFullView.value
                        }
                    }
                }
                AnimatedVisibility(isFullView.value) {
                    (modules - modules.first()).forEach { x ->
                        ModuleView(
                            moduleNum = x.toInt(),
                            isQuarters = isQuarters,
                            marks = marks.filter { it.module == x },
                            groupId = groupId,
                            coroutineScope = coroutineScope,
                            component = component,
                            login = login
                        )
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
private fun ModuleView(
    moduleNum: Int,
    isQuarters: Boolean,
    marks: List<UserMarkPlus>,
    groupId: Int,
    coroutineScope: CoroutineScope,
    component: AllGroupMarksComponent,
    login: String
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
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            ) //is Quarters None ${if(isQuarters) "модуль" else "полугодие"} TODO
            Text(
                text = if (value.isNaN()) {
                    "NaN"
                } else {
                    value.roundTo(2).toString()
                }, fontWeight = FontWeight.SemiBold, fontSize = 20.sp
            )
        }
        FlowRow(rowModifier) {
            marks.forEach {
                Box(Modifier.alpha(if (it.deployLogin != login) .2f else 1f)) {
                    cMarkPlus(it, component = component)
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun cMarkPlus(mark: UserMarkPlus, component: AllGroupMarksComponent) {
    val markSize = 30.dp
    val yOffset = 2.dp
    val tState = rememberTooltipState(isPersistent = true)
    TooltipBox(
        state = tState,
        tooltip = {
            PlainTooltip() {
                Text(
                    "Выставил ${mark.deployLogin}\nв ${mark.deployDate}-${mark.deployTime}\nОб уроке:\n${mark.mark.date} №${mark.mark.reportId}\n${
                        fetchReason(
                            mark.mark.reason
                        )
                    }", textAlign = TextAlign.Center
                )
            }
        },
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        enableUserInput = true
    ) {
        MarkContent(
            mark.mark.content,
            size = markSize,
            textYOffset = yOffset,
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

