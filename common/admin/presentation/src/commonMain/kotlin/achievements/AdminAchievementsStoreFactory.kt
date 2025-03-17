package achievements

import achievements.AdminAchievementsStore.Intent
import achievements.AdminAchievementsStore.Label
import achievements.AdminAchievementsStore.State
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory

class AdminAchievementsStoreFactory(
    private val storeFactory: StoreFactory,
    private val executor: AdminAchievementsExecutor
) {

    fun create(): AdminAchievementsStore {
        return AdminAchievementsStoreImpl()
    }

    private inner class AdminAchievementsStoreImpl :
        AdminAchievementsStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "AdminAchievementsStore",
            initialState = State(),
            executorFactory = ::executor,
            reducer = AdminAchievementsReducer,
            bootstrapper = SimpleBootstrapper(Unit)
        )
}