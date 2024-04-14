package detailedStups

import JournalRepository
import asValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import components.networkInterface.NetworkInterface
import di.Inject
import dnevnikRuMarks.DnevnikRuMarkStore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow

class DetailedStupsComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    private val output: (Output) -> Unit,
    private val studentLogin: String,
    private val reason: String
) : ComponentContext by componentContext {
    //    private val settingsRepository: SettingsRepository = Inject.instance()
//    private val authRepository: AuthRepository = Inject.instance()

    val nInterface =
        NetworkInterface(componentContext, storeFactory, "DetailedStupsComponent")

    val journalRepository: JournalRepository = Inject.instance()


    private val detailedStupsStore =
        instanceKeeper.getStore(key = "detailedStups$studentLogin") {
            DetailedStupsStoreFactory(
                storeFactory = storeFactory,
                login = studentLogin,
                reason = reason,
                nInterface = nInterface,
                journalRepository = journalRepository
            ).create()
        }

    val model = detailedStupsStore.asValue()

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<DetailedStupsStore.State> = detailedStupsStore.stateFlow

    fun onEvent(event: DetailedStupsStore.Intent) {
        detailedStupsStore.accept(event)
    }

    fun onOutput(output: Output) {
        output(output)
    }

    private val backCallback = BackCallback {
        onOutput(Output.BackToHome)
    }


    init {
        backHandler.register(backCallback)
        onEvent(DetailedStupsStore.Intent.Init)

    }

    sealed class Output {
        data object BackToHome : Output()

    }
}