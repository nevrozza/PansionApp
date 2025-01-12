import auth.RChangeLogin
import auth.RFetchAllDevicesResponse
import auth.RTerminateDeviceReceive
import ktor.KtorSettingsRemoteDataSource
import registration.ScanRequestQRReceive
import registration.ScanRequestQRResponse
import registration.SendRegistrationRequestReceive
import settings.SettingsDataSource

class SettingsRepositoryImpl(
    private val cacheDataSource: SettingsDataSource,
    private val remoteDataSource: KtorSettingsRemoteDataSource,
): SettingsRepository {
    override suspend fun scanRegistrationQR(formId: Int) : ScanRequestQRResponse{
        return remoteDataSource.scanRegistrationQR(
            ScanRequestQRReceive(
                formId = formId
            )
        )
    }

    override suspend fun sendRegistrationRequest(r: SendRegistrationRequestReceive) {
        remoteDataSource.sendRegistrationRequest(r)
    }

    override suspend fun changeLogin(r: RChangeLogin) {
        remoteDataSource.changeLogin(r)
    }

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

    override fun saveIsMarkTable(isMarkTable: Boolean) {
        cacheDataSource.saveIsMarkTable(isMarkTable)
    }

    override fun fetchIsMarkTable(): Boolean = cacheDataSource.fetchIsMarkTable()

    override fun saveIsShowingPlusDs(isShowing: Boolean) {
        cacheDataSource.saveIsShowingPlusDs(isShowing)
    }

    override fun fetchIsShowingPlusDS(): Boolean = cacheDataSource.fetchIsShowingPlusDs()

    override fun saveIsTransitionsEnabled(isEnabled: Boolean) {
        cacheDataSource.saveIsTransitionsEnabled(isEnabled)
    }

    override fun fetchIsTransitionsEnabled(): Boolean = cacheDataSource.fetchIsTransitionsEnabled()
    override fun saveFontSize(fontSize: Float) {
        cacheDataSource.saveFontSize(fontSize)
    }
    override fun fetchFontSize(): Float = cacheDataSource.fetchFontSize()

    override fun saveFontType(fontType: Int) {
        cacheDataSource.saveFontType(fontType)
    }
    override fun fetchFontType(): Int = cacheDataSource.fetchFontType()
    override fun saveIsAmoled(isAmoled: Boolean) = cacheDataSource.saveIsAmoledEnabled(isAmoled)
    override fun fetchIsAmoled(): Boolean = cacheDataSource.fetchIsAmoledEnabled()

    override fun saveIsRefreshButtons(isRefreshButtons: Boolean) = cacheDataSource.saveIsRefreshButtonsEnabled(isRefreshButtons)

    override fun fetchIsRefreshButtons(): Boolean = cacheDataSource.fetchIsRefreshButtonsEnabled()
    override fun saveIsAvatars(isAvatars: Boolean) = cacheDataSource.saveIsAvatarsEnabled(isAvatars)

    override fun fetchIsAvatars(): Boolean = cacheDataSource.fetchIsAvatarsEnabled()
    override fun saveHardwareStatus(status: String) = cacheDataSource.saveHardwareStatus(status)

    override fun fetchHardwareStatus(): String = cacheDataSource.fetchHardwareStatus()
}