package achievements

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.networkInterface.NetworkInterface
import decompose.DefaultMVIComponent
import decompose.getChildContext

class HomeAchievementsComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    private val login: String,
    private val name: String,
    private val avatarId: Int,
    private val output: (Output) -> Unit
) : ComponentContext by componentContext,
    DefaultMVIComponent<HomeAchievementsStore.Intent, HomeAchievementsStore.State, HomeAchievementsStore.Label> {
    private val nInterfaceName = "homeAchievementsComponentNInterface"


    val nInterface = NetworkInterface(
        getChildContext(nInterfaceName),
        storeFactory,
        nInterfaceName
    )
    override val store =
        instanceKeeper.getStore {
            HomeAchievementsStoreFactory(
                storeFactory = storeFactory,
                executor = HomeAchievementsExecutor(
                    nInterface = nInterface
                ),
                state = HomeAchievementsStore.State(
                    login = login,
                    name = name,
                    avatarId = avatarId
                )
            ).create()
        }

    fun onOutput(output: Output) {
        output(output)
    }

    sealed class Output {
        data object Back : Output()
    }
}