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
import components.networkInterface.NetworkInterface
import di.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow

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
                stupsDialog = stupsDialogComponent
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
    }

    sealed class Output {
        data object Back : Output()
        data class NavigateToProfile(val studentLogin: String, val fio: FIO, val avatarId: Int) : Output()
    }
}