package groups

import admin.GSubject
import admin.SubjectGroup
import admin.AdultForGroup
import admin.Form
import admin.FormGroup
import admin.FormGroupOfSubject
import admin.Student
import com.arkivanov.mvikotlin.core.store.Store
import groups.GroupsStore.Intent
import groups.GroupsStore.Label
import groups.GroupsStore.State

interface GroupsStore : Store<Intent, State, Label> {

    enum class Views {
        Subjects, Forms, Students
    }

    data class State(
        val view: Views = Views.Subjects,
        val gSubjects: List<GSubject> = listOf(),
        val forms: List<Form> = listOf(),
        val formGroups: List<FormGroup> = listOf(),
        val isFormGroupCreatingMenu: Boolean = false,
        val isFormInProcess: Boolean = false,
        val currentFormTabId: Int = 0,
        val currentFormId: Int = 0,
        val teachers: List<AdultForGroup> = listOf(),
        val mentors: List<AdultForGroup> = listOf(),
        val currentGSubjectIndex: Int = 0,
        val isCreateGSubjectDialogShowing: Boolean = false,
        val createGSubjectText: String = "",
        val createGSubjectError: String = "",
        val isCreatingGSubjectInProcess: Boolean = false,
        val isInited: Boolean = false,
        val initError: String = "",
        val groups: List<SubjectGroup> = listOf(),
        val isGroupInProcess: Boolean = false,
        val groupError: String = "",

        val isCreatingGroupSheetShowing: Boolean = false,
        val isCreatingGroupInProcess: Boolean = false,
        val cError: String = "",
        val cName: String = "",
        val cTeacherLogin: String = "",
        val cDifficult: String = "",

        val isCreatingFormSheetShowing: Boolean = false,
        val isCreatingFormInProcess: Boolean = false,
        val cFormError: String = "",
        val cFormName: String = "",
        val cFormShortName: String = "",
        val cFormMentorLogin: String = "",
        val cFormNum: String = "",

        val cFormGroupSubjectId: Int = 0,
        val cFormGroupGroupId: Int = 0,
        val formGroupsOfNewSubject: List<FormGroupOfSubject> = listOf(),

        val isStudentsInFormInProcess: Boolean = false,
        val studentsInForm: List<Student> = listOf(),
        val studentsInFormError: String = "",

        val currentStudentPlusLogin: String = "",
        val currentStudentListLogin: String = "",

        val studentGroups: List<SubjectGroup> = listOf(),
        val studentInProcess: Boolean = false,
        val studentError: String = ""
    )

    sealed interface Intent {
        data class ChangeCurrentIndex(val index: Int) : Intent
        data class ChangeCurrentClass(val classNum: Int) : Intent
        data class ChangeCurrentFormId(val formId: Int) : Intent
        data class ChangeGSubjectDialogShowing(val isShowing: Boolean) : Intent
        data class ChangeCreateGSubjectText(val text: String) : Intent
        data class ChangeGSubjectList(val gSubjects: List<GSubject>) : Intent
        data object CreateGSubjectError : Intent
        data object OpenFormGroupCreatingMenu : Intent
        data object CreateGSubject : Intent
        data object TryCreateGSubjectAgain : Intent
        data object InitList : Intent
        data object TryInitAgain : Intent
        data object TryChangeIndexAgain : Intent

        data object ChangeView : Intent

        data object CreateGroup : Intent
        data class ChangeCreatingSheetShowing(val isShowing: Boolean) : Intent
        data object TryCreateAgain : Intent
        data class ChangeCName(val name: String) : Intent
        data class ChangeCTeacherLogin(val teacherLogin: String) : Intent
        data class ChangeCDifficult(val difficult: String) : Intent

