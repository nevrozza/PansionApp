import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.SoftKeyboardInterceptionModifierNode
import androidx.compose.ui.platform.LocalPlatformTextInputMethodOverride
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.input.PlatformTextInputService
import androidx.compose.ui.unit.IntSize
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.router.stack.webhistory.DefaultWebHistoryController
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.essenty.lifecycle.stop
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import kotlinx.browser.window
import kotlinx.css.input
import kotlinx.css.textarea
import kotlinx.html.CANVAS
import kotlinx.html.TEXTAREA
import kotlinx.html.TextAreaWrap
import kotlinx.html.onClick
import org.jetbrains.skiko.wasm.onWasmReady
import react.dom.html.TextareaHTMLAttributes
import react.dom.onFocus
import root.RootComponentImpl
import view.WindowType
import web.dom.DocumentVisibilityState
import web.dom.document
import web.events.EventType
import web.navigator.navigator
import server.DeviceTypex
import web.dom.Text
import web.prompts.alert
import web.prompts.prompt
import web.viewport.visualViewport
import web.window.WindowName

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@OptIn(ExperimentalComposeUiApi::class)
fun main() {
//    val a: SoftwareKeyboardController
//
//    a.show()
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
            path = window.location.pathname,
            webHistoryController = DefaultWebHistoryController(),
            storeFactory = DefaultStoreFactory()
        )

    lifecycle.attachToDocument()
    onWasmReady {
        CanvasBasedWindow(
            canvasElementId = "ComposeTarget",
//            applyDefaultStyles = false,
            requestResize = {
                val width = window.innerWidth + ((371 / 1482.0f) * window.innerWidth).toInt()
                val height = window.innerHeight + ((190 / 760.0f) * window.innerHeight).toInt()
                IntSize(width, height)
            }
        ) {
            Root(
                root = root,
                device = WindowType.PC,
                isJs = true
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

    document.addEventListener(
        type = EventType("visibilitychange"),
        callback = { onVisibilityChanged() })
}