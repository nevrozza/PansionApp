
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.foundation.AppBar
import components.foundation.CLazyColumn
import components.foundation.CCheckbox
import components.foundation.DefaultErrorView
import components.foundation.DefaultErrorViewPos
import components.GetAsyncAvatar
import components.GetAsyncIcon
import components.PeriodButton
import components.foundation.cClickable
import components.listDialog.ListDialogStore
import components.networkInterface.NetworkState
import components.networkInterface.isLoading
import components.refresh.RefreshButton
import components.refresh.RefreshWithoutPullCircle
import components.refresh.keyRefresh
import decomposeComponents.listDialogComponent.ListDialogDesktopContent
import decomposeComponents.listDialogComponent.ListDialogMobileContent
import components.refresh.PullRefreshIndicator
import components.refresh.pullRefresh
import components.refresh.rememberPullRefreshState
import rating.PansionPeriod
import rating.RatingComponent
import rating.RatingItem
import rating.RatingStore
import rating.toStr
import resources.RIcons
import server.roundTo
import view.LocalViewManager
import view.WindowScreen
import androidx.compose.desktop.ui.tooling.preview.utils.esp
import utils.toColor

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class,
    ExperimentalSharedTransitionApi::class
)
@Composable
fun SharedTransitionScope.RatingContent(
    component: RatingComponent,
    isSharedVisible: Boolean,
    onExtraRefreshClick: () -> Unit
) {

    val model by component.model.subscribeAsState()
    val nModel by component.nInterface.networkModel.subscribeAsState()
    val viewManager = LocalViewManager.current


    val refreshing = nModel.isLoading

    val refreshState = rememberPullRefreshState(
        refreshing,
        {
            component.onEvent(RatingStore.Intent.Init)
            onExtraRefreshClick()
        })

    LaunchedEffect(Unit) {
        if (!refreshing) component.onEvent(RatingStore.Intent.Init)
    }


    val isExpanded = viewManager.orientation.value == WindowScreen.Expanded
    val rawItems = model.items[model.period?.toStr()]?.get(model.currentSubject)
    Scaffold(
        Modifier.fillMaxSize().keyRefresh(refreshState),
        topBar = {
            AppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {

                        Text(
                            "Рейтинг",
                            modifier = Modifier.padding(start = if (isExpanded) 10.dp else 0.dp),
                            fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                            fontWeight = FontWeight.Black,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            model.lastEditTime,
                            fontSize = MaterialTheme.typography.titleLarge.fontSize,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.alpha(.5f).padding(top = 3.5.dp)
                        )
                        RefreshWithoutPullCircle(
                            refreshing,
                            refreshState.position,
                            !rawItems.isNullOrEmpty()
                        )

                    }

                },
                navigationRow = {
                    if (!isExpanded) {
                        IconButton(
                            onClick = { component.onOutput(RatingComponent.Output.Back) }
                        ) {
                            GetAsyncIcon(
                                path = RIcons.CHEVRON_LEFT
                            )
                        }
                    }
                },
                actionRow = {
                    RefreshButton(refreshState, viewManager)


                    if (viewManager.orientation.value == WindowScreen.Expanded) {
                        IconButton(
                            onClick = {
                                component.onOutput(RatingComponent.Output.NavigateToSettings)
                            }
                        ) {
                            GetAsyncIcon(
                                RIcons.SETTINGS
                            )
                        }
                    }
                }
            )

        },
        bottomBar = {
            val me = model.me[model.period?.toStr()]?.get(model.currentSubject)
            val previousItem: MutableState<RatingItem?> = remember {
                mutableStateOf(
                    null
                )
            }
            AnimatedVisibility(
                me != null && me.top > 10,
                enter = slideInVertically(initialOffsetY = { it * 2 }),
                exit = slideOutVertically(targetOffsetY = { it * 2 }),
            ) {
                if (me != null) {
                    previousItem.value = me
                }
                if (previousItem.value != null) {
                    Column(
                        Modifier.padding(horizontal = 7.5.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        RatingCard(
                            previousItem.value!!,
                            meLogin = model.login,
                            isMe = true,
                            component = component,
                            isSharedVisible = isSharedVisible,
                            isDetailed = model.isDetailed
                        )

                        Spacer(Modifier.height(if (viewManager.orientation.value != WindowScreen.Vertical) 15.dp else 80.dp))
                    }
                }
            }

        }
    ) { padding ->

        AnimatedContent(
            rawItems,
            transitionSpec = { fadeIn(tween(750)).togetherWith(fadeOut(tween(750))) }
        ) { items ->
            Box(Modifier.fillMaxSize().pullRefresh(refreshState)) {
                CLazyColumn(
                    padding = padding,
                    isBottomPaddingNeeded = true,
                    modifier = Modifier,
                    refreshState = refreshState
                ) {
                    item {
                        Row(
                            Modifier.offset(y = -6.dp).horizontalScroll(rememberScrollState()),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box() {
                                ElevatedAssistChip(
                                    onClick = {
                                        component.subjectsListComponent.onEvent(ListDialogStore.Intent.ShowDialog)
                                    },
                                    label = {
                                        AnimatedContent(
                                            model.subjects.firstOrNull() { it.id == model.currentSubject }?.name
                                                ?: "Загрузка.."
                                        ) {
                                            Text(
                                                it, maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        labelColor = MaterialTheme.colorScheme.onPrimary
                                    ),
                                    modifier = Modifier.animateContentSize()
                                )
                                ListDialogDesktopContent(component.subjectsListComponent)
                            }
                            Spacer(Modifier.width(5.dp))
                            Box() {
                                InputChip(
                                    selected = true,
                                    onClick = { component.formsListComponent.onEvent(ListDialogStore.Intent.ShowDialog) },
                                    label = {
                                        AnimatedContent(
                                            component.formsListComponent.state.value.list.firstOrNull { it.id.toInt() == model.forms }?.text
                                                ?: "Загрузка.."
                                        ) {
                                            Text(
                                                it, maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    },
                                    modifier = Modifier.animateContentSize()
                                )
                            }
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
                                    component.onEvent(RatingStore.Intent.ChangeIsDetailed)
                                }) {
                                CCheckbox(
                                    checked = model.isDetailed
                                )
                                Text("Отображать детально")
                            }
                            Spacer(Modifier.width(15.dp))
                        }
                    }

                    if (!items.isNullOrEmpty()) {

                        items(items.sortedBy { it.top }) { i ->
                            RatingCard(
                                i,
                                meLogin = model.login,
                                component = component,
                                isSharedVisible = isSharedVisible,
                                isDetailed = model.isDetailed
                            )
                        }
                    } else {
                        item {
                            Column(
                                Modifier.fillMaxWidth()
                                    .padding(top = viewManager.size!!.maxHeight / 2 - padding.calculateTopPadding() - viewManager.topPadding),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (nModel.state == NetworkState.Error) {
                                    DefaultErrorView(
                                        nModel,
                                        pos = DefaultErrorViewPos.CenteredFull
                                    )
                                }
                                if (nModel.state != NetworkState.Loading) {
                                    Text(
                                        "В этом рейтинге пока никто не участвует!", modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        "Общие требования для участия в таблице рейтинга:\nСтупени ≥ 1 и Ср. Балл ≥ 4",
                                        modifier = Modifier.fillMaxWidth().alpha(.5f),
                                        textAlign = TextAlign.Center,
                                        fontSize = 12.esp,
                                        lineHeight = 14.esp
                                    )
                                    Text(
                                        text = if (nModel.state == NetworkState.None) "\n" else "",
                                        textAlign = TextAlign.Center
                                    )
                                } else {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                    }
                }
                PullRefreshIndicator(
                    state = refreshState,
                    topPadding = padding.calculateTopPadding()
                )
            }
        }
        ListDialogMobileContent(component.subjectsListComponent, title = "Предмет")
        ListDialogMobileContent(component.formsListComponent, title = "Классы")
    }
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.RatingCard(
    item: RatingItem, meLogin: String, isMe: Boolean = false, component: RatingComponent,
    isDetailed: Boolean,
    isSharedVisible: Boolean
) {
    Surface(
        modifier = Modifier
            .then(
                if (isMe) {
                    Modifier.wrapContentSize()
                } else {
                    Modifier
                        .fillMaxWidth()
                }
            )
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        tonalElevation = if (isMe || meLogin == item.login) 24.dp else 2.dp,
        shadowElevation = if (isMe) 12.dp else 0.dp,
        onClick = {
            if (!(isMe || meLogin == item.login)) {
                component.onOutput(
                    RatingComponent.Output.NavigateToProfile(
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
            Box(Modifier.size(40.dp), contentAlignment = Alignment.Center) {
                if (item.top <= 3) {
                    // Show trophy icon for top 3 positions
                    GetAsyncIcon(
                        path = RIcons.TROPHY,
                        tint = when (item.top) {
                            1 -> "#ffd700".toColor()
                            2 -> "#c0c0c0".toColor()
                            else -> "#cd7f32".toColor()
                        },
                        size = 35.dp,
                        contentDescription = "Top position"
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
            GetAsyncAvatar(
                avatarId = item.avatarId,
                name = item.fio.name,
                size = 40.dp,
                textSize = MaterialTheme.typography.titleLarge.fontSize,
                modifier = Modifier.sharedElementWithCallerManagedVisibility(
                    sharedContentState = rememberSharedContentState(key = item.login + "avatar"),
                    visible = isSharedVisible
                )
            )

            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Spacer(Modifier.height(1.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (!isMe) "${item.fio.surname} ${item.fio.name}" else "Вы",
                        fontSize = 18.esp, // Adjust font size for heading
                        lineHeight = 19.esp,
                        fontWeight = FontWeight.Bold // Make text bold for emphasis
                    )
                    if (item.difficulty > 0 && isMe) {
                        Spacer(Modifier.width(10.dp))
                        GetAsyncIcon(
                            path = RIcons.FIRE,
                            size = 18.dp
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            item.difficulty.toString(),
                            fontSize = 18.esp, // Adjust font size for heading
                            lineHeight = 19.esp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
//                    Spacer(Modifier.height(1.dp))
                if (!isMe) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${item.formNum}${if (item.formShortTitle.length < 2) "-" else " "}${item.formShortTitle}${
                                if (item.groupName != "?") {
                                    ": " + (
                                            item.groupName.split(
                                                "кл "
                                            ).getOrNull(1) ?: item.groupName)
                                } else {
                                    ""
                                }
                            }",
                            fontSize = 14.esp,
                            lineHeight = 15.esp,// Adjust font size for body text
                            //                        lineHeight = 15.sp,
                            color = Color.Gray,

                            )
                        if (item.difficulty > 0) {
                            Spacer(Modifier.width(5.dp))
                            GetAsyncIcon(
                                path = RIcons.FIRE,
                                size = 12.dp,
                                tint = Color.Gray
                            )
                            Spacer(Modifier.width(2.dp))
                            Text(
                                item.difficulty.toString(),
                                fontSize = 14.esp, // Adjust font size for heading
                                lineHeight = 15.esp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                        }
                    }
                }

            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (!isDetailed) item.avg else "${item.avgAlg.roundTo(2)} (${item.topAvg})",
                    fontSize = 18.esp,
                    lineHeight = 19.esp
                )
                Spacer(Modifier.height(1.dp))
                Text(
                    text = if (!isDetailed) "+${item.stups}" else "+${item.stupsAlg.roundTo(2)} (${item.topStups})",
                    fontSize = MaterialTheme.typography.titleSmall.fontSize,
//                        lineHeight = 15.sp,
                    color = MaterialTheme.colorScheme.primary,//Color.Green,
                    fontWeight = FontWeight.Bold
                )
            }

        }
    }
}