@file:OptIn(ExperimentalFoundationApi::class)

import admin.groups.forms.CutedForm
import admin.users.User
import admin.users.UserInit
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onPlaced
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
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.*
import components.cBottomSheet.CBottomSheetStore
import components.networkInterface.NetworkState
import decomposeComponents.CAlertDialogContent
import decomposeComponents.CBottomSheetContent
import decomposeComponents.listDialogComponent.customConnection
import dev.chrisbanes.haze.HazeState
import excel.importStudents
import kotlinx.datetime.*
import pullRefresh.PullRefreshIndicator
import pullRefresh.pullRefresh
import pullRefresh.rememberPullRefreshState
import resources.RIcons
import server.Roles
import server.twoNums
import users.UsersComponent
import users.UsersStore
import view.LocalViewManager
import view.LockScreenOrientation
import view.esp

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class,
    ExperimentalFoundationApi::class,
)
@ExperimentalLayoutApi
@Composable
fun UsersContent(
    component: UsersComponent
) {
    LockScreenOrientation(-1)
    val model by component.model.subscribeAsState()
    val nModel by component.nModel.subscribeAsState()
    val viewManager = LocalViewManager.current
    val hazeState = remember { HazeState() }
    val isTextFieldShown = remember { mutableStateOf(false) }

    val refreshState = rememberPullRefreshState(
        (nModel.state == NetworkState.Loading) && model.users != null,
        { component.onEvent(UsersStore.Intent.FetchUsers) })

    Scaffold(

        modifier = Modifier.fillMaxSize().onKeyEvent {
            if (it.key == Key.F5 && it.type == KeyEventType.KeyDown) {
                component.onEvent(UsersStore.Intent.FetchUsers)
            }
            false
        },
        topBar = {
            AppBar(
                navigationRow = {
                    IconButton(
                        onClick = { component.onOutput(UsersComponent.Output.Back) }
                    ) {
                        GetAsyncIcon(
                            path = RIcons.ChevronLeft
                        )
                    }
                },
                title = {
                    val searchBarFocusRequester = remember { FocusRequester() }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.horizontalScroll(
                            rememberScrollState()
                        )
                    ) {
                        Text(
                            "Пользователи",
                            fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                            fontWeight = FontWeight.Black,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        IconButton(
                            onClick = {
                                isTextFieldShown.value = !isTextFieldShown.value
                                if (!isTextFieldShown.value) {
                                    component.onEvent(UsersStore.Intent.UpdateUserFind(""))
                                }
                            }
                        ) {
                            GetAsyncIcon(
                                RIcons.Search
                            )
                        }
                        AnimatedVisibility(
                            isTextFieldShown.value
                        ) {
                            Row {
                                CustomTextField(
                                    value = model.userFindField,
                                    onValueChange = {
                                        component.onEvent(UsersStore.Intent.UpdateUserFind(it))
//                                component.onEvent(AdminAchievementsStore.Intent.ChangeDate(it))
                                    },
                                    text = "Поиск",
                                    isEnabled = true,
                                    isMoveUpLocked = true,
                                    autoCorrect = false,
                                    keyboardType = KeyboardType.Text,
                                    focusRequester = searchBarFocusRequester,
//                            supText = "ФИОЛогин",
//                            supTextSize = 10.sp,
//                            textStyle = TextStyle.Default.copy(fontSize = 10.sp),
                                    width = TextFieldDefaults.MinWidth - 30.dp,
                                    minHeight = 40.dp,
                                    modifier = Modifier.onPlaced {
                                        searchBarFocusRequester.requestFocus()
                                    }
                                )
                                Spacer(Modifier.width(15.dp))
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.cClickable { component.onEvent(
                                UsersStore.Intent.FTeachers(!model.fTeachers)
                            ) }
                        ) {
                            CustomCheckbox(
                                checked = model.fTeachers
                            )
                            Text("Учителя")
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.cClickable {
                                component.onEvent(
                                    UsersStore.Intent.FStudents(!model.fStudents)
                                )
                            }.padding(start = 7.dp)
                        ) {
                            CustomCheckbox(
                                checked = model.fStudents
                            )
                            Text("Ученики")
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.cClickable {
                                component.onEvent(
                                    UsersStore.Intent.FOther(!model.fOther)
                                )
                            }.padding(start = 7.dp)
                        ) {
                            CustomCheckbox(
                                checked = model.fOther
                            )

                            Text("Другое")
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.cClickable {
                                component.onEvent(
                                    UsersStore.Intent.FParents(!model.fParents)
                                )
                            }.padding(start = 7.dp)
                        ) {
                            CustomCheckbox(
                                checked = model.fParents
                            )
                            Text("Родители")
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.cClickable {
                                component.onEvent(
                                    UsersStore.Intent.FNoAdmin(!model.fNoAdmin)
                                )
                            }.padding(start = 7.dp)
                        ) {
                            CustomCheckbox(
                                checked = model.fNoAdmin
                            )
                            Text("Не админ")
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.cClickable {
                                component.onEvent(
                                    UsersStore.Intent.FInActive(!model.fInActive)
                                )
                            }.padding(start = 7.dp)
                        ) {
                            CustomCheckbox(
                                checked = model.fInActive
                            )
                            Text("Inactive")
                        }

                        val showFilePicker = remember { mutableStateOf(false) }


                        CFilePicker(
                            showFilePicker = showFilePicker
                        ) {
                            importStudents(it, component)
                        }

                        IconButton(
                            onClick = {
                                showFilePicker.value = !showFilePicker.value
                            }
                        ) {
                            GetAsyncIcon(
                                path = RIcons.Upload
                            )
                        }
                    }
                },
                actionRow = {
                    if (model.users != null) {
                        IconButton(
                            onClick = { component.onEvent(UsersStore.Intent.FetchUsers) }
                        ) {
                            GetAsyncIcon(
                                RIcons.Refresh
                            )
                        }
                        IconButton(
                            onClick = {
                                component.cUserBottomSheet.onEvent(CBottomSheetStore.Intent.ShowSheet)
                            }
                        ) {
                            GetAsyncIcon(
                                RIcons.Add
                            )
                        }
                    }
                },
                hazeState = hazeState
            )
        }
    ) { padding ->


        val columnNames = listOf(
            "Отчество", //0
            "Логин", //1
            "Пароль",  //2
            "Дата рождения", //3
            "Роль",  //4
            "Модерация",  //5
            "Родитель", //6
        )

        val widthsInit = remember {
            mapOf(
                columnNames[0] to 200.dp,
                columnNames[1] to 160.dp,
                columnNames[2] to 90.dp,
                columnNames[3] to 170.dp,
                columnNames[4] to 90.dp,
                columnNames[5] to 140.dp,
                columnNames[6] to 120.dp
            )
        }

        val roles = listOfNotNull(
            if (model.fOther) Roles.nothing else null,
            if (model.fTeachers) Roles.teacher else null,
            if (model.fStudents) Roles.student else null
        )

        Box(Modifier.fillMaxSize().hazeUnder(viewManager = viewManager, hazeState = hazeState).padding(padding)) {
            Crossfade(targetState = nModel) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    when {
                        model.users != null -> {
                            val users = model.users!!.filter {
                                val isInActive =
                                    if (!model.fInActive) it.isActive else true
                                val moder =
                                    if (!model.fNoAdmin) it.user.moderation != Roles.nothing else true
                                val parent =
                                    if (!model.fParents) !it.user.isParent else true
                                val fio = with(it.user.fio) {
                                    "$surname $name $praname"
                                }
                                ((it.login.lowercase().contains(model.userFindField.lowercase()) || fio.lowercase().contains(model.userFindField.lowercase())) || model.userFindField.isBlank())  && (it.user.role in roles
                                        && moder
                                        && isInActive
                                        && parent)
                            }.sortedWith(compareBy({!it.isActive}, {it.user.fio.surname}))
                            TableScreen(
                                columnNames,
                                widthsInit = widthsInit,
                                users.map {
                                    Pair(
                                        "${if (it.isActive) "" else "."}${it.user.fio.surname} ${it.user.fio.name}",
                                        mapOf(
                                            columnNames[0] to (it.user.fio.praname ?: "--"),
                                            columnNames[1] to it.login,
                                            columnNames[2] to if (it.isProtected) "есть" else "нет",
                                            columnNames[3] to try {
                                                it.user.birthday.substring(0, 2) +
                                                        "." + it.user.birthday.substring(2, 4) +
                                                        "." + it.user.birthday.substring(4)
                                            } catch (_: Throwable) {
                                                "null"
                                            },
                                            columnNames[4] to when (it.user.role) {
                                                Roles.teacher -> "учитель"
                                                Roles.student -> "ученик"
                                                else -> "другое"
                                            },
                                            columnNames[5] to it.user.moderation,
                                            columnNames[6] to if (it.user.isParent) "да" else "нет"
                                        )
                                    )

                                },
                                true,
                                onEditClick = {
//password = model.users!![it].password,
                                    val userForEditing = User(
                                        login = users[it].login,
                                        user = UserInit(
                                            fio = FIO(
                                                name = users[it].user.fio.name,
                                                surname = users[it].user.fio.surname,
                                                praname = users[it].user.fio.praname
                                            ),
                                            birthday = users[it].user.birthday,
                                            role = users[it].user.role,
                                            moderation = users[it].user.moderation,
                                            isParent = users[it].user.isParent
                                        ),
                                        avatarId = users[it].avatarId,
                                        isProtected = users[it].isProtected,
                                        isActive = users[it].isActive,
                                        subjectId = users[it].subjectId
                                    )
                                    component.onEvent(
                                        UsersStore.Intent.OpenEditingSheet(
                                            userForEditing
                                        )
                                    )

                                },
                                modifier = Modifier.padding(8.dp).pullRefresh(refreshState)
                            )
                        }

                        else -> {
                            when (it.state) {
                                NetworkState.Loading -> {
                                    LoadingAnimation()
                                }

                                else -> {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            it.error,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(Modifier.height(5.dp))
                                        if (it.error != "Доступ запрещён") {
                                            CustomTextButton("Попробовать ещё раз") {
                                                component.onEvent(UsersStore.Intent.FetchUsersInit)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                }

            }

            PullRefreshIndicator(
                modifier = Modifier.align(alignment = Alignment.TopCenter),
                refreshing = nModel.state == NetworkState.Loading && model.users != null,
                state = refreshState,
                topPadding = padding.calculateTopPadding()
            )
            editUserSheet(
                component,
                model,
                hazeState = hazeState
            )
            createUserSheet(
                component,
                model,
                hazeState = hazeState
            )

        }

    }
}

@OptIn(ExperimentalFoundationApi::class)
@ExperimentalMaterial3Api
@Composable
private fun editUserSheet(
    component: UsersComponent,
    model: UsersStore.State,
    hazeState: HazeState
) {


    val eNModel = component.eUserBottomSheet.nModel.subscribeAsState()
    val isEditingInProcess = (eNModel.value.state == NetworkState.Loading)
    val isActive = model.users?.firstOrNull { it.login == model.eLogin }?.isActive != false

//                LaunchedEffect(modalBottomSheetState.isVisible) {
//                    if (modalBottomSheetState.isVisible) {
//                        modalBottomSheetState.expand()
//                    }
//                }
    CBottomSheetContent(
        component = component.eUserBottomSheet,
        customLoadingScreen = true,
        customMaxHeight = 0.dp
    ) {
        val focusManager = LocalFocusManager.current
        val lazyList = rememberLazyListState()
//                    Surface(
//                        modifier = Modifier
//                            .wrapContentWidth()
//                            .wrapContentHeight(),
//                        shape = MaterialTheme.shapes.large
//                    ) {
        var num = 0
        Column(
            Modifier.fillMaxWidth()
                .alpha(if (eNModel.value.state == NetworkState.Error) 0.4f else 1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var expandedRoles by remember { mutableStateOf(false) }

            Text(
                model.eLogin, fontWeight = FontWeight.Black,
                fontSize = 27.esp, textAlign = TextAlign.Center
            )
            val properties = listOf(
                model.eName,
                model.eSurname,
                model.ePraname,
                model.eRole,
            )
            num = properties.count { (it ?: "").isNotBlank() }
            if (model.eBirthday.length == 8) num++
            Text(
                buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = MaterialTheme.typography.titleLarge.fontSize
                        )
                    ) {
                        append("Редактировать пользователя ")
                    }
                    withStyle(
                        SpanStyle(
                            fontSize = MaterialTheme.typography.titleSmall.fontSize,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    ) {
                        append("$num/5")
                    }
                }
            )
            Spacer(Modifier.height(5.dp))
            LazyColumn(
                Modifier.imePadding().fillMaxWidth()
                    .nestedScroll(lazyList.customConnection),
                state = lazyList,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {


                    Spacer(Modifier.height(7.dp))
                    CustomTextField(
                        value = model.eSurname,
                        onValueChange = {
                            component.onEvent(UsersStore.Intent.ChangeESurname(it))
                        },
                        text = "Фамилия",
                        isEnabled = !isEditingInProcess,
                        onEnterClicked = {
                            focusManager.moveFocus(FocusDirection.Next)

                        },
                        focusManager = focusManager,
                        isMoveUpLocked = true,
                        autoCorrect = false,
                        keyboardType = KeyboardType.Password
                    )
                    Spacer(Modifier.height(7.dp))
                    CustomTextField(
                        value = model.eName,
                        onValueChange = {
                            component.onEvent(UsersStore.Intent.ChangeEName(it))
                        },
                        text = "Имя",
                        isEnabled = !isEditingInProcess,
                        onEnterClicked = {
                            focusManager.moveFocus(FocusDirection.Next)
                        },
                        focusManager = focusManager,
                        isMoveUpLocked = false,
                        autoCorrect = false,
                        keyboardType = KeyboardType.Password
                    )
                    Spacer(Modifier.height(7.dp))
                    CustomTextField(
                        value = model.ePraname ?: "",
                        onValueChange = {
                            component.onEvent(UsersStore.Intent.ChangeEPraname(it))
                        },
                        text = "Отчество",
                        isEnabled = !isEditingInProcess,
                        onEnterClicked = {
                            focusManager.moveFocus(FocusDirection.Next)
                        },
                        focusManager = focusManager,
                        isMoveUpLocked = false,
                        autoCorrect = false,
                        keyboardType = KeyboardType.Password
                    )
                    Spacer(Modifier.height(7.dp))
                    CustomTextField(
                        value = model.eBirthday,
                        onValueChange = {
                            if (it.length <= 8 && (it.matches("\\d{1,8}".toRegex()) || it.isEmpty())) {
                                component.onEvent(UsersStore.Intent.ChangeEBirthday(it))
                            }
                        },
                        text = "Дата рождения",
                        isEnabled = !isEditingInProcess,
                        onEnterClicked = {
                            focusManager.moveFocus(FocusDirection.Down)
                            expandedRoles = true
                        },
                        focusManager = focusManager,
                        isMoveUpLocked = false,
                        autoCorrect = false,
                        keyboardType = KeyboardType.Number,
                        supText = "ДД.ММ.ГГГГ",
                        isDateEntry = true,
                        trailingIcon = {
                            IconButton(
                                {
                                    component.onEvent(
                                        UsersStore.Intent.ChangeDateDialogShowing(
                                            true
                                        )
                                    )
                                },
                                enabled = !isEditingInProcess
                            ) {
                                GetAsyncIcon(
                                    path = RIcons.Calendar
                                )
                            }
                        }
                    )
                    if (model.isDateDialogShowing) {
                        val datePickerState = rememberDatePickerState(
                            initialSelectedDateMillis =
                            if (model.eBirthday.length == 8) {
                                val day = model.eBirthday.substring(0, 2).toInt()
                                val month = model.eBirthday.substring(2, 4).toInt()
                                val year = model.eBirthday.substring(4).toInt()
                                LocalDate(
                                    year = year,
                                    monthNumber = month,
                                    dayOfMonth = day
                                ).atStartOfDayIn(
                                    TimeZone.UTC
                                ).toEpochMilliseconds()
                            } else null,
                            yearRange = IntRange(1940, model.currentYear - 5)
                        )
                        DatePickerDialog(
                            onDismissRequest = {
                                component.onEvent(
                                    UsersStore.Intent.ChangeDateDialogShowing(
                                        false
                                    )
                                )
                            },
                            confirmButton = {
                                CustomTextButton(
                                    "Ок",
                                    modifier = Modifier.padding(
                                        end = 30.dp,
                                        start = 20.dp,
                                        bottom = 10.dp
                                    ),
                                    color = if (datePickerState.selectedDateMillis != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(
                                        alpha = 0.3f
                                    )
                                ) {
                                    if (datePickerState.selectedDateMillis != null) {
                                        val date =
                                            Instant.fromEpochMilliseconds(
                                                datePickerState.selectedDateMillis!!
                                            )
                                                .toLocalDateTime(TimeZone.of("UTC+3"))
                                        component.onEvent(
                                            UsersStore.Intent.ChangeEBirthday(
                                                "${date.dayOfMonth.twoNums()}${date.monthNumber.twoNums()}${date.year}"
                                            )
                                        )
                                        component.onEvent(
                                            UsersStore.Intent.ChangeDateDialogShowing(
                                                false
                                            )
                                        )
                                    }
                                }
                            },
                            dismissButton = {
                                CustomTextButton(
                                    "Отмена",
                                    modifier = Modifier.padding(bottom = 10.dp)
                                ) {
                                    component.onEvent(
                                        UsersStore.Intent.ChangeDateDialogShowing(
                                            false
                                        )
                                    )
                                }
                            }
                        ) {
                            DatePicker(
                                state = datePickerState,
                                showModeToggle = false,
                                title = {
                                    Text(
                                        "Выберите дату",
                                        modifier = Modifier.padding(
                                            top = 15.dp,
                                            start = 20.dp
                                        )
                                    )
                                }
                            )
                        }
                    }

                    Spacer(Modifier.height(7.dp))

                    val rolesList = mapOf(
                        Roles.student to "Ученик",
                        Roles.teacher to "Учитель",
                        Roles.nothing to "Другое"
                    )
                    ExposedDropdownMenuBox(
                        expanded = expandedRoles,
                        onExpandedChange = {
                            expandedRoles = !expandedRoles
                        }
                    ) {
                        // textfield
                        OutlinedTextField(
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable), // menuAnchor modifier must be passed to the text field for correctness.
                            readOnly = true,
                            value = when (model.eRole) {
                                Roles.teacher -> "Учитель"
                                Roles.student -> "Ученик"
                                Roles.nothing -> "Другое"
                                else -> ""
                            },
                            placeholder = { Text("Выберите") },
                            onValueChange = {},
                            label = { Text("Роль") },
                            trailingIcon = {
                                val chevronRotation = animateFloatAsState(if (expandedRoles) 90f else -90f)
                                GetAsyncIcon(
                                    path = RIcons.ChevronLeft,
                                    modifier = Modifier.padding(end = 10.dp).rotate(chevronRotation.value),
                                    size = 15.dp
                                )
                            },
                            shape = RoundedCornerShape(15.dp),
                            enabled = !isEditingInProcess
                        )
                        // menu
                        ExposedDropdownMenu(
                            expanded = expandedRoles,
                            onDismissRequest = {
                                expandedRoles = false
                            },
                        ) {
                            // menu items
                            rolesList.forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(selectionOption.value) },
                                    onClick = {
                                        component.onEvent(
                                            UsersStore.Intent.ChangeERole(
                                                selectionOption.key
                                            )
                                        )
                                        expandedRoles = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(7.dp))
                    if (model.eRole == Roles.teacher) {
                        var expandedSubjects by remember { mutableStateOf(false) }

                        ExposedDropdownMenuBox(
                            expanded = expandedSubjects,
                            onExpandedChange = {
                                expandedSubjects = !expandedSubjects
                            }
                        ) {

                            // textfield
                            OutlinedTextField(
                                modifier = Modifier
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable), // menuAnchor modifier must be passed to the text field for correctness.
                                readOnly = true,
                                value = model.subjects[model.eSubjectId] ?: "",
                                placeholder = { Text("Выберите") },
                                onValueChange = {},
                                label = { Text("Предмет") },
                                trailingIcon = {
                                    val chevronRotation = animateFloatAsState(if (expandedSubjects) 90f else -90f)
                                    GetAsyncIcon(
                                        path = RIcons.ChevronLeft,
                                        modifier = Modifier.padding(end = 10.dp).rotate(chevronRotation.value),
                                        size = 15.dp
                                    )
                                },
                                shape = RoundedCornerShape(15.dp),
                                enabled = !isEditingInProcess
                            )
                            // menu
                            ExposedDropdownMenu(
                                expanded = expandedSubjects,
                                onDismissRequest = {
                                    expandedSubjects = false
                                },
                            ) {
                                // menu items
                                (model.subjects).forEach { selectionOption ->
                                    DropdownMenuItem(
                                        text = { Text("${selectionOption.key} ${selectionOption.value}" ) },
                                        onClick = {
                                            component.onEvent(
                                                UsersStore.Intent.ChangeESubjectId(
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
                    }
                    Spacer(Modifier.height(7.dp))
                    Row(
                        Modifier.width(TextFieldDefaults.MinWidth)
                            .padding(horizontal = 7.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Модератор",
                            fontWeight = FontWeight.Bold,
                            fontSize = MaterialTheme.typography.titleLarge.fontSize,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Switch(
                            checked = model.eIsModerator,
                            onCheckedChange = {
                                component.onEvent(
                                    UsersStore.Intent.ChangeEIsModerator(
                                        it
                                    )
                                )
                            },
                            enabled = !isEditingInProcess
                        )
                    }
                    Spacer(Modifier.height(7.dp))
                    Row(
                        Modifier.width(TextFieldDefaults.MinWidth)
                            .padding(horizontal = 7.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Наставник",
                            fontWeight = FontWeight.Bold,
                            fontSize = MaterialTheme.typography.titleLarge.fontSize,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Switch(
                            checked = model.eIsMentor,
                            onCheckedChange = {
                                component.onEvent(
                                    UsersStore.Intent.ChangeEIsMentor(
                                        it
                                    )
                                )
                            },
                            enabled = !isEditingInProcess
                        )
                    }
                    Spacer(Modifier.height(7.dp))
                    Row(
                        Modifier.width(TextFieldDefaults.MinWidth)
                            .padding(horizontal = 7.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Родитель",
                            fontWeight = FontWeight.Bold,
                            fontSize = MaterialTheme.typography.titleLarge.fontSize,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Switch(
                            checked = model.eIsParent,
                            onCheckedChange = {
                                component.onEvent(
                                    UsersStore.Intent.ChangeEIsParent(
                                        it
                                    )
                                )
                            },
                            enabled = !isEditingInProcess
                        )
                    }
                    Spacer(Modifier.height(7.dp))

                    if (isActive) {
                        AnimatedVisibility(
                            !model.eIsMentor && !model.eIsModerator
                        ) {
                            CustomTextButton(if (model.eRole == Roles.student) "Отчислить" else "Удалить") {
                                component.onEvent(UsersStore.Intent.DeleteAccountInit(model.eLogin))
                            }
                            Spacer(Modifier.height(7.dp))
                        }
                    } else {
                        CustomTextButton("Восстановить") {
                            component.onEvent(UsersStore.Intent.EditUser)
                        }
                        Spacer(Modifier.height(7.dp))
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp)
                    ) {
                        if (model.eIsPassword) {
                            CustomTextButton(
                                "Сбросить пароль",
                                color = if (!isEditingInProcess) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(
                                    alpha = 0.3f
                                )
                            ) {
                                component.onEvent(UsersStore.Intent.ClearPassword)
                            }
                            if (isActive) {
                                Spacer(Modifier.width(12.dp))
                            }
                        }
                        if (isActive) {
                            AnimatedCommonButton(
                                text = "Редактировать",
                                isEnabled = num == 5,
                                modifier = if (!model.eIsPassword) Modifier.width(
                                    TextFieldDefaults.MinWidth
                                ) else Modifier
                            ) {
                                component.onEvent(UsersStore.Intent.EditUser)
                            }
                        }
                    }
                    Spacer(Modifier.height(10.dp))

                }
            }


//            if (model.eError.isNotBlank()) {
//                Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                    Text(model.eError)
//                    Spacer(Modifier.height(7.dp))
//                    CustomTextButton("Попробовать ещё раз") {
//                        component.onEvent(UsersStore.Intent.TryEditUserAgain)
//                    }
//                }
//            }
        }
    }
    CAlertDialogContent(
        component = component.eDeleteDialog,
        isCustomButtons = false,
        title = "Удалить ${model.eDeletingLogin}?"
    ) {
        val deleteNModel by component.eDeleteDialog.nModel.subscribeAsState()
        Crossfade(deleteNModel.state) {
            when (it) {
                NetworkState.Error -> Column(
                    Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(deleteNModel.error)
                    Spacer(Modifier.height(7.dp))
                    CustomTextButton("Попробовать ещё раз") {
                        deleteNModel.onFixErrorClick()
                    }
                }

                NetworkState.Loading -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

                else -> {}
            }
        }
    }
}


@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalFoundationApi::class, ExperimentalFoundationApi::class
)
@Composable
private fun createUserSheet(
    component: UsersComponent,
    model: UsersStore.State,
    hazeState: HazeState?
) {
    val cNModel = component.cUserBottomSheet.nModel.subscribeAsState()
    val isCreatingInProcess = (cNModel.value.state == NetworkState.Loading)
    val lazyList = rememberLazyListState()
    CBottomSheetContent(
        component = component.cUserBottomSheet,
        customLoadingScreen = true,
        customMaxHeight = 0.dp
    ) {
        val focusManager = LocalFocusManager.current
        var num = 0
        if (model.cLogin.isBlank() && cNModel.value.state != NetworkState.Error) {
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                var expandedRoles by remember { mutableStateOf(false) }
                val properties = listOf(
                    model.cName,
                    model.cSurname,
                    model.cPraname,
                    model.cRole,
                )
                num = properties.count { (it ?: "").isNotBlank() }
                if (model.cBirthday.length == 8) num++
                Text(
                    buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = MaterialTheme.typography.titleLarge.fontSize
                            )
                        ) {
                            append("Создать нового пользователя ")
                        }
                        withStyle(
                            SpanStyle(
                                fontSize = MaterialTheme.typography.titleSmall.fontSize,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        ) {
                            append("$num/5")
                        }
                    }
                )
                Spacer(Modifier.height(5.dp))
                LazyColumn(
                    Modifier
                        .fillMaxWidth()
                        .imePadding()
                        .nestedScroll(lazyList.customConnection),
                    state = lazyList,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {


                        Spacer(Modifier.height(7.dp))
                        CustomTextField(
                            value = model.cSurname,
                            onValueChange = {
                                component.onEvent(UsersStore.Intent.ChangeCSurname(it))
                            },
                            text = "Фамилия",
                            isEnabled = !isCreatingInProcess,//model.isCreatingInProcess,
                            onEnterClicked = {
                                focusManager.moveFocus(FocusDirection.Next)
                            },
                            focusManager = focusManager,
                            isMoveUpLocked = true,
                            autoCorrect = false,
                            keyboardType = KeyboardType.Password
                        )
                        Spacer(Modifier.height(7.dp))
                        CustomTextField(
                            value = model.cName,
                            onValueChange = {
                                component.onEvent(UsersStore.Intent.ChangeCName(it))
                            },
                            text = "Имя",
                            isEnabled = !isCreatingInProcess,
                            onEnterClicked = {
                                focusManager.moveFocus(FocusDirection.Next)
                            },
                            focusManager = focusManager,
                            isMoveUpLocked = false,
                            autoCorrect = false,
                            keyboardType = KeyboardType.Password
                        )
                        Spacer(Modifier.height(7.dp))
                        CustomTextField(
                            value = model.cPraname ?: "",
                            onValueChange = {
                                component.onEvent(UsersStore.Intent.ChangeCPraname(it))
                            },
                            text = "Отчество",
                            isEnabled = !isCreatingInProcess,
                            onEnterClicked = {
                                focusManager.moveFocus(FocusDirection.Next)
                            },
                            focusManager = focusManager,
                            isMoveUpLocked = false,
                            autoCorrect = false,
                            keyboardType = KeyboardType.Password
                        )
                        Spacer(Modifier.height(7.dp))
                        CustomTextField(
                            value = model.cBirthday,
                            onValueChange = {
                                if (it.length <= 8 && (it.matches("\\d{1,8}".toRegex()) || it.isEmpty())) {
                                    component.onEvent(UsersStore.Intent.ChangeCBirthday(it))
                                }
                            },
                            text = "Дата рождения",
                            isEnabled = !isCreatingInProcess,
                            onEnterClicked = {
                                focusManager.moveFocus(FocusDirection.Down)
                                expandedRoles = true
                            },
                            focusManager = focusManager,
                            isMoveUpLocked = false,
                            autoCorrect = false,
                            keyboardType = KeyboardType.Number,
                            supText = "ДД.ММ.ГГГГ",
                            isDateEntry = true,
                            trailingIcon = {
                                IconButton(
                                    {
                                        component.onEvent(
                                            UsersStore.Intent.ChangeDateDialogShowing(
                                                true
                                            )
                                        )
                                    },
                                    enabled = !isCreatingInProcess
                                ) {
                                    GetAsyncIcon(
                                        RIcons.Calendar
                                    )
                                }
                            }
                        )
                        if (model.isDateDialogShowing) {
                            val datePickerState = rememberDatePickerState(
                                initialSelectedDateMillis =
                                if (model.cBirthday.length == 8) {
                                    val day = model.cBirthday.substring(0, 2).toInt()
                                    val month = model.cBirthday.substring(2, 4).toInt()
                                    val year = model.cBirthday.substring(4).toInt()
                                    LocalDate(
                                        year = year,
                                        monthNumber = month,
                                        dayOfMonth = day
                                    ).atStartOfDayIn(
                                        TimeZone.UTC
                                    ).toEpochMilliseconds()
                                } else null,
                                yearRange = IntRange(1940, model.currentYear - 5)
                            )
                            DatePickerDialog(
                                onDismissRequest = {
                                    component.onEvent(
                                        UsersStore.Intent.ChangeDateDialogShowing(
                                            false
                                        )
                                    )
                                },
                                confirmButton = {
                                    CustomTextButton(
                                        "Ок",
                                        modifier = Modifier.padding(
                                            end = 30.dp,
                                            start = 20.dp,
                                            bottom = 10.dp
                                        ),
                                        color = if (datePickerState.selectedDateMillis != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(
                                            alpha = 0.3f
                                        )
                                    ) {
                                        if (datePickerState.selectedDateMillis != null) {
                                            val date =
                                                Instant.fromEpochMilliseconds(
                                                    datePickerState.selectedDateMillis!!
                                                )
                                                    .toLocalDateTime(TimeZone.of("UTC+3"))
                                            component.onEvent(
                                                UsersStore.Intent.ChangeCBirthday(
                                                    "${date.dayOfMonth.twoNums()}${date.monthNumber.twoNums()}${date.year}"
                                                )
                                            )
                                            component.onEvent(
                                                UsersStore.Intent.ChangeDateDialogShowing(
                                                    false
                                                )
                                            )
                                        }
                                    }
                                },
                                dismissButton = {
                                    CustomTextButton(
                                        "Отмена",
                                        modifier = Modifier.padding(bottom = 10.dp)
                                    ) {
                                        component.onEvent(
                                            UsersStore.Intent.ChangeDateDialogShowing(
                                                false
                                            )
                                        )
                                    }
                                }
                            ) {
                                DatePicker(
                                    state = datePickerState,
                                    showModeToggle = false,
                                    title = {
                                        Text(
                                            "Выберите дату",
                                            modifier = Modifier.padding(
                                                top = 15.dp,
                                                start = 20.dp
                                            )
                                        )
                                    }
                                )
                            }
                        }

                        Spacer(Modifier.height(7.dp))

                        val rolesList = mapOf(
                            Roles.student to "Ученик",
                            Roles.teacher to "Учитель",
                            Roles.nothing to "Другое"
                        )

                        ExposedDropdownMenuBox(
                            expanded = expandedRoles,
                            onExpandedChange = {
                                expandedRoles = !expandedRoles
                            }
                        ) {
                            // textfield
                            OutlinedTextField(
                                modifier = Modifier
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable), // menuAnchor modifier must be passed to the text field for correctness.
                                readOnly = true,
                                value = when (model.cRole) {
                                    Roles.teacher -> "Учитель"
                                    Roles.student -> "Ученик"
                                    Roles.nothing -> "Другое"
                                    else -> ""
                                },
                                placeholder = { Text("Выберите") },
                                onValueChange = {},
                                label = { Text("Роль") },
                                trailingIcon = {
                                    val chevronRotation = animateFloatAsState(if (expandedRoles) 90f else -90f)
                                    GetAsyncIcon(
                                        path = RIcons.ChevronLeft,
                                        modifier = Modifier.padding(end = 10.dp).rotate(chevronRotation.value),
                                        size = 15.dp
                                    )
                                },
                                shape = RoundedCornerShape(15.dp),
                                enabled = !isCreatingInProcess
                            )
                            // menu
                            ExposedDropdownMenu(
                                expanded = expandedRoles,
                                onDismissRequest = {
                                    expandedRoles = false
                                },
                            ) {
                                // menu items
                                rolesList.forEach { selectionOption ->
                                    DropdownMenuItem(
                                        text = { Text(selectionOption.value) },
                                        onClick = {
                                            component.onEvent(
                                                UsersStore.Intent.ChangeCRole(
                                                    selectionOption.key
                                                )
                                            )
                                            expandedRoles = false
                                        },
                                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(7.dp))
                        if (model.cRole == Roles.teacher) {
                            var expandedSubjects by remember { mutableStateOf(false) }

                            ExposedDropdownMenuBox(
                                expanded = expandedSubjects,
                                onExpandedChange = {
                                    expandedSubjects = !expandedSubjects
                                }
                            ) {

                                // textfield
                                OutlinedTextField(
                                    modifier = Modifier
                                        .menuAnchor(MenuAnchorType.PrimaryNotEditable), // menuAnchor modifier must be passed to the text field for correctness.
                                    readOnly = true,
                                    value = model.subjects[model.cSubjectId] ?: "",
                                    placeholder = { Text("Выберите") },
                                    onValueChange = {},
                                    label = { Text("Предмет") },
                                    trailingIcon = {
                                        val chevronRotation = animateFloatAsState(if (expandedSubjects) 90f else -90f)
                                        GetAsyncIcon(
                                            path = RIcons.ChevronLeft,
                                            modifier = Modifier.padding(end = 10.dp).rotate(chevronRotation.value),
                                            size = 15.dp
                                        )
                                    },
                                    shape = RoundedCornerShape(15.dp),
                                    enabled = !isCreatingInProcess
                                )
                                // menu
                                ExposedDropdownMenu(
                                    expanded = expandedSubjects,
                                    onDismissRequest = {
                                        expandedSubjects = false
                                    },
                                ) {
                                    // menu items
                                    (model.subjects).forEach { selectionOption ->
                                        DropdownMenuItem(
                                            text = { Text("${selectionOption.key} ${selectionOption.value}" ) },
                                            onClick = {
                                                component.onEvent(
                                                    UsersStore.Intent.ChangeCSubjectId(
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
                        }
                        if (model.cRole != Roles.student) {
                            Row(
                                Modifier.width(TextFieldDefaults.MinWidth)
                                    .padding(horizontal = 7.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Модератор",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                Switch(
                                    checked = model.cIsModerator,
                                    onCheckedChange = {
                                        component.onEvent(
                                            UsersStore.Intent.ChangeCIsModerator(
                                                it
                                            )
                                        )
                                    },
                                    enabled = !isCreatingInProcess
                                )
                            }
                            Spacer(Modifier.height(7.dp))
                            Row(
                                Modifier.width(TextFieldDefaults.MinWidth)
                                    .padding(horizontal = 7.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Наставник",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                Switch(
                                    checked = model.cIsMentor,
                                    onCheckedChange = {
                                        component.onEvent(
                                            UsersStore.Intent.ChangeCIsMentor(
                                                it
                                            )
                                        )
                                    },
                                    enabled = !isCreatingInProcess
                                )
                            }
                            Spacer(Modifier.height(7.dp))
                            Row(
                                Modifier.width(TextFieldDefaults.MinWidth)
                                    .padding(horizontal = 7.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Родитель",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                Switch(
                                    checked = model.cIsParent,
                                    onCheckedChange = {
                                        component.onEvent(
                                            UsersStore.Intent.ChangeCIsParent(
                                                it
                                            )
                                        )
                                    },
                                    enabled = !isCreatingInProcess
                                )
                            }
                        } else {
                            var expandedForms by remember { mutableStateOf(false) }

                            ExposedDropdownMenuBox(
                                expanded = expandedForms,
                                onExpandedChange = {
                                    expandedForms = !expandedForms
                                }
                            ) {
                                val form = model.forms.firstOrNull { it.id == model.cFormId }

                                // textfield
                                OutlinedTextField(
                                    modifier = Modifier
                                        .menuAnchor(MenuAnchorType.PrimaryNotEditable), // menuAnchor modifier must be passed to the text field for correctness.
                                    readOnly = true,
                                    value = if (form != null) "${form.classNum} ${form.title}" else "",
                                    placeholder = { Text("Выберите") },
                                    onValueChange = {},
                                    label = { Text("Класс") },
                                    trailingIcon = {
                                        val chevronRotation = animateFloatAsState(if (expandedForms) 90f else -90f)
                                        GetAsyncIcon(
                                            path = RIcons.ChevronLeft,
                                            modifier = Modifier.padding(end = 10.dp).rotate(chevronRotation.value),
                                            size = 15.dp
                                        )
                                    },
                                    shape = RoundedCornerShape(15.dp),
                                    enabled = !isCreatingInProcess
                                )
                                // menu
                                ExposedDropdownMenu(
                                    expanded = expandedForms,
                                    onDismissRequest = {
                                        expandedForms = false
                                    },
                                ) {
                                    // menu items
                                    (emptyList<CutedForm>() + CutedForm(
                                        id = 0,
                                        title = "Никакой",
                                        0
                                    ) + model.forms).forEach { selectionOption ->
                                        DropdownMenuItem(
                                            text = { Text("${selectionOption.classNum} ${selectionOption.title}") },
                                            onClick = {
                                                component.onEvent(
                                                    UsersStore.Intent.ChangeCFormId(
                                                        selectionOption.id
                                                    )
                                                )
                                                expandedForms = false
                                            },
                                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                        )
                                    }
                                }
                            }
                            Spacer(Modifier.height(7.dp))
                            CustomTextField(
                                value = model.cParentFirstFIO,
                                onValueChange = {
                                    component.onEvent(UsersStore.Intent.ChangeCParentFirstFIO(it))
                                },
                                text = "ФИО матери",
                                isEnabled = !isCreatingInProcess,//model.isCreatingInProcess,
                                onEnterClicked = {
                                    focusManager.moveFocus(FocusDirection.Next)
                                },
                                focusManager = focusManager,
                                isMoveUpLocked = false,
                                autoCorrect = false,
                                keyboardType = KeyboardType.Password,
                                supText = "Фамилия Имя Отчество"
                            )
                            Spacer(Modifier.height(7.dp))
                            CustomTextField(
                                value = model.cParentSecondFIO,
                                onValueChange = {
                                    component.onEvent(UsersStore.Intent.ChangeCParentSecondFIO(it))
                                },
                                text = "ФИО отца",
                                isEnabled = !isCreatingInProcess,//model.isCreatingInProcess,
                                onEnterClicked = {
                                    focusManager.moveFocus(FocusDirection.Next)
                                },
                                focusManager = focusManager,
                                isMoveUpLocked = false,
                                autoCorrect = false,
                                keyboardType = KeyboardType.Password,
                                supText = "Фамилия Имя Отчество"
                            )
                            Spacer(Modifier.height(7.dp))
                        }

                        Spacer(Modifier.height(7.dp))
                        AnimatedCommonButton(
                            text = "Создать",
                            modifier = Modifier.width(TextFieldDefaults.MinWidth),
                            isEnabled = num == 5
                        ) {
                            component.onEvent(UsersStore.Intent.CreateUser)
                        }
                        Spacer(Modifier.height(10.dp))

                    }
                }
            }
        } else {
            Column(
                Modifier.padding(10.dp).fillMaxWidth()
                    .height(if (model.cParentLogins != null) 260.dp else 200.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
//                        if (cNModel.value.state == NetworkState.Error) {
//                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                                Text(cNModel.value.error)
//                                Spacer(Modifier.height(7.dp))
//                                CustomTextButton("Попробовать ещё раз") {
//                                    component.cUserBottomSheet.nInterface.fixError()
//                                }
//                            }
//                        } else {
                Text(
                    "${model.cSurname} ${model.cName} ${model.cPraname}",
                    fontWeight = FontWeight.Bold,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    textAlign = TextAlign.Center
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        model.cLogin,
                        fontWeight = FontWeight.Black,
                        fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(7.dp))
                    if (model.cParentLogins != null) {
                        listOf(
                            model.cParentFirstFIO,
                            model.cParentSecondFIO
                        ).forEachIndexed { i, s ->
                            if (s.isNotEmpty()) {
                                Text(
                                    s,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    model.cParentLogins?.getOrNull(i).toString(),
                                    fontWeight = FontWeight.Black,
                                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                Button(
                    onClick = {
                        component.cUserBottomSheet.onEvent(CBottomSheetStore.Intent.HideSheet)
                        component.onEvent(UsersStore.Intent.ClearUser)
                    },
                    modifier = Modifier.width(TextFieldDefaults.MinWidth)
                ) {
                    Text("Готово")
                }
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}


@Composable
fun TableScreen(
    columnNames: List<String>,
    widthsInit: Map<String, Dp>,
    rows: List<Pair<String, Map<String, String>>>,
    isEditable: Boolean = false,
    onEditClick: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val widths = mutableStateMapOf<String, Dp>()
    columnNames.forEach {
        widths[it] = widthsInit[it] ?: 20.dp
    }
    val density = LocalDensity.current
    val vScrollState = rememberLazyListState()
    val hScrollState = rememberScrollState()

    val lP = 50.dp

    val allHeight = remember { mutableStateOf(0.dp) }
    val allWidth = remember { mutableStateOf(0.dp) }
    ScrollBaredBox(
        vState = vScrollState, hState = hScrollState,
        height = allHeight, width = allWidth,
        modifier = modifier
    ) {
        Box(Modifier.horizontalScroll(hScrollState)) {
            Row() {//modifier = Modifier.horizontalScroll(hhScrollState)
//            Divider(Modifier.height(allHeight.value).width(1.dp))
                Spacer(Modifier.width(lP))
                columnNames.onEachIndexed { index, i ->
                    if (index != widths.size - 1) {
                        Spacer(Modifier.width(widths[i]!! - 0.5.dp))
                        VerticalDivider(
                            Modifier.height(allHeight.value),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = .4f),
                            thickness = 1.dp
                        )
                    }
                }


            }

            Column(
                modifier = Modifier.onGloballyPositioned { layoutCoordinates ->
                    allHeight.value =
                        with(density) { layoutCoordinates.size.height.toDp() }
                }) {
//            Divider(Modifier.padding(start = 1.dp).width(allWidth.value - 1.dp).height(1.dp))

                Row(
                    modifier = Modifier.onGloballyPositioned { layoutCoordinates ->
                        allWidth.value =
                            with(density) { layoutCoordinates.size.width.toDp() + lP / 4 }
                    }, //.horizontalScroll(hhScrollState)
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Spacer(Modifier.width(lP))
                    columnNames.forEach { columnName ->

                        val isChecked = remember { mutableStateOf(false) }


                        Box(
                            modifier = if (!isChecked.value) Modifier.width(IntrinsicSize.Min) else Modifier.width(
                                widths[columnName]!!
                            ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = columnName,
                                fontWeight = FontWeight.ExtraBold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.widthIn(max = 200.dp)
                                    .onGloballyPositioned {
                                        val width = with(density) { it.size.height.toDp() }
                                        if (width > widths[columnName]!!) widths[columnName] =
                                            (width)
                                        else isChecked.value = true

                                    },
                                onTextLayout = {
                                    if (it.hasVisualOverflow) {
                                        widths[columnName] = widths[columnName]!! + 15.dp
                                    }
                                },
                                overflow = TextOverflow.Ellipsis,
                                softWrap = false
                            )
                        }
                    }
                }


                HorizontalDivider(
                    Modifier.padding(start = 1.dp).width(allWidth.value - 1.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = .4f),
                    thickness = 1.dp
                )
                LazyColumn(
                    modifier = Modifier,
                    state = vScrollState,

                    ) {
                    itemsIndexed(items = rows) { index, row ->
                        Column {
                            Text(
                                text = row.first,
                                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .padding(start = 10.dp)
                                    .offset(with(density) { hScrollState.value.toDp() })
                            )
                            Row {
                                Box(
                                    Modifier.width(lP),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    IconButton(
                                        onClick = { onEditClick(index) },
                                        modifier = Modifier.padding(top = 5.dp).size(15.dp)
                                    ) {
                                        GetAsyncIcon(
                                            RIcons.Edit,
                                            size = 10.dp
                                        )
                                    }
                                }
                                row.second.forEach { (key, value) ->
                                    val isChecked = remember { mutableStateOf(false) }
                                    Box(
                                        modifier = if (!isChecked.value) Modifier.width(
                                            IntrinsicSize.Min
                                        )
                                        else Modifier.width(widths[key]!!),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (key == "Логин") {
                                            SelectionContainer {
                                                Text(
                                                    text = value,
                                                    modifier = Modifier
                                                        .widthIn(max = 200.dp)
                                                        .onGloballyPositioned {
                                                            val width =
                                                                with(density) { it.size.height.toDp() }
                                                            if (width > widths[key]!!) widths[key] =
                                                                width
                                                            else isChecked.value = true
                                                        },
                                                )
                                            }
                                        } else {
                                            Text(
                                                text = value,
                                                modifier = Modifier
                                                    .widthIn(max = 200.dp)
                                                    .onGloballyPositioned {
                                                        val width =
                                                            with(density) { it.size.height.toDp() }
                                                        if (width > widths[key]!!) widths[key] =
                                                            width
                                                        else isChecked.value = true
                                                    },
                                            )
                                        }

                                    }
                                }
                            }
                            Spacer(Modifier.height(5.dp))
                            if (index != rows.lastIndex) {
                                HorizontalDivider(
                                    Modifier.padding(start = 1.dp).width(allWidth.value - 1.dp),
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
}


