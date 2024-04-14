package groups.forms

import com.arkivanov.mvikotlin.core.store.Reducer
import groups.forms.FormsStore.State
import groups.forms.FormsStore.Message

object FormsReducer : Reducer<State, Message> {
    override fun State.reduce(msg: Message): State {
        return when (msg) {

            is Message.ChosenFormIdChanged -> copy(formGroups = listOf(), chosenFormId = msg.formId, isFormGroupCreatingMenu = false, cFormGroupGroupId = 0, cFormGroupSubjectId = 0)

            is Message.CFormClassNumChanged -> copy(cFormClassNum = msg.classNum)
            is Message.CFormGroupGroupIdChanged -> copy(cFormGroupGroupId = msg.groupId)
            is Message.CFormGroupSubjectIdChanged -> copy(cFormGroupSubjectId = msg.subjectId)
            is Message.CFormGroupSubjectIdChangedAtAll -> copy(
                cutedGroups = msg.cutedGroups,
                cFormGroupSubjectId = msg.subjectId
            )

            is Message.CFormMentorLoginChanged -> copy(cFormMentorLogin = msg.mentorLogin)
            is Message.CFormShortTitleChanged -> copy(cFormShortTitle = msg.shortTitle)
            is Message.CFormTitleChanged -> copy(cFormTitle = msg.title)
//            Message.FormCreated -> TODO()
//            Message.FormGroupCreated -> TODO()
            Message.FormGroupCreatingMenuOpened -> copy(isFormGroupCreatingMenu = true)
            Message.FormGroupCreationMenuClosed -> copy(isFormGroupCreatingMenu = false)
            is Message.FormGroupsUpdated -> copy(formGroups = msg.groups)
            is Message.MentorsUpdated -> copy(mentors = msg.mentors)
            Message.FormGroupCreated -> copy(
                isFormGroupCreatingMenu = false,
                cFormGroupSubjectId = 0,
                cFormGroupGroupId = 0,
                cutedGroups = listOf()
            )
        }
    }
}