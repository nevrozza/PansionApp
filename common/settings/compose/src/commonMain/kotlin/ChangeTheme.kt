import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
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

fun changeIsTransitionsEnabled(viewManager: ViewManager, isEnabled: Boolean) {
    val repository: SettingsRepository = Inject.instance()
    viewManager.isTransitionsEnabled.value = isEnabled
    repository.saveIsTransitionsEnabled(isEnabled)
}

fun changeFontSize(viewManager: ViewManager, fontSize: Float) {
    val repository: SettingsRepository = Inject.instance()
    viewManager.fontSize.value = fontSize
    repository.saveFontSize(fontSize)
}
fun changeFontType(viewManager: ViewManager, fontType: Int) {
    val repository: SettingsRepository = Inject.instance()
    viewManager.fontType.value = fontType
    repository.saveFontType(fontType)
}


fun changeColorMode(viewManager: ViewManager, colorMode: String) {
    val repository: SettingsRepository = Inject.instance()
    viewManager.colorMode.value = colorMode
    repository.saveColorMode(colorMode)
}

@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
fun setViewManager(viewManager: ViewManager) {
    val repository: SettingsRepository = Inject.instance()
    if (repository.fetchIsHaze()) {
        AppTheme {
//            LocalHazeStyle.provides( HazeMaterials.thin(MaterialTheme.colorScheme.background) )
            viewManager.hazeHardware.value = true
        }
    }
    viewManager.hazeHardware.value = repository.fetchIsTransitionsEnabled()
    viewManager.fontSize.value = repository.fetchFontSize()
    viewManager.fontType.value = repository.fetchFontType()
}

@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
fun changeOnHaze(viewManager: ViewManager) {
    val repository: SettingsRepository = Inject.instance()
    repository.saveIsHaze(true)
    AppTheme {
//        LocalHazeStyle.provides(HazeMaterials.thin(MaterialTheme.colorScheme.background))
        viewManager.hazeHardware.value = true
    }
}

@OptIn(ExperimentalHazeMaterialsApi::class)
fun changeOffHaze(viewManager: ViewManager) {
    val repository: SettingsRepository = Inject.instance()
    repository.saveIsHaze(false)
    viewManager.hazeHardware.value = false
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