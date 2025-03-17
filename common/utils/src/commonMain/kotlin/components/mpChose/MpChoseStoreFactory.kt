package components.mpChose

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory

class MpChoseStoreFactory(
    private val storeFactory: StoreFactory,
    private val name: String,
    private val executor: MpChoseExecutor
) {

    fun create(): MpChoseStore {
        return MpChoseStoreImpl()
    }

    private inner class MpChoseStoreImpl :
        MpChoseStore,
        Store<MpChoseStore.Intent, MpChoseStore.State, MpChoseStore.Label> by storeFactory.create(
            name = name+"ListDialogStore",
            initialState = MpChoseStore.State(),
            executorFactory = ::executor,
            reducer = MpChoseReducer
        )
}