package journal

import CDispatcher
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
import journal.init.RFetchStudentsInGroupReceive
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
    private val fDateListComponent: ListComponent,
    private val fGroupListComponent: ListComponent,
    private val fTeachersListComponent: ListComponent,
    private val fStatusListComponent: ListComponent,
) : CoroutineExecutor<Intent, Unit, State, Message, Label>() {
    override fun executeIntent(intent: Intent) {
        when (intent) {
            Intent.Init -> initComponent()
            is Intent.OnGroupClicked -> {
                dispatch(Message.TimeChanged(intent.time))
                groupListComponent.onEvent(ListDialogStore.Intent.HideDialog)
                studentsInGroupCAlertDialogComponent.onEvent(CAlertDialogStore.Intent.ShowDialog)
                fetchStudentsInGroup(intent.groupId, date = intent.date, lessonId = intent.lessonId)
            }

            Intent.CreateReport -> {
                scope.launch(CDispatcher) {
                    try {
                        studentsInGroupCAlertDialogComponent.nInterface.nStartLoading()
                        val id = mainRepository.createReport(RCreateReportReceive(
                            groupId = state().currentGroupId,
                            date = getDate(),
                            time = state().time,
                            studentLogins = state().studentsInGroup.filter { !it.isDeleted }.map { it.p.login }
                        )).reportId
                        scope.launch {
                            dispatch(Message.ReportCreated(id))
                        }
                        studentsInGroupCAlertDialogComponent.nInterface.nSuccess()
                        fetchHeaders()
                    } catch (_: Throwable) {
                        studentsInGroupCAlertDialogComponent.nInterface.nError(
                            "Не удалось создать отчёт"
                        ) {
                            studentsInGroupCAlertDialogComponent.nInterface.goToNone()
                        }
                        scope.launch {
                            dispatch(Message.ReportCreated(-1))
                        }
                    }
                }
            }

            Intent.ResetCreatingId -> dispatch(Message.CreatingIdReseted)
            is Intent.FetchReportData -> {
                scope.launch {
                    try {
                        nOpenReportInterface.nStartLoading()
                        val rd = mainRepository.fetchReportData(intent.reportHeader.reportId)

                        dispatch(
                            Message.ReportDataFetched(
                                ReportData(
                                    header = intent.reportHeader,
                                    description = rd.description,
                                    editTime = rd.editTime,
                                    ids = rd.ids,
                                    isMentorWas = rd.isMentorWas,
                                    isEditable = rd.isEditable,
                                    customColumns = rd.customColumns
                                )
                            )
                        )
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
            Intent.Refresh -> initComponent()
            Intent.ResetTime -> dispatch(Message.TimeChanged(""))
            is Intent.FilterDate -> {
                dispatch(Message.DateFiltered(intent.date))
                fDateListComponent.onEvent(ListDialogStore.Intent.HideDialog)
            }

            is Intent.FilterGroup -> {
                dispatch(Message.GroupFiltered(intent.groupId))
                fGroupListComponent.onEvent(ListDialogStore.Intent.HideDialog)
            }

            is Intent.FilterStatus -> {
                dispatch(Message.StatusFiltered(intent.bool))
                fStatusListComponent.onEvent(ListDialogStore.Intent.HideDialog)
            }

            is Intent.FilterTeacher -> {
                val ids: MutableList<Int> = mutableListOf()
                val groupItemList = state().headers.filter {
                    if (intent.teacherLogin != null) {
                        it.teacherLogin == intent.teacherLogin
                    } else true
                }.mapNotNull {
                    if (it.groupId !in ids) {
                        ids.add(it.groupId)
                        ListItem(
                            id = it.groupId.toString(),
                            text = "${it.subjectName} ${it.groupName}"
                        )
                    } else null
                }.toSet().toList()
                fGroupListComponent.onEvent(
                    ListDialogStore.Intent.InitList(
                        groupItemList
                    )
                )
                dispatch(Message.TeacherFiltered(intent.teacherLogin))
                fTeachersListComponent.onEvent(ListDialogStore.Intent.HideDialog)
            }

            is Intent.FilterMyChildren -> filterMyChildren(intent.bool)
        }
    }

    private fun filterMyChildren(filter: Boolean) {
        dispatch(Message.MyChildrenFiltered(filter))
        if (filter && state().childrenGroupIds.isEmpty()) {
            fetchChildrenGroupIds()
        }
    }

    private fun fetchChildrenGroupIds() {
        scope.launch() {
            try {
                val r = mainRepository.fetchMentorGroupIds()
                dispatch(Message.MyChildrenGroupsFetched(r.ids))
            } catch (_: Throwable) {
            }
        }
    }

    private fun initComponent() {
        scope.launch {
            fetchHeaders() //async { }
            fetchTeacherGroups() //async { }
            if (state().isMentor) {
                fetchChildrenGroupIds()
            }
        }
    }

    private fun fetchStudentsInGroup(
        groupId: Int,
        date: String?,
        lessonId: Int?
    ) {
        scope.launch {
            try {
                studentsInGroupCAlertDialogComponent.nInterface.nStartLoading()
                val students = mainRepository.fetchStudentsInGroup(
                    RFetchStudentsInGroupReceive(
                        groupId = groupId,
                        date = date,
                        lessonId = lessonId
                    )
                ).students
                dispatch(Message.StudentsInGroupUpdated(students, groupId))
                studentsInGroupCAlertDialogComponent.nInterface.nSuccess()
            } catch (e: Throwable) {
                //CHECK
                studentsInGroupCAlertDialogComponent.nInterface.nError(text = "Не удалось загрузить список учеников =/") {
                    fetchStudentsInGroup(
                        groupId, date, lessonId
                    )
                }
            }
        }
    }

    private fun fetchHeaders() {
        scope.launch(CDispatcher) {
            try {
                nInterface.nStartLoading()
                val headers = mainRepository.fetchReportHeaders()
                println("XXXik: ${headers}")
                scope.launch {
                    dispatch(Message.HeadersUpdated(headers.reportHeaders, headers.currentModule))
                    nInterface.nSuccess()

                    val teacherItemsList = headers.reportHeaders.map {
                        ListItem(
                            id = it.teacherLogin,
                            text = it.teacherName
                        )
                    }.toSet().toList()
                    val dateItemList: List<ListItem> = listOf(
                        ListItem(id = "0", text = "За неделю"),
                        ListItem(id = "1", text = "За прошлую неделю")
                    ) +
                            headers.reportHeaders.map { ListItem(id = it.date, text = it.date) }
                                .toSet()
                                .toList().reversed()

                    val ids: MutableList<Int> = mutableListOf()

                    val groupItemList = headers.reportHeaders.mapNotNull {
                        if (it.groupId !in ids) {
                            ids.add(it.groupId)
                            ListItem(
                                id = it.groupId.toString(),
                                text = "${it.subjectName} ${it.groupName}"
                            )
                        } else null
                    }.toSet().toList()

                    fGroupListComponent.onEvent(
                        ListDialogStore.Intent.InitList(
                            groupItemList
                        )
                    )



                    fGroupListComponent.onEvent(
                        ListDialogStore.Intent.InitList(
                            groupItemList
                        )
                    )
                    fTeachersListComponent.onEvent(
                        ListDialogStore.Intent.InitList(
                            teacherItemsList
                        )
                    )
                    fDateListComponent.onEvent(
                        ListDialogStore.Intent.InitList(
                            dateItemList
                        )
                    )
                }


            } catch (e: Throwable) {
                println(e)
                nInterface.nError("Не удалось загрузить список") {
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
                val groups =
                    mainRepository.fetchTeacherGroups().groups.filter { it.teacherLogin == state().login }
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
