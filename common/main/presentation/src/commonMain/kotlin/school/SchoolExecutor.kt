package school

import CDispatcher
import MainRepository
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import components.cBottomSheet.CBottomSheetComponent
import components.cBottomSheet.CBottomSheetStore
import components.networkInterface.NetworkInterface
import kotlinx.coroutines.launch
import main.RFetchSchoolDataReceive
import main.school.RCreateMinistryStudentReceive
import school.SchoolStore.Intent
import school.SchoolStore.Label
import school.SchoolStore.Message
import school.SchoolStore.State

class SchoolExecutor(
    private val nInterface: NetworkInterface,
    private val openMinSettingsBottom: CBottomSheetComponent,
    private val mainRepository: MainRepository
) : CoroutineExecutor<Intent, Unit, State, Message, Label>() {
    override fun executeIntent(intent: Intent) {
        when (intent) {
            Intent.Init -> init()
            Intent.OpenMinistrySettings -> openMinistrySettings()
            is Intent.SetMinistryStudent -> setMinistryStudent(ministryId = intent.ministryId, fio = intent.fio, login = intent.login)
        }
    }

    private fun setMinistryStudent(ministryId: String, login: String?, fio: String) {
        scope.launch(CDispatcher) {
            openMinSettingsBottom.nInterface.nStartLoading()
            try {
                val r = mainRepository.createMinistryStudent(
                    r = RCreateMinistryStudentReceive(
                        studentFIO = fio,
                        ministryId = ministryId,
                        login = login
                    )
                )
                scope.launch {
                    dispatch(
                        Message.MinistrySettingsOpened(r.students)
                    )
                    openMinSettingsBottom.nInterface.nSuccess()
                }
            } catch (e: Throwable) {

                openMinSettingsBottom.nInterface.nError(
                    "Что-то пошло не так",
                ) {
                    openMinSettingsBottom.nInterface.goToNone()
                }
            }
        }
    }

    private fun openMinistrySettings() {
        openMinSettingsBottom.onEvent(CBottomSheetStore.Intent.ShowSheet)
        scope.launch(CDispatcher) {
            openMinSettingsBottom.nInterface.nStartLoading()
            try {
                val r = mainRepository.fetchMinistrySettings()
                scope.launch {
                    dispatch(
                        Message.MinistrySettingsOpened(r.students)
                    )
                    openMinSettingsBottom.nInterface.nSuccess()
                }
            } catch (e: Throwable) {

                openMinSettingsBottom.nInterface.nError(
                    "Что-то пошло не так",
                ) {
                    openMinistrySettings()
                }
            }
        }
    }

    private fun init() {
        scope.launch(CDispatcher) {
            nInterface.nStartLoading()
            try {
                val r = mainRepository.fetchSchoolData(
                    RFetchSchoolDataReceive(
                        login = state().login
                    )
                )
                scope.launch {
                    dispatch(
                        Message.Inited(
                            formId = r.formId,
                            formNum = r.formNum,
                            formName = r.formName,
                            top = r.top,
                            ministryId = r.ministryId
                        )
                    )
                    nInterface.nSuccess()
                }
            } catch (e: Throwable) {

                nInterface.nError(
                    "Что-то пошло не так",
                ) {
                    init()
                }
            }
        }
    }
}
