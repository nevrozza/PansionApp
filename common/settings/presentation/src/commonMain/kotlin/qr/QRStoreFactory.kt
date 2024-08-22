package qr

import AuthRepository
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.cBottomSheet.CBottomSheetComponent
import components.networkInterface.NetworkInterface
import qr.QRStore.Intent
import qr.QRStore.Label
import qr.QRStore.State

class QRStoreFactory(
    private val storeFactory: StoreFactory,
    private val isRegistration: Boolean,
    private val nInterface: NetworkInterface,
    private val authRepository: AuthRepository,
    private val authBottomSheet: CBottomSheetComponent,
) {

    fun create(): QRStore {
        return QRStoreImpl()
    }

    private inner class QRStoreImpl :
        QRStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "QRStore",
            initialState = QRStore.State(isRegistration = isRegistration),
            executorFactory = { QRExecutor(
                nInterface = nInterface,
                authRepository = authRepository,
                authBottomSheet = authBottomSheet
            ) },
            reducer = QRReducer
        )
}