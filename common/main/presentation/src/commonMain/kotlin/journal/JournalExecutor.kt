package journal

import MainRepository
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import components.cAlertDialog.CAlertDialogComponent
import components.cAlertDialog.CAlertDialogStore
import components.listDialog.ListDialogComponent
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
    private val groupListDialogComponent: ListDialogComponent,
    private val studentsInGroupCAlertDialogComponent: CAlertDialogComponent
) : CoroutineExecutor<Intent, Unit, State, Message, Label>() {
    override fun executeIntent(intent: Intent, getState: () -> State) {
        when (intent) {
            Intent.Init -> initComponent()
            is Intent.OnGroupClicked -> {
                groupListDialogComponent.onEvent(ListDialogStore.Intent.HideDialog)
                studentsInGroupCAlertDialogComponent.onEvent(CAlertDialogStore.Intent.ShowDialog)
                fetchStudentsInGroup(intent.groupId)
            }
        }
    }

    private fun initComponent() {
        scope.launch {
            async { fetchTeacherGroups() }
        }
    }

    private fun fetchStudentsInGroup(groupId: Int) {
        scope.launch {
            try {
                studentsInGroupCAlertDialogComponent.onEvent(CAlertDialogStore.Intent.StartProcess)
                val students = mainRepository.fetchStudentsInGroup(groupId).students
                dispatch(Message.StudentsInGroupUpdated(students, groupId))
                studentsInGroupCAlertDialogComponent.onEvent(CAlertDialogStore.Intent.StopProcess)

            } catch (e: Throwable) {
                println(e)
                studentsInGroupCAlertDialogComponent.onEvent(CAlertDialogStore.Intent.CallError("Не удалось загрузить список учеников =/") {
                    fetchStudentsInGroup(
                        groupId
                    )
                })
            }
        }
    }

    private fun fetchTeacherGroups() {
        scope.launch {
            try {
                groupListDialogComponent.onEvent(ListDialogStore.Intent.StartProcess)
                val groups = mainRepository.fetchTeacherGroups().groups
                groupListDialogComponent.onEvent(ListDialogStore.Intent.InitList(
                    //IGNORE IT
                    groups.filter { it.isActivated }.sortedBy { it.subjectNum }.map {
                        ListItem(
                            id = it.id,
                            text = it.name
                        )
                    }
                ))
            } catch (e: Throwable) {
                println(e)
                groupListDialogComponent.onEvent(ListDialogStore.Intent.CallError("Не удалось загрузить список групп =/") { fetchTeacherGroups() })
            }
        }
    }
}
