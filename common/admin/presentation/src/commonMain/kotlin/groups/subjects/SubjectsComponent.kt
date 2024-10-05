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



    val inactiveSubjectsDialog = CAlertDialogComponent(
        componentContext,
        storeFactory,
        name = "inactiveSubjectsDialog",
        onAcceptClick = {

        }
    )
    val deleteSubjectDialog = CAlertDialogComponent(
        componentContext,
        storeFactory,
        name = "deleteSubjectDialog",
        onAcceptClick = {
            onEvent(SubjectsStore.Intent.DeleteSubject)
        }
    )
    val editSubjectDialog = CAlertDialogComponent(
        componentContext,
        storeFactory,
        name = "editSubjectDialog",
        onAcceptClick = ::onEditSubjectDialog
    )
    private fun onEditSubjectDialog() {
        println("SADIK: INT")
        onEvent(SubjectsStore.Intent.EditSubject(sameCount = groupModel.value.subjects.filter { it.name == model.value.eSubjectText }.size))
    }

    val cSubjectDialog = CAlertDialogComponent(
        componentContext,
        storeFactory,
        name = "createSubjectDialog",
        onAcceptClick = {
            onEvent(SubjectsStore.Intent.CreateSubject)
        },
        onDeclineClick = { dialogOnDeclineClick() }
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
    val eGroupBottomSheet = CBottomSheetComponent(
        componentContext,
        storeFactory,
        name = "editGroupBottomSheet"
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
                nGroupInterface = nGroupInterface,
                editSubjectDialog = editSubjectDialog,
                deleteSubjectDialog = deleteSubjectDialog,
                eGroupBottomSheet = eGroupBottomSheet
            ).create()
        }
    val model = studentsStore.asValue()

    fun onEvent(event: SubjectsStore.Intent) {
        studentsStore.accept(event)
    }


}