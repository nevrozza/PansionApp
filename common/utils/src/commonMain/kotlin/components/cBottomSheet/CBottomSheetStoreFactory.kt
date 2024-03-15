package components.cBottomSheet

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.cBottomSheet.CBottomSheetStore.Intent
import components.cBottomSheet.CBottomSheetStore.Label
import components.cBottomSheet.CBottomSheetStore.State
import components.cBottomSheet.CBottomSheetStore.Message

class CBottomSheetStoreFactory(private val storeFactory: StoreFactory) {

    fun create(): CBottomSheetStore {
        return CBottomSheetStoreImpl()
    }

    private inner class CBottomSheetStoreImpl :
        CBottomSheetStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "CBottomSheetStore",
            initialState = CBottomSheetStore.State(),
            executorFactory = { CBottomSheetExecutor() },
            reducer = CBottomSheetReducer
        )
}