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


class MpChoseComponent(
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
            MpChoseStoreFactory(
                storeFactory = storeFactory,
                name = name,
                networkInterface = nInterface,
                customOnDismiss = customOnDismiss
            ).create()
        }


    val model: Value<MpChoseStore.State> = listStore.asValue()

    private val backCallback = BackCallback {
        onEvent(MpChoseStore.Intent.HideDialog)
    }


    init {
        //backHandler.register(backCallback)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<MpChoseStore.State> = listStore.stateFlow

    fun onEvent(event: MpChoseStore.Intent) {
        listStore.accept(event)
    }



}