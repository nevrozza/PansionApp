package school

import CDispatcher
import JournalRepository
import MainRepository
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import components.cBottomSheet.CBottomSheetComponent
import components.cBottomSheet.CBottomSheetStore
import components.networkInterface.NetworkInterface
import kotlinx.coroutines.launch
import main.RFetchSchoolDataReceive
import main.school.*
import school.SchoolStore.Intent
import school.SchoolStore.Label
import school.SchoolStore.Message
import school.SchoolStore.State
import server.Moderation
import server.Roles
import server.headerTitlesForMinistry

class SchoolExecutor(
    private val nInterface: NetworkInterface,
    private val nDutyInterface: NetworkInterface,
    private val openMinSettingsBottom: CBottomSheetComponent,
    private val ministryOverview: CBottomSheetComponent,
    private val mainRepository: MainRepository,
    private val journalRepository: JournalRepository
) : CoroutineExecutor<Intent, Unit, State, Message, Label>() {
    override fun executeIntent(intent: Intent) {
        when (intent) {
            Intent.Init -> {
                init()
                if (state().moderation in listOf(Moderation.both, Moderation.mentor) || state().role == Roles.student) {
                    fetchDuty()
                }
            }
            is Intent.OpenMinistrySettings -> openMinistrySettings(intent.reason)
            is Intent.SetMinistryStudent -> setMinistryStudent(ministryId = intent.ministryId, fio = intent.fio, login = intent.login)
            is Intent.StartNewDayDuty -> startNewDayDuty(intent.newDutyPeopleCount)
            is Intent.UpdateTodayDuty -> updateTodayDuty(intent.newDutyPeopleCount, intent.kids)
            is Intent.OpenMinistryOverview -> openMinistryOverview(intent.ministryOverviewId, date = state().currentDate.second)
            is Intent.ChangeDate -> {
                dispatch(Message.DateChanged(intent.date))
                openMinistryOverview(ministryOverviewId = state().ministryOverviewId, date = intent.date.second)
            }
        }
    }
    private fun openMinistryOverview(ministryOverviewId: String, date: String) {
        dispatch(Message.MinistryOverviewOpened(ministryOverviewId = ministryOverviewId))
        ministryOverview.onEvent(CBottomSheetStore.Intent.ShowSheet)
        scope.launch(CDispatcher) {
            ministryOverview.nInterface.nStartLoading()
            try {
                val r = journalRepository.fetchMinistryList(
                    RMinistryListReceive(
                        date = date,
                        ministryId = ministryOverviewId,
                        login = state().login,
                        null
                    )
                )

                val newList = state().ministryList.toMutableList()
                val oldItem = newList.firstOrNull { it.date == date && it.ministryId == ministryOverviewId }
                newList.remove(oldItem)
                newList.add(
                    MinistryListItem(
                        date = date,
                        ministryId = ministryOverviewId,
                        kids = r.kids
                    )
                )
                scope.launch {
                    dispatch(Message.MinistryListUpdated(newList))
                    ministryOverview.nInterface.nSuccess()
                }
            } catch (e: Throwable) {
                ministryOverview.nInterface.nError(
                    "Не загрузить данные о министерстве: ${
                        headerTitlesForMinistry[ministryOverviewId]
                    }", e
                ) {
                    openMinistryOverview(ministryOverviewId, date)
                }
            }
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
                    "Не удалось обновить дежурство", e
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
                    "Не удалось редактировать дежурство", e
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
                        login = login,
                        lvl = if (state().ministrySettingsReason == MinistrySettingsReason.School) "1" else "0",
                        reason = state().ministrySettingsReason
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
                    "Что-то пошло не так", e
                ) {
                    openMinSettingsBottom.nInterface.goToNone()
                }
            }
        }
    }

    private fun openMinistrySettings(reason: MinistrySettingsReason) {
        dispatch(Message.MinistrySettingsReasonChanged(reason))
        openMinSettingsBottom.onEvent(CBottomSheetStore.Intent.ShowSheet)
        scope.launch(CDispatcher) {
            openMinSettingsBottom.nInterface.nStartLoading()
            try {
                val r = mainRepository.fetchMinistrySettings(
                    RFetchMinistryStudentsReceive(
                        reason = reason
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
                    "Что-то пошло не так", e
                ) {
                    openMinistrySettings(reason)
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
                    "Не удалось загрузить график дежурств", e
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
                    "Что-то пошло не так", e
                ) {
                    init()
                }
            }
        }
    }
}
