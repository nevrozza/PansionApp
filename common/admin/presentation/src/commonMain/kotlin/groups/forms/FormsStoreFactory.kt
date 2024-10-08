package groups.forms

import AdminRepository
import FIO
import Person
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.networkInterface.NetworkInterface
import components.cBottomSheet.CBottomSheetComponent
import groups.forms.FormsStore.Intent
import groups.forms.FormsStore.Label
import groups.forms.FormsStore.State

class FormsStoreFactory(
    private val storeFactory: StoreFactory,
    private val nFormGroupsInterface: NetworkInterface,
    private val adminRepository: AdminRepository,
    private val updateForms: () -> Unit,
    private val creatingFormBottomSheet: CBottomSheetComponent,
    private val editFormBottomSheet: CBottomSheetComponent,
) {

    fun create(): FormsStore {
        return FormsStoreImpl()
    }

    private inner class FormsStoreImpl :
        FormsStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "FormsStore",
            initialState = FormsStore.State(
                mentors = listOf(
                    Person(
                        login = "sad",
                        fio = FIO(
                            name = "anime",
                            surname = "girl",
                            praname = "hentai"
                        ),
                        true
                    )
                )
            ),
            executorFactory = {
                FormsExecutor(
                    adminRepository = adminRepository,
                    nFormGroupsInterface = nFormGroupsInterface,
                    creatingFormBottomSheet = creatingFormBottomSheet,
                    updateForms = updateForms,
                    editFormBottomSheet = editFormBottomSheet
                )
            },
            reducer = FormsReducer
        )
}