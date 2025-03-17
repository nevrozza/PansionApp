package components.cAlertDialog

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.networkInterface.NetworkInterface
import decompose.DefaultMVIComponent


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
) : ComponentContext by componentContext, DefaultMVIComponent<CAlertDialogStore.Intent, CAlertDialogStore.State, CAlertDialogStore.Label> {
    val nInterface = NetworkInterface(
        componentContext,
        storeFactory,
        name+"NInterface"
    )
    val nModel = nInterface.networkModel

    override val store =
        instanceKeeper.getStore(key = name) {
            CAlertDialogStoreFactory(
                storeFactory = storeFactory,
                state = CAlertDialogStore.State(
                    onAcceptClick = { this.onEvent(CAlertDialogStore.Intent.HideDialog); onAcceptClick()},
                    onDeclineClick = if (onDeclineClick != null) {
                        {
                            this.onEvent(CAlertDialogStore.Intent.HideDialog)
                            onDeclineClick.invoke()
                        }

                    } else {
                        {
                            this.onEvent(CAlertDialogStore.Intent.HideDialog)
                        }
                    },
                    needDelayWhenHide = needDelayWhenHide
                )
            ).create()
        }

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
}