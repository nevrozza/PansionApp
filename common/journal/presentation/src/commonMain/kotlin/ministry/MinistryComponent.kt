package ministry

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.cAlertDialog.CAlertDialogComponent
import components.listDialog.ListComponent
import components.listDialog.ListDialogStore
import components.listDialog.ListItem
import components.networkInterface.NetworkInterface
import decompose.DefaultMVIComponent
import server.Ministries

class MinistryComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    private val output: (Output) -> Unit
) : ComponentContext by componentContext, DefaultMVIComponent<MinistryStore.Intent, MinistryStore.State, MinistryStore.Label> {
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

        val kid = model.value.ministryList.flatMap { it.kids.flatMap { it.value } }
            .firstOrNull { it.login == model.value.mvdLogin }
        if (kid != null) {
            onEvent(
                MinistryStore.Intent.UploadStup(
                    reason = "!ds3",
                    login = model.value.mvdLogin,
                    content = model.value.mvdStups.toString(),
                    reportId = model.value.mvdReportId,
                    custom = model.value.mvdCustom.ifBlank { null },
                    formId = kid.formId
                )
            )
        }
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
        val kid = model.value.ministryList.flatMap { it.kids.flatMap { it.value } }
            .firstOrNull { it.login == model.value.mvdLogin }
        if (kid != null) {
            onEvent(
                MinistryStore.Intent.UploadStup(
                    reason = reason,
                    login = model.value.mvdLogin,
                    content = id,
                    reportId = model.value.mvdReportId,
                    custom = null,
                    formId = kid.formId
                )
            )
            ds1ListComponent.onEvent(ListDialogStore.Intent.HideDialog)
            ds2ListComponent.onEvent(ListDialogStore.Intent.HideDialog)
        }
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

    override val store =
        instanceKeeper.getStore {
            MinistryStoreFactory(
                storeFactory = storeFactory,
                executor =  MinistryExecutor(
                    nInterface = nInterface,
                    ds1ListComponent = ds1ListComponent,
                    ds2ListComponent = ds2ListComponent,
                    ds3DialogComponent = ds3DialogComponent,
                    nUploadInterface = nUploadInterface
                )
            ).create()
        }


    init {
        onEvent(MinistryStore.Intent.Init)

        ministriesListComponent.onEvent(
            ListDialogStore.Intent.InitList(
                listOf(
                    Ministries.MVD to "МВД",
                    Ministries.DRESS_CODE to "Здравоохранение",
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

    fun onOutput(output: Output) {
        output(output)
    }

    sealed class Output {
        data object Back : Output()
    }
}