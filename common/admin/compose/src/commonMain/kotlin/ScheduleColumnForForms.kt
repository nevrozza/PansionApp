import admin.schedule.ScheduleFormValue
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import components.CustomTextField
import components.cClickable
import components.mpChose.MpChoseStore
import components.networkInterface.NetworkInterface
import components.networkInterface.NetworkState
import decomposeComponents.mpChoseComponent.mpChoseDesktopContent
import schedule.*
import server.cut
import server.isTimeFormat
import server.toMinutes


//data class ScheduleForFormsItem(
//    val lessons:
//)

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LazyItemScope.ScheduleColumnForForms(
    component: ScheduleComponent,
    model: ScheduleStore.State,
    nModel: NetworkInterface.NetworkModel,
    mpModel: MpChoseStore.State,
    scrollState: ScrollState,
    minuteHeight: Dp,
    dayStartTime: String,
    form: ScheduleFormValue,
    formId: Int,
    key: String,
    headerP: Dp,
    density: Density
) {
    val groups = model.groups.filter {
        it.id in model.students.filter { s -> s.login in form.logins }.flatMap { s -> s.groups.map { it.first } }
    }


    Box(
        Modifier.width(200.dp).fillMaxHeight().padding(end = 5.dp).animateItem(fadeInSpec = null, fadeOutSpec = null)
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
            modifier = Modifier.zIndex(1000f)
                .offset(y = with(density) { scrollState.value.toDp() })
        ) {
            Box(
                Modifier.height(headerP)
            ) {
//                Column(
//                    modifier = Modifier.fillMaxSize()
//                        .align(Alignment.Center),
//                    verticalArrangement = Arrangement.Center,
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
                Box(Modifier.fillMaxSize()) {
                    Text(
                        "${form.num}${if (form.shortTitle.length < 2) "-" else " "}${form.shortTitle}",
                        modifier = Modifier.align(Alignment.Center),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        lineHeight = 15.sp
                    )
                    Text(
                        text = "${form.logins.size}",
                        modifier = Modifier.align(Alignment.BottomEnd)
                            .padding(end = 5.dp, bottom = 2.dp)
                    )
                    if (component.isCanBeEdited) {
                        Box(modifier = Modifier.align(Alignment.CenterEnd)) {
                            IconButton(
                                onClick = {
                                    with(component) {
                                        mpCreateItem.onEvent(MpChoseStore.Intent.ShowDialog)
                                        onEvent(
                                            ScheduleStore.Intent.ciStart(
                                                formId.toString(),
                                                formId = formId
                                            )
                                        )
                                    }
                                }
                            ) {
                                Icon(
                                    Icons.Rounded.Add, null
                                )
                            }
                            if (model.ciLogin == formId.toString()) {

                                mpChoseDesktopContent(
                                    component = component.mpCreateItem,
                                    backButton = if (model.ciPreview || model.ciId == null) {
                                        null
                                    } else {
                                        {
                                            component.onEvent(ScheduleStore.Intent.ciNullGroupId)
                                        }
                                           },
                                    isCanBeOpened = component.isCanBeEdited
                                ) {
                                    when {
                                        model.ciId == null -> {
                                            val value = model.ciCustom.firstOrNull() ?: ""
                                            Row {
                                                CustomTextField(
                                                    value = value,
                                                    onValueChange = {
                                                        component.onEvent(ScheduleStore.Intent.ciChangeCustom(listOf(it)))
                                                                    },
                                                    text = "Событие",
                                                    isEnabled = nModel.state == NetworkState.None,
                                                    isMoveUpLocked = true,
                                                    autoCorrect = false,
                                                    keyboardType = KeyboardType.Text
                                                )

                                                IconButton(
                                                    onClick = {
                                                        if (value.isNotBlank()) {
                                                            component.onEvent(
                                                                ScheduleStore.Intent.ciChooseGroup(
                                                                    0
                                                                )
                                                            )
                                                        }
                                                              },
                                                    enabled = value.isNotBlank()
                                                ) {
                                                    Icon(
                                                        Icons.Rounded.Done, null
                                                    )
                                                }
                                            }
                                            DropdownMenuItem(
                                                text = { Text("Приём пищи") },
                                                onClick = {
                                                    component.onEvent(
                                                        ScheduleStore.Intent.ciChooseGroup(
                                                            -11
                                                        )
                                                    )
                                                          },
                                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                                )
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
                                                CustomTextField(
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
                                                        CustomTextField(
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
                                                                                    val parts =
                                                                                        customTime.split(
                                                                                            "-"
                                                                                        )
                                                                                    with(
                                                                                        component
                                                                                    ) {
//                                                                                        onEvent(
//                                                                                            ScheduleStore.Intent.ciChooseTime(
//                                                                                                ScheduleTiming(
//                                                                                                    start = parts[0],
//                                                                                                    end = parts[1]
//                                                                                                )
//                                                                                            )
//                                                                                        )
//                                                                                        onEvent(
//                                                                                            ScheduleStore.Intent.ciPreview
//                                                                                        )
                                                                                        component.onEvent(
                                                                                            ScheduleStore.Intent.ciCreate(ScheduleTiming(
                                                                                                start = parts[0],
                                                                                                end = parts[1]
                                                                                            ))
                                                                                        )
                                                                                    }
                                                                                }
                                                                            ) {
                                                                                Icon(
                                                                                    Icons.Rounded.Done,
                                                                                    null
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
                                                                                niFormId = model.ciFormId ?: 0,
                                                                                niGroupId = model.ciId ?: 0,
                                                                                niCustom = model.ciCustom,
                                                                                niTeacherLogin = model.ciLogin ?: model.login,
                                                                                classicStudentErrors = model.ciTiming!!.studentErrors,
                                                                                niOnClick = {
                                                                                    component.onEvent(
                                                                                        ScheduleStore.Intent.ciCreate( ScheduleTiming(
                                                                                            start = parts[0],
                                                                                            end = parts[1]
                                                                                        ))
                                                                                    )
                                                                                },
                                                                                niIndex = (model.items.flatMap { it.value.map { it.index } }.maxByOrNull { it } ?: 1) + 1
                                                                            )
                                                                        }
                                                                    } else {
                                                                        IconButton(
                                                                            onClick = {
//                                                                                component.onEvent(
//                                                                                    ScheduleStore.Intent.ciChooseTime(
//                                                                                        ScheduleTiming(
//                                                                                            start = parts[0],
//                                                                                            end = parts[1]
//                                                                                        )
//                                                                                    )
//                                                                                )
                                                                                component.onEvent(
                                                                                    ScheduleStore.Intent.ciCreate(ScheduleTiming(
                                                                                        start = parts[0],
                                                                                        end = parts[1]
                                                                                    ))
                                                                                )
                                                                            }
                                                                        ) {
                                                                            Icon(
                                                                                Icons.Rounded.Replay,
                                                                                null
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
                                                                    niTeacherLogin = model.ciLogin ?: model.login,
                                                                    classicStudentErrors = t.studentErrors,
                                                                    niOnClick = {
                                                                            with(
                                                                                component
                                                                            ) {
                                                                                onEvent(
                                                                                    ScheduleStore.Intent.ciCreate(t)
                                                                                )
                                                                            }
                                                                    },
                                                                    niIndex = (model.items.flatMap { it.value.map { it.index } }.maxByOrNull { it } ?: 1) + 1
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
                                            component.onEvent(
                                                ScheduleStore.Intent.ciCreate(null)
                                            )
                                            component.mpCreateItem.onEvent(MpChoseStore.Intent.HideDialog)
                                        }
                                    }
                                }

                            }
                        }
                    }
                }
//                }
            }
        }
        Box(Modifier.padding(top = headerP)) {
            val trueItems =
                model.items[key]?.filter { it.groupId in groups.map { it.id } + (-11) + (0) + (-6) }
                    ?.filter { (it.formId == null || it.formId == formId) && (it.groupId != -6 || form.logins.filter { x -> it.custom.contains(x)  }.isNotEmpty()) }
            trueItems?.forEach { e ->
//                val index = trueItems.indexOf(e)
                val aState = remember {
                    MutableTransitionState(false).apply {
                        // Start the animation immediately.
                        targetState = true
                    }
                }
                val coItems =
                    (trueItems).filter {
                        // ! (закончилось раньше чем началось наше) или (началось позже чем началось наше)
                        ((!((it.t.end.toMinutes() < e.t.start.toMinutes() ||
                                it.t.start.toMinutes() > e.t.end.toMinutes())) && it.groupId != -11) ||
                                (!((it.t.end.toMinutes() <= e.t.start.toMinutes() ||
                                        it.t.start.toMinutes() >= e.t.end.toMinutes())) && it.groupId == -11))
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
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        ),
                        onClick = {
                            component.onEvent(
                                ScheduleStore.Intent.StartEdit(
                                    e.index,
                                    formId
                                )
                            )
                        }
                    ) {
                        if (coItems.size <= 1) {
                            Box(Modifier.fillMaxSize()) {
                                ScheduleForFormsContent(
                                    e = e,
                                    model = model,
                                    component = component,
                                    trueItems = trueItems,
                                    nModel = nModel,
                                    isInPopup = false,
                                    form = form,
                                    coItemsCount = coItems.size,
                                    key = key
                                ) {
                                    component.onEvent(ScheduleStore.Intent.eiDelete(e.index))
                                }

//                                if (model.eiIndex == e.index && model.eiFormId == formId && (model.eiGroupId ?: -1) > 0) {
//                                    mpChoseDesktopContent(
//                                        component = component.mpEditItem,
//                                        offset = DpOffset(
//                                            x = 130.dp,
//                                            y = (-35).dp
//                                        )
//                                    ) {
//
//                                    }
//                                }
                            }
                        } else {
                            Box(Modifier.fillMaxSize()) {
                                Text(
                                    text = "Уроков: ${coItems.size}",
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                                Text(
                                    text = coItems.map { item ->
                                        (model.subjects.firstOrNull {
                                            it.id == model.groups
                                                .firstOrNull { it.id == item.groupId }?.subjectId
                                        }?.name
                                                ?: (if (item.groupId == -6) "Доп" else if (item.groupId == -11) "Еда" else if (item.groupId == 0) "Соб" else "null")).cut(3)
                                    }
                                        .toSet().toString()
                                        .removePrefix("[")
                                        .removeSuffix("]"),
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontSize = 12.sp,
                                    modifier = Modifier.align(Alignment.BottomCenter)
                                )
                                Text(
                                    coItems.minByOrNull { it.t.start.toMinutes() }?.t?.start.toString(),
                                    modifier = Modifier.align(
                                        Alignment.TopStart
                                    )
                                        .padding(start = 5.dp),
                                    lineHeight = 13.sp,
                                    fontSize = 13.sp,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    coItems.maxByOrNull { it.t.end.toMinutes() }?.t?.end.toString(),
                                    modifier = Modifier.align(
                                        Alignment.TopEnd
                                    )
                                        .padding(end = 5.dp),
                                    lineHeight = 13.sp,
                                    fontSize = 13.sp,
                                    textAlign = TextAlign.Center
                                )
                                if (model.eiIndex == e.index && model.eiFormId == formId) {
                                    mpChoseDesktopContent(
                                        component = component.mpEditItem,
                                        offset = DpOffset(
                                            x = 130.dp,
                                            y = (-35).dp
                                        ),
                                        isCanBeOpened = true
                                    ) {
                                        Row(Modifier.horizontalScroll(rememberScrollState())) {
                                            coItems.toSet().forEach { item ->
                                                Column {
                                                    Box(Modifier.width(200.dp).height(80.dp)) {
                                                        ScheduleForFormsContent(
                                                            e = item,
                                                            model = model,
                                                            isInPopup = true,
                                                            component = component,
                                                            trueItems = trueItems,
                                                            nModel = nModel,
                                                            coItemsCount = coItems.size,
                                                            form = form,
                                                            key = key
                                                        ) {
                                                            component.onEvent(ScheduleStore.Intent.eiDelete(item.index))
                                                        }
                                                    }
                                                    val logins = fetchLoginsOfLesson(
                                                        trueItems = trueItems,
                                                        solvedConflictsItems = model.solveConflictItems[key],
                                                        students = model.students,
                                                        forms = model.forms,
                                                        lessonIndex = item.index,
                                                        state = model
                                                    )
                                                    val okKids = logins?.okLogins?.mapNotNull { l ->
                                                        model.students.firstOrNull { it.login == l}
                                                    } ?: listOf()
                                                    val deletedKids = logins?.deletedLogins?.mapNotNull { l ->
                                                        model.students.firstOrNull { it.login == l}
                                                    } ?: listOf()
                                                    Column(Modifier.padding(horizontal = 10.dp, vertical = 5.dp)) {
                                                        Text("В этой группе:")
                                                        if (okKids.isNotEmpty()) {
                                                            okKids.forEach {
                                                                Text("${it.fio.surname} ${it.fio.name.first()}. ${(it.fio.praname ?: " ").first()}.")
                                                            }
                                                        }
                                                        if (deletedKids.isNotEmpty()) {
                                                            deletedKids.forEach {
                                                                Text(
                                                                    "${it.fio.surname} ${it.fio.name.first()}. ${(it.fio.praname ?: " ").first()}.",
                                                                    textDecoration = TextDecoration.LineThrough,
                                                                    modifier = Modifier.alpha(.5f)
                                                                )
                                                            }
                                                        }

                                                    }
                                                }
                                                if (e.index != coItems.last().index) {
                                                    Spacer(Modifier.width(15.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BoxScope.ScheduleForFormsContent(
    e: ScheduleItem,
    model: ScheduleStore.State,
    isInPopup: Boolean,
    component: ScheduleComponent,
    nModel: NetworkInterface.NetworkModel,
    trueItems: List<ScheduleItem>,
    coItemsCount: Int,
    form: ScheduleFormValue,
    key: String,
    onDeleteClick: () -> Unit
) {
    if (e.groupId == -11) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            textAlign = TextAlign.Center,
            text = "Приём пищи",
            lineHeight = 14.sp,
            fontSize = 14.sp,
        )

        Text(
            e.t.start,
            modifier = Modifier.align(
                Alignment.BottomStart
            )
                .padding(start = 5.dp),
            lineHeight = 13.sp,
            fontSize = 13.sp,
            textAlign = TextAlign.Center
        )
        Text(
            e.t.end,
            modifier = Modifier.align(
                Alignment.BottomEnd
            )
                .padding(end = 5.dp),
            lineHeight = 13.sp,
            fontSize = 13.sp,
            textAlign = TextAlign.Center
        )

    } else if (e.groupId == -6) {
        val studentFio = model.students.filter { e.custom.contains(it.login) }
        Text(
            modifier = Modifier.align(Alignment.Center),
            textAlign = TextAlign.Center,
            text = "Доп с\n${studentFio.map { "${it.fio.surname} ${it.fio.name[0]}" }}",
            lineHeight = 14.sp,
            fontSize = 14.sp,
        )
        Text(
            model.subjects.firstOrNull { it.id == e.subjectId }?.name.toString(),
            modifier = Modifier.align(
                Alignment.TopCenter
            ),
            lineHeight = 13.sp,
            fontSize = 13.sp,
            textAlign = TextAlign.Center
        )

        Text(
            e.t.start,
            modifier = Modifier.align(
                Alignment.BottomStart
            )
                .padding(start = 5.dp),
            lineHeight = 13.sp,
            fontSize = 13.sp,
            textAlign = TextAlign.Center
        )
        Text(
            e.t.end,
            modifier = Modifier.align(
                Alignment.BottomEnd
            )
                .padding(end = 5.dp),
            lineHeight = 13.sp,
            fontSize = 13.sp,
            textAlign = TextAlign.Center
        )

        Text(
            e.cabinet.toString(),
            modifier = Modifier.align(
                Alignment.TopStart
            ).padding(start = 5.dp),
            lineHeight = 13.sp,
            fontSize = 13.sp,
            textAlign = TextAlign.Center
        )
        Text(
            model.teachers.first { it.login == e.teacherLogin }.fio.surname,
            modifier = Modifier.align(
                Alignment.TopEnd
            ).padding(start = 5.dp),
            lineHeight = 13.sp,
            fontSize = 13.sp,
            textAlign = TextAlign.Center
        )


    } else if (e.groupId == 0) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            textAlign = TextAlign.Center,
            text = e.custom.firstOrNull() ?: "",
            lineHeight = 14.sp,
            fontSize = 14.sp,
        )

        Text(
            e.t.start,
            modifier = Modifier.align(
                Alignment.BottomStart
            )
                .padding(start = 5.dp),
            lineHeight = 13.sp,
            fontSize = 13.sp,
            textAlign = TextAlign.Center
        )
        Text(
            e.t.end,
            modifier = Modifier.align(
                Alignment.BottomEnd
            )
                .padding(end = 5.dp),
            lineHeight = 13.sp,
            fontSize = 13.sp,
            textAlign = TextAlign.Center
        )

        Text(
            e.cabinet.toString(),
            modifier = Modifier.align(
                Alignment.TopStart
            ).padding(start = 5.dp),
            lineHeight = 13.sp,
            fontSize = 13.sp,
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
            lineHeight = 14.sp,
            fontSize = 14.sp,
            modifier = Modifier.align(
                Alignment.Center
            ),
            textAlign = TextAlign.Center
        )
        Text(
            e.t.start.toString(),
            modifier = Modifier.align(
                Alignment.BottomStart
            )
                .padding(start = 5.dp),
            lineHeight = 13.sp,
            fontSize = 13.sp,
            textAlign = TextAlign.Center
        )
        Text(
            e.t.end,
            modifier = Modifier.align(
                Alignment.BottomEnd
            )
                .padding(end = 5.dp),
            lineHeight = 13.sp,
            fontSize = 13.sp,
            textAlign = TextAlign.Center
        )
        Text(
            e.cabinet.toString(),
            modifier = Modifier.align(
                Alignment.TopEnd
            )
                .padding(end = 5.dp),
            lineHeight = 13.sp,
            fontSize = 13.sp,
            textAlign = TextAlign.Center
        )
        Text(
            text = model.teachers.first { it.login == e.teacherLogin }.fio.surname,
            modifier = Modifier.align(
                Alignment.TopStart
            ).padding(start = 5.dp),
            lineHeight = 13.sp,
            fontSize = 13.sp,
            textAlign = TextAlign.Center
        )
    }

    if (isInPopup) {
        if (component.isCanBeEdited) {
            IconButton(
                onClick = { onDeleteClick() },
                modifier = Modifier.size(20.dp).align(Alignment.TopEnd).padding(top = 5.dp, end = 5.dp)
            ) {
                Icon(
                    Icons.Rounded.Delete,
                    null
                )
            }
        }
    }
    else {
        val logins = fetchLoginsOfLesson(
            trueItems = trueItems,
            solvedConflictsItems = model.solveConflictItems[key],
            students = model.students,
            forms = model.forms,
            lessonIndex = e.index,
            state = model
        )
        val okKids = logins?.okLogins?.mapNotNull { l ->
            model.students.firstOrNull { it.login == l}
        } ?: listOf()
        val deletedKids = logins?.deletedLogins?.mapNotNull { l ->
            model.students.firstOrNull { it.login == l}
        } ?: listOf()
        EditPopup(
            model = model,
            e = e,
            component = component,
            nModel = nModel,
            trueItems = trueItems,
            tLogin = null,
            okKids = okKids,
            deletedKids = deletedKids
//            kids = if (coItemsCount == 1) model.students.filter { it.login in form.logins }
//                .filter { e.groupId in it.groups.map { it.first } } else listOf()
        )
    }
}