import androidx.compose.ui.graphics.Color
import di.Inject
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