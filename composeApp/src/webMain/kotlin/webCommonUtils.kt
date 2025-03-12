import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.stack.webhistory.DefaultWebHistoryController
import com.arkivanov.essenty.backhandler.BackDispatcher
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import deviceSupport.deviceType
import root.RootComponent
import root.RootComponentImpl
import server.cut


external fun onLoadFinished()

@Composable
fun PageLoadNotify() {
    LaunchedEffect(Unit) {
        onLoadFinished()
    }
}

fun parseUrlArgs(args: List<String>) : Map<String, String> {
    val output = mutableMapOf<String, String>()
    runCatching {
        for (i in args) {
            val arg = i.split("=")
            output[arg[0]] = arg[1]
        }
    }
    return output
}

@OptIn(ExperimentalDecomposeApi::class)
fun beforeCompatWindowInit(
    configureWebResources: () -> Unit,
    wholePath: String,
    deviceName: String,
    configuration: PlatformConfiguration,
    deviceId: String,
    pathnameForDeepLink: String,
    args: List<String>
): Pair<RootComponent, BackDispatcher> {
    configureWebResources()

    PlatformSDK.init(
        configuration = configuration,
        cConfiguration = CommonPlatformConfiguration(
            deviceName = deviceName.cut(20),//navigator.userAgent ?: "unknown",
            deviceType = deviceType,
            deviceId = deviceId //navigator.userAgent
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
            deepLink = RootComponentImpl.DeepLink.Web(path = pathnameForDeepLink), //RootComponentImpl.DeepLink.None,//
            webHistoryController = DefaultWebHistoryController(), //null, //
            storeFactory = DefaultStoreFactory(), isMentoring = null,
            urlArgs = parseUrlArgs(args),
            wholePath = wholePath
        )

//    lifecycle.attachToDocument()
    lifecycle.resume()
    return root to backDispatcher
}