import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.*
import components.cAlertDialog.CAlertDialogStore
import components.listDialog.ListDialogStore
import components.networkInterface.NetworkState
import decomposeComponents.CAlertDialogContent
import decomposeComponents.listDialogComponent.ListDialogDesktopContent
import decomposeComponents.listDialogComponent.ListDialogMobileContent
import forks.colorPicker.toHex
import forks.splitPane.ExperimentalSplitPaneApi
import forks.splitPane.HorizontalSplitPane
import forks.splitPane.dSplitter
import resources.RIcons
import view.*


@ExperimentalSplitPaneApi
@ExperimentalMaterial3Api
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsContent(
    isExpanded: Boolean,
    settingsComponent: SettingsComponent,
    isVisible: Boolean
//    secondScreen: @Composable () -> Unit
) {
    val viewManager = LocalViewManager.current
    if (isExpanded) {
        HorizontalSplitPane(
            splitPaneState = viewManager.splitPaneState
        ) {
            first(minSize = 400.dp) {
                SettingsView(settingsComponent, viewManager)
            }
            dSplitter()
            second(minSize = 500.dp) {
                Column(
                    Modifier.fillMaxSize().padding(50.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Предпросмотр",
                        fontWeight = FontWeight.Black,
                        fontSize = MaterialTheme.typography.titleLarge.fontSize
                    )
                    Spacer(Modifier.height(20.dp))
                    ThemePreview()
                }
            }
        }
    } else {
        SettingsView(settingsComponent, viewManager)
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SettingsView(
    component: SettingsComponent,
    viewManager: ViewManager
) {


    val isHazeNeedToUpdate = remember { mutableStateOf(false) }
    val isHaze = remember { mutableStateOf(viewManager.hazeHardware.value) }

    val focusManager = LocalFocusManager.current

    if (isHazeNeedToUpdate.value) {
        changeOnHaze(viewManager = viewManager)
        isHazeNeedToUpdate.value = false
    }

    val isColorMenuOpened = remember { mutableStateOf(false) }

    val model by component.model.subscribeAsState()
    val nDevicesModel by component.nDevicesInterface.networkModel.subscribeAsState()

    if (model.newColorMode != null) {
        changeColorMode(viewManager, model.newColorMode ?: viewManager.colorMode.value)
        component.onEvent(SettingsStore.Intent.ChangeColorMode(null))
    }

    val colorRed = if (viewManager.isDark.value) Color(255, 99, 71) else Color.Red
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AppBar(
                title = {
                    Text(
                        "Настройки",

                        fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationRow = {
                    IconButton(
                        onClick = { component.onOutput(SettingsComponent.Output.Back) }
                    ) {
                        GetAsyncIcon(
                            path = RIcons.ChevronLeft
                        )
                    }
                },
                actionRow = {
                    Text(
                        text = applicationVersionString,
                        modifier = Modifier.padding(end = 10.dp).alpha(.5f),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Black,
                        fontSize = 10.esp
                    )
                }
            )
        }
    ) { padding ->
        Box(Modifier) { //.hazeUnder(viewManager, hazeState).padding(horizontal = 15.dp)
            CLazyColumn(
                padding = padding
            ) {
                item {
                    Column(
                        Modifier.fillMaxWidth()
                            .padding(top = (8 * 5).dp, bottom = (8 * 6).dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AnimatedContent(
                            model.secondLogin ?: model.login,
                            transitionSpec = { fadeIn().togetherWith(fadeOut()) }) {
                            Text(
                                it,
                                fontWeight = FontWeight.Black,
                                fontSize = 30.esp,
                                textAlign = TextAlign.Center
                            )
                        }
                        CustomTextButton(
                            text = "Сменить логин",
                            modifier = Modifier.handy()
                        ) {
                            component.changeLoginDialog.onEvent(CAlertDialogStore.Intent.ShowDialog)
                        }
                    }
                }

                item {
                    SettingsBlock(
                        header = "Персонализация",
                        headerContent = {
                            IconButton(
                                onClick = {
                                    isColorMenuOpened.value = !isColorMenuOpened.value
                                },
                                modifier = Modifier.size(25.dp)
                            ) {
                                GetAsyncIcon(
                                    RIcons.BigBrush
                                )
                            }
                        }
                    ) {
                        Spacer(Modifier.height(7.dp))
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Цветовой режим",
                                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                fontWeight = FontWeight.SemiBold
                            )
                            Box() {
                                CustomTextButton(
                                    text = colorModes[viewManager.colorMode.value].toString(),
                                    maxLines = 1
                                ) {
                                    component.colorModeListComponent.onEvent(ListDialogStore.Intent.ShowDialog)
                                }
                                ListDialogDesktopContent(
                                    component = component.colorModeListComponent,
                                    isFullHeight = true
                                )
                            }
                        }
                        Spacer(Modifier.height(10.dp))
                        SettingsSwitchRow(
                            text = "Прозрачность элементов",
                            checked = isHaze.value
                        ) {
                            isHaze.value = it
                            if (it) {
                                isHazeNeedToUpdate.value = true
                            } else {
                                changeOffHaze(viewManager)
                            }
                        }
                        Spacer(Modifier.height(15.dp))
                        SettingsSwitchRow(
                            text = "Анимированные переходы",
                            checked = viewManager.isTransitionsEnabled.value
                        ) {
                            changeIsTransitionsEnabled(viewManager, it)
                        }
                        Spacer(Modifier.height(15.dp))
                        SettingsSwitchRow(
                            text = "Отображать аватарки",
                            checked = viewManager.showAvatars.value
                        ) {
                            changeAvatarsShow(viewManager, it)
                        }
                        Spacer(Modifier.height(5.dp))
                        Text(
                            text = "Аватар будет отображаться только на главном экране",
                            modifier = Modifier.fillMaxWidth().alpha(.5f),
                            textAlign = TextAlign.Center,
                            fontSize = 10.esp,
                            lineHeight = 10.esp
                        )
                        Spacer(Modifier.height(8.dp))
                        SettingsSwitchRow(
                            text = "Amoled (beta)",
                            checked = viewManager.isAmoled.value
                        ) {
                            changeAmoled(viewManager, it)
                        }
                        Spacer(Modifier.height(15.dp))
                        SettingsSwitchRow(
                            text = "Кнопка \"Обновить\"",
                            checked = viewManager.isRefreshButtons.value
                        ) {
                            changeIsRefreshButtons(viewManager, it)
                        }
                        Spacer(Modifier.height(5.dp))
                        Text(
                            text = "Нажимайте F5 или тяните вниз, чтобы обновить",
                            modifier = Modifier.fillMaxWidth().alpha(.5f),
                            textAlign = TextAlign.Center,
                            fontSize = 10.esp,
                            lineHeight = 10.esp
                        )
                    }
                }

                item {
                    Spacer(Modifier.height(10.dp))
                }

                item {
                    CustomTextField(
                        value = viewManager.hardwareStatus.value,
                        onValueChange = {
                            changeHardwareStatus(viewManager, it)
                                        },
                        text = "Статус",
                        supText = "Только на вашем устройстве",
                        isEnabled = true,
                        imeAction = ImeAction.Done,
                        isMoveUpLocked = true,
                        autoCorrect = true,
                        keyboardType = KeyboardType.Text,
                        modifier = Modifier.fillMaxWidth(),
                        onEnterClicked = {
                            focusManager.clearFocus(true)
                        }
                    )
                }

                item {
                    Spacer(Modifier.height(20.dp))
                }

                item {
                    SettingsBlock(
                        header = "Шрифт и его размер",
                        headerContent = {
                            Box {
                                val fontName = when (viewManager.fontType.value) {
                                    FontTypes.Geologica.ordinal -> "Geologica"
                                    FontTypes.Default.ordinal -> "Обычный"
                                    FontTypes.Monospace.ordinal -> "Monospace"
                                    FontTypes.SansSerif.ordinal -> "SansSerif"
                                    else -> "???"
                                }
                                AnimatedContent(fontName) { aText ->
                                    CustomTextButton(
                                        text = aText,
                                        maxLines = 1
                                    ) {
                                        component.fontTypeListComponent.onEvent(ListDialogStore.Intent.ShowDialog)
                                    }
                                }


                                ListDialogDesktopContent(
                                    component = component.fontTypeListComponent,
                                    isFullHeight = true,
                                    offset = DpOffset(x = 40.dp, y = 0.dp)
                                ) {
                                    changeFontType(
                                        viewManager = viewManager,
                                        fontType = it.id.toInt()
                                    )
                                    component.fontTypeListComponent.onEvent(ListDialogStore.Intent.HideDialog)
                                }
                            }
                        }
                    ) {
                        Slider(
                            value = viewManager.fontSize.value,
                            onValueChange = {
                                changeFontSize(
                                    viewManager,
                                    fontSize = it
                                )
                            },
                            valueRange = 0.75f..1.25f,
                            steps = 9
                        )
                        Text(
                            text = "Возможны визуальные баги",
                            modifier = Modifier.fillMaxWidth().alpha(.5f),
                            textAlign = TextAlign.Center,
                            fontSize = 10.esp
                        )
                    }
                }

                item {
                    Spacer(Modifier.height(10.dp))
                }

                item {
                    SettingsBlock(
                        header = "Таблицы"
                    ) {
                        Spacer(Modifier.height(7.dp))
                        SettingsSwitchRow(
                            text = "Использовать по умолчанию",
                            checked = model.isMarkTableDefault
                        ) {
                            component.onEvent(SettingsStore.Intent.ChangeIsMarkTableDefault)
                        }
                        Spacer(Modifier.height(16.dp))
                        SettingsSwitchRow(
                            text = "Отображать +1 за МВД",
                            checked = model.isPlusDsStupsEnabled
                        ) {
                            component.onEvent(SettingsStore.Intent.ChangeIsPlusDsStupsEnabled)
                        }
                    }
                }

                item {
                    Spacer(Modifier.height(14.dp))
                }

                item {
                    SettingsBlock(
                        header = "Устройства",
                        headerContent = {
                            IconButton(
                                onClick = { component.onOutput(SettingsComponent.Output.GoToScanner) }
                            ) {
                                GetAsyncIcon(
                                    path = RIcons.Qr
                                )
                            }
                            AnimatedVisibility(nDevicesModel.state == NetworkState.Loading) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp))
                            }
                            AnimatedVisibility(nDevicesModel.state == NetworkState.Error) {
                                IconButton(
                                    onClick = {
                                        nDevicesModel.onFixErrorClick.invoke()
                                    }
                                ) {
                                    GetAsyncIcon(
                                        path = RIcons.Repeat
                                    )
                                }
                            }
                        }
                    ) {
                        Spacer(Modifier.height(7.dp))
                        AnimatedVisibility(model.deviceList.isNotEmpty()) {
                            Column {
                                model.deviceList.forEach { device ->
                                    Surface(
                                        Modifier.fillMaxWidth(),
                                        tonalElevation = 4.dp,
                                        shape = RoundedCornerShape(15.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth()
                                                .padding(vertical = 4.dp, horizontal = 6.dp),
                                            //                                    horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Spacer(Modifier.width(5.dp))
                                            GetAsyncIcon(
                                                path = getDeviceIcon(
                                                    deviceType = device.deviceType,
                                                    deviceName = device.deviceName ?: ""
                                                ),
                                                contentDescription = "PlatformIcon",
                                                size = 30.dp
                                            )
                                            Spacer(Modifier.width(15.dp))
                                            Column() {
                                                Text(
                                                    device.deviceName ?: "Неизвестное устройство",
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontSize = 16.esp,
                                                    lineHeight = 16.esp
                                                )
                                                Row(
                                                    Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    val time = remember {
                                                        val globalParts = device.time.split("T")
                                                        val partDays = globalParts[0].split("-")
                                                        val partTime = globalParts[1].split(":")
                                                        val days = partDays[2]
                                                        val month = partDays[1]
                                                        val year = partDays[0]

                                                        val hours = partTime[0]
                                                        val minutes = partTime[1]
                                                        "$days.$month.$year ($hours:$minutes)"
                                                    }
                                                    Text(
                                                        time,
                                                        fontSize = 13.esp,
                                                        lineHeight = 13.esp,
                                                        color = MaterialTheme.colorScheme.onBackground.copy(
                                                            alpha = .5f
                                                        )
                                                    )
                                                    CustomTextButton(
                                                        text = if (device.isThisSession) "Вы" else "Удалить",
                                                        fontSize = 13.esp
                                                    ) {
                                                        if (!device.isThisSession) {
                                                            component.onEvent(
                                                                SettingsStore.Intent.TerminateDevice(
                                                                    device.deviceId
                                                                )
                                                            )
                                                        }
                                                    }
                                                }
                                            }

                                        }
                                    }
                                    Spacer(Modifier.height(5.dp))
                                }
                            }
                        }
                    }
                }
                item {
                    Spacer(Modifier.height(25.dp))

                    Box(
                        Modifier.fillMaxWidth().padding(end = (7.5).dp)/*.padding(start = 10.dp)*/,
                        contentAlignment = Alignment.Center
                    ) {
                        TextButton(
                            onClick = {
                                component.quitDialogComponent.onEvent(CAlertDialogStore.Intent.ShowDialog)
                            },
                            colors = ButtonDefaults.textButtonColors(
                                containerColor = Color.Transparent,
                                contentColor = colorRed
                            ),
                            contentPadding = PaddingValues(horizontal = 15.dp)
                        ) {
                            GetAsyncIcon(
                                path = RIcons.Logout,
                                tint = colorRed,
                                size = 25.dp
                            )
                            Spacer(Modifier.width(5.dp))
                            Text(
                                "Выйти из аккаунта",
                                fontWeight = FontWeight.Bold,
                                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                color = colorRed
                            )
                        }
                    }
                    Spacer(Modifier.height(50.dp))
                }
            }
            BottomThemePanel(
                viewManager,
                onThemeClick = {
                    changeTint(viewManager, it)
                },
                isColorMenuOpened = isColorMenuOpened,
                isShowBottomBar = false
            ) {
                changeColorSeed(viewManager, it.toHex())
            }

        }
        CAlertDialogContent(
            component = component.quitDialogComponent,
            isCustomButtons = false,
            title = "Выход",
            acceptColor = colorRed,
            acceptText = "Выйти",
            declineText = "Остаться"
        ) {
            Text(
                text = "Вы уверены, что хотите\nвыйти из аккаунта?",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

        ListDialogMobileContent(
            component = component.colorModeListComponent,
            title = "Цветовой режим"
        )

        ListDialogMobileContent(
            component = component.fontTypeListComponent,
            title = "Выберите шрифт"
        ) {
            changeFontType(
                viewManager = viewManager,
                fontType = it.id.toInt()
            )
            component.fontTypeListComponent.onEvent(ListDialogStore.Intent.HideDialog)
        }


        CAlertDialogContent(
            component = component.changeLoginDialog
        ) {
            val loginNModel by component.changeLoginDialog.nModel.subscribeAsState()
            val isButtonEnabled =
                (loginNModel.state == NetworkState.None) && (model.secondLogin != model.eSecondLogin)
            Column(Modifier.padding(6.dp)) {
                Text(
                    "Смена логина", fontWeight = FontWeight.Bold,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize, modifier = Modifier.padding(start = 5.dp)
                )
                Spacer(Modifier.height(5.dp))
                CustomTextField(
                    value = model.eSecondLogin,
                    onValueChange = {
                        component.onEvent(
                            SettingsStore.Intent.ESecondLogin(
                                it
                            )
                        )
                    },
                    text = "Новый логин",
                    supText = "Кириллицу можно! макс 30",
                    isEnabled = loginNModel.state == NetworkState.None,
                    imeAction = ImeAction.Done,
                    onEnterClicked = {
                        if (isButtonEnabled) {
                            component.onEvent(SettingsStore.Intent.SaveSecondLogin)
                        }
                    },
                    isMoveUpLocked = true,
                    autoCorrect = false,
                    keyboardType = KeyboardType.Text
                )

                Spacer(Modifier.height(7.dp))
                AnimatedCommonButton(
                    text = "Сменить",
                    isEnabled = isButtonEnabled,
                    modifier = Modifier.width(TextFieldDefaults.MinWidth)
                ) {
                    if (isButtonEnabled) {
                        component.onEvent(SettingsStore.Intent.SaveSecondLogin)
                    }
                }
            }

        }
    }
}

@Composable
private fun SettingsBlock(
    header: String,
    headerContent: @Composable () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    Column(Modifier.fillMaxWidth()) {
        SettingsRow(header, isHeader = true) { headerContent() }
        this.content()
    }
}

@Composable
private fun SettingsRow(
    text: String,
    isHeader: Boolean,
    endContent: @Composable () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text,
            fontSize = if (isHeader) 23.esp else MaterialTheme.typography.titleMedium.fontSize,
            fontWeight = if (isHeader) FontWeight.Black else FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(.9f, false)
        )
        endContent()
    }
}

@Composable
private fun SettingsSwitchRow(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    SettingsRow(
        text,
        isHeader = false
    ) {
        Switch(
            checked = checked,
            onCheckedChange = { onCheckedChange(it) },
            modifier = Modifier.height(20.dp)//.scale(.7f).offset(y = (-0.05).dp)
        )
    }
}

