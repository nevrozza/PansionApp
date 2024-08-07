package groups

import AdminRepository
import asValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.networkInterface.NetworkInterface
import components.listDialog.ListComponent
import di.Inject
import groups.forms.FormsComponent
import groups.forms.FormsStore
import groups.students.StudentsComponent
import groups.subjects.SubjectsComponent

class GroupsComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    private val output: (Output) -> Unit
) : ComponentContext by componentContext {
    //    private val settingsRepository: SettingsRepository = Inject.instance()
    private val adminRepository: AdminRepository = Inject.instance()
    val nGroupsInterface = NetworkInterface(

        componentContext,
        storeFactory,
        "groupsComponentNInterface"
    )

    // HARD
    val formsListComponent = ListComponent(
        componentContext,
        storeFactory,
        name = "formListInGroups",
        onItemClick = {
            //onEvent(StudentsStore.Intent.BindStudentToForm(it.id))
        })
    val nSubjectsInterface = NetworkInterface(

        componentContext,
        storeFactory,
        "subjectsComponentNInterface"
    )
    val nFormsInterface = NetworkInterface(

        componentContext,
        storeFactory,
        "formsComponentNInterface"
    )
    private val groupsStore =
        instanceKeeper.getStore {
            GroupsStoreFactory(
                storeFactory = storeFactory,
                adminRepository = adminRepository,
                formListComponent = formsListComponent,
                nGroupsInterface = nGroupsInterface,
                nSubjectsInterface = nSubjectsInterface,
                nFormsInterface = nFormsInterface,
                updateMentorsInForms = {updateMentorsInForms()}
                ).create()
        }

    private fun updateMentorsInForms() {
        formsComponent.onEvent(FormsStore.Intent.UpdateMentors)
    }

    val model = groupsStore.asValue()

    val subjectsComponent = SubjectsComponent(
        componentContext,
        storeFactory,
        groupModel = model,
        updateSubjects = {
            onEvent(GroupsStore.Intent.ChangeSubjectList)
        },
        adminRepository = adminRepository,
        nSubjectsInterface = nSubjectsInterface
    )

    val formsComponent = FormsComponent(
        componentContext,
        storeFactory,
        groupModel = model,
        adminRepository = adminRepository,
        updateForms = {
            onEvent(GroupsStore.Intent.ChangeFormsList)
        },
        nFormsInterface = nFormsInterface
    )


    val studentsComponent = StudentsComponent(
        componentContext,
        storeFactory,
        groupModel = model,
        adminRepository = adminRepository
    )



    init {
        onEvent(GroupsStore.Intent.InitList)
    }

//    @OptIn(ExperimentalCoroutinesApi::class)
//    val state: StateFlow<GroupsStore.State> = groupsStore.stateFlow

    fun onEvent(event: GroupsStore.Intent) {
        groupsStore.accept(event)
    }

    fun onOutput(output: Output) {
        output(output)
    }

    sealed class Output {
        data object Back : Output()
    }
}