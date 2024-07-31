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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import decomposeComponents.CAlertDialogContent
import decomposeComponents.listDialogComponent.ListDialogDesktopContent
import decomposeComponents.listDialogComponent.ListDialogMobileContent
import forks.colorPicker.toHex
import forks.splitPane.ExperimentalSplitPaneApi
import forks.splitPane.HorizontalSplitPane
import forks.splitPane.rememberSplitPaneState
import view.LocalViewManager


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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsView(
    component: SettingsComponent
) {

    val viewManager = LocalViewManager.current

    val model by component.model.subscribeAsState()

    if(model.newColorMode != null) {
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
                Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(padding).imePadding()
            ) {
                Text(
                    model.login,
                    fontWeight = FontWeight.Black,
                    fontSize = 30.sp,
                    modifier = Modifier.fillMaxWidth().padding(top = (8*5).dp, bottom = (8*6).dp),
                    textAlign = TextAlign.Center
                )

                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        text = "Цветовой режим",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black
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

