package components.listDialog

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.store.create

class ListDialogStoreFactory(private val storeFactory: StoreFactory) {

    fun create(): ListDialogStore {
        return ListDialogStoreImpl()
    }

    private inner class ListDialogStoreImpl :
        ListDialogStore,
        Store<ListDialogStore.Intent, ListDialogStore.State, ListDialogStore.Label> by storeFactory.create(
            name = "ListDialogStore",
            initialState = ListDialogStore.State(),
            executorFactory = { ListDialogExecutor() },
            reducer = ListDialogReducer
        )
}