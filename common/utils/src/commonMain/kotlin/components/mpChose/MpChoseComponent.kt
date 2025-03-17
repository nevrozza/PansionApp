package components.mpChose

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.networkInterface.NetworkInterface
import decompose.DefaultMVIComponent


class MpChoseComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    name: String,
    customOnDismiss: (() -> Unit)? = null
) : ComponentContext by componentContext,
    DefaultMVIComponent<MpChoseStore.Intent, MpChoseStore.State, MpChoseStore.Label> {
    val nInterface = NetworkInterface(
        componentContext,
        storeFactory,
        name + "NInterface"
    )
    val nModel = nInterface.networkModel
    override val store =
        instanceKeeper.getStore(key = name) {
            MpChoseStoreFactory(
                storeFactory = storeFactory,
                name = name,
                executor = MpChoseExecutor(
                    nInterface = nInterface,
                    customOnDismiss = customOnDismiss
                )
            ).create()
        }


//    private val backCallback = BackCallback {
//        onEvent(MpChoseStore.Intent.HideDialog)
//    }
//    init {
    //backHandler.register(backCallback)
//    }


}