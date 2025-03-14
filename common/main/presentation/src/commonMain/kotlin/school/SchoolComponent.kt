package school

import JournalRepository
import MainRepository
import asValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import components.cBottomSheet.CBottomSheetComponent
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
    val isSecondScreen: Boolean,
    private val output: (Output) -> Unit
) : ComponentContext by componentContext {
    //    private val settingsRepository: SettingsRepository = Inject.instance()\
    val nInterfaceName = "MainSchoolNInterface"
    val nDutyInterfaceName = "DutySchoolNInterface"
    val ministryBottomSheetComponentName = "MinistrySchoolNInterface"

    val nInterface = NetworkInterface(
        childContext(nInterfaceName + "CONTEXT"),
        storeFactory,
        nInterfaceName
    )
    val nDutyInterface = NetworkInterface(
        childContext(nDutyInterfaceName + "CONTEXT"),
        storeFactory,
        nDutyInterfaceName
    )

    val ministryOverviewComponent = CBottomSheetComponent(
        componentContext = childContext(ministryBottomSheetComponentName+"CONTEXT"),
        storeFactory = storeFactory,
        name = "ministryBottomSheetComponentName"
    )

    val ministrySettingsCBottomSheetComponent = CBottomSheetComponent(
        componentContext = childContext("ministrySettingsCBottomSheetComponentCONTEXT"),
        storeFactory = storeFactory,
        name = "ministrySettingsCBottomSheetComponent"
    )


    private val mainRepository: MainRepository = Inject.instance()
    private val journalRepository: JournalRepository = Inject.instance()
    private val schoolStore =
        instanceKeeper.getStore {
            SchoolStoreFactory(
                storeFactory = storeFactory,
                login = login,
                role = role,
                moderation = moderation,
                nInterface = nInterface,
                mainRepository = mainRepository,
                openMinSettingsBottom = ministrySettingsCBottomSheetComponent,
                nDutyInterface = nDutyInterface,
                ministryOverview = ministryOverviewComponent,
                journalRepository = journalRepository
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
        data object NavigateBack : Output()
        data object NavigateToRating : Output()
        data class NavigateToFormRating(
            val login: String,
            val formName: String?,
            val formNum: Int?,
            val formId: Int?
        ) : Output()

        data class NavigateToSchedule(
            val isModer: Boolean
        ) : Output()

        data object NavigateToMinistry : Output()
        data object NavigateToAchievements : Output()
    }
}