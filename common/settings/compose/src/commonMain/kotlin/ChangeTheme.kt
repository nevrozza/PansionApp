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

fun changeTint(viewManager: ViewManager) {
    val theme = when (viewManager.tint.value) {
        ThemeTint.Auto ->
            ThemeTint.Dark


        ThemeTint.Dark ->
            ThemeTint.Light


        ThemeTint.Light ->
            ThemeTint.Auto
    }
    changeTint(
        viewManager,
        theme
    )
}