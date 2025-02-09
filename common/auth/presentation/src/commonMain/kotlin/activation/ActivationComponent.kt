package activation

import AuthRepository
import SettingsRepository
import asValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import di.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow

class ActivationComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    private val output: (Output) -> Unit
) : ComponentContext by componentContext {
    private val authRepository: AuthRepository = Inject.instance()

    private val activationStore =
        instanceKeeper.getStore {
            ActivationStoreFactory(
                storeFactory = storeFactory,
                authRepository = authRepository
            ).create()
        }

    val model = activationStore.asValue()

    private val backCallback = BackCallback {
        if (model.value.step == ActivationStore.Step.Activation) {
            onEvent(ActivationStore.Intent.ChangeStep(ActivationStore.Step.Login))
        } else if (model.value.step == ActivationStore.Step.Login) {
            onEvent(ActivationStore.Intent.ChangeStep(ActivationStore.Step.Choice))
            updateBackCallback(false)
        }
    }

    init {
        backHandler.register(backCallback)
        updateBackCallback(false)
    }

    private fun updateBackCallback(isEnabled: Boolean) {
        // Set isEnabled to true if you want to override the back button
        backCallback.isEnabled = isEnabled // or false
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<ActivationStore.State> = activationStore.stateFlow

    fun onEvent(event: ActivationStore.Intent) {

        //ChangeStep(step=Login)
        //CheckToGoMain
        if(event.toString() == "ChangeStep(step=Login)") {
            updateBackCallback(true)
        }

        activationStore.accept(event)
    }

    fun navigateToMain() {
        onEvent(ActivationStore.Intent.ResetAll)
        onOutput(Output.NavigateToMain)
    }

    fun onOutput(output: Output) {
        output(output)
    }

    init {
        onEvent(ActivationStore.Intent.Init)
    }

    sealed class Output {
        data object NavigateToMain : Output()
        data class NavigateToLogin(val login: String) : Output()
        data object GoToScanner : Output()
    }
}