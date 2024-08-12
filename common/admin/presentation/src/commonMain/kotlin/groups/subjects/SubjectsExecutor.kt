package groups.subjects

import AdminRepository
import CDispatcher
import MainRepository
import admin.groups.subjects.REditGroupReceive
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import components.networkInterface.NetworkInterface
import components.cAlertDialog.CAlertDialogComponent
import components.cAlertDialog.CAlertDialogStore
import components.cBottomSheet.CBottomSheetComponent
import groups.subjects.SubjectsStore.Intent
import groups.subjects.SubjectsStore.Label
import groups.subjects.SubjectsStore.State
import groups.subjects.SubjectsStore.Message
import kotlinx.coroutines.launch

class SubjectsExecutor(
    private val adminRepository: AdminRepository,
    private val mainRepository: MainRepository,
    private val nGroupInterface: NetworkInterface,
    private val nSubjectsInterface: NetworkInterface,
    private val updateSubjects: () -> Unit,
    private val cSubjectDialog: CAlertDialogComponent,
    private val cGroupBottomSheet: CBottomSheetComponent,
    private val editSubjectDialog: CAlertDialogComponent,
    private val deleteSubjectDialog: CAlertDialogComponent,
    private val eGroupBottomSheet: CBottomSheetComponent,
) : CoroutineExecutor<Intent, Unit, State, Message, Label>() {
    override fun executeIntent(intent: Intent) {
        when (intent) {
            is Intent.ChangeCDifficult -> dispatch(Message.CDifficultChanged(intent.difficult))
            is Intent.ChangeCName -> dispatch(Message.CNameChanged(intent.name))
            is Intent.ChangeCSubjectText -> dispatch(Message.CSubjectTextChanged(intent.text))
            is Intent.ChangeCTeacherLogin -> dispatch(Message.CTeacherLoginChanged(intent.teacherLogin))
            is Intent.ClickOnSubject -> changeSubjectId(intent.subjectId)
            Intent.CreateGroup -> createGroup(state())
            Intent.CreateSubject -> createSubject(state())
            is Intent.FetchStudents -> fetchStudents(groupId = intent.groupId)
            is Intent.ChangeESubjectText -> dispatch(Message.ESubjectTextChanged(intent.text))
            Intent.DeleteSubject -> deleteSubject()
            is Intent.EditSubject -> if(intent.sameCount <= 1) editSubject()
            is Intent.EditSubjectInit -> {
                dispatch(Message.ESubjectTextChanged(intent.text))
                dispatch(Message.EditSubjectInit(intent.subjectId))
            }

            Intent.Update -> update()
            is Intent.ChangeEDifficult -> dispatch(Message.EDifficultChanged(intent.difficult))
            is Intent.ChangeEName -> dispatch(Message.ENameChanged(intent.name))
            is Intent.ChangeETeacherLogin -> dispatch(Message.ETeacherLoginChanged(intent.teacherLogin))
            Intent.DeleteGroup -> editGroup(false)
            Intent.EditGroup -> editGroup(true)
            is Intent.GroupEditInit -> dispatch(Message.EditGroupInit(intent.groupId))
        }
    }
    private fun editGroup(isActive: Boolean) {
        scope.launch(CDispatcher) {
            if (state().eName.isNotBlank() && state().eDifficult.isNotBlank()) {
                try {
                    eGroupBottomSheet.nInterface.nStartLoading()
                    adminRepository.editGroup(
                        REditGroupReceive(
                            id = state().eGroupId,
                            name = state().eName,
                            mentorLogin = state().eTeacherLogin,
                            difficult = state().eDifficult,
                            isActive = isActive
                        )
                    )
                    scope.launch {
                        eGroupBottomSheet.fullySuccess()
                        updateGroups(state().chosenSubjectId)
                    }
                } catch (e: Throwable) {
                    println(e)
                    eGroupBottomSheet.nInterface.nError("Не удалось изменить группу") {
                        eGroupBottomSheet.nInterface.goToNone()
                    }
                }
            }
        }
    }
    private fun update() {
        println("SADIK: INTEGER")
        scope.launch {
            try {
                updateSubjects()
            } catch (_: Throwable) {
                cSubjectDialog.nInterface.nError(
                    "Что-то пошло не так =/",
                    onFixErrorClick = {
                        updateSubjects()
                    })
            }
        }
    }

    private fun wtfUpdateSubjects() {
        scope.launch {
            editSubjectDialog.nInterface.nSuccess()
            deleteSubjectDialog.nInterface.nSuccess()
            update()
            editSubjectDialog.onEvent(CAlertDialogStore.Intent.HideDialog)
            deleteSubjectDialog.onEvent(CAlertDialogStore.Intent.HideDialog)
        }
    }

    private fun editSubject() {

        scope.launch(CDispatcher) {
            if (state().eSubjectText.isNotBlank()) {
                try {
                    editSubjectDialog.nInterface.nStartLoading()
                    adminRepository.editSubject(state().eSubjectId, name = state().eSubjectText)
                    wtfUpdateSubjects()
                } catch (e: Throwable) {
                    println(e)
                    editSubjectDialog.nInterface.nError("Не удалось изменить урок") {
                        editSubjectDialog.nInterface.goToNone()
                    }
                }
            }
        }
    }

    private fun deleteSubject() {
        scope.launch(CDispatcher) {
            try {
                deleteSubjectDialog.nInterface.nStartLoading()
                adminRepository.deleteSubject(state().eSubjectId)
                wtfUpdateSubjects()
            } catch (_: Throwable) {
                deleteSubjectDialog.nInterface.nError("Не удалось удалить урок") {
                    deleteSubjectDialog.nInterface.goToNone()
                }
            }
        }
    }

    private fun fetchStudents(groupId: Int) {
        if (groupId != state().currentGroup) {
            dispatch(Message.CurrentGroupChanged(groupId))
            scope.launch(CDispatcher) {
                nGroupInterface.nStartLoading()
                try {
                    val students = mainRepository.fetchStudentsInGroup(groupId).students
                    val newMap = state().students.toMutableMap()
                    newMap[groupId] = students
                    scope.launch {
                        dispatch(Message.StudentsFetched(newMap.toMap(HashMap())))
                        nGroupInterface.nStartLoading()
                    }
                } catch (_: Throwable) {
                    nGroupInterface.nError("Не удалось загрузить список учеников") {
                        fetchStudents(groupId)
                    }
                }
            }
        } else {
            dispatch(Message.CurrentGroupChanged(0))
        }
    }


    private fun createSubject(state: State) {
        scope.launch {
//            dispatch(GroupsStore.Message.GSubjectListProcessStarted)
            cSubjectDialog.nInterface.nStartLoading()
            try {
                adminRepository.createSubject(state.cSubjectText)
                cSubjectDialog.fullySuccess()
                dispatch(Message.CSubjectTextChanged(""))
                    update()
            } catch (e: Throwable) {
                with(cSubjectDialog.nInterface) {
                    nError("Что-то пошло не так =/", onFixErrorClick = {
                        goToNone()
                    })
                }
                println(e)
            }
        }
    }

    private fun changeSubjectId(id: Int) {
        scope.launch {
            dispatch(Message.ChosenSubjectChanged(id))
            updateGroups(id)
        }
    }

    private fun createGroup(state: State) {
        scope.launch {
//            dispatch(GroupsStore.Message.CreatingProcessStarted)
            cGroupBottomSheet.nInterface.nStartLoading()
            try {
                adminRepository.createGroup(
                    name = state.cName,
                    mentorLogin = state.cTeacherLogin,
                    subjectId = state.chosenSubjectId,
                    difficult = state.cDifficult
                )
                cGroupBottomSheet.fullySuccess()
                dispatch(Message.CNameChanged(""))
                dispatch(Message.CDifficultChanged(""))
                dispatch(Message.CTeacherLoginChanged(""))

                updateGroups(state.chosenSubjectId)
//                dispatch(GroupsStore.Message.GroupCreated(groups))
            } catch (_: Throwable) {
                with(cGroupBottomSheet.nInterface) {
                    nError("Что-то пошло не так =/", onFixErrorClick = {
                        goToNone()
                    })
                }
//                nSubjectsInterface.nError("Что-то пошло не так =/")
            }
        }

    }

    private suspend fun updateGroups(subjectId: Int) {
        nSubjectsInterface.nStartLoading()
        try {
            val groups = adminRepository.fetchGroups(subjectId).groups
            if (state().chosenSubjectId == subjectId) {
                dispatch(Message.GroupsUpdated(groups))
                nSubjectsInterface.nSuccess()
            }
        } catch (_: Throwable) {
            nSubjectsInterface.nError("Что-то пошло не так =/", onFixErrorClick = {
                scope.launch {
                    updateGroups(subjectId)
                }
            })
        }
    }
}
