import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import di.Inject
import view.AppTheme
import view.ThemeTint
import view.ViewManager
import view.toRGB

fun changeTint(viewManager: ViewManager, tint: ThemeTint) {
    val repository: SettingsRepository = Inject.instance()
    viewManager.tint.value = tint
    repository.saveTint(tint.name)
}

fun changeColorSeed(viewManager: ViewManager, colorSeed: String) {
    val repository: SettingsRepository = Inject.instance()
    val color = colorSeed.toRGB()
    viewManager.seedColor.value = Color(color[0], color[1], color[2])
    repository.saveSeedColor(colorSeed)
}


fun changeColorMode(viewManager: ViewManager, colorMode: String) {
    val repository: SettingsRepository = Inject.instance()
    viewManager.colorMode.value = colorMode
    repository.saveColorMode(colorMode)
}

@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
fun setHaze(viewManager: ViewManager) {
    val repository: SettingsRepository = Inject.instance()
    if (repository.fetchIsHaze()) {
        AppTheme {
            val hazeStyle = HazeMaterials.thin()
            viewManager.hazeStyle = mutableStateOf(hazeStyle)
        }
    }
}

@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
fun changeOnHaze(viewManager: ViewManager) {
    val repository: SettingsRepository = Inject.instance()
    repository.saveIsHaze(true)
    AppTheme {
        val hazeStyle = HazeMaterials.thin()
        viewManager.hazeStyle = mutableStateOf(hazeStyle)
    }
}

@OptIn(ExperimentalHazeMaterialsApi::class)
fun changeOffHaze(viewManager: ViewManager) {
    val repository: SettingsRepository = Inject.instance()
    repository.saveIsHaze(false)
    viewManager.hazeStyle = null
}


fun changeTint(viewManager: ViewManager) {
    val theme = when (viewManager.tint.value) {
        ThemeTint.Auto ->
            ThemeTint.Light

        ThemeTint.Dark ->
            ThemeTint.Auto

        ThemeTint.Light ->
            ThemeTint.Dark
    }
    changeTint(
        viewManager,
        theme
    )
}