import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.*
import components.listDialog.ListDialogStore
import components.networkInterface.NetworkState
import decomposeComponents.CAlertDialogContent
import decomposeComponents.listDialogComponent.ListDialogDesktopContent
import decomposeComponents.listDialogComponent.ListDialogMobileContent
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.LocalHazeStyle
import dev.chrisbanes.haze.hazeChild
import main.school.MinistryKid
import ministry.MinistryComponent
import ministry.MinistryStore
import server.Ministries
import view.LocalViewManager


private val headerTitles = mapOf(
    "0" to "...",
    Ministries.MVD to "МВД",
    Ministries.Culture to "Культура",
    Ministries.DressCode to "Здравоохранение",
    Ministries.Education to "Образование",
    Ministries.Print to "Печать",
    Ministries.Social to "Соц опрос",
    Ministries.Sport to "Спорт",
)

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SharedTransitionScope.MinistryContent(
    component: MinistryComponent,
    isVisible: Boolean
) {
    val model by component.model.subscribeAsState()
    val nModel by component.nInterface.networkModel.subscribeAsState()
    val nUploadModel by component.nUploadInterface.networkModel.subscribeAsState()
//    val coroutineScope = rememberCoroutineScope()
//    val focusManager = LocalFocusManager.current
    val viewManager = LocalViewManager.current
//    val scrollState = rememberScrollState()
//    val imeState = rememberImeState()
    val lazyListState = rememberLazyListState()
    val hazeState = remember { HazeState() }

    //PullToRefresh
//    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    Scaffold(
        Modifier.fillMaxSize(),
//                .nestedScroll(scrollBehavior.nestedScrollConnection)
        topBar = {
            val isHaze = viewManager.hazeHardware.value
            Column(
                Modifier.then(
                    if (isHaze) Modifier.hazeChild(
                        state = hazeState,
                        style = LocalHazeStyle.current
                    ) {
                        progressive = view.hazeProgressive
                    }
                    else Modifier
                )
            ) {
                AppBar(
                    containerColor = if (isHaze) Color.Transparent else MaterialTheme.colorScheme.surface,
                    navigationRow = {
                        IconButton(
                            onClick = { component.onOutput(MinistryComponent.Output.Back) }
                        ) {
                            Icon(
                                Icons.Rounded.ArrowBackIosNew, null
                            )
                        }
                    },
                    title = {
                        AnimatedContent(
                            if (model.isMultiMinistry == true && model.pickedMinistry == "0") "Выберите"
                            else headerTitles[model.pickedMinistry].toString(),
                            modifier = Modifier.sharedElementWithCallerManagedVisibility(
                                sharedContentState = rememberSharedContentState(key = "Ministry"),
                                visible = isVisible
                            )
                        ) { text ->
                            Box(contentAlignment = Alignment.BottomEnd) {
                                Text(
                                    text,
                                    fontSize = 25.sp,
                                    fontWeight = FontWeight.Black,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.then(
                                        if (model.isMultiMinistry == true) {
                                            Modifier.cClickable {
                                                component.ministriesListComponent.onEvent(ListDialogStore.Intent.ShowDialog)
                                            }
                                        } else {
                                            Modifier
                                        }
                                    )
                                )
                                ListDialogDesktopContent(
                                    component = component.ministriesListComponent,
                                    isFullHeight = true
                                )
                            }
                        }

                    },
                    actionRow = {
                        AnimatedVisibility(
                            nUploadModel.state != NetworkState.None,
                            enter = fadeIn(animationSpec = tween(300)) +
                                    slideInHorizontally { it },
                            exit = fadeOut(animationSpec = tween(300)) + slideOutHorizontally { it }
                        ) {
                            Row {
                                Crossfade(
                                    nUploadModel.state,
                                    modifier = Modifier.animateContentSize()
                                ) {
                                    when (it) {
                                        NetworkState.None -> {}
                                        NetworkState.Loading ->
                                            CircularProgressIndicator(modifier = Modifier.size(20.dp))

                                        NetworkState.Error ->
                                            CustomTextButton(
                                                "Ошибка"
                                            ) {
                                                nUploadModel.onFixErrorClick()
                                            }
                                    }
                                }
                                Spacer(Modifier.width(10.dp))
                            }
                        }


                    },
                    isTransparentHaze = isHaze,
                    isHazeActivated = true,
                    hazeState = null
                )
                DatesLine(
                    dates = model.dates.reversed(),
                    currentDate = model.currentDate,
                    firstItemWidth = 30.dp
                ) {
                    component.onEvent(MinistryStore.Intent.ChangeDate(it))
                }
                Text(
                    text = "Выставлять ступени в конце дня!",
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

            }

            //LessonReportTopBar(component, isFullView) //, scrollBehavior
        }
    ) { padding ->
        Column(Modifier.fillMaxSize()) {
            Crossfade(nModel.state) { state ->
                when (state) {
                    NetworkState.None -> {
                        val ministryList =
                            model.ministryList.firstOrNull { it.ministryId == model.pickedMinistry && it.date == model.currentDate.second }
                        CLazyColumn(padding = padding, state = lazyListState, hazeState = hazeState) {
                            if (ministryList == null) {
                                item {
                                    Text("meow")
                                }
                            } else {
                                val kidList = ministryList.kids.sortedWith(
                                    compareBy(
                                        { it.formId },
                                        { it.fio.surname })
                                )
                                itemsIndexed(items = kidList, key = { i, item -> item.login }) { i, item ->
                                    if (i == kidList.indexOfFirst { it.formId == item.formId }) {
                                        Text(
                                            item.formTitle,
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(start = 10.dp, bottom = 5.dp, top = 8.dp)
                                        )
                                    }
                                    MinistryKidItem(
                                        item = item,
                                        component = component,
                                        model = model
                                    )
                                    Spacer(Modifier.height(10.dp))
                                }
                            }
                            //                        items(items = model.dates) { i, date ->
                            //                            DateTasksItem(
                            //                                date = date,
                            //                                tasks = model.homeTasks.filter { it.date == date },
                            //                                groups = model.groups,
                            //                                subjects = model.subjects,
                            //                                component = component,
                            //                                model = model
                            //                            )
                            //                            if (i == model.dates.size - 1) {
                            //                                Spacer(Modifier.height(100.dp))
                            //                            }
                            //                        }
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
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
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

    ListDialogMobileContent(
        component = component.ministriesListComponent,
        title = "Министерства",
        hazeState = null
    )

    ListDialogMobileContent(
        component = component.ds1ListComponent,
        title = "Готовность",
        hazeState = null
    )
    ListDialogMobileContent(
        component = component.ds2ListComponent,
        title = "Поведение",
        hazeState = null
    )


    CAlertDialogContent(
        component = component.ds3DialogComponent,
        isCustomButtons = false,
        title = "Нарушение",
        acceptText = "Сохранить"
    ) {
        Column(Modifier.verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
            Stepper(
                isEditable = true,
                count = model.mvdStups,
                maxCount = 0,
                minCount = -10
            ) {
                component.onEvent(
                    MinistryStore.Intent.ChangeDs3Stepper(it)
                )
            }

            CustomTextField(
                value = model.mvdCustom,
                onValueChange = {
                    component.onEvent(
                        MinistryStore.Intent.ChangeDs3Custom(it)
                    )
                },
                text = "Причина",
                isEnabled = true,
                isMoveUpLocked = true,
                autoCorrect = true,
                keyboardType = KeyboardType.Text,
                modifier = Modifier.fillMaxWidth(),
                isSingleLine = false
            )
        }
    }
}


@Composable
private fun MinistryKidItem(
    item: MinistryKid,
    model: MinistryStore.State,
    component: MinistryComponent
) {

    val isScheduleShowing = remember { mutableStateOf(false) }

    Surface(
        Modifier.fillMaxWidth(),
        tonalElevation = 2.dp,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(5.dp).padding(start = 11.dp).animateContentSize()) {
            Text(
                "${item.fio.surname} ${item.fio.name} ${item.fio.praname ?: ""}",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
            Row {
                Text(
                    buildAnnotatedString {
                        val textikToday = "${getStupString(item.dayStups.sumOf { it.content.toIntOrNull() ?: 0 })} "
                        val colorToday = if (textikToday.contains("-")) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground


                        val textikWeek = "${getStupString(item.weekStupsCount)} "
                        val colorWeek = if (textikWeek.contains("-")) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground


                        val textikModule = "${getStupString(item.moduleStupsCount)} "
                        val colorModule = if (textikModule.contains("-")) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground


                        val textikYear = "${getStupString(item.yearStupsCount)} "
                        val colorYear = if (textikYear.contains("-")) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground

                        withStyle(SpanStyle(
                                color = colorToday)) {
                            withStyle(SpanStyle(
                                fontWeight = FontWeight.Bold
                            )) {
                                append("Сегодня: ")
                            }
                            append(textikToday)
                        }

                        withStyle(SpanStyle(color = colorWeek)) {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("Неделя: ")
                            }
                            append(textikWeek)
                        }


                        append("\n")

                        withStyle(SpanStyle( color = colorModule)) {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("Модуль: ")
                            }
                            append(textikModule)
                        }

                        withStyle(SpanStyle( color = colorYear)) {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("Год: ")
                            }
                            append(textikYear)
                        }
                    }
                )
                if (item.lessons.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            isScheduleShowing.value = !isScheduleShowing.value
                        }
                    ) {
                        Icon(Icons.Rounded.Schedule, null)
                    }
                }
            }

            AnimatedVisibility(isScheduleShowing.value) {
                Column {
                    item.lessons.forEachIndexed { i, l ->
                        val stups = item.dayStups.filter { it.reportId == l.reportId }
                        val isGroupView = remember { mutableStateOf(false) }
                        val customText = stups.firstOrNull { !it.custom.isNullOrBlank() }?.custom
                        Column {
                            Row {
                                Text(
                                    text = "${(i + 1)} ",
                                    modifier = Modifier.alpha(.5f)
                                )
                                AnimatedContent(
                                    if (isGroupView.value) l.groupName else l.subjectName,
                                    modifier = Modifier.cClickable {
                                        isGroupView.value = !isGroupView.value
                                    }
                                ) {
                                    Text(it, fontWeight = FontWeight.Bold)
                                }
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = l.time +
                                            (if (l.isUvNka == true) " Ув" else if (l.isUvNka == false) " Н" else "") +
                                            (if (l.isLiked == "t") " +" else if (l.isLiked == "f") " -" else "") +
                                            (if (l.lateTime.isNotEmpty() && l.lateTime != "0") " ${l.lateTime}" else "")
                                )
                            }
                            Row {
                                Text(
                                    text = "${(i + 1)} ",
                                    modifier = Modifier.alpha(.0f)
                                )
                                stups.forEach {
                                    if (it.content != "0") {
                                        val reason = when (it.reason) {
                                            "!ds1" -> "Гот"
                                            "!ds2" -> "Пов"
                                            "!ds3" -> "Нар"
                                            else -> "???"
                                        }
                                        val textik = getStupString(it.content)
                                        val color = if (textik.contains("-")) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                        Spacer(Modifier.width(5.dp))
                                        Box() {
                                            AnimatedContent(
                                                "${textik} ($reason)"
                                            ) { text ->
                                                CustomTextButton(
                                                    text,
                                                    color = color
                                                ) {
                                                    component.onEvent(
                                                        MinistryStore.Intent.OpenMVDEdit(
                                                            login = item.login,
                                                            reason = it.reason,
                                                            reportId = l.reportId,
                                                            custom = it.custom ?: "",
                                                            stups = it.content.toIntOrNull() ?: 0
                                                        )
                                                    )
                                                }
                                            }
                                            if (model.mvdLogin == item.login && model.mvdReportId == l.reportId) {
                                                if (it.reason != "!ds3") {
                                                    ListDialogDesktopContent(
                                                        when (it.reason) {
                                                            "!ds1" -> component.ds1ListComponent
                                                            else -> component.ds2ListComponent
                                                        },
                                                        isFullHeight = true
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                                if (stups.none { it.reason == "!ds1" && it.content != "0" }) {
                                    Spacer(Modifier.width(5.dp))
                                    Box() {
                                        CustomTextButton("готовность?") {
                                            component.onEvent(
                                                MinistryStore.Intent.OpenMVDEdit(
                                                    login = item.login,
                                                    reason = "!ds1",
                                                    reportId = l.reportId,
                                                    custom = "",
                                                    stups = 0
                                                )
                                            )
                                        }
                                        if (model.mvdLogin == item.login && model.mvdReportId == l.reportId) {
                                            ListDialogDesktopContent(
                                                component = component.ds1ListComponent,
                                                isFullHeight = true
                                            )
                                        }
                                    }
                                }
                                if (stups.none { it.reason == "!ds2" && it.content != "0" }) {
                                    Spacer(Modifier.width(5.dp))
                                    Box() {
                                        CustomTextButton("поведение?") {
                                            component.onEvent(
                                                MinistryStore.Intent.OpenMVDEdit(
                                                    login = item.login,
                                                    reason = "!ds2",
                                                    reportId = l.reportId,
                                                    custom = "",
                                                    stups = 0
                                                )
                                            )
                                        }
                                        if (model.mvdLogin == item.login && model.mvdReportId == l.reportId) {
                                            ListDialogDesktopContent(
                                                component = component.ds2ListComponent,
                                                isFullHeight = true
                                            )
                                        }
                                    }
                                }
                                if (stups.none { it.reason == "!ds3" && it.content != "0" }) {
                                    Spacer(Modifier.width(5.dp))
                                    CustomTextButton("нарушение?") {
                                        component.onEvent(
                                            MinistryStore.Intent.OpenMVDEdit(
                                                login = item.login,
                                                reason = "!ds3",
                                                reportId = l.reportId,
                                                custom = "",
                                                stups = 0
                                            )
                                        )
                                    }
                                }
                            }
                            AnimatedVisibility(customText != null) {
                                Text(
                                    customText.toString(),
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(start = 20.dp),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            if (model.pickedMinistry == Ministries.DressCode) {
//                val
            }
        }
    }
}