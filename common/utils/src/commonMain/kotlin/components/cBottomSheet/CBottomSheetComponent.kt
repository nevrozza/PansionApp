package components.cBottomSheet

import asValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.networkInterface.NetworkInterface

class CBottomSheetComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    name: String
) : ComponentContext by componentContext {
    val nInterface = NetworkInterface(
        componentContext,
        storeFactory
    )
    val nModel = nInterface.networkModel
    private val cBottomSheetStore =
        instanceKeeper.getStore(key = name) {
            CBottomSheetStoreFactory(
                storeFactory = storeFactory,
//                networkInterface = nInterface
            ).create()
        }


    val model: Value<CBottomSheetStore.State> = cBottomSheetStore.asValue()

    private val backCallback = BackCallback {
        onEvent(CBottomSheetStore.Intent.HideSheet)
    }

    fun fullySuccess() {
        nInterface.nSuccess()
        onEvent(CBottomSheetStore.Intent.HideSheet)
    }

    init {
        backHandler.register(backCallback)
    }

//    @OptIn(ExperimentalCoroutinesApi::class)
//    val state: StateFlow<CBottomSheetStore.State> = listStore.stateFlow

    fun onEvent(event: CBottomSheetStore.Intent) {
        cBottomSheetStore.accept(event)
    }
}