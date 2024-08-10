import achievements.AdminAchievementsComponent
import achievements.AdminAchievementsStore
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.AnimatedCommonButton
import components.AppBar
import components.CLazyColumn
import components.CustomTextButton
import components.CustomTextField
import components.networkInterface.NetworkInterface
import components.networkInterface.NetworkState
import decomposeComponents.CBottomSheetContent
import server.getLocalDate
import view.LocalViewManager
import view.rememberImeState

@OptIn(
    ExperimentalLayoutApi::class, ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun AdminAchievementsContent(
    component: AdminAchievementsComponent
) {
    val model by component.model.subscribeAsState()
    val nModel by component.nInterface.networkModel.subscribeAsState()
    val viewManager = LocalViewManager.current
    val scrollState = rememberScrollState()
    val imeState = rememberImeState()
    val lazyListState = rememberLazyListState()
    val density = LocalDensity.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AppBar(
                navigationRow = {
                    IconButton(
                        onClick = { component.onOutput(AdminAchievementsComponent.Output.Back) }
                    ) {
                        Icon(
                            Icons.Rounded.ArrowBackIosNew, null
                        )
                    }
                },
                title = {
                    Text(
                        "Достижения",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                actionRow = {

                    IconButton(
                        onClick = { component.onEvent(AdminAchievementsStore.Intent.Init) }
                    ) {
                        Icon(
                            Icons.Rounded.Refresh, null
                        )
                    }
                    IconButton(
                        onClick = { component.onEvent(AdminAchievementsStore.Intent.OpenCreateBS) }
                    ) {
                        Icon(
                            Icons.Rounded.Add, null
                        )
                    }
                },
                isHaze = true
            )
        }
    ) { padding ->
        Crossfade(nModel.state, modifier = Modifier.fillMaxSize()) { state ->
            when (state) {
                NetworkState.None -> CLazyColumn(padding = padding) {
                    val headers = model.achievements.sortedBy { it.id }.reversed().map {
                        Header(
                            text = it.text,
                            date = it.date,
                            showDate = it.showDate
                        )
                    }.toSet().toList()//.sortedBy { getLocalDate(it.date).toEpochDays() }
                    items(items = headers) { h ->
                        val achievements =
                            model.achievements.filter { it.text == h.text && it.date == h.date && it.showDate == h.showDate }
                        Column(modifier = Modifier.padding(bottom = 7.dp)) {
                            CustomTextButton(
                                text = buildAnnotatedString {
                                    withStyle(
                                        SpanStyle(
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                    ) {
                                        append(h.text + " ")
                                    }
                                    withStyle(
                                        SpanStyle(
                                            fontWeight = FontWeight.Black,
                                            color = MaterialTheme.colorScheme.onBackground.copy(
                                                alpha = .5f
                                            )
                                        )
                                    ) {
                                        append(h.date)
                                    }
                                },
                                color = MaterialTheme.colorScheme.onBackground
                            ) {
                                component.onEvent(
                                    AdminAchievementsStore.Intent.OpenHugeBS(
                                        text = h.text,
                                        date = h.date,
                                        showDate = h.showDate ?: "",
                                        oldText = h.text,
                                        oldDate = h.date,
                                        oldShowDate = h.showDate ?: "",
                                    )
                                )
                            }
                            achievements.sortedBy { it.id }.reversed().forEach { a ->
                                val fio =
                                    model.students.firstOrNull { it.login == a.studentLogin }?.fio
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.horizontalScroll(
                                    rememberScrollState()).clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                                    component.onEvent(AdminAchievementsStore.Intent.OpenEditBS(
                                        id = a.id,
                                        studentLogin = a.studentLogin,
                                        subjectId = a.subjectId,
                                        stups = a.stups,
                                        text = a.text,
                                        date = a.date
                                    ))
                                }) {
                                    Text(text = a.id.toString(), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground.copy(alpha = .3f))
                                    Spacer(Modifier.width(5.dp))
                                    Text(
                                        "${fio?.surname} ${fio?.name} ${fio?.praname?.get(0)}.",
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(Modifier.width(5.dp))
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
                                    Text("${model.subjects[a.subjectId]}")
                                    Spacer(Modifier.width(5.dp))
                                    if (a.stups != 0) {
                                        Text(
                                            "+${a.stups}",
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                }
                                Spacer(Modifier.height(2.dp))
                            }
                            Spacer(Modifier.height(1.dp))
                            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                IconButton(
                                    onClick = {
                                        val a = achievements.first { it.text == h.text }
                                        component.onEvent(
                                            AdminAchievementsStore.Intent.OpenAddBS(
                                                date = h.date,
                                                showDate = h.showDate ?: "",
                                                text = h.text,
                                                subjectId = a.subjectId,
                                                stups = a.stups
                                            )
                                        )
                                    },
                                    modifier = Modifier.size(width = 40.dp, height = 20.dp)
                                ) {
                                    Box(
                                        Modifier.fillMaxSize().background(
                                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = .5f)
                                        ), contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Rounded.Add, null
                                        )
                                    }
                                }
                            }
                        }
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

        CBottomSheetContent(
            component = component.bottomSheetComponent
        ) {
            BottomSheetContent(
                model = model,
                component = component,
                nBSInterface = component.nBSInterface
            )
        }
        CBottomSheetContent(
            component = component.hugeBottomSheetComponent
        ) {
            HugeBottomSheetContent(
                model = model,
                component = component,
                nBSInterface = component.nBSInterface
            )
        }
        CBottomSheetContent(
            component = component.editBottomSheetComponent
        ) {
            EditBottomSheetContent(
                model = model,
                component = component,
                nBSInterface = component.nBSInterface
            )
        }


    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun BottomSheetContent(
    model: AdminAchievementsStore.State,
    component: AdminAchievementsComponent,
    nBSInterface: NetworkInterface
) {
    val isAllowed =
        model.bsStudentLogin !in model.achievements.filter { it.text == model.bsText && it.date == model.bsDate && it.subjectId == model.bsSubjectId }
            .map { it.studentLogin }
    val nBSModel by nBSInterface.networkModel.subscribeAsState()

    var expandedStudents by remember { mutableStateOf(false) }
    var expandedSubjects by remember { mutableStateOf(false) }

    Column(Modifier.padding(horizontal = 10.dp).padding(bottom = 10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            val studentsMap =
                model.students.associate { it.login to "${it.fio.surname} ${it.fio.name.first()}. ${(it.fio.praname ?: "").first()}." }

            ExposedDropdownMenuBox(
                expanded = expandedStudents,
                onExpandedChange = {
                    expandedStudents = !expandedStudents
                }
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(.6f), // menuAnchor modifier must be passed to the text field for correctness.
                    readOnly = true,
                    value = studentsMap[model.bsStudentLogin] ?: "",
                    placeholder = { Text("Выберите") },
                    onValueChange = {},
                    label = { Text("Ученик") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expandedStudents
                        )
                    },
                    shape = RoundedCornerShape(15.dp),
                    enabled = nBSModel.state != NetworkState.Loading,
                    maxLines = 1
                )
                // menu

                ExposedDropdownMenu(
                    expanded = expandedStudents,
                    onDismissRequest = {
                        expandedStudents = false
                    },
                ) {
                    // menu items
                    studentsMap.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption.value) },
                            onClick = {
                                component.onEvent(
                                    AdminAchievementsStore.Intent.ChangeStudentLogin(
                                        login = selectionOption.key
                                    )
                                )
                                expandedStudents = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }
            }
            Spacer(Modifier.width(5.dp))
            CustomTextField(
                value = model.bsDate,
                onValueChange = {
                    component.onEvent(AdminAchievementsStore.Intent.ChangeDate(it))
                },
                text = "Дата",
                isEnabled = nBSModel.state == NetworkState.None,
                isMoveUpLocked = true,
                autoCorrect = false,
                keyboardType = KeyboardType.Number,
                modifier = Modifier.fillMaxWidth(),
                supText = "дд.мм.гггг"
            )
        }
        CustomTextField(
            value = model.bsText,
            onValueChange = {
                component.onEvent(AdminAchievementsStore.Intent.ChangeText(it))
            },
            text = "Достижение",
            isEnabled = nBSModel.state == NetworkState.None,
            isMoveUpLocked = true,
            autoCorrect = true,
            keyboardType = KeyboardType.Text,
            modifier = Modifier.fillMaxWidth(),
            isSingleLine = false,
            supText = "Например: ВСОШ Математика [Региональный этап]"
        )
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            ExposedDropdownMenuBox(
                expanded = expandedSubjects,
                onExpandedChange = {
                    expandedSubjects = !expandedSubjects
                }
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(.7f), // menuAnchor modifier must be passed to the text field for correctness.
                    readOnly = true,
                    value = model.subjects[model.bsSubjectId] ?: "",
                    placeholder = { Text("Выберите") },
                    onValueChange = {},
                    label = { Text("Предмет") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expandedSubjects
                        )
                    },
                    shape = RoundedCornerShape(15.dp),
                    enabled = nBSModel.state == NetworkState.None,
                    maxLines = 1
                )
                // menu

                ExposedDropdownMenu(
                    expanded = expandedSubjects,
                    onDismissRequest = {
                        expandedStudents = false
                    },
                ) {
                    // menu items
                    model.subjects.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption.value) },
                            onClick = {
                                component.onEvent(
                                    AdminAchievementsStore.Intent.ChangeSubjectId(
                                        selectionOption.key
                                    )
                                )
                                expandedSubjects = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }
            }
            Spacer(Modifier.width(5.dp))
            CustomTextField(
                value = if (model.bsStups != 0) model.bsStups.toString() else "",
                onValueChange = {
                    component.onEvent(AdminAchievementsStore.Intent.ChangeStups(if (it.isNotEmpty()) it.toInt() else 0))
                },
                text = "Награда",
                isEnabled = nBSModel.state == NetworkState.None,
                isMoveUpLocked = true,
                autoCorrect = false,
                keyboardType = KeyboardType.Number,
                modifier = Modifier.fillMaxWidth(),
                supText = "Ступени"
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            CustomTextField(
                value = model.bsShowDate,
                onValueChange = {
                    component.onEvent(AdminAchievementsStore.Intent.ChangeShowDate(it))
                },
                text = "Дата показа",
                isEnabled = nBSModel.state == NetworkState.None,
                isMoveUpLocked = true,
                autoCorrect = false,
                keyboardType = KeyboardType.Number,
                modifier = Modifier.fillMaxWidth(.5f),
                supText = "Можно пустым"
            )
            Spacer(Modifier.width(5.dp))
//            if (model.bsId != null) {
//                IconButton(
//                    onClick = {}
//                ) {
//                    Icon(
//                        Icons.Rounded.Close, null
//                    )
//                }
//            }
            AnimatedCommonButton(
                text =
                if (nBSModel.state == NetworkState.Error) {
                    nBSModel.error
                } else {
                    if (isAllowed) {
                        if (model.bsId == null) "Создать" else "Редактировать"
                    } else {
                        "Уже существует"
                    }
                },
                modifier = Modifier.padding(top = 6.dp).fillMaxWidth()
                    .height(TextFieldDefaults.MinHeight),
                isEnabled =
                isAllowed && nBSModel.state != NetworkState.Loading && model.bsText.isNotEmpty() && model.bsDate.isNotEmpty() && model.bsStudentLogin.isNotEmpty() && model.bsSubjectId != null,
                shape = RoundedCornerShape(15.dp)
            ) {
                if (nBSModel.state == NetworkState.Error) {
                    nBSModel.onFixErrorClick()
                } else {
                    component.onEvent(AdminAchievementsStore.Intent.CreateAchievement)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun HugeBottomSheetContent(
    model: AdminAchievementsStore.State,
    component: AdminAchievementsComponent,
    nBSInterface: NetworkInterface
) {
    val isAllowed = model.achievements.filter { it.text == model.bsText && it.date == model.bsDate && it.showDate == model.bsShowDate }.isEmpty()
    println()
    val nBSModel by nBSInterface.networkModel.subscribeAsState()

    Column(Modifier.padding(horizontal = 10.dp).padding(bottom = 10.dp)) {
        CustomTextField(
            value = model.bsText,
            onValueChange = {
                component.onEvent(AdminAchievementsStore.Intent.ChangeText(it))
            },
            text = "Достижение",
            isEnabled = nBSModel.state == NetworkState.None,
            isMoveUpLocked = true,
            autoCorrect = true,
            keyboardType = KeyboardType.Text,
            modifier = Modifier.fillMaxWidth(),
            isSingleLine = false,
            supText = "Например: ВСОШ Математика [Региональный этап]"
        )
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            CustomTextField(
                value = model.bsDate,
                onValueChange = {
                    component.onEvent(AdminAchievementsStore.Intent.ChangeDate(it))
                },
                text = "Дата",
                isEnabled = nBSModel.state == NetworkState.None,
                isMoveUpLocked = true,
                autoCorrect = false,
                keyboardType = KeyboardType.Number,
                modifier = Modifier.fillMaxWidth(.5f),
                supText = "дд.мм.гггг"
            )
            Spacer(Modifier.width(5.dp))
            CustomTextField(
                value = model.bsShowDate,
                onValueChange = {
                    component.onEvent(AdminAchievementsStore.Intent.ChangeShowDate(it))
                },
                text = "Дата показа",
                isEnabled = nBSModel.state == NetworkState.None,
                isMoveUpLocked = true,
                autoCorrect = false,
                keyboardType = KeyboardType.Number,
                modifier = Modifier.fillMaxWidth(),
                supText = "Можно пустым"
            )
        }
        AnimatedCommonButton(
            text = if (isAllowed) "Редактировать" else "Уже существует",
            modifier = Modifier.padding(top = 6.dp).fillMaxWidth()
                .height(TextFieldDefaults.MinHeight),
            isEnabled = isAllowed && nBSModel.state != NetworkState.Loading && model.bsText.isNotEmpty() && model.bsDate.isNotEmpty(),
            shape = RoundedCornerShape(15.dp)
        ) {
            if (nBSModel.state == NetworkState.Error) {
                nBSModel.onFixErrorClick()
            } else {
                component.onEvent(AdminAchievementsStore.Intent.UpdateGroupAchievement)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun EditBottomSheetContent(
    model: AdminAchievementsStore.State,
    component: AdminAchievementsComponent,
    nBSInterface: NetworkInterface
) {
    val isAllowed = model.achievements.filter { it.text == model.bsText && it.date == model.bsDate && it.studentLogin == model.bsStudentLogin && it.subjectId == model.bsSubjectId && it.stups == model.bsStups }.isEmpty()
    val nBSModel by nBSInterface.networkModel.subscribeAsState()

    var expandedStudents by remember { mutableStateOf(false) }
    var expandedSubjects by remember { mutableStateOf(false) }


    Column(Modifier.padding(horizontal = 10.dp).padding(bottom = 10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            ExposedDropdownMenuBox(
                expanded = expandedSubjects,
                onExpandedChange = {
                    expandedSubjects = !expandedSubjects
                }
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(.7f), // menuAnchor modifier must be passed to the text field for correctness.
                    readOnly = true,
                    value = model.subjects[model.bsSubjectId] ?: "",
                    placeholder = { Text("Выберите") },
                    onValueChange = {},
                    label = { Text("Предмет") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expandedSubjects
                        )
                    },
                    shape = RoundedCornerShape(15.dp),
                    enabled = nBSModel.state == NetworkState.None,
                    maxLines = 1
                )
                // menu

                ExposedDropdownMenu(
                    expanded = expandedSubjects,
                    onDismissRequest = {
                        expandedStudents = false
                    },
                ) {
                    // menu items
                    model.subjects.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption.value) },
                            onClick = {
                                component.onEvent(
                                    AdminAchievementsStore.Intent.ChangeSubjectId(
                                        selectionOption.key
                                    )
                                )
                                expandedSubjects = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }
            }
            Spacer(Modifier.width(5.dp))
            CustomTextField(
                value = if (model.bsStups != 0) model.bsStups.toString() else "",
                onValueChange = {
                    component.onEvent(AdminAchievementsStore.Intent.ChangeStups(if (it.isNotEmpty()) it.toInt() else 0))
                },
                text = "Награда",
                isEnabled = nBSModel.state == NetworkState.None,
                isMoveUpLocked = true,
                autoCorrect = false,
                keyboardType = KeyboardType.Number,
                modifier = Modifier.fillMaxWidth(),
                supText = "Ступени"
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            val studentsMap =
                model.students.associate { it.login to "${it.fio.surname} ${it.fio.name.first()}. ${(it.fio.praname ?: "").first()}." }

            ExposedDropdownMenuBox(
                expanded = expandedStudents,
                onExpandedChange = {
                    expandedStudents = !expandedStudents
                }
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(.6f), // menuAnchor modifier must be passed to the text field for correctness.
                    readOnly = true,
                    value = studentsMap[model.bsStudentLogin] ?: "",
                    placeholder = { Text("Выберите") },
                    onValueChange = {},
                    label = { Text("Ученик") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expandedStudents
                        )
                    },
                    shape = RoundedCornerShape(15.dp),
                    enabled = nBSModel.state == NetworkState.None,
                    maxLines = 1
                )
                // menu

                ExposedDropdownMenu(
                    expanded = expandedStudents,
                    onDismissRequest = {
                        expandedStudents = false
                    },
                ) {
                    // menu items
                    studentsMap.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption.value) },
                            onClick = {
                                component.onEvent(
                                    AdminAchievementsStore.Intent.ChangeStudentLogin(
                                        login = selectionOption.key
                                    )
                                )
                                expandedStudents = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }
            }
            Spacer(Modifier.width(5.dp))
//            if (model.bsId != null) {
//                IconButton(
//                    onClick = {}
//                ) {
//                    Icon(
//                        Icons.Rounded.Close, null
//                    )
//                }
//            }
            AnimatedCommonButton(
                text =
                if (nBSModel.state == NetworkState.Error) {
                    nBSModel.error
                } else {
                    if (isAllowed) {
                        if (model.bsId == null) "Создать" else "Редактировать"
                    } else {
                        "Уже существует"
                    }
                },
                modifier = Modifier.padding(top = 6.dp).fillMaxWidth()
                    .height(TextFieldDefaults.MinHeight),
                isEnabled =
                isAllowed && nBSModel.state != NetworkState.Loading && model.bsText.isNotEmpty() && model.bsDate.isNotEmpty() && model.bsStudentLogin.isNotEmpty() && model.bsSubjectId != null,
                shape = RoundedCornerShape(15.dp)
            ) {
                if (nBSModel.state == NetworkState.Error) {
                    nBSModel.onFixErrorClick()
                } else {
                    component.onEvent(AdminAchievementsStore.Intent.EditAchievement)
                }
            }
        }
    }
}

//@Serializable
private data class Header(
    val text: String,
    val date: String,
    val showDate: String?
)
