package groups.students

import AdminRepository
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.listDialog.ListComponent
import components.networkInterface.NetworkInterface
import decompose.DefaultMVIComponent
import decompose.getChildContext
import groups.GroupsStore

class StudentsComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    val groupModel: Value<GroupsStore.State>,
    private val adminRepository: AdminRepository
) : ComponentContext by componentContext, DefaultMVIComponent<StudentsStore.Intent, StudentsStore.State, StudentsStore.Label> {

    private val nStudentGroupInterfaceName = "studentGroupsNInterface"

    private val nStudentGroupsInterface = NetworkInterface(
        getChildContext(nStudentGroupInterfaceName),
        storeFactory,
        nStudentGroupInterfaceName
    )

    private val nStudentsInterfaceName = "studentsNInterface"

    val nStudentsInterface = NetworkInterface(
        getChildContext(nStudentsInterfaceName),
        storeFactory,
        nStudentsInterfaceName
    )
//
    val nStudentsModel = nStudentsInterface.networkModel
    val nStudentGroupsModel = nStudentGroupsInterface.networkModel

    //HARD
    val formsListComponent = ListComponent(
        componentContext,
        storeFactory,
        name = "formListInGroups",
        onItemClick = {
            onEvent(StudentsStore.Intent.BindStudentToForm(it.id.toInt()))
        })

    override val store =
        instanceKeeper.getStore {
            StudentsStoreFactory(
                storeFactory = storeFactory,
                executor = StudentsExecutor(
                    adminRepository = adminRepository,
                    formsListComponent = formsListComponent,
                    nStudentsInterface = nStudentsInterface,
                    nStudentGroupsInterface = nStudentGroupsInterface
                )
            ).create()
        }



}