import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.ComposeViewport
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.stack.webhistory.DefaultWebHistoryController
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.essenty.lifecycle.stop
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import di.Inject
import org.jetbrains.skiko.wasm.onWasmReady
import root.RootComponentImpl
import server.DeviceTypex
import view.WindowType
import kotlinx.browser.window
import org.jetbrains.skiko.wasm.onWasmReady
import view.AppTheme
import view.LocalViewManager
import view.ViewManager
import view.toRGB
import view.toTint
import web.dom.DocumentVisibilityState
import web.dom.document
import web.events.EventType

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@OptIn(ExperimentalComposeUiApi::class, ExperimentalDecomposeApi::class)
fun main() {
    PlatformSDK.init(
        configuration = PlatformConfiguration(),
        cConfiguration = CommonPlatformConfiguration(
            deviceName = "edge",//navigator.userAgent ?: "unknown",
            deviceType = DeviceTypex.web,
            deviceId = "c53379fe-19a7-3f07-911c-0c9d195b1925" //navigator.userAgent
        )
    )
    val lifecycle = LifecycleRegistry()

    val root =
        RootComponentImpl(
            componentContext = DefaultComponentContext(
                lifecycle = lifecycle
            ),
            deepLink = RootComponentImpl.DeepLink.None,//RootComponentImpl.DeepLink.Web(path = window.location.pathname),
            webHistoryController = null,//DefaultWebHistoryController(),
            storeFactory = DefaultStoreFactory(), isMentoring = null
        )

    lifecycle.attachToDocument()
    onWasmReady {
        ComposeViewport(
            viewportContainerId = "composeApp",
        ) {
            val settingsRepository: SettingsRepository = Inject.instance()
            val rgb = settingsRepository.fetchSeedColor().toRGB()
            val viewManager = remember {
                ViewManager(
                    seedColor = mutableStateOf(
                        Color(
                            red = rgb[0],
                            green = rgb[1],
                            blue = rgb[2]
                        )
                    ),
                    tint = mutableStateOf(settingsRepository.fetchTint().toTint()),
                    colorMode = mutableStateOf(settingsRepository.fetchColorMode())
                )
            }
            CompositionLocalProvider(
                LocalViewManager provides viewManager
            ) {
                PageLoadNotify()
                AppTheme {
                    Root(
                        root = root,
                        device = WindowType.PC,
                        isJs = true
                    )
                }
            }
        }
    }
}

external fun onLoadFinished()

@Composable
fun PageLoadNotify() {
    LaunchedEffect(Unit) {
        onLoadFinished()
    }
}

private fun LifecycleRegistry.attachToDocument() {
    fun onVisibilityChanged() {
        if (document.visibilityState == DocumentVisibilityState.visible) {
            resume()
        } else {
            stop()
        }
    }

    onVisibilityChanged()

    document.addEventListener(
        type = EventType("visibilitychange"),
        callback = { onVisibilityChanged() })
}