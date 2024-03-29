package groups.subjects

import AdminRepository
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import components.networkInterface.NetworkInterface
import components.cAlertDialog.CAlertDialogComponent
import components.cBottomSheet.CBottomSheetComponent
import groups.subjects.SubjectsStore.Intent
import groups.subjects.SubjectsStore.Label
import groups.subjects.SubjectsStore.State
import groups.subjects.SubjectsStore.Message
import kotlinx.coroutines.launch

class SubjectsExecutor(
    private val adminRepository: AdminRepository,
    private val nSubjectsInterface: NetworkInterface,
    private val updateSubjects: () -> Unit,
    private val cSubjectDialog: CAlertDialogComponent,
    private val cGroupBottomSheet: CBottomSheetComponent
) : CoroutineExecutor<Intent, Unit, State, Message, Label>() {
    override fun executeIntent(intent: Intent, getState: () -> State) {
        when (intent) {
            is Intent.ChangeCDifficult -> dispatch(Message.CDifficultChanged(intent.difficult))
            is Intent.ChangeCName -> dispatch(Message.CNameChanged(intent.name))
            is Intent.ChangeCSubjectText -> dispatch(Message.CSubjectTextChanged(intent.text))
            is Intent.ChangeCTeacherLogin -> dispatch(Message.CTeacherLoginChanged(intent.teacherLogin))
            is Intent.ClickOnSubject -> changeSubjectId(intent.subjectId)
            Intent.CreateGroup -> createGroup(getState())
            Intent.CreateSubject -> createSubject(getState())
        }
    }


    private fun createSubject(state: State) {
        scope.launch {
//            dispatch(GroupsStore.Message.GSubjectListProcessStarted)
            cSubjectDialog.nInterface.nStartLoading()
            try {
                adminRepository.createSubject(state.cSubjectText)

            } catch (e: Throwable) {
                with(cSubjectDialog.nInterface) {
                    nError("Что-то пошло не так =/", onFixErrorClick = {
                        goToNone()
                    })
                }
                println(e)
            }
            try {
                updateSubjects()
            } catch (_: Throwable) {
                cSubjectDialog.nInterface.nError("Что-то пошло не так =/", onFixErrorClick = {
                    updateSubjects()
                })
            }
            cSubjectDialog.fullySuccess()
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
//                dispatch(GroupsStore.Message.GroupCreated(groups))
            } catch (_: Throwable) {
                with(cGroupBottomSheet.nInterface) {
                    nError("Что-то пошло не так =/", onFixErrorClick = {
                        goToNone()
                    })
                }
//                nSubjectsInterface.nError("Что-то пошло не так =/")
            }
            updateGroups(state.chosenSubjectId)
        }

    }

    private suspend fun updateGroups(subjectId: Int) {
        nSubjectsInterface.nStartLoading()
        try {
            val groups = adminRepository.fetchGroups(subjectId).groups
            dispatch(Message.GroupsUpdated(groups))
            nSubjectsInterface.nSuccess()
        } catch (_: Throwable) {
            nSubjectsInterface.nError("Что-то пошло не так =/", onFixErrorClick = {
                scope.launch {
                    updateGroups(subjectId)
                }
            })
        }
    }
}
