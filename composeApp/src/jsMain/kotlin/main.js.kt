import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeViewport
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import androidx.compose.ui.window.CanvasBasedWindow
import com.arkivanov.decompose.router.stack.webhistory.DefaultWebHistoryController
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.essenty.lifecycle.stop
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import di.Inject
import forks.splitPane.SplitPaneState
import org.jetbrains.skiko.wasm.onWasmReady
import root.RootComponentImpl
import server.DeviceTypex
import view.WindowType
import kotlinx.browser.window
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
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
            deviceName = getDeviceName(),//navigator.userAgent ?: "unknown",
            deviceType = DeviceTypex.web,
            deviceId = getOrCreateDeviceUUID() //navigator.userAgent
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

    val sizeManager = SizeManager().apply {
        resize()
    }

    onWasmReady {
        CanvasBasedWindow(
            canvasElementId = "composeApp",
            applyDefaultStyles = false,
            requestResize = {
                sizeManager.changes.first()
            }
        ) {

//        ComposeViewport(
//            viewportContainerId = "composeApp",
//        ) {
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
                    colorMode = mutableStateOf(settingsRepository.fetchColorMode()),
                    splitPaneState = SplitPaneState(
                        moveEnabled = true,
                        initialPositionPercentage = 0f
                    )
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

fun getDeviceName(): String {
    val userAgent = window.navigator.userAgent
    var deviceName = when {
        userAgent.contains("iPhone", ignoreCase = true) -> "iPhone"
        userAgent.contains("Samsung", ignoreCase = true) -> "Samsung"
        userAgent.contains("Ubuntu", ignoreCase = true) -> "Ubuntu"
        userAgent.contains("Fedora", ignoreCase = true) -> "Fedora"
        userAgent.contains("iPad", ignoreCase = true) -> "iPad"
        userAgent.contains("Android", ignoreCase = true) -> "Android"
        userAgent.contains("Windows", ignoreCase = true) -> "Windows"
        userAgent.contains("Macintosh", ignoreCase = true) -> "MacOS"
        userAgent.contains("Linux", ignoreCase = true) -> "Linux"
        else -> "Устройство"
    }
    deviceName = when {
        userAgent.contains("OPR", ignoreCase = true) -> "Opera "
        userAgent.contains("Edg", ignoreCase = true) -> "Edge "
        userAgent.contains("Firefox", ignoreCase = true) -> "Firefox "

        userAgent.contains("EdgiOS", ignoreCase = true) -> "Edge "
        userAgent.contains("FxiOS", ignoreCase = true) -> "Firefox "
        userAgent.contains("CriOS", ignoreCase = true) -> "Chrome "
        else -> ""
    } + deviceName

    return deviceName + " JS"
}

class SizeManager {
    private val _changes = Channel<IntSize>(CONFLATED)
    val changes get() = _changes.receiveAsFlow()

    init {
        window.asDynamic()
            .visualViewport
            .onresize = ::resize
    }

    fun resize() {
        _changes.trySend(
            IntSize(
                window.innerWidth,
                window.asDynamic().visualViewport.height as Int
            )
        )
    }
}

fun getOrCreateDeviceUUID(): String {
    val storedUUID = kotlinx.browser.localStorage.getItem("deviceUUID")
    return if (storedUUID != null) {
        storedUUID
    } else {
        val newUUID = uuid4().toString()
        kotlinx.browser.localStorage.setItem("deviceUUID", newUUID)
        newUUID
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