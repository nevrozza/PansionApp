import settings.SettingsDataSource

class SettingsRepositoryImpl(
    private val cacheDataSource: SettingsDataSource
): SettingsRepository {

    override fun saveTint(tint: String) {
        cacheDataSource.saveTint(tint)
    }

    override fun fetchTint(): String {
        return cacheDataSource.fetchTint()
    }

    override fun saveLanguage(language: String) {
        cacheDataSource.saveLanguage(language)
    }

    override fun fetchLanguage(): String {
        return cacheDataSource.fetchLanguage()
    }

    override fun saveColor(color: String) {
        cacheDataSource.saveColor(color)
    }

    override fun fetchColor(): String {
        return cacheDataSource.fetchColor()
    }

}