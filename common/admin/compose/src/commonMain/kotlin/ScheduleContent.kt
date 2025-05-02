import admin.schedule.ScheduleGroup
import admin.schedule.SchedulePerson
import admin.schedule.ScheduleSubject
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.desktop.ui.tooling.preview.utils.esp
import androidx.compose.desktop.ui.tooling.preview.utils.popupPositionProvider
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.rememberTooltipState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.DateButton
import components.GetAsyncIcon
import components.SaveAnimation
import components.ScrollBaredBox
import components.cAlertDialog.CAlertDialogStore
import components.foundation.AppBar
import components.foundation.CCheckbox
import components.foundation.CTextButton
import components.foundation.CTextField
import components.foundation.DefaultErrorView
import components.foundation.DefaultErrorViewPos
import components.foundation.cClickable
import components.foundation.hazeUnder
import components.listDialog.ListDialogStore
import components.mpChose.MpChoseStore
import components.networkInterface.NetworkInterface
import components.networkInterface.NetworkState
import components.networkInterface.isLoading
import decomposeComponents.CAlertDialogContent
import decomposeComponents.listDialogComponent.ListDialogDesktopContent
import decomposeComponents.listDialogComponent.ListDialogMobileContent
import decomposeComponents.mpChoseComponent.mpChoseDesktopContent
import resources.RIcons
import schedule.ScheduleComponent
import schedule.ScheduleItem
import schedule.ScheduleStore
import schedule.ScheduleStore.EditState
import schedule.ScheduleTiming
import schedule.StudentError
import schedule.fetchLoginsOfLesson
import schedule.tOverlap
import schedule.timingsPairs
import server.ScheduleIds
import server.isTimeFormat
import server.toMinutes
import server.weekPairs
import utils.LockScreenOrientation
import view.LocalViewManager
import view.blend
import view.colorScheme


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ScheduleContent(
    component: ScheduleComponent
) {
    LockScreenOrientation(-1)


    val viewManager = LocalViewManager.current


    val model by component.model.subscribeAsState()
    val mpModel by component.mpCreateItem.model.subscribeAsState()
    val mpEditModel by component.mpEditItem.model.subscribeAsState()
    val nModel by component.nInterface.networkModel.subscribeAsState()

    LaunchedEffect(Unit) {
        if (!nModel.isLoading) component.onEvent(ScheduleStore.Intent.Init)
    }

    val scrollState = rememberScrollState()
    val density = LocalDensity.current
    val lazyListState = remember { LazyListState() }
    val isStatic = remember { mutableStateOf(false) }
    val isExtraHidden = remember { mutableStateOf(false) }


    val dayStartTime = "8:30"

    val key = if (model.isDefault) model.defaultDate.toString() else model.currentDate.second

    Scaffold(
        modifier = Modifier.fillMaxSize().hazeUnder(
            viewManager
        ),
        topBar = {
            AppBar(
                title = {

                    Row(
                        Modifier.fillMaxHeight().horizontalScroll(rememberScrollState()),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        AnimatedContent(
                            targetState = if (model.isDefault) "Стандартное" else "Актуальное"
                        ) { text ->
                            CTextButton(
                                text = text,
                                fontSize = MaterialTheme.typography.headlineSmall.fontSize

                            ) {
                                component.onEvent(ScheduleStore.Intent.ChangeEditMode)
                            }
                        }
//                        Spacer(Modifier.width(7.dp))
//                        Text(
//                            "расписание",
//
//                            fontSize = 25.sp,
//                            fontWeight = FontWeight.Black,
//                            maxLines = 1,
//                            overflow = TextOverflow.Ellipsis
//                        )

                        Spacer(Modifier.width(15.dp))
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            if (model.isDefault) {
                                Row(
                                    Modifier.fillMaxHeight(),
//                                        .horizontalScroll(rememberScrollState()),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Spacer(Modifier.width(10.dp))
                                    weekPairs.forEach { item ->
                                        if (item.key !in listOf(6, 7)) {
                                            FilledTonalButton(
                                                modifier = Modifier.size(50.dp).padding(end = 5.dp),
                                                shape = RoundedCornerShape(30),
                                                colors = ButtonDefaults.filledTonalButtonColors(
                                                    containerColor = if (item.key == model.defaultDate) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceColorAtElevation(
                                                        2.dp
                                                    ),
                                                    contentColor = if (item.key == model.defaultDate) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface
                                                ),
                                                onClick = {
                                                    component.onEvent(
                                                        ScheduleStore.Intent.ChangeDefaultDate(
                                                            item.key
                                                        )
                                                    )
                                                },
                                                contentPadding = PaddingValues(0.dp)
                                            ) {
                                                Box(
                                                    Modifier.fillMaxSize(),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = item.value,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                                        modifier = Modifier.fillMaxWidth(),
                                                        textAlign = TextAlign.Center
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                Spacer(Modifier.width(10.dp))
                                Row(
                                    Modifier.fillMaxHeight(),
                                    //.horizontalScroll(rememberScrollState()),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    model.dates.toList().forEach { item ->
                                        DateButton(
                                            currentDate = model.currentDate.second,
                                            dayOfWeek = item.first,
                                            date = item.second
                                        ) {
                                            component.onEvent(
                                                ScheduleStore.Intent.ChangeCurrentDate(
                                                    item
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                },
                navigationRow = {
                    IconButton(
                        onClick = { component.onOutput(ScheduleComponent.Output.Back) }
                    ) {
                        GetAsyncIcon(
                            path = RIcons.CHEVRON_LEFT
                        )
                    }
                },
                actionRow = {
                    IconButton(
                        onClick = {
                            isExtraHidden.value = !isExtraHidden.value
                        }
                    ) {
                        GetAsyncIcon(
                            if (!isExtraHidden.value) RIcons.VISIBILITY
                            else RIcons.VISIBILITY_OFF
                        )
                    }
                    IconButton(
                        onClick = {
                            component.onEvent(ScheduleStore.Intent.ChangeIsTeacherView)
                        }
                    ) {
                        GetAsyncIcon(
                            path = if (model.isTeachersView) RIcons.USER else RIcons.GROUP
                        )
                    }

                    IconToggleButton(
                        checked = isStatic.value,
                        onCheckedChange = {
                            isStatic.value = it
                        }
                    ) {
                        GetAsyncIcon(
                            RIcons.PUZZLE
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if (component.isCanBeEdited) {
                Crossfade(nModel.state) {
                    SmallFloatingActionButton(
                        onClick = {
                            when (it) {
                                NetworkState.Error -> {
                                    nModel.onFixErrorClick()
                                }

                                NetworkState.None -> {
                                    component.onEvent(ScheduleStore.Intent.SaveSchedule)
                                }

                                NetworkState.Loading -> {}
                            }
                        },
                        modifier = Modifier.animateContentSize()
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
                                Text(nModel.error)
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        Crossfade(nModel.state) { nState ->
            when (nState) {
                NetworkState.None -> BoxWithConstraints(modifier = Modifier.padding(padding)) {
                    val headerP = 55.dp //55
                    val maxHeight = this.maxHeight - headerP
                    val minuteHeight =
                        if (isStatic.value) maxHeight / ("20:00".toMinutes() - dayStartTime.toMinutes()) else (1.7f).dp

                    val timings = listOf(
                        "08:45",
                        "09:00",
                        "09:40",
                        "10:25",
                        "11:15",
                        "12:00",
                        "12:20",
                        "13:00",
                        "13:45",
                        "14:30",
                        "15:15",
                        "16:00",
                        "16:20",
                        "17:00",
                        "17:50",
                        "18:35",
                        "19:25"
                    )

                    //                    remember {
                    //                        isStatic.value = minuteHeight >= 0.8f.dp
                    //                    }
                    val widthCount =
                        if (model.isTeachersView && model.groups.isNotEmpty() && model.students.isNotEmpty() && model.subjects.isNotEmpty() && model.teachers.isNotEmpty()) {
                            model.activeTeachers[key]?.size ?: 0
                        } else {
                            model.forms.toList().size
                        }
                    ScrollBaredBox(
                        vState = scrollState,
                        hState = lazyListState,
                        height = mutableStateOf(minuteHeight * ("20:00".toMinutes() - "08:45".toMinutes())),
                        width = mutableStateOf(
                            12.dp +
                                    widthCount * 200.dp
                        )
                    ) {

                        Box(
                            modifier = Modifier.fillMaxSize().verticalScroll(scrollState)
                                .padding(start = 8.dp)
                        ) {
                            //TIMINGS
                            Box(Modifier.padding(top = headerP)) {
                                timings.forEach {
                                    Row(Modifier.padding(top = minuteHeight * (it.toMinutes() - dayStartTime.toMinutes()))) {
                                        Text(
                                            text = it,
                                            fontSize = 14.esp,
                                            lineHeight = 14.esp,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = .0f),
                                            modifier = Modifier.offset(y = (-10).dp),
                                            textAlign = TextAlign.Center
                                        )
                                        HorizontalDivider(
                                            color = MaterialTheme.colorScheme.outline.copy(
                                                alpha = .4f
                                            )
                                        )
                                    }
                                }
                            }
                            //ADD TEACHER
                            if (model.isTeachersView && component.isCanBeEdited) {
                                Box() {
                                    IconButton(
                                        onClick = {
                                            component.listCreateTeacher.onEvent(ListDialogStore.Intent.ShowDialog)
                                        }
                                    ) {
                                        GetAsyncIcon(
                                            RIcons.PERSON_ADD
                                        )
                                    }
                                    ListDialogDesktopContent(
                                        component = component.listCreateTeacher,
                                        offset = DpOffset(x = 40.dp, y = -25.dp)
                                    )

                                }
                            }
                            //LINES FOR TIMINGS
                            Box(Modifier.zIndex(3f).padding(top = headerP)) {
                                timings.forEach {
                                    Row(Modifier.padding(top = minuteHeight * (it.toMinutes() - dayStartTime.toMinutes()))) {
                                        Text(
                                            text = it,
                                            fontSize = 14.esp,
                                            lineHeight = 14.esp,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = .5f),
                                            modifier = Modifier.offset(y = (-10).dp),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }

                            if (model.isTeachersView && model.groups.isNotEmpty() && model.students.isNotEmpty() && model.subjects.isNotEmpty() && model.teachers.isNotEmpty()) {
                                val trueTeachers =
                                    model.activeTeachers[key]
                                LazyRow(
                                    Modifier.padding(start = 38.dp).fillMaxSize(),
                                    state = lazyListState
                                ) {
                                    item { Spacer(Modifier.width(12.dp)) }
                                    items(
                                        trueTeachers?.reversed() ?: emptyList(),
                                        key = { it }) { login ->
                                        Box(Modifier.height(minuteHeight * ("20:00".toMinutes() - "08:45".toMinutes()) + headerP)) {
                                            ScheduleColumn(
                                                component = component,
//                                                model = model,
                                                nModel,
                                                mpModel,
                                                mpEditModel = mpEditModel,
                                                scrollState,
                                                minuteHeight,
                                                dayStartTime,
                                                login,
                                                key,
                                                headerP,
                                                density,
                                                isExtraHidden = isExtraHidden.value
                                            )
                                        }
                                    }
                                    item { Spacer(Modifier.width(200.dp)) }
                                }
                            } else if (!model.isTeachersView && model.groups.isNotEmpty() && model.students.isNotEmpty() && model.subjects.isNotEmpty() && model.teachers.isNotEmpty() && model.forms.isNotEmpty()) {
                                LazyRow(
                                    Modifier.padding(start = 38.dp).fillMaxSize(),
                                    state = lazyListState
                                ) {
                                    item { Spacer(Modifier.width(12.dp)) }
                                    items(
                                        items = model.forms.toList().sortedWith(
                                            compareBy({ it.second.num }, { it.second.shortTitle })
                                        ).reversed(),
                                        key = { it.first }) { form ->
                                        Box(Modifier.height(minuteHeight * ("20:00".toMinutes() - "08:45".toMinutes()) + headerP)) {
                                            ScheduleColumnForForms(
                                                component = component,
//                                                model = model,
                                                nModel,
                                                mpModel,
                                                mpEditModel,
                                                scrollState,
                                                minuteHeight,
                                                dayStartTime,
                                                form = form.second,
                                                formId = form.first,
                                                key = key,
                                                headerP,
                                                density,
                                                isExtraHidden = isExtraHidden.value
                                            )
                                        }
                                    }
                                    item { Spacer(Modifier.width(200.dp)) }
                                }
                            }


                        }
                        AnimatedVisibility(
                            visible = model.items[key].isNullOrEmpty(),
                            modifier = Modifier.align(
                                Alignment.Center
                            ),
                            enter = fadeIn() + scaleIn(),
                            exit = fadeOut() + scaleOut()
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Здесь пока нет предметов")
                                Spacer(Modifier.height(7.dp))
                                AnimatedVisibility(!model.isDefault && model.items[model.currentDate.first.toString()]?.isNotEmpty() == true && component.isCanBeEdited) {
                                    CTextButton("Копировать из\nстандартного расписания") {
                                        component.onEvent(ScheduleStore.Intent.CopyFromStandart)
                                    }
                                }
                            }
                        }
                    }
                    SaveAnimation(model.isSavedAnimation) {
                        component.onEvent(ScheduleStore.Intent.IsSavedAnimation(false))
                    }
                }

                NetworkState.Loading -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }

                NetworkState.Error -> DefaultErrorView(
                    nModel,
                    pos = DefaultErrorViewPos.CenteredFull
                )
            }
        }

    }
    ListDialogMobileContent(
        component = component.listCreateTeacher,
        title = "Добавить учителя"
    )

    if (model.niErrors.isNotEmpty()) {

        CAlertDialogContent(
            component = component.chooseConflictDialog,
            dialogProperties = DialogProperties(false, false)
        ) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                model.niErrors.forEach { e ->
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(Modifier.height(5.dp))
                        Text(
                            "Куда пойдут ${
                                model.students.filter { it.login in e.logins }
                                    .map { it.fio.surname }
                            }?",
                            textAlign = TextAlign.Center
                        )
                        val prevText = getNameOfConflictLesson(
                            groupId = e.groupId,
                            teacherLogin = e.teacherLogin,
                            model = model,
                            id = e.id
                        )
                        val nextText = getNameOfConflictLesson(
                            groupId = model.niGroupId!!,
                            teacherLogin = model.niTeacherLogin!!,
                            model = model,
                            id = model.niId!!
                        )
                        CTextButton(
                            text = prevText
                        ) {
                            component.onEvent(
                                ScheduleStore.Intent.SolveConflict(
                                    fromLessonId = model.niId!!,
                                    toLessonId = e.id,
                                    studentLogins = e.logins
                                )
                            )
                        }
                        Text("ИЛИ")
                        CTextButton(
                            text = nextText
                        ) {
                            component.onEvent(
                                ScheduleStore.Intent.SolveConflict(
                                    fromLessonId = e.id,
                                    toLessonId = model.niId!!,
                                    studentLogins = e.logins
                                )
                            )

                        }
                        Spacer(Modifier.height(5.dp))
                    }
                }
            }
        }
    }
}

private fun getNameOfConflictLesson(
    groupId: Int,
    teacherLogin: String,
    id: Int,
    model: ScheduleStore.State
): String {

    return if (groupId == -6) {
        "Доп с ${model.teachers.firstOrNull { it.login == teacherLogin }?.fio?.surname}"
    } else if (groupId == -11) {
        "Приём пищи"
    } else if (groupId == 0) {
        if ((model.items.flatMap { it.value.map { it.index } }.maxByOrNull { it } ?: 1) + 1 != id) {
            val key =
                if (model.isDefault) model.defaultDate.toString() else model.currentDate.second
            val trueItems = model.items[key]?.firstOrNull { it.index == id }
            "${trueItems?.custom?.firstOrNull()}"
        } else {
            model.ciCustom.firstOrNull().toString()
        }
    } else {
        val groupx =
            model.groups.firstOrNull { it.id == groupId }
        val subjectx =
            if (groupx != null) model.subjects.firstOrNull { it.id == groupx.subjectId } else null
        "${subjectx?.name} ${groupx?.name}"
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun LazyItemScope.ScheduleColumn(
    component: ScheduleComponent,
    nModel: NetworkInterface.NetworkModel,
    mpModel: MpChoseStore.State,
    mpEditModel: MpChoseStore.State,
    scrollState: ScrollState,
    minuteHeight: Dp,
    dayStartTime: String,
    login: String,
    key: String,
    headerP: Dp,
    density: Density,
    isExtraHidden: Boolean
) {
    val model by component.model.subscribeAsState()
    val c = model.teachers.first { it.login == login }
    val cabinet = model.cabinets.firstOrNull { it.login == login }


    Box(
        Modifier.width(200.dp).fillMaxHeight().padding(end = 5.dp)
            .animateItem(fadeInSpec = null, fadeOutSpec = null)
    ) {
        val headerState = remember {
            MutableTransitionState(false).apply {
                // Start the animation immediately.
                targetState = true
            }
        }
        AnimatedVisibility(
            visibleState = headerState,
            enter = fadeIn() + scaleIn(),
            modifier = Modifier.zIndex(1f)
                .offset(y = with(density) { scrollState.value.toDp() })
        ) {
            Box(
                Modifier.height(headerP)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                        .align(Alignment.Center),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "${c.fio.surname} ${c.fio.name} ${c.fio.praname}",
                        modifier = Modifier,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = MaterialTheme.typography.titleSmall.fontSize,
                        lineHeight = MaterialTheme.typography.titleSmall.fontSize
                    )
                    if (cabinet != null) {
                        Text(
                            cabinet.cabinet.toString(),
                            fontSize = 11.esp,
                            modifier = Modifier.offset(y = (-7).dp)
                        )
                    }
                }
                if (component.isCanBeEdited) {
                    Box(modifier = Modifier.align(Alignment.CenterEnd)) {
                        IconButton(
                            onClick = {
                                with(component) {
                                    mpCreateItem.onEvent(MpChoseStore.Intent.ShowDialog)
                                    onEvent(
                                        ScheduleStore.Intent.ciStart(
                                            c.login
                                        )
                                    )
                                }
                            }
                        ) {
                            GetAsyncIcon(
                                RIcons.ADD
                            )
                        }
                        if (model.ciLogin == c.login) {

                            mpChoseDesktopContent(
                                component = component.mpCreateItem,
                                backButton = if (model.ciPreview || model.ciId == null) {
                                    null
                                } else {
                                    {
                                        component.onEvent(
                                            ScheduleStore.Intent.ciChangeCustom(
                                                listOf(
                                                    ""
                                                )
                                            )
                                        )
                                        component.onEvent(ScheduleStore.Intent.ciNullGroupId)
                                    }
                                }
                            ) {
                                when {
                                    model.ciId == null -> {
                                        DropdownMenuItem(
                                            text = { Text("Доп занятие") },
                                            onClick = {
                                                component.onEvent(
                                                    ScheduleStore.Intent.ciChooseGroup(
                                                        -6
                                                    )
                                                )
                                            },
                                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                        )
                                        (model.teachers.first { it.login == c.login }.groups.filter { it.second }
                                            .sortedBy { x -> model.groups.first { it.id == x.first }.subjectId }).forEach { s ->
                                                val group =
                                                    model.groups.firstOrNull { it.id == s.first }
                                                if (group != null) {
                                                    val subject =
                                                        model.subjects.firstOrNull { it.id == group.subjectId }
                                                    if (subject != null) {
                                                        DropdownMenuItem(
                                                            text = { Text("${subject.name}  ${group.name}") },
                                                            onClick = {
                                                                component.onEvent(
                                                                    ScheduleStore.Intent.ciChooseGroup(
                                                                        s.first
                                                                    )
                                                                )
                                                            },
                                                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                                        )
                                                    }
                                                }
                                            }
                                    }

                                    !model.ciPreview -> {

                                        //Len'
                                        var isCustomTime by remember {
                                            mutableStateOf(
                                                false
                                            )
                                        }
                                        var customTime by remember {
                                            mutableStateOf(
                                                ""
                                            )
                                        }

                                        if (model.ciTimings == null) {
                                            Text("Загрузка..")
                                        } else {
                                            CTextField(
                                                value = model.ciCabinet.toString(),
                                                onValueChange = {
                                                    if (it == "") {
                                                        component.onEvent(
                                                            ScheduleStore.Intent.ciChangeCabinet(
                                                                0
                                                            )
                                                        )
                                                    } else if (it.matches(
                                                            Regex(
                                                                "^[1-3]?[0-1]?[0-9]?$"
                                                            )
                                                        )
                                                    ) {
                                                        component.onEvent(
                                                            ScheduleStore.Intent.ciChangeCabinet(
                                                                it.toInt()
                                                            )
                                                        )
                                                    }
                                                },
                                                text = "Кабинет",
                                                isEnabled = nModel.state == NetworkState.None,
                                                isMoveUpLocked = true,
                                                autoCorrect = false,
                                                keyboardType = KeyboardType.Number,
                                                modifier = Modifier.width(
                                                    130.dp
                                                ).height(60.dp)
                                            )
                                            if (!isCustomTime) {
                                                DropdownMenuItem(
                                                    text = { Text("Своё значение") },
                                                    onClick = {
                                                        isCustomTime =
                                                            true
                                                    },
                                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                                )
                                            } else {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    CTextField(
                                                        value = customTime,
                                                        onValueChange = {
                                                            if (!it.contains(
                                                                    " "
                                                                )
                                                            ) {
                                                                if (it.length <= 11) {
                                                                    customTime =
                                                                        it
                                                                }
                                                                if (it.length == 11 && isTimeFormat(
                                                                        it
                                                                    )
                                                                ) {
                                                                    val parts =
                                                                        it.split(
                                                                            "-"
                                                                        )
                                                                    component.onEvent(
                                                                        ScheduleStore.Intent.ciChooseTime(
                                                                            ScheduleTiming(
                                                                                start = parts[0],
                                                                                end = parts[1]
                                                                            )
                                                                        )
                                                                    )

                                                                }
                                                            }
                                                        },
                                                        text = "Время",
                                                        isEnabled = nModel.state == NetworkState.None,
                                                        isMoveUpLocked = true,
                                                        autoCorrect = false,
                                                        keyboardType = KeyboardType.Number,
                                                        modifier = Modifier.width(
                                                            130.dp
                                                        ).height(60.dp)
                                                    )

                                                    Spacer(
                                                        Modifier.width(
                                                            5.dp
                                                        )
                                                    )
                                                    if (customTime.length == 11 && isTimeFormat(
                                                            customTime
                                                        )
                                                    ) {
                                                        val parts =
                                                            customTime.split(
                                                                "-"
                                                            )
                                                        if (model.ciTiming == null) {
                                                            CircularProgressIndicator(
                                                                modifier = Modifier.size(
                                                                    20.dp
                                                                )
                                                            )
                                                        } else {
                                                            if (model.ciTiming!!.start == parts[0] && model.ciTiming!!.end == parts[1]) {
                                                                if (model.ciTiming!!.studentErrors.isEmpty() && model.ciTiming!!.cabinetErrorGroupId == 0) {
                                                                    IconButton(
                                                                        onClick = {
                                                                            val partsOnClick =
                                                                                customTime.split(
                                                                                    "-"
                                                                                )
                                                                            with(
                                                                                component
                                                                            ) {
                                                                                onEvent(
                                                                                    ScheduleStore.Intent.ciChooseTime(
                                                                                        ScheduleTiming(
                                                                                            start = partsOnClick[0],
                                                                                            end = partsOnClick[1]
                                                                                        )
                                                                                    )
                                                                                )
                                                                                onEvent(
                                                                                    ScheduleStore.Intent.ciPreview
                                                                                )
                                                                            }
                                                                        }
                                                                    ) {
                                                                        GetAsyncIcon(
                                                                            RIcons.CHECK
                                                                        )
                                                                    }
                                                                } else {
                                                                    val cabinetErrorGroup =
                                                                        model.groups.firstOrNull { it.id == model.ciTiming!!.cabinetErrorGroupId }
                                                                    val cabinetErrorSubject =
                                                                        if (cabinetErrorGroup != null) model.subjects.firstOrNull { it.id == cabinetErrorGroup.subjectId } else null

                                                                    val studentErrors =
                                                                        getStudentErrors(
                                                                            model.ciTiming!!.studentErrors,
                                                                            model
                                                                        )

                                                                    ErrorsTooltip(
                                                                        cabinetErrorSubject = cabinetErrorSubject,
                                                                        cabinetErrorGroup = cabinetErrorGroup,
                                                                        studentErrors = studentErrors,
                                                                        cabinetErrorGroupId = model.ciTiming!!.cabinetErrorGroupId,
                                                                        component = component,
                                                                        niCustom = model.ciCustom,
                                                                        niFormId = model.ciFormId
                                                                            ?: 0,
                                                                        niGroupId = model.ciId ?: 0,
                                                                        niTeacherLogin = model.ciLogin
                                                                            ?: model.login,
                                                                        classicStudentErrors = model.ciTiming!!.studentErrors,
                                                                        niOnClick = {
                                                                            with(component) {
                                                                                onEvent(
                                                                                    ScheduleStore.Intent.ciCreate(
                                                                                        ScheduleTiming(
                                                                                            start = parts[0],
                                                                                            end = parts[1]
                                                                                        )
                                                                                    )
                                                                                )
                                                                            }
                                                                        },
                                                                        niIndex = (model.items.flatMap { it.value.map { it.index } }
                                                                            .maxByOrNull { it }
                                                                            ?: 1) + 1
                                                                    )
                                                                }
                                                            } else {
                                                                IconButton(
                                                                    onClick = {
                                                                        component.onEvent(
                                                                            ScheduleStore.Intent.ciChooseTime(
                                                                                ScheduleTiming(
                                                                                    start = parts[0],
                                                                                    end = parts[1]
                                                                                )
                                                                            )
                                                                        )
                                                                    }
                                                                ) {
                                                                    GetAsyncIcon(
                                                                        path = RIcons.REPEAT
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            model.ciTimings!!.sortedBy { it.start }
                                                .forEach { t ->
                                                    val source =
                                                        remember { MutableInteractionSource() }
                                                    val isHovered =
                                                        source.collectIsHoveredAsState().value
                                                    LaunchedEffect(
                                                        isHovered
                                                    ) {
                                                        if (isHovered) {
                                                            component.onEvent(
                                                                ScheduleStore.Intent.ciChooseTime(
                                                                    t
                                                                )
                                                            )
                                                        }
                                                    }
                                                    DropdownMenuItem(
                                                        text = {
                                                            Text(
                                                                "${t.start}-${t.end}",
                                                                modifier = Modifier.align(
                                                                    Alignment.Center
                                                                ),
                                                                color = MaterialTheme.colorScheme.onSurface.copy(
                                                                    alpha = if (t.cabinetErrorGroupId == 0 && t.studentErrors.isEmpty()) 1f else .3f
                                                                )
                                                            )
                                                        },
                                                        trailingIcon = {
                                                            if (t.cabinetErrorGroupId != 0 || t.studentErrors.isNotEmpty()) {
                                                                val cabinetErrorGroup =
                                                                    model.groups.firstOrNull { it.id == t.cabinetErrorGroupId }
                                                                val cabinetErrorSubject =
                                                                    if (cabinetErrorGroup != null) model.subjects.firstOrNull { it.id == cabinetErrorGroup.subjectId } else null

                                                                val studentErrors =
                                                                    getStudentErrors(
                                                                        t.studentErrors,
                                                                        model
                                                                    )
                                                                ErrorsTooltip(
                                                                    cabinetErrorSubject = cabinetErrorSubject,
                                                                    cabinetErrorGroup = cabinetErrorGroup,
                                                                    studentErrors = studentErrors,
                                                                    cabinetErrorGroupId = t.cabinetErrorGroupId,
                                                                    component = component,
                                                                    niCustom = model.ciCustom,
                                                                    niFormId = model.ciFormId ?: 0,
                                                                    niGroupId = model.ciId ?: 0,
                                                                    niTeacherLogin = model.ciLogin
                                                                        ?: model.login,
                                                                    classicStudentErrors = t.studentErrors,
                                                                    niOnClick = {
                                                                        with(
                                                                            component
                                                                        ) {

                                                                            onEvent(
                                                                                ScheduleStore.Intent.ciCreate(
                                                                                    t
                                                                                )
                                                                            )
                                                                        }
                                                                    },
                                                                    niIndex = (model.items.flatMap { it.value.map { it.index } }
                                                                        .maxByOrNull { it }
                                                                        ?: 1) + 1
                                                                )
                                                            }
                                                        },
                                                        onClick = {
                                                            if (t.cabinetErrorGroupId == 0 && t.studentErrors.isEmpty()) {
                                                                with(
                                                                    component
                                                                ) {
                                                                    onEvent(
                                                                        ScheduleStore.Intent.ciChooseTime(
                                                                            t
                                                                        )
                                                                    )
                                                                    onEvent(
                                                                        ScheduleStore.Intent.ciPreview
                                                                    )
                                                                }
                                                            }
                                                        },
                                                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                                        interactionSource = source
                                                    )
                                                }
                                        }
                                    }

                                    model.ciPreview -> {
                                        if ((model.ciId ?: 0) > 0) {
                                            if (model.ciLogin != null && model.ciTiming != null && model.ciId != null) {
                                                val group =
                                                    model.groups.first { it.id == model.ciId }
                                                val subject =
                                                    model.subjects.first { it.id == group.subjectId }
                                                Column(
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    modifier = Modifier.padding(
                                                        horizontal = 10.dp
                                                    )
                                                        .widthIn(min = 120.dp)
                                                        .wrapContentSize()
                                                ) {
                                                    Text(
                                                        "Создать урок?",
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 18.esp
                                                    )
                                                    Text(
                                                        subject.name,
                                                        fontWeight = FontWeight.SemiBold
                                                    )
                                                    Text(group.name)
                                                    Text("${model.ciTiming!!.start}-${model.ciTiming!!.end}")
                                                    Text(model.ciCabinet.toString())
                                                    Row(
                                                        Modifier.height(40.dp)
                                                            .cClickable(
                                                                //                                                        interactionSource = remember { MutableInteractionSource() },
                                                                //                                                        null
                                                            ) {
                                                                component.onEvent(
                                                                    ScheduleStore.Intent.ciChangeIsPair
                                                                )
                                                            },
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        CCheckbox(
                                                            checked = model.ciIsPair
                                                        )
                                                        Text("Ещё урок")
                                                    }
                                                    Row(
                                                        Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceEvenly
                                                    ) {
                                                        IconButton(
                                                            onClick = {
                                                                component.onEvent(
                                                                    ScheduleStore.Intent.ciFalsePreview
                                                                )
                                                            }
                                                        ) {
                                                            GetAsyncIcon(
                                                                RIcons.CLOSE
                                                            )
                                                        }
                                                        IconButton(
                                                            onClick = {
                                                                with(
                                                                    component
                                                                ) {
                                                                    onEvent(
                                                                        ScheduleStore.Intent.ciCreate(
                                                                            null
                                                                        )
                                                                    )
                                                                }
                                                            }
                                                        ) {
                                                            GetAsyncIcon(
                                                                RIcons.CHECK
                                                            )
                                                        }
                                                    }
                                                }
                                            } else {
                                                Text("Error")
                                            }
                                        } else if (model.ciId == -6) {
                                            val trueItems =
                                                model.items[key]?.filter { it.groupId in model.groups.map { it.id } + (-11) + (0) + (-6) }
                                                    ?: emptyList()
                                            //                                                    ?.filter { (it.formId == null ) && (it.groupId != -6 || it.custom in form.logins) }
                                            val coItems =
                                                (trueItems).filter {
                                                    // ! (закончилось раньше чем началось наше) или (началось позже чем началось наше)
                                                    ((!((it.t.end.toMinutes() < model.ciTiming!!.start.toMinutes() ||
                                                            it.t.start.toMinutes() > model.ciTiming!!.end.toMinutes())) && it.groupId != -11) ||
                                                            (!((it.t.end.toMinutes() <= model.ciTiming!!.start.toMinutes() ||
                                                                    it.t.start.toMinutes() >= model.ciTiming!!.end.toMinutes())) && it.groupId == -11))
                                                }

                                            val studentErrors = schedule.getStudentErrors(
                                                coItems = coItems,
                                                students = model.students,//logins?.okLogins?.mapNotNull { l ->
                                                //                                                    model.students.firstOrNull { it.login == l }
                                                //                                                                                                } ?: listOf(),
                                                state = model
                                            )

                                            val filterStudents = remember { mutableStateOf("") }
                                            Row {
                                                IconButton(
                                                    onClick = {
                                                        component.onEvent(
                                                            ScheduleStore.Intent.ciFalsePreview
                                                        )
                                                    }
                                                ) {
                                                    GetAsyncIcon(
                                                        RIcons.CHEVRON_LEFT
                                                    )
                                                }
                                                Column {
                                                    var expandedGSubjects by remember {
                                                        mutableStateOf(
                                                            false
                                                        )
                                                    }
                                                    val subjectsMap =
                                                        model.subjects.filter { it.isActive }
                                                            .sortedBy { it.id != c.subjectId }
                                                            .associate { it.id to it.name }

                                                    ExposedDropdownMenuBox(
                                                        expanded = expandedGSubjects,
                                                        onExpandedChange = {
                                                            expandedGSubjects =
                                                                !expandedGSubjects
                                                        }
                                                    ) {
                                                        // textfield
                                                        val gSubject =
                                                            model.subjects.find { it.id == model.ciSubjectId }

                                                        OutlinedTextField(
                                                            modifier = Modifier
                                                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                                                .defaultMinSize(
                                                                    minWidth = 5.dp
                                                                ), // menuAnchor modifier must be passed to the text field for correctness.
                                                            readOnly = true,
                                                            value = (gSubject?.name)
                                                                ?: "",
                                                            placeholder = {
                                                                Text(
                                                                    "Выберите"
                                                                )
                                                            },
                                                            onValueChange = {},
                                                            label = { Text("Предмет") },
                                                            trailingIcon = {
                                                                val chevronRotation =
                                                                    animateFloatAsState(if (expandedGSubjects) 90f else -90f)
                                                                GetAsyncIcon(
                                                                    path = RIcons.CHEVRON_LEFT,
                                                                    modifier = Modifier.padding(end = 10.dp)
                                                                        .rotate(chevronRotation.value),
                                                                    size = 15.dp
                                                                )
                                                            },
                                                            shape = RoundedCornerShape(
                                                                15.dp
                                                            ),
                                                            enabled = true//!model.isCreatingFormInProcess
                                                        )
                                                        // menu

                                                        ExposedDropdownMenu(
                                                            expanded = expandedGSubjects,
                                                            onDismissRequest = {
                                                                expandedGSubjects =
                                                                    false
                                                            },
                                                        ) {
                                                            // menu items
                                                            subjectsMap.forEach { selectionOption ->
                                                                DropdownMenuItem(
                                                                    text = {
                                                                        Text(
                                                                            selectionOption.value,
                                                                            //color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (selectionOption.key in sortedList) 0.3f else 1f)
                                                                        )
                                                                    },
                                                                    onClick = {
                                                                        component.onEvent(
                                                                            ScheduleStore.Intent.ciChangeSubjectId(
                                                                                selectionOption.key
                                                                            )
                                                                        )
                                                                        expandedGSubjects =
                                                                            false
                                                                    },
                                                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                                                )
                                                            }
                                                        }
                                                    }


                                                    CTextField(
                                                        value = filterStudents.value,
                                                        onValueChange = {
                                                            filterStudents.value = it

                                                        },
                                                        text = "ФИО ученика",
                                                        isEnabled = nModel.state == NetworkState.None,
                                                        isMoveUpLocked = true,
                                                        autoCorrect = false,
                                                        keyboardType = KeyboardType.Text
                                                    )
                                                }



                                                AnimatedVisibility(
                                                    model.ciCustom.isNotEmpty() && model.ciSubjectId != null
                                                ) {
                                                    IconButton(
                                                        onClick = {
                                                            val erroredItems =
                                                                studentErrors.filter {
                                                                    it.logins.filter {
                                                                        it in model.ciCustom
                                                                    }.isNotEmpty()
                                                                }.map {
                                                                    it.copy(
                                                                        logins = it.logins.filter {
                                                                            model.ciCustom.contains(
                                                                                it
                                                                            )
                                                                        }
                                                                    )
                                                                }
                                                            if (erroredItems.isEmpty()) {
                                                                component.mpCreateItem.onEvent(
                                                                    MpChoseStore.Intent.HideDialog
                                                                )
                                                                component.onEvent(
                                                                    ScheduleStore.Intent.ciCreate(
                                                                        null
                                                                    )
                                                                )
                                                            } else {

                                                                val niId = (coItems.filter {
                                                                    it.groupId == model.ciId &&
                                                                            it.subjectId == model.ciSubjectId &&
                                                                            it.formId == model.ciFormId &&
                                                                            tOverlap(
                                                                                it.t,
                                                                                model.ciTiming
                                                                            )
                                                                }.getOrNull(0)?.index)
                                                                    ?: ((model.items.flatMap { it.value.map { it.index } }
                                                                        .maxByOrNull { it }
                                                                        ?: 1) + 1)
                                                                println("NEW NI_ID: ${niId}")
                                                                //                                                            val item = coItems.first { it.index == erroredItem.id }
                                                                component.onEvent(
                                                                    ScheduleStore.Intent.StartConflict(
                                                                        niFormId = model.ciFormId
                                                                            ?: 0,
                                                                        niGroupId = model.ciId!!,
                                                                        niCustom = model.ciCustom,
                                                                        niTeacherLogin = model.ciLogin
                                                                            ?: model.login,
                                                                        niErrors = erroredItems,
                                                                        niId = niId,
                                                                        niOnClick = {
                                                                            component.mpCreateItem.onEvent(
                                                                                MpChoseStore.Intent.HideDialog
                                                                            )
                                                                            component.onEvent(
                                                                                ScheduleStore.Intent.ciCreate(
                                                                                    null
                                                                                )
                                                                            )
                                                                        }
                                                                    ))
                                                                component.chooseConflictDialog.onEvent(
                                                                    CAlertDialogStore.Intent.ShowDialog
                                                                )
                                                            }
                                                        }
                                                    ) {
                                                        GetAsyncIcon(
                                                            RIcons.CHECK
                                                        )
                                                    }
                                                }
                                            }


                                            //

                                            //                                            Column(Modifier.verticalScroll(rememberScrollState())) {
                                            model.students.filter {
                                                "${it.fio.surname} ${it.fio.name} ${it.fio.praname}".lowercase()
                                                    .contains(filterStudents.value.lowercase())
                                            }.sortedWith(
                                                compareBy(
                                                    { !model.ciCustom.contains(it.login) },
                                                    { it.fio.surname })
                                            ).forEach { s ->
                                                val isPicked = model.ciCustom.contains(s.login)
                                                //                                                val cabinetErros = coItems.filter { it.cabinet  }
                                                //                                                val isErrored = s.login in studentErrors.flatMap { it.logins }
                                                val erroredItem =
                                                    studentErrors.firstOrNull { s.login in it.logins }
                                                        ?.copy(
                                                            logins = listOf(s.login)
                                                        )
                                                DropdownMenuItem(
                                                    text = {
                                                        Text(
                                                            text = "${s.fio.surname} ${s.fio.name} ${s.fio.praname}",
                                                            color = MaterialTheme.colorScheme
                                                                .onSurface.blend(
                                                                    when {
                                                                        isPicked -> Color.Green
                                                                        erroredItem == null -> MaterialTheme.colorScheme
                                                                            .onSurface

                                                                        else -> Color.Red
                                                                    }
                                                                )
                                                        )
                                                    },
                                                    onClick = {
                                                        component.onEvent(
                                                            ScheduleStore.Intent.ciChangeCustom(
                                                                if (isPicked) (model.ciCustom - s.login) else model.ciCustom + s.login
                                                            )
                                                        )
                                                        filterStudents.value = ""


                                                    },
                                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                                )
                                            }
//                                            }
                                        }
                                    }
                                }
                            }

                        }
                    }
                }

            }
        }
        Box(Modifier.padding(top = headerP)) {
            val trueItems =
                model.items[key]
            trueItems?.filter {
                it.teacherLogin == login &&
                        (!isExtraHidden || (isExtraHidden && it.groupId != ScheduleIds.EXTRA))
            }
                ?.forEach { e ->
//                    val index = trueItems.indexOf(e)
                    val aState = remember {
                        MutableTransitionState(false).apply {
                            // Start the animation immediately.
                            targetState = true
                        }
                    }
                    AnimatedVisibility(
                        visibleState = aState,
                        enter = fadeIn() + scaleIn()
                    ) {
                        val tPadding by animateDpAsState(
                            minuteHeight * (e.t.start.toMinutes() - dayStartTime.toMinutes())
                        )
                        val height by animateDpAsState(minuteHeight * (e.t.end.toMinutes() - e.t.start.toMinutes()))

                        Card(
                            modifier = Modifier
                                .padding(top = tPadding)
                                .fillMaxWidth()
                                .height(height),
                            colors = CardDefaults.cardColors(
                                containerColor = if (e.teacherLogin == e.teacherLoginBefore) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.tertiaryContainer
                            ),
                            onClick = {
                                component.onEvent(
                                    ScheduleStore.Intent.StartEdit(
                                        e.index,
                                        0
                                    )
                                )
                            }
                        ) {
//                            Text(e.index.toString())
                            Box(Modifier.fillMaxSize()) {
                                if (e.groupId == -11) {
                                    Text(
                                        modifier = Modifier.fillMaxSize(),
                                        textAlign = TextAlign.Center,
                                        text = "Обед",
                                        lineHeight = 14.esp,
                                        fontSize = 14.esp,
                                    )

                                    Text(
                                        e.t.start,
                                        modifier = Modifier.align(
                                            Alignment.BottomStart
                                        )
                                            .padding(start = 5.dp),
                                        lineHeight = 13.esp,
                                        fontSize = 13.esp,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        e.t.end,
                                        modifier = Modifier.align(
                                            Alignment.BottomEnd
                                        )
                                            .padding(end = 5.dp),
                                        lineHeight = 13.esp,
                                        fontSize = 13.esp,
                                        textAlign = TextAlign.Center
                                    )

                                } else if (e.groupId == -6) {
                                    val studentFio =
                                        model.students.filter { e.custom.contains(it.login) }
                                    Text(
                                        modifier = Modifier.align(Alignment.Center),
                                        textAlign = TextAlign.Center,
                                        text = "Доп с\n${studentFio.map { "${it.fio.surname} ${it.fio.name[0]}" }}",
                                        lineHeight = 14.esp,
                                        fontSize = 14.esp,
                                    )
                                    Text(
                                        model.subjects.firstOrNull { it.id == e.subjectId }?.name.toString(),
                                        modifier = Modifier.align(
                                            Alignment.TopEnd
                                        ),
                                        lineHeight = 13.esp,
                                        fontSize = 13.esp,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        e.t.start,
                                        modifier = Modifier.align(
                                            Alignment.BottomStart
                                        )
                                            .padding(start = 5.dp),
                                        lineHeight = 13.esp,
                                        fontSize = 13.esp,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        e.t.end,
                                        modifier = Modifier.align(
                                            Alignment.BottomEnd
                                        )
                                            .padding(end = 5.dp),
                                        lineHeight = 13.esp,
                                        fontSize = 13.esp,
                                        textAlign = TextAlign.Center
                                    )

                                    Text(
                                        e.cabinet.toString(),
                                        modifier = Modifier.align(
                                            Alignment.TopStart
                                        ).padding(start = 5.dp),
                                        lineHeight = 13.esp,
                                        fontSize = 13.esp,
                                        textAlign = TextAlign.Center
                                    )
                                } else {

                                    val group =
                                        model.groups.first { it.id == e.groupId }
                                    Text(
                                        text = buildAnnotatedString {
                                            withStyle(
                                                style = SpanStyle(
                                                    fontWeight = FontWeight.Bold
                                                )
                                            ) {
                                                append(model.subjects.first { it.id == group.subjectId }.name)
                                            }
                                            append("\n" + group.name)
                                        },
                                        lineHeight = 14.esp,
                                        fontSize = 14.esp,
                                        modifier = Modifier.align(
                                            Alignment.Center
                                        ),
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        e.t.start,
                                        modifier = Modifier.align(
                                            Alignment.BottomStart
                                        )
                                            .padding(start = 5.dp),
                                        lineHeight = 13.esp,
                                        fontSize = 13.esp,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        e.t.end,
                                        modifier = Modifier.align(
                                            Alignment.BottomEnd
                                        )
                                            .padding(end = 5.dp),
                                        lineHeight = 13.esp,
                                        fontSize = 13.esp,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        e.cabinet.toString(),
                                        modifier = Modifier.align(
                                            Alignment.TopEnd
                                        )
                                            .padding(end = 5.dp),
                                        lineHeight = 13.esp,
                                        fontSize = 13.esp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                                val logins = fetchLoginsOfLesson(
                                    trueItems = trueItems,
                                    solvedConflictsItems = model.solveConflictItems[key],
                                    students = model.students,
                                    forms = model.forms,
                                    lessonIndex = e.index,
                                    state = model
                                )
                                val okKids = logins?.okLogins?.mapNotNull { l ->
                                    getFormatedKid(l, model)
                                } ?: listOf()
                                val deletedKids = logins?.deletedLogins?.mapNotNull { l ->
                                    getFormatedKid(l, model)
                                } ?: listOf()
                                EditPopup(
//                                    model = model,
                                    nModel = nModel,
//                                    index = index,
                                    e = e,
                                    component = component,
                                    trueItems = trueItems,
                                    tLogin = login,
                                    okKids = okKids,
                                    deletedKids = deletedKids,
                                    currentForm = null
                                )


                            }
                        }
                    }
                }

            val tStart = remember { mutableStateOf("") }
            val tEnd = remember { mutableStateOf("") }
            AnimatedVisibility(
                (model.ciLogin == c.login && model.ciTiming != null && mpModel.isDialogShowing) ||
                        (((model.eiNewLogin
                            ?: trueItems?.firstOrNull { it.index == model.eiIndex }?.teacherLogin) == c.login && model.eiTiming != null) && mpEditModel.isDialogShowing),
                enter = fadeIn() + scaleIn()
            ) {
                val t = if (mpEditModel.isDialogShowing) ScheduleTiming(
                    start = model.eiTiming?.first ?: "00:01",
                    end = model.eiTiming?.second ?: "00:02"
                ) else model.ciTiming ?: ScheduleTiming(
                    start = tStart.value,
                    end = tEnd.value
                )
                if (model.ciTiming != null) {
                    tStart.value = t.start
                    tEnd.value = t.end
                }
                val tPadding by animateDpAsState(
                    (minuteHeight * (t.start.toMinutes() - dayStartTime.toMinutes())).coerceAtLeast(
                        0.dp
                    )
                )
                val height by animateDpAsState(
                    (minuteHeight * (t.end.toMinutes() - t.start.toMinutes())).coerceAtLeast(
                        0.dp
                    )
                )

                Card(
                    Modifier.padding(top = tPadding)
                        .fillMaxWidth()
                        .height(height),
                    colors = CardDefaults.cardColors(
                        containerColor =
                        if (t.cabinetErrorGroupId == 0 && t.studentErrors.isEmpty())
                            MaterialTheme.colorScheme.primaryContainer.copy(
                                alpha = .3f
                            )
                        else
                            MaterialTheme.colorScheme.errorContainer.copy(
                                alpha = .3f
                            )
                    )
                ) { }
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BoxScope.EditPopup(
    nModel: NetworkInterface.NetworkModel,
    e: ScheduleItem,
//    index: Int,
    component: ScheduleComponent,
    trueItems: List<ScheduleItem>,
    tLogin: String?,
    okKids: List<Pair<SchedulePerson, String?>>,
    deletedKids: List<Pair<SchedulePerson, String?>>,
    currentForm: String?
) {
    val model by component.model.subscribeAsState()
    if (model.eiIndex == e.index) {
        if (e.groupId !in listOf(ScheduleIds.FOOD, ScheduleIds.EXTRA, 0)) {

            val login = tLogin ?: trueItems.first { it.index == e.index }.teacherLogin
            val c = model.teachers.first { it.login == login }

            val groupId =
                model.eiGroupId ?: e.groupId
            val cabinetik =
                model.eiCabinet ?: e.cabinet
            val newLogin =
                model.eiNewLogin
            val t =
                model.eiTiming ?: Pair(
                    e.t.start,
                    e.t.end
                )

            mpChoseDesktopContent(
                component = component.mpEditItem,
                offset = DpOffset(
                    x = 130.dp,
                    y = (-35).dp
                ),
                backButton =
                when (model.eiState) {
                    EditState.Preview -> null

                    else -> {
                        {
                            component.onEvent(
                                ScheduleStore.Intent.eiChangeState(
                                    EditState.Preview
                                )
                            )
                        }
                    }
                }

            ) {
                /////
                val group =
                    model.groups.firstOrNull { it.id == groupId }
                if (group != null) {
                    val subject =
                        model.subjects.first { it.id == group.subjectId }
                    when (model.eiState) {
                        EditState.Preview -> Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(
                                horizontal = 10.dp
                            )
                                .widthIn(min = 120.dp)
                                .wrapContentSize()
                        ) {
                            Text(
                                if (component.isCanBeEdited) "Редактировать" else "Урок",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.esp
                            )
                            Spacer(
                                Modifier.height(
                                    8.dp
                                )
                            )
                            CTextButton(
                                buildAnnotatedString {
                                    withStyle(
                                        ParagraphStyle(
                                            TextAlign.Center,
                                            lineHeight = MaterialTheme.typography.titleMedium.fontSize
                                        )
                                    ) {
                                        withStyle(
                                            SpanStyle(
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        ) {
                                            append(
                                                subject.name
                                            )
                                        }
                                        append(
                                            "\n${group.name}"
                                        )
                                    }
                                }
                            ) {
                                if (component.isCanBeEdited) {
                                    component.onEvent(
                                        ScheduleStore.Intent.eiChangeState(
                                            EditState.Groups
                                        )
                                    )
                                }
                            }
                            Spacer(
                                Modifier.height(
                                    7.dp
                                )
                            )
                            CTextButton(
                                buildAnnotatedString {
                                    withStyle(
                                        ParagraphStyle(
                                            TextAlign.Center,
                                            lineHeight = MaterialTheme.typography.titleMedium.fontSize
                                        )
                                    ) {
                                        append(
                                            "${t.first}-${t.second}"
                                        )
                                    }
                                }
                            ) {
                                if (component.isCanBeEdited) {
                                    component.onEvent(
                                        ScheduleStore.Intent.eiChangeState(
                                            EditState.Timings
                                        )
                                    )
                                }
                            }
                            Spacer(Modifier.height(5.dp))
                            if ((newLogin != null && e.teacherLoginBefore != newLogin) || e.teacherLogin != e.teacherLoginBefore) {
                                Text("${e.teacherLoginBefore} -> ${newLogin ?: e.teacherLogin}")
                            }
                            Spacer(
                                Modifier.height(
                                    5.dp
                                )
                            )

                            CTextField(
                                value = cabinetik.toString(),
                                onValueChange = {
                                    if (component.isCanBeEdited) {
                                        if (it == "") {
                                            component.onEvent(
                                                ScheduleStore.Intent.eiCheck(
                                                    cabinet = 0,
                                                    login = login,
                                                    id = groupId,
                                                    s = t
                                                )
                                            )
                                            component.onEvent(
                                                ScheduleStore.Intent.eiChangeCabinet(
                                                    0
                                                )
                                            )
                                        } else if (it.matches(
                                                Regex(
                                                    "^[1-3]?[0-1]?[0-9]?$"
                                                )
                                            )
                                        ) {
                                            component.onEvent(
                                                ScheduleStore.Intent.eiCheck(
                                                    cabinet = it.toInt(),
                                                    login = login,
                                                    id = groupId,
                                                    s = t
                                                )
                                            )
                                            component.onEvent(
                                                ScheduleStore.Intent.eiChangeCabinet(
                                                    it.toInt()
                                                )
                                            )
                                        }
                                    }
                                },
                                text = "Кабинет",
                                isEnabled = nModel.state == NetworkState.None && component.isCanBeEdited,
                                isMoveUpLocked = true,
                                autoCorrect = false,
                                keyboardType = KeyboardType.Number,
                                modifier = Modifier.width(
                                    130.dp
                                ).height(
                                    60.dp
                                )
                            )
                            if (component.isCanBeEdited) {
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    IconButton(
                                        onClick = {

                                            //
                                            component.onEvent(
                                                ScheduleStore.Intent.eiDelete(
                                                    e.index
                                                )
                                            )
                                        }
                                    ) {
                                        GetAsyncIcon(
                                            RIcons.TRASH_CAN_REGULAR
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            component.onEvent(
                                                ScheduleStore.Intent.eiChangeState(
                                                    EditState.Swap
                                                )
                                            )
                                        }
                                    ) {
                                        GetAsyncIcon(
                                            path = RIcons.SWAP_HORIZ
                                        )
                                    }
                                    AnimatedVisibility(
                                        (model.eiCabinet !in listOf(
                                            null,
                                            e.cabinet
                                        ) ||
                                                model.eiGroupId !in listOf(
                                            null,
                                            e.groupId
                                        ) ||
                                                model.eiTiming !in listOf(
                                            null,
                                            Pair(
                                                e.t.start,
                                                e.t.end
                                            )
                                        ) || (newLogin != null))
                                    ) {
                                        if (model.eiCabinetErrorGroupId == 0 && model.eiStudentErrors.isEmpty()) {
                                            IconButton(
                                                onClick = {
                                                    component.onEvent(
                                                        ScheduleStore.Intent.eiSave(
                                                            index = e.index,
                                                            cabinet = cabinetik,
                                                            login = newLogin
                                                                ?: login,
                                                            id = groupId,
                                                            s = t
                                                        )
                                                    )
                                                }
                                            ) {
                                                GetAsyncIcon(
                                                    RIcons.CHECK
                                                )
                                            }
                                        } else {
                                            ///
                                            val cabinetErrorGroup =
                                                model.groups.firstOrNull { it.id == model.eiCabinetErrorGroupId }
                                            val cabinetErrorSubject =
                                                if (cabinetErrorGroup != null) model.subjects.firstOrNull { it.id == cabinetErrorGroup.subjectId } else null

                                            val studentErrors =
                                                getStudentErrors(
                                                    model.eiStudentErrors,
                                                    model
                                                )
//                                            val login = tLogin ?: trueItems.first { it.index == e.index }.teacherLogin
//                                            val c = model.teachers.first { it.login == login }

//                                            val groupId =
//                                                model.eiGroupId ?: e.groupId
//                                            val cabinetik =
//                                                model.eiCabinet ?: e.cabinet
//                                            val newLogin =
//                                                model.eiNewLogin
//                                            val t =
//                                                model.eiTiming ?: Pair(
//                                                    e.t.start,
//                                                    e.t.end
//                                                )
                                            ErrorsTooltip(
                                                cabinetErrorSubject = cabinetErrorSubject,
                                                cabinetErrorGroup = cabinetErrorGroup,
                                                studentErrors = studentErrors,
                                                cabinetErrorGroupId = model.eiCabinetErrorGroupId,
                                                component = component,
                                                niCustom = listOf(""),
                                                niFormId = model.eiFormId ?: 0,
                                                niGroupId = groupId,
                                                niTeacherLogin = login,
                                                classicStudentErrors = model.eiStudentErrors,
                                                niOnClick = {
                                                    component.onEvent(
                                                        ScheduleStore.Intent.eiSave(
                                                            index = e.index,
                                                            cabinet = cabinetik,
                                                            login = newLogin
                                                                ?: login,
                                                            id = groupId,
                                                            s = t
                                                        )
                                                    )
                                                },
                                                niIndex = model.eiIndex!!
                                            )
                                        }
                                    }

                                    ///
                                }
                            }

                            InThisGroupContent(okKids, deletedKids, currentForm = null)
                        }


                        EditState.Groups -> (model.teachers.first { it.login == c.login }.groups.filter { it.second }
                            .sortedBy { x -> model.groups.first { it.id == x.first }.subjectId }).forEach { s ->
                                val egroup =
                                    model.groups.firstOrNull { it.id == s.first }
                                if (egroup != null) {
                                    val esubject =
                                        model.subjects.firstOrNull { it.id == egroup.subjectId }
                                    if (esubject != null) {
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    "${esubject.name}  ${egroup.name}"
                                                )
                                            },
                                            onClick = {
                                                component.onEvent(
                                                    ScheduleStore.Intent.eiCheck(
                                                        cabinet = cabinetik,
                                                        login = login,
                                                        id = s.first,
                                                        s = t
                                                    )
                                                )
                                                component.onEvent(
                                                    ScheduleStore.Intent.eiChooseGroup(
                                                        s.first
                                                    )
                                                )
                                            },
                                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                        )
                                    }
                                }
                            }

                        EditState.Timings -> {
                            var isCustomTime by remember {
                                mutableStateOf(
                                    false
                                )
                            }
                            var customTime by remember {
                                mutableStateOf(
                                    ""
                                )
                            }

                            if (!isCustomTime) {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            "Своё значение"
                                        )
                                    },
                                    onClick = {
                                        isCustomTime =
                                            true
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                )
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CTextField(
                                        value = customTime,
                                        onValueChange = {
                                            if (!it.contains(
                                                    " "
                                                )
                                            ) {
                                                if (it.length <= 11) {
                                                    customTime =
                                                        it
                                                }
                                            }
                                        },
                                        text = "Время",
                                        isEnabled = true,
                                        isMoveUpLocked = true,
                                        autoCorrect = false,
                                        keyboardType = KeyboardType.Number,
                                        modifier = Modifier.width(
                                            130.dp
                                        )
                                            .height(
                                                60.dp
                                            )
                                    )

                                    Spacer(
                                        Modifier.width(
                                            5.dp
                                        )
                                    )
                                    if (customTime.length == 11 && isTimeFormat(
                                            customTime
                                        )
                                    ) {
                                        IconButton(
                                            onClick = {
                                                val parts =
                                                    customTime.split(
                                                        "-"
                                                    )
                                                with(
                                                    component
                                                ) {
                                                    onEvent(
                                                        ScheduleStore.Intent.eiCheck(
                                                            cabinet = cabinetik,
                                                            login = login,
                                                            id = groupId,
                                                            s = Pair(
                                                                parts[0],
                                                                parts[1]
                                                            )
                                                        )
                                                    )
                                                    onEvent(
                                                        ScheduleStore.Intent.eiChangeTiming(
                                                            Pair(
                                                                parts[0],
                                                                parts[1]
                                                            )
                                                        )
                                                    )
                                                }
                                            }
                                        ) {
                                            GetAsyncIcon(
                                                RIcons.CHECK
                                            )
                                        }

                                    }
                                }
                            }

                            timingsPairs.forEach { t ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            "${t.first}-${t.second}",
                                            modifier = Modifier.align(
                                                Alignment.Center
                                            )
                                        )
                                    },
                                    onClick = {
                                        component.onEvent(
                                            ScheduleStore.Intent.eiCheck(
                                                cabinet = cabinetik,
                                                login = login,
                                                id = groupId,
                                                s = t
                                            )
                                        )
                                        component.onEvent(
                                            ScheduleStore.Intent.eiChangeTiming(
                                                t
                                            )
                                        )
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                )
                            }
                        }

                        EditState.Swap -> (model.teachers.filter { it.login != e.teacherLogin }).forEach { t ->
                            val coItems =
                                (trueItems - e).filter {
                                    // ! (закончилось раньше чем началось наше) или (началось позже чем началось наше)
                                    ((!((it.t.end.toMinutes() < e.t.start.toMinutes() ||
                                            it.t.start.toMinutes() > e.t.end.toMinutes())) && it.groupId != -11) ||
                                            (!((it.t.end.toMinutes() <= e.t.start.toMinutes() ||
                                                    it.t.start.toMinutes() >= e.t.end.toMinutes())) && it.groupId == -11))
                                }
                            if (t.login in (model.activeTeachers[model.currentDate.second]
                                    ?: listOf())
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            "${t.fio.surname} ${t.fio.name}",
                                            color = if (coItems.map { it.teacherLogin == t.login }
                                                    .isEmpty() || t.login == e.teacherLogin) MaterialTheme.colorScheme.onBackground
                                            else MaterialTheme.colorScheme.error
                                        )
                                    },
                                    onClick = {

                                        if (coItems.map { it.teacherLogin == t.login }
                                                .isEmpty() || t.login == e.teacherLogin) {
                                            component.onEvent(
                                                ScheduleStore.Intent.eiChangeLogin(
                                                    t.login
                                                )
                                            )
                                        }
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                )
                            }
                        }
                    }
                    ////
                } else {
                    Text("Ошибка")
                    IconButton(
                        onClick = {
                            component.onEvent(
                                ScheduleStore.Intent.eiDelete(
                                    e.index
                                )
                            )
                        }
                    ) {
                        GetAsyncIcon(
                            RIcons.TRASH_CAN_REGULAR
                        )
                    }
                }
            }
        } else {
            mpChoseDesktopContent(
                component = component.mpEditItem,
                offset = DpOffset(
                    x = 130.dp,
                    y = (-10).dp
                ),
                backButton = null
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    InThisGroupContent(okKids, deletedKids, currentForm = currentForm)
                    CTextButton(
                        text = "Удалить${if (e.groupId == -6) " доп" else ""}",
                        modifier = Modifier.padding(7.dp)
                    ) {
                        component.onEvent(ScheduleStore.Intent.eiDelete(e.index))
                    }
                }
            }
        }
    }
}

fun getStudentErrors(
    errors: List<StudentError>,
    model: ScheduleStore.State
): List<StudentErrorCompose> {
    return errors.mapNotNull { e ->
        val groupx =
            model.groups.firstOrNull { it.id == e.groupId }
        val subjectx =
            if (groupx != null) model.subjects.firstOrNull { it.id == groupx.subjectId } else null
        if (subjectx != null) {
            StudentErrorCompose(
                subjectName = subjectx.name,
                groupName = groupx!!.name,
                studentFios = model.students.filter { it.login in e.logins }
                    .map { it.fio.surname }
            )
        } else if (e.groupId == -6) {
            StudentErrorCompose(
                subjectName = "Доп с",
                groupName = "${model.teachers.firstOrNull { it.login == e.teacherLogin }?.fio?.surname}",
                studentFios = model.students.filter { it.login in e.logins }
                    .map { it.fio.surname }
            )
        } else if (e.groupId == 0) {
            StudentErrorCompose(
                subjectName = "Событие",
                groupName = getNameOfConflictLesson(
                    groupId = 0,
                    teacherLogin = e.teacherLogin,
                    id = e.id,
                    model = model
                ),
                studentFios = model.students.filter { it.login in e.logins }
                    .map { it.fio.surname }
            )
        } else if (e.groupId == -11) {
            StudentErrorCompose(
                subjectName = "Приём пищи",
                groupName = "",
                studentFios = model.students.filter { it.login in e.logins }
                    .map { it.fio.surname }
            )
        } else null
    }
}

@Composable
fun InThisGroupContent(
    okKids: List<Pair<SchedulePerson, String?>>,
    deletedKids: List<Pair<SchedulePerson, String?>>,
    currentForm: String?
) {
    val currentFormColor = colorScheme.onSurface.blend(
        colorScheme.primary, amount = .5f
    )
    Column(Modifier.padding(horizontal = 10.dp, vertical = 5.dp)) {
        Text("В этой группе:")
        if (okKids.isNotEmpty()) {
            okKids.forEach {
                val student = it.first
                val form = it.second
                val isCurrentForm = currentForm != null && currentForm == form
                Text(
                    "${form?.let { f -> "$f " } ?: ""}${student.fio.surname} ${student.fio.name.first()}. ${(student.fio.praname ?: " ").first()}.",
                    color =  if (isCurrentForm) currentFormColor else Color.Unspecified,
                    fontWeight = if (isCurrentForm) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }
        if (deletedKids.isNotEmpty()) {
            deletedKids.forEach {
                val student = it.first
                val form = it.second
                Text(
                    "${it.second?.let { f -> "$f " } ?: ""}${student.fio.surname} ${student.fio.name.first()}. ${(student.fio.praname ?: " ").first()}.",
                    textDecoration = TextDecoration.LineThrough,
                    modifier = Modifier.alpha(.5f),
                    color = if (currentForm != null && currentForm == form) currentFormColor else Color.Unspecified

                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ErrorsTooltip(
    cabinetErrorSubject: ScheduleSubject?,
    cabinetErrorGroup: ScheduleGroup?,
    studentErrors: List<StudentErrorCompose>,
    classicStudentErrors: List<StudentError>,
    cabinetErrorGroupId: Int,
    component: ScheduleComponent,
    niFormId: Int,
    niGroupId: Int,
    niCustom: List<String>,
    niTeacherLogin: String,
    niIndex: Int,
    niOnClick: () -> Unit
) {
    val tState =
        rememberTooltipState(
            isPersistent = true
        )
    val model by component.model.subscribeAsState()
    TooltipBox(
        state = tState,
        tooltip = {
            PlainTooltip() {
                Text(
                    buildString {
                        if (cabinetErrorGroupId == -6) {
                            append(
                                "Кабинет занят: ${"Доп"}"
                            )
                        }
                        if (cabinetErrorSubject != null) {
                            append(
                                "Кабинет занят: ${cabinetErrorSubject.name} ${cabinetErrorGroup!!.name}"
                            )
                        }
                        if (studentErrors.isNotEmpty()) {
                            studentErrors.forEachIndexed { i, it ->
                                if (cabinetErrorSubject != null || i != 0) append(
                                    "\n"
                                )
                                append(
                                    "Накладка: "
                                )
                                append(
                                    "${it.subjectName} ${it.groupName} ${it.studentFios}"
                                )
                            }
                        }
                    },
                    textAlign = TextAlign.Center
                )
            }
        },
        positionProvider = popupPositionProvider
    ) {
        IconButton(
            {
                val key =
                    if (model.isDefault) model.defaultDate.toString() else model.currentDate.second
                val trueItems =
                    model.items[key]?.filter { it.groupId in model.groups.map { it.id } + (-11) + (0) + (-6) }
                        ?: emptyList()
                //                                                    ?.filter { (it.formId == null ) && (it.groupId != -6 || it.custom in form.logins) }
                val coItems =
                    (trueItems).filter {
                        // ! (закончилось раньше чем началось наше) или (началось позже чем началось наше)
                        ((!((it.t.end.toMinutes() < model.ciTiming!!.start.toMinutes() ||
                                it.t.start.toMinutes() > model.ciTiming!!.end.toMinutes())) && it.groupId != -11) ||
                                (!((it.t.end.toMinutes() <= model.ciTiming!!.start.toMinutes() ||
                                        it.t.start.toMinutes() >= model.ciTiming!!.end.toMinutes())) && it.groupId == -11))
                    }

                val niId = (coItems.filter {
                    it.groupId == model.ciId &&
                            it.subjectId == model.ciSubjectId &&
                            it.formId == model.ciFormId &&
                            tOverlap(it.t, model.ciTiming)
                }.getOrNull(0)?.index) ?: niIndex
                println("NEW NI_ID: ${niId}")


                component.chooseConflictDialog.onEvent(CAlertDialogStore.Intent.ShowDialog)
                component.onEvent(
                    ScheduleStore.Intent.StartConflict(
                        niFormId = niFormId,
                        niGroupId = niGroupId,
                        niCustom = niCustom,
                        niTeacherLogin = niTeacherLogin,
                        niErrors = classicStudentErrors,
                        niOnClick = niOnClick,
                        niId = niId
                    )
                )
            },
            enabled = true
        ) {
            GetAsyncIcon(
                path = RIcons.ERROR_OUTLINE,
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

data class StudentErrorCompose(
    val subjectName: String,
    val groupName: String,
    val studentFios: List<String>
)
