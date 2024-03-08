import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import androidx.compose.material3.Text
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.router.stack.webhistory.DefaultWebHistoryController
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.essenty.lifecycle.stop
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import kotlinx.browser.window
import org.jetbrains.skiko.wasm.onWasmReady
import root.RootComponentImpl
import view.WindowType
import web.dom.DocumentVisibilityState
import web.dom.document
import web.events.EventType
import web.navigator.navigator
@ExperimentalAnimationApi
@ExperimentalFoundationApi
@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    PlatformSDK.init(
        configuration = PlatformConfiguration(),
        cConfiguration = CommonPlatformConfiguration(
            deviceName = navigator.userAgent ?: "unknown",
            deviceType = "Web",
            deviceId = navigator.userAgent
        )
    )
    val lifecycle = LifecycleRegistry()

    val root =
        RootComponentImpl(
            componentContext = DefaultComponentContext(
                lifecycle = lifecycle
            ),
            deepLink = RootComponentImpl.DeepLink.Web(path = window.location.pathname),
            path = window.location.pathname,
            webHistoryController = DefaultWebHistoryController(),
            storeFactory = DefaultStoreFactory()
        )

    lifecycle.attachToDocument()
    onWasmReady {
        CanvasBasedWindow {
            Root(
                root = root,
                device = WindowType.PC
            )

        }
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

    document.addEventListener(type = EventType("visibilitychange"), callback = { onVisibilityChanged() })
}