        data object CreateForm : Intent
        data class ChangeCreatingFormSheetShowing(val isShowing: Boolean) : Intent
        data object TryCreateFormAgain : Intent
        data class ChangeCFormName(val name: String) : Intent
        data class ChangeCFormShortName(val shortName: String) : Intent
        data class ChangeCFormMentorLogin(val mentorLogin: String) : Intent
        data class ChangeCFormNum(val num: String) : Intent
        data class ChangeCFormGroupSubjectId(val subjectId: Int) : Intent
        data class ChangeCFormGroupGroupId(val groupId: Int) : Intent
        data object CloseFormGroupCreationMenu : Intent
        data object CreateFormGroup : Intent
        data class CreateUserForm(val formId: Int) : Intent
        data class ClickOnStudentPlus(val studentLogin: String) : Intent
        data class ClickOnStudent(val studentLogin: String) : Intent
    }

    sealed interface Message {
        data class CurrentIndexChanged(val index: Int, val groups: List<SubjectGroup>) : Message
        data class CurrentClassChanged(val classNum: Int, val students: List<Student>) : Message
        data class CurrentClassStartedChanged(val classNum: Int) : Message
        data object FetchingFormStudentsError : Message
        data class CurrentFormIdChanged(val formId: Int, val groups: List<FormGroup>) : Message
        data class FormsProcessStarted(val formId: Int) : Message
        data class GSubjectDialogShowingChanged(val isShowing: Boolean) : Message
        data class CreateGSubjectTextChanged(val text: String) : Message
        data class GSubjectListChanged(val gSubjects: List<GSubject>) : Message
        data object CreateGSubjectErrored : Message
        data object FormGroupCreatingMenuOpened : Message
        data object CreateGSubjectAgainTryed : Message
        data class ListInited(
            val gSubjects: List<GSubject>,
            val groups: List<SubjectGroup>,
            val teachers: List<AdultForGroup>,
            val mentors: List<AdultForGroup>,
            val forms: List<Form>,
            val studentsInit: List<Student>
        ) : Message

        data object GSubjectListProcessStarted : Message
        data object InitErrored : Message
        data object ClearInitError : Message
        data object ClearGroupError : Message
        data object ChangeIndexErrored : Message
        data class ViewChanged(val view: Views) : Message
        data class GroupsProcessStarted(val index: Int) : Message

        data class CreatingSheetShowingChanged(val isShowing: Boolean) : Message
        data object CreatingProcessStarted : Message
        data object CreationError : Message
        data object TryCreateAgain : Message
        data class CNameChanged(val name: String) : Message
        data class CMentorLoginChanged(val mentorLogin: String) : Message
        data class CDifficultChanged(val difficult: String) : Message
        data class GroupCreated(val groups: List<SubjectGroup>) : Message

        data object TryCreateFormAgain : Message
        data class CreatingFormSheetShowingChanged(val isShowing: Boolean) : Message
        data object CreatingFormProcessStarted : Message
        data object CreationFormError : Message
        data class CFormNameChanged(val name: String) : Message
        data class CFormShortNameChanged(val shortName: String) : Message
        data class CFormMentorLoginChanged(val mentorLogin: String) : Message
        data class CFormNumChanged(val num: String) : Message
        data class FormCreated(val forms: List<Form>) : Message
        data class CFormGroupSubjectIdChanged(val subjectId: Int) : Message
        data class CFormGroupGroupIdChanged(val groupId: Int) : Message
        data class CFormGroupSubjectIdChangedAtAll(
            val subjectId: Int,
            val formSubjectGroups: List<FormGroupOfSubject>
        ) : Message

        data object FormGroupCreationMenuClosed : Message
        data class FormGroupCreated(val groups: List<FormGroup>) : Message
        data class UserFormCreated(val students: List<Student>) : Message
        data class StudentPlusClicked(val studentLogin: String) : Message
        data class StudentClicked(val studentLogin: String) : Message
        data class StudentProcessChanged(val isInProcess: Boolean) : Message
        data class StudentDownloaded(val studentGroups: List<SubjectGroup>) : Message
        data class StudentErrored(val error: String) : Message
    }

    sealed interface Label

}
