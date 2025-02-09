@file:OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)

import activation.ActivationComponent
import activation.ActivationStore
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.desktop.ui.tooling.preview.utils.bringIntoView
import androidx.compose.desktop.ui.tooling.preview.utils.esp
import androidx.compose.desktop.ui.tooling.preview.utils.popupPositionProvider
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.foundation.AnimatedCommonButton
import components.foundation.AnimatedElevatedButton
import components.BottomThemePanel
import components.foundation.CTextButton
import components.foundation.CTextField
import components.GetAsyncIcon
import components.foundation.LoadingAnimation
import kotlinx.coroutines.launch
import resources.RIcons
import utils.rememberImeState
import utils.toHex
import view.LocalViewManager


@OptIn(
    ExperimentalLayoutApi::class, ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun ActivationContent(
    component: ActivationComponent
) {
    val scrollState = rememberScrollState()
    val imeState = rememberImeState()
    val model by component.model.subscribeAsState()
    val focusManager = LocalFocusManager.current
    val viewManager = LocalViewManager.current
    val coroutineScope = rememberCoroutineScope()
    val isLoginButtonEnabled =
        !model.isInProcess && model.login.isNotBlank()
    val isActivationButtonEnabled =
        (!model.isInProcess && model.password.isNotBlank() && !model.isVerifyingPassword) || (!model.isInProcess && model.verifyPassword == model.password && model.isVerifyingPassword)
    remember { FocusRequester() }
    val focusRequester2 = remember { FocusRequester() }

    LaunchedEffect(model.activated) {
        if (model.activated) {
            component.navigateToMain()
        }
    }


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
                            if (model.error == "Данный аккаунт уже активирован") {
                                component.onOutput(ActivationComponent.Output.NavigateToLogin(login = model.login))
                            }
                            hostState.value.showSnackbar(message = model.error)
                        }
                    } else {
                        hostState.value.currentSnackbarData?.dismiss()
                    }
                }

            }
        }
    ) { padding ->
        Box {
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
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
                        .animateContentSize()
                ) {
                    GetAsyncIcon(
                        path = RIcons.MGU,
                        size = 200.dp
                    )

                    //Title
                    Crossfade(
                        targetState = model.name,
                        modifier = Modifier.animateContentSize()
                    ) { name ->
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            when (name) {
                                null -> {
                                    Spacer(Modifier)
                                    Text(
                                        "Активируйте свой аккаунт",
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = (26.5f).esp,
                                        lineHeight = 28.esp
                                    )
                                }

                                else -> Text(
                                    "Здравствуйте\n$name!",
                                    lineHeight = 28.esp,
                                    fontWeight = FontWeight.Normal,
                                    textAlign = TextAlign.Center,
                                    fontSize = (26.5f).esp
                                )
                            } //30
                        }
                    }
                    //Steps
                    Crossfade(
                        targetState = model.step
                    ) { label ->
                        Column(
                            Modifier.fillMaxWidth().heightIn(min = 127.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            when (label) {
                                ActivationStore.Step.Choice -> {
                                    Spacer(Modifier.height(50.dp))
                                    CTextButton("Через логин") {
                                        component.onEvent(
                                            ActivationStore.Intent.ChangeStep(
                                                ActivationStore.Step.Login
                                            )
                                        )
                                    }

                                }

                                ActivationStore.Step.Login -> {
                                    Spacer(Modifier.height(5.dp))
                                    Text(
                                        "Введите логин",
                                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                        modifier = Modifier.alpha(.5f)
                                    )
                                    Spacer(Modifier.height(10.dp))
                                    CTextField(
                                        modifier = Modifier,
                                        value = model.login,
                                        onValueChange = {
                                            component.onEvent(ActivationStore.Intent.InputLogin(it))
                                        },
                                        text = "Логин",
                                        isEnabled = !model.isInProcess,
                                        leadingIcon = {
                                            GetAsyncIcon(
                                                path = RIcons.USER,
                                                size = 19.dp
                                            )
                                        },

                                        isMoveUpLocked = true,
                                        autoCorrect = false,
                                        focusManager = focusManager,
                                        onEnterClicked = {
                                            if (isLoginButtonEnabled) {
                                                component.onEvent(
                                                    ActivationStore.Intent.ChangeStepOnActivation
                                                )
                                            }
                                        },
                                        keyboardType = KeyboardType.Password
                                    )
                                    Spacer(Modifier.height(10.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(start = 35.dp)
                                    ) {
                                        CTextButton("Уже активирован") {
                                            component.onOutput(
                                                ActivationComponent.Output.NavigateToLogin(
                                                    ""
                                                )
                                            )
                                        }
                                        Spacer(Modifier.width(5.dp))


                                        val tState =
                                            rememberTooltipState(isPersistent = true)
                                        Row(modifier = Modifier.size(30.dp)) {
                                            AnimatedVisibility(
                                                model.logins.isNotEmpty()
                                            ) {
                                                TooltipBox(
                                                    state = tState,
                                                    tooltip = {
                                                        PlainTooltip() {
                                                            Column {
                                                                model.logins.forEach {
                                                                    Text(it)
                                                                    if (model.logins.last() != it) {
                                                                        Spacer(Modifier.height(2.dp))
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    },
                                                    positionProvider = popupPositionProvider,
                                                    enableUserInput = false,
                                                    focusable = false
                                                ) {


                                                    IconButton(
                                                        onClick = {
                                                            coroutineScope.launch {
                                                                tState.show()
                                                            }
                                                        },
                                                        modifier = Modifier.size(30.dp)
                                                    ) {
                                                        GetAsyncIcon(RIcons.LINK)
                                                    }

                                                }
                                            }
                                        }
                                    }


                                    AnimatedVisibility(
                                        model.isInProcess
                                    ) {
                                        LoadingAnimation(
                                            Modifier.padding(top = 14.dp),
                                            circleSize = 10.dp,
                                            travelDistance = 8.dp,
                                            spaceBetween = 6.dp
                                        )
                                    }
                                }

                                ActivationStore.Step.Activation -> {
                            Spacer(Modifier.height(5.dp))
                                    AnimatedContent(
                                        if (model.isVerifyingPassword) "Подтвердите пароль" else "Придумайте пароль"
                                    ) { text ->
                                        Text(
                                            text,
                                            textAlign = TextAlign.Center,
                                            fontSize = MaterialTheme.typography.titleLarge.fontSize
                                        )
                                    }
                                    Spacer(Modifier.height(10.dp))
                                    CTextField(
                                        modifier = Modifier.focusRequester(focusRequester2)
                                            .onPlaced {
                                                if (model.password.isBlank()) {
                                                    focusRequester2.requestFocus()
                                                }
                                            },
                                        value = if (model.isVerifyingPassword) model.verifyPassword else model.password,
                                        onValueChange = {
                                            if (model.isVerifyingPassword) {
                                                component.onEvent(
                                                    ActivationStore.Intent.ChangeVerifyPassword(
                                                        it
                                                    )
                                                )
                                            } else {
                                                component.onEvent(
                                                    ActivationStore.Intent.InputPassword(
                                                        it
                                                    )
                                                )
                                            }
                                        },
                                        supText = "Пароль",
                                        isEnabled = !model.isInProcess,
                                        isMoveUpLocked = true,
                                        autoCorrect = false,
                                        focusManager = focusManager,
                                        onEnterClicked = {
                                            if (isActivationButtonEnabled) {
                                                if (model.isVerifyingPassword) {
                                                    component.onEvent(
                                                        ActivationStore.Intent.CheckToGoMain
                                                    )
                                                } else {
                                                    component.onEvent(
                                                        ActivationStore.Intent.ChangeVerify
                                                    )
                                                }
                                            }
                                        },
                                        keyboardType = KeyboardType.Password,
                                        passwordVisibleInit = false
                                    )
                                }
                            }
                        }
                    }
                }

                val transition = updateTransition(model.step)
                Column(
                    Modifier,
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    transition.AnimatedContent(
                        Modifier.fillMaxWidth(),
                        transitionSpec = {
                            (fadeIn(animationSpec = tween(300)) +
                                    slideInVertically { it })
                                .togetherWith(fadeOut(animationSpec = tween(300)) + slideOutVertically { it })
                        }) { label ->

                        Row(
                            modifier = Modifier.padding(bottom = 5.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            when (label) {
                                ActivationStore.Step.Choice -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    CTextButton("QR-код") {}
                                    Spacer(Modifier.height(10.dp))
                                    OutlinedButton(
                                        contentPadding = PaddingValues(horizontal = 15.dp),
                                        onClick = {
                                            component.onOutput(
                                                ActivationComponent.Output.NavigateToLogin(
                                                    ""
                                                )
                                            )
                                        }) {
                                        Text("Уже активирован")
                                    }
                                }

                                ActivationStore.Step.Login -> {
//                                    IconButton(
//                                        onClick = {},
//                                        enabled = false
//                                    ) {
//
//                                    }
                                    AnimatedElevatedButton(
                                        text = "Далее",
                                        isEnabled = isLoginButtonEnabled
                                    ) {
                                        component.onEvent(
                                            ActivationStore.Intent.ChangeStepOnActivation
                                        )
                                    }

                                }

                                ActivationStore.Step.Activation -> {
                                    IconButton(
                                        onClick = {
                                            component.onEvent(
                                                if (model.isVerifyingPassword) {
                                                    ActivationStore.Intent.ChangeVerify
                                                } else {
                                                    ActivationStore.Intent.ChangeStep(
                                                        ActivationStore.Step.Login
                                                    )
                                                }
                                            )
                                        }
                                    ) {
                                        GetAsyncIcon(
                                            RIcons.CHEVRON_LEFT
                                        )
                                    }
                                    Spacer(Modifier.width(5.dp))
                                    AnimatedContent(
                                        if (model.isVerifyingPassword && model.password == model.verifyPassword) "Подтвердить"
                                        else if (model.isVerifyingPassword && model.password != model.verifyPassword) "Пароли не совпадают"
                                        else "Активировать"
                                    ) { text ->
                                        AnimatedCommonButton(
                                            text = text,
                                            isEnabled = isActivationButtonEnabled
                                        ) {
                                            if (model.isVerifyingPassword) {
                                                component.onEvent(
                                                    ActivationStore.Intent.CheckToGoMain
                                                )
                                            } else {
                                                component.onEvent(
                                                    ActivationStore.Intent.ChangeVerify
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    BottomThemePanel(
                        modifier = Modifier.bringIntoView(scrollState, imeState),
                        viewManager = viewManager,
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
}