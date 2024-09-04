package qr

import AuthRepository
import SettingsRepository
import asValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.cBottomSheet.CBottomSheetComponent
import components.networkInterface.NetworkInterface
import di.Inject

class QRComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    val isRegistration: Boolean,
    private val output: (Output) -> Unit
) : ComponentContext by componentContext {

    private val settingsRepository: SettingsRepository = Inject.instance()
    private val authRepository: AuthRepository = Inject.instance()

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

    private val qrStore =
        instanceKeeper.getStore {
            QRStoreFactory(
                storeFactory = storeFactory,
                isRegistration = isRegistration,
                nInterface = nInterface,
                authRepository = authRepository,
                authBottomSheet = authBottomSheet,
                registerBottomSheet = registerBottomSheet,
                settingsRepository = settingsRepository
            ).create()
        }

    val model = qrStore.asValue()


    fun onEvent(event: QRStore.Intent) {
        qrStore.accept(event)
    }

    fun onOutput(output: Output) {
        output(output)
    }

    sealed class Output {
        data object Back : Output()
    }


    init {
    }
}