package components.mpChose

import asValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import components.networkInterface.NetworkInterface
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow


class mpChoseComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    name: String,
    customOnDismiss: (() -> Unit)? = null
) : ComponentContext by componentContext {
    val nInterface = NetworkInterface(
        componentContext,
        storeFactory,
        name+"NInterface"
    )
    val nModel = nInterface.networkModel
    private val listStore =
        instanceKeeper.getStore(key = name) {
            mpChoseStoreFactory(
                storeFactory = storeFactory,
                name = name,
                networkInterface = nInterface,
                customOnDismiss = customOnDismiss
            ).create()
        }


    val model: Value<mpChoseStore.State> = listStore.asValue()

    private val backCallback = BackCallback {
        onEvent(mpChoseStore.Intent.HideDialog)
    }


    init {
        //backHandler.register(backCallback)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<mpChoseStore.State> = listStore.stateFlow

    fun onEvent(event: mpChoseStore.Intent) {
        listStore.accept(event)
    }



}