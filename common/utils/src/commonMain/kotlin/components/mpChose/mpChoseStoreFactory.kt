package components.mpChose

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.networkInterface.NetworkInterface

class mpChoseStoreFactory(
    private val storeFactory: StoreFactory,
    private val name: String,
    private val networkInterface: NetworkInterface,
    private val customOnDismiss: (() -> Unit)? = null
) {

    fun create(): mpChoseStore {
        return mpChoseStoreImpl()
    }

    private inner class mpChoseStoreImpl :
        mpChoseStore,
        Store<mpChoseStore.Intent, mpChoseStore.State, mpChoseStore.Label> by storeFactory.create(
            name = name+"ListDialogStore",
            initialState = mpChoseStore.State(),
            executorFactory = { mpChoseExecutor(nInterface = networkInterface, customOnDismiss = customOnDismiss) },
            reducer = mpChoseReducer
        )
}