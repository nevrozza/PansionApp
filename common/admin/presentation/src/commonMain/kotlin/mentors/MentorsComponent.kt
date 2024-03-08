package mentors

import asValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow

class MentorsComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    private val output: (Output) -> Unit
) : ComponentContext by componentContext {
    //    private val settingsRepository: SettingsRepository = Inject.instance()
//    private val authRepository: AuthRepository = Inject.instance()
    private val mentorsStore =
        instanceKeeper.getStore {
            MentorsStoreFactory(
                storeFactory = storeFactory,
//                authRepository = authRepository
            ).create()
        }

    val model = mentorsStore.asValue()

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<MentorsStore.State> = mentorsStore.stateFlow

    fun onEvent(event: MentorsStore.Intent) {
        mentorsStore.accept(event)
    }

    fun onOutput(output: Output) {
        output(output)
    }

    sealed class Output {
//        data object NavigateToMentors : Output()
    }
}