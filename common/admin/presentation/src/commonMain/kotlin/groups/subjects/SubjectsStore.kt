package groups.subjects

import Person
import admin.groups.Group
import com.arkivanov.mvikotlin.core.store.Store
import groups.subjects.SubjectsStore.Intent
import groups.subjects.SubjectsStore.Label
import groups.subjects.SubjectsStore.State

interface SubjectsStore : Store<Intent, State, Label> {
    data class State(
        val chosenSubjectId: Int = 0,
        val groups: List<Group> = listOf(),
//        val isCreateGSubjectDialogShowing: Boolean = false,

//        val cSubjectError: String = "",
//        val isCreatingGSubjectInProcess: Boolean = false,

        val cSubjectText: String = "",

//        val isGroupInProcess: Boolean = false,
//        val groupError: String = "",
//        val isCreatingGroupSheetShowing: Boolean = false,
//        val isCreatingGroupInProcess: Boolean = false,

        val cError: String = "",
        val cName: String = "",
        val cTeacherLogin: String = "",
        val cDifficult: String = "",
        val students: HashMap<Int, List<Person>> = hashMapOf(),
        val currentGroup: Int = 0
    )

    sealed interface Intent {
        data class FetchStudents(val groupId: Int) : Intent
        data class ClickOnSubject(val subjectId: Int) : Intent

        data class ChangeCSubjectText(val text: String) : Intent

        data object CreateSubject : Intent


        data class ChangeCName(val name: String) : Intent
        data class ChangeCTeacherLogin(val teacherLogin: String) : Intent
        data class ChangeCDifficult(val difficult: String) : Intent

        data object CreateGroup : Intent


//        data class ChangeCreatingSheetShowing(val isShowing: Boolean) : Intent
//        data object TryCreateAgain : GroupsStore.Intent


    }

    sealed interface Message {
        data class CurrentGroupChanged(val currentGroup: Int) : Message
        data class StudentsFetched(val students: HashMap<Int, List<Person>>) : Message
        data class ChosenSubjectChanged(val subjectId: Int) : Message //Subjects

        data class GroupsUpdated(val groups: List<Group>) : Message

        data class CSubjectTextChanged(val text: String) : Message //Subjects
        data class CNameChanged(val name: String) : Message
        data class CTeacherLoginChanged(val teacherLogin: String) : Message
        data class CDifficultChanged(val difficult: String) : Message

//        data object CreateGSubjectAgainTryed : GroupsStore.Message //Subjects

//        data class GroupsProcessStarted(val index: Int) : GroupsStore.Message

//        data class CreatingSheetShowingChanged(val isShowing: Boolean) : GroupsStore.Message
//        data object CreatingProcessStarted : GroupsStore.Message
//        data object CreationError : GroupsStore.Message
//        data object TryCreateAgain : GroupsStore.Message

//        data class GroupCreated(val groups: List<SubjectGroup>) : GroupsStore.Message
    }

    sealed interface Label

}
