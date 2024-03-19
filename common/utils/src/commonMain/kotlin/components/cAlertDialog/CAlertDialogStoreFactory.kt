package components.cAlertDialog

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory

class CAlertDialogStoreFactory(
    private val storeFactory: StoreFactory,
    private val onAcceptClick: () -> Unit,
    private val onDeclineClick: () -> Unit,
    private val needDelayWhenHide: Boolean
) {

    fun create(): CAlertDialogStore {
        return ListDialogStoreImpl()
    }

    private inner class ListDialogStoreImpl :
        CAlertDialogStore,
        Store<CAlertDialogStore.Intent, CAlertDialogStore.State, CAlertDialogStore.Label> by storeFactory.create(
            name = "cAlertDialogStore",
            initialState = CAlertDialogStore.State(
                onAcceptClick = onAcceptClick,
                onDeclineClick = onDeclineClick,
                needDelayWhenHide = needDelayWhenHide
            ),
            executorFactory = { CAlertDialogExecutor() },
            reducer = CAlertDialogReducer
        )
}