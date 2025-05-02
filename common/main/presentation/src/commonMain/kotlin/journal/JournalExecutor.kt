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
import deviceSupport.launchIO
import deviceSupport.withMain
import di.Inject
import journal.JournalStore.Intent
import journal.JournalStore.Label
import journal.JournalStore.Message
import journal.JournalStore.State
import journal.init.RFetchStudentsInGroupReceive
import report.RCreateReportReceive
import server.getDate

class JournalExecutor(
    private val mainRepository: MainRepository = Inject.instance(),
    private val groupListComponent: ListComponent,
    private val studentsInGroupCAlertDialogComponent: CAlertDialogComponent,
    private val nInterface: NetworkInterface,
    private val nOpenReportInterface: NetworkInterface,
    private val fDateListComponent: ListComponent,
    private val fGroupListComponent: ListComponent,
    private val fTeachersListComponent: ListComponent,
    private val fStatusListComponent: ListComponent,
) : CoroutineExecutor<Intent, Unit, State, Message, Label>() {

    override fun executeAction(action: Unit) {
        initComponent()

        fStatusListComponent.onEvent(
            ListDialogStore.Intent.InitList(
                listOf(
                    ListItem(
                        id = "True",
                        text = "Закончен"
                    ),
                    ListItem(
                        id = "False",
                        text = "В процессе"
                    )
                )
            )
        )
    }


    override fun executeIntent(intent: Intent) {
        when (intent) {
            Intent.Init -> initComponent()
            is Intent.OnGroupClicked -> {
                dispatch(Message.TimeChanged(intent.time))
                dispatch(Message.LessonIdChanged(intent.lessonId))
                groupListComponent.onEvent(ListDialogStore.Intent.HideDialog)
                studentsInGroupCAlertDialogComponent.onEvent(CAlertDialogStore.Intent.ShowDialog)
                fetchStudentsInGroup(intent.groupId, date = intent.date, lessonId = intent.lessonId)
            }

            is Intent.CreateReport -> {
                scope.launchIO {
                    try {
                        studentsInGroupCAlertDialogComponent.nInterface.nStartLoading()

                        val studentLogins = state().studentsInGroup.filter { !it.isDeleted }
                        val deletedStudentLogins = state().studentsInGroup - studentLogins.toSet()
                        val id = mainRepository.createReport(RCreateReportReceive(
                            groupId = state().currentGroupId,
                            date = getDate(),
                            time = state().time,
                            studentLogins = studentLogins.map { it.p.login },
                            deletedStudentLogins = deletedStudentLogins.map { it.p.login },
                            lessonId = state().lessonId
                        )).reportId
                        withMain {
                            dispatch(Message.ReportCreated(id))
                        }
                        studentsInGroupCAlertDialogComponent.nInterface.nSuccess()
                        fetchHeaders()
                    } catch (e: Throwable) {
                        studentsInGroupCAlertDialogComponent.nInterface.nError(
                            "Не удалось создать отчёт", e
                        ) {
                            studentsInGroupCAlertDialogComponent.nInterface.goToNone()
                        }
                        withMain {
                            dispatch(Message.ReportCreated(-1))
                        }
                    }
                }
            }

            Intent.ResetCreatingId -> dispatch(Message.CreatingIdReseted)
            is Intent.FetchReportData -> {
                scope.launchIO {
                    try {
                        nOpenReportInterface.nStartLoading()
                        val rd = mainRepository.fetchReportData(intent.reportHeader.reportId)
                        withMain {
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
                        }
                    } catch (e: Throwable) {
                        withMain {
                            nOpenReportInterface.nError(
                                "Не удалось открыть отчёт", e
                            ) {
                                nOpenReportInterface.goToNone()
                            }
                            dispatch(Message.ReportCreated(-1))
                        }
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
        scope.launchIO {
            try {
                val r = mainRepository.fetchMentorGroupIds()
                withMain {
                    dispatch(Message.MyChildrenGroupsFetched(r.ids))
                }
            } catch (_: Throwable) {
            }
        }
    }

    private fun initComponent() {
//        scope.launch {
            fetchHeaders() //async { }
            fetchTeacherGroups() //async { }
            if (state().isMentor) {
                fetchChildrenGroupIds()
            }
//        }
    }

    private fun fetchStudentsInGroup(
        groupId: Int,
        date: String?,
        lessonId: Int?
    ) {
        scope.launchIO {
            try {
                studentsInGroupCAlertDialogComponent.nInterface.nStartLoading()
                val students = mainRepository.fetchStudentsInGroup(
                    RFetchStudentsInGroupReceive(
                        groupId = groupId,
                        date = date,
                        lessonId = lessonId
                    )
                ).students
                withMain {
                    dispatch(Message.StudentsInGroupUpdated(students, groupId))
                    studentsInGroupCAlertDialogComponent.nInterface.nSuccess()
                }
            } catch (e: Throwable) {
                //CHECK
                studentsInGroupCAlertDialogComponent.nInterface.nError(text = "Не удалось загрузить список учеников =/", e) {
                    fetchStudentsInGroup(
                        groupId, date, lessonId
                    )
                }
            }
        }
    }

    private fun fetchHeaders() {
        scope.launchIO {
            try {
                nInterface.nStartLoading()
                val headers = mainRepository.fetchReportHeaders()
                withMain {
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
                nInterface.nError("Не удалось загрузить список", e) {
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
        scope.launchIO {
            try {
                groupListComponent.nInterface.nStartLoading()
                val groups =
                    mainRepository.fetchTeacherGroups().groups.filter { it.teacherLogin == state().login }
                withMain {
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
                }
            } catch (e: Throwable) {
                println(e)
                groupListComponent.nInterface.nError("Не удалось загрузить список групп", e) {
                    fetchTeacherGroups()
                }
//                groupListComponent.onEvent(ListDialogStore.Intent.CallError("Не удалось загрузить список групп =/") { fetchTeacherGroups() })
            }
        }
    }
}
