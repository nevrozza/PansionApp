package activation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import decompose.DefaultMVIComponent

class ActivationComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    private val output: (Output) -> Unit
) : ComponentContext by componentContext, DefaultMVIComponent<ActivationStore.Intent, ActivationStore.State, Nothing> {
    override val store =
        instanceKeeper.getStore {
            ActivationStoreFactory(
                storeFactory = storeFactory
            ).create()
        }

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


    override fun onEvent(event: ActivationStore.Intent) {
        if(event.toString() == "ChangeStep(step=Login)") {
            updateBackCallback(true)
        }
        super.onEvent(event)
    }

    fun navigateToMain() {
        onEvent(ActivationStore.Intent.ResetAll)
        onOutput(Output.NavigateToMain)
    }

    fun onOutput(output: Output) {
        output(output)
    }

    sealed class Output {
        data object NavigateToMain : Output()
        data class NavigateToLogin(val login: String) : Output()
        data object GoToScanner : Output()
    }
}