interface SettingsRepository {
    fun saveTint(tint: String)
    fun fetchTint(): String

    fun saveLanguage(language: String)
    fun fetchLanguage(): String

    fun saveColor(color: String)
    fun fetchColor(): String

//    fun fetchSettingsScreenData(): SettingsScreenData
}

//data class SettingsScreenData(
//    val
//)