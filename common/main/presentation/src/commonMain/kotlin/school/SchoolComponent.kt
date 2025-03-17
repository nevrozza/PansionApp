package school

import JournalRepository
import MainRepository
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.cBottomSheet.CBottomSheetComponent
import components.networkInterface.NetworkInterface
import decompose.DefaultMVIComponent
import di.Inject

class SchoolComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    private val login: String,
    private val role: String,
    private val moderation: String,
    val isSecondScreen: Boolean,
    private val output: (Output) -> Unit
) : ComponentContext by componentContext, DefaultMVIComponent<SchoolStore.Intent, SchoolStore.State, SchoolStore.Label> {
    //    private val settingsRepository: SettingsRepository = Inject.instance()\
    private val nInterfaceName = "MainSchoolNInterface"
    private val nDutyInterfaceName = "DutySchoolNInterface"
    private val ministryBottomSheetComponentName = "MinistrySchoolNInterface"

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
    override val store =
        instanceKeeper.getStore {
            SchoolStoreFactory(
                storeFactory = storeFactory,
                state = SchoolStore.State(
                    login = login,
                    role = role,
                    moderation = moderation
                ),
                executor = SchoolExecutor(
                    nInterface = nInterface,
                    mainRepository = mainRepository,
                    openMinSettingsBottom = ministrySettingsCBottomSheetComponent,
                    nDutyInterface = nDutyInterface,
                    ministryOverview = ministryOverviewComponent,
                    journalRepository = journalRepository
                )
            ).create()
        }


    fun onOutput(output: Output) {
        output(output)
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