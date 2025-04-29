import auth.RChangeLogin
import auth.RFetchAllDevicesResponse
import auth.RTerminateDeviceReceive
import registration.ScanRequestQRResponse
import registration.SendRegistrationRequestReceive

interface SettingsRepository {

    suspend fun scanRegistrationQR(formId: Int) : ScanRequestQRResponse
    suspend fun sendRegistrationRequest(
        r: SendRegistrationRequestReceive
    )
    suspend fun changeLogin(
        r: RChangeLogin
    )


    suspend fun fetchDevices(): RFetchAllDevicesResponse
    suspend fun terminateDevice(r: RTerminateDeviceReceive)


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

    fun saveIsMarkTable(isMarkTable: Boolean)
    fun fetchIsMarkTable() : Boolean

    fun saveIsShowingPlusDs(isShowing: Boolean)
    fun fetchIsShowingPlusDS() : Boolean

    fun saveIsTransitionsEnabled(isEnabled: Boolean)
    fun fetchIsTransitionsEnabled() : Boolean

    fun saveFontSize(fontSize: Float)
    fun fetchFontSize() : Float

    fun saveFontType(fontType: Int)
    fun fetchFontType() : Int

    fun saveIsAmoled(isAmoled: Boolean)
    fun fetchIsAmoled(): Boolean

    fun saveIsRefreshButtons(isRefreshButtons: Boolean)
    fun fetchIsRefreshButtons(): Boolean

    fun saveIsHideKeyboardButton(isHideKeyboardButton: Boolean)
    fun fetchIsHideKeyboardButton(): Boolean

    fun saveIsLockedVerticalView(isLocked: Boolean)
    fun fetchIsLockedVerticalView(): Boolean?

    fun saveIsAvatars(isAvatars: Boolean)
    fun fetchIsAvatars(): Boolean

    fun saveHardwareStatus(status: String)
    fun fetchHardwareStatus(): String
//    fun fetchSettingsScreenData(): SettingsScreenData
}

//data class SettingsScreenData(
//    val
//)