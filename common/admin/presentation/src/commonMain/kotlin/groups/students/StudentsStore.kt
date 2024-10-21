package groups.students

import Person
import admin.groups.Group
import admin.groups.forms.CutedGroup
import com.arkivanov.mvikotlin.core.store.Store
import groups.GroupsStore
import groups.forms.FormsStore
import groups.students.StudentsStore.Intent
import groups.students.StudentsStore.Label
import groups.students.StudentsStore.State

interface StudentsStore : Store<Intent, State, Label> {
    data class State(
        val chosenFormTabId: Int = 0,

//        val isStudentsInFormInProcess: Boolean = false,
        val studentsInForm: List<Person> = listOf(),
//        val studentsInFormError: String = "",

        val chosenStudentPlusLogin: String = "",
        val chosenStudentLogin: String = "",

        val studentGroups: List<Group> = listOf(),


        val isFormGroupCreatingMenu: Boolean = false,

        val cFormGroupSubjectId: Int = 0,
        val cFormGroupGroupId: Int = 0,
        val cutedGroups: List<CutedGroup> = listOf(),
//        val studentInProcess: Boolean = false,
//        val studentError: String = ""
    )

    sealed interface Intent {
        data class ClickOnFormTab(val formId: Int) : Intent

        data class BindStudentToForm(val formId: Int) : Intent
        data class ClickOnStudentPlus(val studentLogin: String) : Intent
        data class ClickOnStudent(val studentLogin: String) : Intent


        data object OpenFormGroupCreationMenu : Intent
        data object CloseFormGroupCreationMenu : Intent

        data class ChangeCFormGroupSubjectId(val subjectId: Int) : Intent
        data class ChangeCFormGroupGroupId(val groupId: Int) : Intent
        data object CreateFormGroup : Intent


        data class DeleteStudentGroup(val login: String, val subjectId: Int, val groupId: Int, val afterAll: () -> Unit) : Intent
    }

    sealed interface Message {
        data class ChosenFormChanged(val formId: Int) : Message
        data class StudentsUpdated(val students: List<Person>) : Message

//        data class UserFormCreated(val students: List<Person>) : Message
        data class OnStudentPlusClicked(val studentLogin: String) : Message
        data class OnStudentClicked(val studentLogin: String) : Message

        data class StudentGroupsUpdated(val studentGroups: List<Group>) : Message


        data object FormGroupCreatingMenuOpened : Message
        data object FormGroupCreationMenuClosed : Message

        data class CFormGroupSubjectIdChanged(val subjectId: Int) : Message
        data class CFormGroupGroupIdChanged(val groupId: Int) : Message
        data class CFormGroupSubjectIdChangedAtAll(
            val subjectId: Int,
            val cutedGroups: List<CutedGroup>
        ) : Message


        data object FormGroupCreated : Message



//        data class StudentProcessChanged(val isInProcess: Boolean) : Message
//        data class StudentDownloaded(val studentGroups: List<Group>) : Message
//        data class StudentErrored(val error: String) : Message
    }

    sealed interface Label

}
