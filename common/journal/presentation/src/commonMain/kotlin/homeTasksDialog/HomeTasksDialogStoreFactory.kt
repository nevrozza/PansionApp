package homeTasksDialog

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import homeTasksDialog.HomeTasksDialogStore.Intent
import homeTasksDialog.HomeTasksDialogStore.Label
import homeTasksDialog.HomeTasksDialogStore.State

class HomeTasksDialogStoreFactory(
    private val storeFactory: StoreFactory,
    private val state: State,
    private val executor: HomeTasksDialogExecutor
) {

    fun create(): HomeTasksDialogStore {
        return HomeTasksDialogStoreImpl()
    }

    private inner class HomeTasksDialogStoreImpl :
        HomeTasksDialogStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "HomeTasksDialogStore",
            initialState = state,
            executorFactory = ::executor,
            reducer = HomeTasksDialogReducer
        )
}