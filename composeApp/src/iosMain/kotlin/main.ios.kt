import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.essenty.lifecycle.ApplicationLifecycle
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import platform.UIKit.UIViewController
import root.RootComponentImpl
import server.DeviceTypex

@OptIn(ExperimentalDecomposeApi::class, ExperimentalAnimationApi::class,
    ExperimentalFoundationApi::class
)
fun MainViewController(): UIViewController =
    ComposeUIViewController {
        PlatformSDK.init(
            configuration = PlatformConfiguration(),
            cConfiguration = CommonPlatformConfiguration(
                deviceName = "unknown",
                deviceType = DeviceTypex.ios,
                deviceId = "c53379fe-19a7-3f07-911c-0c9d195b1925"//getDeviceId()
            )
        )
        val root = RootComponentImpl(componentContext = DefaultComponentContext(
            lifecycle = ApplicationLifecycle()
        ), storeFactory = DefaultStoreFactory())
        Root(root)
    }