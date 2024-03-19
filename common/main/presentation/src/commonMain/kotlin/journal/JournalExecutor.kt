package journal

import MainRepository
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import components.cAlertDialog.CAlertDialogComponent
import components.cAlertDialog.CAlertDialogStore
import components.listDialog.ListComponent
import components.listDialog.ListDialogStore
import components.listDialog.ListItem
import journal.JournalStore.Intent
import journal.JournalStore.Label
import journal.JournalStore.State
import journal.JournalStore.Message
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class JournalExecutor(
    private val mainRepository: MainRepository,
    private val groupListComponent: ListComponent,
    private val studentsInGroupCAlertDialogComponent: CAlertDialogComponent
) : CoroutineExecutor<Intent, Unit, State, Message, Label>() {
    override fun executeIntent(intent: Intent) {
        when (intent) {
            Intent.Init -> initComponent()
            is Intent.OnGroupClicked -> {
                groupListComponent.onEvent(ListDialogStore.Intent.HideDialog)
                studentsInGroupCAlertDialogComponent.onEvent(CAlertDialogStore.Intent.ShowDialog)
                fetchStudentsInGroup(intent.groupId)
            }
        }
    }

    private fun initComponent() {
        scope.launch {
            fetchTeacherGroups() //async { }
        }
    }

    private fun fetchStudentsInGroup(groupId: Int) {
        scope.launch {
            try {
                studentsInGroupCAlertDialogComponent.nInterface.nStartLoading()
                val students = mainRepository.fetchStudentsInGroup(groupId).students
                dispatch(Message.StudentsInGroupUpdated(students, groupId))
                studentsInGroupCAlertDialogComponent.fullySuccess()

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

    private fun fetchTeacherGroups() {
        scope.launch {
//            try {
//                groupListComponent.onEvent(ListDialogStore.Intent.StartProcess)
//                val groups = mainRepository.fetchTeacherGroups().groups
//                groupListComponent.onEvent(ListDialogStore.Intent.InitList(
//                    groups.filter { it.cutedGroup.isActive }.sortedBy { it.subjectId }.map {
//                        ListItem(
//                            id = it.cutedGroup.groupId,
//                            text = "${it.subjectName} ${it.cutedGroup.groupName}"
//                        )
//                    }
//                ))
//            } catch (e: Throwable) {
//                println(e)
//                groupListComponent.onEvent(ListDialogStore.Intent.CallError("Не удалось загрузить список групп =/") { fetchTeacherGroups() })
//            }
        }
    }
}
