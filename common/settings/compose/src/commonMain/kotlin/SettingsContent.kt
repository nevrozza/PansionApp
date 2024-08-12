import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import forks.splitPane.dSplitter
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.Android
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Autorenew
import androidx.compose.material.icons.rounded.Computer
import androidx.compose.material.icons.rounded.DeviceUnknown
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.PhoneIphone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.AppBar
import components.BottomThemePanel
import components.CustomTextButton
import components.ThemePreview
import components.cAlertDialog.CAlertDialogStore
import components.listDialog.ListDialogStore
import components.networkInterface.NetworkState
import decomposeComponents.CAlertDialogContent
import decomposeComponents.listDialogComponent.ListDialogDesktopContent
import decomposeComponents.listDialogComponent.ListDialogMobileContent
import forks.colorPicker.toHex
import forks.splitPane.ExperimentalSplitPaneApi
import forks.splitPane.HorizontalSplitPane
import forks.splitPane.rememberSplitPaneState
import server.DeviceTypex
import view.LocalViewManager
import view.ViewManager


@ExperimentalSplitPaneApi
@ExperimentalMaterial3Api
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsContent(
    isExpanded: Boolean,
    settingsComponent: SettingsComponent
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
                    Text("Предпросмотр", fontWeight = FontWeight.Black, fontSize = 20.sp)
                    Spacer(Modifier.height(20.dp))
                    ThemePreview()
                }
            }
        }
    } else {
        SettingsView(settingsComponent, viewManager)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsView(
    component: SettingsComponent,
    viewManager: ViewManager
) {


    val isHazeNeedToUpdate = remember { mutableStateOf(false) }
    val isHaze = remember { mutableStateOf(viewManager.hazeStyle?.value != null) }


    if (isHazeNeedToUpdate.value) {
        changeOnHaze(viewManager = viewManager)
        isHazeNeedToUpdate.value = false
    }


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
                navigationRow = {
                    IconButton(
                        onClick = { component.onOutput(SettingsComponent.Output.Back) }
                    ) {
                        Icon(
                            Icons.Rounded.ArrowBackIosNew, null
                        )
                    }
                },
                title = {
                    Text(
                        "Настройки",

                        fontSize = 25.sp,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
        }
    ) { padding ->
        Box(Modifier.padding(horizontal = 15.dp)) {
            Column(
                Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(padding)
                    .imePadding()
            ) {
                Text(
                    model.login,
                    fontWeight = FontWeight.Black,
                    fontSize = 30.sp,
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = (8 * 5).dp, bottom = (8 * 6).dp),
                    textAlign = TextAlign.Center
                )
                Text("Персонализация", fontSize = 23.sp, fontWeight = FontWeight.Black)
                Spacer(Modifier.height(7.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Цветовой режим",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Box() {
                        CustomTextButton(
                            text = colorModes[viewManager.colorMode.value].toString()
                        ) {
                            component.colorModeListComponent.onEvent(ListDialogStore.Intent.ShowDialog)
                        }
                        ListDialogDesktopContent(
                            component = component.colorModeListComponent
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Прозрачность элементов",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Switch(
                        checked = isHaze.value,
                        onCheckedChange = {
                            isHaze.value = it
                            if (it) {
                                isHazeNeedToUpdate.value = true
                            } else {
                                changeOffHaze(viewManager)
                            }
                        },
                        modifier = Modifier.height(20.dp)//.scale(.7f).offset(y = (-0.05).dp)
                    )
                }

                Spacer(Modifier.height(15.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Устройства", fontSize = 23.sp, fontWeight = FontWeight.Black)
                    AnimatedVisibility(nDevicesModel.state == NetworkState.Loading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    }
                    AnimatedVisibility(nDevicesModel.state == NetworkState.Error) {
                        IconButton(
                            onClick = {
                                nDevicesModel.onFixErrorClick.invoke()
                            }
                        ) {
                            Icon(
                                Icons.Rounded.Autorenew, null
                            )
                        }
                    }
                }
                Spacer(Modifier.height(5.dp))
                AnimatedVisibility(model.deviceList.isNotEmpty()) {
                    Column {
                        model.deviceList.forEach { device ->
                            Surface(Modifier.fillMaxWidth(), tonalElevation = 4.dp, shape = RoundedCornerShape(15.dp)) {
                                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp, horizontal = 6.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            when (device.deviceType) {
                                                DeviceTypex.desktop -> Icons.Rounded.Computer
                                                DeviceTypex.android -> Icons.Rounded.Android
                                                DeviceTypex.ios -> Icons.Rounded.PhoneIphone
                                                DeviceTypex.web -> Icons.Rounded.Language
                                                else -> Icons.Rounded.DeviceUnknown
                                            }, "PlatformIcon"
                                        )
                                        Spacer(Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                device.deviceName ?: "Неизвестное устройство",
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 16.sp,
                                                lineHeight = 16.sp
                                            )
                                            Text(
                                                device.time,
                                                fontSize = 16.sp,
                                                lineHeight = 16.sp,
                                                color = MaterialTheme.colorScheme.onBackground.copy(
                                                    alpha = .5f
                                                )
                                            )
                                        }
                                        Spacer(Modifier.width(10.dp))
                                    }
                                    CustomTextButton(
                                        text = if (device.isThisSession) "Данное\nустройство" else "Завершить\nсессию"
                                    ) {
                                        if (!device.isThisSession) {
                                            component.onEvent(SettingsStore.Intent.TerminateDevice(device.deviceId))
                                        }
                                    }
                                }
                            }
                            Spacer(Modifier.height(5.dp))
                        }
                    }
                }

                Spacer(Modifier.height(50.dp))

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

                        Icon(
                            Icons.AutoMirrored.Rounded.Logout,
                            null,
                            tint = colorRed,
                            modifier = Modifier.size(25.dp)
                        )
                        Spacer(Modifier.width(5.dp))
                        Text(
                            "Выйти из аккаунта",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = colorRed
                        )
                    }
                }
                Spacer(Modifier.height(50.dp))


            }
            BottomThemePanel(
                viewManager,
                onThemeClick = {
                    changeTint(viewManager, it)
                }
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
    }
}

