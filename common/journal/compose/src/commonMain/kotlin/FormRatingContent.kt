import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.*
import components.listDialog.ListDialogStore
import components.networkInterface.NetworkState
import decomposeComponents.CAlertDialogContent
import decomposeComponents.listDialogComponent.ListDialogDesktopContent
import decomposeComponents.listDialogComponent.ListDialogMobileContent
import formRating.FormRatingComponent
import formRating.FormRatingStore
import rating.FormRatingStudent
import rating.PansionPeriod
import rating.toStr
import resources.RIcons
import server.Roles
import server.fetchReason
import server.getLocalDate
import server.roundTo
import view.LocalViewManager
import view.WindowScreen
import view.esp
import view.toColor
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import components.networkInterface.isLoading
import components.refresh.RefreshButton
import components.refresh.RefreshWithoutPullCircle
import components.refresh.keyRefresh
import pullRefresh.PullRefreshIndicator
import pullRefresh.pullRefresh
import pullRefresh.rememberPullRefreshState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.FormRatingContent(
    component: FormRatingComponent,
    isVisible: Boolean
) {




    val model by component.model.subscribeAsState()
    val formPickerModel by component.formPickerDialog.model.subscribeAsState()
    val nModel by component.nInterface.networkModel.subscribeAsState()
    val nFormPickerModel by component.formPickerDialog.nInterface.networkModel.subscribeAsState()
    val viewManager = LocalViewManager.current
    val isExpanded = viewManager.orientation.value == WindowScreen.Expanded
    val coroutineScope = rememberCoroutineScope()

    val rawPage =
        model.formRatingPages.firstOrNull { it.formId == model.formId && it.period == model.period }


    val refreshing = nModel.isLoading

    val refreshState = rememberPullRefreshState(
        refreshing,
        { component.onEvent(FormRatingStore.Intent.Init) }
    )

    LaunchedEffect(Unit) {
        refreshState.onRefreshState.value()
    }

    Scaffold(
        Modifier.fillMaxSize().keyRefresh(refreshState),
        topBar = {
            AppBar(
                title = {
                    AnimatedContent(
                        (model.formName ?: "Выберите") + " класс ",
                        transitionSpec = { fadeIn().togetherWith(fadeOut()) },
                        modifier = Modifier.cClickable {
                            component.formPickerDialog.onEvent(
                                ListDialogStore.Intent.ShowDialog
                            )
                        }
                    ) { text ->
                        Text(
                            text,
                            modifier = Modifier.padding(start = 10.dp),
                            fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                            fontWeight = FontWeight.Black,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    RefreshWithoutPullCircle(refreshing, refreshState.position, rawPage != null)
                },
                navigationRow = {
                    if (!isExpanded) {
                        IconButton(
                            onClick = { component.onOutput(FormRatingComponent.Output.Back) }
                        ) {
                            GetAsyncIcon(
                                path = RIcons.ChevronLeft
                            )
                        }
                    }
                },
                actionRow = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (model.role != Roles.student) {
                            Box(contentAlignment = Alignment.Center) {
                                Crossfade(nFormPickerModel.state) { cf ->
                                    Box(Modifier.size(40.dp), contentAlignment = Alignment.Center) {
                                        when (cf) {
                                            NetworkState.Error -> IconButton(onClick = {
                                                nFormPickerModel.onFixErrorClick()
                                            }) {
                                                GetAsyncIcon(
                                                    RIcons.ErrorOutline
                                                )
                                            }

                                            NetworkState.Loading -> {
                                                CircularProgressIndicator(Modifier.size(25.dp))
                                            }

                                            NetworkState.None ->
                                                this@Row.AnimatedVisibility(
                                                    model.availableForms.size > 1 || (model.formId == null && model.availableForms.size == 1)
                                                ) {
                                                    val chevronRotation =
                                                        animateFloatAsState(if (formPickerModel.isDialogShowing) 90f else -90f)
                                                    IconButton(onClick = {
                                                        component.formPickerDialog.onEvent(
                                                            ListDialogStore.Intent.ShowDialog
                                                        )
                                                    }) {
                                                        GetAsyncIcon(
                                                            RIcons.ChevronLeft,
                                                            modifier = Modifier.rotate(chevronRotation.value)
                                                        )
                                                    }
                                                }

                                        }
                                    }
                                }

                                ListDialogDesktopContent(
                                    component = component.formPickerDialog
                                )
                            }
                        }
                        RefreshButton(refreshState, viewManager)
                    }
                }
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().pullRefresh(refreshState)) {
            Crossfade(nModel.state) { state ->
                when {
                    state is NetworkState.None || rawPage != null -> {
                        AnimatedContent(
                            rawPage,
                            transitionSpec = { fadeIn(tween(750)).togetherWith(fadeOut(tween(750))) }
                        ) { page ->


                            CLazyColumn(
                                padding = padding,
                                refreshState = refreshState
                            ) {
                                item {

                                    Row(
                                        modifier = Modifier.horizontalScroll(
                                            rememberScrollState()
                                        ),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Spacer(Modifier.width(5.dp))
                                        PeriodButton(
                                            inActiveText = "Недели",
                                            currentPeriod = model.period?.toStr() ?: "",
                                            isActive = model.period is PansionPeriod.Week,
                                            component = component.weekListComponent
                                        )
                                        Spacer(Modifier.width(5.dp))
                                        PeriodButton(
                                            inActiveText = "Модули",
                                            currentPeriod = model.period?.toStr() ?: "",
                                            isActive = model.period is PansionPeriod.Module,
                                            component = component.moduleListComponent
                                        )
                                        Spacer(Modifier.width(5.dp))
                                        PeriodButton(
                                            inActiveText = "Периоды",
                                            currentPeriod = model.period?.toStr() ?: "",
                                            isActive = model.period is PansionPeriod.Year || model.period is PansionPeriod.Half,
                                            component = component.periodListComponent
                                        )
                                        Spacer(Modifier.width(10.dp))
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.cClickable {
                                                component.onEvent(FormRatingStore.Intent.ChangeIsDetailed)
                                            }) {
                                            CustomCheckbox(
                                                checked = model.isDetailed
                                            )
                                            Text("Отображать детально")
                                        }
                                        Spacer(Modifier.width(15.dp))
                                    }
                                    //                                listOf(
                                    //                                    Pair(0, "За неделю"),
                                    //                                    Pair(1, "За прошлую неделю"),
                                    //                                    Pair(2, "За модуль"),
                                    //                                    Pair(3, "За полугодие"),
                                    //                                    Pair(4, "За год")
                                    //                                ).forEach { item ->
                                    //
                                    //                                    CFilterChip(
                                    //                                        label = item.second,
                                    //                                        isSelected = model.period == item.first,
                                    //                                        state = state,
                                    //                                        coroutineScope = coroutineScope
                                    //                                    ) {
                                    //                                        component.onEvent(
                                    //                                            FormRatingStore.Intent.ChangePeriod(item.first)
                                    //                                        )
                                    //                                    }
                                    //                                    Spacer(Modifier.width(5.dp))
                                    //                                }
                                    //                            }
                                }
                                if (page != null) {
                                    val inRating = page.students.filter { it.avg.toFloat() >= 4 && it.edStups.isNotEmpty() && it.avgAlg > 0 }
                                    items(inRating, key = { it.login }) { s ->
                                        FormRatingCard(
                                            item = s,
                                            meLogin = model.login,
                                            component = component,
                                            isVisible = isVisible,
                                            isDetailed = model.isDetailed,
                                            isInRanked = true
                                        )
                                    }
                                    itemsIndexed(
                                        items = page.students.filter { it !in inRating },
                                        key = { _, item -> item.login }) { index, item ->
                                        if (index == 0) {
                                            Text("Не участвуют в рейтинге: ")
                                        }
                                        FormRatingCard(
                                            item = item,
                                            meLogin = model.login,
                                            component = component,
                                            isVisible = isVisible,
                                            isDetailed = model.isDetailed,
                                            isInRanked = false
                                        )
                                    }

                                }
                            }
                        }
                    }

                    state is NetworkState.Loading -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }

                    state is NetworkState.Error -> DefaultErrorView(
                        nModel,
                        DefaultErrorViewPos.CenteredFull
                    )
                }
            }
            PullRefreshIndicator(refreshState, padding.calculateTopPadding())
        }
    }
    ListDialogMobileContent(
        component = component.formPickerDialog,
        title = "Классы"
    )

    val detailedStupsStudent =
        rawPage?.students?.firstOrNull { it.login == model.stupsLogin }

    CAlertDialogContent(
        component = component.stupsDialogComponent,
        title = "Ступени: ${if (detailedStupsStudent != null) (detailedStupsStudent.fio.surname + " " + detailedStupsStudent.fio.name) else "null"}",
        titleXOffset = 5.dp
    ) {
        Column(Modifier.verticalScroll(rememberScrollState())) {
            rawPage?.students?.firstOrNull { it.login == model.stupsLogin }?.edStups?.sortedBy {
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
                    Text(
                        model.subjects[it.subjectId].toString(),
                        modifier = Modifier.weight(1f, false),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(it.date.subSequence(0, 5).toString(), modifier = Modifier.weight(1f, false))
                    Text(fetchReason(it.reason).split(":")[1], modifier = Modifier.weight(.5f, false))
                    BorderStup(it.content, reason = it.reason, addModifier = Modifier.weight(.5f, false))
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.FormRatingCard(
    item: FormRatingStudent,
    meLogin: String,
    isVisible: Boolean,
    isDetailed: Boolean,
    isInRanked: Boolean,
    component: FormRatingComponent
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        tonalElevation = if (meLogin == item.login) 24.dp else 2.dp,
        shadowElevation = 0.dp,
        onClick = {
            if (meLogin != item.login) {
                component.onOutput(
                    FormRatingComponent.Output.NavigateToProfile(
                        studentLogin = item.login,
                        fio = item.fio,
                        avatarId = item.avatarId
                    )
                )
            }
        }
    ) {
        Row(
            modifier = Modifier.padding(end = 16.dp, start = 8.dp).padding(vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (item.top != 0 && isInRanked) {
                Box(Modifier.size(40.dp), contentAlignment = Alignment.Center) {
                    if (item.top <= 3) {
                        GetAsyncIcon(
                            path = RIcons.Trophy,
                            tint = when (item.top) {
                                1 -> "#ffd700".toColor()
                                2 -> "#c0c0c0".toColor()
                                else -> "#cd7f32".toColor()
                            },
                            size = 35.dp
                        )
                    } else {
                        // Show position number for other positions
                        Text(
                            text = item.top.toString(),
                            fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                            fontWeight = FontWeight.Black,
                            fontStyle = FontStyle.Italic
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
            GetAsyncAvatar(
                avatarId = item.avatarId,
                name = item.fio.name,
                size = 40.dp,
                textSize = MaterialTheme.typography.titleLarge.fontSize,
                modifier = Modifier.sharedElementWithCallerManagedVisibility(
                    sharedContentState = rememberSharedContentState(key = item.login + "avatar"),
                    visible = isVisible
                )
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${item.fio.surname} ${item.fio.name}",
                    fontSize = 18.esp, // Adjust font size for heading
                    lineHeight = 19.esp,
                    fontWeight = FontWeight.Bold // Make text bold for emphasis
                )
                Spacer(Modifier.height(1.dp))
                Text(
                    text = buildAnnotatedString {
                        if (item.formTitle != null) {
                            append(item.formTitle)
                            append(" ")
                        }
                        append("мвд: ${item.mvdStupsCount} ")
                        append("зд: ${item.zdStupsCount} ")
                    },
                    fontSize = MaterialTheme.typography.titleSmall.fontSize, // Adjust font size for body text
//                    lineHeight = 15.sp,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.width(8.dp))


            val stups = if (isDetailed) item.stupsAlg else item.edStups.sumOf { it.content.toIntOrNull() ?: 0 }
            val avg = if (isDetailed) item.avgAlg.roundTo(2) else item.avg

            Column(
                horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clip(
                    RoundedCornerShape(8.dp)
                ).clickable {
                    component.onEvent(FormRatingStore.Intent.SelectStupsLogin(item.login))
                }) {
                Text(
                    text = if (item.top != 0 && isInRanked) {
                         avg + " (${item.topAvg})"
                    } else if ((avg.toFloatOrNull() ?: 0f) > 0f) avg
                    else "",
                    fontSize = 18.esp,
                    lineHeight = 19.esp
                )
                Spacer(Modifier.height(1.dp))
                Text(
                    text = if (item.top != 0 && isInRanked) {
                        "+${stups} (${item.topStups})"
                    } else if (item.edStups.isNotEmpty()) (if (stups.toString()
                            .first() != '-'
                    ) "+" else "") + stups.toString()
                    else "",
                    fontSize = MaterialTheme.typography.titleSmall.fontSize,
//                    lineHeight = 15.sp,
                    color = MaterialTheme.colorScheme.primary,//Color.Green,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}