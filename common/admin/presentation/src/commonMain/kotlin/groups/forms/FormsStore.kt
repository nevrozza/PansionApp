package groups.forms

import Person
import admin.groups.forms.CutedGroup
import admin.groups.forms.FormGroup
import com.arkivanov.mvikotlin.core.store.Store
import groups.forms.FormsStore.Intent
import groups.forms.FormsStore.Label
import groups.forms.FormsStore.State

interface FormsStore : Store<Intent, State, Label> {

    data class State(
        val mentors: List<Person> = listOf(),
        val chosenFormId: Int = 0,
        val formGroups: List<FormGroup> = listOf(),

        val isFormGroupCreatingMenu: Boolean = false,

        val cFormGroupSubjectId: Int = 0,
        val cFormGroupGroupId: Int = 0,
        val cutedGroups: List<CutedGroup> = listOf(),

        val cFormTitle: String = "",
        val cFormShortTitle: String = "",
        val cFormMentorLogin: String = "",
        val cFormClassNum: String = "",

        val eFormId: Int = 0,
        val eFormTitle: String = "",
        val eFormShortTitle: String = "",
        val eFormMentorLogin: String = "",
        val eFormClassNum: String = "",
    )

    sealed interface Intent {

        data class EditFormInit(val formId: Int) : Intent

        data class ChangeEFormTitle(val title: String) : Intent
        data class ChangeEFormShortTitle(val shortTitle: String) : Intent
        data class ChangeEFormMentorLogin(val mentorLogin: String) : Intent
        data class ChangeEFormClassNum(val classNum: String) : Intent

        data object EditForm : Intent

        data class ClickOnForm(val formId: Int) : Intent

        data class ChangeCFormTitle(val title: String) : Intent
        data class ChangeCFormShortTitle(val shortTitle: String) : Intent
        data class ChangeCFormMentorLogin(val mentorLogin: String) : Intent
        data class ChangeCFormClassNum(val classNum: String) : Intent
        data object CreateForm : Intent

//        data class ChangeCreatingFormSheetShowing(val isShowing: Boolean) : GroupsStore.Intent
//        data object TryCreateFormAgain : GroupsStore.Intent


        data object OpenFormGroupCreationMenu : Intent
        data object CloseFormGroupCreationMenu : Intent

        data class ChangeCFormGroupSubjectId(val subjectId: Int) : Intent
        data class ChangeCFormGroupGroupId(val groupId: Int) : Intent
        data object CreateFormGroup : Intent

        data class DeleteFormGroup(val subjectId: Int, val groupId: Int) : Intent

        data object UpdateMentors : Intent
    }

    sealed interface Message {
        data class EditFormInit(val formId: Int) : Message

        data class ChangeEFormTitle(val title: String) : Message
        data class ChangeEFormShortTitle(val shortTitle: String) : Message
        data class ChangeEFormMentorLogin(val mentorLogin: String) : Message
        data class ChangeEFormClassNum(val classNum: String) : Message



        data class ChosenFormIdChanged(val formId: Int) : Message  //, val groups: List<FormGroup>
        data class FormGroupsUpdated(val groups: List<FormGroup>) : Message  //, val groups: List<FormGroup>
//        data class FormsProcessStarted(val formId: Int) : Message

//        data object TryCreateFormAgain : Message
//        data class CreatingFormSheetShowingChanged(val isShowing: Boolean) : Message
//        data object CreatingFormProcessStarted : Message
//        data object CreationFormError : Message
        data class CFormTitleChanged(val title: String) : Message
        data class CFormShortTitleChanged(val shortTitle: String) : Message
        data class CFormMentorLoginChanged(val mentorLogin: String) : Message
        data class CFormClassNumChanged(val classNum: String) : Message
//        data object FormCreated : Message

        data object FormGroupCreatingMenuOpened : Message
        data object FormGroupCreationMenuClosed : Message

        data class CFormGroupSubjectIdChanged(val subjectId: Int) : Message
        data class CFormGroupGroupIdChanged(val groupId: Int) : Message
        data class CFormGroupSubjectIdChangedAtAll(
            val subjectId: Int,
            val cutedGroups: List<CutedGroup>
        ) : Message


        data object FormGroupCreated : Message
        data class MentorsUpdated(val mentors: List<Person>) : Message
//        data object FormGroupCreated : Message
    }

    sealed interface Label

}
