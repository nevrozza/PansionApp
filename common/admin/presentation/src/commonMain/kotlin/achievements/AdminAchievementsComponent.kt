package achievements

import AdminRepository
import asValue
import cabinets.CabinetsStore
import cabinets.CabinetsStoreFactory
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.cBottomSheet.CBottomSheetComponent
import components.networkInterface.NetworkInterface
import di.Inject

class AdminAchievementsComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    private val output: (Output) -> Unit
) : ComponentContext by componentContext {
    private val nInterfaceName = "adminAchievementsNInterface"
    private val nBSInterfaceName = "adminAchievementsNBSInterface"
    private val bottomSheetComponentName = "adminAchievementsBottomSheetComponent"
    private val hugeBottomSheetComponentName = "adminAchievementsHugeBottomSheetComponent"
    private val editBottomSheetComponentName = "adminAchievementsEditBottomSheetComponent"
    val nInterface = NetworkInterface(
        childContext(nInterfaceName + "CONTEXT"),
        storeFactory,
        nInterfaceName
    )
    val nBSInterface = NetworkInterface(
        childContext(nBSInterfaceName + "CONTEXT"),
        storeFactory,
        nBSInterfaceName
    )

    private val adminRepository: AdminRepository = Inject.instance()

    val bottomSheetComponent = CBottomSheetComponent(
        childContext(bottomSheetComponentName + "CONTEXT"),
        storeFactory = storeFactory,
        name = bottomSheetComponentName
    )
    val hugeBottomSheetComponent = CBottomSheetComponent(
        childContext(hugeBottomSheetComponentName + "CONTEXT"),
        storeFactory = storeFactory,
        name = hugeBottomSheetComponentName
    )
    val editBottomSheetComponent = CBottomSheetComponent(
        childContext(editBottomSheetComponentName + "CONTEXT"),
        storeFactory = storeFactory,
        name = editBottomSheetComponentName
    )

    private val adminAchievementsStore =
        instanceKeeper.getStore {
            AdminAchievementsStoreFactory(
                storeFactory = storeFactory,
                adminRepository = adminRepository,
                nInterface = nInterface,
                bottomSheetComponent = bottomSheetComponent,
                nBSInterface = nBSInterface,
                hugeBottomSheetComponent = hugeBottomSheetComponent,
                editBottomSheetComponent = editBottomSheetComponent
            ).create()
        }


    init {
        onEvent(AdminAchievementsStore.Intent.Init)
    }

    val model = adminAchievementsStore.asValue()

//    @OptIn(ExperimentalCoroutinesApi::class)
//    val state: StateFlow<UsersStore.State> = usersStore.stateFlow

    fun onEvent(event: AdminAchievementsStore.Intent) {
        adminAchievementsStore.accept(event)
    }

    fun onOutput(output: Output) {
        output(output)
    }

    sealed class Output {
        data object Back : Output()
    }
}