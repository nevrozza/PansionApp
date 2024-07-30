package achievements

import AdminRepository
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.arkivanov.mvikotlin.core.store.StoreFactory
import achievements.AdminAchievementsStore.Intent
import achievements.AdminAchievementsStore.Label
import achievements.AdminAchievementsStore.State
import achievements.AdminAchievementsStore.Message
import components.cBottomSheet.CBottomSheetComponent
import components.networkInterface.NetworkInterface

class AdminAchievementsStoreFactory(
    private val storeFactory: StoreFactory,
    private val adminRepository: AdminRepository,
    private val bottomSheetComponent: CBottomSheetComponent,
    private val hugeBottomSheetComponent: CBottomSheetComponent,
    private val editBottomSheetComponent: CBottomSheetComponent,
    private val nInterface: NetworkInterface,
    private val nBSInterface: NetworkInterface,
) {

    fun create(): AdminAchievementsStore {
        return AdminAchievementsStoreImpl()
    }

    private inner class AdminAchievementsStoreImpl :
        AdminAchievementsStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "AdminAchievementsStore",
            initialState = AdminAchievementsStore.State(),
            executorFactory = { AdminAchievementsExecutor(
                adminRepository = adminRepository,
                nInterface = nInterface,
                bottomSheetComponent = bottomSheetComponent,
                hugeBottomSheetComponent = hugeBottomSheetComponent,
                editBottomSheetComponent = editBottomSheetComponent,
                nBSInterface = nBSInterface
            ) },
            reducer = AdminAchievementsReducer
        )
}