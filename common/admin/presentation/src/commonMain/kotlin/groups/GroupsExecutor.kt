package groups

import AdminRepository
import admin.groups.forms.Form
import admin.groups.forms.formSort
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import components.listDialog.ListComponent
import components.listDialog.ListDialogStore
import components.listDialog.ListItem
import components.networkInterface.NetworkInterface
import deviceSupport.launchIO
import deviceSupport.withMain
import groups.GroupsStore.Intent
import groups.GroupsStore.Label
import groups.GroupsStore.Message
import groups.GroupsStore.State

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
                )
            )

            Intent.ChangeSubjectList -> updateSubjects()
            Intent.ChangeFormsList -> updateForms()
        }
    }

    private fun updateForms() {
        scope.launchIO {
            nFormsInterface.nStartLoading()
            try {
                val forms = adminRepository.fetchAllForms().forms.formSort()
                withMain {
                    dispatch(Message.FormsListChanged(forms))
                    nFormsInterface.nSuccess()
                    updateFormsList(forms)
                }
            } catch (e: Throwable) {
                nFormsInterface.nError("Что-то пошло не так =/", e, onFixErrorClick = {
                    updateForms()
                })
            }
        }
    }

    private fun updateSubjects() {
        scope.launchIO {
            nSubjectsInterface.nStartLoading()
            try {
                val subjects = adminRepository.fetchAllSubjects().subjects
                withMain {
                    dispatch(Message.SubjectListChanged(subjects))
                    nSubjectsInterface.nSuccess()
                }
            } catch (e: Throwable) {
                nSubjectsInterface.nError("Что-то пошло не так =/", e, onFixErrorClick = {
                    updateSubjects()
                })
            }
        }
    }

    private fun init() {
        nGroupsInterface.nStartLoading()
        scope.launchIO {
            try {
                val teachersA = adminRepository.fetchAllTeachers().teachers
                val subjectsA =  adminRepository.fetchAllSubjects().subjects
                val formsA = adminRepository.fetchAllForms().forms.formSort()
                // async {

//                val subjects = subjectsA.await()
//                val teachers = teachersA.await()
//                val forms = formsA.await()
                withMain {
                    updateMentorsInForms()
                    dispatch(
                        Message.ListInited(
                            subjectsA,
                            teachersA,
                            formsA
                        )
                    )
                    nGroupsInterface.nSuccess()
                    updateFormsList(formsA)
                }
            } catch (e: Throwable) {
                nGroupsInterface.nError("Что-то пошло не так =/", e) {
                        init()
                }
            }

        }
    }

    private fun updateFormsList(forms: List<Form>) {
        formListComponent.onEvent(ListDialogStore.Intent.InitList(emptyList<ListItem>() + ListItem("0", "Никакой") + forms.map {
            ListItem(
                id = it.id.toString(),
                text = "${it.form.classNum}${if (it.form.title.length < 2) "-" else " "}${it.form.title} класс"
            )
        }))
    }
}
