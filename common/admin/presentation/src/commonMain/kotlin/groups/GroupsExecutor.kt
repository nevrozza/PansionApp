package groups

import AdminRepository
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import components.listDialog.ListDialogComponent
import components.listDialog.ListDialogStore
import components.listDialog.ListItem
import groups.GroupsStore.Intent
import groups.GroupsStore.Label
import groups.GroupsStore.State
import groups.GroupsStore.Message
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GroupsExecutor(
    private val adminRepository: AdminRepository,
    private val formListDialogComponent: ListDialogComponent
) :
    CoroutineExecutor<Intent, Unit, State, Message, Label>() {
    override fun executeIntent(intent: Intent, getState: () -> State) {
        when (intent) {
            is Intent.ChangeCurrentIndex -> changeSubjectIndex(intent.index)
            is Intent.ChangeCreateGSubjectText -> dispatch(Message.CreateGSubjectTextChanged(intent.text))
            is Intent.ChangeGSubjectDialogShowing -> dispatch(
                Message.GSubjectDialogShowingChanged(
                    intent.isShowing
                )
            )

            is Intent.ChangeGSubjectList -> dispatch(Message.GSubjectListChanged(intent.gSubjects))
            is Intent.InitList -> init()
            Intent.CreateGSubjectError -> dispatch(Message.CreateGSubjectErrored)
            Intent.TryCreateGSubjectAgain -> dispatch(Message.CreateGSubjectAgainTryed)
            Intent.CreateGSubject -> createGSubject(getState())
            Intent.TryInitAgain -> {
                dispatch(Message.ClearInitError)
                init()
            }

            Intent.TryChangeIndexAgain -> {
                dispatch(Message.ClearGroupError)
                changeSubjectIndex(getState().currentGSubjectIndex)
            }

            is Intent.ChangeCDifficult -> dispatch(Message.CDifficultChanged(intent.difficult))
            is Intent.ChangeCTeacherLogin -> dispatch(Message.CMentorLoginChanged(intent.teacherLogin))
            is Intent.ChangeCName -> dispatch(Message.CNameChanged(intent.name))
            is Intent.ChangeCreatingSheetShowing -> dispatch(
                Message.CreatingSheetShowingChanged(
                    intent.isShowing
                )
            )

            Intent.TryCreateAgain -> dispatch(Message.TryCreateAgain)
            Intent.CreateGroup -> createGroup(getState())
            Intent.ChangeView -> dispatch(
                Message.ViewChanged(
                    when (getState().view) {
                        GroupsStore.Views.Subjects -> {
                            GroupsStore.Views.Forms
                        }

                        GroupsStore.Views.Forms -> {
                            GroupsStore.Views.Students
                        }

                        else -> {
                            GroupsStore.Views.Subjects
                        }
                    }
                )
            )

            is Intent.ChangeCurrentClass -> changeCurrentClass(formId = intent.classNum)
            is Intent.ChangeCFormMentorLogin -> dispatch(Message.CFormMentorLoginChanged(intent.mentorLogin))
            is Intent.ChangeCFormName -> dispatch(Message.CFormNameChanged(intent.name))
            is Intent.ChangeCFormShortName -> dispatch(Message.CFormShortNameChanged(intent.shortName))
            is Intent.ChangeCFormNum -> dispatch(Message.CFormNumChanged(intent.num))
            is Intent.ChangeCreatingFormSheetShowing -> dispatch(
                Message.CreatingFormSheetShowingChanged(
                    intent.isShowing
                )
            )

            Intent.CreateForm -> createForm(getState())
            Intent.TryCreateFormAgain -> dispatch(Message.TryCreateFormAgain)
            is Intent.ChangeCurrentFormId -> changeFormId(intent.formId)
            Intent.OpenFormGroupCreatingMenu -> dispatch(Message.FormGroupCreatingMenuOpened)
            is Intent.ChangeCFormGroupSubjectId -> changeCFormGroupSubjectId(intent.subjectId)
            is Intent.ChangeCFormGroupGroupId -> dispatch(Message.CFormGroupGroupIdChanged(intent.groupId))
            Intent.CloseFormGroupCreationMenu -> dispatch(Message.FormGroupCreationMenuClosed)
            Intent.CreateFormGroup -> createFormGroup(getState())
            is Intent.CreateUserForm -> createUserForm(getState(), intent.formId)
            is Intent.ClickOnStudentPlus -> {
                dispatch(Message.StudentPlusClicked(intent.studentLogin))
            } //; formListDialogComponent.onEvent(ListDialogStore.Intent.ShowDialog())
            is Intent.ClickOnStudent -> changeStudent(intent.studentLogin)
        }
    }

    private fun changeCurrentClass(formId: Int) {
        scope.launch {
            dispatch(Message.CurrentClassStartedChanged(formId))
            try {
                val students = adminRepository.fetchStudentsInForm(formId)

                dispatch(Message.CurrentClassChanged(formId, students.students))
            } catch (_: Throwable) {
                println("error!")
                dispatch(Message.FetchingFormStudentsError)
            }
        }
    }

    private fun createFormGroup(state: State) {
        scope.launch {
//            dispatch(Message.CreatingProcessStarted)
            try {
                val groups = adminRepository.createFormGroup(
                    formId = state.currentFormId,
                    subjectId = state.cFormGroupSubjectId,
                    groupId = state.cFormGroupGroupId
                ).groups
                dispatch(Message.FormGroupCreated(groups))
            } catch (_: Throwable) {
                dispatch(Message.CreationError)
            }
        }
    }

    private fun createUserForm(state: State, formId: Int) {
        scope.launch {
            formListDialogComponent.onEvent(ListDialogStore.Intent.StartProcess)
            try {
                val students = adminRepository.createUserForm(
                    login = state.currentStudentPlusLogin,
                    formId = formId,
                    currentFormIdToGetList = state.currentFormTabId
                ).students

                dispatch(Message.UserFormCreated(students))
                with(formListDialogComponent) {
                    onEvent(ListDialogStore.Intent.HideDialog)
                    delay(200)
                    onEvent(ListDialogStore.Intent.StopProcess)
                }
            } catch (_: Throwable) {
                formListDialogComponent.onEvent(ListDialogStore.Intent.CallError("Что-то пошло не так =/"))
            }
        }
    }

    private fun changeCFormGroupSubjectId(subjectId: Int) {
        scope.launch {
            dispatch(Message.CFormGroupSubjectIdChanged(subjectId))
            try {
                val groups = adminRepository.fetchSubjectFormGroups(subjectId).groups
                dispatch(Message.CFormGroupSubjectIdChangedAtAll(subjectId, groups))
            } catch (_: Throwable) {
                println("error!")
            }
        }
    }

    private fun createGroup(state: GroupsStore.State) {
        scope.launch {
            dispatch(Message.CreatingProcessStarted)
            try {
                val groups = adminRepository.createGroup(
                    name = state.cName,
                    mentorLogin = state.cTeacherLogin,
                    subjectId = state.currentGSubjectIndex,
                    difficult = state.cDifficult
                ).groups
                dispatch(Message.GroupCreated(groups))
            } catch (_: Throwable) {
                dispatch(Message.CreationError)
            }
        }
    }

    private fun createForm(state: GroupsStore.State) {
        scope.launch {
            dispatch(Message.CreatingFormProcessStarted)
            try {
                val forms = adminRepository.createForm(
                    name = state.cFormName,
                    mentorLogin = state.cFormMentorLogin,
                    classNum = state.cFormNum.toInt(),
                    shortName = state.cFormShortName
                ).forms
                dispatch(Message.FormCreated(forms))
                formListDialogComponent.onEvent(ListDialogStore.Intent.InitList(forms.map {
                    ListItem(
                        id = it.id,
                        text = "${it.classNum}${if (it.name.length < 2) "-" else " "}${it.name} класс"
                    )
                }))
            } catch (_: Throwable) {
                dispatch(Message.CreationFormError)
            }
        }
    }

    private fun createGSubject(state: State) {
        scope.launch {
            dispatch(Message.GSubjectListProcessStarted)
            try {
                val gSubjects = adminRepository.createGSubject(state.createGSubjectText).gSubjects
                dispatch(Message.GSubjectListChanged(gSubjects))
                changeSubjectIndex(gSubjects.last().id)
            } catch (e: Throwable) {
                dispatch(Message.CreateGSubjectErrored)
                println(e)
            }
        }
    }

    private fun changeSubjectIndex(id: Int) {
        scope.launch {
            try {
                dispatch(Message.GroupsProcessStarted(id))
                val groups = adminRepository.fetchSubjectGroups(id).groups
                dispatch(Message.CurrentIndexChanged(id, groups))
            } catch (e: Throwable) {
                println(e)
                dispatch(Message.ChangeIndexErrored)
            }
        }
    }

    private fun changeFormId(id: Int) {
        if (id == 0) {
            dispatch(Message.CurrentFormIdChanged(0, listOf()))
        } else {
            scope.launch {
                try {
                    dispatch(Message.FormsProcessStarted(id))
                    val groups = adminRepository.fetchFormGroups(id).groups
                    dispatch(Message.CurrentFormIdChanged(id, groups))
                } catch (e: Throwable) {
                    println(e)
                    dispatch(Message.CurrentFormIdChanged(0, listOf()))
                }
            }
        }
    }

    private fun changeStudent(login: String) {

        scope.launch {
            try {
                dispatch(Message.StudentClicked(login))
                val groups = adminRepository.fetchStudentGroups(login).groups
                dispatch(Message.StudentDownloaded(groups))
            } catch (e: Throwable) {
                println(e)
                dispatch(Message.StudentErrored("Что-то пошло не так =/"))
            }
        }

    }

    private fun init() {
        scope.launch {
            try {
                val teachersA = async { adminRepository.fetchAllTeachersForGroups().teachers }
                val studentsInitA = async { adminRepository.fetchStudentsInForm(0).students }
                val mentorsA = async { adminRepository.fetchAllMentorsForGroups().mentors }
                val gSubjectsA = async { adminRepository.fetchAllGSubject().gSubjects }
                val formsA = async { adminRepository.fetchAllForms().forms }


                val gSubjects = gSubjectsA.await()
                val groups = if (gSubjects.isNotEmpty()) {
                    async {

                        adminRepository.fetchSubjectGroups(gSubjects.last().id).groups
                    }.await()
                } else listOf()

                val teachers = teachersA.await()
                val mentors = mentorsA.await()
                val forms = formsA.await()
                val studentsInit = studentsInitA.await()

                dispatch(
                    Message.ListInited(
                        gSubjects,
                        groups,
                        teachers,
                        mentors,
                        forms,
                        studentsInit
                    )
                )
                formListDialogComponent.onEvent(ListDialogStore.Intent.InitList(forms.map {
                    ListItem(
                        id = it.id,
                        text = "${it.classNum}${if (it.name.length < 2) "-" else " "}${it.name} класс"
                    )
                }))


            } catch (e: Throwable) {
                dispatch(Message.InitErrored)
                println(e)
            }

        }
    }
}
