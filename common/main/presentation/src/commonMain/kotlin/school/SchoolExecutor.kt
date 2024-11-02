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
import main.school.RFetchDutyReceive
import main.school.RStartNewDayDuty
import main.school.RUpdateTodayDuty
import school.SchoolStore.Intent
import school.SchoolStore.Label
import school.SchoolStore.Message
import school.SchoolStore.State
import server.Moderation
import server.Roles

class SchoolExecutor(
    private val nInterface: NetworkInterface,
    private val nDutyInterface: NetworkInterface,
    private val openMinSettingsBottom: CBottomSheetComponent,
    private val mainRepository: MainRepository
) : CoroutineExecutor<Intent, Unit, State, Message, Label>() {
    override fun executeIntent(intent: Intent) {
        when (intent) {
            Intent.Init -> {
                init()
                if (state().moderation in listOf(Moderation.both, Moderation.mentor) || state().role == Roles.student) {
                    fetchDuty()
                }
            }
            Intent.OpenMinistrySettings -> openMinistrySettings()
            is Intent.SetMinistryStudent -> setMinistryStudent(ministryId = intent.ministryId, fio = intent.fio, login = intent.login)
            is Intent.StartNewDayDuty -> startNewDayDuty(intent.newDutyPeopleCount)
            is Intent.UpdateTodayDuty -> updateTodayDuty(intent.newDutyPeopleCount, intent.kids)
        }
    }

    private fun startNewDayDuty(newDutyPeopleCount: Int) {
        scope.launch(CDispatcher) {
            nDutyInterface.nStartLoading()
            try {
                mainRepository.startNewDayDuty(
                    RStartNewDayDuty(
                        newDutiesCount = newDutyPeopleCount
                    )
                )
                scope.launch {
                    nDutyInterface.nSuccess()
                    fetchDuty()
                }
            } catch (e: Throwable) {

                nDutyInterface.nError(
                    "Не удалось обновить дежурство",
                ) {
                    nDutyInterface.goToNone()
                }
            }
        }
    }
    private fun updateTodayDuty(newDutyPeopleCount: Int, kids: List<String>) {
        scope.launch(CDispatcher) {
            nDutyInterface.nStartLoading()
            try {
                mainRepository.updateTodayDuty(
                    RUpdateTodayDuty(
                        newDutiesCount = newDutyPeopleCount,
                        kids = kids
                    )
                )
                scope.launch {
                    nDutyInterface.nSuccess()
                    fetchDuty()
                }
            } catch (e: Throwable) {

                nDutyInterface.nError(
                    "Не удалось редактировать дежурство",
                ) {
                    nDutyInterface.goToNone()
                }
            }
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

    private fun fetchDuty() {
        scope.launch(CDispatcher) {
            nDutyInterface.nStartLoading()
            try {
                val r = mainRepository.fetchDuty(
                    RFetchDutyReceive(
                        login = state().login
                    )
                )
                scope.launch {
                    dispatch(
                        Message.DutyFetched(
                            dutyKids = r.list,
                            dutyPeopleCount = r.peopleCount
                        )
                    )
                    nDutyInterface.nSuccess()
                }
            } catch (e: Throwable) {
                nDutyInterface.nError(
                    "Не удалось загрузить график дежурств",
                ) {
                    fetchDuty()
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
                            ministryId = r.ministryId,
                            mvdStupsCount = r.mvdStups,
                            zdStupsCount = r.zdStups
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
