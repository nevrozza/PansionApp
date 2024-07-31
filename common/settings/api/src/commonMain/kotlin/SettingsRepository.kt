interface SettingsRepository {
    fun saveTint(tint: String)
    fun fetchTint(): String

    fun saveLanguage(language: String)
    fun fetchLanguage(): String

    fun saveSeedColor(color: String)
    fun fetchSeedColor(): String

    fun saveIsDynamic(isDynamic: Boolean)
    fun fetchIsDynamic(): Boolean

    fun saveColorMode(colorMode: String)
    fun fetchColorMode() : String

    fun saveIsHaze(isHaze: Boolean)
    fun fetchIsHaze() : Boolean
//    fun fetchSettingsScreenData(): SettingsScreenData
}

//data class SettingsScreenData(
//    val
//)