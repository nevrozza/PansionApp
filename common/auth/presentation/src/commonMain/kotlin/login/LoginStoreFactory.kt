package login

import AuthRepository
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import login.LoginStore.Intent
import login.LoginStore.Label
import login.LoginStore.State

class LoginStoreFactory(private val storeFactory: StoreFactory, private val authRepository: AuthRepository) {

    fun create(): LoginStore {
        return LoginStoreImpl()
    }

    private inner class LoginStoreImpl :
        LoginStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "LoginStore",
            initialState = State(),
            executorFactory = { LoginExecutor(authRepository) },
            reducer = LoginReducer
        )
}