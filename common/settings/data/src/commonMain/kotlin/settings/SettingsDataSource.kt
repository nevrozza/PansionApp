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
        return settings[tintKey, ThemeTint.Auto.name]
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

    fun fetchIsHaze(): Boolean = settings[isHazeKey, true]

    fun saveIsMarkTable(isMarkTable: Boolean) {
        settings[isMarkTableKey] = isMarkTable
    }

    fun fetchIsMarkTable() : Boolean =  settings[isMarkTableKey, true]


    fun saveIsShowingPlusDs(isShowing: Boolean) {
        settings[isShowingPlusDSKey] = isShowing
    }

    fun fetchIsShowingPlusDs() : Boolean = settings[isShowingPlusDSKey, false]

    fun saveIsTransitionsEnabled(isEnabled: Boolean) {
        settings[isTransitionsEnabledKey] = isEnabled
    }
    fun fetchIsTransitionsEnabled() = settings[isTransitionsEnabledKey, true]

    fun saveFontSize(fontSize: Float) {
        settings[fontSizeKey] = fontSize.coerceAtLeast(0.2f)
    }

    fun fetchFontSize() = settings[fontSizeKey, 1f].coerceAtLeast(0.2f)

    fun fetchFontType() = settings[fontTypeKey, 5]//.coerceAtLeast(0.2f)

    fun saveFontType(fontType: Int) {
        settings[fontTypeKey] = fontType// fontSize.coerceAtLeast(0.2f)
    }


    fun saveIsAmoledEnabled(isEnabled: Boolean) {
        settings[isAmoledEnabledKey] = isEnabled
    }
    fun fetchIsAmoledEnabled() = settings[isAmoledEnabledKey, false]

    fun saveIsAvatarsEnabled(isEnabled: Boolean) {
        settings[isAvatarsEnabledKey] = isEnabled
    }
    fun fetchIsAvatarsEnabled() = settings[isAvatarsEnabledKey, true]

    fun saveIsRefreshButtonsEnabled(isEnabled: Boolean) {
        settings[isRefreshButtonsEnabledKey] = isEnabled
    }
    fun fetchIsRefreshButtonsEnabled() = settings[isRefreshButtonsEnabledKey, true]

    fun saveHardwareStatus(status: String) {
        settings[hardwareStatusKey] = status
    }
    fun fetchHardwareStatus() = settings[hardwareStatusKey, ""]

    companion object {
        const val languageKey = "languageKey"
        const val tintKey = "tintKey"
        const val seedColorKey = "seedColorKey"
        const val isDynamicKey = "isDynamicKey"
        const val colorModeKey = "colorModeKey"
        const val isHazeKey = "isHazeKey"
        const val fontSizeKey = "fontSizeKey"
        const val fontTypeKey = "fontTypeKey"
        const val isMarkTableKey = "isMarkTableKey"
        const val isShowingPlusDSKey = "isShowingPlusDSKey"
        const val isTransitionsEnabledKey = "isTransitionsEnabledKey"

        const val isAmoledEnabledKey = "isAmoledEnabledKey"
        const val isRefreshButtonsEnabledKey = "isRefreshButtonsEnabledKey"
        const val isAvatarsEnabledKey = "isAvatarsEnabledKey"
        const val hardwareStatusKey = "hardwareStatusKey"
    }

}