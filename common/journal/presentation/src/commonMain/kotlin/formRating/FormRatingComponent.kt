package formRating

import FIO
import JournalRepository
import asValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import components.cAlertDialog.CAlertDialogComponent
import components.listDialog.ListComponent
import components.listDialog.ListDialogStore
import components.listDialog.ListItem
import components.networkInterface.NetworkInterface
import di.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import rating.PansionPeriod
import rating.toPeriod
import rating.toStr

class FormRatingComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    private val formId: Int?,
    private val formNum: Int?,
    private val formName: String?,
    private val login: String,
    private val output: (Output) -> Unit
) : ComponentContext by componentContext {
    //    private val settingsRepository: SettingsRepository = Inject.instance()
//    private val authRepository: AuthRepository = Inject.instance()
    private val formPickerDialogName = "FormPickerDialogName"
    val formPickerDialog = ListComponent(
        componentContext = childContext(formPickerDialogName+"CONTEXT"),
        storeFactory = storeFactory,
        name = formPickerDialogName,
        onItemClick = {
            onEvent(
                FormRatingStore.Intent.ChangeForm(
                    it.id.toInt()
                )
            )
        }
    )


    val stupsDialogComponent = CAlertDialogComponent(
        childContext("StupsDialogComponentIntFormRatingCONTEXT"),
        storeFactory,
        name = "StupsDialogComponentIntFormRating",
        {}
    )


    val weekListComponent = ListComponent(
        componentContext,
        storeFactory,
        name = "RatingWeekListComponent",
        onItemClick = {onPeriodItemClick(it.id)}
    )
    val moduleListComponent = ListComponent(
        componentContext,
        storeFactory,
        name = "RatingModuleListComponent",
        onItemClick = {onPeriodItemClick(it.id)}
    )
    val periodListComponent = ListComponent(
        componentContext,
        storeFactory,
        name = "RatingPeriodListComponent",
        onItemClick = {onPeriodItemClick(it.id)}
    )

    private fun onPeriodItemClick(id: String) {
        onEvent(FormRatingStore.Intent.ChangePeriod(id.toPeriod()))
        listOf(weekListComponent, moduleListComponent, periodListComponent).forEach {
            it.onEvent(ListDialogStore.Intent.HideDialog)
        }
    }



    private val nInterfaceName = "FormRatingInterfaceName"

    val nInterface =
        NetworkInterface(childContext(nInterfaceName + "CONTEXT"), storeFactory, nInterfaceName)

    val journalRepository: JournalRepository = Inject.instance()

    private val formRatingStore =
        instanceKeeper.getStore {
            FormRatingStoreFactory(
                storeFactory = storeFactory,
                login = login,
                journalRepository = journalRepository,
                nInterface = nInterface,
                formName = formName,
                formId = formId,
                formNum = formNum,
                formPickerDialog = formPickerDialog,
                stupsDialog = stupsDialogComponent,
                weeksListComponent = weekListComponent,
                moduleListComponent = moduleListComponent,
                periodListComponent = periodListComponent
            ).create()
        }

    val model = formRatingStore.asValue()

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<FormRatingStore.State> = formRatingStore.stateFlow

    fun onEvent(event: FormRatingStore.Intent) {
        formRatingStore.accept(event)
    }

    fun onOutput(output: Output) {
        output(output)
    }


    init {
        onEvent(FormRatingStore.Intent.Init)

        periodListComponent.onEvent(
            ListDialogStore.Intent.InitList(
                listOf(Pair(PansionPeriod.Year, "За год")).map {
                    ListItem(
                        id = it.first.toStr(),
                        text = it.second
                    )
                }
            )
        )
    }

    sealed class Output {
        data object Back : Output()
        data class NavigateToProfile(val studentLogin: String, val fio: FIO, val avatarId: Int) : Output()
    }
}