package lessonReport

import AuthRepository
import JournalRepository
import ReportData
import asValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import components.cAlertDialog.CAlertDialogComponent
import components.cAlertDialog.CAlertDialogStore
import components.cBottomSheet.CBottomSheetComponent
import components.listDialog.ListComponent
import components.listDialog.ListDialogStore
import components.listDialog.ListItem
import components.networkInterface.NetworkInterface
import di.Inject
import homeTasksDialog.HomeTasksDialogComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import server.getSixTime


class LessonReportComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    private val output: (Output) -> Unit,
    private val reportData: ReportData
) : ComponentContext by componentContext {
    //    private val settingsRepository: SettingsRepository = Inject.instance()
//    private val authRepository: AuthRepository = Inject.instance()

    private val homeTaskDialogContentName = "homeTaskDialogContentName"
    val homeTasksDialogComponent = HomeTasksDialogComponent(
        componentContext = childContext(homeTaskDialogContentName+"CONTEXT"),
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
    private fun onSaveQuitAcceptClick() {
        onEvent(LessonReportStore.Intent.UpdateWholeReport)
        saveQuitNameDialogComponent.onEvent(CAlertDialogStore.Intent.HideDialog)
        onOutput(Output.Back)
    }
    private fun onSaveQuitDeclineClick() {
        saveQuitNameDialogComponent.onEvent(CAlertDialogStore.Intent.HideDialog)
        onOutput(Output.Back)
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
        onEvent(LessonReportStore.Intent.SetMark(mark))
        if (id != "no"
            || state.value.students.first { state.value.selectedLogin == it.login }
                .marksOfCurrentLesson.count { it.reason == state.value.selectedMarkReason } == 4
        ) {
            setMarkMenuComponent.onEvent(ListDialogStore.Intent.HideDialog)
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

    private val lessonReportStore =
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
                nHomeTasksInterface = nHomeTasksInterface
            ).create()
        }

    val setReportColumnsComponent = CBottomSheetComponent(
        componentContext,
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


    val model = lessonReportStore.asValue()

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<LessonReportStore.State> = lessonReportStore.stateFlow

    fun onEvent(event: LessonReportStore.Intent) {
        lessonReportStore.accept(event)
    }

    fun onOutput(output: Output) {
        //ONLY ONE OUTPUT DEPRECATED
//        if (model.value.isUpdateNeeded) {
//            onEvent(LessonReportStore.Intent.UpdateWholeReport)
//        }
        output(output)
    }


    init {
        onEvent(LessonReportStore.Intent.Init)
        setMarkMenuComponent.onEvent(
            ListDialogStore.Intent.InitList(
                listOf(
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
            )
        )

        println(getSixTime())
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
//        setReportColumnsComponent.onEvent(CBottomSheetStore.Intent.ShowSheet)
    }

    sealed class Output {
        //        data object NavigateToMentors : Output()
//        data object NavigateToUsers : Output()
//        data object NavigateToGroups : Output()
//        data object NavigateToStudents : Output()
        data object Back : Output()

    }
}