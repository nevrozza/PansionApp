package groups

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.*
import components.cAlertDialog.CAlertDialogStore
import components.cBottomSheet.CBottomSheetStore
import components.networkInterface.NetworkInterface
import components.networkInterface.NetworkState
import decomposeComponents.CAlertDialogContent
import decomposeComponents.CBottomSheetContent
import groups.students.StudentsComponent
import groups.students.StudentsStore
import groups.subjects.SubjectsComponent
import groups.subjects.SubjectsStore
import kotlinx.coroutines.CoroutineScope
import resources.RIcons
import view.LocalViewManager

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@ExperimentalFoundationApi
@Composable
fun SubjectsContent(
    component: SubjectsComponent,
    sComponent: StudentsComponent,
    coroutineScope: CoroutineScope,
    topPadding: Dp,
//    hazeState: HazeState
) {
    val gModel by component.groupModel.subscribeAsState()
    val model by component.model.subscribeAsState()
    val nSModel by component.nSubjectsInterface.networkModel.subscribeAsState()
    val viewManager = LocalViewManager.current

    Box() {
//    Spacer(Modifier.height(10.dp))
        Crossfade(nSModel.state) {
            when {
                it == NetworkState.Loading && model.groups.isEmpty() -> {
                    Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        LoadingAnimation()
                    }
                }

                it == NetworkState.Error -> {
                    DefaultGroupsErrorScreen(
                        component.nSubjectsInterface
                    )
                }

                else -> {
                    if (model.groups.isNotEmpty()) {
                        Spacer(Modifier.height(7.dp))
                        CLazyColumn(padding = PaddingValues(top = topPadding)) {
                            items(model.groups.sortedByDescending { it.isActive }) { group ->
                                if (model.groups.any { !it.isActive }) {
                                    Box(
                                        Modifier.fillMaxWidth().padding(5.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (group == model.groups.firstOrNull { it.isActive }) {
                                            Text("Активные")
                                        } else if (group == model.groups.firstOrNull { !it.isActive }) {
                                            Text("Удалённые")
                                        }
                                    }
                                }
                                val mentor =
                                    gModel.teachers.find { it.login == group.group.teacherLogin }
                                val mentorName =
                                    try {
                                        "${mentor!!.fio.surname} ${mentor.fio.name.first()}. ${(mentor.fio.praname ?: " ").first()}."
                                    } catch (_: Throwable) {
                                        ""
                                    }
                                Column {
                                    ElevatedCard(
                                        modifier = Modifier.heightIn(TextFieldDefaults.MinHeight)
                                            .fillMaxWidth()//.padding(horizontal = 10.dp)
                                            .padding(bottom = 5.dp),
                                        onClick = {
                                            component.onEvent(
                                                SubjectsStore.Intent.FetchStudents(
                                                    group.id,
                                                    false
                                                )
                                            )
                                        }
                                    ) {
                                        Row(
                                            Modifier.fillMaxWidth().padding(horizontal = 10.dp)
                                                .padding(bottom = 10.dp, top = 5.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column(Modifier.weight(1f, false)) {
                                                Text(
                                                    group.group.name,
                                                    modifier = Modifier
                                                        .padding(start = 5.dp),
                                                    fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                                Row(verticalAlignment = Alignment.CenterVertically) {

                                                    Spacer(Modifier.width(4.dp))
                                                    GetAsyncIcon(RIcons.User, size = 18.dp)
                                                    Spacer(Modifier.width(4.dp))
                                                    Text(text = mentorName)
                                                    Spacer(Modifier.width(6.dp))
                                                    GetAsyncIcon(
                                                        path = RIcons.Fire,
                                                        size = 18.dp
                                                    )
                                                    Spacer(Modifier.width(4.dp))
                                                    Text(group.group.difficult)
                                                }
                                            }
                                            IconButton(
                                                onClick = {
                                                    component.onEvent(
                                                        SubjectsStore.Intent.GroupEditInit(
                                                            group.id
                                                        )
                                                    )
                                                    component.onEvent(
                                                        SubjectsStore.Intent.ChangeEName(
                                                            group.group.name
                                                        )
                                                    )
                                                    component.onEvent(
                                                        SubjectsStore.Intent.ChangeETeacherLogin(
                                                            group.group.teacherLogin
                                                        )
                                                    )
                                                    component.onEvent(
                                                        SubjectsStore.Intent.ChangeEDifficult(
                                                            group.group.difficult
                                                        )
                                                    )
                                                    component.eGroupBottomSheet.onEvent(
                                                        CBottomSheetStore.Intent.ShowSheet
                                                    )
                                                },
                                                modifier = Modifier.weight(0.1f, false)
                                            ) {
                                                GetAsyncIcon(
                                                    path = RIcons.Edit
                                                )
                                            }
                                        }
                                    }
                                    FlowRow(
                                        Modifier.animateContentSize().fillMaxWidth()
                                            .padding(horizontal = 5.dp).padding(bottom = 5.dp),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        if (model.currentGroup == group.id) {
                                            val list = model.students[group.id]
                                            list?.forEach { student ->
                                                val isGoingToDelete = remember { mutableStateOf(false) }
                                                Text("${student.fio.surname} ${student.fio.name}${if (list.last() != student) ", " else ""}", modifier = Modifier.cClickable {
                                                    isGoingToDelete.value = !isGoingToDelete.value
                                                })
                                                if (isGoingToDelete.value) {
                                                    IconButton(
                                                        onClick = {
                                                            sComponent.onEvent(StudentsStore.Intent.DeleteStudentGroup(
                                                                login = student.login,
                                                                subjectId = group.group.subjectId,
                                                                groupId = group.id,
                                                                afterAll = { component.onEvent(SubjectsStore.Intent.FetchStudents(group.id, true)) }
                                                            ))
                                                        },
                                                        modifier = Modifier.size(25.dp)
                                                    ) {
                                                        GetAsyncIcon(
                                                            RIcons.Close
                                                        )
                                                    }
                                                }
                                            }
                                            val isWannaCreate = remember { mutableStateOf(false) }
                                            Column {
                                                if (isWannaCreate.value) {
                                                    CustomTextField(
                                                        value = model.addStudentToGroupLogin,
                                                        onValueChange = {
                                                            component.onEvent(SubjectsStore.Intent.ChangeAddStudentToGroupLogin(it))
//                                                            component.onEvent(UsersStore.Intent.ChangeESurname(it))
                                                        },
                                                        text = "ФИО",
                                                        isEnabled = true,
                                                        onEnterClicked = {
                                                            if (model.addStudentToGroupLogin.isNotBlank() && model.addStudentToGroupLogin !in (model.students[model.currentGroup]?.map { it.fio.surname + " " + it.fio.name + " " + it.fio.praname } ?: listOf())) {
                                                                component.onEvent(SubjectsStore.Intent.AddStudentToGroup)
                                                            } else {
                                                                component.onEvent(SubjectsStore.Intent.ChangeAddStudentToGroupLogin(""))
                                                            }
//                                                            focusManager.moveFocus(FocusDirection.Next)
                                                        },
//                                                        focusManager = focusManager,
                                                        isMoveUpLocked = true,
                                                        autoCorrect = false,
                                                        keyboardType = KeyboardType.Text
                                                    )
                                                } else {
                                                    IconButton(
                                                        onClick = { isWannaCreate.value = true },
                                                        modifier = Modifier.size(25.dp)
                                                    ) {
                                                        GetAsyncIcon(
                                                            RIcons.Add
                                                        )
                                                    }
                                                }
                                            }

                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        Box(
                            Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Здесь пустовато =)")
                        }
                    }
                }
            }
        }
    }

    CAlertDialogContent(
        component = component.editSubjectDialog,
        isCustomButtons = false,
        title = "${gModel.subjects.firstOrNull { it.id == model.eSubjectId }?.name}",
        acceptText = "Сохранить"
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            CustomTextField(
                value = model.eSubjectText,
                onValueChange = {
                    component.onEvent(
                        SubjectsStore.Intent.ChangeESubjectText(
                            it
                        )
                    )
                },
                text = "Название урока",
                isEnabled =
                component.nSubjectsInterface.networkModel.value.state != NetworkState.Loading,
                onEnterClicked = {
                    component.onEvent(
                        SubjectsStore.Intent.EditSubject(
                            sameCount = gModel.subjects.filter { it.name == model.eSubjectText }.size
                        )
                    )
                },
                isMoveUpLocked = true,
                autoCorrect = true,
                keyboardType = KeyboardType.Text
            )
            Spacer(Modifier.height(7.dp))
            CustomTextButton("Удалить") {
                component.deleteSubjectDialog.onEvent(CAlertDialogStore.Intent.ShowDialog)
            }
        }
    }

    CAlertDialogContent(
        component = component.deleteSubjectDialog,
        isCustomButtons = false,
        title = "Удалить урок?"
    ) {}




    CBottomSheetContent(
        component = component.eGroupBottomSheet
    ) {
        var deleteGroup by remember { mutableStateOf(false) }
        val isActive = model.groups.firstOrNull { it.id == model.eGroupId }?.isActive == true
        val focusManager = LocalFocusManager.current
        var num = 0
        Column(
            Modifier.padding(top = 5.dp, bottom = 10.dp)
                .padding(horizontal = 10.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var expandedTeachers by remember { mutableStateOf(false) }
            val properties = listOf(
                model.eName,
                model.eTeacherLogin,
                model.eDifficult
            )
            num = properties.count { it.isNotBlank() }
//                        if (model.cBirthday.length == 8) num++
            Text(
                buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = MaterialTheme.typography.titleLarge.fontSize
                        )
                    ) {
                        append("${model.groups.first { it.id == model.eGroupId }.group.name} ")
                    }
                    withStyle(
                        SpanStyle(
                            fontSize = MaterialTheme.typography.titleSmall.fontSize,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    ) {
                        append("$num/${properties.size}")
                    }
                }
            )
            Spacer(Modifier.height(5.dp))
            Column(
                Modifier.imePadding()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {


                Spacer(Modifier.height(7.dp))
                CustomTextField(
                    value = model.eName,
                    onValueChange = {
                        component.onEvent(
                            SubjectsStore.Intent.ChangeEName(
                                it
                            )
                        )
                    },
                    text = "Название группы",
                    isEnabled = component.nSubjectsInterface.networkModel.value.state != NetworkState.Loading,
                    onEnterClicked = {
                        focusManager.moveFocus(FocusDirection.Next)
                        expandedTeachers = true
                    },
                    focusManager = focusManager,
                    isMoveUpLocked = true,
                    autoCorrect = true,
                    keyboardType = KeyboardType.Text,
                    supText = "10 кл Профиль"
                )
                Spacer(Modifier.height(7.dp))

                val teachersMap =
                    gModel.teachers.associate { it.login to "${it.fio.surname} ${it.fio.name.first()}. ${(it.fio.praname ?: " ").first()}." }

                ExposedDropdownMenuBox(
                    expanded = expandedTeachers,
                    onExpandedChange = {
                        expandedTeachers = !expandedTeachers
                    }
                ) {
                    // textfield
                    val mentor =
                        gModel.teachers.find { it.login == model.eTeacherLogin }
                    val mentorName =
                        try {
                            "${mentor!!.fio.surname} ${mentor.fio.name.first()}. ${(mentor.fio.praname ?: " ").first()}."
                        } catch (_: Throwable) {
                            ""
                        }
                    OutlinedTextField(
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable), // menuAnchor modifier must be passed to the text field for correctness.
                        readOnly = true,
                        value = mentorName,
                        placeholder = { Text("Выберите") },
                        onValueChange = {},
                        label = { Text("Учитель") },
                        trailingIcon = {
                            val chevronRotation = animateFloatAsState(if (expandedTeachers) 90f else -90f)
                            GetAsyncIcon(
                                path = RIcons.ChevronLeft,
                                modifier = Modifier.padding(end = 10.dp).rotate(chevronRotation.value),
                                size = 15.dp
                            )
                        },
                        shape = RoundedCornerShape(15.dp),
                        enabled = component.nSubjectsInterface.networkModel.value.state != NetworkState.Loading
                    )
                    // menu

                    ExposedDropdownMenu(
                        expanded = expandedTeachers,
                        onDismissRequest = {
                            expandedTeachers = false
                        },
                    ) {
                        // menu items
                        teachersMap.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption.value) },
                                onClick = {
                                    component.onEvent(
                                        SubjectsStore.Intent.ChangeETeacherLogin(
                                            selectionOption.key
                                        )
                                    )
                                    expandedTeachers = false
                                    focusManager.moveFocus(FocusDirection.Next)
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                            )
                        }
                    }
                }
                Spacer(Modifier.height(7.dp))
                CustomTextField(
                    value = model.eDifficult,
                    onValueChange = {
                        if (it.length < 2) {
                            component.onEvent(
                                SubjectsStore.Intent.ChangeEDifficult(
                                    it
                                )
                            )
                        }
                    },
                    text = "Уровень сложности",
                    isEnabled = component.nSubjectsInterface.networkModel.value.state != NetworkState.Loading,
                    onEnterClicked = {
                        if (num == properties.size) {
                            component.onEvent(SubjectsStore.Intent.EditGroup)
                        }
                    },
                    focusManager = focusManager,
                    isMoveUpLocked = false,
                    autoCorrect = false,
                    keyboardType = KeyboardType.Number,
                    supText = "Цифра [0-9]"
                )
                Spacer(Modifier.height(7.dp))
                if (isActive) {
                    AnimatedVisibility(!deleteGroup) {
                        CustomTextButton("Удалить группу") {
                            deleteGroup = true
                        }
                    }
                    AnimatedVisibility(deleteGroup) {
                        Row() {
                            CustomTextButton("Удалить") {
                                component.onEvent(SubjectsStore.Intent.DeleteGroup)
                            }
                            Spacer(Modifier.width(40.dp))
                            CustomTextButton("Отмена") {
                                deleteGroup = false
                            }
                        }
                    }

                    Spacer(Modifier.height(7.dp))
                }
                AnimatedCommonButton(
                    text = if (isActive) "Редактировать" else "Восстановить",
                    modifier = Modifier.width(TextFieldDefaults.MinWidth),
                    isEnabled = num == properties.size
                ) {
                    if (num == properties.size) {
                        component.onEvent(SubjectsStore.Intent.EditGroup)
                    }
                }
                Spacer(Modifier.height(10.dp))
            }
        }
    }
}


@Composable
fun DefaultGroupsErrorScreen(
//    isFabShowing: MutableState<Boolean>,
    nInterface: NetworkInterface
) {
    DefaultErrorView(nInterface.networkModel.value, DefaultErrorViewPos.CenteredFull)
}