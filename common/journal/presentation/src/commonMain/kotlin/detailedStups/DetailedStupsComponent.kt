package detailedStups

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.networkInterface.NetworkInterface
import decompose.DefaultMVIComponent

class DetailedStupsComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    private val output: (Output) -> Unit,
    private val studentLogin: String,
    private val name: String,
    private val avatarId: Int,
    private val reason: String
) : ComponentContext by componentContext,
    DefaultMVIComponent<DetailedStupsStore.Intent, DetailedStupsStore.State, DetailedStupsStore.Label> {

    val nInterface =
        NetworkInterface(componentContext, storeFactory, "DetailedStupsComponent")


    override val store =
        instanceKeeper.getStore(key = "detailedStups$studentLogin") {
            DetailedStupsStoreFactory(
                storeFactory = storeFactory,
                state = DetailedStupsStore.State(
                    login = studentLogin,
                    reason = reason,
                    name = name,
                    avatarId = avatarId
                ),
                executor =  DetailedStupsExecutor(
                    nInterface = nInterface
                )
            ).create()
        }

    fun onOutput(output: Output) {
        output(output)
    }

    sealed class Output {
        data object Back : Output()
        data class NavigateToAchievements(val login: String, val name: String, val avatarId: Int) :
            Output()

    }
}