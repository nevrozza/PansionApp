package qr

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
actual fun isCameraAvailable(): Boolean {


    val cameraPermissionState = rememberPermissionState(
        android.Manifest.permission.CAMERA
    )
    DisposableEffect(Unit) {
        if (cameraPermissionState.status is PermissionStatus.Denied) {
            cameraPermissionState.launchPermissionRequest()
        }
        onDispose {  }
    }

    return cameraPermissionState.status == PermissionStatus.Granted
}