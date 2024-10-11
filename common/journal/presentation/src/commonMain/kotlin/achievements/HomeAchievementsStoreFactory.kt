package achievements

import JournalRepository
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.arkivanov.mvikotlin.core.store.StoreFactory
import achievements.HomeAchievementsStore.Intent
import achievements.HomeAchievementsStore.Label
import achievements.HomeAchievementsStore.State
import achievements.HomeAchievementsStore.Message
import components.networkInterface.NetworkInterface

class HomeAchievementsStoreFactory(
    private val storeFactory: StoreFactory,
    private val nInterface: NetworkInterface,
    private val journalRepository: JournalRepository,
    private val login: String,
    private val name: String,
    private val avatarId: Int,
) {

    fun create(): HomeAchievementsStore {
        return HomeAchievementsStoreImpl()
    }

    private inner class HomeAchievementsStoreImpl :
        HomeAchievementsStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "HomeAchievementsStore",
            initialState = HomeAchievementsStore.State(
                login = login,
                name = name,
                avatarId = avatarId
            ),
            executorFactory = { HomeAchievementsExecutor(
                nInterface = nInterface,
                journalRepository = journalRepository
            ) },
            reducer = HomeAchievementsReducer
        )
}