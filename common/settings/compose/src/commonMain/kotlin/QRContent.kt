import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Android
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Computer
import androidx.compose.material.icons.rounded.DeviceUnknown
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.PhoneIphone
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.AnimatedCommonButton
import components.CustomTextField
import components.networkInterface.NetworkState
import decomposeComponents.CBottomSheetContent
import kotlinx.coroutines.launch
import qr.QRComponent
import qr.QRStore
import qr.isCameraAvailable
import server.DeviceTypex
import view.LocalViewManager
import view.ViewManager

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
                            keyboardType = KeyboardType.Number,
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
    }

    IconButton(onClick = {
        component.onOutput(QRComponent.Output.Back)
    }, modifier = Modifier.padding(top = viewManager.topPadding, start = 20.dp)) {
        Icon(Icons.Rounded.ArrowBackIosNew, null)
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
                Icon(
                    when (model.deviceType) {
                        DeviceTypex.desktop -> Icons.Rounded.Computer
                        DeviceTypex.android -> Icons.Rounded.Android
                        DeviceTypex.ios -> Icons.Rounded.PhoneIphone
                        DeviceTypex.web -> Icons.Rounded.Language
                        else -> Icons.Rounded.DeviceUnknown
                    }, "PlatformIcon",
                    modifier = Modifier.size(40.dp)
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
}