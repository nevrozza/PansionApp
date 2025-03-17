package formRating

import AuthRepository
import FIO
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.cAlertDialog.CAlertDialogComponent
import components.listDialog.ListComponent
import components.listDialog.ListDialogStore
import components.networkInterface.NetworkInterface
import decompose.DefaultMVIComponent
import di.Inject
import rating.toPeriod

class FormRatingComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    private val formId: Int?,
    private val formNum: Int?,
    private val formName: String?,
    private val login: String,
    private val output: (Output) -> Unit
) : ComponentContext by componentContext, DefaultMVIComponent<FormRatingStore.Intent, FormRatingStore.State, FormRatingStore.Label> {
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


    val weeksListComponent = ListComponent(
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
        listOf(weeksListComponent, moduleListComponent, periodListComponent).forEach {
            it.onEvent(ListDialogStore.Intent.HideDialog)
        }
    }



    private val nInterfaceName = "FormRatingInterfaceName"

    val nInterface =
        NetworkInterface(childContext(nInterfaceName + "CONTEXT"), storeFactory, nInterfaceName)


    override val store =
        instanceKeeper.getStore {
            FormRatingStoreFactory(
                storeFactory = storeFactory,
                state = FormRatingStore.State(
                    login = login,
                    formNum = formNum,
                    formId = formId,
                    formName = formName,
                    role = Inject.instance<AuthRepository>().fetchRole()
                ),
                executor = FormRatingExecutor(
                    nInterface = nInterface,
                    formPickerDialog = formPickerDialog,
                    stupsDialog = stupsDialogComponent,
                    weeksListComponent = weeksListComponent,
                    moduleListComponent = moduleListComponent,
                    periodListComponent = periodListComponent
                )
            ).create()
        }

    fun onOutput(output: Output) {
        output(output)
    }

    sealed class Output {
        data object Back : Output()
        data class NavigateToProfile(val studentLogin: String, val fio: FIO, val avatarId: Int) : Output()
    }
}