package groups.subjects

import AdminRepository
import MainRepository
import asValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.networkInterface.NetworkInterface
import components.cAlertDialog.CAlertDialogComponent
import components.cAlertDialog.CAlertDialogStore
import components.cBottomSheet.CBottomSheetComponent
import di.Inject
import groups.GroupsStore

class SubjectsComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    val groupModel: Value<GroupsStore.State>,
    updateSubjects: () -> Unit,
    private val adminRepository: AdminRepository,
    val nSubjectsInterface: NetworkInterface
) : ComponentContext by componentContext {
//    private val nStudentGroupsInterface = NetworkInterface()
//    val nStudentsInterface = NetworkInterface()
//    //
//    val nStudentsModel = nStudentsInterface.networkModel
//    val nStudentGroupsModel = nStudentGroupsInterface.networkModel

    val mainRepository: MainRepository = Inject.instance()

    val nGroupInterface = NetworkInterface(
        componentContext, storeFactory,
        name = "NSubjectInsGroupInterface"
    )

    val cSubjectDialog = CAlertDialogComponent(
        componentContext,
        storeFactory,
        name = "createSubjectDialog",
        onAcceptClick = {
            onEvent(SubjectsStore.Intent.CreateSubject)
        },
        onDeclineClick = { dialogOnDeclineClick() }

//        {
//            createSubjectDialogOnDeclineClick()
//        }
    )

    fun dialogOnDeclineClick() {
        onEvent(SubjectsStore.Intent.ChangeCSubjectText(""))
    }

//    private fun createSubjectDialogOnDeclineClick() {
//        cSubjectDialog.onEvent(CAlertDialogStore.Intent.HideDialog)
//    }

    val cGroupBottomSheet = CBottomSheetComponent(
        componentContext,
        storeFactory,
        name = "createGroupBottomSheet"
    )
//    val formsListComponent = ListComponent(
//        componentContext,
//        storeFactory,
//        name = "formListInGroups",
//        onItemClick = {
//            onEvent(StudentsStore.Intent.BindStudentToForm(it.id))
//        })


    private val studentsStore =
        instanceKeeper.getStore {
            SubjectsStoreFactory(
                storeFactory = storeFactory,
                adminRepository = adminRepository,
                nSubjectsInterface = nSubjectsInterface,
                updateSubjects = { updateSubjects() },
                cSubjectDialog = cSubjectDialog,
                cGroupBottomSheet = cGroupBottomSheet,
                mainRepository = mainRepository,
                nGroupInterface = nGroupInterface
            ).create()
        }
    val model = studentsStore.asValue()

    fun onEvent(event: SubjectsStore.Intent) {
        studentsStore.accept(event)
    }


}