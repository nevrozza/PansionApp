package qr

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import qr.QRStore.Intent
import qr.QRStore.Label
import qr.QRStore.State

class QRStoreFactory(
    private val storeFactory: StoreFactory,
    private val state: State,
    private val executor: QRExecutor
) {

    fun create(): QRStore {
        return QRStoreImpl()
    }

    private inner class QRStoreImpl :
        QRStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "QRStore",
            initialState = state,
            executorFactory = ::executor,
            reducer = QRReducer
        )
}