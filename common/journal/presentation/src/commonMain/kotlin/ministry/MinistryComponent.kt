package ministry

import JournalRepository
import asValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.listDialog.ListComponent
import components.listDialog.ListDialogStore
import components.listDialog.ListItem
import components.networkInterface.NetworkInterface
import di.Inject
import server.Ministries

class MinistryComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    private val output: (Output) -> Unit
) : ComponentContext by componentContext {
    private val nInterfaceName = "ministryComponentNInterface"


    val nInterface = NetworkInterface(
        childContext(nInterfaceName + "CONTEXT"),
        storeFactory,
        nInterfaceName
    )

    private val journalRepository: JournalRepository = Inject.instance()
    val ministriesListComponent = ListComponent(
        componentContext,
        storeFactory,
        name = "MinistriesListComponent",
        onItemClick = {
            onMinistriesClick(it.id)
        }
    )
    private fun onMinistriesClick(id: String) {
        onEvent(MinistryStore.Intent.ChangeMinistry(id))
        ministriesListComponent.onEvent(ListDialogStore.Intent.HideDialog)
    }

    private val ministryStore =
        instanceKeeper.getStore {
            MinistryStoreFactory(
                storeFactory = storeFactory,
                nInterface = nInterface,
                journalRepository = journalRepository
            ).create()
        }


    init {
        onEvent(MinistryStore.Intent.Init)

        ministriesListComponent.onEvent(ListDialogStore.Intent.InitList(
            listOf(
                Ministries.MVD to "МВД",
                   Ministries.Culture to "Культура",
                   Ministries.DressCode to "Здравоохранение",
                   Ministries.Education to "Образование",
                   Ministries.Print to "Печать",
                   Ministries.Social to "Соц опрос",
                   Ministries.Sport to "Спорт").map {
                       ListItem(
                           id = it.first,
                           text = it.second
                       )
                   }
        ))

    }

    val model = ministryStore.asValue()

//    @OptIn(ExperimentalCoroutinesApi::class)
//    val state: StateFlow<UsersStore.State> = usersStore.stateFlow

    fun onEvent(event: MinistryStore.Intent) {
        ministryStore.accept(event)
    }

    fun onOutput(output: Output) {
        output(output)
    }

    sealed class Output {
        data object Back : Output()
    }
}