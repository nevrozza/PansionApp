package groups.forms

import AdminRepository
import asValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.networkInterface.NetworkInterface
import components.cBottomSheet.CBottomSheetComponent
import groups.GroupsStore

class FormsComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    val groupModel: Value<GroupsStore.State>,
    private val adminRepository: AdminRepository,
    private val updateForms: () -> Unit,
    val nFormsInterface: NetworkInterface
) : ComponentContext by componentContext {
    private val nFormGroupsInterface = NetworkInterface(
        componentContext,
        storeFactory,
        "FormGroupsNInterface"
    )


    val creatingFormBottomSheet = CBottomSheetComponent(
        componentContext,
        storeFactory,
        name = "creatingFormBottomSheet"
    )
    val editFormBottomSheet = CBottomSheetComponent(
        componentContext,
        storeFactory,
        name = "editFormBottomSheet"
    )

    val nFormsModel = nFormsInterface.networkModel
    val nFormGroupsModel = nFormGroupsInterface.networkModel
    val formsStore =
        instanceKeeper.getStore("formsStore") {
            FormsStoreFactory(
                storeFactory = storeFactory,
                nFormGroupsInterface = nFormGroupsInterface,
                adminRepository = adminRepository,
                creatingFormBottomSheet = creatingFormBottomSheet,
                updateForms = updateForms,
                editFormBottomSheet = editFormBottomSheet
            ).create()
        }
    val model = formsStore.asValue()

    init {
//        nFormsInterface.nError("пошёл ты") {}
        onEvent(FormsStore.Intent.UpdateMentors)
    }

    fun onEvent(event: FormsStore.Intent) {
        formsStore.accept(event)
    }
}