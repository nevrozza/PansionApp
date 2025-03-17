package login

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import login.LoginStore.Intent
import login.LoginStore.Label
import login.LoginStore.State

class LoginStoreFactory(
    private val storeFactory: StoreFactory,
    private val state: State,
    private val executor: LoginExecutor
) {

    fun create(): LoginStore {
        return LoginStoreImpl()
    }

    private inner class LoginStoreImpl :
        LoginStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "LoginStore",
            initialState = state,
            executorFactory = ::executor,
            reducer = LoginReducer
        )
}