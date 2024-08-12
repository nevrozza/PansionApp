@file:OptIn(ExperimentalAnimationApi::class)

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoMode
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.useResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.lifecycle.LifecycleController
import com.arkivanov.essenty.instancekeeper.InstanceKeeperDispatcher
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.essenty.statekeeper.SerializableContainer
import com.arkivanov.essenty.statekeeper.StateKeeperDispatcher
import com.arkivanov.mvikotlin.core.utils.setMainThreadId
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import components.CustomTextButton
import di.Inject
import forks.splitPane.ExperimentalSplitPaneApi
import forks.splitPane.rememberSplitPaneState
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.intui.standalone.theme.IntUiTheme
import org.jetbrains.jewel.intui.standalone.theme.darkThemeDefinition
import org.jetbrains.jewel.intui.window.decoratedWindow
import org.jetbrains.jewel.intui.window.styling.dark
import org.jetbrains.jewel.intui.window.styling.light
import org.jetbrains.jewel.ui.ComponentStyling
import org.jetbrains.jewel.window.DecoratedWindow
import org.jetbrains.jewel.window.DecoratedWindowScope
import org.jetbrains.jewel.window.TitleBar
import org.jetbrains.jewel.window.newFullscreenControls
import org.jetbrains.jewel.window.styling.LocalTitleBarStyle
import org.jetbrains.jewel.window.styling.TitleBarColors
import org.jetbrains.jewel.window.styling.TitleBarMetrics
import org.jetbrains.jewel.window.styling.TitleBarStyle
import resources.Images
import root.RootComponentImpl
import server.DeviceTypex
import view.AppTheme
import view.LocalViewManager
import view.ThemeTint
import view.ViewManager
import view.WindowType
import view.toRGB
import view.toTint
import java.awt.Dimension
import java.awt.Robot
import java.io.File
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean
import javax.swing.SwingUtilities

private const val SAVED_STATE_FILE_NAME = "saved_state.dat"

// c53379fe-19a7-3f07-911c-0c9d195b1925
//@ExperimentalFoundationApi
//@OptIn(ExperimentalDecomposeApi::class, ExperimentalAnimationApi::class)
//fun main() {
//    println("Text")
//}

