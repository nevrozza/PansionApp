package admin

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.arkivanov.mvikotlin.core.store.StoreFactory
import admin.AdminStore.Intent
import admin.AdminStore.Label
import admin.AdminStore.State
import admin.AdminStore.Message

class AdminStoreFactory(private val storeFactory: StoreFactory) {

    fun create(): AdminStore {
        return AdminStoreImpl()
    }

    private inner class AdminStoreImpl :
        AdminStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "AdminStore",
            initialState = AdminStore.State(),
            executorFactory = { AdminExecutor() },
            reducer = AdminReducer
        )
}