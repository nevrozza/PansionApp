package qr

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.cBottomSheet.CBottomSheetComponent
import components.networkInterface.NetworkInterface
import decompose.DefaultMVIComponent

class QRComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    val isRegistration: Boolean,
    private val output: (Output) -> Unit
) : ComponentContext by componentContext, DefaultMVIComponent<QRStore.Intent, QRStore.State, QRStore.Label> {


    val authBottomSheet = CBottomSheetComponent(
        componentContext = childContext("QRCBottomSheetComponentCONTEXT"),
        storeFactory = storeFactory,
        name = "QRCBottomSheetComponent"
    )
    val registerBottomSheet = CBottomSheetComponent(
        componentContext = childContext("REGISTERQRCBottomSheetComponentCONTEXT"),
        storeFactory = storeFactory,
        name = "REGISTERQRCBottomSheetComponent"
    )

    val nInterface = NetworkInterface(
        componentContext = childContext("QRNetworkInterfaceCONTEXT"),
        storeFactory = storeFactory,
        name = "QRNetworkInterface"
    )

    override val store =
        instanceKeeper.getStore {
            QRStoreFactory(
                storeFactory = storeFactory,
                state = QRStore.State(isRegistration = isRegistration),
                executor = QRExecutor(
                    nInterface = nInterface,
                    authBottomSheet = authBottomSheet,
                    registerBottomSheet = registerBottomSheet
                )
            ).create()
        }
    fun onOutput(output: Output) {
        output(output)
    }

    sealed class Output {
        data object Back : Output()
    }

}