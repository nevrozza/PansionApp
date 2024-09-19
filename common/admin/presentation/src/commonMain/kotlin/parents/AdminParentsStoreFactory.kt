package parents

import AdminRepository
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.listDialog.ListComponent
import components.networkInterface.NetworkInterface
import parents.AdminParentsStore.Intent
import parents.AdminParentsStore.Label
import parents.AdminParentsStore.State
import parents.AdminParentsStore.Message

class AdminParentsStoreFactory(
    private val storeFactory: StoreFactory,
    private val adminRepository: AdminRepository,
    private val nInterface: NetworkInterface,
    private val parentEditPicker: ListComponent,
    private val childCreatePicker: ListComponent,
) {

    fun create(): AdminParentsStore {
        return AdminParentsStoreImpl()
    }

    private inner class AdminParentsStoreImpl :
        AdminParentsStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "AdminParentsStore",
            initialState = State(),
            executorFactory = { AdminParentsExecutor(
                adminRepository = adminRepository,
                nInterface = nInterface,
                parentEditPicker = parentEditPicker,
                childCreatePicker = childCreatePicker
            ) },
            reducer = AdminParentsReducer
        )
}