import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.AnimatedCommonButton
import components.CustomTextButton
import components.CustomTextField
import components.GetAsyncIcon
import components.cBottomSheet.CBottomSheetStore
import components.networkInterface.NetworkState
import decomposeComponents.CBottomSheetContent
import decomposeComponents.listDialogComponent.customConnection
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import qr.QRComponent
import qr.QRStore
import qr.isCameraAvailable
import resources.RIcons
import server.twoNums
import view.LocalViewManager
import view.esp

@Composable
expect fun QRContent(component: QRComponent, snackBarHostState: SnackbarHostState)

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun QRContentActual(component: QRComponent) {
    val model by component.model.subscribeAsState()
    val nModel by component.nInterface.networkModel.subscribeAsState()
    val viewManager = LocalViewManager.current
    val coroutineScope = rememberCoroutineScope()

    val snackBarHostState by remember { mutableStateOf(SnackbarHostState()) }

    DisposableEffect(nModel.error) {
        onDispose {
            if (nModel.state == NetworkState.Error) {
                coroutineScope.launch {
                    snackBarHostState.showSnackbar(message = "Что-то пошло не так")
                }
                component.onEvent(QRStore.Intent.GoToNone)
            } else {
                //snackBarHostState.currentSnackbarData?.dismiss()
            }
        }

    }

    Box(Modifier.imePadding().verticalScroll(rememberScrollState())) {
        Box(
            Modifier.size(
                width = viewManager.size!!.maxWidth,
                height = viewManager.size!!.maxHeight
            )
        ) {
            if (isCameraAvailable()) {
                QRContent(component, snackBarHostState)
            } else {
                Box(
                    Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Нет доступа к камере")
                        Spacer(Modifier.height(15.dp))
                        CustomTextField(
                            value = model.code,
                            onValueChange = {
                                component.onEvent(
                                    QRStore.Intent.ChangeCode(it)
                                )
                            },
                            isMoveUpLocked = true,
                            autoCorrect = false,
                            keyboardType = KeyboardType.Text,
                            onEnterClicked = {
                                component.onEvent(
                                    QRStore.Intent.SendToServer
                                )
                            },
                            isEnabled = true,
                            text = "Код"
                        )
                    }

                }
            }
            SnackbarHost(
                hostState = snackBarHostState,
                snackbar = {
                    Snackbar(
                        it
                    )
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }

    IconButton(onClick = {
        component.onOutput(QRComponent.Output.Back)
    }, modifier = Modifier.padding(top = viewManager.topPadding, start = 20.dp)) {
        GetAsyncIcon(
            path = RIcons.ChevronLeft
        )
    }
    CBottomSheetContent(
        component = component.authBottomSheet
    ) {
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(70.dp).clip(CircleShape).background(
                    brush = Brush.verticalGradient(
                        colors = if (viewManager.isDark.value) listOf(
                            MaterialTheme.colorScheme.secondary,
                            MaterialTheme.colorScheme.primaryContainer
                        ) else listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.secondary
                        ),
                        tileMode = TileMode.Decal
                    )
                ),
                contentAlignment = Alignment.Center
            ) {
                GetAsyncIcon(
                    path = getDeviceIcon(
                        deviceType = model.deviceType,
                        deviceName = model.deviceName ?: ""
                    ),
                    contentDescription = "PlatformIcon",
                    size = 40.dp
                )
            }
            Spacer(Modifier.height(7.dp))
            Text(model.deviceName)
            Spacer(Modifier.height(7.dp))
            AnimatedCommonButton(
                text = "Войти на этом устройстве",
                isEnabled = nModel.state == NetworkState.None
            ) {
                component.onEvent(QRStore.Intent.SendToServerAtAll)
            }
            Spacer(Modifier.height(25.dp))
        }
    }



    createUserSheet(
        component = component,
        model = model
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun createUserSheet(
    component: QRComponent,
    model: QRStore.State
) {
    val cNModel = component.registerBottomSheet.nModel.subscribeAsState()
    val isCreatingInProcess = (cNModel.value.state == NetworkState.Loading)
    val lazyList = rememberLazyListState()
    CBottomSheetContent(
        component = component.registerBottomSheet,
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
                val properties = listOf(
                    model.cName,
                    model.cSurname,
                    model.cPraname,
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
                            append("${model.formName} ")
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
                                component.onEvent(QRStore.Intent.ChangeCSurname(it))
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
                                component.onEvent(QRStore.Intent.ChangeCName(it))
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
                                component.onEvent(QRStore.Intent.ChangeCPraname(it))
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
                                    component.onEvent(QRStore.Intent.ChangeCBirthday(it))
                                }
                            },
                            text = "Дата рождения",
                            isEnabled = !isCreatingInProcess,
                            onEnterClicked = {
                                focusManager.moveFocus(FocusDirection.Down)
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
                                            QRStore.Intent.ChangeDateDialogShowing(
                                                true
                                            )
                                        )
                                    },
                                    enabled = !isCreatingInProcess
                                ) {
                                    GetAsyncIcon(RIcons.Calendar)
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
                                        QRStore.Intent.ChangeDateDialogShowing(
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
                                                QRStore.Intent.ChangeCBirthday(
                                                    "${date.dayOfMonth.twoNums()}${date.monthNumber.twoNums()}${date.year}"
                                                )
                                            )
                                            component.onEvent(
                                                QRStore.Intent.ChangeDateDialogShowing(
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
                                            QRStore.Intent.ChangeDateDialogShowing(
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
                        CustomTextField(
                            value = model.cParentFirstFIO,
                            onValueChange = {
                                component.onEvent(QRStore.Intent.ChangeCParentFirstFIO(it))
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
                                component.onEvent(QRStore.Intent.ChangeCParentSecondFIO(it))
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

                        AnimatedCommonButton(
                            text = "Создать",
                            modifier = Modifier.width(TextFieldDefaults.MinWidth),
                            isEnabled = num == 4
                        ) {
                            component.onEvent(QRStore.Intent.SendToServerAtAll)
                        }
                        Spacer(Modifier.height(10.dp))
                    }
                }
            }
        } else {
            Column(
                Modifier.padding(10.dp).fillMaxWidth()
                    .height(200.dp),
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
                        fontSize = 19.esp,
                        textAlign = TextAlign.Center
                    )
                }

                Button(
                    onClick = {
                        component.registerBottomSheet.onEvent(CBottomSheetStore.Intent.HideSheet)
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