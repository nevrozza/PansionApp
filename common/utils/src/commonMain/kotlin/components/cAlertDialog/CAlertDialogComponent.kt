package components.cAlertDialog

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


//return@ListComponent model.value.forms.map {
//    ListItem(
//        id = it.id,
//        text = "${it.classNum}${if (it.name.length < 2) "-" else " "}${it.name} класс"
//    )
//}
class CAlertDialogComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    name: String,
    private val onAcceptClick: () -> Unit,
    private val onDeclineClick: (() -> Unit)? = null,
    private val needDelayWhenHide: Boolean = false
//    private val isDeclineShowing
) : ComponentContext by componentContext {
    val nInterface = NetworkInterface(
        componentContext,
        storeFactory,
        name+"NInterface"
    )
    val nModel = nInterface.networkModel

    private val cAlertDialogStore =
        instanceKeeper.getStore(key = name) {
            CAlertDialogStoreFactory(
                storeFactory = storeFactory,
                onAcceptClick = { this.onEvent(CAlertDialogStore.Intent.HideDialog); onAcceptClick()},
                onDeclineClick = if (onDeclineClick != null) {
                    {
                        this.onEvent(CAlertDialogStore.Intent.HideDialog)
                        onDeclineClick!!()
                    }

                } else {
                    {
                        this.onEvent(CAlertDialogStore.Intent.HideDialog)
                    }
                },
                needDelayWhenHide = needDelayWhenHide//onDeclineClick
//                authRepository = authRepository
            ).create()
        }

    val model: Value<CAlertDialogStore.State> = cAlertDialogStore.asValue()

//    private val backCallback = BackCallback {
//        onEvent(CAlertDialogStore.Intent.HideDialog)
//    }
//
//
//    init {
//        backHandler.register(backCallback)
//    }

    fun fullySuccess() {
        nInterface.nSuccess()
        onEvent(CAlertDialogStore.Intent.HideDialog)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<CAlertDialogStore.State> = cAlertDialogStore.stateFlow

    fun onEvent(event: CAlertDialogStore.Intent) {
        cAlertDialogStore.accept(event)
    }
}