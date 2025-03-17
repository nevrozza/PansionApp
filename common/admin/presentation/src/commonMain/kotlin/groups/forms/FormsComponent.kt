package groups.forms

import AdminRepository
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.cBottomSheet.CBottomSheetComponent
import components.networkInterface.NetworkInterface
import decompose.DefaultMVIComponent
import decompose.getChildContext
import groups.GroupsStore

class FormsComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,

    val groupModel: Value<GroupsStore.State>,
    private val adminRepository: AdminRepository,
    private val updateForms: () -> Unit,
    val nFormsInterface: NetworkInterface

) : ComponentContext by componentContext,
    DefaultMVIComponent<FormsStore.Intent, FormsStore.State, FormsStore.Label> {

    private val nFormGroupsInterfaceName =
        "FormGroupsNInterface"

    private val creatingFormBottomSheetName = "creatingFormBottomSheet"
    private val editFormBottomSheetName = "editFormBottomSheet"


    private val nFormGroupsInterface = NetworkInterface(
        getChildContext(nFormGroupsInterfaceName),
        storeFactory,
        nFormGroupsInterfaceName
    )

    val creatingFormBottomSheet = CBottomSheetComponent(
        getChildContext(creatingFormBottomSheetName),
        storeFactory,
        name = creatingFormBottomSheetName
    )
    val editFormBottomSheet = CBottomSheetComponent(
        getChildContext(editFormBottomSheetName),
        storeFactory,
        name = editFormBottomSheetName
    )

    val nFormsModel = nFormsInterface.networkModel
    val nFormGroupsModel = nFormGroupsInterface.networkModel


    override val store =
        instanceKeeper.getStore("formsStore") {
            FormsStoreFactory(
                storeFactory = storeFactory,
                executor = FormsExecutor(
                    adminRepository = adminRepository,
                    nFormGroupsInterface = nFormGroupsInterface,
                    creatingFormBottomSheet = creatingFormBottomSheet,
                    updateForms = updateForms,
                    editFormBottomSheet = editFormBottomSheet
                )
            ).create()
        }
}