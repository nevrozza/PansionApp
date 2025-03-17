package components.cAlertDialog

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory

class CAlertDialogStoreFactory(
    private val storeFactory: StoreFactory,
    private val state: CAlertDialogStore.State
) {

    fun create(): CAlertDialogStore {
        return ListDialogStoreImpl()
    }

    private inner class ListDialogStoreImpl :
        CAlertDialogStore,
        Store<CAlertDialogStore.Intent, CAlertDialogStore.State, CAlertDialogStore.Label> by storeFactory.create(
            name = "cAlertDialogStore",
            initialState = state,
            executorFactory = { CAlertDialogExecutor() },
            reducer = CAlertDialogReducer
        )
}