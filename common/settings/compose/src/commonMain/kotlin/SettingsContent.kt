import admin.users.User
import admin.users.UserInit
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import forks.splitPane.dSplitter
import androidx.compose.animation.core.EaseInQuad
import androidx.compose.animation.core.EaseOutQuad
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.AutoMode
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.AppBar
import components.CustomTextButton
import components.LoadingAnimation
import components.ThemePreview
import components.cAlertDialog.CAlertDialogComponent
import components.cAlertDialog.CAlertDialogStore
import components.cBottomSheet.CBottomSheetStore
import components.networkInterface.NetworkState
import decomposeComponents.CAlertDialogContent
import forks.splitPane.ExperimentalSplitPaneApi
import forks.splitPane.HorizontalSplitPane
import forks.splitPane.rememberSplitPaneState
import pullRefresh.PullRefreshIndicator
import pullRefresh.pullRefresh
import pullRefresh.rememberPullRefreshState
import server.Roles
import view.AllThemes
import view.AppTheme
import view.LocalViewManager
import view.ThemeColors
import view.ThemeTint
import view.bringIntoView
import view.defaultDarkPalette
import view.defaultLightPalette
import view.dynamicDarkScheme
import view.dynamicLightScheme
import view.greenDarkPalette
import view.greenLightPalette
import view.redDarkPalette
import view.redLightPalette
import view.rememberImeState
import view.yellowDarkPalette
import view.yellowLightPalette

@ExperimentalMaterial3Api
@Composable
fun SettingsContent(
    isExpanded: Boolean,
    component: SettingsComponent
) {
    val viewManager = LocalViewManager.current
    val model by component.model.subscribeAsState()
    val isDarkTheme = isSystemInDarkTheme()

    val theme: String =
        if (model.themeTint == ThemeTint.Dark.name || (model.themeTint == ThemeTint.Auto.name && isDarkTheme)) {
            when (model.color) {
                ThemeColors.Dynamic.name -> AllThemes.DarkDynamic.name
                ThemeColors.Green.name -> AllThemes.DarkGreen.name
                ThemeColors.Red.name -> AllThemes.DarkRed.name
                ThemeColors.Yellow.name -> AllThemes.DarkYellow.name
                else -> {
                    AllThemes.DarkDefault.name
                }
            }
        } else {
            when (model.color) {
                ThemeColors.Dynamic.name -> AllThemes.LightDynamic.name
                ThemeColors.Green.name -> AllThemes.LightGreen.name
                ThemeColors.Red.name -> AllThemes.LightRed.name
                ThemeColors.Yellow.name -> AllThemes.LightYellow.name
                else -> {
                    AllThemes.LightDefault.name
                }
            }
        }
    viewManager.tint.value = model.themeTint
    viewManager.color.value = model.color
    val timeEnter = 300
    val easingEnter = EaseOutQuad
    val timeExit = 300
    val easingExit = EaseInQuad


    //Dark
    RequrseSettingsView(
        isExpanded,
        component,
        theme,
        timeEnter,
        easingEnter,
        timeExit,
        easingExit,
        AllThemes.DarkDynamic.name,
        dynamicDarkScheme() ?: defaultDarkPalette()
    )
    RequrseSettingsView(
        isExpanded,
        component,
        theme,
        timeEnter,
        easingEnter,
        timeExit,
        easingExit,
        AllThemes.DarkDefault.name,
        defaultDarkPalette()
    )
    RequrseSettingsView(
        isExpanded,
        component,
        theme,
        timeEnter,
        easingEnter,
        timeExit,
        easingExit,
        AllThemes.DarkGreen.name,
        greenDarkPalette()
    )
    RequrseSettingsView(
        isExpanded,
        component,
        theme,
        timeEnter,
        easingEnter,
        timeExit,
        easingExit,
        AllThemes.DarkRed.name,
        redDarkPalette()
    )
    RequrseSettingsView(
        isExpanded,
        component,
        theme,
        timeEnter,
        easingEnter,
        timeExit,
        easingExit,
        AllThemes.DarkYellow.name,
        yellowDarkPalette()
    )

    //Light
    RequrseSettingsView(
        isExpanded,
        component,
        theme,
        timeEnter,
        easingEnter,
        timeExit,
        easingExit,
        AllThemes.LightDynamic.name,
        dynamicLightScheme() ?: defaultLightPalette()
    )
    RequrseSettingsView(
        isExpanded,
        component,
        theme,
        timeEnter,
        easingEnter,
        timeExit,
        easingExit,
        AllThemes.LightDefault.name,
        defaultLightPalette()
    )
    RequrseSettingsView(
        isExpanded,
        component,
        theme,
        timeEnter,
        easingEnter,
        timeExit,
        easingExit,
        AllThemes.LightGreen.name,
        greenLightPalette()
    )
    RequrseSettingsView(
        isExpanded,
        component,
        theme,
        timeEnter,
        easingEnter,
        timeExit,
        easingExit,
        AllThemes.LightRed.name,
        redLightPalette()
    )
    RequrseSettingsView(
        isExpanded,
        component,
        theme,
        timeEnter,
        easingEnter,
        timeExit,
        easingExit,
        AllThemes.LightYellow.name,
        yellowLightPalette()
    )


}


