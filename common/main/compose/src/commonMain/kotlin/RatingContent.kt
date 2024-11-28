
import androidx.compose.animation.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.*
import components.listDialog.ListDialogStore
import components.networkInterface.NetworkState
import decomposeComponents.listDialogComponent.ListDialogDesktopContent
import decomposeComponents.listDialogComponent.ListDialogMobileContent
import dev.chrisbanes.haze.HazeState
import pullRefresh.PullRefreshIndicator
import pullRefresh.pullRefresh
import pullRefresh.rememberPullRefreshState
import rating.RatingComponent
import rating.RatingItem
import rating.RatingStore
import resources.RIcons
import view.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class,
    ExperimentalSharedTransitionApi::class
)
@Composable
fun SharedTransitionScope.RatingContent(
    component: RatingComponent,
    isSharedVisible: Boolean
) {
    val model by component.model.subscribeAsState()
    val nModel by component.nInterface.networkModel.subscribeAsState()
    val focusManager = LocalFocusManager.current
    val viewManager = LocalViewManager.current
//    val scrollState = rememberScrollState()
    val imeState = rememberImeState()
    val lazyListState = rememberLazyListState()

    val hazeState = remember { HazeState() }

    val refreshState = rememberPullRefreshState(
        nModel.state == NetworkState.Loading,
        { component.onEvent(RatingStore.Intent.Init) })

    val isExpanded = viewManager.orientation.value == WindowScreen.Expanded

    Scaffold(
        Modifier.fillMaxSize(),
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
                    }

                },
                navigationRow = {
                    if (!isExpanded) {
                        IconButton(
                            onClick = { component.onOutput(RatingComponent.Output.Back) }
                        ) {
                            GetAsyncIcon(
                                path = RIcons.ChevronLeft
                            )
                        }
                    }
                },
                actionRow = {
                    IconButton(
                        onClick = { component.onEvent(RatingStore.Intent.Init) }
                    ) {
                        GetAsyncIcon(
                            RIcons.Refresh
                        )
                    }


                    if (viewManager.orientation.value == WindowScreen.Expanded) {
                        IconButton(
                            onClick = {
                                component.onOutput(RatingComponent.Output.NavigateToSettings)
                            }
                        ) {
                            GetAsyncIcon(
                                RIcons.Settings
                            )
                        }
                    }
                },
                hazeState = hazeState
            )

        },
        bottomBar = {
            val me = model.me[model.currentSubject]
            val previousItem: MutableState<RatingItem?> = remember {
                mutableStateOf(
                    null
                )
            }
            AnimatedVisibility(
                me != null && me.first > 10,
                enter = slideInVertically(initialOffsetY = { it * 2 }),
                exit = slideOutVertically(targetOffsetY = { it * 2 }),
            ) {
                if (me != null) {
                    previousItem.value = RatingItem(
                        login = model.login,
                        fio = model.fio,
                        avatarId = model.avatarId,
                        stups = me.second,
                        top = me.first,
                        groupName = "",
                        formNum = 0,
                        formShortTitle = "",
                        avg = ""
                    )
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
                            isSharedVisible = isSharedVisible
                        )

                        Spacer(Modifier.height(if (viewManager.orientation.value != WindowScreen.Vertical) 15.dp else 80.dp))
                    }
                }
            }

        }
    ) { padding ->
        Box(Modifier.fillMaxSize()) {
            CLazyColumn(
                padding = padding,
                isBottomPaddingNeeded = true,
                modifier = Modifier.pullRefresh(refreshState),
                hazeState = hazeState
            ) {
                item {
                    Row(Modifier.offset(y = -6.dp).horizontalScroll(rememberScrollState())) {
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
                            ListDialogDesktopContent(component.formsListComponent)
                        }
                        Spacer(Modifier.width(5.dp))

                        Box() {
                            AssistChip(
                                onClick = { component.periodListComponent.onEvent(ListDialogStore.Intent.ShowDialog) },
                                label = {
                                    AnimatedContent(
                                        component.periodListComponent.state.value.list.firstOrNull { it.id.toInt() == model.period }?.text
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
                            ListDialogDesktopContent(component.periodListComponent)
                        }
                        Spacer(Modifier.width(15.dp))
                    }
                }
                val items = model.items[model.currentSubject]
                if (!items.isNullOrEmpty()) {

                    items(items.sortedBy { it.top }) { i ->
                        RatingCard(i, meLogin = model.login, component = component, isSharedVisible = isSharedVisible)
                    }
                } else {
                    item {
                        Column(
                            Modifier.fillMaxWidth()
                                .padding(top = viewManager.size!!.maxHeight / 2 - padding.calculateTopPadding() - viewManager.topPadding),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (nModel.state != NetworkState.Loading) {
                                Text(
                                    text = if (nModel.state == NetworkState.None) "Здесь пусто 0_0\nТребования для участия в таблице рейтинга:\nСтупени >= 1 & Ср. Балл >=4" else if (nModel.state == NetworkState.Error) "Произошла ошибка" else "",
                                    textAlign = TextAlign.Center
                                )
                                if (nModel.state == NetworkState.Error) {
                                    CustomTextButton(text = "Попробовать ещё раз") {
                                        nModel.onFixErrorClick()
                                    }
                                }
                            } else {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }


            ListDialogMobileContent(component.subjectsListComponent, title = "Предмет")
            ListDialogMobileContent(component.periodListComponent, title = "Период")
            ListDialogMobileContent(component.formsListComponent, title = "Классы")
            PullRefreshIndicator(
                modifier = Modifier.align(alignment = Alignment.TopCenter),
                refreshing = nModel.state == NetworkState.Loading && model.items[model.currentSubject].isNullOrEmpty(),
                state = refreshState,
                topPadding = padding.calculateTopPadding()
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.RatingCard(
    item: RatingItem, meLogin: String, isMe: Boolean = false, component: RatingComponent,
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
                component.onOutput(RatingComponent.Output.NavigateToProfile(
                    studentLogin = item.login,
                    fio = item.fio,
                    avatarId = item.avatarId
                ))
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
                        path = RIcons.Trophy,
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
            if (!isMe) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${item.fio.surname} ${item.fio.name}",
                        fontSize = 18.esp, // Adjust font size for heading
                        lineHeight = 19.esp,
                        fontWeight = FontWeight.Bold // Make text bold for emphasis
                    )
                    Spacer(Modifier.height(1.dp))
                    Text(
                        text = "${item.formNum}${if (item.formShortTitle.length < 2) "-" else " "}${item.formShortTitle}: ${
                            item.groupName.split(
                                "кл "
                            ).getOrNull(1) ?: item.groupName
                        }",
                        fontSize = MaterialTheme.typography.titleSmall.fontSize, // Adjust font size for body text
//                        lineHeight = 15.sp,
                        color = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = item.avg,
                        fontSize = 18.esp,
                        lineHeight = 19.esp
                    )
                    Spacer(Modifier.height(1.dp))
                    Text(
                        text = "+${item.stups}",
                        fontSize = MaterialTheme.typography.titleSmall.fontSize,
//                        lineHeight = 15.sp,
                        color = MaterialTheme.colorScheme.primary,//Color.Green,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Text(
                    text = "Вы",
                    fontSize = 26.esp, // Adjust font size for heading
                    fontWeight = FontWeight.Bold, // Make text bold for emphasis,
                    modifier = Modifier.offset(y = (-2.5).dp)
                )

                Spacer(modifier = Modifier.width(13.dp))

                Text(
                    text = "${if (item.stups > 0) "+" else "-"}${item.stups}",
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}