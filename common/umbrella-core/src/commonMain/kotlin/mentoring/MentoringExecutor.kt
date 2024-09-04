package mentoring

import CDispatcher
import MainRepository
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import components.networkInterface.NetworkInterface
import kotlinx.coroutines.launch
import mentoring.MentoringStore.Intent
import mentoring.MentoringStore.Label
import mentoring.MentoringStore.State
import mentoring.MentoringStore.Message
import mentoring.preAttendance.ClientPreAttendance
import mentoring.preAttendance.RFetchPreAttendanceDayReceive
import mentoring.preAttendance.RSavePreAttendanceDayReceive
import registration.CloseRequestQRReceive
import registration.OpenRequestQRReceive
import registration.RegistrationRequest
import registration.SolveRequestReceive

class MentoringExecutor(
    private val mainRepository: MainRepository,
    private val nInterface: NetworkInterface,
    private val nPreAttendance: NetworkInterface
) : CoroutineExecutor<Intent, Unit, State, Message, Label>() {
    override fun executeIntent(intent: Intent) {
        when (intent) {
            Intent.FetchStudents -> fetchStudents()
            is Intent.SelectStudent -> dispatch(Message.StudentSelected(intent.login))
            is Intent.SelectPreAttendanceLogin -> selectPreAttendance(intent.login, intent.date)
            is Intent.ChangeDate -> dispatch(Message.DateChanged(intent.date))
            is Intent.StartEditPreAttendance -> dispatch(Message.EditPreAttendanceStarted(
                start = intent.start,
                end = intent.end,
                reason = intent.reason,
                cIsGood = intent.cIsGood
            ))

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
        }
    }
    private fun solveRequest(isAccepted: Boolean, r: RegistrationRequest) {
        scope.launch(CDispatcher) {
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
        scope.launch(CDispatcher) {
            try {
                println("SADIKX${formId}${isOpen}")
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
                scope.launch {
                    dispatch(Message.FormsUpdated(
                        forms = state().forms.map {
                            if (it.id == formId)
                                it.copy(isQrActive = isOpen)
                            else it
                        }
                    ))
                    println("SSX")
                }

            } catch (e: Throwable) {
                println("XXS: ${e}")
            }
        }
    }

    private fun savePreAttendance(login: String, date: String, start: String, end: String, reason: String, isGood: Boolean) {
        scope.launch(CDispatcher) {
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
                if(newPreAttendance.containsKey(login)) {
                    val oldLogin = newPreAttendance[login]!!.toMutableMap() // date list
                    oldLogin[date] = PA
                    newPreAttendance[login] = oldLogin
                } else {
                    newPreAttendance[login] = mapOf(date to PA)
                }

                scope.launch {
                    dispatch(Message.PreAttendanceUpdate(preAttendance = newPreAttendance, schedule = state().schedule))
                    nPreAttendance.nSuccess()
                }

            } catch (_: Throwable) {
                nPreAttendance.nError(text = "Не удалось сохранить ДО") {
                    savePreAttendance(login, date, start, end, reason, isGood)
                }
            }
        }
    }

    private fun selectPreAttendance(login: String?, date: String) {
        dispatch(Message.PreAttendanceLoginChanged(login))
        if(login != null) {
            scope.launch(CDispatcher) {
                nPreAttendance.nStartLoading()
                try {
                    val r = mainRepository.fetchPreAttendanceDay(RFetchPreAttendanceDayReceive(
                        studentLogin = login,
                        date = date
                    ))

                    //Map<String/*Login*/, Map<String/*Date*/, List<ScheduleForAttendance>>>
                    val newSchedule = state().schedule.toMutableMap() // login map
                    if(newSchedule.containsKey(login)) {
                        val oldLogin = newSchedule[login]!!.toMutableMap() // date list
                        oldLogin[date] = r.schedule
                        newSchedule[login] = oldLogin
                    } else {
                        newSchedule[login] = mapOf(date to r.schedule)
                    }

                    val newPreAttendance = state().preAttendance.toMutableMap() // login map
                    if(newPreAttendance.containsKey(login)) {
                        val oldLogin = newPreAttendance[login]!!.toMutableMap() // date list
                        oldLogin[date] = r.attendance
                        newPreAttendance[login] = oldLogin
                    } else {
                        newPreAttendance[login] = mapOf(date to r.attendance)
                    }

                    scope.launch {
                        dispatch(Message.PreAttendanceUpdate(
                            preAttendance = newPreAttendance,
                            schedule = newSchedule
                        ))
                        nPreAttendance.nSuccess()
                    }

                } catch (_: Throwable) {
                    nPreAttendance.nError(text = "Не удалось загрузить уроки") {
                        selectPreAttendance(login, date)
                    }
                }
            }
        }
    }

    private fun fetchStudents() {
        scope.launch(CDispatcher) {
            nInterface.nStartLoading()
            try {
                val r = mainRepository.fetchMentorStudents()
                scope.launch {
                    dispatch(Message.StudentsFetched(forms = r.forms, students = r.students, requests = r.requests))
                    nInterface.nSuccess()
                }
            } catch (_: Throwable) {
                nInterface.nError(text = "Не удалось загрузить учеников") {
                    fetchStudents()
                }
            }
        }
    }
}
