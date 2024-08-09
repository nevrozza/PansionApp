import auth.RFetchAllDevicesResponse
import auth.RTerminateDeviceReceive
import ktor.KtorSettingsRemoteDataSource
import settings.SettingsDataSource

class SettingsRepositoryImpl(
    private val cacheDataSource: SettingsDataSource,
    private val remoteDataSource: KtorSettingsRemoteDataSource,
): SettingsRepository {
    override suspend fun fetchDevices(): RFetchAllDevicesResponse {
        return remoteDataSource.fetchDevices()
    }

    override suspend fun terminateDevice(r: RTerminateDeviceReceive) {
        remoteDataSource.terminateDevice(r)
    }

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

    override fun saveColorMode(colorMode: String) {
        cacheDataSource.saveColorMode(colorMode)
    }

    override fun fetchColorMode(): String {
        return cacheDataSource.fetchColorMode()
    }

    override fun saveIsHaze(isHaze: Boolean) {
       cacheDataSource.saveIsHaze(isHaze)
    }

    override fun fetchIsHaze(): Boolean {
        return cacheDataSource.fetchIsHaze()
    }
}