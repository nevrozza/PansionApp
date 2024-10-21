package mentoring

import MentorPerson
import allGroupMarks.DatesFilter
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.PermContactCalendar
import androidx.compose.material.icons.rounded.Receipt
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Summarize
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.AnimatedCommonButton
import components.AnimatedElevatedButton
import components.AppBar
import components.CFilterChip
import components.CLazyColumn
import components.CustomCheckbox
import components.CustomTextButton
import components.CustomTextField
import components.DatesLine
import components.GetAvatar
import components.LoadingAnimation
import components.MarkTable
import components.cClickable
import components.networkInterface.NetworkState
import io.github.alexzhirkevich.qrose.options.QrBallShape
import io.github.alexzhirkevich.qrose.options.QrFrameShape
import io.github.alexzhirkevich.qrose.options.QrPixelShape
import io.github.alexzhirkevich.qrose.options.QrShapes
import io.github.alexzhirkevich.qrose.options.roundCorners
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import registration.RegistrationRequest
import root.RootComponent.Config
import view.LocalViewManager
import view.WindowScreen
import view.rememberImeState

@ExperimentalLayoutApi
@Composable
fun MentoringContent(
    component: MentoringComponent
) {
    val model by component.model.subscribeAsState()
    val nModel by component.nInterface.networkModel.subscribeAsState()
    val focusManager = LocalFocusManager.current
    val viewManager = LocalViewManager.current
//    val scrollState = rememberScrollState()
    val imeState = rememberImeState()
    val lazyListState = rememberLazyListState()

    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        Modifier.fillMaxSize(),
        topBar = {
            AppBar(
                title = {

                    Text(
                        "Ученики",
                        modifier = Modifier.padding(start = 10.dp),
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                actionRow = {
                    AnimatedVisibility(
                        model.formsForSummary.isNotEmpty(),
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        IconButton(
                            onClick = {
                                component.onEvent(MentoringStore.Intent.ChangeView)
                            }
                        ) {
                            Icon(
                                if (model.isTableView) Icons.Rounded.PermContactCalendar else Icons.Rounded.Summarize,
                                null
                            )
                        }
                    }
                    IconButton(
                        onClick = {
                            component.onEvent(MentoringStore.Intent.FetchStudents)
                        }
                    ) {
                        Icon(Icons.Rounded.Refresh, null)
                    }

                }
            )
        }
    ) { padding ->

        Crossfade(nModel.state) {
            when (it) {
                NetworkState.Loading -> {
                    Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        LoadingAnimation()
                    }
                }

                NetworkState.None -> {
                    Crossfade(model.isTableView) { cf ->
                        if (cf) {
                            Box(
                                Modifier.fillMaxSize().padding(padding).padding(
                                    bottom =
                                    if (viewManager.orientation.value != WindowScreen.Expanded) {
                                        padding.calculateBottomPadding() + 80.dp - 20.dp
                                    } else 0.dp
                                ),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(modifier = Modifier.offset(y = (-10).dp)) {
                                    Row(
                                        Modifier.horizontalScroll(rememberScrollState())
                                            .offset(y = 10.dp)
                                    ) {
                                        CFilterChip(
                                            label = "За неделю",
                                            isSelected = model.dateFilter is DatesFilter.Week,
                                            state = nModel.state,
                                            coroutineScope = coroutineScope
                                        ) {
                                            component.onEvent(
                                                MentoringStore.Intent.ChangeFilterDate(
                                                    DatesFilter.Week
                                                )
                                            )
                                        }
                                        Spacer(Modifier.width(5.dp))
                                        CFilterChip(
                                            label = "За прошлую неделю",
                                            isSelected = model.dateFilter is DatesFilter.PreviousWeek,
                                            state = nModel.state,
                                            coroutineScope = coroutineScope
                                        ) {
                                            component.onEvent(
                                                MentoringStore.Intent.ChangeFilterDate(
                                                    DatesFilter.PreviousWeek
                                                )
                                            )
                                        }
                                        Spacer(Modifier.width(5.dp))
                                        model.modules.forEach { module ->
                                            CFilterChip(
                                                label = "За ${module} модуль",
                                                isSelected = model.dateFilter is DatesFilter.Module && module in (model.dateFilter as DatesFilter.Module).modules,
                                                state = nModel.state,
                                                coroutineScope = coroutineScope
                                            ) {
                                                component.onEvent(
                                                    MentoringStore.Intent.ChangeFilterDate(
                                                        DatesFilter.Module(
                                                            listOf(module)
                                                        )
                                                    )
                                                )
                                            }
                                            Spacer(Modifier.width(5.dp))
                                        }
                                    }
                                    Row(Modifier.horizontalScroll(rememberScrollState())) {
                                        model.filteredSubjects.forEach { s ->
                                            CFilterChip(
                                                label = s.value,
                                                isSelected = s.key == model.chosenSubject,
                                                state = nModel.state,
                                                coroutineScope = coroutineScope
                                            ) {
                                                component.onEvent(
                                                    MentoringStore.Intent.ChangeSubject(
                                                        s.key
                                                    )
                                                )
                                            }
                                            Spacer(Modifier.width(5.dp))
                                        }
                                    }
                                    MarkTable(
                                        fields = model.filteredStudents.associate { s -> s.login to "${s.fio.surname} ${s.fio.name[0]}.${if (s.fio.praname != null) " " + s.fio.praname!![0] + "." else ""}" },
                                        dms = model.filteredDateMarks,
                                        nki = model.filteredNki
                                    )
                                }
                            }
                        } else {
                            CLazyColumn(padding = padding, isBottomPaddingNeeded = true) {
                                items(model.forms) { f ->
                                    FormsItem(
                                        form = f,
                                        students = model.students.filter { it.formId == f.id },
                                        component = component,
                                        model = model,
                                        requests = model.requests.filter { it.formId == f.id }
                                    )
                                }
                            }
                        }
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FormsItem(
    form: MentorForms,
    students: List<MentorPerson>,
    requests: List<RegistrationRequest>,
    model: MentoringStore.State,
    component: MentoringComponent
) {
    val nPAModel by component.nPreAttendanceInterface.networkModel.subscribeAsState()
    val isExpanded = remember { mutableStateOf(false) }
    Column(Modifier.animateContentSize()) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "${form.num} ${form.title}",
                modifier = Modifier.padding(start = 7.dp),
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = form.id in model.formsForSummary,
                    onCheckedChange = {
                        component.onEvent(MentoringStore.Intent.FormToSummary(form.id))
                    }
                )
                IconButton(
                    onClick = { isExpanded.value = !isExpanded.value },
                    modifier = Modifier.size(35.dp)
                ) {
                    Icon(
                        if (isExpanded.value) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                        null
                    )
                }
                IconButton(
                    onClick = {
                        component.onEvent(
                            MentoringStore.Intent.ManageQr(
                                formId = form.id,
                                isOpen = !form.isQrActive
                            )
                        )
                    },
                    modifier = Modifier.size(35.dp)
                ) {
                    AnimatedContent(
                        if (form.isQrActive) Icons.Rounded.Close
                        else Icons.Rounded.Add
                    ) {
                        Icon(it, null)
                    }
                }
            }
        }
        Spacer(Modifier.height(2.dp))
        AnimatedVisibility(form.isQrActive) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    rememberQrCodePainter(
                        data = "Form" + form.id,
                        shapes = QrShapes(
                            ball = QrBallShape.roundCorners(.25f),
                            //code = QrCodeShape.circle(),
                            darkPixel = QrPixelShape.roundCorners(),
                            frame = QrFrameShape.roundCorners(.25f)
                        )
                    ),
                    null,
                    Modifier.padding(bottom = 7.dp).fillMaxWidth(),
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
                )
                Spacer(Modifier.height(2.dp))
                Text("Form" + form.id)
            }
        }
        Spacer(Modifier.height(6.dp))
        AnimatedVisibility(
            requests.isNotEmpty()
        ) {
            Column {
                requests.forEach { r ->
                    Surface(
                        tonalElevation = 2.dp,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(6.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,

                                modifier = Modifier.fillMaxWidth()
                            ) {
                                GetAvatar(
                                    avatarId = r.avatarId,
                                    name = r.name,
                                    size = 55.dp,
                                    textSize = 22.sp
                                )
                                Spacer(Modifier.width(10.dp))
                                Column {
                                    Row {
                                        Text(r.surname, fontWeight = FontWeight.Bold)
                                        Spacer(Modifier.width(3.dp))
                                        Text(r.name, fontWeight = FontWeight.Bold)
                                        Spacer(Modifier.width(3.dp))
                                        Text(r.praname, fontWeight = FontWeight.Bold)
                                    }
                                    if (r.fioFather.isNotEmpty()) {
                                        Row {
                                            Text(r.fioFather)
                                        }
                                    }
                                    if (r.fioMother.isNotEmpty()) {
                                        Row {
                                            Text(r.fioMother)
                                        }
                                    }
                                }

                            }
                            Row(
                                Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Spacer(Modifier.width(15.dp))
                                Text(
                                    "${r.birthday.subSequence(0, 2)}.${
                                        r.birthday.subSequence(
                                            2,
                                            4
                                        )
                                    }.${r.birthday.subSequence(4, 8)}"
                                )
                                Row(
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    IconButton(
                                        onClick = {
                                            component.onEvent(
                                                MentoringStore.Intent.SolveRequest(
                                                    true,
                                                    r
                                                )
                                            )
                                        }
                                    ) {
                                        Icon(
                                            Icons.Rounded.Done, null
                                        )
                                    }
                                    Spacer(Modifier.width(7.dp))

                                    IconButton(
                                        onClick = {
                                            component.onEvent(
                                                MentoringStore.Intent.SolveRequest(
                                                    false,
                                                    r
                                                )
                                            )
                                        }
                                    ) {
                                        Icon(
                                            Icons.Rounded.Close, null
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                }
                Spacer(Modifier.height(12.dp))
            }
        }
        if (isExpanded.value) {
            students.forEach { s ->
                val isChosenPA = model.chosenAttendanceLogin == s.login
                Surface(
                    shape = RoundedCornerShape(15.dp),
                    tonalElevation = if (model.chosenLogin == s.login) 17.dp else 2.dp
                ) {

                    Column(
                        Modifier.fillMaxWidth().padding(4.dp).padding(start = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(Modifier.fillMaxWidth()) {
                            Column(Modifier.align(Alignment.CenterStart)) {
                                Text("${s.fio.surname} ${s.fio.name}")
                                Text("${s.fio.praname}")
                            }
                            Row(
                                Modifier.align(Alignment.CenterEnd),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                IconButton(
                                    onClick = {
                                        if (!isChosenPA) {
                                            component.onEvent(
                                                MentoringStore.Intent.SelectPreAttendanceLogin(
                                                    login = s.login,
                                                    date = model.currentDate.second
                                                )
                                            )
                                        } else {
                                            component.onEvent(
                                                MentoringStore.Intent.SelectPreAttendanceLogin(
                                                    login = null,
                                                    date = model.currentDate.second
                                                )
                                            )
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Receipt, null,
                                        tint = if (isChosenPA) MaterialTheme.colorScheme.primary else LocalContentColor.current
                                    )
                                }
                                Box(
                                    Modifier.size(height = 15.dp, width = 5.dp)
                                        .clip(RoundedCornerShape(15.dp))
                                        .background(
                                            MaterialTheme.colorScheme.primaryContainer.copy(
                                                alpha = .5f
                                            )
                                        )
                                )
                                Spacer(Modifier.width(5.dp))
                                IconButton(
                                    onClick = {
                                        component.onOutput(
                                            MentoringComponent.Output.CreateSecondView(
                                                login = s.login,
                                                fio = s.fio,
                                                avatarId = s.avatarId,
                                                config = Config.MainHome
                                            )
                                        )
                                        component.onEvent(MentoringStore.Intent.SelectStudent(s.login))
                                    },
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(
                                        Icons.Rounded.Home, null
                                    )
                                }
//
//                                IconButton(
//                                    onClick = {
//                                        component.onOutput(
//                                            MentoringComponent.Output.CreateSecondView(
//                                                login = s.login,
//                                                fio = s.fio,
//                                                avatarId = s.avatarId,
//                                                config = Config.HomeStudentLines(
//                                                    login = s.login
//                                                )
//                                            )
//                                        )
//                                        component.onEvent(MentoringStore.Intent.SelectStudent(s.login))
//                                    },
//                                    modifier = Modifier.size(30.dp)
//                                ) {
//                                    Icon(
//                                        Icons.Rounded.ManageSearch, null
//                                    )
//                                }
//
//                                IconButton(
//                                    onClick = {
//                                        component.onOutput(
//                                            MentoringComponent.Output.CreateSecondView(
//                                                login = s.login,
//                                                fio = s.fio,
//                                                avatarId = s.avatarId,
//                                                config = Config.HomeDnevnikRuMarks(
//                                                    studentLogin = s.login
//                                                )
//                                            )
//                                        )
//                                        component.onEvent(MentoringStore.Intent.SelectStudent(s.login))
//                                    },
//                                    modifier = Modifier.size(30.dp)
//                                ) {
//                                    Icon(
//                                        Icons.Outlined.PlaylistAddCheckCircle, null
//                                    )
//                                }
//
//
//                                IconButton(
//                                    onClick = {
//                                        component.onOutput(
//                                            MentoringComponent.Output.CreateSecondView(
//                                                login = s.login,
//                                                fio = s.fio,
//                                                avatarId = s.avatarId,
//                                                config = Config.HomeTasks(
//                                                    studentLogin = s.login,
//                                                    avatarId = s.avatarId,
//                                                    name = s.fio.name
//                                                )
//                                            )
//                                        )
//                                        component.onEvent(MentoringStore.Intent.SelectStudent(s.login))
//                                    },
//                                    modifier = Modifier.size(30.dp)
//                                ) {
//                                    Icon(
//                                        Icons.Rounded.HistoryEdu, null
//                                    )
//                                }
//                                IconButton(
//                                    onClick = {
//                                        component.onOutput(
//                                            MentoringComponent.Output.CreateSecondView(
//                                                login = s.login,
//                                                fio = s.fio,
//                                                avatarId = s.avatarId,
//                                                config = Config.HomeAchievements(
//                                                    studentLogin = s.login,
//                                                    name = s.fio.name,
//                                                    avatarId = s.avatarId
//                                                )
//                                            )
//                                        )
//                                        component.onEvent(MentoringStore.Intent.SelectStudent(s.login))
//                                    },
//                                    modifier = Modifier.size(30.dp)
//                                ) {
//                                    Icon(
//                                        Icons.Rounded.EmojiEvents, null
//                                    )
//                                }
                            }
                        }


                        AnimatedVisibility(
                            model.chosenAttendanceLogin == s.login,
                            modifier = Modifier.fillMaxWidth().padding(end = 4.dp)
                                .animateContentSize()
                        ) {
                            Column {
                                Spacer(Modifier.height(5.dp))
//                        Box(
//                            Modifier.height(2.dp).fillMaxWidth(.93f)
//                                .align(Alignment.CenterHorizontally)
//                                .clip(RoundedCornerShape(15.dp))
//                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = .5f))
//                        )
//                        Spacer(Modifier.height(5.dp))
                                DatesLine(
                                    dates = model.dates,
                                    currentDate = model.currentDate,
                                    onClick = {
                                        component.onEvent(MentoringStore.Intent.ChangeDate(it))
                                        component.onEvent(
                                            MentoringStore.Intent.SelectPreAttendanceLogin(
                                                login = s.login,
                                                date = it.second
                                            )
                                        )
                                    }
                                )
                                Crossfade(nPAModel.state, modifier = Modifier.fillMaxWidth()) { n ->
                                    when (n) {
                                        NetworkState.Error -> {
                                            Text("error")
                                        }

                                        NetworkState.Loading -> {
                                            Box(
                                                Modifier.fillMaxWidth().height(50.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                CircularProgressIndicator(Modifier.size(20.dp))
                                            }
                                        }

                                        NetworkState.None -> {
                                            val schedule =
                                                model.schedule[s.login]?.get(model.currentDate.second)
                                            val preAttendance =
                                                model.preAttendance[s.login]?.get(model.currentDate.second)

                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    Modifier.fillMaxWidth(.5f),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    if (!schedule.isNullOrEmpty()) {
                                                        Column {
                                                            schedule.forEach {
                                                                Text("${it.subjectName} (${it.groupName})")
                                                                Text("${it.start}-${it.end}")
                                                            }
                                                        }
                                                    } else {
                                                        Text("Уроков нет")
                                                    }
                                                }
                                                Box(
                                                    Modifier.fillMaxWidth(),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    if (model.cStart != null) {
                                                        Column {
                                                            Row {
                                                                CustomTextField(
                                                                    value = model.cStart ?: "",
                                                                    onValueChange = {
                                                                        if (it.length < 6) {
                                                                            component.onEvent(
                                                                                MentoringStore.Intent.ChangeCStart(
                                                                                    it
                                                                                )
                                                                            )
                                                                        }
                                                                    },
                                                                    text = "Старт",
                                                                    isEnabled = true,
                                                                    isMoveUpLocked = true,
                                                                    autoCorrect = false,
                                                                    keyboardType = KeyboardType.Text,
                                                                    modifier = Modifier.fillMaxWidth(
                                                                        .5f
                                                                    ),
                                                                    supText = "чч:мм"
                                                                )
                                                                Spacer(Modifier.width(5.dp))
                                                                CustomTextField(
                                                                    value = model.cEnd ?: "",
                                                                    onValueChange = {
                                                                        if (it.length < 6) {
                                                                            component.onEvent(
                                                                                MentoringStore.Intent.ChangeCEnd(
                                                                                    it
                                                                                )
                                                                            )
                                                                        }
                                                                    },
                                                                    text = "Кон.",
                                                                    isEnabled = true,
                                                                    isMoveUpLocked = true,
                                                                    autoCorrect = false,
                                                                    keyboardType = KeyboardType.Text,
                                                                    modifier = Modifier.fillMaxWidth(),
                                                                    supText = "чч:мм"
                                                                )
                                                            }
                                                            CustomTextField(
                                                                value = model.cReason ?: "",
                                                                onValueChange = {
                                                                    component.onEvent(
                                                                        MentoringStore.Intent.ChangeCReason(
                                                                            it
                                                                        )
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
                                                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.cClickable {
                                                                component.onEvent(
                                                                    MentoringStore.Intent.ChangeCIsGood(
                                                                        !(model.cIsGood
                                                                            ?: false)
                                                                    )
                                                                )
                                                            }) {
                                                                CustomCheckbox(
                                                                    checked = model.cIsGood
                                                                        ?: false
                                                                )
                                                                Spacer(Modifier.width(5.dp))
                                                                Text("Уважительная")
                                                            }
                                                            Spacer(Modifier.width(3.dp))
                                                            Row {
                                                                IconButton(
                                                                    onClick = {
                                                                        component.onEvent(
                                                                            MentoringStore.Intent.StartEditPreAttendance(
                                                                                null,
                                                                                null,
                                                                                null,
                                                                                null
                                                                            )
                                                                        )
                                                                    }
                                                                ) {
                                                                    Icon(Icons.Rounded.Close, null)
                                                                }

                                                                AnimatedCommonButton(
                                                                    text = "Сохранить",
                                                                    modifier = Modifier.fillMaxWidth(),
                                                                    isEnabled = !model.cStart.isNullOrEmpty() && !model.cEnd.isNullOrEmpty() && !model.cReason.isNullOrEmpty()
                                                                ) {
                                                                    component.onEvent(
                                                                        MentoringStore.Intent.SavePreAttendance(
                                                                            login = s.login,
                                                                            date = model.currentDate.second
                                                                        )
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    } else {
                                                        if (preAttendance == null) {
                                                            CustomTextButton(text = "Добавить дневное\nотсутствие") {
                                                                component.onEvent(
                                                                    MentoringStore.Intent.StartEditPreAttendance(
                                                                        "", "", "", false
                                                                    )
                                                                )
                                                            }
                                                        } else {
                                                            Column(
                                                                modifier = Modifier.fillMaxWidth(),
                                                                horizontalAlignment = Alignment.CenterHorizontally
                                                            ) {
                                                                Text("${preAttendance.start}-${preAttendance.end}")
                                                                Text(preAttendance.reason)
                                                                Text(if (preAttendance.isGood) "Уважительная" else "Н-ая")
                                                                AnimatedElevatedButton(
                                                                    text = "Редактировать",
                                                                    isEnabled = true,
                                                                    modifier = Modifier.fillMaxWidth()
                                                                ) {
                                                                    component.onEvent(
                                                                        MentoringStore.Intent.StartEditPreAttendance(
                                                                            start = preAttendance.start,
                                                                            end = preAttendance.end,
                                                                            reason = preAttendance.reason,
                                                                            cIsGood = preAttendance.isGood
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
                                }
                            }
                        }

                    }
                }
                Spacer(Modifier.height(6.dp))
            }
        }

        Spacer(Modifier.height(10.dp))
    }
}
