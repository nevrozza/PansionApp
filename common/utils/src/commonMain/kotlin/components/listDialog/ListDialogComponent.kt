package components.listDialog

import asValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow


data class ListItem(
    val id: Int,
    val text: String,
    val isClickable: Boolean = true
)
//return@ListDialogComponent model.value.forms.map {
//    ListItem(
//        id = it.id,
//        text = "${it.classNum}${if (it.name.length < 2) "-" else " "}${it.name} класс"
//    )
//}
class ListDialogComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    name: String,
    private val onItemClick: (ListItem) -> Unit
) : ComponentContext by componentContext {
    //    private val settingsRepository: SettingsRepository = Inject.instance()
//    private val adminRepository: AdminRepository = Inject.instance()
    private val listDialogStore =
        instanceKeeper.getStore(key = name) {
            ListDialogStoreFactory(
                storeFactory = storeFactory
//                authRepository = authRepository
            ).create()
        }

    val model: Value<ListDialogStore.State> = listDialogStore.asValue()

    private val backCallback = BackCallback {
        onEvent(ListDialogStore.Intent.HideDialog)
    }


    init {
        backHandler.register(backCallback)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<ListDialogStore.State> = listDialogStore.stateFlow

    fun onEvent(event: ListDialogStore.Intent) {
        listDialogStore.accept(event)
    }

    fun onClick(item: ListItem) {
        onItemClick(item)
    }


}