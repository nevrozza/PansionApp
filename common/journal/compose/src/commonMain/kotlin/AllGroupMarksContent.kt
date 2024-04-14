@file:OptIn(
    ExperimentalLayoutApi::class, ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class
)

import allGroupMarks.AllGroupMarksComponent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BasicTooltipBox
import androidx.compose.foundation.BasicTooltipState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LocalPolice
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.ThumbDown
import androidx.compose.material.icons.rounded.ThumbsUpDown
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.UnfoldLess
import androidx.compose.material.icons.rounded.UnfoldMore
import androidx.compose.material.icons.rounded.ViewWeek
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TooltipState
import androidx.compose.material3.rememberTooltipState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isPrimaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupPositionProvider
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.value.getValue
import components.AnimatedElevatedButton
import components.AppBar
import components.CLazyColumn
import components.CustomTextButton
import components.CustomTextField
import components.MarkContent
import components.ReportTitle
import components.ScrollBaredBox
import components.TeacherTime
import components.cAlertDialog.CAlertDialogStore
import components.cBottomSheet.CBottomSheetStore
import components.listDialog.ListDialogStore
import components.networkInterface.NetworkState
import decomposeComponents.CAlertDialogContent
import decomposeComponents.CBottomSheetContent
import decomposeComponents.ListDialogContent
import dnevnikRuMarks.DnevnikRuMarkStore
import dnevnikRuMarks.DnevnikRuMarksComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import lessonReport.ColumnTypes
import lessonReport.LessonReportComponent
import lessonReport.LessonReportStore
import lessonReport.Mark
import lessonReport.MarkColumn
import lessonReport.Stup
import lessonReport.opozdanie
import lessonReport.prisut
import lessonReport.srBall
import pullRefresh.PullRefreshIndicator
import pullRefresh.rememberPullRefreshState
import report.UserMark
import server.roundTo
import view.LocalViewManager
import view.LockScreenOrientation
import view.WindowScreen
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
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val viewManager = LocalViewManager.current
//    val scrollState = rememberScrollState()
    val imeState = rememberImeState()
    val lazyListState = rememberLazyListState()
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
                                                fontWeight = FontWeight.Bold,
                                            )
                                        ) {
                                            append(model.subjectName)
                                        }
                                    },
                                    overflow = TextOverflow.Ellipsis,
                                    fontSize = bigTextSize,
                                    maxLines = 1,
                                    style = TextStyle(
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
                                            fontWeight = FontWeight.Bold,
                                        )
                                    ) {
                                        append(model.groupName)
                                    }

                                },
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                style = TextStyle(
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
                            if(model.students.isNotEmpty()) {
                                items(model.students) { s ->
                                    AllGroupMarksStudentItem(
                                        title = s.shortFIO,
                                        groupId = model.groupId,
                                        marks = s.marks.sortedBy { it.date }.reversed(),
                                        stups = s.stups,
                                        coroutineScope = coroutineScope
                                    )
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

        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AllGroupMarksStudentItem(
    title: String,
    groupId: Int,
    marks: List<UserMark>,
    stups: List<UserMark>,
//    stupsCount: Int,
    coroutineScope: CoroutineScope
) {
//    val isFullView = remember { mutableStateOf(false) }

    val rowModifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp).padding(top = 5.dp)

    val AVGMarks = marks.filter { it.isGoToAvg }
    val value = (AVGMarks.sumOf { it.content.toInt() }) / (AVGMarks.size).toFloat()

    ElevatedCard(
        Modifier.fillMaxWidth().padding(horizontal = 10.dp).padding(top = 10.dp)
            .animateContentSize().clip(CardDefaults.elevatedShape)
    ) {
//            .clickable {
//                isFullView.value = !isFullView.value
//            }) {
        Column(Modifier.padding(5.dp).padding(start = 5.dp)) {
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
                    FilledTonalButton(
                        onClick = {},
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.height(20.dp).offset(y = 2.dp)
                    ) {
                        Text(
                            "+${
                                stups.filter { it.reason.subSequence(0, 3) != "!ds" }
                                    .sumOf { it.content.toInt() }
                            }", modifier = Modifier.offset(x = -2.dp)
                        )
                    }
                    Spacer(Modifier.width(5.dp))
                    OutlinedButton(
                        onClick = {},
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.height(20.dp).offset(y = 2.dp),
                    ) {
                        Text(
                            "+${
                                stups.filter { it.reason.subSequence(0, 3) == "!ds" }
                                    .sumOf { it.content.toInt() }
                            }", modifier = Modifier.offset(x = -2.dp)
                        )
                    }
                }
                Text(
                    text = if (value.isNaN()) {
                        "NaN"
                    } else {
                        value.roundTo(2).toString()
                    }, fontWeight = FontWeight.Bold, fontSize = 25.sp
                )
            }
//            if (!isFullView.value) {
//                LazyRow(rowModifier, userScrollEnabled = false) {
//                    items(marks) {
//                        cMark(it, coroutineScope = coroutineScope)
//                    }
//                }
//            } else {
            FlowRow(rowModifier) {
                marks.forEach {
                    Box(Modifier.alpha(if (it.groupId != groupId) .2f else 1f)) {
                        cMark(it, coroutineScope = coroutineScope)
                    }
                }
            }
//            }

        }
    }
}


