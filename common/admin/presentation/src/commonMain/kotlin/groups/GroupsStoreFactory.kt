package groups

import AdminRepository
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.listDialog.ListDialogComponent
import groups.GroupsStore.Intent
import groups.GroupsStore.Label
import groups.GroupsStore.State

class GroupsStoreFactory(
    private val storeFactory: StoreFactory,
    private val adminRepository: AdminRepository,
    private val formListDialogComponent: ListDialogComponent
) {

    fun create(): GroupsStore {
        return GroupsStoreImpl()
    }

    private inner class GroupsStoreImpl :
        GroupsStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "GroupsStore",
            initialState = GroupsStore.State(),
            executorFactory = { GroupsExecutor(adminRepository = adminRepository, formListDialogComponent) },
            reducer = GroupsReducer
        )
}