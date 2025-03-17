package admin

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import decompose.DefaultMVIComponent

class AdminComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    private val output: (Output) -> Unit
) : ComponentContext by componentContext,
    DefaultMVIComponent<AdminStore.Intent, AdminStore.State, AdminStore.Label> {
    override val store =
        instanceKeeper.getStore {
            AdminStoreFactory(
                storeFactory = storeFactory,
//                authRepository = authRepository
            ).create()
        }


    fun onOutput(output: Output) {
        output(output)
    }

    sealed class Output {
        data object NavigateToUsers : Output()
        data object NavigateToGroups : Output()

        data object NavigateToCabinets : Output()
        data object NavigateToCalendar : Output()
        data object NavigateToAchievements : Output()
        data object NavigateToParents : Output()

    }
}

data class AdminItem(
    val title: String,
    val routing: AdminComponent.Output
)