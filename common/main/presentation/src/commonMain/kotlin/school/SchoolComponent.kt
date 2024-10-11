package school

import AuthRepository
import MainRepository
import asValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import components.cAlertDialog.CAlertDialogComponent
import components.networkInterface.NetworkInterface
import di.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow

class SchoolComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    private val login: String,
    private val role: String,
    private val moderation: String,
    private val output: (Output) -> Unit
) : ComponentContext by componentContext {
    //    private val settingsRepository: SettingsRepository = Inject.instance()\
    val nInterfaceName = "MainSchoolNInterface"

    val nInterface = NetworkInterface(
        childContext(nInterfaceName + "CONTEXT"),
        storeFactory,
        nInterfaceName
    )

    private val mainRepository: MainRepository = Inject.instance()
    private val schoolStore =
        instanceKeeper.getStore {
            SchoolStoreFactory(
                storeFactory = storeFactory,
                login = login,
                role = role,
                moderation = moderation,
                nInterface = nInterface,
                mainRepository = mainRepository
//                authRepository = authRepository
            ).create()
        }


    val model = schoolStore.asValue()

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<SchoolStore.State> = schoolStore.stateFlow

    fun onEvent(event: SchoolStore.Intent) {
        schoolStore.accept(event)
    }

    fun onOutput(output: Output) {
        output(output)
    }

    init {
        onEvent(SchoolStore.Intent.Init)
    }

    sealed class Output {
        data object NavigateToRating : Output()
        data class NavigateToFormRating(
            val login: String,
            val formName: String?,
            val formNum: Int?,
            val formId: Int?
        ) : Output()

    }
}