import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.desktop.ui.tooling.preview.utils.esp
import androidx.compose.desktop.ui.tooling.preview.utils.popupPositionProvider
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
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
import androidx.compose.material3.rememberTooltipState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.ErrorAnimation
import components.GetAsyncIcon
import components.SaveAnimation
import components.cAlertDialog.CAlertDialogStore
import components.cBottomSheet.CBottomSheetStore
import components.foundation.AnimatedElevatedButton
import components.foundation.AppBar
import components.foundation.CCheckbox
import components.foundation.CTextButton
import components.foundation.CTextField
import components.foundation.DefaultErrorView
import components.foundation.DefaultErrorViewPos
import components.foundation.cClickable
import components.foundation.hazeUnder
import components.journal.MarkContent
import components.journal.ReportTitle
import components.journal.TeacherTime
import components.listDialog.ListDialogStore
import components.listDialog.ListItem
import components.networkInterface.NetworkState
import decomposeComponents.CAlertDialogContent
import decomposeComponents.CBottomSheetContent
import decomposeComponents.listDialogComponent.ListDialogMobileContent
import eu.wewox.minabox.MinaBox
import eu.wewox.minabox.MinaBoxItem
import homeTasksDialog.HomeTasksDialogStore
import homework.CreateReportHomeworkItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import layouts.TableCellOutline
import layouts.defaultMinaBoxTableModifier
import layouts.defaultScrollbarData
import lessonReport.ColumnTypes
import lessonReport.LessonReportComponent
import lessonReport.LessonReportStore
import lessonReport.MarkColumn
import lessonReport.ReportColumn
import lessonReport.StudentLine
import lessonReportUtils.LessonReportTableCell
import lessonReportUtils.LessonReportTableHeader
import lessonReportUtils.LessonReportTableTitle
import lessonReportUtils.getMaxStupsCount
import resources.RIcons
import server.fetchReason
import server.roundTo
import utils.LockScreenOrientation
import view.DefaultMultiPane
import view.LocalViewManager
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
//            val minSize = (LocalViewManager.current.size?.maxWidth ?: 0.dp)
            val isLikeMenuOpened =
                remember { mutableStateOf(false) } //mutableStateOf(minSize >= BottomSheetDefaults.SheetMaxWidth)
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

                                    SetupTabContent(component, isLikeMenuOpened)
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
                                                CTextButton("Кнопка") {
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
                                                        RIcons.SAVE
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
                                                    RIcons.HISTORY
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
                                RIcons.TUNE
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
                                    path = RIcons.THUMBTACK
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
                                RIcons.HOMEWORK
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
                                        RIcons.LIKE,
                                        size = 16.dp,
                                        modifier = Modifier.align(Alignment.TopStart)
                                    )
                                    GetAsyncIcon(
                                        RIcons.LIKE,
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
            Modifier.fillMaxSize(),
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
                                                RIcons.SAVE
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
                                                    RIcons.EDIT,
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
                                RIcons.PERSON_ADD
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
                        CCheckbox(
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
                                val chevronRotation =
                                    animateFloatAsState(if (expandedType) 90f else -90f)
                                GetAsyncIcon(
                                    path = RIcons.CHEVRON_LEFT,
                                    modifier = Modifier.padding(end = 10.dp)
                                        .rotate(chevronRotation.value),
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
                        CTextField(
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

                CTextField(
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
                    CCheckbox(
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
                RIcons.MENU
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

        CTextField(
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

        val isLikeMenuOpenedFully = isLikeMenuOpened.value && model.students.isNotEmpty()

        AnimatedVisibility(!isLikeMenuOpenedFully) {
            FlowRow(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                likesDislikesItems(
                    model = model,
                    component = component,
                    isAll = false
                )
            }
        }

        AnimatedVisibility(isLikeMenuOpenedFully) {
            FlowRow(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {

                likesDislikesItems(
                    model = model,
                    component = component,
                    isAll = true
                )
            }
        }
        CTextField(
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
                CCheckbox(
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

@Composable
fun likesDislikesItems(
    model: LessonReportStore.State,
    component: LessonReportComponent,
    isAll: Boolean
) {
    val filteredStudents =
        model.students.filter { (isAll) || (it.login in model.likedList || it.login in model.dislikedList) }
    filteredStudents
        .sortedBy { it.shortFio }.forEach { student ->
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
    if (filteredStudents.isNotEmpty()) {
        Spacer(Modifier.height(10.dp))
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
                    path = RIcons.CHEVRON_LEFT,
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
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    CCheckbox(
                        checked = isChecked,
                        modifier = Modifier.size(25.dp)
                    )
                }
                Spacer(Modifier.height(6.dp))
            }
        }
    }
}

private fun List<Float>.sumOf(range: IntRange): Float =
    range.sumOf { get(it).toDouble() }.toFloat()

@OptIn(ExperimentalFoundationApi::class)
@ExperimentalMaterial3Api
@Composable
fun LessonTable(
    component: LessonReportComponent,
//    currentParentWidth: Dp
) {
    val model by component.model.subscribeAsState()

//    val viewManager = LocalViewManager.current

    val density = LocalDensity.current

    val lP = 50.dp

    val columnsCount = model.columnNames.size
    val rowsCount = (model.students.size)


    val cellHeight = 57.dp

    val headerPadding = with(density) { (cellHeight - 30.dp).toPx() }
    MinaBox(
        modifier = defaultMinaBoxTableModifier,
        scrollBarData = defaultScrollbarData
    ) {
        items(
            count = columnsCount * rowsCount,
            layoutInfo = {
                val index = it + columnsCount
                val column = index % columnsCount
                val row = index / columnsCount
                val reportColumn = model.columnNames[column]

                val itemSizePx = with(density) {
                    DpSize(
                        width = getColumnWidth(reportColumn),
                        cellHeight
                    ).toSize()
                }
                val prevX = model.columnNames
                    .map { with(density) { getColumnWidth(it).toPx() } }.sumOf(0 until column)
                MinaBoxItem(
                    x = prevX,
                    y = itemSizePx.height * row - headerPadding,
                    width = itemSizePx.width,
                    height = itemSizePx.height
                )
            },
            key = { it }
        ) { index ->
            val columnIndex = index % columnsCount
            val studentIndex = (index / (columnsCount))

            TableCellOutline {
                Row(
                    Modifier.fillMaxSize().padding(bottom = 5.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.Bottom
                ) {
                    if (columnIndex == 0) {
                        Spacer(Modifier.width(lP))
                    }
                    LessonReportTableCell(
                        student = model.students[studentIndex],
                        column = model.columnNames[columnIndex],
                        model = model,
                        component = component
                    )
                }
            }
        }

        items(
            rowsCount,
            layoutInfo = {
                val itemSizePx = with(density) {
                    DpSize(
                        width = 400.dp,
                        height = cellHeight
                    ).toSize()
                }
                MinaBoxItem(
                    x = 0f,
                    y = itemSizePx.height * (it),
                    width = itemSizePx.width,
                    height = itemSizePx.height,
                    lockHorizontally = true
                )

            }
        ) { index ->
            Box(
                Modifier.fillMaxSize().padding(bottom = 3.dp),
                contentAlignment = Alignment.BottomStart
            ) {
                LessonReportTableTitle(model.students[index], model, component)
            }
        }

        items(
            count = columnsCount,
            layoutInfo = {
                val column = it % columnsCount
                val row = it / columnsCount
                val reportColumn = model.columnNames[column]

                val itemSizePx = with(density) {
                    DpSize(
                        width = getColumnWidth(reportColumn),
                        30.dp
                    ).toSize()
                }
                val prevX = model.columnNames
                    .map { with(density) { getColumnWidth(it).toPx() } }.sumOf(0 until column)
                MinaBoxItem(
                    x = prevX,
                    y = itemSizePx.height * row,
                    width = itemSizePx.width,
                    height = itemSizePx.height,
                    lockVertically = true
                )
            },
            key = { it - model.columnNames.size }
        ) { index ->
            TableCellOutline(backgroundColor = MaterialTheme.colorScheme.background) {
                LessonReportTableHeader(model.columnNames[index], lP)
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
fun LikeDislikeRow(
    component: LessonReportComponent,
    student: StudentLine
) {
    CompositionLocalProvider(
        LocalContentColor provides LocalContentColor.current
    ) {
        if (component.model.value.isEditable) {
            IconButton(onClick = {
                component.onEvent(LessonReportStore.Intent.LikeStudent(student.login))
            }, modifier = Modifier.size(20.dp)) {
                GetAsyncIcon(
                    path = RIcons.LIKE_OUTLINE
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
                    path = RIcons.LIKE_OUTLINE,
                    modifier = Modifier.rotate(180f)
                )
            }
        }
    }
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

private fun getColumnWidth(column: ReportColumn): Dp {
    return when (column.type) {
        ColumnTypes.PRISUT -> (150).dp + 50.dp //lP
        ColumnTypes.OPOZDANIE -> (104).dp
        ColumnTypes.SR_BALL -> (50).dp
        else -> 150.dp
    }
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
            path = RIcons.MENU
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
            path = RIcons.CHEVRON_LEFT
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
