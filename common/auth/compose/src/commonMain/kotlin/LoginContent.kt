import activation.ActivationComponent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import components.AnimatedCommonButton
import components.CustomTextField
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.School
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.AppBar
import components.CentreAppBar
import components.LoadingAnimation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import login.LoginComponent
import login.LoginComponent.Output
import login.LoginStore
import login.LoginStore.Intent
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
    val coroutineScope = rememberCoroutineScope()
    val isButtonEnabled =
        !model.isInProcess && model.login.isNotBlank() && model.password.isNotBlank()


    if (model.logined) {
        component.onOutput(Output.NavigateToMain)
    }

    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    val imeState = rememberImeState()
    Scaffold(
        Modifier.fillMaxSize(),
        snackbarHost = {
            val hostState = remember{ mutableStateOf(SnackbarHostState()) }
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
                    Text("Вход", fontSize = 20.sp, fontWeight = FontWeight.Black)
                },
                navigationRow = {
                    IconButton(
                        onClick = { component.onOutput(Output.BackToActivation) }
                    ) {
                        Icon(
                            Icons.Rounded.ArrowBackIosNew, null
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
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Rounded.School,
                    null,
                    Modifier.size(150.dp)
                )
                CustomTextField(
                    value = model.login,
                    onValueChange = {
                        component.onEvent(Intent.InputLogin(it))
                    },
                    text = "Логин",
                    isEnabled = !model.isInProcess,
                    leadingIcon = {
                        val image = Icons.Rounded.Person
                        // Please provide localized description for accessibility services
                        val description = "Login"
                        Icon(imageVector = image, description)
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
                AnimatedCommonButton(
                    modifier = Modifier.bringIntoView(scrollState, imeState),
                    isEnabled = isButtonEnabled,
                    onClick = {
                        component.onEvent(Intent.CheckToGoMain)
                    },
                    text = "Войти в аккаунт"
                )
            }
        }
    }
}

