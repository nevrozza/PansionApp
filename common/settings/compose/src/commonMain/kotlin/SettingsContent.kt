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
import androidx.compose.ui.graphics.toArgb
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
import components.BottomThemePanel
import components.CustomTextButton
import components.LoadingAnimation
import components.ThemePreview
import components.cAlertDialog.CAlertDialogComponent
import components.cAlertDialog.CAlertDialogStore
import components.cBottomSheet.CBottomSheetStore
import components.networkInterface.NetworkState
import decomposeComponents.CAlertDialogContent
import forks.colorPicker.toHex
import forks.splitPane.ExperimentalSplitPaneApi
import forks.splitPane.HorizontalSplitPane
import forks.splitPane.rememberSplitPaneState
import pullRefresh.PullRefreshIndicator
import pullRefresh.pullRefresh
import pullRefresh.rememberPullRefreshState
import server.Roles
import view.AppTheme
import view.LocalViewManager
import view.ThemeTint
import view.bringIntoView


@ExperimentalSplitPaneApi
@ExperimentalMaterial3Api
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsContent(
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
                }
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
    }
}

