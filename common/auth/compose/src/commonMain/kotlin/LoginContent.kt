import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.*
import forks.colorPicker.toHex
import io.github.alexzhirkevich.qrose.options.*
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import kotlinx.coroutines.launch
import login.LoginComponent
import login.LoginComponent.Output
import login.LoginStore.Intent
import resources.RIcons
import view.LocalViewManager
import view.bringIntoView
import view.rememberImeState

@ExperimentalFoundationApi
@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalLayoutApi
@Composable
fun LoginContent(
    component: LoginComponent
) {
    val model by component.model.subscribeAsState()

    val viewManager = LocalViewManager.current

    val coroutineScope = rememberCoroutineScope()
    val isButtonEnabled =
        !model.isInProcess && model.login.isNotBlank() && model.password.isNotBlank()

    LaunchedEffect(model.logined) {
        if (model.logined) {
            component.onOutput(Output.NavigateToMain)
        }
    }

    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    val imeState = rememberImeState()
    Scaffold(
        Modifier.fillMaxSize(),
        snackbarHost = {
            val hostState = remember { mutableStateOf(SnackbarHostState()) }
            SnackbarHost(
                hostState = hostState.value,
                snackbar = {
                    Snackbar(
                        it
                    )
                }
            )
            //Actions
            DisposableEffect(model.isErrorShown) {
                onDispose {
                    if (model.isErrorShown) {
                        coroutineScope.launch {
                            hostState.value.showSnackbar(message = model.error)
                        }
                    } else {
                        hostState.value.currentSnackbarData?.dismiss()
                    }
                }

            }
        },
        topBar = {
            CentreAppBar(
                modifier = Modifier.widthIn(max = 450.dp).padding(top = 10.dp)
                    .padding(horizontal = 5.dp),
                title = {
                    Text("Вход", fontSize = MaterialTheme.typography.titleLarge.fontSize, fontWeight = FontWeight.Black)
                },
                navigationRow = {
                    IconButton(
                        onClick = { component.onOutput(Output.BackToActivation) }
                    ) {
                        GetAsyncIcon(
                            path = RIcons.ChevronLeft
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box() {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(padding)
                    .consumeWindowInsets(padding)
                    .imePadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(Modifier)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Crossfade(model.qrToken == "", modifier = Modifier.animateContentSize()) {
                        if (it) {
                            GetAsyncIcon(
                                path = RIcons.SchoolCap,
                                size = 150.dp
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Image(
                                    rememberQrCodePainter(
                                        data = model.qrToken,
                                        shapes = QrShapes(
                                            ball = QrBallShape.roundCorners(.25f),
                                            //code = QrCodeShape.circle(),
                                            darkPixel = QrPixelShape.roundCorners(),
                                            frame = QrFrameShape.roundCorners(.25f)
                                        )
                                    ),
                                    null,
                                    Modifier.padding(bottom = 7.dp).size(150.dp),
                                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
                                )
                                Text(model.qrToken)
                                Spacer(Modifier.height(7.dp))
                            }
                        }
                    }
                    CustomTextField(
                        value = model.login,
                        onValueChange = {
                            component.onEvent(Intent.InputLogin(it))
                        },
                        text = "Логин",
                        isEnabled = !model.isInProcess,
                        leadingIcon = {
                            GetAsyncIcon(
                                path = RIcons.User,
                                size = 19.dp
                            )
                        },
                        onEnterClicked = {
                            focusManager.moveFocus(FocusDirection.Down)
                        },
                        focusManager = focusManager,
                        isMoveUpLocked = true,
                        autoCorrect = false,
                        keyboardType = KeyboardType.Password
                    )


                    Spacer(Modifier.height(15.dp))

                    CustomTextField(
                        value = model.password,
                        onValueChange = {
                            component.onEvent(Intent.InputPassword(it))
                        },
                        text = "Пароль",
                        isEnabled = !model.isInProcess,
                        passwordVisibleInit = false,
                        focusManager = focusManager,
                        onEnterClicked = {
                            if (isButtonEnabled) {
                                component.onEvent(Intent.CheckToGoMain)
                            }
                        },
                        keyboardType = KeyboardType.Password
                    )

                    Spacer(Modifier.height(10.dp))
                    AnimatedVisibility(
                        model.isInProcess
                    ) {
                        LoadingAnimation(
                            Modifier.padding(top = 12.dp),
                            circleSize = 10.dp,
                            travelDistance = 8.dp,
                            spaceBetween = 6.dp
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {

                            },
                            modifier = Modifier.alpha(.0f),
                            enabled = false
                        ) {
                            GetAsyncIcon(
                                path = RIcons.Qr
                            )
                        }
                        AnimatedCommonButton(
                            modifier = Modifier.bringIntoView(scrollState, imeState),
                            isEnabled = isButtonEnabled,
                            onClick = {
                                component.onEvent(Intent.CheckToGoMain)
                            },
                            text = "Войти в аккаунт"
                        )
                        IconButton(
                            onClick = {
                                component.onEvent(Intent.GetQrToken)
                            }
                        ) {
                            AnimatedContent(if (model.qrToken == "") RIcons.Qr else RIcons.Refresh) {
                                GetAsyncIcon(
                                    path = it
                                )
                            }
                        }
                    }
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

        }
    }
}

