package schedule

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.cAlertDialog.CAlertDialogComponent
import components.listDialog.ListComponent
import components.listDialog.ListDialogStore
import components.mpChose.MpChoseComponent
import components.networkInterface.NetworkInterface
import decompose.DefaultMVIComponent

class ScheduleComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    login: String,
    val isCanBeEdited: Boolean,
    private val output: (Output) -> Unit
) : ComponentContext by componentContext, DefaultMVIComponent<ScheduleStore.Intent, ScheduleStore.State, ScheduleStore.Label> {
    val nInterface = NetworkInterface(
        componentContext,
        storeFactory,
        "scheduleComponentNInterface"
    )
    val mpCreateItem = MpChoseComponent(
        componentContext,
        storeFactory,
        name = "mpChoseCreateScheduleItem"
    )
    val mpEditItem = MpChoseComponent(
        componentContext,
        storeFactory,
        name = "mpChoseEditScheduleItem"
    )


    val chooseConflictDialog = CAlertDialogComponent(
        componentContext,
        storeFactory,
        name = "chooseConflictDialogSCHEDULE",
        onAcceptClick = {}
    )

    val listCreateTeacher = ListComponent(
        componentContext = componentContext,
        storeFactory = storeFactory,
        name = "listCreateTeacher",
        onItemClick = {
            onCreateTeacherClick(it.id)
        }
    )

    private fun onCreateTeacherClick(login: String) {
        val state = model.value
        val key = if (state.isDefault) state.defaultDate.toString() else state.currentDate.second
        if (login !in (state.activeTeachers[key] ?: emptyList())
        ) {
            onEvent(
                ScheduleStore.Intent.CreateTeacher(login)
            )
        }
        listCreateTeacher.onEvent(ListDialogStore.Intent.HideDialog)
    }


    override val store =
        instanceKeeper.getStore {
            ScheduleStoreFactory(
                storeFactory = storeFactory,
                state = ScheduleStore.State(login = login),
                executor = ScheduleExecutor(
                    nInterface = nInterface,
                    mpCreateItem = mpCreateItem,
                    mpEditItem = mpEditItem,
                    listCreateTeacher = listCreateTeacher
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