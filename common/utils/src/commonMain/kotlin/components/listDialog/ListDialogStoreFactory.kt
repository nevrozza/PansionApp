package components.listDialog

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory

class ListDialogStoreFactory(
    private val storeFactory: StoreFactory,
    private val name: String,
    private val executor: ListDialogExecutor
) {

    fun create(): ListDialogStore {
        return ListDialogStoreImpl()
    }

    private inner class ListDialogStoreImpl :
        ListDialogStore,
        Store<ListDialogStore.Intent, ListDialogStore.State, ListDialogStore.Label> by storeFactory.create(
            name = name+"ListDialogStore",
            initialState = ListDialogStore.State(),
            executorFactory = ::executor,
            reducer = ListDialogReducer
        )
}