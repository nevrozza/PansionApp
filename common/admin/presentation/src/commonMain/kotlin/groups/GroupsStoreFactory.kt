package groups

import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import groups.GroupsStore.Intent
import groups.GroupsStore.Label
import groups.GroupsStore.State

class GroupsStoreFactory(
    private val storeFactory: StoreFactory,
    private val executor: GroupsExecutor
) {

    fun create(): GroupsStore {
        return GroupsStoreImpl()
    }

    private inner class GroupsStoreImpl :
        GroupsStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "GroupsStore",
            initialState = State(),
            executorFactory = ::executor,
            reducer = GroupsReducer,
            bootstrapper = SimpleBootstrapper(Unit)
        )
}