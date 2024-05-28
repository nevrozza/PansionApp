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

    override fun saveSeedColor(color: String) {
        cacheDataSource.saveSeedColor(color)
    }

    override fun fetchSeedColor(): String {
        return cacheDataSource.fetchSeedColor()
    }

    override fun saveIsDynamic(isDynamic: Boolean) {
        cacheDataSource.saveIsDynamic(isDynamic)
    }

    override fun fetchIsDynamic(): Boolean {
        return cacheDataSource.fetchIsDynamic()
    }
}