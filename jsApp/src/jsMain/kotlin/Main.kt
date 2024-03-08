import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.stack.webhistory.DefaultWebHistoryController
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.essenty.lifecycle.stop
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import react.create
import react.dom.client.createRoot
import root.RootComponentImpl
import web.dom.DocumentVisibilityState
import web.dom.document
import web.events.EventType
import web.navigator.navigator
import web.window.window

@OptIn(ExperimentalDecomposeApi::class)
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
            deepLink = RootComponentImpl.DeepLink.Web(path = kotlinx.browser.window.location.pathname),
            path = kotlinx.browser.window.location.pathname,
            webHistoryController = DefaultWebHistoryController(),
            storeFactory = DefaultStoreFactory()
        )

    lifecycle.attachToDocument()

    createRoot(document.getElementById("app")!!).render(
        RootContent.create {
            component = root
        }
    )
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
