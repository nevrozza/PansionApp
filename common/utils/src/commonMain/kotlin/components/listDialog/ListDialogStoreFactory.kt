package components.listDialog

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.networkInterface.NetworkInterface

class ListDialogStoreFactory(
    private val storeFactory: StoreFactory,
    private val networkInterface: NetworkInterface,
    private val customOnDismiss: (() -> Unit)? = null
) {

    fun create(): ListDialogStore {
        return ListDialogStoreImpl()
    }

    private inner class ListDialogStoreImpl :
        ListDialogStore,
        Store<ListDialogStore.Intent, ListDialogStore.State, ListDialogStore.Label> by storeFactory.create(
            name = "ListDialogStore",
            initialState = ListDialogStore.State(),
            executorFactory = { ListDialogExecutor(nInterface = networkInterface, customOnDismiss = customOnDismiss) },
            reducer = ListDialogReducer
        )
}