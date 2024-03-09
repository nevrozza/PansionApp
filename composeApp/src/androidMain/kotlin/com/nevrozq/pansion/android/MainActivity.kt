package com.nevrozq.pansion.android

import CommonPlatformConfiguration
import PlatformConfiguration
import Root
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.arkivanov.decompose.defaultComponentContext
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import io.ktor.util.decodeBase64Bytes
import root.RootComponentImpl
import java.util.UUID

//@ExperimentalFoundationApi
class MainActivity : AppCompatActivity() {
    //@SuppressLint("SourceLockedOrientationActivity")

    @OptIn(ExperimentalAnimationApi::class, ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        @SuppressLint("HardwareIds")
        val uuid = UUID.nameUUIDFromBytes(Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID).decodeBase64Bytes());
        PlatformSDK.init(
            configuration = PlatformConfiguration(applicationContext),
            cConfiguration = CommonPlatformConfiguration(
                deviceName = Build.MODEL ?: "unknown",
                deviceType = "Android",
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

        val root = RootComponentImpl(componentContext = defaultComponentContext(), storeFactory = DefaultStoreFactory())

        setContent {
            Root(root)
        }
    }
}

