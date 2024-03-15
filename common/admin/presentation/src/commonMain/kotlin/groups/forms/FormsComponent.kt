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
        storeFactory
    )


    val creatingFormBottomSheet = CBottomSheetComponent(
        componentContext,
        storeFactory,
        name = "creatingFormBottomSheet"
    )

    val nFormsModel = nFormsInterface.networkModel
    val nFormGroupsModel = nFormGroupsInterface.networkModel
    private val formsStore =
        instanceKeeper.getStore {
            FormsStoreFactory(
                storeFactory = storeFactory,
                nFormGroupsInterface = nFormGroupsInterface,
                adminRepository = adminRepository,
                creatingFormBottomSheet = creatingFormBottomSheet,
                updateForms = updateForms
            ).create()
        }
    val model = formsStore.asValue()

    fun onEvent(event: FormsStore.Intent) {
        formsStore.accept(event)
    }
}