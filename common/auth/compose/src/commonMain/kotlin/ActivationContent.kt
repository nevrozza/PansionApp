@file:OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)

import activation.ActivationComponent
import activation.ActivationStore
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.EaseInQuad
import androidx.compose.animation.core.EaseOutQuad
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.isSystemInDarkTheme
import components.AnimatedCommonButton
import components.AnimatedElevatedButton
import components.CustomTextButton
import components.CustomTextField
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.ArrowBackIos
import androidx.compose.material.icons.rounded.AutoMode
import androidx.compose.material.icons.rounded.Cake
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.BottomThemePanel
import components.LoadingAnimation
import forks.colorPicker.toHex
import kotlinx.coroutines.launch
import login.LoginComponent
import resources.Images
import view.defaultDarkPalette
import view.defaultLightPalette
import view.greenDarkPalette
import view.greenLightPalette
import view.redDarkPalette
import view.redLightPalette
import view.yellowDarkPalette
import view.yellowLightPalette
import view.AppTheme
import view.LocalViewManager
import view.ThemeTint
import view.bringIntoView
import view.dynamicDarkScheme
import view.dynamicLightScheme
import view.rememberImeState


@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
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
        !model.isInProcess && model.password.isNotBlank()
    val focusRequester1 = remember { FocusRequester() }
    val focusRequester2 = remember { FocusRequester() }

    if (model.activated) {
        component.navigateToMain()
    }


    Scaffold(
        Modifier.fillMaxSize(),
        snackbarHost = {
            val hostState = remember{mutableStateOf(SnackbarHostState())}
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
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    Icon(
                        Images.MGU,
                        null,
                        Modifier.size(200.dp)
                    )
                    //Title
                    Crossfade(
                        targetState = model.name
                    ) { name ->
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            when (name) {
                                null -> Text(
                                    "Активируйте свой аккаунт\n",
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 28.sp
                                )

                                else -> Text(
                                    "Здравствуйте\n$name!",
                                    lineHeight = 33.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Center,
                                    fontSize = 28.sp
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
                                    CustomTextButton("Через логин") {
                                        component.onEvent(
                                            ActivationStore.Intent.ChangeStep(
                                                ActivationStore.Step.Login
                                            )
                                        )
                                    }

                                }

                                ActivationStore.Step.Login -> {
//                            Spacer(Modifier.height(5.dp))
                                    Text("Введите логин с карточки", fontSize = 20.sp)
                                    Spacer(Modifier.height(10.dp))
                                    CustomTextField(
                                        modifier = Modifier.focusRequester(focusRequester1)
                                            .onPlaced {
                                                if (model.login.isBlank()) {
                                                    focusRequester1.requestFocus()
                                                }
                                            },
                                        value = model.login,
                                        onValueChange = {
                                            component.onEvent(ActivationStore.Intent.InputLogin(it))
                                        },
                                        supText = "Логин",
                                        isEnabled = !model.isInProcess,
                                        leadingIcon = {
                                            val image = Icons.Rounded.Person
                                            // Please provide localized description for accessibility services
                                            val description = "Login"
                                            Icon(imageVector = image, description)
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
                                    CustomTextButton("Уже активирован") {
                                        component.onOutput(ActivationComponent.Output.NavigateToLogin)
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
//                            Spacer(Modifier.height(5.dp))
                                    Text(
                                        "Придумайте пароль",
                                        textAlign = TextAlign.Center,
                                        fontSize = 20.sp
                                    )
                                    Spacer(Modifier.height(10.dp))
                                    CustomTextField(
                                        modifier = Modifier.focusRequester(focusRequester2)
                                            .onPlaced {
                                                if (model.password.isBlank()) {
                                                    focusRequester2.requestFocus()
                                                }
                                            },
                                        value = model.password,
                                        onValueChange = {
                                            component.onEvent(
                                                ActivationStore.Intent.InputPassword(
                                                    it
                                                )
                                            )
                                        },
                                        supText = "Пароль",
                                        isEnabled = !model.isInProcess,
                                        isMoveUpLocked = true,
                                        autoCorrect = false,
                                        focusManager = focusManager,
                                        onEnterClicked = {
                                            if (isActivationButtonEnabled) {
                                                component.onEvent(
                                                    ActivationStore.Intent.CheckToGoMain
                                                )
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
                                ActivationStore.Step.Choice -> Column(horizontalAlignment = Alignment.CenterHorizontally){
                                    CustomTextButton("QR-код") {}
                                    Spacer(Modifier.height(10.dp))
                                    OutlinedButton(
                                        contentPadding = PaddingValues(horizontal = 15.dp),
                                        onClick = {
                                            component.onOutput(ActivationComponent.Output.NavigateToLogin)
                                        }) {
                                        Text("Уже активирован")
                                    }
                                }

                                ActivationStore.Step.Login -> {
//                                    IconButton(
//                                        onClick = {
//                                            component.onEvent(
//                                                ActivationStore.Intent.ChangeStep(
//                                                    ActivationStore.Step.Choice
//                                                )
//                                            )
//                                        }
//                                    ) {
//                                        Icon(
//                                            Icons.Rounded.ArrowBackIos,
//                                            null
//                                        )
//                                    }
//                                    Spacer(Modifier.width(5.dp))
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
                                                ActivationStore.Intent.ChangeStep(
                                                    ActivationStore.Step.Login
                                                )
                                            )
                                        }
                                    ) {
                                        Icon(
                                            Icons.Rounded.ArrowBackIos,
                                            null
                                        )
                                    }
                                    Spacer(Modifier.width(5.dp))
                                    AnimatedCommonButton(
                                        text = "Активировать",
                                        isEnabled = isActivationButtonEnabled
                                    ) {
                                        component.onEvent(
                                            ActivationStore.Intent.CheckToGoMain
                                        )
                                    }
                                }
                            }
                        }
                    }

                    BottomThemePanel(
                        viewManager,
                        onThemeClick = {
                            changeTint(viewManager)
                        }
                    ) {
                        changeColorSeed(viewManager, it.toHex())
                    }

//                    Row(
//                        modifier = Modifier.widthIn(max = 470.dp).fillMaxWidth()
//                            .bringIntoView(scrollState, imeState)
//                            .padding(horizontal = 15.dp)
//                            .padding(bottom = 5.dp),
//                        horizontalArrangement = Arrangement.SpaceBetween,
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Row(verticalAlignment = Alignment.CenterVertically) {
//                            AnimatedContent(
//                                when (model.themeTint) {
//                                    ThemeTint.Auto.name -> Icons.Rounded.AutoMode
//                                    ThemeTint.Dark.name -> Icons.Rounded.DarkMode
//                                    else -> Icons.Rounded.LightMode
//                                }
//                            ) {
//                                IconButton(
//                                    onClick = { component.onEvent(ActivationStore.Intent.ChangeTint) }
//                                ) {
//                                    Icon(
//                                        it,
//                                        null,
//                                        modifier = Modifier.size(27.dp)
//                                    )
//                                }
//                            }
//                            Spacer(Modifier.width(10.dp))
//
//                            Button(
//                                onClick = {
//                                    component.onEvent(ActivationStore.Intent.ChangeColor)
//                                },
//                                contentPadding = PaddingValues(0.dp),
//                                shape = CircleShape,
//                                modifier = Modifier.size(25.dp),
//                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
//                            ) {}
//                        }
//
//                        Row() {
//                            Icon(
//                                Icons.AutoMirrored.Rounded.Send,
//                                null,
//                                modifier = Modifier.rotate(360-45.0f)
//                            )
//                            Text(
//                                "@pansionApp"
//                            )
//                        }
//
//                        AnimatedContent(
//                            when (model.language) {
//                                else -> "\uD83C\uDDF7\uD83C\uDDFA"
//                            }
//                        ) {
//                            TextButton(onClick = {
//                                component.onEvent(ActivationStore.Intent.ChangeLanguage)
//                            }) {
//                                Text(it, fontSize = 20.sp)
//                            }
//                        }
//                    }
                }
            }
        }

    }
}