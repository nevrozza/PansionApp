package components.mpChose

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.networkInterface.NetworkInterface

class MpChoseStoreFactory(
    private val storeFactory: StoreFactory,
    private val name: String,
    private val networkInterface: NetworkInterface,
    private val customOnDismiss: (() -> Unit)? = null
) {

    fun create(): MpChoseStore {
        return MpChoseStoreImpl()
    }

    private inner class MpChoseStoreImpl :
        MpChoseStore,
        Store<MpChoseStore.Intent, MpChoseStore.State, MpChoseStore.Label> by storeFactory.create(
            name = name+"ListDialogStore",
            initialState = MpChoseStore.State(),
            executorFactory = { MpChoseExecutor(nInterface = networkInterface, customOnDismiss = customOnDismiss) },
            reducer = MpChoseReducer
        )
}