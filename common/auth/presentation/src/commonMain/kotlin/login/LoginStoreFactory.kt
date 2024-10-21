package login

import AuthRepository
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import login.LoginStore.Intent
import login.LoginStore.Label
import login.LoginStore.State
import kotlin.math.log

class LoginStoreFactory(
    private val storeFactory: StoreFactory, private val authRepository: AuthRepository,
    private val login: String
) {

    fun create(): LoginStore {
        return LoginStoreImpl()
    }

    private inner class LoginStoreImpl :
        LoginStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "LoginStore",
            initialState = State(
                login = login
            ),
            executorFactory = { LoginExecutor(authRepository) },
            reducer = LoginReducer
        )
}