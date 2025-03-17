package groups

import AdminRepository
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.listDialog.ListComponent
import components.networkInterface.NetworkInterface
import decompose.DefaultMVIComponent
import decompose.getChildContext
import di.Inject
import groups.forms.FormsComponent
import groups.forms.FormsStore
import groups.students.StudentsComponent
import groups.subjects.SubjectsComponent

class GroupsComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    private val output: (Output) -> Unit
) : ComponentContext by componentContext,
    DefaultMVIComponent<GroupsStore.Intent, GroupsStore.State, GroupsStore.Label> {
    private val adminRepository: AdminRepository = Inject.instance()

    // HARD
    val formsListComponent = ListComponent(
        componentContext,
        storeFactory,
        name = "formListInGroups",
        onItemClick = {
            //onEvent(StudentsStore.Intent.BindStudentToForm(it.id))
        })

    private val nGroupsInterfaceName = "groupsComponentNInterface"
    private val nSubjectsInterfaceName = "subjectsComponentNInterface"
    private val nFormsInterfaceName = "formsComponentNInterface"

    val nGroupsInterface = NetworkInterface(
        getChildContext(nGroupsInterfaceName),
        storeFactory,
        nGroupsInterfaceName
    )

    val nSubjectsInterface = NetworkInterface(
        getChildContext(nSubjectsInterfaceName),
        storeFactory,
        nSubjectsInterfaceName
    )

    val nFormsInterface = NetworkInterface(
        getChildContext(nFormsInterfaceName),
        storeFactory,
        nFormsInterfaceName
    )
    override val store =
        instanceKeeper.getStore {
            GroupsStoreFactory(
                storeFactory = storeFactory,
                GroupsExecutor(
                    adminRepository = adminRepository,
                    formsListComponent = formsListComponent,
                    nGroupsInterface = nGroupsInterface,
                    nSubjectsInterface = nSubjectsInterface,
                    nFormsInterface = nFormsInterface,
                    updateMentorsInForms = { updateMentorsInForms() }
                )
            ).create()
        }

    private fun updateMentorsInForms() {
        formsComponent.onEvent(FormsStore.Intent.UpdateMentors)
    }


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

    fun onOutput(output: Output) {
        output(output)
    }

    sealed class Output {
        data object Back : Output()
    }
}