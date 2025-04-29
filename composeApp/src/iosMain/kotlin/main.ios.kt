
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.desktop.ui.tooling.preview.utils.GlobalHazeState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.PredictiveBackGestureOverlay
import com.arkivanov.essenty.backhandler.BackDispatcher
import com.arkivanov.essenty.lifecycle.ApplicationLifecycle
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import dev.chrisbanes.haze.HazeState
import deviceSupport.deviceType
import di.Inject
import forks.splitPane.ExperimentalSplitPaneApi
import forks.splitPane.SplitPaneState
import platform.UIKit.UIDevice
import platform.UIKit.UIViewController
import root.RootComponentImpl
import server.cut
import view.AppTheme
import view.LocalViewManager
import view.ViewManager
import view.toRGB
import view.toTint

@OptIn(
    ExperimentalDecomposeApi::class, ExperimentalAnimationApi::class,
    ExperimentalFoundationApi::class, ExperimentalSplitPaneApi::class
)
fun MainViewController(): UIViewController =
    ComposeUIViewController {
        val backDispatcher = BackDispatcher()
        PlatformSDK.init(
            configuration = PlatformConfiguration(),
            cConfiguration = CommonPlatformConfiguration(
                deviceName = UIDevice.currentDevice.name.cut(20),
                deviceType = deviceType,
                deviceId = UIDevice.currentDevice.identifierForVendor.toString()//getDeviceId()
            )
        )
        val settingsRepository: SettingsRepository = Inject.instance()
        val rgb = settingsRepository.fetchSeedColor().toRGB()
        val viewManager = remember {
            ViewManager(
                seedColor = mutableStateOf(Color(red = rgb[0], green = rgb[1], blue = rgb[2])),
                tint = mutableStateOf(settingsRepository.fetchTint().toTint()),
                colorMode = mutableStateOf(settingsRepository.fetchColorMode()),
                splitPaneState = SplitPaneState(
                    moveEnabled = true,
                    initialPositionPercentage = 0f
                )
            )
        }
        val root = RootComponentImpl(
            componentContext = DefaultComponentContext(
                lifecycle = ApplicationLifecycle(),
                backHandler = backDispatcher
            ), storeFactory = DefaultStoreFactory(), isMentoring = null,
            urlArgs = emptyMap(),
            wholePath = ""
        )
        CompositionLocalProvider(
            LocalViewManager provides viewManager,
            GlobalHazeState provides remember { HazeState() }
        ) {
            AppTheme {
                Scaffold() {
                    viewManager.topPadding = it.calculateTopPadding()
                    PredictiveBackGestureOverlay(
                        backDispatcher = backDispatcher,
                        backIcon = { _, _ -> },
                        endEdgeEnabled = true,
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        Root(root)
                    }
                }
            }
        }
    }
