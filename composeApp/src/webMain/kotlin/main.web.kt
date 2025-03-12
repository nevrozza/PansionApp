import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.desktop.ui.tooling.preview.utils.GlobalHazeState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.PredictiveBackGestureOverlay
import com.arkivanov.essenty.backhandler.BackDispatcher
import dev.chrisbanes.haze.HazeState
import deviceSupport.initIsLockedVerticalView
import di.Inject
import forks.splitPane.ExperimentalSplitPaneApi
import forks.splitPane.SplitPaneState
import utils.toHex
import view.AppTheme
import view.LocalViewManager
import view.ViewManager
import view.WindowType
import view.toRGB
import view.toTint

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@ExperimentalDecomposeApi
@ExperimentalSplitPaneApi
@JsName("webMain")
fun main() {
    CompatWindow { (root, backDispatcher, deviceName) ->
        val settingsRepository: SettingsRepository = Inject.instance()
        initIsLockedVerticalView(settingsRepository.fetchIsLockedVerticalView(), deviceName) {
            settingsRepository.saveIsLockedVerticalView(it)
        }
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
            LocalViewManager provides viewManager,
            GlobalHazeState provides remember { HazeState() }
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

                changeMetaThemeColor(hex)
            }
        }
    }
}

