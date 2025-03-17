package studentReportDialog

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.cBottomSheet.CBottomSheetComponent
import decompose.DefaultMVIComponent

class StudentReportComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
) : ComponentContext by componentContext, DefaultMVIComponent<StudentReportDialogStore.Intent, StudentReportDialogStore.State, StudentReportDialogStore.Label> {
    //    private val settingsRepository: SettingsRepository = Inject.instance()
//    private val authRepository: AuthRepository = Inject.instance()

//    val dialogName = "StudentReportDialogInterfaceName"

    val dialog = CBottomSheetComponent(
        componentContext = componentContext,
        storeFactory = storeFactory,
//        name = dialogName
    )

    override val store =
        instanceKeeper.getStore {
            StudentReportDialogStoreFactory(
                storeFactory = storeFactory,
                dialog = dialog
            ).create()
        }
}