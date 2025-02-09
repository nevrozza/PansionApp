
import SettingsStore.Intent
import SettingsStore.Label
import SettingsStore.Message
import SettingsStore.State
import auth.RChangeLogin
import auth.RTerminateDeviceReceive
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import components.cAlertDialog.CAlertDialogComponent
import components.networkInterface.NetworkInterface
import deviceSupport.launchIO
import deviceSupport.withMain
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope

class SettingsExecutor(
    private val settingsRepository: SettingsRepository,
    private val authRepository: AuthRepository,
    private val nDevicesInterface: NetworkInterface,
    private val changeLoginDialog: CAlertDialogComponent
) : CoroutineExecutor<Intent, Unit, State, Message, Label>() {
    override fun executeIntent(intent: Intent) {
        when (intent) {
            Intent.ClickOnQuit -> quit()
            is Intent.ChangeColorMode -> dispatch(Message.ColorModeChanged(intent.colorMode))
            is Intent.FetchDevices -> fetchDevices()
            is Intent.TerminateDevice -> terminate(intent.id)
            is Intent.ESecondLogin -> dispatch(Message.ESecondLogin(intent.secondLogin))
            Intent.SaveSecondLogin -> saveSecondLogin()


            Intent.ChangeIsMarkTableDefault -> {
                settingsRepository.saveIsMarkTable(!state().isMarkTableDefault)
                dispatch(Message.IsMarkTableDefaultChanged(!state().isMarkTableDefault))
            }
            Intent.ChangeIsPlusDsStupsEnabled -> {
                settingsRepository.saveIsShowingPlusDs(!state().isPlusDsStupsEnabled)
                dispatch(Message.IsPlusDsStupsEnabledChanged(!state().isPlusDsStupsEnabled))
            }
        }
    }
    private fun saveSecondLogin() {
        scope.launchIO {
            changeLoginDialog.nInterface.nStartLoading()
            try {
                settingsRepository.changeLogin(RChangeLogin(
                    state().eSecondLogin
                ))
                withMain {
                    dispatch(
                        Message.SecondLoginChanged(
                            if (state().eSecondLogin.isBlank()) null
                            else state().eSecondLogin
                        )
                    )
                    changeLoginDialog.fullySuccess()
                }
            } catch (e: Throwable) {
                changeLoginDialog.nInterface.nError("Что-то пошло не так", e) {
                    changeLoginDialog.nInterface.goToNone()
                }
            }
        }
    }

    private fun terminate(id: String) {
        scope.launchIO {
            try {
                settingsRepository.terminateDevice(RTerminateDeviceReceive(id = id))
                fetchDevices()
            } catch (_: Throwable) {}
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun quit() {

        GlobalScope.launchIO {

            try {
                authRepository.logout()
            } catch (_: Throwable) {

            }
        }
    }

    private fun fetchDevices() {
        scope.launchIO {
            try {
                val devices = settingsRepository.fetchDevices()
                withMain {
                    dispatch(Message.DevicesFetched(devices.tokens.reversed()))
                    dispatch(Message.SecondLoginChanged(devices.secondLogin))
                }
            } catch (e: Throwable) {
                nDevicesInterface.nError("Ошибка", e) {
                    fetchDevices()
                }
            }
        }
    }
}
