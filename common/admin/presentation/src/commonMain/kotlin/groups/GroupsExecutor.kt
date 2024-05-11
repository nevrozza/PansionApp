package groups

import AdminRepository
import admin.groups.forms.Form
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import components.networkInterface.NetworkInterface
import components.listDialog.ListComponent
import components.listDialog.ListDialogStore
import components.listDialog.ListItem
import groups.GroupsStore.Intent
import groups.GroupsStore.Label
import groups.GroupsStore.State
import groups.GroupsStore.Message
import groups.subjects.SubjectsComponent
import groups.subjects.SubjectsStore
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class GroupsExecutor(
    private val adminRepository: AdminRepository,
    private val formListComponent: ListComponent,
    private val nGroupsInterface: NetworkInterface,
    private val nSubjectsInterface: NetworkInterface,
    private val nFormsInterface: NetworkInterface,
    private val updateMentorsInForms: () -> Unit
) :
    CoroutineExecutor<Intent, Unit, State, Message, Label>() {
    override fun executeIntent(intent: Intent) {
        when (intent) {
            is Intent.InitList -> init()

            is Intent.ChangeView -> dispatch(
                Message.ViewChanged(
                    intent.view
//                    when (state().view) {
//                        GroupsStore.Views.Subjects -> {
//                            GroupsStore.Views.Forms
//                        }
//
//                        GroupsStore.Views.Forms -> {
//                            GroupsStore.Views.Students
//                        }
//
//                        else -> {
//                            GroupsStore.Views.Subjects
//                        }
//                    }
                )
            )

            Intent.ChangeSubjectList -> updateSubjects()
            Intent.ChangeFormsList -> updateForms()
        }
    }

    private fun updateForms() {
        scope.launch {
            nFormsInterface.nStartLoading()
            try {
                val forms = adminRepository.fetchAllForms().forms
                println("sad0: $forms")
                dispatch(Message.FormsListChanged(forms))
                nFormsInterface.nSuccess()
                println("sad-1: $forms")
                updateFormsList(forms)
            } catch (_: Throwable) {
                nFormsInterface.nError("Что-то пошло не так =/", onFixErrorClick = {
                    updateForms()
                })
            }
        }
    }

    private fun updateSubjects() {
        scope.launch {
            nSubjectsInterface.nStartLoading()
            try {
                val subjects = adminRepository.fetchAllSubjects().subjects
                dispatch(Message.SubjectListChanged(subjects))
                nSubjectsInterface.nSuccess()
            } catch (_: Throwable) {
                nSubjectsInterface.nError("Что-то пошло не так =/", onFixErrorClick = {
                    updateSubjects()
                })
            }
        }
    }

    private fun init() {
        nGroupsInterface.nStartLoading()
        scope.launch {
            try {
                val teachersA = adminRepository.fetchAllTeachers().teachers
                val subjectsA =  adminRepository.fetchAllSubjects().subjects
                val formsA = adminRepository.fetchAllForms().forms
                // async {

//                val subjects = subjectsA.await()
//                val teachers = teachersA.await()
//                val forms = formsA.await()

                dispatch(
                    Message.ListInited(
                        subjectsA,
                        teachersA,
                        formsA
                    )
                )
//                subjectsComponent.onEvent(SubjectsStore.Intent.ClickOnSubject(subjectsA.first().id))
                updateFormsList(formsA)
            } catch (e: Throwable) {
                nGroupsInterface.nError("Что-то пошло не так =/") {
                        init()
                        updateMentorsInForms()
                }
                println(e)
            }

        }
    }

    private fun updateFormsList(forms: List<Form>) {
        println("sad:$forms")
        formListComponent.onEvent(ListDialogStore.Intent.InitList(forms.map {
            ListItem(
                id = it.id.toString(),
                text = "${it.form.classNum}${if (it.form.title.length < 2) "-" else " "}${it.form.title} класс"
            )
        }))
    }
}
