package journal

import MainRepository
import ReportData
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import components.cAlertDialog.CAlertDialogComponent
import components.cAlertDialog.CAlertDialogStore
import components.listDialog.ListComponent
import components.listDialog.ListDialogStore
import components.listDialog.ListItem
import components.networkInterface.NetworkInterface
import journal.JournalStore.Intent
import journal.JournalStore.Label
import journal.JournalStore.State
import journal.JournalStore.Message
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import report.RCreateReportReceive
import server.getDate
import server.getSixTime

class JournalExecutor(
    private val mainRepository: MainRepository,
    private val groupListComponent: ListComponent,
    private val studentsInGroupCAlertDialogComponent: CAlertDialogComponent,
    private val nInterface: NetworkInterface,
    private val nOpenReportInterface: NetworkInterface,
) : CoroutineExecutor<Intent, Unit, State, Message, Label>() {
    override fun executeIntent(intent: Intent) {
        when (intent) {
            Intent.Init -> initComponent()
            is Intent.OnGroupClicked -> {
                groupListComponent.onEvent(ListDialogStore.Intent.HideDialog)
                studentsInGroupCAlertDialogComponent.onEvent(CAlertDialogStore.Intent.ShowDialog)
                fetchStudentsInGroup(intent.groupId)
            }

            Intent.CreateReport -> {
                scope.launch {
                    try {
                        studentsInGroupCAlertDialogComponent.nInterface.nStartLoading()
                        val id = mainRepository.createReport(RCreateReportReceive(
                            groupId = state().currentGroupId,
                            date = getDate(),
                            time = getSixTime(),
                            studentLogins = state().studentsInGroup.map { it.login }
                        )).reportId
                        dispatch(Message.ReportCreated(id))
                        studentsInGroupCAlertDialogComponent.nInterface.nSuccess()
                        fetchHeaders()
                    } catch (_: Throwable) {
                        studentsInGroupCAlertDialogComponent.nInterface.nError(
                            "Не удалось создать отчёт"
                        ) {
                            studentsInGroupCAlertDialogComponent.nInterface.goToNone()
                        }
                        dispatch(Message.ReportCreated(-1))
                    }
                }
            }

            Intent.ResetCreatingId -> dispatch(Message.CreatingIdReseted)
            is Intent.FetchReportData -> {
                scope.launch {
                    try {
                        nOpenReportInterface.nStartLoading()
                        val rd = mainRepository.fetchReportData(intent.reportHeader.reportId)
                        dispatch(Message.ReportDataFetched(
                            ReportData(
                                header = intent.reportHeader,
                                topic = rd.topic,
                                description = rd.description,
                                editTime = rd.editTime,
                                ids = rd.ids,
                                isMentorWas = rd.isMentorWas,
                                isEditable = rd.isEditable,
                                customColumns = rd.customColumns
                            )
                        ))
                        nOpenReportInterface.nSuccess()
                    } catch (_: Throwable) {
                        nOpenReportInterface.nError(
                            "Не удалось открыть отчёт"
                        ) {
                            nOpenReportInterface.goToNone()
                        }
                        dispatch(Message.ReportCreated(-1))
                    }
                }
            }
            Intent.ResetReportData -> dispatch(Message.ReportDataReseted)
            Intent.Refresh -> fetchHeaders()

        }
    }

    private fun initComponent() {
        scope.launch {

            fetchHeaders() //async { }
            fetchTeacherGroups() //async { }
        }
    }

    private fun fetchStudentsInGroup(groupId: Int) {
        scope.launch {
            try {
                studentsInGroupCAlertDialogComponent.nInterface.nStartLoading()
                val students = mainRepository.fetchStudentsInGroup(groupId).students
                dispatch(Message.StudentsInGroupUpdated(students, groupId))
                studentsInGroupCAlertDialogComponent.nInterface.nSuccess()
            } catch (e: Throwable) {
                println(e)
//                studentsInGroupCAlertDialogComponent.onEvent(CAlertDialogStore.Intent.CallError("Не удалось загрузить список учеников =/") {
//                    fetchStudentsInGroup(
//                        groupId
//                    )
//                })
            }
        }
    }

    private fun fetchHeaders() {
        print("started1")
        scope.launch {
            print("started2")
            try {
                print("started3")
                nInterface.nStartLoading()
//                groupListComponent.nInterface.nStartLoading()
                val headers = mainRepository.fetchReportHeaders().reportHeaders
                println("xx $headers")
                dispatch(Message.HeadersUpdated(headers))
//                groupListComponent.nInterface.nSuccess()
                nInterface.nSuccess()

            } catch (e: Throwable) {
                println(e)
                nInterface.nError("Не удалось загрузить список")  {
                    fetchHeaders()
                }
//                groupListComponent.nInterface.nError("Не удалось загрузить список групп") {
//                    fetchTeacherGroups()
//                }
//                groupListComponent.onEvent(ListDialogStore.Intent.CallError("Не удалось загрузить список групп =/") { fetchTeacherGroups() })
            }
        }
    }

    private fun fetchTeacherGroups() {
        scope.launch {
            try {
                groupListComponent.nInterface.nStartLoading()
                val groups = mainRepository.fetchTeacherGroups().groups
                groupListComponent.onEvent(ListDialogStore.Intent.InitList(
                    groups.filter { it.cutedGroup.isActive }.sortedBy { it.subjectId }.map {
                        ListItem(
                            id = it.cutedGroup.groupId.toString(),
                            text = "${it.subjectName} ${it.cutedGroup.groupName}"
                        )
                    }
                ))
                dispatch(Message.TeacherGroupsUpdated(groups))
                groupListComponent.nInterface.nSuccess()
            } catch (e: Throwable) {
                println(e)
                groupListComponent.nInterface.nError("Не удалось загрузить список групп") {
                    fetchTeacherGroups()
                }
//                groupListComponent.onEvent(ListDialogStore.Intent.CallError("Не удалось загрузить список групп =/") { fetchTeacherGroups() })
            }
        }
    }
}
