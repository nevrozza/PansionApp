package settings

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import view.Language
import view.ThemeTint
import view.isCanInDynamic

class SettingsDataSource(
    private val settings: Settings
) {

    fun saveLanguage(language: String) {
        settings[languageKey] = language
    }

    fun fetchLanguage(): String {
        return settings[languageKey, Language.Russian.name]
    }

    fun saveTint(tint: String) {
        settings[tintKey] = tint
    }

    fun fetchTint(): String {
        return settings[tintKey, ThemeTint.Dark.name]
    }

    fun saveSeedColor(color: String) {
        settings[seedColorKey] = color
    }

    fun fetchSeedColor(): String {
        return settings[seedColorKey, "6E007F"]
    }

    fun saveIsDynamic(isDynamic: Boolean) {
        settings[isDynamicKey] = isDynamic
    }

    fun fetchIsDynamic(): Boolean {
        return settings[isDynamicKey, isCanInDynamic()]
    }

    fun saveColorMode(colorMode: String) {
        settings[colorModeKey] = colorMode
    }

    fun fetchColorMode(): String {
        return settings[colorModeKey, "0"]
    }

    fun saveIsHaze(isHaze: Boolean) {
        settings[isHazeKey] = isHaze
    }

    fun fetchIsHaze(): Boolean {
        return settings[isHazeKey, true]
    }


    companion object {
        const val languageKey = "languageKey"
        const val tintKey = "tintKey"
        const val seedColorKey = "seedColorKey"
        const val isDynamicKey = "isDynamicKey"
        const val colorModeKey = "colorModeKey"
        const val isHazeKey = "isHazeKey"
    }

}