package groups.subjects

import AdminRepository
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.cAlertDialog.CAlertDialogComponent
import components.cBottomSheet.CBottomSheetComponent
import components.networkInterface.NetworkInterface
import decompose.DefaultMVIComponent
import decompose.getChildContext
import groups.GroupsStore

class SubjectsComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    val groupModel: Value<GroupsStore.State>,
    updateSubjects: () -> Unit,
    private val adminRepository: AdminRepository,
    val nSubjectsInterface: NetworkInterface
) : ComponentContext by componentContext,
    DefaultMVIComponent<SubjectsStore.Intent, SubjectsStore.State, SubjectsStore.Label> {

    private val nGroupInterfaceName = "NSubjectInsGroupInterfaceName"
    private val inactiveSubjectsDialogName = "inactiveSubjectsDialogName"
    private val deleteSubjectDialogName = "deleteSubjectDialogName"
    private val editSubjectDialogName = "editSubjectDialogName"
    private val cSubjectDialogName = "createSubjectDialog"

    private val createGroupBottomSheet = "createGroupBottomSheet"
    private val editGroupBottomSheet = "editGroupBottomSheet"

    private val nGroupInterface = NetworkInterface(
        getChildContext(nGroupInterfaceName), storeFactory,
        name = nGroupInterfaceName
    )

    val inactiveSubjectsDialog = CAlertDialogComponent(
        getChildContext(inactiveSubjectsDialogName),
        storeFactory,
        name = inactiveSubjectsDialogName,
        onAcceptClick = {}
    )

    val deleteSubjectDialog = CAlertDialogComponent(
        getChildContext(deleteSubjectDialogName),
        storeFactory,
        name = deleteSubjectDialogName,
        onAcceptClick = { onEvent(SubjectsStore.Intent.DeleteSubject) }
    )
    val editSubjectDialog = CAlertDialogComponent(
        getChildContext(editSubjectDialogName),
        storeFactory,
        name = editSubjectDialogName,
        onAcceptClick = ::onEditSubjectDialog
    )

    private fun onEditSubjectDialog() {
        onEvent(SubjectsStore.Intent.EditSubject(sameCount = groupModel.value.subjects.filter { it.name == model.value.eSubjectText }.size))
    }

    val cSubjectDialog = CAlertDialogComponent(
        getChildContext(cSubjectDialogName),
        storeFactory,
        name = cSubjectDialogName,
        onAcceptClick = {
            onEvent(SubjectsStore.Intent.CreateSubject)
        },
        onDeclineClick = { dialogOnDeclineClick() }
    )

    private fun dialogOnDeclineClick() {
        onEvent(SubjectsStore.Intent.ChangeCSubjectText(""))
    }

    val cGroupBottomSheet = CBottomSheetComponent(
        getChildContext(createGroupBottomSheet),
        storeFactory,
        name = createGroupBottomSheet
    )
    val eGroupBottomSheet = CBottomSheetComponent(
        getChildContext(editGroupBottomSheet),
        storeFactory,
        name = editGroupBottomSheet
    )


    override val store =
        instanceKeeper.getStore {
            SubjectsStoreFactory(
                storeFactory = storeFactory,
                executor = SubjectsExecutor(
                    adminRepository = adminRepository,
                    nSubjectsInterface = nSubjectsInterface,
                    updateSubjects = { updateSubjects() },
                    cSubjectDialog = cSubjectDialog,
                    cGroupBottomSheet = cGroupBottomSheet,
                    nGroupInterface = nGroupInterface,
                    editSubjectDialog = editSubjectDialog,
                    deleteSubjectDialog = deleteSubjectDialog,
                    eGroupBottomSheet = eGroupBottomSheet
                )
            ).create()
        }


}