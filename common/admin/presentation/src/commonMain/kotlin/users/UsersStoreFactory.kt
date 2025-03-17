package users

import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import users.UsersStore.Intent
import users.UsersStore.Label
import users.UsersStore.State

class UsersStoreFactory(
    private val storeFactory: StoreFactory,
    private val executor: UsersExecutor
) {

    fun create(): UsersStore =
        object :
        UsersStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "UsersStore",
            initialState = State(),
            executorFactory = ::executor,
            reducer = UsersReducer,
            bootstrapper = SimpleBootstrapper(Unit)
        ) {}
}
