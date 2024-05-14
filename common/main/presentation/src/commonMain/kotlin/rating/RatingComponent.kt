package rating

import AuthRepository
import MainRepository
import asValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import components.listDialog.ListComponent
import components.listDialog.ListDialogStore
import components.networkInterface.NetworkInterface
import di.Inject
import home.HomeComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow

class RatingComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    private val output: (Output) -> Unit
) : ComponentContext by componentContext {
    //    private val settingsRepository: SettingsRepository = Inject.instance()
    val nInterface = NetworkInterface(
        componentContext = componentContext,
        storeFactory = storeFactory,
        name = "RatingNetworkkInterface"
    )

    val subjectsListComponent = ListComponent(
        componentContext,
        storeFactory,
        name = "SubjectsListComponent",
        onItemClick = {onItemClick(it.id.toInt())}
    )

    private fun onItemClick(id: Int) {
        onEvent(RatingStore.Intent.ClickOnSubject(id))
        subjectsListComponent.onEvent(ListDialogStore.Intent.HideDialog)
    }

    private val authRepository: AuthRepository = Inject.instance()
    private val mainRepository: MainRepository = Inject.instance()
    private val homeStore =
        instanceKeeper.getStore {
            RatingStoreFactory(
                storeFactory = storeFactory,
                authRepository = authRepository,
                mainRepository = mainRepository,
                nInterface = nInterface,
                subjectsListComponent = subjectsListComponent
            ).create()
        }

    val model = homeStore.asValue()

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<RatingStore.State> = homeStore.stateFlow

    fun getLogin() : String {
        return model.value.login
    }

    fun onEvent(event: RatingStore.Intent) {
        homeStore.accept(event)
    }

    init {
        onEvent(RatingStore.Intent.Init)
        //.Init(
        //            avatarId = authRepository.fetchAvatarId(),
        //            login = authRepository.fetchLogin(),
        //            name = authRepository.fetchName(),
        //            surname = authRepository.fetchSurname(),
        //            praname = authRepository.fetchPraname()
        //
    }

    fun onOutput(output: Output) {
        output(output)
    }

    sealed class Output {
        data object NavigateToSettings : Output()
    }
}