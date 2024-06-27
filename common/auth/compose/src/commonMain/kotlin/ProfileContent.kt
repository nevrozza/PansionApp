@file:OptIn(
    ExperimentalLayoutApi::class, ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class
)

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOut
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CheckCircleOutline
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aay.compose.barChart.BarChart
import com.aay.compose.barChart.model.BarParameters
import com.aay.compose.donutChart.DonutChart
import com.aay.compose.donutChart.PieChart
import com.aay.compose.donutChart.model.PieChartData
import com.aay.compose.radarChart.RadarChart
import com.aay.compose.radarChart.model.NetLinesStyle
import com.aay.compose.radarChart.model.Polygon
import com.aay.compose.radarChart.model.PolygonStyle
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.AppBar
import components.CLazyColumn
import components.CustomTextButton
import components.GetAvatar
import components.MarkContent
import components.hazeHeader
import components.networkInterface.NetworkState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import profile.ProfileComponent
import profile.ProfileStore
import report.UserMark
import resources.Images
import resources.getAvatarImageVector
import server.fetchReason
import server.roundTo
import view.LocalViewManager
import view.handy
import view.rememberImeState

@OptIn(ExperimentalFoundationApi::class)
@ExperimentalMaterial3Api
@ExperimentalLayoutApi
@Composable
fun ProfileContent(
    component: ProfileComponent
) {
    val model by component.model.subscribeAsState()
    val nModel by component.nInterface.networkModel.subscribeAsState()
    val nAvatarModel by component.nAvatarInterface.networkModel.subscribeAsState()
    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val viewManager = LocalViewManager.current
//    val scrollState = rememberScrollState()
    val imeState = rememberImeState()
    val lazyListState = rememberLazyListState()

    val isFullHeader = !lazyListState.canScrollBackward || model.tabIndex == 2
    val headerAvatar = if (model.tabIndex == 2) model.newAvatarId else model.avatarId

    //PullToRefresh
//    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        Modifier.fillMaxSize(),
//                .nestedScroll(scrollBehavior.nestedScrollConnection)
        topBar = {
            Column(
                Modifier
                    .hazeHeader(viewManager)
            ) {
                AppBar(
                    navigationRow = {
                        IconButton(
                            onClick = { component.onOutput(ProfileComponent.Output.BackToHome) }
                        ) {
                            Icon(
                                Icons.Rounded.ArrowBackIosNew, null
                            )
                        }
                    },
                    title = {
                        Box(modifier = Modifier.fillMaxWidth().padding(end = 10.dp)) {
                            AnimatedContent(
                                if (!isFullHeader) model.fio.name else "Профиль",
                                modifier = Modifier.align(Alignment.CenterStart)
                            ) {
                                Text(
                                    it,//"Успеваемость",
                                    fontSize = 25.sp,
                                    fontWeight = FontWeight.Black,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            this@Column.AnimatedVisibility(
                                !isFullHeader,
                                enter = fadeIn() + expandVertically(
                                    expandFrom = Alignment.Top, clip = false
                                ),
                                exit = fadeOut() + shrinkVertically(
                                    shrinkTowards = Alignment.Top, clip = false
                                ),
                                modifier = Modifier.align(Alignment.Center)
                                    .offset(x = -17.5.dp, y = 2.dp)
                            ) {
                                GetAvatar(
                                    avatarId = headerAvatar,
                                    name = model.fio.name,
                                    size = 40.dp,
                                    textSize = 15.sp
                                )
                            }

                        }
                    },
                    isHaze = false,
                    isTransparentHaze = true
                )
                AnimatedVisibility(
                    isFullHeader,
                    enter = fadeIn() + expandVertically(clip = false) + scaleIn(),
                    exit = fadeOut() + shrinkVertically(clip = false) + scaleOut(),
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        GetAvatar(
                            avatarId = headerAvatar,
                            name = model.fio.name,
                            size = 150.dp,
                            textSize = 75.sp
                        )
                        Spacer(Modifier.height(15.dp))
                        Text(
                            text = "${model.fio.surname} ${model.fio.name} ${if (model.fio.praname.isNullOrEmpty()) "" else "\n${model.fio.praname}"}",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 10.dp).fillMaxWidth(),
                            fontWeight = FontWeight.Black,
                            fontSize = 25.sp
                        )

                        Spacer(Modifier.height(5.dp)) //3.dp
//            HorizontalDivider(Modifier.width(340.dp).height(1.dp).padding(vertical = 15.dp, horizontal = 30.dp), color = MaterialTheme.colorScheme.onBackground.copy(alpha = .1f))
                    }
                }
                TabRow( //Scrollable
                    selectedTabIndex = model.tabIndex,
                    containerColor = Color.Transparent
                ) {
                    for (i in (0..2)) {
                        val text = when (i) {
                            0 -> "Обо мне"
                            1 -> "Статистика"
                            else -> "Аватарки"
                        }
                        Tab(
                            selected = model.tabIndex == i,
                            onClick = {
                                component.onEvent(ProfileStore.Intent.ChangeTab(i))
                            },
                            text = {
                                Text(
                                    text = text,
                                    maxLines = 1
                                )
                            }
                        )
                    }
                }
            }
            //LessonReportTopBar(component, isFullView) //, scrollBehavior
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible =
                model.avatarId != model.newAvatarId,
                enter = slideInVertically(initialOffsetY = { it * 2 }),
                exit = slideOutVertically(targetOffsetY = { it * 2 }),
            ) {
                Crossfade(nAvatarModel.state) {
                    SmallFloatingActionButton(
                        onClick = {
                            if (it != NetworkState.Loading && model.avatarId != model.newAvatarId) {
                                 component.onEvent(ProfileStore.Intent.SaveAvatarId)
                            }
                        }
                    ) {
                        when (it) {
                            NetworkState.None -> {
                                Icon(
                                    Icons.Rounded.Save,
                                    null
                                )
                            }

                            NetworkState.Loading -> {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp))
                            }

                            NetworkState.Error -> {
                                Text(nAvatarModel.error)
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        Crossfade(nModel.state) {
            when (it) {
                NetworkState.None -> CLazyColumn(
                    padding = PaddingValues(
                        top = padding.calculateTopPadding(),
                        bottom = padding.calculateBottomPadding()
                    ),
                    state = lazyListState
                ) {
                    when (model.tabIndex) {
                        0 -> {}
                        1 -> {
                            item {
                                BarChartPreview()
                            }
                            item {
                                PieChartPreview()
                            }
                            item {
                                DonutChartPreview()
                            }
                            item {
                                RadarChartPreview()
                            }
                        }

                        else -> {
                            item {
                                FlowRow(
                                    Modifier.fillMaxWidth().padding(top = 10.dp),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    for (i in Images.Avatars.avatarIds) {
                                        AvatarButton(
                                            currentAvatar = headerAvatar,
                                            i = i,
                                            name = model.fio.name
                                        ) {
                                            component.onEvent(ProfileStore.Intent.SetNewAvatarId(i))
                                        }
                                    }
                                }
                            }
                        }
                    }
//                        items(model.subjects) {
//                            SubjectMarksItem(
//                                title = it.subjectName,
//                                marks = it.marks.sortedBy { it.date }.reversed(),
//                                stupsCount = it.stupCount,
//                                coroutineScope = coroutineScope
//                            )
//                        }
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

@Composable
private fun AvatarButton(currentAvatar: Int, i: Int, name: String, onClick: () -> Unit) {
    Box() {
        GetAvatar(
            avatarId = i,
            name = name,
            modifier = Modifier.padding(5.dp).clip(CircleShape).clickable { onClick() }
        )
        if (currentAvatar == i) {
            Icon(
                Icons.Rounded.CheckCircleOutline,
                null,
                modifier = Modifier.align(Alignment.BottomEnd)
                    .padding(5.dp).background(
                        MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    ),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}


@Composable
private fun BarChartPreview() {

    val testBarParameters: List<BarParameters> = listOf(
        BarParameters(
            dataName = "Completed",
            data = listOf(0.6, 10.6, 80.0, 50.6, 44.0, 100.6, 10.0),
            barColor = Color(0xFF6C3428)
        ),
        BarParameters(
            dataName = "Completed",
            data = listOf(50.0, 30.6, 77.0, 69.6, 50.0, 30.6, 80.0),
            barColor = Color(0xFFBA704F),
        ),
        BarParameters(
            dataName = "Completed",
            data = listOf(100.0, 99.6, 60.0, 80.6, 10.0, 100.6, 55.99),
            barColor = Color(0xFFDFA878),
        ),
    )

    Box(Modifier.fillMaxWidth().height(300.dp)) {
        BarChart(
            chartParameters = testBarParameters,
            gridColor = Color.DarkGray,
            xAxisData = listOf("2016", "2017", "2018", "2019", "2020", "2021", "2022"),
            isShowGrid = true,
            animateChart = true,
            showGridWithSpacer = false,
            yAxisStyle = LocalTextStyle.current.copy(
                fontSize = 14.sp,
                color = Color.DarkGray,
            ),
            xAxisStyle = LocalTextStyle.current.copy(
                fontSize = 14.sp,
                color = Color.DarkGray,
                fontWeight = FontWeight.W400
            ),
            yAxisRange = 15,
            barWidth = 20.dp,
            barCornerRadius = 15.dp,

            )
    }
}

@Composable
private fun PieChartPreview() {
    val testPieChartData: List<PieChartData> = listOf(
        PieChartData(
            partName = "part A",
            data = 500.0,
            color = Color(0xFF22A699),
        ),
        PieChartData(
            partName = "Part B",
            data = 700.0,
            color = Color(0xFFF2BE22),
        ),
        PieChartData(
            partName = "Part C",
            data = 500.0,
            color = Color(0xFFF29727),
        ),
        PieChartData(
            partName = "Part D",
            data = 100.0,
            color = Color(0xFFF24C3D),
        ),
    )

    PieChart(
        modifier = Modifier.fillMaxWidth().height(300.dp),
        pieChartData = testPieChartData,
        ratioLineColor = Color.LightGray,
        textRatioStyle = LocalTextStyle.current.copy(color = Color.Gray),
    )
}

@Composable
private fun DonutChartPreview() {
    val testPieChartData: List<PieChartData> = listOf(
        PieChartData(
            partName = "part A",
            data = 500.0,
            color = Color(0xFF0B666A),
        ),
        PieChartData(
            partName = "Part B",
            data = 700.0,
            color = Color(0xFF35A29F),
        ),
        PieChartData(
            partName = "Part C",
            data = 500.0,
            color = Color(0xFF97FEED),
        ),
        PieChartData(
            partName = "Part D",
            data = 100.0,
            color = Color(0xFF071952),
        ),
    )

    DonutChart(
        modifier = Modifier.fillMaxWidth().height(300.dp),
        pieChartData = testPieChartData,
        centerTitle = "Orders",
        centerTitleStyle = LocalTextStyle.current.copy(color = Color(0xFF071952)),
        outerCircularColor = Color.LightGray,
        innerCircularColor = Color.Gray,
        ratioLineColor = Color.LightGray,
    )
}

@Composable
private fun RadarChartPreview() {
    val radarLabels =
        listOf(
            "Party A",
            "Party A",
            "Party A",
            "Part A",
            "Party A",
            "Party A",
            "Party A",
            "Party A",
            "Party A"
        )
    val values2 = listOf(120.0, 160.0, 110.0, 112.0, 200.0, 120.0, 145.0, 101.0, 200.0)
    val values = listOf(180.0, 180.0, 165.0, 135.0, 120.0, 150.0, 140.0, 190.0, 200.0)
    val labelsStyle = LocalTextStyle.current.copy(
        color = Color.Black,
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp
    )

    val scalarValuesStyle = LocalTextStyle.current.copy(
        color = Color.Black,
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp
    )

    RadarChart(
        modifier = Modifier.fillMaxWidth().height(300.dp),
        radarLabels = radarLabels,
        labelsStyle = labelsStyle,
        netLinesStyle = NetLinesStyle(
            netLineColor = Color(0x90ffD3CFD3),
            netLinesStrokeWidth = 2f,
            netLinesStrokeCap = StrokeCap.Butt
        ),
        scalarSteps = 2,
        scalarValue = 200.0,
        scalarValuesStyle = scalarValuesStyle,
        polygons = listOf(
            Polygon(
                values = values,
                unit = "$",
                style = PolygonStyle(
                    fillColor = Color(0xffc2ff86),
                    fillColorAlpha = 0.5f,
                    borderColor = Color(0xffe6ffd6),
                    borderColorAlpha = 0.5f,
                    borderStrokeWidth = 2f,
                    borderStrokeCap = StrokeCap.Butt,
                )
            ),
            Polygon(
                values = values2,
                unit = "$",
                style = PolygonStyle(
                    fillColor = Color(0xffFFDBDE),
                    fillColorAlpha = 0.5f,
                    borderColor = Color(0xffFF8B99),
                    borderColorAlpha = 0.5f,
                    borderStrokeWidth = 2f,
                    borderStrokeCap = StrokeCap.Butt
                )
            )
        )
    )
}