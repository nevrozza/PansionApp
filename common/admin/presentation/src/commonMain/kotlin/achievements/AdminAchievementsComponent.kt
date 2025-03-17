package achievements

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.cBottomSheet.CBottomSheetComponent
import components.networkInterface.NetworkInterface
import decompose.DefaultMVIComponent
import decompose.getChildContext

class AdminAchievementsComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    private val output: (Output) -> Unit
) : ComponentContext by componentContext, DefaultMVIComponent<AdminAchievementsStore.Intent, AdminAchievementsStore.State, AdminAchievementsStore.Label> {
    private val nInterfaceName = "adminAchievementsNInterface"
    private val nBSInterfaceName = "adminAchievementsNBSInterface"
    private val bottomSheetComponentName = "adminAchievementsBottomSheetComponent"
    private val hugeBottomSheetComponentName = "adminAchievementsHugeBottomSheetComponent"
    private val editBottomSheetComponentName = "adminAchievementsEditBottomSheetComponent"
    val nInterface = NetworkInterface(
        getChildContext(nInterfaceName),
        storeFactory,
        nInterfaceName
    )
    val nBSInterface = NetworkInterface(
        getChildContext(nBSInterfaceName),
        storeFactory,
        nBSInterfaceName
    )

    val bottomSheetComponent = CBottomSheetComponent(
        getChildContext(bottomSheetComponentName),
        storeFactory = storeFactory,
        name = bottomSheetComponentName
    )
    val hugeBottomSheetComponent = CBottomSheetComponent(
        getChildContext(hugeBottomSheetComponentName),
        storeFactory = storeFactory,
        name = hugeBottomSheetComponentName
    )
    val editBottomSheetComponent = CBottomSheetComponent(
        getChildContext(editBottomSheetComponentName),
        storeFactory = storeFactory,
        name = editBottomSheetComponentName
    )

    override val store =
        instanceKeeper.getStore {
            AdminAchievementsStoreFactory(
                storeFactory = storeFactory,
                executor = AdminAchievementsExecutor(
                    nInterface = nInterface,
                    bottomSheetComponent = bottomSheetComponent,
                    hugeBottomSheetComponent = hugeBottomSheetComponent,
                    editBottomSheetComponent = editBottomSheetComponent,
                    nBSInterface = nBSInterface
                )
            ).create()
        }


    fun onOutput(output: Output) {
        output(output)
    }

    sealed class Output {
        data object Back : Output()
    }
}