package groups

import Person
import admin.groups.Group
import admin.groups.Subject
import admin.groups.forms.Form
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
        val subjects: List<Subject> = listOf(),
        val forms: List<Form> = listOf(),
        val teachers: List<Person> = listOf(),
//        val isInited: Boolean = false,
//        val initError: String = ""
    )

    sealed interface Intent {
        data object ChangeSubjectList : Intent
        data object ChangeFormsList : Intent
        data object InitList : Intent
//        data object TryInitAgain : Intent

        data object ChangeView : Intent
    }

    sealed interface Message {
        data class SubjectListChanged(val subjects: List<Subject>) : Message
        data class FormsListChanged(val forms: List<Form>) : Message
        data class ListInited(
            val subjects: List<Subject>,
            val teachers: List<Person>,
            val forms: List<Form>
        ) : Message

//        data object GSubjectListProcessStarted : Message
//        data object InitErrored : Message
//        data object ClearInitError : Message
        data class ViewChanged(val view: Views) : Message
    }

    sealed interface Label

}
