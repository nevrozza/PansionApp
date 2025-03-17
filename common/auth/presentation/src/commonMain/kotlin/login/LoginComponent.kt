package login

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import decompose.DefaultMVIComponent

class LoginComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    private val login: String,
    private val output: (Output) -> Unit
) : ComponentContext by componentContext, DefaultMVIComponent<LoginStore.Intent, LoginStore.State, LoginStore.Label> {

    override val store =
        instanceKeeper.getStore {
            LoginStoreFactory(
                storeFactory = storeFactory,
                executor = LoginExecutor(),
                state = LoginStore.State(login = login)
            ).create()
        }


    fun onOutput(output: Output) {
        output(output)
    }

    sealed class Output {
        data object NavigateToMain : Output()
        data object BackToActivation : Output()
    }
}