@ExperimentalFoundationApi
@OptIn(ExperimentalDecomposeApi::class, ExperimentalAnimationApi::class,
    DelicateCoroutinesApi::class, ExperimentalSplitPaneApi::class
)
fun main() {
//

        GlobalScope.launch(Dispatchers.IO) {
            com.nevrozq.pansion.main()
        }

        PlatformSDK.init(
            configuration = PlatformConfiguration(),
            cConfiguration = CommonPlatformConfiguration(
                deviceName = getDeviceName() ?: "unknown",
                deviceType = DeviceTypex.desktop,
                deviceId = getDeviceId()
            )
        )

        val lifecycle = LifecycleRegistry()
        val stateKeeper =
            StateKeeperDispatcher() //File(SAVED_STATE_FILE_NAME).readSerializableContainer()
        val root = runOnUiThread {

            setMainThreadId(Thread.currentThread().id)

            RootComponentImpl(
                componentContext = DefaultComponentContext(
                    lifecycle = lifecycle,
                    stateKeeper = stateKeeper
                ),
                storeFactory = DefaultStoreFactory(),
//            deepLink = RootComponentImpl.DeepLink.None,
//            webHistoryController = null
            )

        }
//    stateKeeper.unregister("UsersStoreState")


        application {



            val windowState = rememberWindowState()
            windowState.size = DpSize(950.dp, 480.dp) //950 480 //480 800

            var isVisible by remember { mutableStateOf(true) }

            Tray(
                icon = painterResource("favicon.ico"),//TrayIcon,
                menu = {
                    Item(
                        "Показать",
                        onClick = { isVisible = true }
                    )
                    Item(
                        "Закрыть",
                        onClick = ::exitApplication
                    )
                },
                tooltip = "Pansion App",
                onAction = {
                    isVisible = true
                }
            )

            if (isVisible) {
                var isCloseDialogVisible by remember { mutableStateOf(false) }

                val settingsRepository: SettingsRepository = Inject.instance()
                val rgb = settingsRepository.fetchSeedColor().toRGB()
                val themeDefinition = JewelTheme.darkThemeDefinition()
                val viewManager = //remember {
                    ViewManager(
                        seedColor = mutableStateOf(
                            Color(
                                red = rgb[0],
                                green = rgb[1],
                                blue = rgb[2]
                            )
                        ),
                        tint = mutableStateOf(settingsRepository.fetchTint().toTint()),
                        colorMode = mutableStateOf(settingsRepository.fetchColorMode()),
                        splitPaneState = rememberSplitPaneState(initialPositionPercentage = .5f)
                    )
                //}
                CompositionLocalProvider(
                    LocalViewManager provides viewManager
                ) {
                    IntUiTheme(
                        themeDefinition,
                        styling = ComponentStyling.decoratedWindow(
                            titleBarStyle = TitleBarStyle.light()
                        )
                    ) {
                        AppTheme {
                            DecoratedWindow(
                                onCloseRequest = { isCloseDialogVisible = true },
                                state = windowState,
                                title = "Pansion App", //Pansion App
                                visible = isVisible,
                                icon = BitmapPainter(useResource("favicon.ico", ::loadImageBitmap))

                            ) {

                                LifecycleController(
                                    lifecycleRegistry = lifecycle,
                                    windowState = windowState,
                                    windowInfo = LocalWindowInfo.current,
                                )

                                val l = LocalTitleBarStyle.current
                                viewManager.topPadding =
                                    (l.metrics.height - 10.dp).coerceAtLeast(0.dp)
                                this.window.setMinSize(400, 600)
                                Box(contentAlignment = Alignment.TopCenter) {
                                    Root(root, WindowType.PC)

                                    MainTitleBar(viewManager, l)
                                    if (isCloseDialogVisible) {
                                        AlertDialog(
                                            onDismissRequest = { isCloseDialogVisible = false },
                                            confirmButton = {
                                                CustomTextButton(
                                                    text = "Свернуть в трею"
                                                ) {
                                                    isVisible = false
                                                    isCloseDialogVisible = false
                                                }
                                            },
                                            dismissButton = {
                                                CustomTextButton(
                                                    text = "Закрыть"
                                                ) {
                                                    //stateKeeper.save().writeToFile(File(SAVED_STATE_FILE_NAME))
                                                    exitApplication()
                                                }
                                            },
//                                            title = { Text("Закрыть приложение?") },
                                            text = { Text("Чтобы продолжить получать уведомления, выберите \"Свернуть\" (приложение будет работать в фоновом режиме)") }

                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
//    } else {
//        application {
//            DialogWindow(
//                onCloseRequest = { ::exitApplication }
//            ) {
//                Row(
//                    Modifier.fillMaxSize(),
//                    verticalAlignment = Alignment.CenterVertically,
//                    horizontalArrangement = Arrangement.Center
//                ) {
//                    Text("Приложение уже работает на фоне")
//                }
//            }
//        }
//    }
}

@Composable
private fun DecoratedWindowScope.MainTitleBar(viewManager: ViewManager, l: TitleBarStyle) {
    TitleBar(
        Modifier.newFullscreenControls(),
        style = TitleBarStyle(
            colors = TitleBarColors(
                background = if (viewManager.hazeStyle != null) MaterialTheme.colorScheme.background.copy(
                    alpha = 0.0f
                ) else MaterialTheme.colorScheme.background,
                inactiveBackground = if (viewManager.hazeStyle != null) MaterialTheme.colorScheme.background.copy(
                    alpha = 0.0f
                ) else MaterialTheme.colorScheme.background,
                content = MaterialTheme.colorScheme.onBackground,
                border = if (viewManager.hazeStyle != null) MaterialTheme.colorScheme.background.copy(
                    alpha = 0.0f
                ) else MaterialTheme.colorScheme.background,
                fullscreenControlButtonsBackground = l.colors.fullscreenControlButtonsBackground,
                titlePaneButtonHoveredBackground = l.colors.titlePaneButtonHoveredBackground,
                titlePaneButtonPressedBackground = l.colors.titlePaneButtonPressedBackground,
                titlePaneCloseButtonHoveredBackground = l.colors.titlePaneCloseButtonHoveredBackground,
                titlePaneCloseButtonPressedBackground = l.colors.titlePaneCloseButtonPressedBackground,
                iconButtonHoveredBackground = l.colors.iconButtonHoveredBackground,
                iconButtonPressedBackground = l.colors.iconButtonPressedBackground,
                dropdownHoveredBackground = l.colors.dropdownHoveredBackground,
                dropdownPressedBackground = l.colors.dropdownPressedBackground
            ),
            metrics = TitleBarMetrics(
                height = l.metrics.height,
                titlePaneButtonSize = l.metrics.titlePaneButtonSize,
                gradientStartX = 0.dp, //-200
                gradientEndX = 0.dp //300
            ),
            icons = l.icons,
            dropdownStyle = l.dropdownStyle,
            iconButtonStyle = l.iconButtonStyle,
            paneButtonStyle = l.paneButtonStyle,
            paneCloseButtonStyle = l.paneCloseButtonStyle,
        ),
        gradientStartColor = MaterialTheme.colorScheme.inversePrimary//.surfaceColorAtElevation(26.dp)
    ) {
        AnimatedContent(
            when (viewManager.tint.value) {
                ThemeTint.Auto -> Icons.Rounded.AutoMode
                ThemeTint.Dark -> Icons.Rounded.DarkMode
                ThemeTint.Light -> Icons.Rounded.LightMode
            },
            modifier = Modifier.align(Alignment.Start)
                .padding(start = 5.dp),
        ) {
            androidx.compose.material3.IconButton(
                onClick = {
                    changeTint(
                        viewManager
                    )
                },
                modifier = Modifier.fillMaxHeight()
            ) {
                Icon(
                    imageVector = it,
                    "Change Theme",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

        }
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )

    }
}


@OptIn(ExperimentalSerializationApi::class)
fun SerializableContainer.writeToFile(file: File) {
    file.outputStream().use { output ->
        Json.encodeToStream(SerializableContainer.serializer(), this, output)
    }
}

@OptIn(ExperimentalSerializationApi::class)
fun File.readSerializableContainer(): SerializableContainer? =
    takeIf(File::exists)?.inputStream()?.use { input ->
        try {
            Json.decodeFromStream(SerializableContainer.serializer(), input)
        } catch (e: Exception) {
            null
        }
    }

//    JFrame().init(root)


object TrayIcon : Painter() {
    override val intrinsicSize = Size(256f, 256f)

    override fun DrawScope.onDraw() {
//        this.drawImage(painterResource("resources/favicon.ico"))
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Button(
    text: String = "",
    onClick: () -> Unit = {},
    color: Color = Color(210, 210, 210),
    size: Int = 16
) {
    val buttonHover = remember { mutableStateOf(false) }
    Surface(
        color = if (buttonHover.value)
            Color(color.red / 1.3f, color.green / 1.3f, color.blue / 1.3f)
        else
            color,
        shape = RoundedCornerShape((size / 2).dp)
    ) {
        Box(
            modifier = Modifier
                .clickable(onClick = onClick)
                .size(size.dp, size.dp)
                .pointerMoveFilter(
                    onEnter = {
                        buttonHover.value = true
                        false
                    },
                    onExit = {
                        buttonHover.value = false
                        false
                    },
                    onMove = { false }
                )
        ) {
            Text(text = text)
        }
    }
}

fun ComposeWindow.setMinSize(width: Int, height: Int) =
    Dimension(width, height).also { this.minimumSize = it }


fun getDeviceName(): String? {
    try {
        val localhost = InetAddress.getLocalHost()
        return localhost.hostName
    } catch (e: Throwable) {
        e.printStackTrace()
    }
    return null
}

fun getDeviceId(): String {
    val sb = StringBuilder()

    try {
        // Get the MAC address of the first network interface
        val networkInterfaces = NetworkInterface.getNetworkInterfaces()
        while (networkInterfaces.hasMoreElements()) {
            val networkInterface = networkInterfaces.nextElement()
            val macAddress = networkInterface.hardwareAddress
            if (macAddress != null) {
                for (byte in macAddress) {
                    sb.append(String.format("%02X", byte))
                }
                break
            }
        }

        // Get the hostname
        val localhost = InetAddress.getLocalHost()
        val hostname = localhost.hostName

        // Combine the MAC address and hostname
        sb.append(hostname)
    } catch (e: Throwable) {
        e.printStackTrace()
    }

    // Generate a UUID from the combined string
    val deviceId = UUID.nameUUIDFromBytes(sb.toString().toByteArray()).toString()

    return deviceId
}


fun <T> invokeOnAwtSync(block: () -> T): T {
    var result: T? = null
    SwingUtilities.invokeAndWait { result = block() }

    @Suppress("UNCHECKED_CAST")
    return result as T
}
//val osName = System.getProperty("os.name")
//    val osVersion = System.getProperty("os.version")
//    val userName = System.getProperty("user.name")

//internal fun <T> runOnUiThread(block: () -> T): T {
//    if (SwingUtilities.isEventDispatchThread()) {
//        return block()
//    }
//
//    var error: Throwable? = null
//    var result: T? = null
//
//    SwingUtilities.invokeAndWait {
//        try {
//            result = block()
//        } catch (e: Throwable) {
//            error = e
//        }
//    }
//
//    error?.also { throw it }
//
//    @Suppress("UNCHECKED_CAST")
//    return result as T
//}