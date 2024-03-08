@file:OptIn(ExperimentalFoundationApi::class)

import admin.User
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Switch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
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
import components.AnimatedCommonButton
import components.AppBar
import components.CustomTextButton
import components.CustomTextField
import components.LoadingAnimation
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import pullRefresh.PullRefreshIndicator
import pullRefresh.pullRefresh
import pullRefresh.rememberPullRefreshState
import server.IsParentStatus
import server.Roles
import server.twoNums
import users.UsersComponent
import users.UsersStore
import view.LocalViewManager
import view.rememberImeState

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class,
    ExperimentalFoundationApi::class,
)
@ExperimentalLayoutApi
@Composable
fun UsersContent(
    component: UsersComponent
) {
    val model by component.model.subscribeAsState()
    val viewManager = LocalViewManager.current
//    val scrollState = rememberScrollState()
    val imeState = rememberImeState()
    val lazyListState = rememberLazyListState()

    val refreshState = rememberPullRefreshState(
        model.isInProcess && model.users != null,
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
                        onClick = { component.onOutput(UsersComponent.Output.BackToAdmin) }
                    ) {
                        Icon(
                            Icons.Rounded.ArrowBackIosNew, null
                        )
                    }
                },
                title = {
                    Text(
                        "Пользователи",

                        fontSize = 25.sp,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                actionRow = {
                    if (model.users != null) {
                        IconButton(
                            onClick = { component.onEvent(UsersStore.Intent.FetchUsers) }
                        ) {
                            Icon(
                                Icons.Filled.Refresh, null
                            )
                        }
                        IconButton(
                            onClick = {
                                component.onEvent(
                                    UsersStore.Intent.ChangeCreatingSheetShowing(
                                        true
                                    )
                                )
                            }
                        ) {
                            Icon(
                                Icons.Rounded.Add, null
                            )
                        }
                    }
                }
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

        Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            if (model.users != null) {
                TableScreen(
                    columnNames,
                    widthsInit = widthsInit,
                    model.users!!.map {
                        Pair(
                            "${it.surname} ${it.name}",
                            mapOf(
                                columnNames[0] to (it.praname ?: "--"),
                                columnNames[1] to it.login,
                                columnNames[2] to if (it.password != null) "есть" else "нет",
                                columnNames[3] to try {
                                    it.birthday!!.substring(0, 2) +
                                            "." + it.birthday!!.substring(2, 4) +
                                            "." + it.birthday!!.substring(4)
                                } catch (_: Throwable) {
                                    "null"
                                },
                                columnNames[4] to when (it.role) {
                                    Roles.teacher -> "учитель"
                                    Roles.student -> "ученик"
                                    else -> "другое"
                                },
                                columnNames[5] to it.moderation,
                                columnNames[6] to if(it.isParent) "да" else "нет"
                            )
                        )

                    },
                    true,
                    onEditClick = {

                        val userForEditing = User(
                            login = model.users!![it].login,
                            password = model.users!![it].password,
                            name = model.users!![it].name,
                            surname = model.users!![it].surname,
                            praname = model.users!![it].praname,
                            birthday = model.users!![it].birthday,
                            role = model.users!![it].role,
                            moderation = model.users!![it].moderation,
                            isParent = model.users!![it].isParent,
                            avatarId = model.users!![it].avatarId
                        )
                        component.onEvent(UsersStore.Intent.OpenEditingSheet(userForEditing))

                    },
                    modifier = Modifier.padding(8.dp).pullRefresh(refreshState)
                )
            } else {
                if (model.isInProcess) {
                    LoadingAnimation()
                } else if (model.isAccessDenied) {
                    Text("Доступ запрещён", fontWeight = FontWeight.Bold)
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Не удалось загрузить список", fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(5.dp))
                        CustomTextButton("Попробовать ещё раз") {
                            component.onEvent(UsersStore.Intent.FetchUsersInit)
                        }
                    }
                }
            }

            PullRefreshIndicator(
                modifier = Modifier.align(alignment = Alignment.TopCenter),
                refreshing = model.isInProcess && model.users != null,
                state = refreshState,
            )
            if (model.isCreatingSheetShowing) {
                val modalBottomSheetState = rememberModalBottomSheetState(
                    skipPartiallyExpanded = true
                )

//                LaunchedEffect(modalBottomSheetState.isVisible) {
//                    if (modalBottomSheetState.isVisible) {
//                        modalBottomSheetState.expand()
//                    }
//                }

                ModalBottomSheet(
                    onDismissRequest = {
                        component.onEvent(UsersStore.Intent.ChangeCreatingSheetShowing(false))
                    },

                    sheetState = modalBottomSheetState,
                    windowInsets = WindowInsets.ime
                ) {
                    val focusManager = LocalFocusManager.current
//                    Surface(
//                        modifier = Modifier
//                            .wrapContentWidth()
//                            .wrapContentHeight(),
//                        shape = MaterialTheme.shapes.large
//                    ) {
                    var num = 0
                    if (model.cLogin.isBlank() && model.cError.isBlank()) {
                        Column(
                            Modifier.padding(10.dp).fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            var expandedRoles by remember { mutableStateOf(false) }
                            val properties = listOf(
                                model.cName,
                                model.cSurname,
                                model.cPraname,
                                model.cRole,
                            )
                            num = properties.count { (it ?:"").isNotBlank() }
                            if (model.cBirthday.length == 8) num++
                            Text(
                                buildAnnotatedString {
                                    withStyle(
                                        SpanStyle(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 20.sp
                                        )
                                    ) {
                                        append("Создать нового пользователя ")
                                    }
                                    withStyle(
                                        SpanStyle(
                                            fontSize = 15.sp,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    ) {
                                        append("$num/5")
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
                                    value = model.cSurname,
                                    onValueChange = {
                                        component.onEvent(UsersStore.Intent.ChangeCSurname(it))
                                    },
                                    text = "Фамилия",
                                    isEnabled = !model.isCreatingInProcess,
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
                                    isEnabled = !model.isCreatingInProcess,
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
                                    isEnabled = !model.isCreatingInProcess,
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
                                    isEnabled = !model.isCreatingInProcess,
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
                                            enabled = !model.isCreatingInProcess
                                        ) {
                                            Icon(
                                                Icons.Rounded.CalendarToday,
                                                null
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
                                                            .toLocalDateTime(TimeZone.of("Europe/Moscow"))
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
                                            .menuAnchor(), // menuAnchor modifier must be passed to the text field for correctness.
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
                                            ExposedDropdownMenuDefaults.TrailingIcon(
                                                expanded = expandedRoles
                                            )
                                        },
                                        shape = RoundedCornerShape(15.dp),
                                        enabled = !model.isCreatingInProcess
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
                                Row(
                                    Modifier.width(TextFieldDefaults.MinWidth)
                                        .padding(horizontal = 7.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "Модератор",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp,
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
                                        enabled = !model.isCreatingInProcess
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
                                        fontSize = 20.sp,
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
                                        enabled = !model.isCreatingInProcess
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
                                        fontSize = 20.sp,
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
                                        enabled = !model.isCreatingInProcess
                                    )
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
                    } else {
                        Column(
                            Modifier.padding(10.dp).fillMaxWidth().height(200.dp),
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (model.cError.isNotBlank() && model.cLogin.isBlank()) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(model.cError)
                                    Spacer(Modifier.height(7.dp))
                                    CustomTextButton("Попробовать ещё раз") {
                                        component.onEvent(UsersStore.Intent.TryCreateAgain)
                                    }
                                }
                            } else {
                                Text(
                                    "${model.cSurname} ${model.cName} ${model.cPraname}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    textAlign = TextAlign.Center
                                )

                                Text(
                                    model.cLogin,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 28.sp,
                                    textAlign = TextAlign.Center
                                )

                                Button(
                                    onClick = {
                                        component.onEvent(
                                            UsersStore.Intent.ChangeCreatingSheetShowing(
                                                false
                                            )
                                        )
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
            }

            if (model.isEditingSheetShowing) {
                val modalBottomSheetState = rememberModalBottomSheetState(
                    skipPartiallyExpanded = true
                )

//                LaunchedEffect(modalBottomSheetState.isVisible) {
//                    if (modalBottomSheetState.isVisible) {
//                        modalBottomSheetState.expand()
//                    }
//                }

                ModalBottomSheet(
                    onDismissRequest = {
                        component.onEvent(UsersStore.Intent.ChangeEditingSheetShowing(false))
                    },
                    sheetState = modalBottomSheetState,
                    windowInsets = WindowInsets.ime
                ) {
                    val focusManager = LocalFocusManager.current
//                    Surface(
//                        modifier = Modifier
//                            .wrapContentWidth()
//                            .wrapContentHeight(),
//                        shape = MaterialTheme.shapes.large
//                    ) {
                    var num = 0
                    Box(contentAlignment = Alignment.Center) {
                        Column(
                            Modifier.padding(horizontal = 10.dp).padding(bottom = 10.dp)
                                .fillMaxWidth().alpha(if(model.eError.isNotBlank()) 0.4f else 1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            var expandedRoles by remember { mutableStateOf(false) }

                            Text(
                                model.eLogin, fontWeight = FontWeight.Black,
                                fontSize = 27.sp, textAlign = TextAlign.Center
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
                                            fontSize = 20.sp
                                        )
                                    ) {
                                        append("Редактировать пользователя ")
                                    }
                                    withStyle(
                                        SpanStyle(
                                            fontSize = 15.sp,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    ) {
                                        append("$num/5")
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
                                    value = model.eSurname,
                                    onValueChange = {
                                        component.onEvent(UsersStore.Intent.ChangeESurname(it))
                                    },
                                    text = "Фамилия",
                                    isEnabled = !model.isEditingInProcess,
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
                                    isEnabled = !model.isEditingInProcess,
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
                                    isEnabled = !model.isEditingInProcess,
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
                                    isEnabled = !model.isEditingInProcess,
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
                                            enabled = !model.isEditingInProcess
                                        ) {
                                            Icon(
                                                Icons.Rounded.CalendarToday,
                                                null
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
                                                            .toLocalDateTime(TimeZone.of("Europe/Moscow"))
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
                                            .menuAnchor(), // menuAnchor modifier must be passed to the text field for correctness.
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
                                            ExposedDropdownMenuDefaults.TrailingIcon(
                                                expanded = expandedRoles
                                            )
                                        },
                                        shape = RoundedCornerShape(15.dp),
                                        enabled = !model.isCreatingInProcess
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
                                Row(
                                    Modifier.width(TextFieldDefaults.MinWidth)
                                        .padding(horizontal = 7.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "Модератор",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp,
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
                                        enabled = !model.isEditingInProcess
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
                                        fontSize = 20.sp,
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
                                        enabled = !model.isCreatingInProcess
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
                                        fontSize = 20.sp,
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
                                        enabled = !model.isEditingInProcess
                                    )
                                }
                                Spacer(Modifier.height(7.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (model.eIsPassword) {
                                        CustomTextButton(
                                            "Сбросить пароль",
                                            color = if (!model.isEditingInProcess) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(
                                                alpha = 0.3f
                                            )
                                        ) {
                                            component.onEvent(UsersStore.Intent.ClearPassword)
                                        }
                                        Spacer(Modifier.width(12.dp))
                                    }
                                    AnimatedCommonButton(
                                        text = "Редактировать",
                                        isEnabled = num == 5,
                                        modifier = if(!model.eIsPassword) Modifier.width(TextFieldDefaults.MinWidth) else Modifier
                                    ) {
                                        component.onEvent(UsersStore.Intent.EditUser)
                                    }
                                }
                                Spacer(Modifier.height(10.dp))

                            }
                        }


                        if (model.eError.isNotBlank()) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(model.eError)
                                Spacer(Modifier.height(7.dp))
                                CustomTextButton("Попробовать ещё раз") {
                                    component.onEvent(UsersStore.Intent.TryEditUserAgain)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}






