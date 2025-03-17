package lessonReport

import AuthRepository
import JournalRepository
import ReportData
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.cAlertDialog.CAlertDialogComponent
import components.cAlertDialog.CAlertDialogStore
import components.cBottomSheet.CBottomSheetComponent
import components.listDialog.ListComponent
import components.listDialog.ListDialogStore
import components.listDialog.ListItem
import components.networkInterface.NetworkInterface
import decompose.DefaultMVIComponent
import di.Inject
import homeTasksDialog.HomeTasksDialogComponent


class LessonReportComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    private val output: (Output) -> Unit,
    private val reportData: ReportData,
    private val updateListScreen: () -> Unit,
) : ComponentContext by componentContext, DefaultMVIComponent<LessonReportStore.Intent, LessonReportStore.State, LessonReportStore.Label> {


    private val homeTaskDialogContentName = "homeTaskDialogContentName"
    val homeTasksDialogComponent = HomeTasksDialogComponent(
        componentContext = childContext(homeTaskDialogContentName + "CONTEXT"),
        storeFactory = storeFactory,
        groupId = reportData.header.groupId
    )

    val nInterface =
        NetworkInterface(componentContext, storeFactory, "LessonReportComponent")

    private val journalRepository: JournalRepository = Inject.instance()
    private val authRepository: AuthRepository = Inject.instance()

    private val homeTaskName = "NLessonReportHomeTasksInterface"

    val nHomeTasksInterface = NetworkInterface(
        componentContext = childContext(homeTaskName + "CONTEXT"),
        storeFactory = storeFactory,
        name = homeTaskName
    )

    private val homeTasksTabsName = "homeTasksTabDialogComponent"
    val homeTasksTabDialogComponent = CAlertDialogComponent(
        componentContext = childContext(homeTasksTabsName + "CONTEXT"),
        storeFactory = storeFactory,
        name = homeTasksTabsName,
        onAcceptClick = ::onTasksTabAcceptClick
    )
    private val saveQuitName = "saveQuitDialogComponent"
    val saveQuitNameDialogComponent = CAlertDialogComponent(
        componentContext = childContext(saveQuitName + "CONTEXT"),
        storeFactory = storeFactory,
        name = saveQuitName,
        onAcceptClick = ::onSaveQuitAcceptClick,
        onDeclineClick = ::onSaveQuitDeclineClick
    )

    private fun onTasksTabAcceptClick() {
        onEvent(LessonReportStore.Intent.OnTasksTabAcceptClick)
        homeTasksTabDialogComponent.onEvent(CAlertDialogStore.Intent.HideDialog)
    }


    var invokeAfterQuitClick = { onOutput(Output.BackAtAll) }

    private fun onSaveQuitAcceptClick() {
        if (store.state.isUpdateNeeded) {
            onEvent(LessonReportStore.Intent.UpdateWholeReport)
        }
        if (store.state.homeTasksToEditIds.isNotEmpty() || true in store.state.hometasks.map { it.isNew }) {
            onEvent(LessonReportStore.Intent.SaveHomeTasks)
        }
        saveQuitNameDialogComponent.onEvent(CAlertDialogStore.Intent.HideDialog)
        invokeAfterQuitClick()
        invokeAfterQuitClick = { onOutput(Output.BackAtAll) }
    }

    private fun onSaveQuitDeclineClick() {
        saveQuitNameDialogComponent.onEvent(CAlertDialogStore.Intent.HideDialog)
        invokeAfterQuitClick()
        invokeAfterQuitClick = { onOutput(Output.BackAtAll) }
    }

    val marksDialogComponent = CAlertDialogComponent(
        componentContext = componentContext,
        storeFactory = storeFactory,
        name = "detailedStudentMarksDialogComponent",
        {}
    )

    val setMarkMenuComponent = ListComponent(
        componentContext,
        storeFactory,
        name = "SetMarkMenuInReport",
        onItemClick = {
            try {
                onSetMarkMenuItemClick(it.text, it.id)
            } catch (_: Throwable) {
            }
        },
        customOnDismiss = { onEvent(LessonReportStore.Intent.ClearSelection) }
    )
    val setDzMarkMenuComponent = ListComponent(
        componentContext,
        storeFactory,
        name = "SetDzMarkMenuInReport",
        onItemClick = {
            try {
                onSetMarkMenuItemClick(it.text, it.id)
            } catch (_: Throwable) {
            }
        },
        customOnDismiss = { onEvent(LessonReportStore.Intent.ClearSelection) }
    )

    val setLateTimeMenuComponent = ListComponent(
        componentContext,
        storeFactory,
        name = "SetLateTimeMenuInReport",
        onItemClick = {
            try {
//                onSetMarkMenuItemClick(it.text)

                onSetLateTimeMenuItemClick(it.text)
            } catch (_: Throwable) {

            }

        },
        customOnDismiss = {
            onEvent(LessonReportStore.Intent.ClearSelection)
        }
    )

    val deleteMarkMenuComponent = ListComponent(
        componentContext,
        storeFactory,
        name = "DeleteMarkMenuInReport",
        onItemClick = {
            try {
//                onSetMarkMenuItemClick(it.text)
                onDeleteMarkMenuItemClick(it.id.toInt())
            } catch (_: Throwable) {

            }

        },
        customOnDismiss = { onEvent(LessonReportStore.Intent.ClearSelection) }
    )

    private fun onSetMarkMenuItemClick(mark: String, id: String) {
        onEvent(LessonReportStore.Intent.SetMark(id))
        if (mark != "no"
            || store.state.students.first { store.state.selectedLogin == it.login }
                .marksOfCurrentLesson.count { it.reason == store.state.selectedMarkReason } == 4
        ) {
            setMarkMenuComponent.onEvent(ListDialogStore.Intent.HideDialog)
            setDzMarkMenuComponent.onEvent(ListDialogStore.Intent.HideDialog)
        }
    }

    private fun onSetLateTimeMenuItemClick(mark: String) {
        onEvent(LessonReportStore.Intent.SetLateTime(model.value.selectedLogin, mark))

        setLateTimeMenuComponent.onEvent(ListDialogStore.Intent.HideDialog)
    }

    private fun onDeleteMarkMenuItemClick(id: Int) {
        if (id == 5) {
            onEvent(LessonReportStore.Intent.DeleteMark)
            deleteMarkMenuComponent.onEvent(ListDialogStore.Intent.HideDialog)
        }
    }

    override val store =
        instanceKeeper.getStore(key = "lessonReportN${reportData.header.reportId}") {
            LessonReportStoreFactory(
                storeFactory = storeFactory,
                setMarkMenuComponent = setMarkMenuComponent,
                deleteMarkMenuComponent = deleteMarkMenuComponent,
                setLateTimeMenuComponent = setLateTimeMenuComponent,
                nInterface = nInterface,
                journalRepository = journalRepository,
                data = reportData,
                marksDialogComponent = marksDialogComponent,
                authRepository = authRepository,
                nHomeTasksInterface = nHomeTasksInterface,
                setDzMarkMenuComponent = setDzMarkMenuComponent,
                updateListScreen = updateListScreen
            ).create()
        }

    val setReportColumnsComponent = CBottomSheetComponent(
        childContext("SetReportColumnsCONTEXT"),
        storeFactory,
        "SetReportColumns"
    )

    val confirmDeletingColumnDialogComponent = CAlertDialogComponent(
        componentContext,
        storeFactory,
        name = "ConfirmDeletingColumnDialog",
        onAcceptClick = {
            onEvent(LessonReportStore.Intent.DeleteColumn)
        }
    )

    fun onOutput(output: Output) {
        output(output)
    }
    fun outerIsNeedToSave() : Boolean {
        return (store.state.isUpdateNeeded || store.state.homeTasksToEditIds.isNotEmpty() || true in store.state.hometasks.map { it.isNew }) && model.value.isEditable
    }
    fun outerDialogForSaving() {
        saveQuitNameDialogComponent.onEvent(CAlertDialogStore.Intent.ShowDialog)
    }
    init {
        onEvent(LessonReportStore.Intent.Init)
        val marks = listOf(
            ListItem(
                id = "5",
                text = "5"
            ),
            ListItem(
                id = "4",
                text = "4"
            ),
            ListItem(
                id = "3",
                text = "3"
            ),
            ListItem(
                id = "2",
                text = "2"
            ),

        )
        setDzMarkMenuComponent.onEvent(
            ListDialogStore.Intent.InitList(
                marks +ListItem(
                    id = "+2",
                    text = "Д"
                )
            )
        )
        setMarkMenuComponent.onEvent(
            ListDialogStore.Intent.InitList(
                marks
            )
        )

        deleteMarkMenuComponent.onEvent(
            ListDialogStore.Intent.InitList(
                listOf(
                    ListItem(
                        "5",
                        "Удалить"
                    )
                )
            )
        )
        setLateTimeMenuComponent.onEvent(
            ListDialogStore.Intent.InitList(
                listOf(
                    ListItem(id = "1", text = ">2"),
                    ListItem(id = "2", text = ">5"),
                    ListItem(id = "3", text = ">10")
                )
            )
        )
    }

    sealed class Output {
        data object Back : Output()
        data object BackAtAll : Output()

    }
}