package settings

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import deviceSupport.isCanInDynamic
import view.Language
import view.ThemeTint

class SettingsDataSource(
    private val settings: Settings
) {

    fun saveLanguage(language: String) {
        settings[LANGUAGE_KEY] = language
    }

    fun fetchLanguage(): String {
        return settings[LANGUAGE_KEY, Language.Russian.name]
    }

    fun saveTint(tint: String) {
        settings[TINT_KEY] = tint
    }

    fun fetchTint(): String {
        return settings[TINT_KEY, ThemeTint.Auto.name]
    }

    fun saveSeedColor(color: String) {
        settings[SEED_COLOR_KEY] = color
    }

    fun fetchSeedColor(): String {
        return settings[SEED_COLOR_KEY, "6E007F"]
    }

    fun saveIsDynamic(isDynamic: Boolean) {
        settings[IS_DYNAMIC_KEY] = isDynamic
    }

    fun fetchIsDynamic(): Boolean {
        return settings[IS_DYNAMIC_KEY, isCanInDynamic()]
    }

    fun saveColorMode(colorMode: String) {
        settings[COLOR_MODE_KEY] = colorMode
    }

    fun fetchColorMode(): String {
        return settings[COLOR_MODE_KEY, "0"]
    }

    fun saveIsHaze(isHaze: Boolean) {
        settings[IS_HAZE_KEY] = isHaze
    }

    fun fetchIsHaze(): Boolean = settings[IS_HAZE_KEY, true]

    fun saveIsMarkTable(isMarkTable: Boolean) {
        settings[IS_MARK_TABLE_KEY] = isMarkTable
    }

    fun fetchIsMarkTable() : Boolean =  settings[IS_MARK_TABLE_KEY, true]


    fun saveIsShowingPlusDs(isShowing: Boolean) {
        settings[IS_SHOWING_PLUS_DS_KEY] = isShowing
    }

    fun fetchIsShowingPlusDs() : Boolean = settings[IS_SHOWING_PLUS_DS_KEY, false]

    fun saveIsTransitionsEnabled(isEnabled: Boolean) {
        settings[IS_TRANSITIONS_ENABLED_KEY] = isEnabled
    }
    fun fetchIsTransitionsEnabled() = settings[IS_TRANSITIONS_ENABLED_KEY, true]

    fun saveFontSize(fontSize: Float) {
        settings[FONT_SIZE_KEY] = fontSize.coerceAtLeast(0.2f)
    }

    fun fetchFontSize() = settings[FONT_SIZE_KEY, 1f].coerceAtLeast(0.2f)

    fun fetchFontType() = settings[FONT_TYPE_KEY, 5]//.coerceAtLeast(0.2f)

    fun saveFontType(fontType: Int) {
        settings[FONT_TYPE_KEY] = fontType// fontSize.coerceAtLeast(0.2f)
    }


    fun saveIsAmoledEnabled(isEnabled: Boolean) {
        settings[IS_AMOLED_ENABLED_KEY] = isEnabled
    }
    fun fetchIsAmoledEnabled() = settings[IS_AMOLED_ENABLED_KEY, false]

    fun saveIsAvatarsEnabled(isEnabled: Boolean) {
        settings[IS_AVATARS_ENABLED_KEY] = isEnabled
    }
    fun fetchIsAvatarsEnabled() = settings[IS_AVATARS_ENABLED_KEY, true]

    fun saveIsRefreshButtonsEnabled(isEnabled: Boolean) {
        settings[IS_REFRESH_BUTTONS_ENABLED_KEY] = isEnabled
    }
    fun fetchIsRefreshButtonsEnabled() = settings[IS_REFRESH_BUTTONS_ENABLED_KEY, true]

    fun saveHardwareStatus(status: String) {
        settings[HARDWARE_STATUS_KEY] = status
    }
    fun fetchHardwareStatus() = settings[HARDWARE_STATUS_KEY, ""]

    fun saveIsLockedVerticalView(isLocked: Boolean) {
        println("SAVED!: !")
        settings[IS_LOCKED_VERTICAL_VIEW_KEY] = isLocked
    }
    fun fetchIsLockedVerticalView() : Boolean? = settings.getBooleanOrNull(IS_LOCKED_VERTICAL_VIEW_KEY)

    companion object {
        const val LANGUAGE_KEY = "languageKey"
        const val TINT_KEY = "tintKey"
        const val SEED_COLOR_KEY = "seedColorKey"
        const val IS_DYNAMIC_KEY = "isDynamicKey"
        const val COLOR_MODE_KEY = "colorModeKey"
        const val IS_HAZE_KEY = "isHazeKey"
        const val FONT_SIZE_KEY = "fontSizeKey"
        const val FONT_TYPE_KEY = "fontTypeKey"
        const val IS_MARK_TABLE_KEY = "isMarkTableKey"
        const val IS_SHOWING_PLUS_DS_KEY = "isShowingPlusDSKey"
        const val IS_TRANSITIONS_ENABLED_KEY = "isTransitionsEnabledKey"

        const val IS_AMOLED_ENABLED_KEY = "isAmoledEnabledKey"
        const val IS_REFRESH_BUTTONS_ENABLED_KEY = "isRefreshButtonsEnabledKey"
        const val IS_AVATARS_ENABLED_KEY = "isAvatarsEnabledKey"
        const val HARDWARE_STATUS_KEY = "hardwareStatusKey"
        const val IS_LOCKED_VERTICAL_VIEW_KEY = "isLockedVerticalViewKey"
    }

}