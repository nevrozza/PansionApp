@file:OptIn(ExperimentalAnimationApi::class)

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoMode
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
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
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.mvikotlin.core.utils.setMainThreadId
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import di.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.intui.standalone.theme.IntUiTheme
import org.jetbrains.jewel.intui.standalone.theme.darkThemeDefinition
import org.jetbrains.jewel.intui.window.decoratedWindow
import org.jetbrains.jewel.intui.window.styling.dark
import org.jetbrains.jewel.intui.window.styling.light
import org.jetbrains.jewel.ui.ComponentStyling
import org.jetbrains.jewel.window.DecoratedWindow
import org.jetbrains.jewel.window.TitleBar
import org.jetbrains.jewel.window.newFullscreenControls
import org.jetbrains.jewel.window.styling.LocalTitleBarStyle
import org.jetbrains.jewel.window.styling.TitleBarColors
import org.jetbrains.jewel.window.styling.TitleBarMetrics
import org.jetbrains.jewel.window.styling.TitleBarStyle
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
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.UUID
import javax.swing.SwingUtilities


// c53379fe-19a7-3f07-911c-0c9d195b1925
@ExperimentalFoundationApi
@OptIn(ExperimentalDecomposeApi::class, ExperimentalAnimationApi::class)
fun main() {
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


    val root = invokeOnAwtSync {
        setMainThreadId(Thread.currentThread().id)
        val lifecycle = LifecycleRegistry()
        val rootComponent = RootComponentImpl(
            componentContext = DefaultComponentContext(
                lifecycle = lifecycle
            ),
            storeFactory = DefaultStoreFactory()
        )
        lifecycle.resume()
        rootComponent
    }
    application {
        val windowState = rememberWindowState()
        windowState.size = DpSize(480.dp, 800.dp)
        var isVisible by remember { mutableStateOf(true) }

        val settingsRepository: SettingsRepository = Inject.instance()
        val rgb = settingsRepository.fetchSeedColor().toRGB()
        val themeDefinition = JewelTheme.darkThemeDefinition()
        val viewManager = remember {
            ViewManager(
                seedColor = mutableStateOf(Color(red = rgb[0], green = rgb[1], blue = rgb[2])),
                tint = mutableStateOf(settingsRepository.fetchTint().toTint())
            )
        }


        Tray(
            icon = TrayIcon,
            menu = {
                Item(
                    "Показать",
                    onClick = { isVisible = true }
                )
                Item(
                    "Выход",
                    onClick = ::exitApplication
                )
            },
            tooltip = "Pansion App",
            onAction = {
                isVisible = true
            }
        )
        CompositionLocalProvider(
            LocalViewManager provides viewManager
        ) {
            if (isVisible) {
                IntUiTheme(
                    themeDefinition,
                    styling = ComponentStyling.decoratedWindow(
                        titleBarStyle = TitleBarStyle.light()
                    )
                ) {
                    AppTheme {
                        DecoratedWindow(
                            onCloseRequest = { isVisible = false },
                            state = windowState,
                            title = "Pansion App",
                            visible = isVisible,
                            icon = BitmapPainter(useResource("favicon.ico", ::loadImageBitmap))

                        ) {
                            val l = LocalTitleBarStyle.current
                            viewManager.topPadding = (l.metrics.height - 10.dp).coerceAtLeast(0.dp)
                            this.window.setMinSize(400, 600)
                            Box(contentAlignment = Alignment.TopCenter) {
                                Root(root, WindowType.PC)
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
                        }
                    }
                }
            }
        }
    }
}


//    JFrame().init(root)


object TrayIcon : Painter() {
    override val intrinsicSize = Size(256f, 256f)

    override fun DrawScope.onDraw() {
        drawOval(Color(0xFFFFA500))
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