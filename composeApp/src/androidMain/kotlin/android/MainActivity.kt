package android

import CommonPlatformConfiguration
import PlatformConfiguration
import PlatformSDK
import Root
import SettingsRepository
import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.defaultComponentContext
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import dev.chrisbanes.haze.HazeState
import di.Inject
import forks.splitPane.ExperimentalSplitPaneApi
import forks.splitPane.SplitPaneState
import io.ktor.util.decodeBase64Bytes
import root.RootComponent
import root.RootComponentImpl
import server.DeviceTypex
import server.cut
import view.AppTheme
import view.GlobalHazeState
import view.LocalViewManager
import view.ViewManager
import view.toRGB
import view.toTint
import java.util.UUID

//@ExperimentalFoundationApi
class MainActivity : AppCompatActivity() {
    //@SuppressLint("SourceLockedOrientationActivity")

    @SuppressLint("SourceLockedOrientationActivity")
    @OptIn(ExperimentalAnimationApi::class, ExperimentalFoundationApi::class,
        ExperimentalDecomposeApi::class, ExperimentalSplitPaneApi::class
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        @SuppressLint("HardwareIds")
        val uuid = UUID.nameUUIDFromBytes(Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID).decodeBase64Bytes());
        PlatformSDK.init(
            configuration = PlatformConfiguration(applicationContext),
            cConfiguration = CommonPlatformConfiguration(
                deviceName = Build.MODEL?.cut(20) ?: "unknown",
                deviceType = DeviceTypex.android,
                deviceId = uuid.toString()
            )
        )
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val windowInsetsController =
            WindowCompat.getInsetsController(window, window.decorView)
        // Configure the behavior of the hidden system bars.
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars())


        val root = RootComponentImpl(
            isMentoring = null,
            componentContext = defaultComponentContext(),
            storeFactory = DefaultStoreFactory(),
            urlArgs = emptyMap(),
            wholePath = ""
        )

        setContent {
            val x by root.childStack.subscribeAsState()
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
            CompositionLocalProvider(
                LocalViewManager provides viewManager,
                GlobalHazeState provides remember { HazeState() }
            ) {
                AppTheme {
                    Scaffold() {
                        viewManager.topPadding = it.calculateTopPadding()
                        Root(root)
                    }
                }
                BackHandler(x.active.instance is RootComponent.Child.MainHome) {
                    this.finishAffinity()
                }
            }
        }
    }
}

