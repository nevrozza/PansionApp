
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.window.CanvasBasedWindow
import com.arkivanov.essenty.backhandler.BackDispatcher
import kotlinx.browser.window
import kotlinx.coroutines.await
import org.jetbrains.compose.resources.configureWebResources
import org.jetbrains.skiko.wasm.onWasmReady
import root.RootComponent
import view.LocalBottomWebPadding

@OptIn(ExperimentalComposeUiApi::class)
actual fun CompatWindow(
    content: @Composable (Triple<RootComponent, BackDispatcher, String>) -> Unit
) {
    onWasmReady {

        val sizeManager = SizeManager().apply {
            resize()
        }

        val deviceName = getDeviceName()
        val (root, backDispatcher) = beforeCompatWindowInit(
            configureWebResources = {
                configureWebResources {
                    resourcePathMapping { path ->
                        "/$path"
                    }
                }
            },
            wholePath = window.location.href.split(window.location.host)[1].removePrefix("/"),
            deviceName = deviceName,
            configuration = PlatformConfiguration(),
            deviceId = getOrCreateDeviceUUID(),
            pathnameForDeepLink = window.location.pathname,
            args = window.location.href.split(window.location.pathname)[1].removePrefix("?")
                .split("?")
        )

        val bottomPaddingWeb = mutableStateOf(0.0f)


        CanvasBasedWindow(
            canvasElementId = "composeApp",
            applyDefaultStyles = false,
            requestResize = {
                val size = sizeManager.getChanges().await()
                val height = window.innerHeight
                bottomPaddingWeb.value = (height - size.height) * window.devicePixelRatio.toFloat()

                IntSize(width = window.innerWidth, height = height)
            }
        ) {
            CompositionLocalProvider(
                LocalBottomWebPadding provides bottomPaddingWeb
            ) {
                Box(Modifier.fillMaxSize()) {
                    content(Triple(root, backDispatcher, deviceName))
                    Text("JS", color = Color.Red, modifier = Modifier.align(Alignment.TopEnd))
                }
            }
        }
    }
}