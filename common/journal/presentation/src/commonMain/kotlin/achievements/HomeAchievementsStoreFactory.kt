package achievements

import achievements.HomeAchievementsStore.Intent
import achievements.HomeAchievementsStore.Label
import achievements.HomeAchievementsStore.State
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory

class HomeAchievementsStoreFactory(
    private val storeFactory: StoreFactory,
    private val state: State,
    private val executor: HomeAchievementsExecutor
) {

    fun create(): HomeAchievementsStore {
        return HomeAchievementsStoreImpl()
    }

    private inner class HomeAchievementsStoreImpl :
        HomeAchievementsStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "HomeAchievementsStore",
            initialState = state,
            executorFactory = ::executor,
            reducer = HomeAchievementsReducer,
            bootstrapper = SimpleBootstrapper(Unit)
        )
}