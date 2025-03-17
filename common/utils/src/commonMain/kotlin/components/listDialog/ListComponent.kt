package components.listDialog

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.networkInterface.NetworkInterface
import decompose.DefaultMVIComponent


data class ListItem(
    val id: String,
    val text: String,
    val isClickable: Boolean = true
)

//return@ListComponent model.value.forms.map {
//    ListItem(
//        id = it.id,
//        text = "${it.classNum}${if (it.name.length < 2) "-" else " "}${it.name} класс"
//    )
//}
class ListComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    name: String,
    private val onItemClick: (ListItem) -> Unit,
    customOnDismiss: (() -> Unit)? = null
) : ComponentContext by componentContext,
    DefaultMVIComponent<ListDialogStore.Intent, ListDialogStore.State, ListDialogStore.Label> {
    val nInterface = NetworkInterface(
        componentContext,
        storeFactory,
        name + "NInterface"
    )
    val nModel = nInterface.networkModel
    override val store =
        instanceKeeper.getStore(key = name) {
            ListDialogStoreFactory(
                storeFactory = storeFactory,
                name = name,
                executor = ListDialogExecutor(
                    nInterface = nInterface,
                    customOnDismiss = customOnDismiss
                )
            ).create()
        }

//    private val backCallback = BackCallback {
//        onEvent(ListDialogStore.Intent.HideDialog)
//    }
//    init {
    //backHandler.register(backCallback)
//    }


    fun onClick(item: ListItem) {
        onItemClick(item)
    }


}