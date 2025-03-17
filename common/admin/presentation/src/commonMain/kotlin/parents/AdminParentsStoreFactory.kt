package parents

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import parents.AdminParentsStore.Intent
import parents.AdminParentsStore.Label
import parents.AdminParentsStore.State

class AdminParentsStoreFactory(
    private val storeFactory: StoreFactory,
    private val executor: AdminParentsExecutor
) {

    fun create(): AdminParentsStore {
        return AdminParentsStoreImpl()
    }

    private inner class AdminParentsStoreImpl :
        AdminParentsStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "AdminParentsStore",
            initialState = State(),
            executorFactory = ::executor,
            reducer = AdminParentsReducer
        )
}