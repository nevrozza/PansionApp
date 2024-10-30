package school

import MainRepository
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.cBottomSheet.CBottomSheetComponent
import components.networkInterface.NetworkInterface
import school.SchoolStore.Intent
import school.SchoolStore.Label
import school.SchoolStore.State

class SchoolStoreFactory(
    private val storeFactory: StoreFactory,
    private val login: String,
    private val role: String,
    private val moderation: String,
    private val nInterface: NetworkInterface,
    private val mainRepository: MainRepository,
    private val openMinSettingsBottom: CBottomSheetComponent
) {

    fun create(): SchoolStore {
        return SchoolStoreImpl()
    }

    private inner class SchoolStoreImpl :
        SchoolStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "SchoolStore",
            initialState = SchoolStore.State(
                login = login,
                role = role,
                moderation = moderation
            ),
            executorFactory = { SchoolExecutor(
                nInterface = nInterface,
                mainRepository = mainRepository,
                openMinSettingsBottom = openMinSettingsBottom
            ) },
            reducer = SchoolReducer
        )
}