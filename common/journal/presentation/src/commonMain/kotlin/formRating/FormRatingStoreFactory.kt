
package formRating

import AuthRepository
import JournalRepository
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.cAlertDialog.CAlertDialogComponent
import components.listDialog.ListComponent
import components.networkInterface.NetworkInterface
import di.Inject
import formRating.FormRatingStore.Intent
import formRating.FormRatingStore.Label
import formRating.FormRatingStore.State
import formRating.FormRatingStore.Message

class FormRatingStoreFactory(
    private val storeFactory: StoreFactory,
    private val nInterface: NetworkInterface,
    private val journalRepository: JournalRepository,
    private val formPickerDialog: ListComponent,
    private val stupsDialog: CAlertDialogComponent,
    private val login: String,
    private val formNum: Int?,
    private val formId: Int?,
    private val formName: String?,
) {

    fun create(): FormRatingStore {
        return FormRatingStoreImpl()
    }

    private inner class FormRatingStoreImpl :
        FormRatingStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "FormRatingStore",
            initialState = FormRatingStore.State(
                login = login,
                formNum = formNum,
                formId = formId,
                formName = formName,
                role = Inject.instance<AuthRepository>().fetchRole()
            ),
            executorFactory = { FormRatingExecutor(
                nInterface = nInterface,
                journalRepository = journalRepository,
                formPickerDialog = formPickerDialog,
                stupsDialog = stupsDialog
            ) },
            reducer = FormRatingReducer
        )
}