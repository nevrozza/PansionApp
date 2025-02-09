package mentoring

import MainRepository
import allGroupMarks.DateModule
import allGroupMarks.DatesFilter
import allGroupMarks.getDF
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import components.MarkTableItem
import components.networkInterface.NetworkInterface
import deviceSupport.launchIO
import deviceSupport.withMain
import mentoring.MentoringStore.Intent
import mentoring.MentoringStore.Label
import mentoring.MentoringStore.Message
import mentoring.MentoringStore.State
import mentoring.preAttendance.ClientPreAttendance
import mentoring.preAttendance.RFetchPreAttendanceDayReceive
import mentoring.preAttendance.RSavePreAttendanceDayReceive
import registration.CloseRequestQRReceive
import registration.OpenRequestQRReceive
import registration.RegistrationRequest
import registration.SolveRequestReceive
import server.getCurrentEdYear
import server.getLocalDate

class MentoringExecutor(
    private val mainRepository: MainRepository,
    private val nInterface: NetworkInterface,
    private val nPreAttendance: NetworkInterface
) : CoroutineExecutor<Intent, Unit, State, Message, Label>() {
    override fun executeIntent(intent: Intent) {
        when (intent) {
            Intent.FetchStudents -> {
                fetchStudents()
                if (state().isTableView) fetchTable()
            }

            is Intent.SelectStudent -> dispatch(Message.StudentSelected(intent.login))
            is Intent.SelectPreAttendanceLogin -> selectPreAttendance(intent.login, intent.date, dayOfWeek = intent.dayOfWeek)
            is Intent.ChangeDate -> dispatch(Message.DateChanged(intent.date))
            is Intent.StartEditPreAttendance -> dispatch(
                Message.EditPreAttendanceStarted(
                    start = intent.start,
                    end = intent.end,
                    reason = intent.reason,
                    cIsGood = intent.cIsGood
                )
            )

            is Intent.ChangeCEnd -> dispatch(Message.CEndChanged(intent.end))
            is Intent.ChangeCIsGood -> dispatch(Message.CIsGoodChanged(intent.isGood))
            is Intent.ChangeCReason -> dispatch(Message.CReasonChanged(intent.reason))
            is Intent.ChangeCStart -> dispatch(Message.CStartChanged(intent.start))
            is Intent.SavePreAttendance -> savePreAttendance(
                login = intent.login, date = intent.date,
                start = state().cStart.toString(), end = state().cEnd.toString(),
                reason = state().cReason.toString(), isGood = state().cIsGood ?: false
            )

            is Intent.ManageQr -> manageQR(formId = intent.formId, isOpen = intent.isOpen)
            is Intent.SolveRequest -> solveRequest(isAccepted = intent.isAccepted, r = intent.r)
            is Intent.FormToSummary -> {
                val newFormsToSum = state().formsForSummary.toMutableList()
                if (intent.formId in newFormsToSum) {
                    newFormsToSum.remove(intent.formId)
                } else newFormsToSum.add(intent.formId)
                dispatch(Message.FormsToSummaryUpdated(newFormsToSum.toSet().toList()))
            }

            Intent.ChangeView -> {
                if (!state().isTableView) fetchTable()
                dispatch(Message.ViewChanged(!state().isTableView))
            }

            is Intent.ChangeFilterDate -> {
                dispatch(
                    Message.FilterDateChanged(
                        getDF(
                            oldDF = state().dateFilter,
                            newDF = intent.dateFilter
                        )
                    )
                )
                updateTableAfterPeriod()
            }

            is Intent.ChangeSubject -> {
                dispatch(Message.SubjectChanged(intent.subjectId))
                updateTableAfterSubject()
            }

            is Intent.UpdateOpenedForms -> dispatch(Message.OpenedFormsUpdated(intent.openedForms))
        }
    }

    private fun updateTableAfterPeriod() {
        scope.launchIO {
            val filteredDates = state().allDates.filter {
                when (state().dateFilter) {
                    is DatesFilter.Week -> it.date in state().weekDays
                    is DatesFilter.PreviousWeek -> it.date in state().previousWeekDays
                    is DatesFilter.Module -> it.module in (state().dateFilter as DatesFilter.Module).modules
                    else -> false
                }
            }

            val filteredSubjects = state().allSubjects.filter { s ->
                val groupsIds =
                    state().allGroups.filter { it.subjectId == s.key }.map { it.groupId }.toSet()
                val marks = state().allDateMarks.flatMap {
                    it.value.filter { it.groupId in groupsIds }
                }
                val nki = state().allNki.flatMap {
                    it.value.filter { it.groupId in groupsIds }
                }
                marks.isNotEmpty() || nki.isNotEmpty()
            }
            withMain {
                dispatch(
                    Message.UpdateTableAfterPeriod(
                        filteredSubjects, filteredDates
                    )
                )

                updateTableAfterSubject()
            }
        }
    }

    private fun updateTableAfterSubject() {
        scope.launchIO {
            val groupsIds =
                state().allGroups.filter { it.subjectId == state().chosenSubject }
                    .map { it.groupId }.toSet()
            val filteredMarks = state().allDateMarks.mapNotNull {
                val value = it.value.mapNotNull {
                    if (it.groupId in groupsIds) {
                        it
                    } else null
                }
                if (value.isNotEmpty()) {
                    it.key to value
                } else null
            }.toMap().filter { it.key in state().filteredDates.map { it.date } }

            val filteredNki = state().allNki.map {
                it.key to it.value.filter { it.groupId in groupsIds && (it.date in state().filteredDates.map { it.date }) }
            }.toMap()

            val filteredStudents = state().students.filter {
                val studentGroupIds = state().studentToGroups[it.login]
                
                !(studentGroupIds?.filter {
                    it in groupsIds
                }).isNullOrEmpty()
            }

            withMain {
                dispatch(
                    Message.UpdateTableAfterSubject(
                        filteredMarks,
                        filteredNki,
                        filteredStudents
                    )
                )
            }
        }
    }

    private fun fetchTable() {
        scope.launchIO {
            nInterface.nStartLoading()
            try {
                val r = mainRepository.fetchJournalBySubjects(
                    RFetchJournalBySubjectsReceive(
                        forms = state().formsForSummary,
                        edYear = getCurrentEdYear()
                    )
                )

                val dates =
                    (r.studentsMarks.flatMap {
                        (it.value).map { DateModule(it.mark.date, it.mark.module) }.toSet()
                    } + r.studentsNki.flatMap {
                        it.value.map {
                            DateModule(it.date, module = it.module)
                        }
                    }).toSet().toList().sortedBy { getLocalDate(it.date).toEpochDays() }
                val dm: MutableMap<String, MutableList<MarkTableItem>> = mutableMapOf()

                dates.map { it.date }.toSet().forEach { d ->
                    r.studentsMarks.forEach { s ->
                        val nd = (dm[d] ?: mutableListOf())
                        nd.addAll((s.value).filter { it.mark.date == d }.map {
                            MarkTableItem(
                                content = it.mark.content,
                                login = s.key,
                                reason = it.mark.reason,
                                reportId = it.mark.reportId,
                                module = it.mark.module,
                                date = it.mark.date,
                                deployTime = it.deployTime,
                                deployDate = it.deployDate,
                                deployLogin = it.deployLogin,
                                groupId = it.mark.groupId,
                                onClick = { reportId ->
//                                    studentReportDialog.onEvent(
//                                        StudentReportDialogStore.Intent.OpenDialog(
//                                            login = state().studentLogin,
//                                            reportId = reportId
//                                        )
//                                    )
                                }
                            )
                        })
                        dm[d] = nd
                    }
                }

                withMain {
                    dispatch(
                        Message.TableLoaded(
                            allSubjects = r.subjects,
                            allDates = dates,
                            allDateMarks = dm,
                            allNki = r.studentsNki,
                            chosenSubject = r.subjects.keys.firstOrNull() ?: 1,
                            groups = r.groups,
                            modules = dates.map { it.module }.toSet().toList(),
                            studentToGroups = r.studentsGroups
                        )
                    )
                    updateTableAfterPeriod()
                    updateTableAfterSubject()
                    nInterface.nSuccess()
                }
            } catch (e: Throwable) {
                withMain {
                    nInterface.nError("Не удалось загрузить таблицу", e) {
                        nInterface.goToNone()
                        dispatch(Message.ViewChanged(false))
                    }
                }
            }
        }
    }

    private fun solveRequest(isAccepted: Boolean, r: RegistrationRequest) {
        scope.launchIO {
            try {
                mainRepository.solveRegistrationRequest(
                    SolveRequestReceive(
                        isAccepted = isAccepted,
                        request = r
                    )
                )
                fetchStudents()
            } catch (e: Throwable) {
                println("IDK: ${e}")
            }
        }
    }

    private fun manageQR(formId: Int, isOpen: Boolean) {
        scope.launchIO {
            try {
                if (isOpen) {
                    mainRepository.openRegistrationQR(
                        OpenRequestQRReceive(
                            formId = formId
                        )
                    )
                } else {
                    mainRepository.closeRegistrationQR(
                        CloseRequestQRReceive(
                            formId = formId
                        )
                    )
                }
                withMain {
                    dispatch(Message.FormsUpdated(
                        forms = state().forms.map {
                            if (it.id == formId)
                                it.copy(isQrActive = isOpen)
                            else it
                        }
                    ))
                }

            } catch (e: Throwable) {
                println("XXSERROR: ${e}")
            }
        }
    }

    private fun savePreAttendance(
        login: String,
        date: String,
        start: String,
        end: String,
        reason: String,
        isGood: Boolean
    ) {
        scope.launchIO {
            try {
                nPreAttendance.nStartLoading()
                val PA = ClientPreAttendance(
                    start = start,
                    end = end,
                    reason = reason,
                    isGood = isGood
                )
                mainRepository.savePreAttendanceDay(
                    RSavePreAttendanceDayReceive(
                        studentLogin = login,
                        date = date,
                        preAttendance = PA
                    )
                )

                val newPreAttendance = state().preAttendance.toMutableMap() // login map
                if (newPreAttendance.containsKey(login)) {
                    val oldLogin = newPreAttendance[login]!!.toMutableMap() // date list
                    oldLogin[date] = PA
                    newPreAttendance[login] = oldLogin
                } else {
                    newPreAttendance[login] = mapOf(date to PA)
                }

                withMain {
                    dispatch(
                        Message.PreAttendanceUpdate(
                            preAttendance = newPreAttendance,
                            schedule = state().schedule
                        )
                    )
                    nPreAttendance.nSuccess()
                }

            } catch (e: Throwable) {
                nPreAttendance.nError(text = "Не удалось сохранить ДО", e) {
                    savePreAttendance(login, date, start, end, reason, isGood)
                }
            }
        }
    }

    private fun selectPreAttendance(login: String?, date: String, dayOfWeek: String) {
        dispatch(Message.PreAttendanceLoginChanged(login))
        if (login != null) {
            scope.launchIO {
                nPreAttendance.nStartLoading()
                try {
                    val r = mainRepository.fetchPreAttendanceDay(
                        RFetchPreAttendanceDayReceive(
                            studentLogin = login,
                            date = date,
                            dayOfWeek = dayOfWeek
                        )
                    )

                    //Map<String/*Login*/, Map<String/*Date*/, List<ScheduleForAttendance>>>
                    val newSchedule = state().schedule.toMutableMap() // login map
                    if (newSchedule.containsKey(login)) {
                        val oldLogin = newSchedule[login]!!.toMutableMap() // date list
                        oldLogin[date] = r.schedule
                        newSchedule[login] = oldLogin
                    } else {
                        newSchedule[login] = mapOf(date to r.schedule)
                    }

                    val newPreAttendance = state().preAttendance.toMutableMap() // login map
                    if (newPreAttendance.containsKey(login)) {
                        val oldLogin = newPreAttendance[login]!!.toMutableMap() // date list
                        oldLogin[date] = r.attendance
                        newPreAttendance[login] = oldLogin
                    } else {
                        newPreAttendance[login] = mapOf(date to r.attendance)
                    }

                    withMain {
                        dispatch(
                            Message.PreAttendanceUpdate(
                                preAttendance = newPreAttendance,
                                schedule = newSchedule
                            )
                        )
                        nPreAttendance.nSuccess()
                    }

                } catch (e: Throwable) {
                    nPreAttendance.nError(text = "Не удалось загрузить уроки", e) {
                        selectPreAttendance(login, date, dayOfWeek = dayOfWeek)
                    }
                }
            }
        }
    }

    private fun fetchStudents() {
        scope.launchIO {
            nInterface.nStartLoading()
            try {
                val r = mainRepository.fetchMentorStudents()
                withMain {
                    dispatch(
                        Message.StudentsFetched(
                            forms = r.forms,
                            students = r.students,
                            requests = r.requests
                        )
                    )
                    nInterface.nSuccess()
                }
            } catch (e: Throwable) {
                nInterface.nError(text = "Не удалось загрузить учеников", e) {
                    fetchStudents()
                }
            }
        }
    }
}
