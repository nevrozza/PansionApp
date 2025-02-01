
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberTooltipState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.compose.ui.util.fastSumBy
import androidx.compose.ui.window.DialogProperties
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.AnimatedElevatedButton
import components.AppBar
import components.CustomCheckbox
import components.CustomTextButton
import components.CustomTextField
import components.DefaultErrorView
import components.DefaultErrorViewPos
import components.ErrorAnimation
import components.GetAsyncIcon
import components.MarkContent
import components.ReportTitle
import components.SaveAnimation
import components.ScrollBaredBox
import components.Stepper
import components.TeacherTime
import components.cAlertDialog.CAlertDialogStore
import components.cBottomSheet.CBottomSheetStore
import components.cClickable
import components.hazeUnder
import components.listDialog.ListDialogStore
import components.listDialog.ListItem
import components.networkInterface.NetworkState
import decomposeComponents.CAlertDialogContent
import decomposeComponents.CBottomSheetContent
import decomposeComponents.listDialogComponent.ListDialogDesktopContent
import decomposeComponents.listDialogComponent.ListDialogMobileContent
import homeTasksDialog.HomeTasksDialogStore
import homework.CreateReportHomeworkItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import lessonReport.ColumnTypes
import lessonReport.LessonReportComponent
import lessonReport.LessonReportStore
import lessonReport.MarkColumn
import lessonReport.StudentLine
import lessonReport.Stup
import resources.RIcons
import server.fetchReason
import server.getDate
import server.getSixTime
import server.roundTo
import server.st
import server.toMinutes
import view.LocalViewManager
import view.LockScreenOrientation
import view.blend
import view.esp
import view.popupPositionProvider
import kotlin.math.max


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun LessonReportScreen(
    component: LessonReportComponent,
    isExpanded: Boolean,
    listScreen: @Composable () -> Unit
) {
    val viewManager = LocalViewManager.current

    LockScreenOrientation(-1)

    DefaultMultiPane(
        isExpanded = isExpanded,
        leftScreen = listScreen,
        viewManager = viewManager,
        isFullScreenSupport = true
    ) {
        LessonReportContent(component)
    }


    LessonReportOverlay(
        component
    )


    LaunchedEffect(Unit) {
        component.setReportColumnsComponent.onEvent(CBottomSheetStore.Intent.ShowSheet)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonReportOverlay(
    component: LessonReportComponent
) {
    val model by component.model.subscribeAsState()
    val nHomeTasksModel by component.nHomeTasksInterface.networkModel.subscribeAsState()
    Box(Modifier.fillMaxSize()) {
        HomeTasksDialogContent(
            component.homeTasksDialogComponent
        )

        CAlertDialogContent(
            component.confirmDeletingColumnDialogComponent,
            isCustomButtons = false,
            title = if (model.deletingReportColumn != null) {
                val columnNamePrefix =
                    getColumnNamePrefix(model.deletingReportColumn!!.type)
                "$columnNamePrefix: " + model.deletingReportColumn!!.title.removePrefix("dz")
                    .removePrefix("cl").removePrefix("st").removePrefix("ds")
            } else "null",
            acceptText = "Удалить"
        ) {

            Column {
                Text(
                    text = "Вы уверены, что вы хотите удалить эту колонку?",
                    modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = buildAnnotatedString {
                        append("Это приведёт к удалению ")
                        withStyle(
                            SpanStyle(
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append("всех")
                        }
                        append(" оценок этой колонки")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }


        //SAVE_ANIMATION
        SaveAnimation(
            model.isHomeTasksSavedAnimation,
            customText = "Домашние задания сохранены",
            modifier = Modifier.align(
                Alignment.BottomCenter
            ).padding(bottom = 30.dp)
        ) {
            component.onEvent(LessonReportStore.Intent.IsHomeTasksSavedAnimation(false))
        }
        ErrorAnimation(
            textError = "Не удалось загрузить задания\nна сервер",
            isShowing = model.isHomeTasksErrorAnimation, modifier = Modifier.align(
                Alignment.BottomCenter
            ).padding(bottom = 30.dp)
        ) {
            component.onEvent(LessonReportStore.Intent.IsHomeTasksErrorAnimation(false))
        }


        //SAVE_ANIMATION
        SaveAnimation(model.isSavedAnimation, "Отчёт успешно сохранён!") {
            component.onEvent(LessonReportStore.Intent.IsSavedAnimation(false))
        }
        ErrorAnimation(
            textError = "Не удалось загрузить отчёт\nна сервер",
            isShowing = model.isErrorAnimation
        ) {
            component.onEvent(LessonReportStore.Intent.IsErrorAnimation(false))
        }


        CAlertDialogContent(
            component = component.saveQuitNameDialogComponent,
            isCustomButtons = false,
            title = "Сохранить отчёт?",
            acceptText = "Сохранить",
            declineText = "Не сохранять",
            dialogProperties = DialogProperties(true, true),
            isClickOutsideEqualsDecline = false
        ) {
        }


        CBottomSheetContent(
            component.setReportColumnsComponent,
        ) {
            val minSize = (LocalViewManager.current.size?.maxWidth ?: 0.dp)
            val isLikeMenuOpened =
                remember { mutableStateOf(minSize >= BottomSheetDefaults.SheetMaxWidth) }
            Column {
                Box(Modifier.padding(horizontal = 5.dp)) {
                    Crossfade(
                        model.settingsTab
                    ) {
                        when (it) {
                            LessonReportStore.SettingsTab.MarksTab -> {

                                Column() {
                                    Text(
                                        "Настройка колонок",
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center,
                                        fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(Modifier.height(25.dp))
                                    MarksTabContent(component)


                                }
                            }

                            LessonReportStore.SettingsTab.SetupTab -> {
                                Column(Modifier) {
                                    Text(
                                        "Об уроке",
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center,
                                        fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(Modifier.height(15.dp))

                                    SetupTabContent(component, isLikeMenuOpened, )
//                                    MarksTabContent(component)

                                }

                            }

                            LessonReportStore.SettingsTab.HomeWorkTab -> {
                                Column {
                                    Box(
                                        Modifier.fillMaxWidth()
                                    ) {

                                        Text(
                                            "Домашние задания",
                                            modifier = Modifier.fillMaxWidth()
                                                .align(Alignment.Center),
                                            textAlign = TextAlign.Center,
                                            fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                                            fontWeight = FontWeight.Bold
                                        )

                                        Row(
                                            Modifier.align(Alignment.CenterEnd),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            if (nHomeTasksModel.state is NetworkState.Loading) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(
                                                        20.dp
                                                    )
                                                )
                                            }
                                            if (nHomeTasksModel.state is NetworkState.Error) {
                                                CustomTextButton("Кнопка") {
                                                    nHomeTasksModel.onFixErrorClick()
                                                }
                                            }

                                            if ((model.homeTasksToEditIds.isNotEmpty() || true in model.hometasks.map { it.isNew }) && model.isEditable) {
                                                IconButton(
                                                    onClick = {
                                                        component.onEvent(LessonReportStore.Intent.SaveHomeTasks)

                                                    },
                                                    enabled = true
                                                ) {
                                                    GetAsyncIcon(
                                                        RIcons.Save
                                                    )
                                                }
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
                                                    RIcons.History
                                                )
                                            }
                                        }
                                    }
                                    Spacer(Modifier.height(15.dp))

                                    HomeWorkTabContent(component)
                                }
                            }
                        }
                    }
                    Column() {
                        FilledTonalIconToggleButton(
                            checked = model.settingsTab == LessonReportStore.SettingsTab.SetupTab,
                            onCheckedChange = {
                                if (it) {
                                    component.onEvent(
                                        LessonReportStore.Intent.ChangeSettingsTab(
                                            LessonReportStore.SettingsTab.SetupTab
                                        )
                                    )
                                }
                            }
                        ) {
                            GetAsyncIcon(
                                RIcons.Tune
                            )
                        }
                        if (component.model.value.isEditable) {
                            FilledTonalIconToggleButton(
                                checked = model.settingsTab == LessonReportStore.SettingsTab.MarksTab,
                                onCheckedChange = {
                                    if (it) {
                                        component.onEvent(
                                            LessonReportStore.Intent.ChangeSettingsTab(
                                                LessonReportStore.SettingsTab.MarksTab
                                            )
                                        )
                                    }
                                }
                            ) {
                                GetAsyncIcon(
                                    path = RIcons.Thumbtack
                                )
                            }
                        }
                        FilledTonalIconToggleButton(
                            checked = model.settingsTab == LessonReportStore.SettingsTab.HomeWorkTab,
                            onCheckedChange = {
                                if (it) {
                                    component.onEvent(
                                        LessonReportStore.Intent.ChangeSettingsTab(
                                            LessonReportStore.SettingsTab.HomeWorkTab
                                        )
                                    )
                                }
                            }
                        ) {
                            GetAsyncIcon(
                                RIcons.HomeWork
                            )
                        }
                        AnimatedVisibility(model.settingsTab == LessonReportStore.SettingsTab.SetupTab) {
                            Spacer(Modifier.height(80.dp))
                            IconToggleButton(
                                checked = isLikeMenuOpened.value,
                                onCheckedChange = {
                                    isLikeMenuOpened.value = it
                                }
                            ) {
                                Box(Modifier.size(30.dp)) {
                                    GetAsyncIcon(
                                        RIcons.Like,
                                        size = 16.dp,
                                        modifier = Modifier.align(Alignment.TopStart)
                                    )
                                    GetAsyncIcon(
                                        RIcons.Like,
                                        size = 16.dp,
                                        modifier = Modifier.rotate(180f).align(Alignment.BottomEnd)
                                    )
                                }


                            }
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@ExperimentalMaterial3Api
@ExperimentalLayoutApi
@Composable
fun LessonReportContent(
    component: LessonReportComponent
) {
    val model by component.model.subscribeAsState()
    val nModel by component.nInterface.networkModel.subscribeAsState()

    val viewManager = LocalViewManager.current
    BoxWithConstraints {

        val isFullView by mutableStateOf(this.maxWidth > 600.dp)
        Scaffold(
            Modifier.fillMaxSize()
            ,
            topBar = {
                LessonReportTopBar(component, isFullView) //, scrollBehavior
            },
            floatingActionButton = {
                if (model.isEditable) {
                    Row(
                        modifier = Modifier.animateContentSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AnimatedContent(if (model.status) "Заполнен" else "В процессе") {
                            Text(it)
                        }
                        Checkbox(
                            checked = model.status,
                            onCheckedChange = {
                                component.onEvent(LessonReportStore.Intent.ChangeStatus(it))
                            }
                        )
                        val isUpdateNeeded =
                            model.isUpdateNeeded || model.homeTasksToEditIds.isNotEmpty() || true in model.hometasks.map { it.isNew }
                        AnimatedVisibility(isUpdateNeeded) {
                            Crossfade(nModel.state) {
                                SmallFloatingActionButton(
                                    onClick = {
                                        if (it != NetworkState.Loading && isUpdateNeeded) {
                                            if (model.isUpdateNeeded) {
                                                component.onEvent(LessonReportStore.Intent.UpdateWholeReport)

                                            }
                                            if (model.homeTasksToEditIds.isNotEmpty() || true in model.hometasks.map { it.isNew }) {
                                                component.onEvent(LessonReportStore.Intent.SaveHomeTasks)
                                            }
                                        }
                                    }
                                ) {
                                    when (it) {
                                        NetworkState.None -> {
                                            GetAsyncIcon(
                                                RIcons.Save
                                            )
                                        }

                                        NetworkState.Loading -> {
                                            CircularProgressIndicator(modifier = Modifier.size(20.dp))
                                        }

                                        NetworkState.Error -> {
                                            Text("Попробовать ещё раз")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        ) { padding ->
            Column(Modifier.fillMaxSize().hazeUnder(viewManager).padding(padding)) {
                Crossfade(nModel.state) {
                    when {
                        model.students.isNotEmpty() -> {
                            Column(
                                Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                AnimatedVisibility(model.isInfoShowing && !isFullView) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = if (model.topic.isNotEmpty()) model.topic else "Тема не выставлена",
                                            modifier = Modifier.fillMaxWidth()
                                                .padding(start = 3.dp),
                                            textAlign = TextAlign.Center,
                                            overflow = TextOverflow.Ellipsis,
                                            maxLines = 2
                                        )
                                        TeacherTime(
                                            teacherName = model.teacherName,
                                            time = model.time
                                        )

                                    }
                                }
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    LessonTable(
                                        component
                                    )

                                }
                            }
                        }

                        it == NetworkState.Loading -> {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }

                        it == NetworkState.Error -> DefaultErrorView(
                            nModel,
                            DefaultErrorViewPos.CenteredFull
                        )
                    }
                }
                val reasonColumnName = try {
                    model.columnNames.first {
                        it.type == model.selectedMarkReason
                    }.title.removePrefix("dz").removePrefix("cl").removePrefix("st")
                        .removePrefix("ds")
                } catch (e: Throwable) {
                    "null"
                }
                val markStudentFIO = try {
                    model.students.first { it.login == model.selectedLogin }.shortFio
                } catch (_: Throwable) {
                    "null"
                }

                val markValue = try {
                    model.students.first { it.login == model.selectedLogin }.marksOfCurrentLesson.filter { it.reason == model.selectedMarkReason }
                        .get(model.selectedMarkValue.toInt()).value
                } catch (_: Throwable) {
                    "null"
                }

                ListDialogMobileContent(
                    component = component.setLateTimeMenuComponent,
                    title = "Выберите время опоздания"
                )

                //Set Mark
                ListDialogMobileContent(
                    component = component.setMarkMenuComponent,
                    title =
                        markStudentFIO + "\n${getColumnNamePrefix(model.selectedMarkReason)}: " + reasonColumnName,
                    modifier = Modifier.setMarksBind(component)
                )
                ListDialogMobileContent(
                    component = component.setDzMarkMenuComponent,
                    title =
                        markStudentFIO + "\n${getColumnNamePrefix(model.selectedMarkReason)}: " + reasonColumnName,
                    modifier = Modifier.setMarksBind(component)
                )

                ListDialogMobileContent(
                    component = component.deleteMarkMenuComponent,
                    title =
                        markStudentFIO + "\n${getColumnNamePrefix(model.selectedMarkReason)}: " + reasonColumnName + " - " + markValue
                                + "\n" + model.selectedDeploy
                )
            }

//            PullRefreshIndicator(
//                modifier = Modifier.align(alignment = Alignment.TopCenter),
//                refreshing = refreshing,
//                state = refreshState,
//                topPadding = padding.calculateTopPadding()
//            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeWorkTabContent(
    component: LessonReportComponent
) {
    val model by component.model.subscribeAsState()


    val tabs =
        (setOf(null) + (model.hometasks.map { it.studentLogins } + model.homeTasksNewTabs).toSet()).toList()
    val selectedTabIndex = remember { mutableStateOf(0) }
    Box() {
        Column(
            Modifier.verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)
                .padding(start = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BoxWithConstraints(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                SecondaryScrollableTabRow(
                    selectedTabIndex = selectedTabIndex.value,
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
                    tabs.forEachIndexed { i, tab ->
                        val tabText = if (tab != null) {
                            (model.students.filter { it.login in tab }).map {
                                it.shortFio.removeSuffix(
                                    "."
                                )
                            }.toString().removePrefix("[").removeSuffix("]")
                        } else "Все"
                        val tState =
                            rememberTooltipState(
                                isPersistent = true
                            )
                        TooltipBox(
                            state = tState,
                            tooltip = {
                                if (tab != null) {
                                    PlainTooltip() {
                                        Text(
                                            tabText.replace(",", "\n"),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            },
                            positionProvider = popupPositionProvider
                        ) {
                            Tab(
                                selected = selectedTabIndex.value == i,
                                onClick = {
                                    selectedTabIndex.value = i
                                },
                                text = {

                                    Box(Modifier.fillMaxWidth()) {
                                        Text(
                                            tabText,
                                            overflow = TextOverflow.Ellipsis,
                                            maxLines = 2,
                                            fontSize = MaterialTheme.typography.titleSmall.fontSize,
                                            modifier = Modifier.align(Alignment.Center)
                                        )
                                        if (tab != null) {
                                            IconButton(
                                                onClick = {
                                                    component.homeTasksTabDialogComponent.onEvent(
                                                        CAlertDialogStore.Intent.ShowDialog
                                                    )
                                                    component.onEvent(
                                                        LessonReportStore.Intent.UpdateTabLoginsId(
                                                            tab
                                                        )
                                                    )
                                                },
                                                modifier = Modifier.size(20.dp)
                                                    .align(Alignment.CenterEnd)
                                            ) {
                                                GetAsyncIcon(
                                                    RIcons.Edit,
                                                    size = 15.dp
                                                )
                                            }
                                        }
                                    }
                                },
                                modifier = Modifier.width(
                                    (((this@BoxWithConstraints.maxWidth / tabs.count()
                                        .toFloat()) - 1.dp) - ((if (component.model.value.isEditable) 80.dp else 0.dp) / tabs.count())).coerceAtLeast(
                                        200.dp
                                    )
                                )
                            )
                        }
                    }
                    if (component.model.value.isEditable) {
                        IconButton(
                            onClick = {
                                component.homeTasksTabDialogComponent.onEvent(CAlertDialogStore.Intent.ShowDialog)
                                component.onEvent(LessonReportStore.Intent.UpdateTabLoginsId(null))
                            }
                        ) {
                            GetAsyncIcon(
                                RIcons.PersonAdd
                            )
                        }
                    }
                }
            }

            val tasks = model.hometasks.filter { it.studentLogins == tabs[selectedTabIndex.value] }
            tasks.forEach {
                ReportHomeTaskItem(
                    task = it,
                    component = component
                )
            }
            if (tasks.none { it.type == "" || it.text == "" } && component.model.value.isEditable) {
                TextButton(
                    onClick = {
                        component.onEvent(
                            LessonReportStore.Intent.AddEmptyHomeTask(
                                studentLogins = tabs[selectedTabIndex.value]
                            )
                        )
                    }
                ) {
                    Text("Добавить задание")
                }
            }
        }
        SaveAnimation(model.isHomeTasksSavedAnimation) {
            component.onEvent(LessonReportStore.Intent.IsHomeTasksSavedAnimation(false))
        }
        ErrorAnimation(
            textError = "Не удалось загрузить задания\nна сервер",
            isShowing = model.isHomeTasksErrorAnimation
        ) {
            component.onEvent(LessonReportStore.Intent.IsHomeTasksErrorAnimation(false))
        }


        CAlertDialogContent(
            component.homeTasksTabDialogComponent,
            acceptText = if (model.tabLogins == null) "Создать" else "Редактировать",
            isCustomButtons = false,
            title = if (model.tabLogins == null) "Новая группа" else "Обновить группу",
            isSaveButtonEnabled = model.newTabLogins.sorted() !in (model.homeTasksNewTabs.map { it.sorted() } + model.hometasks.map { it.studentLogins }
                .map { it?.sorted() }) && model.newTabLogins.isNotEmpty(),
        ) {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                model.students.forEach { s ->
                    val isChecked = s.login in model.newTabLogins
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.cClickable(component.model.value.isEditable) {
                            if (!isChecked && s.login !in model.newTabLogins) {
                                component.onEvent(LessonReportStore.Intent.AddLoginToNewTab(s.login))
                            } else if (isChecked && s.login in model.newTabLogins) {
                                component.onEvent(
                                    LessonReportStore.Intent.DeleteLoginFromNewTab(
                                        s.login
                                    )
                                )
                            }
                        }) {
                        CustomCheckbox(
                            checked = isChecked
                        )
                        Spacer(Modifier.width(5.dp))
                        Text(s.shortFio)
                    }
                }
            }
        }

    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun ReportHomeTaskItem(
    task: CreateReportHomeworkItem,
    component: LessonReportComponent
) {
    val isStups = task.type != "" && task.type.subSequence(0, 3) == "!st"

    val isFullView = remember { mutableStateOf(task.isNew) }

    var expandedType by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier.padding(top = 6.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(15.dp))
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp))
            .animateContentSize()
    ) {

        if (isFullView.value) {
            Column(Modifier.padding(4.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    ExposedDropdownMenuBox(
                        expanded = expandedType,
                        onExpandedChange = {
                            expandedType = !expandedType
                        }
                    ) {
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(.6f)
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable), // menuAnchor modifier must be passed to the text field for correctness.
                            readOnly = true,
                            value = typesList[task.type] ?: "Выберите",
                            placeholder = { Text("Выберите") },
                            onValueChange = {},
                            label = { Text("Тип") },
                            trailingIcon = {
                                val chevronRotation = animateFloatAsState(if (expandedType) 90f else -90f)
                                GetAsyncIcon(
                                    path = RIcons.ChevronLeft,
                                    modifier = Modifier.padding(end = 10.dp).rotate(chevronRotation.value),
                                    size = 15.dp
                                )
                            },
                            shape = RoundedCornerShape(15.dp)
                        )
                        // menu
                        if (component.model.value.isEditable) {
                            ExposedDropdownMenu(
                                expanded = expandedType,
                                onDismissRequest = {
                                    expandedType = false
                                },
                            ) {
                                // menu items
                                typesList.forEach { selectionOption ->
                                    DropdownMenuItem(
                                        text = { Text(selectionOption.value) },
                                        onClick = {
                                            component.onEvent(
                                                LessonReportStore.Intent.ChangeHomeTaskType(
                                                    id = task.id,
                                                    type = selectionOption.key,
                                                    isNew = task.isNew
                                                )
                                            )
                                            expandedType = false
                                        },
                                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.width(8.dp))
                    if (isStups) {
                        CustomTextField(
                            value = if (task.stups == 0) "" else task.stups.toString(),
                            onValueChange = {
                                if (component.model.value.isEditable) {
                                    component.onEvent(
                                        LessonReportStore.Intent.ChangeHomeTaskAward(
                                            id = task.id,
                                            award = if (it == "") 0 else it.toInt(),
                                            isNew = task.isNew
                                        )
                                    )
                                }
                            },
                            text = "Награда",
                            supText = "макс: ${getMaxStupsCount(task.type)}",
                            isEnabled = true,
                            isMoveUpLocked = true,
                            autoCorrect = true,
                            keyboardType = KeyboardType.Text,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        FilesButton(
                            Modifier.padding(top = 7.dp).height(TextFieldDefaults.MinHeight)
                        )
                    }
                }

                CustomTextField(
                    value = task.text,
                    onValueChange = {
                        if (component.model.value.isEditable) {
                            component.onEvent(
                                LessonReportStore.Intent.ChangeHomeTaskText(
                                    id = task.id,
                                    text = it,
                                    isNew = task.isNew
                                )
                            )
                        }
                    },
                    isEnabled = true,
                    text = "Задание",
                    supText = "Текст задания",
                    isSingleLine = false,
                    //focusManager = focusManager,
                    isMoveUpLocked = true,
                    autoCorrect = true,
                    keyboardType = KeyboardType.Text,
                    modifier = Modifier.fillMaxWidth()
                )
                if (isStups) {
                    FilesButton()
                }
                Spacer(Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.cClickable(isEnabled = component.model.value.isEditable) {
                        component.onEvent(
                            LessonReportStore.Intent.ChangeHomeTaskIsNec(
                                id = task.id,
                                isNec = !task.isNec,
                                isNew = task.isNew
                            )
                        )
                    }.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text("Обязательное задание")
                    Spacer(Modifier.width(6.dp))
                    CustomCheckbox(
                        checked = task.isNec
                    )
                }
            }
        } else {
            Column(Modifier.padding(4.dp).padding(start = 4.dp)) {
                Text(
                    buildAnnotatedString {
                        withStyle(SpanStyle(fontSize = 18.esp, fontWeight = FontWeight.Bold)) {
                            append("Тип: ")
                        }
                        append(typesList[task.type] ?: "Не выбрано")
                        if (task.stups > 0) {
                            append(" (+${task.stups})")
                        }
                    }
                )

                Text("${if (task.isNec) "*" else ""}${task.text}")
            }
        }
        IconButton(
            onClick = {
                isFullView.value = !isFullView.value
            },
            modifier = Modifier.size(20.dp).align(Alignment.TopEnd)
        ) {
            GetAsyncIcon(
                RIcons.Menu
            )
        }
    }
}


@Composable
private fun FilesButton(modifier: Modifier = Modifier) {
    FilledTonalButton(
        onClick = {},
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(15.dp)
    ) {
        Text("///")
    }
}

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalLayoutApi::class
)
@Composable
private fun SetupTabContent(
    component: LessonReportComponent,
    isLikeMenuOpened: MutableState<Boolean>
) {
    val model by component.model.subscribeAsState()

    val nModel by component.nInterface.networkModel.subscribeAsState()
    Column(
        Modifier.verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)
            .padding(start = 30.dp)
    ) {

        CustomTextField(
            value = model.topic,
            onValueChange = {
                if (component.model.value.isEditable) {
                    component.onEvent(LessonReportStore.Intent.ChangeTopic(it))
                }
                //component.onEvent(UsersStore.Intent.ChangeESurname(it))
            },
            text = "Тема урока",
            isEnabled = true, //!isEditingInProcess,
            onEnterClicked = {
                //focusManager.moveFocus(FocusDirection.Next)

            },
            isSingleLine = false,
            //focusManager = focusManager,
            isMoveUpLocked = true,
            autoCorrect = true,
            keyboardType = KeyboardType.Text,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(10.dp))
        AnimatedVisibility(isLikeMenuOpened.value && model.students.isNotEmpty()) {
            FlowRow(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                model.students.sortedBy { it.shortFio }.forEach { student ->
                    val containerColor = animateColorAsState(
                        when (student.login) {
                            in model.likedList -> {
                                Color.Green.copy(alpha = .1f)
                            }

                            in model.dislikedList -> {
                                Color.Red.copy(alpha = .1f)
                            }

                            else -> {
                                MaterialTheme.colorScheme.primary.copy(
                                    alpha = 0.1f
                                )
                            }
                        }
                    )
                    Card(
                        Modifier.padding(5.dp),
                        //elevation = CardDefaults.elevatedCardElevation(defaultElevation = 15.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = containerColor.value)
                    ) {
                        Row(
                            Modifier.padding(5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(student.shortFio, maxLines = 1)
                            Spacer(Modifier.width(2.dp))
                            LikeDislikeRow(
                                component = component,
                                student = student
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(10.dp))
        }
        CustomTextField(
            value = model.description,
            onValueChange = {
                if (component.model.value.isEditable) {
                    component.onEvent(LessonReportStore.Intent.ChangeDescription(it))
                }
                //component.onEvent(UsersStore.Intent.ChangeESurname(it))
            },
            text = "Примечания",
            isEnabled = true, //!isEditingInProcess,
            onEnterClicked = {
                //focusManager.moveFocus(FocusDirection.Next)

            },
            isSingleLine = false,
            //focusManager = focusManager,
            isMoveUpLocked = true,
            autoCorrect = true,
            keyboardType = KeyboardType.Text,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(10.dp))
        FlowRow(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            val isChecked = model.isMentorWas
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.cClickable(component.model.value.isEditable) {
                    component.onEvent(LessonReportStore.Intent.ChangeIsMentorWas)
                }) {
                Text("Наставник")
                CustomCheckbox(
                    checked = isChecked,
                    modifier = Modifier.padding(start = 10.dp).size(25.dp)
                )
            }
            if (model.editTime.isNotEmpty()) {
                Box(modifier = Modifier.height(48.dp), contentAlignment = Alignment.Center) {
                    Text("Изменено: ${model.editTime}")
                }
            }
            //MAKE BUTTON FOR RETRY
            if (component.model.value.isEditable) {
                AnimatedElevatedButton(
                    text = if (nModel.state != NetworkState.Error) "Сохранить" else "Ещё раз",
                    isEnabled = nModel.state != NetworkState.Error && model.isUpdateNeeded,
                    //                modifier = Modifier.height(48.dp)
                ) {
                    component.onEvent(LessonReportStore.Intent.UpdateWholeReport)
                }
            }
        }
        Spacer(Modifier.height(30.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun MarksTabContent(
    component: LessonReportComponent
) {
    val isExpanded =
        (LocalViewManager.current.size?.maxWidth ?: 0.dp) >= BottomSheetDefaults.SheetMaxWidth

    val dzColumnsList = listOf(
        MarkColumn("dzПисьм. работа", "!dz1"),
        MarkColumn("dzРешение задач", "!dz2"),
        MarkColumn("dzУстно", "!dz3"),
//                                    "Другое"
        MarkColumn("dzДругое", "!dz4"),
    )

    val clColumnsList = listOf(
        MarkColumn("clК/Р", "!cl1"),
        MarkColumn("clС/Р", "!cl2"),
        MarkColumn("clТест", "!cl3"),
        MarkColumn("clПисьм. работа", "!cl4"),
        MarkColumn("clРабота на уроке", "!cl5"),
//                                    "Другое"
    )

    //Ступени:
    //ДЗ -> !st1
    //Работа на уроке -> !st2
    //М/К -> !st3
    //Тетрадь -> !st4
    //Личностный рост -> !st5

    val stColumnsList = listOf(
        MarkColumn("stДЗ", "!st1"),
        MarkColumn("stМ/К", "!st2"),
//        MarkColumn("stТетрадь", "!st3"),
        MarkColumn("stУрок", "!st4"),
        MarkColumn("stРост", "!st5")
//                                    "Другое"
    )


    //Ступени:
    //Готовность -> !ds1
    //Поведение -> !ds2
    //Нарушение -> !ds3

    val dsColumnsList = listOf(
        MarkColumn("dsГотовность", "!ds1"),
        MarkColumn("dsПоведение", "!ds2"),
        MarkColumn("dsНарушение", "!ds3")
//                                    "Другое"
    )

    //IDK WHY
    val coroutine = rememberCoroutineScope()
    val dzBringIntoViewRequester = BringIntoViewRequester()
    val clBringIntoViewRequester = BringIntoViewRequester()
    val stBringIntoViewRequester = BringIntoViewRequester()
    val dsBringIntoViewRequester = BringIntoViewRequester()
    val endBringIntoViewRequester = BringIntoViewRequester()
    if (isExpanded) {
        Row(Modifier.padding(horizontal = 20.dp).verticalScroll(rememberScrollState())) {
            Column(
                Modifier.fillMaxWidth(.5f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ColumnsSettingsItem(
                    title = "Домашняя работа",
                    component = component,
                    columnsList = dzColumnsList,
                    currentId = "!dz",
                    bringIntoViewRequester = dzBringIntoViewRequester
                ) {}
                Spacer(Modifier.height(5.dp))

                ColumnsSettingsItem(
                    title = "Ступени успеха",
                    component = component,
                    columnsList = stColumnsList,
                    currentId = "!st",
                    bringIntoViewRequester = stBringIntoViewRequester
                ) {}
                Spacer(Modifier.height(30.dp))
            }
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                ColumnsSettingsItem(
                    title = "Классная работа",
                    component = component,
                    columnsList = clColumnsList,
                    currentId = "!cl",
                    bringIntoViewRequester = clBringIntoViewRequester
                ) {}
                Spacer(Modifier.height(5.dp))

                ColumnsSettingsItem(
                    title = "Дисциплина",
                    component = component,
                    columnsList = dsColumnsList,
                    currentId = "!ds",
                    bringIntoViewRequester = dsBringIntoViewRequester
                ) {}
            }
        }
    } else {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
//                                        .width(300.dp)
        ) {
            ColumnsSettingsItem(
                title = "Домашняя работа",
                component = component,
                columnsList = dzColumnsList,
                currentId = "!dz",
                bringIntoViewRequester = dzBringIntoViewRequester
            ) {
                coroutine.launch {
                    delay(100)
                    clBringIntoViewRequester.bringIntoView()
                }
            }
            Spacer(Modifier.height(5.dp))

            ColumnsSettingsItem(
                title = "Классная работа",
                component = component,
                columnsList = clColumnsList,
                currentId = "!cl",
                bringIntoViewRequester = clBringIntoViewRequester
            ) {
                coroutine.launch {
                    delay(100)
                    stBringIntoViewRequester.bringIntoView()
                }
            }
            Spacer(Modifier.height(5.dp))

            ColumnsSettingsItem(
                title = "Ступени успеха",
                component = component,
                columnsList = stColumnsList,
                currentId = "!st",
                bringIntoViewRequester = stBringIntoViewRequester
            ) {
                coroutine.launch {
                    delay(100)
                    dsBringIntoViewRequester.bringIntoView()
                }
            }
            Spacer(Modifier.height(5.dp))

            ColumnsSettingsItem(
                title = "Дисциплина",
                component = component,
                columnsList = dsColumnsList,
                currentId = "!ds",
                bringIntoViewRequester = dsBringIntoViewRequester
            ) {
                coroutine.launch {
                    delay(100)
                    endBringIntoViewRequester.bringIntoView()
                }
            }
            //Kost
            //Empty Composables can't bringIntoView
            Text(
                text = "Hi",
                color = MaterialTheme.colorScheme.surfaceColorAtElevation(BottomSheetDefaults.Elevation),
                modifier = Modifier.height(30.dp)
                    .bringIntoViewRequester(endBringIntoViewRequester)
            )
        }
    }
}

private fun getColumnNamePrefix(reasonId: String): String {
    return try {

        when (reasonId.subSequence(
            0,
            3
        )) {
            "!dz" -> "ДЗ"
            "!cl" -> "Кл/Р"
            "!st" -> "Ступени"
            "!ds" -> "Дисциплина"
            else -> "null"
        }
    } catch (_: Throwable) {
        "null"
    }
}

@OptIn(ExperimentalFoundationApi::class)
@ExperimentalMaterial3Api
@Composable
private fun ColumnsSettingsItem(
    title: String,
//    isDropDownOpened: MutableState<Boolean>,
    columnsList: List<MarkColumn>,
    component: LessonReportComponent,
    bringIntoViewRequester: BringIntoViewRequester,
    currentId: String,
    bringIntoRequest: () -> Unit
) {
    val model by component.model.subscribeAsState()
    val isExpanded =
        (LocalViewManager.current.size?.maxWidth ?: 0.dp) >= BottomSheetDefaults.SheetMaxWidth
    val coroutineScope = rememberCoroutineScope()
    val isOpened = remember { mutableStateOf(isExpanded) }
    val currentList = model.columnNames.filter {
        it.type.subSequence(0, 3) == currentId
    }
    val columnNames = model.columnNames.map { it.title }

    val sortedColumnList = columnsList.sortedBy {
        if (!isOpened.value) {
            it.title !in columnNames
        } else null
    }

    Column(
        Modifier.fillMaxWidth().animateContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.cClickable {
            isOpened.value = !isOpened.value
            bringIntoRequest()
        }) {
            Text(
                title,
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.bringIntoViewRequester(bringIntoViewRequester)
            )
            if (!isExpanded) {
                val chevronRotation = animateFloatAsState(if (isOpened.value) 90f else -90f)
                GetAsyncIcon(
                    path = RIcons.ChevronLeft,
                    modifier = Modifier.padding(end = 10.dp).rotate(chevronRotation.value),
                    size = 15.dp
                )
            } else {
                Spacer(Modifier.height(30.dp))
            }
        }
        val inColumn = sortedColumnList.filter { isOpened.value || it.title in columnNames }
        Column() {
            inColumn.forEach { i ->
                val isInProcess = remember { mutableStateOf(false) }
                val isChecked = (i.title in currentList.map { it.title })
                Row(
                    Modifier.cClickable(component.model.value.isEditable) {
                        if (!isInProcess.value) {
                            if (!isChecked) {
                                isInProcess.value = true
                                coroutineScope.launch {
                                    component.onEvent(
                                        LessonReportStore.Intent.CreateColumn(
                                            i.title,
                                            i.reasonId
                                        )
                                    )
                                    while (true) {
                                        if (i.title in model.columnNames.map { it.title }) {
                                            isInProcess.value = false
                                            break
                                        }
                                        delay(200)
                                    }
                                }
                            } else {
                                if (currentList.find { it.title == i.title } != null) {

                                    component.onEvent(
                                        LessonReportStore.Intent.DeleteColumnInit(
                                            reportColumn = currentList.first { it.title == i.title }
                                        )
                                    )
                                    component.confirmDeletingColumnDialogComponent.onEvent(
                                        CAlertDialogStore.Intent.ShowDialog
                                    )
                                }
                            }
                        }
                    }.width(170.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        ("!${i.title}").removePrefix(currentId),
                        modifier = Modifier.width(145.dp),
                        maxLines=1,
                        overflow = TextOverflow.Ellipsis
                    )
                    CustomCheckbox(
                        checked = isChecked,
                        modifier = Modifier.size(25.dp)
                    )
                }
                Spacer(Modifier.height(6.dp))
            }
        }
    }
}


@ExperimentalMaterial3Api
@Composable
fun LessonTable(
    component: LessonReportComponent,
//    currentParentWidth: Dp
) {
    val model by component.model.subscribeAsState()

//    val viewManager = LocalViewManager.current

    val density = LocalDensity.current
    val vScrollState = rememberLazyListState()
    val hScrollState = rememberScrollState()

    val allHeight = remember { mutableStateOf(0.dp) }
    val allWidth = remember { mutableStateOf(0.dp) }
    val lP = 50.dp

    allWidth.value = model.columnNames.map {
        when (it.type) {
            ColumnTypes.prisut -> 150.dp
            ColumnTypes.opozdanie -> 104.dp
            ColumnTypes.srBall -> 50.dp
            else -> 150.dp
        }
    }.fastSumBy { it.value.toInt() }.dp + lP

    allHeight.value = 25.dp + (model.students.size * 55.dp)


    ScrollBaredBox(
        vState = vScrollState, hState = hScrollState,
        height = allHeight, width = allWidth,
        modifier = Modifier.animateContentSize()
    ) {
        Box(Modifier.horizontalScroll(hScrollState)) {
            Row() {//modifier = Modifier.horizontalScroll(hhScrollState)
//            Divider(Modifier.height(allHeight.value).width(1.dp))
                Spacer(Modifier.width(lP))
                model.columnNames.onEachIndexed { index, i ->
                    if (index != model.columnNames.lastIndex) {
                        val width: Dp = when (i.type) {
                            ColumnTypes.prisut -> 150.dp - 1.5.dp
                            ColumnTypes.opozdanie -> 104.dp - 1.5.dp
                            ColumnTypes.srBall -> 50.dp - 1.5.dp
                            else -> 150.dp - 1.5.dp
                        }
                        Spacer(Modifier.width(width))
                        VerticalDivider(
                            Modifier.height(allHeight.value).padding(vertical = 1.dp),
                            thickness = (1.5).dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = .4f)
                        )
                    }
                }


            }
            Column(
                modifier = Modifier
            ) {
                Row(
                    modifier = Modifier,
//                        .onGloballyPositioned { layoutCoordinates ->
//                        allWidth.value =
//                            with(density) { layoutCoordinates.size.width.toDp() + lP / 4 }
//                    }, //.horizontalScroll(hhScrollState)
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Spacer(Modifier.width(lP))
                    model.columnNames.forEach { column ->

//                        val isChecked = remember { mutableStateOf(false) }


                        Box(
                            modifier = Modifier.width(
                                when (column.type) {
                                    ColumnTypes.prisut -> 150.dp
                                    ColumnTypes.opozdanie -> 104.dp
                                    ColumnTypes.srBall -> 50.dp
                                    else -> 150.dp
                                }
                            ),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Box() {
                                    when (column.type.subSequence(0, 3)) {
                                        "!dz" -> {
                                            GetAsyncIcon(
                                                RIcons.Home,
                                                size = 14.dp,
                                                modifier = Modifier.offset(y = (0).dp, x = -1.dp)
                                            )
                                        }

                                        "!st" -> {
                                            GetAsyncIcon(
                                                RIcons.Star,
                                                size = 14.dp,
                                                modifier = Modifier.offset(y = (0).dp, x = -1.dp)
                                            )
                                        }

                                        "!ds" -> {
                                            GetAsyncIcon(
                                                RIcons.Shield,
                                                size = 14.dp,
                                                modifier = Modifier.offset(y = (0).dp, x = -1.dp)
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = column.title.removePrefix("dz").removePrefix("cl")
                                        .removePrefix("st").removePrefix("ds"),
                                    fontWeight = FontWeight.ExtraBold,
                                    textAlign = TextAlign.Center,

                                    overflow = TextOverflow.Ellipsis,
                                    softWrap = false
                                )
                            }
                        }
                    }
                }


                HorizontalDivider(
                    Modifier.padding(start = 1.dp).width(allWidth.value - 1.dp)//.height(1.dp)
                    , color = MaterialTheme.colorScheme.outline.copy(alpha = .4f),
                    thickness = 1.5.dp
                )

                LazyColumn(
                    modifier = Modifier,
                    state = vScrollState,
                ) {
                    itemsIndexed(items = model.students.sortedBy { it.shortFio }) { index, student ->
                        val fioColor =
                            MaterialTheme.colorScheme
                                .onSurface.blend(
                                    when (student.login) {
                                        in model.likedList -> Color.Green
                                        in model.dislikedList -> Color.Red
                                        else -> MaterialTheme.colorScheme
                                            .onSurface
                                    }
                                )

                        val isPersonWasOnLesson = student.attended?.attendedType in listOf("0", null)

                        
                        Column {
                            Row(
                                modifier = Modifier
                                    .padding(start = 10.dp)
                                    .offset(with(density) { hScrollState.value.toDp() }),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = student.shortFio,
                                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                    fontWeight = FontWeight.Normal,
                                    color = fioColor,
                                    modifier = Modifier
                                )
                                Spacer(Modifier.width(10.dp))
                                LikeDislikeRow(
                                    component = component,
                                    student = student
                                )
                            }
                            Row(Modifier.padding(start = lP)) {
                                model.columnNames.forEach { column ->
//                                    val isChecked = remember { mutableStateOf(false) }
                                    Box(
                                        modifier = Modifier.width(
                                            when (column.type) {
                                                ColumnTypes.prisut -> 150.dp
                                                ColumnTypes.opozdanie -> 104.dp
                                                ColumnTypes.srBall -> 50.dp
                                                else -> 150.dp
                                            }
                                        ),
//                                            .onGloballyPositioned {
//                                                val width =
//                                                    with(density) { it.size.width.toDp() }
//                                                if (width > widths[column.title]!!) widths[column.title] =
//                                                    width
//                                                else isChecked.value = true
//                                            }.then(
//                                                if (!isChecked.value) Modifier.width(
//                                                    IntrinsicSize.Min
//                                                )
//                                                else Modifier.width(widths[column.title]!!)
//                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Box(modifier = Modifier.height(25.dp)) {
                                            if (column.type in listOf(
                                                    ColumnTypes.opozdanie,
                                                    ColumnTypes.prisut,
                                                    ColumnTypes.srBall
                                                )
                                            ) {
                                                when (column.type) {
                                                    ColumnTypes.prisut -> {
                                                        val isDot = student.attended?.reason != null
                                                        Row(
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            modifier = Modifier.padding(end = if (isDot) 6.dp else 0.dp)
                                                        ) {
                                                            if (isDot) {
                                                                Box(
                                                                    Modifier
                                                                        .size(5.dp).clip(
                                                                            CircleShape
                                                                        )
                                                                        .background(MaterialTheme.colorScheme.primary)
                                                                )
                                                            }
                                                            Spacer(Modifier.width(3.dp))
                                                            PrisutCheckBox(
                                                                modifier = Modifier.size(25.dp),
                                                                attendedType = student.attended?.attendedType
                                                                    ?: "0",
                                                                reason = student.attended?.reason,
                                                                enabled = model.isEditable
                                                            ) {
                                                                component.onEvent(
                                                                    LessonReportStore.Intent.ChangeAttendance(
                                                                        studentLogin = student.login,
                                                                        attendedType = it
                                                                    )
                                                                )
                                                            }
                                                        }
                                                    }

                                                    ColumnTypes.opozdanie -> {
                                                        Crossfade(student.lateTime) {
//                                                            var x by remember {
//                                                                mutableStateOf(
//                                                                    0.0f
//                                                                )
//                                                            }
//                                                            var y by remember {
//                                                                mutableStateOf(
//                                                                    0.0f
//                                                                )
//                                                            }
                                                            when (it) {
                                                                "0" -> Row(
                                                                    Modifier.fillMaxWidth(),
                                                                    verticalAlignment = Alignment.CenterVertically,
                                                                    horizontalArrangement = Arrangement.Center
                                                                ) {
                                                                    val lessonMinutes =
                                                                        model.time.toMinutes()
                                                                    val currentMinutes =
                                                                        getSixTime().toMinutes()
                                                                    if ((model.date == getDate() && lessonMinutes <= currentMinutes && currentMinutes - lessonMinutes <= 40) && model.isEditable) {
                                                                        FilledTonalButton(
                                                                            enabled =  isPersonWasOnLesson,
                                                                            contentPadding = PaddingValues(
                                                                                horizontal = 5.dp
                                                                            ), onClick = {
                                                                                component.onEvent(
                                                                                    LessonReportStore.Intent.SetLateTime(
                                                                                        student.login,
                                                                                        "auto"
                                                                                    )
                                                                                )
                                                                            }) {
                                                                            Text("Опозд.")
                                                                        }
                                                                    }
                                                                    if (component.model.value.isEditable) {
                                                                        Box() {
                                                                            IconButton(
                                                                                enabled =  isPersonWasOnLesson,
                                                                                modifier = Modifier.width(
                                                                                    30.dp
                                                                                )
                                                                                , onClick = {
                                                                                    component.onEvent(
                                                                                        LessonReportStore.Intent.OpenSetLateTimeMenu(
                                                                                            student.login,
                                                                                            x = 0f,
                                                                                            y = 0f
                                                                                        )
                                                                                    )
                                                                                }) {
                                                                                GetAsyncIcon(
                                                                                    RIcons.MoreVert
                                                                                )
                                                                            }
                                                                            if (model.selectedLogin == student.login) {
                                                                                ListDialogDesktopContent(
                                                                                    component.setLateTimeMenuComponent,
                                                                                    offset = DpOffset(
                                                                                        x = 27.dp,
                                                                                        y = -18.dp
                                                                                    ),
                                                                                    isFullHeight = true
                                                                                )
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                else -> Row(
                                                                    Modifier.fillMaxWidth(),
                                                                    verticalAlignment = Alignment.CenterVertically,
                                                                    horizontalArrangement = Arrangement.Center
                                                                ) {
                                                                    Text(
                                                                        it,
                                                                        fontWeight = FontWeight.Bold
                                                                    )
                                                                    if (component.model.value.isEditable) {
                                                                        IconButton(
                                                                            modifier = Modifier.width(
                                                                                30.dp
                                                                            ),
                                                                            onClick = {
                                                                                component.onEvent(
                                                                                    LessonReportStore.Intent.SetLateTime(
                                                                                        student.login,
                                                                                        "0"
                                                                                    )
                                                                                )
                                                                            }) {
                                                                            GetAsyncIcon(
                                                                                RIcons.Close
                                                                            )
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }

                                                    ColumnTypes.srBall -> {
                                                        val marks =
                                                            student.marksOfCurrentLesson.filter { it.isGoToAvg }
                                                        val value =
                                                            (student.avgMark.previousSum + marks.sumOf { it.value.toInt() }) / (student.avgMark.countOfMarks + marks.size).toFloat()

                                                        if (value.isNaN()) {
                                                            Text(
                                                                text = "NaN",
                                                                fontWeight = FontWeight.Black
                                                            )
                                                        } else {
                                                            CustomTextButton(
                                                                text = value.roundTo(2),
                                                                fontWeight = FontWeight.Black,
                                                                color = MaterialTheme.colorScheme.onSurface
                                                            ) {
                                                                component.onEvent(
                                                                    LessonReportStore.Intent.OpenDetailedMarks(
                                                                        student.login
                                                                    )
                                                                )
                                                            }
                                                        }
                                                    }

                                                    else -> Text(
                                                        text = column.title
                                                    )
                                                }
                                            } else if (column.type.subSequence(
                                                    0,
                                                    3
                                                ) !in listOf("!st", "!ds")
                                            ) {

                                                val marks =
                                                    student.marksOfCurrentLesson.filter { it.reason == column.type }
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.Center,
                                                ) {
                                                    marks.forEachIndexed { index, mark ->
//                                                        var x by remember { mutableStateOf(0.0f) }
//                                                        var y by remember { mutableStateOf(0.0f) }
                                                        Box() {
                                                            MarkContent(
                                                                mark = if (mark.value == "+2") "Д" else mark.value,
                                                                offset = DpOffset(0.dp, -2.dp),
                                                                background = if (student.login == model.selectedLogin && column.type == model.selectedMarkReason && index.toString() == model.selectedMarkValue) {
                                                                    MaterialTheme.colorScheme.primary.copy(
                                                                        alpha = .2f
                                                                    ).hv()
                                                                } else {
                                                                    MaterialTheme.colorScheme.primary.copy(
                                                                        alpha = .2f
                                                                    )
                                                                },
                                                                addModifier = Modifier
                                                                    .clickable(enabled = model.isEditable) {
                                                                        component.onEvent(
                                                                            LessonReportStore.Intent.OpenDeleteMarkMenu(
                                                                                reasonId = column.type,
                                                                                studentLogin = student.login,
                                                                                markValue = index,
                                                                                selectedDeploy = "${mark.deployLogin}: ${mark.deployDate} (${mark.deployTime})"
                                                                            )
                                                                        )
                                                                    },
                                                                paddingValues = PaddingValues(end = if (index != 3) 5.dp else 0.dp)
                                                            )
                                                            if (model.selectedMarkValue == index.toString()
                                                                && (model.selectedLogin == student.login)
                                                                && (model.selectedMarkReason == column.type)
                                                            ) {
                                                                ListDialogDesktopContent(
                                                                    component.deleteMarkMenuComponent,
                                                                    offset = DpOffset(
                                                                        x = 27.dp,
                                                                        y = -18.dp
                                                                    ),
                                                                    title = if (model.isModer) "Выставил ${mark.deployLogin}\nв ${mark.deployDate} (${mark.deployTime})" else null,
                                                                    isFullHeight = true
                                                                )
                                                            }
                                                        }
                                                    }

                                                    if (marks.size != 4 && model.isEditable) {
                                                        Box() {
                                                            Box(
                                                                Modifier.offset(y = -2.dp)
                                                                    //.padding(start = 5.dp)
                                                                    .size(25.dp)
                                                                    .clip(RoundedCornerShape(percent = 30))
                                                                    .background(
                                                                        if (student.login == model.selectedLogin && column.type == model.selectedMarkReason && model.selectedMarkValue.isBlank()) {
                                                                            MaterialTheme.colorScheme.primary.copy(
                                                                                alpha = .2f
                                                                            ).hv()
                                                                        } else {
                                                                            MaterialTheme.colorScheme.primary.copy(
                                                                                alpha = .2f
                                                                            )
                                                                        }

                                                                    )
                                                                    .clickable {
                                                                        component.onEvent(
                                                                            LessonReportStore.Intent.OpenSetMarksMenu(
                                                                                reasonId = column.type,
                                                                                studentLogin = student.login,
                                                                                x = 0f,
                                                                                y = 0f
                                                                            )
                                                                        )
                                                                    },
                                                                contentAlignment = Alignment.Center
                                                            ) {
                                                                GetAsyncIcon(
                                                                    RIcons.Add,
                                                                    tint = MaterialTheme.colorScheme.onSurface
                                                                )
                                                            }
                                                            if (model.selectedMarkReason == column.type && model.selectedLogin == student.login) {
                                                                ListDialogDesktopContent(
                                                                    if (column.type.st == "!dz") component.setDzMarkMenuComponent else component.setMarkMenuComponent,
                                                                    offset = DpOffset(
                                                                        x = 27.dp,
                                                                        y = -18.dp
                                                                    ),
                                                                    isFullHeight = true,
                                                                    modifier = Modifier.setMarksBind(
                                                                        component
                                                                    )
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            } else {
                                                val reason =
                                                    column.type
                                                Stepper(
                                                    isEditable = model.isEditable,
                                                    count = (student.stupsOfCurrentLesson.firstOrNull { it.reason == column.type }
                                                        ?: Stup(
                                                            0,
                                                            "",
                                                            id = model.ids,
                                                            deployTime = "",
                                                            deployLogin = "",
                                                            deployDate = "",
                                                            custom = null
                                                        )).value,
                                                    maxCount =
                                                        getMaxStupsCount(reason),
                                                    minCount =
                                                        when (reason) {
                                                            "!st1" -> -1
                                                            "!ds1" -> -1
                                                            "!ds2" -> -3
                                                            "!ds3" -> -10
                                                            else -> 0
                                                        }
                                                ) {
                                                    component.onEvent(
                                                        LessonReportStore.Intent.ChangeStups(
                                                            login = student.login,
                                                            value = it,
                                                            columnReason = column.type
                                                        )
                                                    )
                                                }
                                            }

                                        }
                                    }
                                }
                            }
                            Spacer(Modifier.height(5.dp))
                            if (index != model.students.lastIndex) {
                                HorizontalDivider(
                                    Modifier.padding(start = 1.dp)
                                        .width(allWidth.value - 1.dp),
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = .4f),
                                    thickness = 1.dp
                                )
                            }
                        }
                    }
                }
            }


        }


    }
    val detailedMarksName =
        model.students.firstOrNull { it.login == model.detailedMarksLogin }?.shortFio ?: "null"

    val avg = (model.detailedMarks.sumOf { it.content.toInt() } / max(
        model.detailedMarks.size,
        1
    ).toFloat()).roundTo(2)

    CAlertDialogContent(
        component = component.marksDialogComponent,
        title = "Оценки: $detailedMarksName $avg",
        titleXOffset = 5.dp
    ) {
        Column(Modifier.verticalScroll(rememberScrollState())) {
            model.detailedMarks.forEach {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 1.dp)
                        .padding(horizontal = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(it.date)
                    Text(fetchReason(it.reason))
                    MarkContent(it.content)
                }
            }
        }
    }

}

@Composable
private fun LikeDislikeRow(
    component: LessonReportComponent,
    student: StudentLine
) {
    if (component.model.value.isEditable) {
        IconButton(onClick = {
            component.onEvent(LessonReportStore.Intent.LikeStudent(student.login))
        }, modifier = Modifier.size(20.dp)) {
            GetAsyncIcon(
                path = RIcons.Like
            )
        }
        Spacer(Modifier.width(7.dp))
        IconButton(onClick = {
            component.onEvent(
                LessonReportStore.Intent.DislikeStudent(
                    student.login
                )
            )
        }, modifier = Modifier.size(20.dp)) {
            GetAsyncIcon(
                path = RIcons.Like,
                modifier = Modifier.rotate(180f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrisutCheckBox(
    modifier: Modifier,
    attendedType: String,
    reason: String?,
    enabled: Boolean,
    onCheckedChange: (String) -> Unit
) {
    val tState = rememberTooltipState(isPersistent = true)
    TooltipBox(
        state = tState,
        tooltip = {
            if (reason != null) {
                PlainTooltip() {
                    Text(
                        reason.toString()
                    )
                }
            }
        },
        positionProvider = popupPositionProvider,
        enableUserInput = true
    ) {

        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            OutlinedCard(
                modifier = Modifier
                    .fillMaxSize(),//.alpha(if (checked) 1f else .5f),
                shape = AbsoluteRoundedCornerShape(40),
                border = BorderStroke(
                    color = if (attendedType == "0") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant, //if (checked) MaterialTheme.colorScheme.surface else
                    width = 1.dp
                ),
                onClick = {
                    //0-bil. 1-n. 2-Uv
                    if (enabled) {
                        val newValue = when (attendedType) {
                            "0" -> "1"
                            "1" -> "2"
                            else -> "0"
                        }
                        onCheckedChange(newValue)
                    }
                }) {
                AnimatedVisibility(attendedType == "0") {
                    GetAsyncIcon(
                        path = RIcons.Check,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.fillMaxSize()
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }
                AnimatedVisibility(attendedType == "1") {
                    Text(
                        text = "Н",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxSize(),
                        textAlign = TextAlign.Center
                    )
                }
                AnimatedVisibility(attendedType == "2") {
                    Text(
                        text = "УВ",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxSize(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

    }
}

private fun getMaxStupsCount(reason: String) = when (reason) {
    "!ds1" -> 1
    "!ds2" -> 1
    "!ds3" -> 0
    "!st5" -> 1
    else -> 3
}

//val isCollapsed = remember { derivedStateOf { scrollBehavior.state.collapsedFraction > 0.5 } }
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonReportTopBar(
    component: LessonReportComponent,
    isFullView: Boolean,
//    isVisible: Boolean
) {
    val model by component.model.subscribeAsState()
    AppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ReportTitle(
                    subjectName = model.subjectName,
                    groupName = model.groupName,
                    lessonReportId = model.lessonReportId,
                    teacher = model.teacherName,
                    time = model.time,
                    date = model.date,
                    isFullView = isFullView,
                    isStartPadding = true,
                    isEnded = model.status, //false,
                    module = model.module
                ) {
                    component.onEvent(LessonReportStore.Intent.ChangeInfoShowing)
                }
                if (isFullView) {
                    Text(
                        text = model.topic.ifEmpty { "Тема не выставлена" },
                        modifier = Modifier.fillMaxWidth().padding(start = 3.dp),
                        textAlign = TextAlign.Center,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2
                    )
                }
            }
        },
        navigationRow = {
            backAB(component)
        },
        actionRow = {
            settingsAB(component)
//            refreshAB(component)
        }
    )

}

@Composable
private fun settingsAB(
    component: LessonReportComponent
) {
    IconButton(
        onClick = {
            component.setReportColumnsComponent.onEvent(CBottomSheetStore.Intent.ShowSheet)
        }
    ) {
        GetAsyncIcon(
            path = RIcons.Menu
        )
    }
}

@Composable
private fun backAB(
    component: LessonReportComponent
) {
    IconButton(
        onClick = {
            component.onOutput(LessonReportComponent.Output.Back)
        }
    ) {
        GetAsyncIcon(
            path = RIcons.ChevronLeft
        )
    }
}

@Composable
fun Modifier.setMarksBind(
    component: LessonReportComponent
): Modifier {
    return this then Modifier.onKeyEvent {
        if (it.key in listOf(
                Key.Two, Key.Three, Key.Four, Key.Five,
                Key.NumPad2, Key.NumPad3, Key.NumPad4, Key.NumPad5
            ) && it.type == KeyEventType.KeyDown
        ) {
            var mark = it.key.toString().split(" ", "-").last()
            if (mark.toInt() > 9) {
                mark = (mark.last().code + 4).toString().last().toString()
            }
            component.setMarkMenuComponent.onClick(
                ListItem(
                    id = mark,
                    text = "no"
                )
            )
            component.setMarkMenuComponent.onEvent(ListDialogStore.Intent.HideDialog)
            component.setDzMarkMenuComponent.onEvent(ListDialogStore.Intent.HideDialog)
        }
        false
    }
}
