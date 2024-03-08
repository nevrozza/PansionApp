package groups

import com.arkivanov.mvikotlin.core.store.Reducer
import groups.GroupsStore.State
import groups.GroupsStore.Message

object GroupsReducer : Reducer<State, Message> {
    override fun State.reduce(msg: Message): State {
        return when (msg) {
            is Message.CurrentIndexChanged -> copy(
                currentGSubjectIndex = msg.index,
                isGroupInProcess = false,
                groups = msg.groups
            )
            is Message.CreateGSubjectTextChanged -> copy(createGSubjectText = msg.text)
            is Message.GSubjectDialogShowingChanged -> copy(isCreateGSubjectDialogShowing = msg.isShowing)
            Message.CreateGSubjectAgainTryed -> copy(
                createGSubjectError = "",
                isCreatingGSubjectInProcess = false
            )

            Message.CreateGSubjectErrored -> copy(createGSubjectError = "Что-то пошло не так =/")
            is Message.GSubjectListChanged -> copy(
                createGSubjectError = "",
                createGSubjectText = "",
                currentGSubjectIndex = if(msg.gSubjects.isNotEmpty()) msg.gSubjects.last().id else 0,
                gSubjects = msg.gSubjects,
                isCreateGSubjectDialogShowing = false,
                isCreatingGSubjectInProcess = false
            )

            is Message.ListInited -> copy(
                isInited = true,
                gSubjects = msg.gSubjects,
                currentGSubjectIndex = if(msg.gSubjects.isNotEmpty()) msg.gSubjects.last().id else 0,
                groups = msg.groups,
                teachers = msg.teachers,
                mentors = msg.mentors,
                forms = msg.forms,
                studentsInForm = msg.studentsInit
            )

            Message.GSubjectListProcessStarted -> copy(
                isCreatingGSubjectInProcess = true
            )

            Message.ClearInitError -> copy(initError = "")
            Message.InitErrored -> copy(initError = "Что-то пошло не так =/")
            is Message.GroupsProcessStarted -> copy(
                currentGSubjectIndex = msg.index,
                isGroupInProcess = true
            )

            Message.ChangeIndexErrored -> copy(
                groupError = "Не удаётся загрузить группы"
            )
            Message.ClearGroupError -> copy(
                groupError = ""
            )

            is Message.CDifficultChanged -> copy(cDifficult = msg.difficult)
            is Message.CMentorLoginChanged -> copy(cTeacherLogin = msg.mentorLogin)
            is Message.CNameChanged -> copy(cName = msg.name)
            Message.CreatingProcessStarted -> copy(isCreatingGroupInProcess = true)
            is Message.CreatingSheetShowingChanged -> copy(isCreatingGroupSheetShowing = msg.isShowing)
            Message.CreationError -> copy(cError = "Что-то пошло не так =/", isCreatingGroupInProcess = false)
            is Message.GroupCreated -> copy(
                groups = msg.groups,
                isCreatingGroupInProcess = false,
                cError = "",
                cName = "",
                cTeacherLogin = "",
                cDifficult = "",
                isCreatingGroupSheetShowing = false
                )
            Message.TryCreateAgain -> copy(cError = "")
            is Message.ViewChanged -> copy(view = msg.view)
            is Message.CurrentClassChanged -> copy(
                currentFormTabId = msg.classNum,
                studentsInForm = msg.students,
                isStudentsInFormInProcess = false
            )
            is Message.CFormMentorLoginChanged -> copy(cFormMentorLogin = msg.mentorLogin)
            is Message.CFormNameChanged -> copy(cFormName = msg.name)
            is Message.CFormShortNameChanged -> copy(cFormShortName = msg.shortName)
            is Message.CFormNumChanged -> copy(cFormNum = msg.num)
            Message.CreatingFormProcessStarted -> copy(isCreatingFormInProcess = true)
            is Message.CreatingFormSheetShowingChanged -> copy(isCreatingFormSheetShowing = msg.isShowing)
            Message.CreationFormError -> copy(cFormError = "Что-то пошло не так =/")
            is Message.FormCreated -> copy(
                forms = msg.forms,
                isCreatingFormInProcess = false,
                cFormError = "",
                cFormName = "",
                cFormShortName = "",
                cFormMentorLogin = "",
                cFormNum = "",
                isCreatingFormSheetShowing = false
            )
            Message.TryCreateFormAgain -> copy(cFormError = "")
            is Message.FormsProcessStarted -> copy(
                currentFormId = msg.formId,
                isFormInProcess = true,
                isFormGroupCreatingMenu = false
            )
            is Message.CurrentFormIdChanged -> copy(
                currentFormId = msg.formId,
                isFormInProcess = false,
                formGroups = msg.groups
            )

            Message.FormGroupCreatingMenuOpened -> copy(isFormGroupCreatingMenu = true, cFormGroupSubjectId = 0,
                    cFormGroupGroupId = 0)
            is Message.CFormGroupSubjectIdChanged -> copy(
                cFormGroupSubjectId = msg.subjectId,
                cFormGroupGroupId = 0
            )

            is Message.CFormGroupGroupIdChanged -> copy(
                cFormGroupGroupId = msg.groupId
            )
            is Message.CFormGroupSubjectIdChangedAtAll -> copy(
                cFormGroupSubjectId = msg.subjectId,
                formGroupsOfNewSubject = msg.formSubjectGroups
            )

            Message.FormGroupCreationMenuClosed -> copy(
                isFormGroupCreatingMenu = false
            )

            is Message.FormGroupCreated -> copy(
                isFormGroupCreatingMenu = false,
                cFormGroupGroupId = 0,
                cFormGroupSubjectId = 0,
                formGroups = msg.groups
            )

            is Message.CurrentClassStartedChanged -> copy(
                isStudentsInFormInProcess = true,
                currentFormTabId = msg.classNum
            )
            Message.FetchingFormStudentsError -> copy(
                isCreatingGSubjectInProcess = false,
                studentsInFormError = "Что-то пошло не так =/"
            )

            is Message.StudentPlusClicked -> copy(
                currentStudentPlusLogin = msg.studentLogin
            )

            is Message.UserFormCreated -> copy(
                studentsInForm = msg.students
            )

            is Message.StudentClicked -> copy(
                studentGroups = listOf(),
                studentError = "",
                studentInProcess = true,
                currentStudentListLogin = msg.studentLogin
            )
            is Message.StudentDownloaded -> copy(
                studentGroups = msg.studentGroups,
                studentInProcess = false
            )
            is Message.StudentErrored -> copy(
                studentInProcess = false,
                studentError = msg.error
            )
            is Message.StudentProcessChanged -> copy(
                studentInProcess = msg.isInProcess
            )
        }
    }
}