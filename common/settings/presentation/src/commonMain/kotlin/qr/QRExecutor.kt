package qr

import AuthRepository
import CDispatcher
import auth.RFetchQrTokenResponse
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import components.cBottomSheet.CBottomSheetComponent
import components.cBottomSheet.CBottomSheetStore
import components.networkInterface.NetworkInterface
import kotlinx.coroutines.launch
import qr.QRStore.Intent
import qr.QRStore.Label
import qr.QRStore.State
import qr.QRStore.Message

class QRExecutor(
    private val nInterface: NetworkInterface,
    private val authBottomSheet: CBottomSheetComponent,
    private val authRepository: AuthRepository,
) : CoroutineExecutor<Intent, Unit, State, Message, Label>() {
    override fun executeIntent(intent: Intent) {
        when (intent) {
            is Intent.ChangeCode -> dispatch(Message.CodeChanged(intent.code))
            Intent.SendToServer -> sendToServer()
            Intent.SendToServerAtAll -> sendToServerAtAll()
            Intent.GoToNone -> nInterface.goToNone()
        }
    }

    private fun sendToServer() {
        scope.launch(CDispatcher) {
            nInterface.nStartLoading()
            try {
                if (state().code.subSequence(0, 4) == "AUTH") {
                    val r = authRepository.activateQRToken(
                        RFetchQrTokenResponse(
                            token = state().code
                        )
                    )
                    scope.launch {
                        nInterface.nSuccess()
                        dispatch(Message.AuthReceived(deviceName = r.deviceName, deviceType = r.deviceType))
                        authBottomSheet.onEvent(CBottomSheetStore.Intent.ShowSheet)
                    }
                }
                scope.launch {
                    nInterface.nSuccess()
                }
            } catch (_: Throwable) {
                nInterface.nError("Не удалось") {

                }
            }
        }
    }
    private fun sendToServerAtAll() {
        scope.launch(CDispatcher) {
            nInterface.nStartLoading()
            try {
                if (state().code.subSequence(0, 4) == "AUTH") {
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
