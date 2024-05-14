import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
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
import server.fetchReason
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.compose.ui.util.fastSumBy
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.value.getValue
import components.AnimatedElevatedButton
import components.AppBar
import components.CustomTextButton
import components.CustomTextField
import components.MarkContent
import components.ReportTitle
import components.ScrollBaredBox
import components.TeacherTime
import components.cAlertDialog.CAlertDialogStore
import components.cBottomSheet.CBottomSheetStore
import components.networkInterface.NetworkState
import decomposeComponents.CAlertDialogContent
import decomposeComponents.CBottomSheetContent
import decomposeComponents.listDialogComponent.ListDialogContent
import decomposeComponents.listDialogComponent.ListDialogDesktopContent
import decomposeComponents.listDialogComponent.ListDialogMobileContent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import lessonReport.ColumnTypes
import lessonReport.LessonReportComponent
import lessonReport.LessonReportStore
import lessonReport.MarkColumn
import lessonReport.Stup
import lessonReport.opozdanie
import lessonReport.prisut
import lessonReport.srBall
import pullRefresh.PullRefreshIndicator
import pullRefresh.rememberPullRefreshState
import server.roundTo
import view.LocalViewManager
import view.LockScreenOrientation
import view.rememberImeState

@OptIn(ExperimentalFoundationApi::class)
@ExperimentalMaterial3Api
@ExperimentalLayoutApi
@Composable
fun LessonReportContent(
    component: LessonReportComponent
) {
    LockScreenOrientation(-1)
    val model by component.model.subscribeAsState()
    val nModel by component.nInterface.networkModel.subscribeAsState()
    val focusManager = LocalFocusManager.current
    val viewManager = LocalViewManager.current
//    val scrollState = rememberScrollState()
    val imeState = rememberImeState()
    val lazyListState = rememberLazyListState()
    //PullToRefresh
    val refreshScope = rememberCoroutineScope()
    var refreshing by remember { mutableStateOf(false) }
    fun refresh() = refreshScope.launch {
        refreshing = true
        delay(1000)
        refreshing = false
    }
//    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val refreshState = rememberPullRefreshState(refreshing, ::refresh)
    BoxWithConstraints {

        val isFullView by mutableStateOf(this.maxWidth > 600.dp)
        println(isFullView)
        Scaffold(
            Modifier.fillMaxSize()
//                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .onKeyEvent {
                    if (it.key == Key.F5 && it.type == KeyEventType.KeyDown) {
                        refresh()
                    }
                    false
                },
            topBar = {
                LessonReportTopBar(component, isFullView) //, scrollBehavior
            },
            floatingActionButton = {

                Crossfade(nModel.state) {
                    SmallFloatingActionButton(
                        onClick = {
                            if (it != NetworkState.Loading) {
                                component.onEvent(LessonReportStore.Intent.UpdateWholeReport)
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
                                Text("Попробовать ещё раз")
                            }
                        }
                    }

//                    AnimatedContent(nModel.state) {
//                    when (it) {
//                        NetworkState.None -> {
//                            SmallFloatingActionButton(
//                                onClick = {
//                                    component.onEvent(LessonReportStore.Intent.UpdateWholeReport)
//                                }
//                            ) {
//                                Icon(
//                                    Icons.Rounded.Save,
//                                    null
//                                )
//                            }
//                        }
//
//                        NetworkState.Loading -> {
//                            SmallFloatingActionButton(
//                                onClick = {}
//                            ) {
//                                CircularProgressIndicator()
//                            }
//                        }
//
//                        NetworkState.Error -> {}
//                    }
//                }
                }
            }

        ) { padding ->
            Column(Modifier.fillMaxSize().padding(padding)) {
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
                                            text = if (model.topic.isNotEmpty()) model.topic else "Тема не задана",
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
                                        component,
                                        currentParentWidth = (this@BoxWithConstraints as BoxWithConstraintsScope).maxWidth
                                    )

                                }
                            }
                        }

                        it == NetworkState.Loading -> {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }

                        it == NetworkState.Error -> {
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
                    markStudentFIO + "\n${getColumnNamePrefix(model.selectedMarkReason)}: " + reasonColumnName
                )

                ListDialogMobileContent(
                    component = component.deleteMarkMenuComponent,
                    title =
                    markStudentFIO + "\n${getColumnNamePrefix(model.selectedMarkReason)}: " + reasonColumnName + " - " + markValue
                )
            }
            PullRefreshIndicator(
                modifier = Modifier.align(alignment = Alignment.TopCenter),
                refreshing = refreshing,
                state = refreshState,
            )
            CBottomSheetContent(
                component.setReportColumnsComponent
            ) {
                val minSize = (LocalViewManager.current.size?.maxWidth ?: 0.dp)
                val isLikeMenuOpened =
                    remember { mutableStateOf(minSize >= BottomSheetDefaults.SheetMaxWidth) }
                Box(Modifier.padding(horizontal = 5.dp)) {
                    Crossfade(
                        model.settingsTab
                    ) {
                        when (it) {
                            LessonReportStore.SettingsTab.MarksTab -> {

                                Column(Modifier) {
                                    Text(
                                        "Настройка колонок",
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center,
                                        fontSize = 25.sp,
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
                                        fontSize = 25.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(Modifier.height(15.dp))

                                    SetupTabContent(component, isLikeMenuOpened)
//                                    MarksTabContent(component)

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
                            Icon(
                                Icons.Rounded.Tune,
                                null
                            )
                        }
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
                            Icon(
                                Icons.Rounded.ViewWeek,
                                null
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
                                Icon(
                                    Icons.Rounded.ThumbsUpDown,
                                    null
                                )
                            }
                        }
                    }
                }
            }
        }


        LaunchedEffect(Unit) {
            component.setReportColumnsComponent.onEvent(CBottomSheetStore.Intent.ShowSheet)
        }


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
                            SpanStyle(fontWeight = FontWeight.Bold)
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
    val model by component.model

    val nModel by component.nInterface.networkModel.subscribeAsState()
    Column(
        Modifier.verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)
            .padding(start = 30.dp)
    ) {

        CustomTextField(
            value = model.topic,
            onValueChange = {
                component.onEvent(LessonReportStore.Intent.ChangeTopic(it))
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
                            IconButton(onClick = {
                                component.onEvent(LessonReportStore.Intent.LikeStudent(student.login))
                                println("sad")
                            }, modifier = Modifier.size(20.dp)) {
                                Icon(
                                    Icons.Rounded.ThumbDown, null,
                                    modifier = Modifier.rotate(180f)
                                )
                            }
                            Spacer(Modifier.width(2.dp))
                            IconButton(onClick = {
                                component.onEvent(
                                    LessonReportStore.Intent.DislikeStudent(
                                        student.login
                                    )
                                )
                            }, modifier = Modifier.size(20.dp)) {
                                Icon(
                                    Icons.Rounded.ThumbDown, null
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(10.dp))
        }
        CustomTextField(
            value = model.description,
            onValueChange = {
                component.onEvent(LessonReportStore.Intent.ChangeDescription(it))
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Наставник")
                Checkbox(
                    checked = model.isMentorWas,
                    onCheckedChange = {
                        component.onEvent(LessonReportStore.Intent.ChangeIsMentorWas)
                    }
                )
            }
            if (model.editTime.isNotEmpty()) {
                Box(modifier = Modifier.height(48.dp), contentAlignment = Alignment.Center) {
                    Text("Последнее изменение: ${model.editTime}")

                }
            }
            //MAKE BUTTON FOR RETRY
            AnimatedElevatedButton(
                text = "Сохранить",
                isEnabled = nModel.state == NetworkState.None,
//                modifier = Modifier.height(48.dp)
            ) {
                component.onEvent(LessonReportStore.Intent.UpdateWholeReport)
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
        MarkColumn("dzТест", "!dz1"),
        MarkColumn("dzПисьм. работа", "!dz2"),
        MarkColumn("dzРешение задач", "!dz3"),
//                                    "Другое"
        MarkColumn("dzУстный ответ", "!dz4"),
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
        MarkColumn("stТетрадь", "!st3"),
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
        Row(Modifier.padding(horizontal = 20.dp)) {
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
    val isExpanded =
        (LocalViewManager.current.size?.maxWidth ?: 0.dp) >= BottomSheetDefaults.SheetMaxWidth
    val coroutineScope = rememberCoroutineScope()
    val isOpened = remember { mutableStateOf(isExpanded) }
    val currentList = component.model.value.columnNames.filter {
        it.type.subSequence(0, 3) == currentId
    }
    val columnNames = component.model.value.columnNames.map { it.title }

    val sortedColumnList = columnsList.sortedBy {
        if (!isOpened.value) {
            it.title !in columnNames
        } else null
    }

    Column(
        Modifier.fillMaxWidth().animateContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "$title",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.bringIntoViewRequester(bringIntoViewRequester)
            )
            if (!isExpanded) {
                IconButton(
                    onClick = {
                        isOpened.value = !isOpened.value
                        bringIntoRequest()
                    },
                    modifier = Modifier.size(30.dp)
                ) {
                    AnimatedContent(
                        if (isOpened.value) Icons.Rounded.UnfoldLess else Icons.Rounded.UnfoldMore
                    ) {
                        Icon(
                            it, null
                        )
                    }
                }
            } else {
                Spacer(Modifier.height(30.dp))
            }
        }
        val inColumn = sortedColumnList.filter { isOpened.value || it.title in columnNames }
        LazyColumn(Modifier.height((inColumn.size * 30).dp + 1.dp), userScrollEnabled = false) {
            items(items = inColumn, key = { i -> i.reasonId }) { i ->
                val isInProcess = remember { mutableStateOf(false) }
                Row(
                    Modifier.animateItemPlacement().width(170.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        ("!${i.title}").removePrefix(currentId)
                    )
                    Checkbox(
                        modifier = Modifier.size(30.dp),
                        checked = (i.title in currentList.map { it.title }),
                        enabled = !isInProcess.value,
                        onCheckedChange = { isCheck ->
//                        coroutineScope.launch {
//                            delay(150)
                            if (isCheck) {
                                isInProcess.value = true
                                coroutineScope.launch {
                                    component.onEvent(
                                        LessonReportStore.Intent.CreateColumn(
                                            i.title,
                                            i.reasonId
                                        )
                                    )
                                    while (true) {
                                        if (i.title in component.model.value.columnNames.map { it.title }) {
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
                    )
                }
            }
        }
    }
}


@ExperimentalMaterial3Api
@Composable
fun LessonTable(
    component: LessonReportComponent,
    currentParentWidth: Dp
) {
    val model by component.model.subscribeAsState()

    val viewManager = LocalViewManager.current

    val density = LocalDensity.current
    val vScrollState = rememberLazyListState()
    val hScrollState = rememberScrollState()

    val allHeight = remember { mutableStateOf(0.dp) }
    val allWidth = remember { mutableStateOf(0.dp) }
    val lP = 50.dp

//    val widths = mutableStateMapOf<String, Dp>(
//        prisut/*columnNames[0]*/ to 150.dp, //
//        opozdanie/*columnNames[1]*/ to 104.dp, //
//        srBall/*columnNames[2]*/ to 50.dp //
//    )
//
//    model.columnNames.forEach {
//        if (widths[it.title] == null) {
//            if (it.type !in listOf(
//                    ColumnTypes.opozdanie,
//                    ColumnTypes.srBall,
//                    ColumnTypes.prisut
//                )
//            ) {
//                widths[it.title] = 150.dp
//            } else {
//                widths[it.title] = 50.dp
//            }
//        }
//    }

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
//                    .onGloballyPositioned { layoutCoordinates ->
//                    allHeight.value =
//                        with(density) { layoutCoordinates.size.height.toDp() }
//                }
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
                                            Icon(
                                                Icons.Rounded.Home,
                                                null,
                                                modifier = Modifier.size(14.dp)
                                                    .offset(y = (2).dp, x = -1.dp)
                                            )
                                        }

                                        "!st" -> {
                                            Icon(
                                                Icons.Rounded.Star,
                                                null,
                                                modifier = Modifier.size(14.dp)
                                                    .offset(y = (2).dp, x = -1.dp)
                                            )
                                        }

                                        "!ds" -> {
                                            Icon(
                                                Icons.Rounded.LocalPolice,
                                                null,
                                                modifier = Modifier.size(14.dp)
                                                    .offset(y = (2).dp, x = -1.dp)
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = column.title.removePrefix("dz").removePrefix("cl")
                                        .removePrefix("st").removePrefix("ds"),
                                    fontWeight = FontWeight.ExtraBold,
                                    textAlign = TextAlign.Center,
//                                    modifier = Modifier.width(when(column.type) {
//                                        ColumnTypes.prisut -> 150.dp
//                                        ColumnTypes.opozdanie -> 104.dp
//                                        ColumnTypes.srBall -> 50.dp
//                                        else -> 150.dp
//                                    })
//                                        .onGloballyPositioned {
//                                            val width = with(density) { it.size.height.toDp() }
//                                            if (width > widths[column.title]!!) widths[column.title] =
//                                                (width)
//                                            else {
//                                                isChecked.value =
//                                                    true//; println("${column.title}: ${widths[column.title]}")
//                                            }
//
//                                        },

//                                    onTextLayout = {
//                                        if (it.hasVisualOverflow) {
//                                            widths[column.title] =
//                                                widths[column.title]!! + 15.dp
//                                        }
//                                    },

                                    overflow = TextOverflow.Ellipsis,
                                    softWrap = false
                                )
//                                val count = remember { mutableStateOf(0) }
//                                if (column.type is ColumnTypes.srBall && model.isEditable) {
//                                    Box(
//                                        Modifier.offset(y = 1.dp).padding(start = 5.dp).size(13.dp)
//                                            .clip(CircleShape).background(
//                                                MaterialTheme.colorScheme.onSurface.copy(alpha = .4f)
//                                            ).clickable {
//                                                component.onEvent(
//                                                    LessonReportStore.Intent.CreateColumn(
//                                                        if (count.value == 0) "За домашнюю 18.03" else if (count.value == 1) "За домашнюю 16.03" else "За классную"
//                                                    )
//                                                )
//                                                count.value++
//                                            },
//                                        contentAlignment = Alignment.Center
//                                    ) {
//                                        Icon(
//                                            Icons.Rounded.Menu,
//                                            modifier = Modifier,
//                                            contentDescription = null,
//                                            tint = MaterialTheme.colorScheme.onSurface
//                                        )
//                                    }
//                                }
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
                        Column {
                            Text(
                                text = student.shortFio,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (student.login in model.likedList) Color.Green.copy(
                                    alpha = .3f
                                ) else if (student.login in model.dislikedList) Color.Red.copy(
                                    alpha = .3f
                                ) else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .padding(start = 10.dp)
                                    .offset(with(density) { hScrollState.value.toDp() })
                            )
                            Row(Modifier.padding(start = lP)) {

//                            Box(
//                                Modifier.width(lP),
//                                contentAlignment = Alignment.CenterEnd
//                            ) {
//                                IconButton(
//                                    onClick = { onEditClick(index) },
//                                    modifier = Modifier.padding(top = 5.dp).size(15.dp)
//                                ) {
//                                    Icon(Icons.Rounded.Edit, null)
//                                }
//                            }
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
                                                        Checkbox(
                                                            checked = student.attended.isAttended,
                                                            onCheckedChange = null,
                                                            enabled = model.isEditable
                                                        )
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
                                                                    FilledTonalButton(
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
                                                                    Box() {
                                                                        IconButton(
                                                                            modifier = Modifier.width(
                                                                                30.dp
                                                                            )
//                                                                            .onGloballyPositioned {
//                                                                            x =
//                                                                                it.positionInRoot().x - 110f - (viewManager.size!!.maxWidth - currentParentWidth).value// it.positionInParent().x
//                                                                            y =
//                                                                                it.positionInRoot().y - 50f
//
//                                                                        }
                                                                            , onClick = {
                                                                                component.onEvent(
                                                                                    LessonReportStore.Intent.OpenSetLateTimeMenu(
                                                                                        student.login,
                                                                                        x = 0f,
                                                                                        y = 0f
                                                                                    )
                                                                                )
                                                                            }) {
                                                                            Icon(
                                                                                Icons.Rounded.MoreVert,
                                                                                null
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

                                                                else -> Row(
                                                                    Modifier.fillMaxWidth(),
                                                                    verticalAlignment = Alignment.CenterVertically,
                                                                    horizontalArrangement = Arrangement.Center
                                                                ) {
                                                                    Text(
                                                                        it,
                                                                        fontWeight = FontWeight.Bold
                                                                    )
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
                                                                        Icon(
                                                                            Icons.Rounded.Close,
                                                                            null
                                                                        )
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }

                                                    ColumnTypes.srBall -> {
                                                        val marks =
                                                            student.marksOfCurrentLesson.filter { it.isGoToAvg }
                                                        val value =
                                                            (student.avgMark.previousSum + marks.sumOf { it.value }) / (student.avgMark.countOfMarks + marks.size).toFloat()

                                                        if (value.isNaN()) {
                                                            Text(
                                                                text = "NaN",
                                                                fontWeight = FontWeight.Black
                                                            )
                                                        } else {
                                                            CustomTextButton(
                                                                text = value.roundTo(2).toString(),
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
                                                                mark = mark.value.toString(),
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
//                                                                .onGloballyPositioned {
//                                                                    x =
//                                                                        it.positionInRoot().x - 110f - (viewManager.size!!.maxWidth - currentParentWidth).value// it.positionInParent().x
//                                                                    y =
//                                                                        it.positionInRoot().y - 50f
//
//                                                                }
                                                                    .clickable {
                                                                        component.onEvent(
                                                                            LessonReportStore.Intent.OpenDeleteMarkMenu(
                                                                                reasonId = column.type,
                                                                                studentLogin = student.login,
                                                                                markValue = index,
                                                                                x = 0f,
                                                                                y = 0f
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
                                                                    isFullHeight = true
                                                                )
                                                            }
                                                        }
                                                    }

                                                    if (marks.size != 4) {
//                                                        var x by remember { mutableStateOf(0.0f) }
//                                                        var y by remember { mutableStateOf(0.0f) }
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
//                                                                .onGloballyPositioned {
//                                                                    x =
//                                                                        it.positionInRoot().x - 110f - (viewManager.size!!.maxWidth - currentParentWidth).value// it.positionInParent().x
//                                                                    y =
//                                                                        it.positionInRoot().y - 50f
//
//                                                                }
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
                                                                Icon(
                                                                    Icons.Rounded.Add,
                                                                    modifier = Modifier,
                                                                    contentDescription = null,
                                                                    tint = MaterialTheme.colorScheme.onSurface
                                                                )
                                                            }
                                                            if (model.selectedMarkReason == column.type && model.selectedLogin == student.login) {
                                                                ListDialogDesktopContent(
                                                                    component.setMarkMenuComponent,
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
                                            } else {
                                                val reason =
                                                    column.type
                                                Stepper(
                                                    isEditable = model.isEditable,
                                                    count = (student.stupsOfCurrentLesson.firstOrNull { it.reason == column.type }
                                                        ?: Stup(0, "", id = model.ids)).value,
                                                    maxCount =
                                                    when (reason) {
                                                        "!ds1" -> 1
                                                        "!ds2" -> 1
                                                        "!ds3" -> 0
                                                        "!st5" -> 1
                                                        else -> 3
                                                    },
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
                                Divider(
                                    Modifier.padding(start = 1.dp)
                                        .width(allWidth.value - 1.dp)
                                        .height(1.dp),
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = .4f)
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

    CAlertDialogContent(
        component = component.marksDialogComponent,
        title = "Оценки: $detailedMarksName",
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
private fun Stepper(
    count: Int,
    isEditable: Boolean,
    maxCount: Int,
    minCount: Int,
    onChangeCount: (Int) -> Unit
) {
    val animatedAllAlpha by animateFloatAsState(if (count != 0) 1f else .2f)
    Row(
        modifier = Modifier.border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(if (count != 0) 1f else .2f),
            shape = RoundedCornerShape(30)
        ).clip(RoundedCornerShape(30)).alpha(animatedAllAlpha)
    ) {
        if (isEditable) {
//            val animatedAlpha by animateFloatAsState(if (count != minCount) 1f else .2f)
            IconButton(
                onClick = {
                    onChangeCount(count - 1)
                },
                enabled = count != minCount
            ) {
                Icon(Icons.Rounded.Remove, null)
            }
        }
        AnimatedContent(
            count
        ) {
            Text(it.toString())
        }
        if (isEditable) {
            IconButton(
                onClick = { onChangeCount(count + 1) },
                enabled = count != maxCount
            ) {
                Icon(Icons.Rounded.Add, null)
            }
        }
    }
}

//val isCollapsed = remember { derivedStateOf { scrollBehavior.state.collapsedFraction > 0.5 } }
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonReportTopBar(
    component: LessonReportComponent,
    isFullView: Boolean
) {
    val model = component.model.value
    AppBar(
        navigationRow = {
            backAB(component)
        },
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
                    isStartPadding = true
                ) {
                    component.onEvent(LessonReportStore.Intent.ChangeInfoShowing)
                }
                if (isFullView) {
                    Text(
                        text = if (model.topic.isNotEmpty()) model.topic else "Тема не задана",
                        modifier = Modifier.fillMaxWidth().padding(start = 3.dp),
                        textAlign = TextAlign.Center,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2
                    )
                }
            }
        },
        actionRow = {
            settingsAB(component)
            refreshAB(component)

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
        Icon(
            Icons.Rounded.ViewWeek,
            null
        )
    }
}

@Composable
private fun backAB(
    component: LessonReportComponent
) {
    IconButton(
        onClick = { component.onOutput(LessonReportComponent.Output.BackToJournal) }
    ) {
        Icon(
            Icons.Rounded.ArrowBackIosNew, null
        )
    }
}

@Composable
private fun refreshAB(
    component: LessonReportComponent
) {
    IconButton(
        onClick = { }//*refresh()*// }
    ) {
        Icon(
            Icons.Filled.Refresh, null
        )
    }
}
