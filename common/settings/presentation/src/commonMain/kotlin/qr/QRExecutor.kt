package qr

import AuthRepository
import CDispatcher
import CommonPlatformConfiguration
import PlatformConfiguration
import SettingsRepository
import auth.RFetchQrTokenResponse
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import components.cBottomSheet.CBottomSheetComponent
import components.cBottomSheet.CBottomSheetStore
import components.networkInterface.NetworkInterface
import di.Inject
import kotlinx.coroutines.launch
import qr.QRStore.Intent
import qr.QRStore.Label
import qr.QRStore.State
import qr.QRStore.Message
import registration.RegistrationRequest
import registration.SendRegistrationRequestReceive

class QRExecutor(
    private val nInterface: NetworkInterface,
    private val authBottomSheet: CBottomSheetComponent,
    private val registerBottomSheet: CBottomSheetComponent,
    private val authRepository: AuthRepository,
    private val settingsRepository: SettingsRepository,
) : CoroutineExecutor<Intent, Unit, State, Message, Label>() {
    override fun executeIntent(intent: Intent) {
        when (intent) {
            is Intent.ChangeCode ->
                dispatch(Message.CodeChanged(intent.code))

            Intent.SendToServer -> sendToServer()
            Intent.SendToServerAtAll -> sendToServerAtAll()
            Intent.GoToNone -> nInterface.goToNone()

            is Intent.ChangeDateDialogShowing -> dispatch(Message.DateDialogShowingChanged(intent.isShowing))

            is Intent.ChangeCName -> dispatch(Message.CNameChanged(intent.name))
            is Intent.ChangeCSurname -> dispatch(Message.CSurnameChanged(intent.surname))
            is Intent.ChangeCPraname -> dispatch(Message.CPranameChanged(intent.praname))
            is Intent.ChangeCBirthday -> dispatch(Message.CBirthdayChanged(intent.birthday))
            is Intent.ChangeCParentFirstFIO -> dispatch(Message.CParentFirstFIOChanged(intent.fio))
            is Intent.ChangeCParentSecondFIO -> dispatch(Message.CParentSecondFIOChanged(intent.fio))
        }
    }

    private fun sendToServer() {
        scope.launch(CDispatcher) {
            nInterface.nStartLoading()
            try {

                val auth = state().code.split("AUTH").lastOrNull()
                if (auth != state().code && !state().isRegistration) {
                    val r = authRepository.activateQRToken(
                        RFetchQrTokenResponse(
                            token = "AUTH$auth"
                        )
                    )
                    scope.launch {
                        nInterface.nSuccess()
                        dispatch(
                            Message.AuthReceived(
                                deviceName = r.deviceName,
                                deviceType = r.deviceType
                            )
                        )
                        authBottomSheet.onEvent(CBottomSheetStore.Intent.ShowSheet)
                    }
                }

                val form = state().code.split("Form").lastOrNull()
                println("CHECK: ${form}")
                if (form != state().code && state().isRegistration) {
                    if (form?.toIntOrNull() != null) {
                        val r = settingsRepository.scanRegistrationQR(form.toInt())
                        scope.launch {
                            nInterface.nSuccess()
                            dispatch(
                                Message.FormReceived(
                                    formName = r.formName
                                )
                            )
                            registerBottomSheet.onEvent(CBottomSheetStore.Intent.ShowSheet)
                        }
                    }
                }
                scope.launch {
                    nInterface.nSuccess()
                }
            } catch (e: Throwable) {
                println("CHECK1: ${e}")
                nInterface.nError("Не удалось") {

                }
            }
        }
    }

    private fun sendToServerAtAll() {
        scope.launch(CDispatcher) {
            nInterface.nStartLoading()
            try {
                if (state().isRegistration) {
                    val platform = Inject.instance<CommonPlatformConfiguration>()
                    settingsRepository.sendRegistrationRequest(
                        SendRegistrationRequestReceive(
                            deviceId = platform.deviceId,
                            request = RegistrationRequest(
                                name = state().cName,
                                surname = state().cSurname,
                                praname = state().cPraname ?: "",
                                birthday = state().cBirthday,
                                fioFather = state().cParentSecondFIO,
                                fioMother = state().cParentFirstFIO,
                                avatarId = state().cAvatarId,
                                formId = state().code.split("Form").last().toInt()
                            )
                        )
                    )

                    scope.launch {
                        dispatch(Message.LoginChanged(
                            "Готово!\nВы увидите свой логин при запуске приложения, когда Вашу заявку одобрят"
                        ))
                    }
//                    authRepository.activateQRTokenAtAll(
//                        RFetchQrTokenResponse(
//                            token = state().code
//                        )
//                    )
                } else {
                    authRepository.activateQRTokenAtAll(
                        RFetchQrTokenResponse(
                            token = state().code
                        )
                    )
                }
                scope.launch {
                    nInterface.nSuccess()
                    authBottomSheet.onEvent(CBottomSheetStore.Intent.HideSheet)
                    dispatch(Message.CodeChanged(""))
                }
            } catch (_: Throwable) {
                nInterface.nError("Не удалось") {

                }
            }
        }
    }
}
