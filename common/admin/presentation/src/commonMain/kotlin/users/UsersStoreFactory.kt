package users

import AdminRepository
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.arkivanov.mvikotlin.core.store.StoreFactory
import users.UsersStore.Intent
import users.UsersStore.Label
import users.UsersStore.State
import users.UsersStore.Message

class UsersStoreFactory(private val storeFactory: StoreFactory, private val adminRepository: AdminRepository) {

    fun create(): UsersStore {
        return UsersStoreImpl()
    }

    private inner class UsersStoreImpl :
        UsersStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "UsersStore",
            initialState = UsersStore.State(),
            executorFactory = { UsersExecutor(adminRepository) },
            reducer = UsersReducer
        )
}