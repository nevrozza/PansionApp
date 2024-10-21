import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState

@Composable
actual fun CFilePicker(
    showFilePicker: MutableState<Boolean>,
    onPick: (String) -> Unit
) {
    com.darkrockstudios.libraries.mpfilepicker.FilePicker(show = showFilePicker.value) { platformFile ->
        showFilePicker.value = false
        if (platformFile != null) {
            onPick(platformFile.path)
        }
    }
}