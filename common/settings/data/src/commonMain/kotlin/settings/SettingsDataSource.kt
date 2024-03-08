package settings

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import view.Language
import view.ThemeColors
import view.ThemeTint

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
        return settings[tintKey, ThemeTint.Auto.name]
    }

    fun saveColor(color: String) {
        settings[colorKey] = color
    }

    fun fetchColor(): String {
        return settings[colorKey, ThemeColors.Default.name]
    }

    companion object {
        const val languageKey = "languageKey"
        const val tintKey = "tintKey"
        const val colorKey = "colorKey"
    }

}