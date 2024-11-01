package ministry

import JournalRepository
import asValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.cAlertDialog.CAlertDialogComponent
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
    private val nUploadInterfaceName = "ministryComponentNUploadInterface"

    private val nDialogInterfaceName = "ds3MinistryDialogInterfaceName"


    val ds3DialogComponent = CAlertDialogComponent(
        componentContext = childContext(nDialogInterfaceName + "CONTEXT"),
        storeFactory = storeFactory,
        name = nDialogInterfaceName,
        onAcceptClick = {
            onDs3SaveClick()
        }
    )

    private fun onDs3SaveClick() {
        onEvent(
            MinistryStore.Intent.UploadStup(
                reason = "!ds3",
                login = model.value.mvdLogin,
                content = model.value.mvdStups.toString(),
                reportId = model.value.mvdReportId,
                custom = model.value.mvdCustom.ifBlank { null }
            )
        )
    }

    val ds1ListComponent = ListComponent(
        componentContext,
        storeFactory,
        name = "ds1ListComponent",
        onItemClick = {
            onDsClick("!ds1", id = it.id)
        }
    )

    val ds2ListComponent = ListComponent(
        componentContext,
        storeFactory,
        name = "ds2ListComponent",
        onItemClick = {
            onDsClick("!ds2", id = it.id)
        }
    )

    private fun onDsClick(reason: String, id: String) {
        onEvent(MinistryStore.Intent.UploadStup(
            reason = reason,
            login = model.value.mvdLogin,
            content = id,
            reportId = model.value.mvdReportId,
            custom = null
        ))
        ds1ListComponent.onEvent(ListDialogStore.Intent.HideDialog)
        ds2ListComponent.onEvent(ListDialogStore.Intent.HideDialog)
    }


    val nInterface = NetworkInterface(
        childContext(nInterfaceName + "CONTEXT"),
        storeFactory,
        nInterfaceName
    )

    val nUploadInterface = NetworkInterface(
        childContext(nUploadInterfaceName + "CONTEXT"),
        storeFactory,
        nUploadInterfaceName
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
                journalRepository = journalRepository,
                ds1ListComponent = ds1ListComponent,
                ds2ListComponent = ds2ListComponent,
                ds3DialogComponent = ds3DialogComponent,
                nUploadInterface = nUploadInterface
            ).create()
        }


    init {
        onEvent(MinistryStore.Intent.Init)

        ministriesListComponent.onEvent(
            ListDialogStore.Intent.InitList(
            listOf(
                Ministries.MVD to "МВД",
                Ministries.DressCode to "Здравоохранение",
            )
                .map {
                    ListItem(
                        id = it.first,
                        text = it.second
                    )
                }
        ))
        ds1ListComponent.onEvent(
            ListDialogStore.Intent.InitList(
            listOf(
                "+1" to "+1",
                "0" to "±0",
                "-1" to "-1"
            )
                .map {
                    ListItem(
                        id = it.first,
                        text = it.second
                    )
                }
        ))
        ds2ListComponent.onEvent(
            ListDialogStore.Intent.InitList(
            listOf(
                "+1" to "+1",
                "0" to "±0",
                "-1" to "-1",
                "-2" to "-2",
                "-3" to "-3",
            )
                .map {
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