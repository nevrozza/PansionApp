@file:OptIn(ExperimentalAnimationApi::class)

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Fullscreen
import androidx.compose.material.icons.rounded.FullscreenExit
import androidx.compose.material.icons.rounded.HorizontalRule
import androidx.compose.material.icons.rounded.Minimize
import androidx.compose.material3.Button
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.PointerIcon.Companion.Text
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.semantics.SemanticsProperties.Text
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import androidx.compose.ui.zIndex
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.mvikotlin.core.utils.setMainThreadId
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.descriptors.PrimitiveKind
import org.jetbrains.jewel.foundation.GlobalColors
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.foundation.theme.ThemeDefinition
import org.jetbrains.jewel.intui.core.theme.IntUiDarkTheme
import org.jetbrains.jewel.intui.standalone.theme.IntUiTheme
import org.jetbrains.jewel.intui.standalone.theme.darkThemeDefinition
import org.jetbrains.jewel.intui.window.decoratedWindow
import org.jetbrains.jewel.intui.window.styling.light
import org.jetbrains.jewel.ui.ComponentStyling
import org.jetbrains.jewel.ui.component.IconButton
import org.jetbrains.jewel.ui.component.Tooltip
import org.jetbrains.jewel.window.DecoratedWindow
import org.jetbrains.jewel.window.TitleBar
import org.jetbrains.jewel.window.newFullscreenControls
import org.jetbrains.jewel.window.styling.DecoratedWindowStyle
import org.jetbrains.jewel.window.styling.LocalTitleBarStyle
import org.jetbrains.jewel.window.styling.TitleBarColors
import org.jetbrains.jewel.window.styling.TitleBarStyle
import root.RootComponentImpl
import view.ThemeColors
import view.WindowType
import view.colorSchemeGetter
import java.awt.AWTEvent
import java.awt.Desktop
import java.awt.Dimension
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.URI
import java.util.UUID
import javax.swing.JFrame
import javax.swing.SwingUtilities
import javax.swing.WindowConstants



// c53379fe-19a7-3f07-911c-0c9d195b1925
@ExperimentalFoundationApi
@OptIn(ExperimentalDecomposeApi::class)
fun main() {
    GlobalScope.launch(Dispatchers.IO) {
        com.nevrozq.pansion.main()
    }
    PlatformSDK.init(
        configuration = PlatformConfiguration(),
        cConfiguration = CommonPlatformConfiguration(
            deviceName = getDeviceName() ?: "unknown",
            deviceType = "PC",
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
        val themeDefinition = JewelTheme.darkThemeDefinition()
        IntUiTheme(
            themeDefinition,
            styling = ComponentStyling.decoratedWindow(
                titleBarStyle = TitleBarStyle.light()
            )
        ) {
            DecoratedWindow(
                onCloseRequest = { exitApplication() },
                state = windowState,
                title = "PansionApp",
                visible = isVisible,

                ) {
                val color = remember { mutableStateOf(ThemeColors.Default.name) }
                val isDark = remember { mutableStateOf(true) }
                val colorScheme = colorSchemeGetter(isDark.value, color.value)
                val l = LocalTitleBarStyle.current

                TitleBar(
                    Modifier.newFullscreenControls(),
                    style = TitleBarStyle(
                        colors = TitleBarColors(
                            background = colorScheme.surfaceColorAtElevation(2.dp),
                            inactiveBackground = colorScheme.background,
                            content = colorScheme.onBackground,
                            border = l.colors.border,
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
                        metrics = l.metrics,
                        icons = l.icons,
                        dropdownStyle = l.dropdownStyle,
                        iconButtonStyle = l.iconButtonStyle,
                        paneButtonStyle = l.paneButtonStyle,
                        paneCloseButtonStyle = l.paneCloseButtonStyle,
                    ),
                    gradientStartColor = colorScheme.surfaceColorAtElevation(26.dp)
                ) {
                    Text(text = title, color = colorScheme.onBackground, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start).padding(start = 15.dp),)

                }
                this.window.setMinSize(400, 600)


//                Row(
//                    modifier = Modifier.background(color = Color(75, 75, 75))
//                        .fillMaxWidth()
//                        .height(30.dp)
//                        .padding(start = 20.dp, end = 10.dp),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    WindowDraggableArea(
//                        modifier = Modifier.weight(1f)
//                    ) {
//                        Text(text = "Undecorated window", color = Color.White)
//                    }
//                    Row {
//                        Button(
//                            onClick = {
//                                val current = window.isFocused
//                                if (current != null) {
//                                    window.extendedState = JFrame.ICONIFIED
//                                }
//                            }
//                        )
//                        Spacer(modifier = Modifier.width(5.dp))
//                        Button(
//                            onClick = {
//                                val current = window.isFocused
//                                if (current != null) {
//                                    if (window.extendedState == JFrame.MAXIMIZED_BOTH) {
//                                        window.extendedState = JFrame.NORMAL
//                                    } else {
//                                        window.extendedState = JFrame.MAXIMIZED_BOTH
//                                    }
//                                }
//                            }
//                        )
//                        Spacer(modifier = Modifier.width(5.dp))
//                        Button(
//                            onClick = {
//                                window.defaultCloseOperation
//                            }
//                        )
//                    }
//                }

                    Root(root, WindowType.PC) { colorX, isDarkX ->
                        isDark.value = isDarkX
                        color.value = colorX
                    }


            }
        }
    }


//    JFrame().init(root)
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