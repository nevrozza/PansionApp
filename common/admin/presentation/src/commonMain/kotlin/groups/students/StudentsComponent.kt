package groups.students

import AdminRepository
import asValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.networkInterface.NetworkInterface
import components.listDialog.ListComponent
import groups.GroupsStore

class StudentsComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    val groupModel: Value<GroupsStore.State>,
    private val adminRepository: AdminRepository
) : ComponentContext by componentContext {
    private val nStudentGroupsInterface = NetworkInterface(
        componentContext,
        storeFactory,
        "studentGroupsNInterface"
    )
    val nStudentsInterface = NetworkInterface(
        componentContext,
        storeFactory,
        "studentsComponentInterface"
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

    private val studentsStore =
        instanceKeeper.getStore {
            StudentsStoreFactory(
                storeFactory = storeFactory,
                adminRepository = adminRepository,
                formsListComponent = formsListComponent,
                nStudentsInterface = nStudentsInterface,
                nStudentGroupsInterface = nStudentGroupsInterface
            ).create()
        }
    val model = studentsStore.asValue()

    fun onEvent(event: StudentsStore.Intent) {
        studentsStore.accept(event)
    }

    init {
        onEvent(StudentsStore.Intent.ClickOnFormTab(0))
    }


}