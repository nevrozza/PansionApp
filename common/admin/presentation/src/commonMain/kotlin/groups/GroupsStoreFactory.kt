package groups

import AdminRepository
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.networkInterface.NetworkInterface
import components.listDialog.ListComponent
import groups.GroupsStore.Intent
import groups.GroupsStore.Label
import groups.GroupsStore.State
import groups.subjects.SubjectsComponent

class GroupsStoreFactory(
    private val storeFactory: StoreFactory,
    private val adminRepository: AdminRepository,
    private val formListComponent: ListComponent,
    private val nGroupsInterface: NetworkInterface,
    private val nSubjectsInterface: NetworkInterface,
    private val nFormsInterface: NetworkInterface,
    private val updateMentorsInForms: () -> Unit
) {

    fun create(): GroupsStore {
        return GroupsStoreImpl()
    }

    private inner class GroupsStoreImpl :
        GroupsStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "GroupsStore",
            initialState = GroupsStore.State(),
            executorFactory = {
                GroupsExecutor(
                    adminRepository = adminRepository,
                    formListComponent = formListComponent,
                    nGroupsInterface = nGroupsInterface,
                    nSubjectsInterface = nSubjectsInterface,
                    nFormsInterface = nFormsInterface,
                    updateMentorsInForms = updateMentorsInForms
                )
            },
            reducer = GroupsReducer
        )
}