import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.SoftKeyboardInterceptionModifierNode
import androidx.compose.ui.platform.LocalPlatformTextInputMethodOverride
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.input.PlatformTextInputService
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.window.ComposeViewport
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.stack.webhistory.DefaultWebHistoryController
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.essenty.lifecycle.stop
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import di.Inject
import kotlinx.browser.window
import root.RootComponentImpl
import view.WindowType
import server.DeviceTypex
import view.AppTheme
import view.LocalViewManager
import view.ViewManager
import view.toRGB
import view.toTint

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@OptIn(ExperimentalComposeUiApi::class, ExperimentalDecomposeApi::class
)
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
            deepLink = RootComponentImpl.DeepLink.Web(path = window.location.pathname),
            webHistoryController = DefaultWebHistoryController(),
            storeFactory = DefaultStoreFactory()
        )

//    lifecycle.attachToDocument()
    lifecycle.resume()
//    Window
    //CanvasBasedWindow
    ComposeViewport(
        viewportContainerId = "composeApp",
//            applyDefaultStyles = false,
//        requestResize = {
//            val width = window.innerWidth + ((371 / 1482.0f) * window.innerWidth).toInt()
//            val height = window.innerHeight + ((190 / 760.0f) * window.innerHeight).toInt()
//            IntSize(width, height)
//        }
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

external fun onLoadFinished()

@Composable
fun PageLoadNotify() {
    LaunchedEffect(Unit) {
        onLoadFinished()
    }
}


//private fun LifecycleRegistry.attachToDocument() {
//    fun onVisibilityChanged() {
//        if (document.visibilityState == DocumentVisibilityState.visible) {
//            resume()
//        } else {
//            stop()
//        }
//    }
//
//    onVisibilityChanged()
//
//    document.addEventListener(
//        type = EventType("visibilitychange"),
//        callback = { onVisibilityChanged() })
//}