@ExperimentalMaterial3Api
@Composable
private fun RequrseSettingsView(
    isExpanded: Boolean,
    component: SettingsComponent,
    theme: String,
    timeEnter: Int,
    easingEnter: Easing,
    timeExit: Int,
    easingExit: Easing,
    color: String,
    colorScheme: ColorScheme,
) {
    AnimatedVisibility(
        visible = theme == color,
        enter = fadeIn(tween(timeEnter, easing = easingEnter)),
        exit = fadeOut(tween(timeExit, easing = easingExit))
    ) {
        AppTheme(colorScheme) {
            MultiPaneSettings(isExpanded, component)
        }
    }
}
@ExperimentalSplitPaneApi
@ExperimentalMaterial3Api
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MultiPaneSettings(
    isExpanded: Boolean,
    settingsComponent: SettingsComponent
//    secondScreen: @Composable () -> Unit
) {
    if (isExpanded) {
        val splitterState = rememberSplitPaneState(.5f)
        HorizontalSplitPane(
            splitPaneState = splitterState
        ) {
            first(minSize = 320.dp) {
                SettingsView(settingsComponent)
            }
            dSplitter()
            second(minSize = 500.dp) {
                Column(Modifier.fillMaxSize().padding(50.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Предпросмотр", fontWeight = FontWeight.Black, fontSize = 20.sp)
                    Spacer(Modifier.height(20.dp))
                    ThemePreview()
                }
            }
        }
    } else {
        SettingsView(settingsComponent)
    }
}
@Composable
fun SettingsView(
    component: SettingsComponent
) {

    val viewManager = LocalViewManager.current

    val model by component.model.subscribeAsState()

    val colorRed = if (viewManager.isDark.value) Color(255, 99, 71) else Color.Red
    Scaffold(

        modifier = Modifier.fillMaxSize(),
        topBar = {
            AppBar(
                navigationRow = {
                    IconButton(
                        onClick = { component.onOutput(SettingsComponent.Output.BackToHome) }
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
                },
//                actionRow = {
////                    if (model.users != null) {
////                        IconButton(
////                            onClick = { component.onEvent(UsersStore.Intent.FetchUsers) }
////                        ) {
////                            Icon(
////                                Icons.Filled.Refresh, null
////                            )
////                        }
////                        IconButton(
////                            onClick = {
////                                component.cUserBottomSheet.onEvent(CBottomSheetStore.Intent.ShowSheet)
////                            }
////                        ) {
////                            Icon(
////                                Icons.Rounded.Add, null
////                            )
////                        }
//                    }
//                }
            )
        }
    ) { padding ->
        Box(Modifier.padding(horizontal = 15.dp)) {
            Column(
                Modifier.fillMaxSize().padding(padding).imePadding()
            ) {
                Text(
                    model.login,
                    fontWeight = FontWeight.Black,
                    fontSize = 30.sp,
                    modifier = Modifier.fillMaxWidth().padding(top = (8*5).dp, bottom = (8*6).dp),
                    textAlign = TextAlign.Center
                )
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


            }

            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
                Row(
                    modifier = Modifier.widthIn(max = 470.dp).fillMaxWidth()
//                    .bringIntoView(scrollState, imeState)

                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AnimatedContent(
                            when (model.themeTint) {
                                ThemeTint.Auto.name -> Icons.Rounded.AutoMode
                                ThemeTint.Dark.name -> Icons.Rounded.DarkMode
                                else -> Icons.Rounded.LightMode
                            }
                        ) {
                            IconButton(
                                onClick = { component.onEvent(SettingsStore.Intent.ChangeTint) }
                            ) {
                                Icon(
                                    it,
                                    null,
                                    modifier = Modifier.size(27.dp)
                                )
                            }
                        }
                        Spacer(Modifier.width(10.dp))

                        Button(
                            onClick = {
                                component.onEvent(SettingsStore.Intent.ChangeColor)
                            },
                            contentPadding = PaddingValues(0.dp),
                            shape = CircleShape,
                            modifier = Modifier.size(25.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {}
                    }

                    Row() {
                        Icon(
                            Icons.AutoMirrored.Rounded.Send,
                            null,
                            modifier = Modifier.rotate(360-45.0f)
                        )
                        Text(
                            "@pansionApp"
                        )
                    }

                    AnimatedContent(
                        when (model.language) {
                            else -> "\uD83C\uDDF7\uD83C\uDDFA"
                        }
                    ) {
                        TextButton(onClick = {
                            component.onEvent(SettingsStore.Intent.ChangeLanguage)
                        }) {
                            Text(it, fontSize = 20.sp)
                        }
                    }
                }
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
    }
}
