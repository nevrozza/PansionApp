@file:OptIn(
    ExperimentalLayoutApi::class, ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class
)

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.AppBar
import components.CLazyColumn
import components.CustomTextButton
import components.MarkContent
import components.StupsButton
import components.StupsButtons
import components.networkInterface.NetworkState
import dev.chrisbanes.haze.hazeChild
import dnevnikRuMarks.DnevnikRuMarkStore
import dnevnikRuMarks.DnevnikRuMarksComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import report.UserMark
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
fun DnevnikRuMarkContent(
    component: DnevnikRuMarksComponent
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
            val isHaze = viewManager.hazeStyle != null
            Column(
                Modifier.then(
                    if (isHaze) Modifier.hazeChild(
                        state = viewManager.hazeState,
                        style = viewManager.hazeStyle!!.value
                    )
                    else Modifier
                ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AppBar(
                    containerColor = if (isHaze) Color.Transparent else MaterialTheme.colorScheme.surface,
                    navigationRow = {
                        IconButton(
                            onClick = { component.onOutput(DnevnikRuMarksComponent.Output.BackToHome) }
                        ) {
                            Icon(
                                Icons.Rounded.ArrowBackIosNew, null
                            )
                        }
                    },
                    title = {

                        Text(
                            "Успеваемость",
                            //modifier = Modifier.padding(start = 10.dp),
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Black,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                )
                AnimatedVisibility(
                    model.isQuarters != null,
//                    modifier = Modifier.then(
//                        if (viewManager.hazeState != null && viewManager.hazeStyle != null) Modifier.hazeChild(
//                            state = viewManager.hazeState!!.value,
//                            style = viewManager.hazeStyle!!.value
//                        )
//                        else Modifier
//                    )
                ) {
                    BoxWithConstraints(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
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
                                    modifier = Modifier.width(((this@BoxWithConstraints.maxWidth / model.tabsCount.toFloat()) - 1.dp).coerceAtLeast(100.dp))
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
                NetworkState.None -> CLazyColumn(
                    padding = PaddingValues(
                        top = padding.calculateTopPadding(),
                        bottom = padding.calculateBottomPadding()
                    )
                ) {
                    items(model.subjects[(model.tabIndex ?: 0)] ?: listOf()) {
                        SubjectMarksItem(
                            title = it.subjectName,
                            marks = it.marks.sortedBy { getLocalDate(it.date).toEpochDays() }.reversed(),//.sortedBy { it.date }.reversed(),
                            stupsCount = it.stupCount,
                            coroutineScope = coroutineScope
                        )
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

private data class TabData(
    val index: Int,
    val text: String,
    val onClick: () -> Unit
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SubjectMarksItem(
    title: String,
    marks: List<UserMark>,
    stupsCount: Int,
    coroutineScope: CoroutineScope
) {
    val isFullView = remember { mutableStateOf(false) }

    val rowModifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp).padding(top = 5.dp)

    val AVGMarks = marks.filter { it.isGoToAvg }
    val value = (AVGMarks.sumOf { it.content.toInt() }) / (AVGMarks.size).toFloat()

    ElevatedCard(
        Modifier.fillMaxWidth().padding(top = 10.dp) //.padding(horizontal = 10.dp)
            .clip(CardDefaults.elevatedShape)) {
        Column(Modifier.padding(5.dp).padding(start = 5.dp).animateContentSize()) {
            Row(
                Modifier.fillMaxWidth().padding(end = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(title, fontWeight = FontWeight.Bold, fontSize = 25.sp)
                    Spacer(Modifier.width(5.dp))
                    StupsButton(
                        stupsCount
                    ) {}
                }
                Text(
                    text = if (value.isNaN()) {
                        "NaN"
                    } else {
                        value.roundTo(2).toString()
                    }, fontWeight = FontWeight.Bold, fontSize = 25.sp
                )
            }
            if (!isFullView.value) {
                LazyRow(rowModifier, userScrollEnabled = false) {
                    items(marks) {
                        cMark(it, coroutineScope = coroutineScope)
                    }
                }
            } else {
                FlowRow(rowModifier) {
                    marks.forEach {
                        cMark(it, coroutineScope = coroutineScope)
                    }
                }
            }

            if(marks.isNotEmpty()) {
                Box(
                    Modifier.fillMaxWidth().padding(end = 5.dp),
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
            }

        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun cMark(mark: UserMark, coroutineScope: CoroutineScope) {
    val markSize = 30.dp
    val yOffset = 2.dp
    val tState = rememberTooltipState(isPersistent = false)
    TooltipBox(
        state = tState,
        tooltip = {
            PlainTooltip(modifier = Modifier.clickable {}) {
                println(mark.reason)
                Text("${mark.date}\n${fetchReason(mark.reason)}", textAlign = TextAlign.Center)
            }
        },
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider()
    ) {
        MarkContent(
            mark.content,
            size = markSize,
            textYOffset = yOffset,
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