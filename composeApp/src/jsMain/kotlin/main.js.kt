import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
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
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import androidx.compose.ui.window.CanvasBasedWindow
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.PredictiveBackGestureOverlay
import com.arkivanov.decompose.router.stack.webhistory.DefaultWebHistoryController
import com.arkivanov.essenty.backhandler.BackDispatcher
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.essenty.lifecycle.stop
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.benasher44.uuid.uuid4
import dev.chrisbanes.haze.HazeState
import di.Inject
import forks.colorPicker.toHex
import forks.splitPane.ExperimentalSplitPaneApi
import forks.splitPane.SplitPaneState
import js.core.asList
import org.jetbrains.skiko.wasm.onWasmReady
import root.RootComponentImpl
import server.DeviceTypex
import kotlinx.browser.window
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import org.jetbrains.compose.resources.configureWebResources
import org.w3c.dom.HTMLMetaElement
import server.cut
import view.*
import web.dom.DocumentVisibilityState
import web.dom.document
import web.events.EventType

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@ExperimentalDecomposeApi
@ExperimentalSplitPaneApi
@JsName("jsMain")
fun main() {
    configureWebResources {
        resourcePathMapping { path ->
            "/$path" }
    }
    PlatformSDK.init(
        configuration = PlatformConfiguration(),
        cConfiguration = CommonPlatformConfiguration(
            deviceName = getDeviceName().cut(20),//navigator.userAgent ?: "unknown",
            deviceType = DeviceTypex.web,
            deviceId = getOrCreateDeviceUUID() //navigator.userAgent
        )
    )
    val lifecycle = LifecycleRegistry()
    val backDispatcher = BackDispatcher()
    val root =
        RootComponentImpl(
            componentContext = DefaultComponentContext(
                lifecycle = lifecycle,
                backHandler = backDispatcher
            ),
            deepLink = RootComponentImpl.DeepLink.Web(path = window.location.pathname),//RootComponentImpl.DeepLink.Web(path = window.location.pathname),
            webHistoryController = DefaultWebHistoryController(),//DefaultWebHistoryController(),
            storeFactory = DefaultStoreFactory(), isMentoring = null,
            urlArgs = parseUrlArgs(),
            wholePath = window.location.href.split(window.location.pathname)[1]
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
            val haze = remember {HazeState()}
            CompositionLocalProvider(
                LocalViewManager provides viewManager,
                GlobalHazeState provides haze
            ) {
                PageLoadNotify()
                AppTheme {
                    PredictiveBackGestureOverlay(
                        backDispatcher = backDispatcher,
                        backIcon = { _, _ -> },
                        endEdgeEnabled = true,
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        Root(
                            root = root,
                            device = WindowType.PC
                        )
                    }
                    val hex = MaterialTheme.colorScheme.background.toHex()
                    changeThemeColor(hex)
                }
            }
        }
    }
}

fun parseUrlArgs() : Map<String, String> {
    val output = mutableMapOf<String, String>()
    runCatching {
        val args =  window.location.href.split(window.location.pathname)[1].removePrefix("?").split("?")
        for (i in args) {
            val arg = i.split("=")
            output[arg[0]] = arg[1]
        }
    }
    return output
}

fun changeThemeColor(newColor: String) {
    val metaTags = document.head.querySelectorAll("meta[name=theme-color]").asList()
    @Suppress("CAST_NEVER_SUCCEEDS") val themeColorMetaTag = metaTags[0] as HTMLMetaElement?

    if (themeColorMetaTag != null) {
        themeColorMetaTag.content = newColor
    }
    document.body.style.backgroundColor = newColor
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
        userAgent.contains("Chrome", ignoreCase = true) -> "Chrome "
        userAgent.contains("Safari", ignoreCase = true) -> "Safari "
        userAgent.contains("YaBrowser", ignoreCase = true) -> "Yandex "
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