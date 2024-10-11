package achievements

import JournalRepository
import asValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.networkInterface.NetworkInterface
import di.Inject

class HomeAchievementsComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    private val login: String,
    private val name: String,
    private val avatarId: Int,
    private val output: (Output) -> Unit
) : ComponentContext by componentContext {
    private val nInterfaceName = "homeAchievementsComponentNInterface"


    val nInterface = NetworkInterface(
        childContext(nInterfaceName + "CONTEXT"),
        storeFactory,
        nInterfaceName
    )

    private val journalRepository: JournalRepository = Inject.instance()


    private val homeAchievementsStore =
        instanceKeeper.getStore {
            HomeAchievementsStoreFactory(
                storeFactory = storeFactory,
                login = login,
                nInterface = nInterface,
                journalRepository = journalRepository,
                name = name,
                avatarId = avatarId
            ).create()
        }


    init {
        onEvent(HomeAchievementsStore.Intent.Init)
    }

    val model = homeAchievementsStore.asValue()

//    @OptIn(ExperimentalCoroutinesApi::class)
//    val state: StateFlow<UsersStore.State> = usersStore.stateFlow

    fun onEvent(event: HomeAchievementsStore.Intent) {
        homeAchievementsStore.accept(event)
    }

    fun onOutput(output: Output) {
        output(output)
    }

    sealed class Output {
        data object Back : Output()
    }
}