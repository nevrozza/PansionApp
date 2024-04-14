@file:OptIn(ExperimentalMaterial3Api::class)

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.AlphaTestZatichka
import components.AppBar
import components.CustomTextButton
import components.LoadingAnimation
import components.MarkContent
import components.ReportTitle
import components.networkInterface.NetworkState
import home.HomeComponent
import home.HomeStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pullRefresh.PullRefreshIndicator
import pullRefresh.pullRefresh
import pullRefresh.rememberPullRefreshState
import report.Grade
import report.UserMark
import server.Roles
import server.fetchReason
import server.roundTo
import view.LocalViewManager
import view.WindowScreen
import view.handy
import view.rememberImeState

@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalLayoutApi
@Composable
fun HomeContent(
    component: HomeComponent,
    role: String
) {
    val model by component.model.subscribeAsState()
    val nTeacherModel by component.teacherNInterface.networkModel.subscribeAsState()
    val viewManager = LocalViewManager.current
    if (role == Roles.student) {
        TrueHomeContent(component)
    } else if (role == Roles.teacher) {
        AlphaTestZatichka(
            onSettingsClick =
            if (viewManager.orientation.value != WindowScreen.Expanded) {
                { component.onOutput(HomeComponent.Output.NavigateToSettings) }
            } else null
        ) {
            Crossfade(nTeacherModel.state, modifier = Modifier.padding(top = 10.dp)) { state ->
                when (state) {
                    NetworkState.None -> {
                        Column(Modifier.verticalScroll(rememberScrollState())) {
                            model.teacherGroups.forEach {
                                FilledTonalButton(
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp).padding(horizontal = 50.dp)
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
                                        Modifier.fillMaxWidth().padding(vertical = 5.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            buildAnnotatedString {
                                                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
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
                        LoadingAnimation()
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
        }

    }
}

@Composable
fun TrueHomeContent(
    component: HomeComponent
) {
    val model by component.model.subscribeAsState()
    val nQuickTabModel by component.quickTabNInterface.networkModel.subscribeAsState()
    val nGradesModel by component.gradesNInterface.networkModel.subscribeAsState()
    val focusManager = LocalFocusManager.current
    val viewManager = LocalViewManager.current
//    val scrollState = rememberScrollState()
    val imeState = rememberImeState()
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    //PullToRefresh
    val refreshScope = rememberCoroutineScope()
    var refreshing by remember { mutableStateOf(false) }
    fun refresh() = refreshScope.launch {
        refreshing = true
        delay(1000)
        refreshing = false
    }

    val refreshState = rememberPullRefreshState(refreshing, ::refresh)

    Scaffold(
        Modifier.fillMaxSize()
            .onKeyEvent {
                if (it.key == Key.F5 && it.type == KeyEventType.KeyDown) {
                    refresh()
                }
                false
            },
        topBar = {
            AppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AnimatedContent(
                            targetState = if (lazyListState.firstVisibleItemIndex != 0) "Расписание" else "Главная"
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
                        if (model.name == "Мария" && model.surname == "Губская" && model.praname == "Дмитриевна") {
                            Text(
                                "Всё получится!!!",
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

                    IconButton(
                        onClick = { refresh() }
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
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            LazyColumn(
                Modifier
                    .padding(horizontal = 15.dp)
                    .consumeWindowInsets(padding)
                    .imePadding()
                    .pullRefresh(refreshState)
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
                                Modifier.fillMaxWidth().clip(CardDefaults.elevatedShape).weight(1f)
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
                                    Text("Оценки", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                                    Spacer(Modifier.height(5.dp))
                                    Box(
                                        Modifier.fillMaxWidth().padding(end = 5.dp, bottom = 5.dp),
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
                                Modifier.fillMaxWidth().clip(CardDefaults.elevatedShape).weight(1f)
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
                        Crossfade(nGradesModel.state) {
                            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                when(it) {
                                    NetworkState.None -> LazyRow(Modifier.fillMaxWidth()) {
                                        items(model.grades.sortedBy { it.date }.reversed()) {
                                            println("zxcxx: ${it.subjectName}")
                                            cGrade(it, coroutineScope)
                                        }
                                    }
                                    NetworkState.Loading -> LoadingAnimation()
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun cGrade(mark: Grade, coroutineScope: CoroutineScope) {
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
                width = if(cutedReason !in listOf("!ds", "!st")) 0.dp else 1.dp,
                color = if(cutedReason !in listOf("!ds", "!st")) Color.Transparent else MaterialTheme.colorScheme.outline.copy(1f),
                shape = RoundedCornerShape(30)
            )
            .clip(RoundedCornerShape(percent = 30))
            .background(
                if(cutedReason !in listOf("!ds", "!st")) MaterialTheme.colorScheme.primary.copy(
                    alpha = .2f
                ) else MaterialTheme.colorScheme.surface
            )
            .then(addModifier),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(5.dp).padding(horizontal = 2.dp).offset(y = -2.dp)) {
            Text(
                (if(cutedReason in listOf("!ds", "!st") && mark.toInt() > 0) "+" else "") + mark,
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
fun GetAvatar(avatarId: Int, name: String) {
    val viewManager = LocalViewManager.current
    Box(
        modifier = Modifier.size(70.dp).clip(CircleShape).background(
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

        Text(
            name[0].toString(),
            fontSize = 30.sp,
            fontWeight = FontWeight.Normal,
            color = Color.White
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
