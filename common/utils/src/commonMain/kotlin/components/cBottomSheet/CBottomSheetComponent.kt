package components.cBottomSheet

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.networkInterface.NetworkInterface
import decompose.DefaultMVIComponent

class CBottomSheetComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    name: String? = null
) : ComponentContext by componentContext,
    DefaultMVIComponent<CBottomSheetStore.Intent, CBottomSheetStore.State, CBottomSheetStore.Label> {
    val nInterface = if (name != null) NetworkInterface(
        childContext(name + "NInterface" + "CONTEXT"),
        storeFactory,
        name + "NInterface"
    ) else NetworkInterface(
        componentContext,
        storeFactory,
        null
    )
    val nModel = nInterface.networkModel
    override val store =
        if (name != null)
            instanceKeeper.getStore(key = name) {
                CBottomSheetStoreFactory(
                    storeFactory = storeFactory,
                ).create()
            } else instanceKeeper.getStore() {
            CBottomSheetStoreFactory(
                storeFactory = storeFactory,
            ).create()
        }


    fun fullySuccess() {
        nInterface.nSuccess()
        onEvent(CBottomSheetStore.Intent.HideSheet)
    }

//    private val backCallback = BackCallback {
//        onEvent(CBottomSheetStore.Intent.HideSheet)
//    }
//    init {
//        backHandler.register(backCallback)
//    }

}