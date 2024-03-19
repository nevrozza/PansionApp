package groups

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalFocusManager
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
import components.CustomTextButton
import components.CustomTextField
import components.LoadingAnimation
import components.cAlertDialog.CAlertDialogStore
import components.networkInterface.NetworkState
import components.cBottomSheet.CBottomSheetStore
import decomposeComponents.CAlertDialogContent
import decomposeComponents.CBottomSheetContent
import decomposeComponents.ListDialogContent
import groups.forms.FormsStore
import groups.subjects.SubjectsStore
import view.LocalViewManager
import view.rememberImeState

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class,
    ExperimentalFoundationApi::class,
)
@ExperimentalLayoutApi
@Composable
fun GroupsContent(
    component: GroupsComponent
) {

    val model by component.model.subscribeAsState()
    val nModel by component.nGroupsInterface.networkModel.subscribeAsState()
    val subjectsModel by component.subjectsComponent.model.subscribeAsState()
    val formsModel by component.formsComponent.model.subscribeAsState()
    val studentsModel by component.studentsComponent.model.subscribeAsState()

    val viewManager = LocalViewManager.current
//    val scrollState = rememberScrollState()
    val imeState = rememberImeState()
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val isInited =
        model.forms.isNotEmpty() || model.teachers.isNotEmpty() || model.subjects.isNotEmpty()

    val isButtonEnabled =
        (subjectsModel.cSubjectText.isNotBlank() && subjectsModel.cSubjectText !in model.subjects.map { it.name })
//    val refreshState = rememberPullRefreshState(
//        model.isInProcess && model.teachers != null,
//        { component.onEvent(UsersStore.Intent.FetchUsers) })
    val isFabShowing = rememberSaveable { mutableStateOf(false) }

    Scaffold(

        modifier = Modifier.fillMaxSize().onKeyEvent {
            if (it.key == Key.F5 && it.type == KeyEventType.KeyDown) {
//                component.onEvent(UsersStore.Intent.FetchUsers)
            }
            false
        },
        topBar = {
            AppBar(
                navigationRow = {
                    IconButton(
                        onClick = { component.onOutput(GroupsComponent.Output.BackToAdmin) }
                    ) {
                        Icon(
                            Icons.Rounded.ArrowBackIosNew, null
                        )
                    }
                },
                title = {
                    Text(
                        "Группы",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                actionRow = {
                    if (isInited) {
                        CustomTextButton(
                            text = when (model.view) {
                                GroupsStore.Views.Subjects -> "Предметы"
                                GroupsStore.Views.Forms -> "Классы"
                                GroupsStore.Views.Students -> "Ученики"
                            },
                            modifier = Modifier.padding(end = 7.dp)
                        ) {
                            component.onEvent(GroupsStore.Intent.ChangeView)
                        }
                    }
                }
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            AnimatedVisibility(
                visible = isFabShowing.value,
                enter = slideInVertically(initialOffsetY = { it * 2 }),
                exit = slideOutVertically(targetOffsetY = { it * 2 }),
            ) {
                ExtendedFloatingActionButton(
                    text = {
                        AnimatedContent(
                            if (model.view == GroupsStore.Views.Subjects) "Создать группу" else "Создать класс"
                        ) {
                            Text(it)
                        }
                    },
                    icon = {
                        Icon(
                            Icons.Rounded.Add,
                            null
                        )
                    },
                    onClick = {
                        if (model.view == GroupsStore.Views.Subjects) {
//                            component.onEvent(GroupsStore.Intent.ChangeCreatingSheetShowing(true))
                            component.subjectsComponent.cGroupBottomSheet.onEvent(CBottomSheetStore.Intent.ShowSheet)
                        } else if (model.view == GroupsStore.Views.Forms) {
                            component.formsComponent.creatingFormBottomSheet.onEvent(
                                CBottomSheetStore.Intent.ShowSheet
                            )
                        }
                    },
                    shape = MaterialTheme.shapes.large
                )
            }
        }

    )
    { padding ->

        Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            if (nModel.state == NetworkState.Error && !isInited) {
                DefaultGroupsErrorScreen(
                    isFabShowing,
                    component.nGroupsInterface
                )
            } else if (isInited) {
                Crossfade(model.view) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        when (it) {
                            GroupsStore.Views.Subjects -> {
                                if (subjectsModel.chosenSubjectId == 0) {
                                    if (model.subjects.isNotEmpty()) {
                                        component.subjectsComponent.onEvent(
                                            SubjectsStore.Intent.ClickOnSubject(
                                                model.subjects.last().id
                                            )
                                        )
                                    } else {
                                        component.subjectsComponent.cSubjectDialog.onEvent(
                                            CAlertDialogStore.Intent.ShowDialog
                                        )
                                    }
                                }
                                SubjectsContent(
                                    component = component.subjectsComponent,
                                    coroutineScope = coroutineScope,
                                    isFabShowing = isFabShowing
                                )
                            }

                            GroupsStore.Views.Forms -> FormsContent(
                                component = component.formsComponent,
                                isFabShowing = isFabShowing,
                                padding = padding
                            )

                            GroupsStore.Views.Students -> StudentsContent(
                                component = component.studentsComponent,
                                coroutineScope = coroutineScope,
                                isFabShowing = isFabShowing
                            )
                        }
                    }
                }
                //                      PullRefreshIndicator(
////                modifier = Modifier.align(alignment = Alignment.TopCenter),
////                refreshing = model.isInProcess && model.teachers != null,
////                state = refreshState,
////            )
            } else {
                LoadingAnimation()
            }

        }


        //Create Subject
        CAlertDialogContent(
            component = component.subjectsComponent.cSubjectDialog,
            //customIf = component.subjectsComponent.cSubjectDialog.model.value.isDialogShowing || (model.subjects.isEmpty() && isInited), //TODO
            isCustomButtons = true
        ) {

            val nexted = remember { mutableStateOf(false) }
            val focusManager = LocalFocusManager.current

            Column(Modifier.padding(6.dp)) {
                Text(
                    "Создать урок", fontWeight = FontWeight.Bold,
                    fontSize = 20.sp, modifier = Modifier.padding(start = 5.dp)
                )
                Spacer(Modifier.height(5.dp))
                CustomTextField(
                    value = subjectsModel.cSubjectText,
                    onValueChange = {
//                        component.onEvent(
//                            GroupsStore.Intent.ChangeCreateGSubjectText(
//                                it
//                            )
//                        )
                        component.subjectsComponent.onEvent(
                            SubjectsStore.Intent.ChangeCSubjectText(
                                it
                            )
                        )
                        //component.onEvent(UsersStore.Intent.ChangeESurname(it))
                    },
                    modifier = if (!nexted.value) {
                        Modifier.onPlaced {
                            focusManager.moveFocus(FocusDirection.Next)
                        }.onFocusEvent {
                            if (it.isFocused) nexted.value = true
                        }
                    } else Modifier,
                    text = "Название урока",
                    isEnabled =
                    !(component.nSubjectsInterface.networkModel.value.state == NetworkState.Loading),
                    onEnterClicked = {
//                    focusManager.moveFocus(FocusDirection.Next)
                        if (isButtonEnabled) {
//                            component.onEvent(GroupsStore.Intent.CreateGSubject)
                            component.subjectsComponent.onEvent(SubjectsStore.Intent.CreateSubject)
                        }
                    },


//                focusManager = focusManager,
                    isMoveUpLocked = true,
                    autoCorrect = true,
                    keyboardType = KeyboardType.Text
                )

                Spacer(Modifier.height(7.dp))
                AnimatedCommonButton(
                    text = "Создать",
                    isEnabled = isButtonEnabled,
                    modifier = Modifier.width(TextFieldDefaults.MinWidth)
                ) {
                    if (isButtonEnabled) {
                        component.subjectsComponent.onEvent(SubjectsStore.Intent.CreateSubject)
                    }
                }
            }

        }


        CBottomSheetContent(
            component = component.formsComponent.creatingFormBottomSheet,
            customMaxHeight = 0.dp
        ) {
//            println(formsModel)
            val focusManager = LocalFocusManager.current

            var num = 0
            Column(
                Modifier//.padding(top = 5.dp, bottom = 10.dp).padding(horizontal = 10.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                var expandedMentors by remember { mutableStateOf(false) }
                val properties = listOf(
                    formsModel.cFormTitle,
                    formsModel.cFormMentorLogin,
                    formsModel.cFormClassNum
                )
                num = properties.count { (it ?: "").isNotBlank() }
                Text(
                    buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        ) {
                            append("Создать новый класс ")
                        }
                        withStyle(
                            SpanStyle(
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        ) {
                            append("$num/${properties.size}")
                        }
                    }
                )
                Column(
                    Modifier.imePadding()
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(Modifier.height(5.dp))
                    CustomTextField(
                        value = formsModel.cFormClassNum,
                        onValueChange = {
                            if (it.length < 3) {
//                                component.onEvent(GroupsStore.Intent.ChangeCFormNum(it))
                                component.formsComponent.onEvent(
                                    FormsStore.Intent.ChangeCFormClassNum(
                                        it
                                    )
                                )
                            }
                        },
                        onEnterClicked = {
                            focusManager.moveFocus(FocusDirection.Next)
                        },
                        text = "Номер класса",
                        isEnabled = !(component.nFormsInterface.networkModel.value.state == NetworkState.Loading),
                        focusManager = focusManager,
                        isMoveUpLocked = false,
                        autoCorrect = false,
                        keyboardType = KeyboardType.Number
                    )
                    Spacer(Modifier.height(7.dp))
                    CustomTextField(
                        value = formsModel.cFormTitle,
                        onValueChange = {
                            component.formsComponent.onEvent(
                                FormsStore.Intent.ChangeCFormTitle(
                                    it
                                )
                            )
                        },
                        text = "Название направления",
                        isEnabled = !(component.nFormsInterface.networkModel.value.state == NetworkState.Loading),
                        onEnterClicked = {
                            focusManager.moveFocus(FocusDirection.Next)
                        },
                        supText = "Инженерный/А",
                        focusManager = focusManager,
                        isMoveUpLocked = true,
                        autoCorrect = true,
                        keyboardType = KeyboardType.Text
                    )
                    Spacer(Modifier.height(7.dp))


                    CustomTextField(
                        value = formsModel.cFormShortTitle,
                        onValueChange = {
                            component.formsComponent.onEvent(
                                FormsStore.Intent.ChangeCFormShortTitle(
                                    it
                                )
                            )
                        },
                        text = "Сокращение",
                        isEnabled = !(component.nFormsInterface.networkModel.value.state == NetworkState.Loading),
                        onEnterClicked = {
                            focusManager.moveFocus(FocusDirection.Next)
                            expandedMentors = true
                        },
                        supText = "инж/А",
                        focusManager = focusManager,
                        isMoveUpLocked = true,
                        autoCorrect = true,
                        keyboardType = KeyboardType.Text
                    )
                    Spacer(Modifier.height(7.dp))

                    val mentorsMap =
                        formsModel.mentors.associate { it.login to "${it.fio.surname} ${it.fio.name.first()}. ${(it.fio.praname ?: "").first()}." }

                    ExposedDropdownMenuBox(
                        expanded = expandedMentors,
                        onExpandedChange = {
                            expandedMentors = !expandedMentors
                        }
                    ) {
                        // textfield
                        val mentor =
                            formsModel.mentors.find { it.login == formsModel.cFormMentorLogin }
                        val mentorName =
                            try {
                                "${mentor!!.fio.surname} ${mentor.fio.name.first()}. ${(mentor.fio.praname ?: " ").first()}."
                            } catch (_: Throwable) {
                                ""
                            }
                        OutlinedTextField(
                            modifier = Modifier
                                .menuAnchor(), // menuAnchor modifier must be passed to the text field for correctness.
                            readOnly = true,
                            value = mentorName,
                            placeholder = { Text("Выберите") },
                            onValueChange = {},
                            label = { Text("Наставник") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = expandedMentors
                                )
                            },
                            shape = RoundedCornerShape(15.dp),
                            enabled = !(component.nFormsInterface.networkModel.value.state == NetworkState.Loading)
                        )
                        // menu

                        ExposedDropdownMenu(
                            expanded = expandedMentors,
                            onDismissRequest = {
                                expandedMentors = false
                            },
                        ) {
                            // menu items
                            mentorsMap.forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(selectionOption.value) },
                                    onClick = {
                                        component.formsComponent.onEvent(
                                            FormsStore.Intent.ChangeCFormMentorLogin(
                                                selectionOption.key
                                            )
                                        )
                                        expandedMentors = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                )
                            }
                        }


//
//                            onEnterClicked = {
//                                if (num == properties.size) {
//                                    component.onEvent(GroupsStore.Intent.CreateGroup)
//                                }
//                            },

                    }
                    Spacer(Modifier.height(7.dp))
                    AnimatedCommonButton(
                        text = "Создать",
                        modifier = Modifier.width(TextFieldDefaults.MinWidth),
                        isEnabled = num == properties.size
                    ) {
                        component.formsComponent.onEvent(FormsStore.Intent.CreateForm)
                    }
                    Spacer(Modifier.height(10.dp))
                }
            }
        }

        CBottomSheetContent(
            component = component.subjectsComponent.cGroupBottomSheet
        ) {
            val focusManager = LocalFocusManager.current
//                    Surface(
//                        modifier = Modifier
//                            .wrapContentWidth()
//                            .wrapContentHeight(),
//                        shape = MaterialTheme.shapes.large
//                    ) {
            var num = 0
            Column(
                Modifier.padding(top = 5.dp, bottom = 10.dp)
                    .padding(horizontal = 10.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                var expandedTeachers by remember { mutableStateOf(false) }
                val properties = listOf(
                    subjectsModel.cName,
                    subjectsModel.cTeacherLogin,
                    subjectsModel.cDifficult
                )
                num = properties.count { (it ?: "").isNotBlank() }
//                        if (model.cBirthday.length == 8) num++
                Text(
                    buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        ) {
                            append("Создать новую группу ")
                        }
                        withStyle(
                            SpanStyle(
                                fontSize = 15.sp,
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
                        .verticalScroll(rememberScrollState())
                ) {


                    Spacer(Modifier.height(7.dp))
                    CustomTextField(
                        value = subjectsModel.cName,
                        onValueChange = {
                            component.subjectsComponent.onEvent(
                                SubjectsStore.Intent.ChangeCName(
                                    it
                                )
                            )
                        },
                        text = "Название группы",
                        isEnabled = !(component.nSubjectsInterface.networkModel.value.state == NetworkState.Loading),
                        onEnterClicked = {
                            focusManager.moveFocus(FocusDirection.Next)
                            expandedTeachers = true
                        },
                        focusManager = focusManager,
                        isMoveUpLocked = true,
                        autoCorrect = true,
                        keyboardType = KeyboardType.Text
                    )
                    Spacer(Modifier.height(7.dp))

                    val teachersMap =
                        model.teachers.associate { it.login to "${it.fio.surname} ${it.fio.name.first()}. ${(it.fio.praname ?: " ").first()}." }

                    ExposedDropdownMenuBox(
                        expanded = expandedTeachers,
                        onExpandedChange = {
                            expandedTeachers = !expandedTeachers
                        }
                    ) {
                        // textfield
                        val mentor =
                            model.teachers.find { it.login == subjectsModel.cTeacherLogin }
                        val mentorName =
                            try {
                                "${mentor!!.fio.surname} ${mentor.fio.name.first()}. ${(mentor.fio.praname ?: " ").first()}."
                            } catch (_: Throwable) {
                                ""
                            }
                        OutlinedTextField(
                            modifier = Modifier
                                .menuAnchor(), // menuAnchor modifier must be passed to the text field for correctness.
                            readOnly = true,
                            value = mentorName,
                            placeholder = { Text("Выберите") },
                            onValueChange = {},
                            label = { Text("Учитель") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = expandedTeachers
                                )
                            },
                            shape = RoundedCornerShape(15.dp),
                            enabled = !(component.nSubjectsInterface.networkModel.value.state == NetworkState.Loading)
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
                                        component.subjectsComponent.onEvent(
                                            SubjectsStore.Intent.ChangeCTeacherLogin(
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
                        value = subjectsModel.cDifficult,
                        onValueChange = {
                            if (it.length < 2) {
                                component.subjectsComponent.onEvent(
                                    SubjectsStore.Intent.ChangeCDifficult(
                                        it
                                    )
                                )
                            }
                        },
                        text = "Уровень сложности",
                        isEnabled = !(component.nSubjectsInterface.networkModel.value.state == NetworkState.Loading),
                        onEnterClicked = {
                            if (num == properties.size) {
                                component.subjectsComponent.onEvent(SubjectsStore.Intent.CreateGroup)
                            }
                        },
                        focusManager = focusManager,
                        isMoveUpLocked = false,
                        autoCorrect = false,
                        keyboardType = KeyboardType.Number
                    )
                    Spacer(Modifier.height(7.dp))
                    AnimatedCommonButton(
                        text = "Создать",
                        modifier = Modifier.width(TextFieldDefaults.MinWidth),
                        isEnabled = num == properties.size
                    ) {
                        if (num == properties.size) {
                            component.subjectsComponent.onEvent(SubjectsStore.Intent.CreateGroup)
                        }
                    }
                    Spacer(Modifier.height(10.dp))

                }
            }
        }


        ListDialogContent(component.studentsComponent.formsListComponent)
    }


}

@Composable
fun SubjectItem(
    title: String,
    isChosen: Boolean,
    onClick: () -> Unit
) {
    FilledTonalButton(
        onClick = { if (!isChosen) onClick() },
        modifier = Modifier.fillMaxHeight(),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = if (isChosen) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceColorAtElevation(
                2.dp
            )
        )
    ) {
        Text(
            title,
            color = if (isChosen) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface
        )
    }
}
