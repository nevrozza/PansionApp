package lessonReport

import com.arkivanov.mvikotlin.core.store.Store
import homework.CreateReportHomeworkItem
import report.Attended
import report.UserMark

interface LessonReportStore : Store<LessonReportStore.Intent, LessonReportStore.State, LessonReportStore.Label> {
    data class State(
        val lessonReportId: Int,
        val isModer: Boolean,
        val isEditable: Boolean,
        val subjectName: String,
        val subjectId: Int,
        val groupName: String,
        val groupId: Int,
        val teacherName: String,
        val date: String,
        val time: String,
        val editTime: String,
        val topic: String,
        val description: String,
        val likedList: List<String> = emptyList(),
        val dislikedList: List<String> = emptyList(),
        val students: List<StudentLine> = emptyList(),
        val columnNames: List<ReportColumn>,
        val settingsTab : SettingsTab = SettingsTab.SetupTab,
        val deletingReportColumn: ReportColumn? = null,
        val selectedLogin: String = "",
        val selectedMarkReason: String = "",
        val selectedMarkValue: String = "",
        val selectedDeploy: String = "login: Date-Time",
        val isInfoShowing: Boolean = true,
//        val isFabShowing: Boolean = false,
        val status: Boolean,
        val module: Int,
        val ids: Int,
        val isMentorWas: Boolean,
        val detailedMarksLogin: String = "",
        val detailedMarks: List<UserMark> = emptyList(),
        val isSavedAnimation: Boolean = false,
        val isErrorAnimation: Boolean = false,
        val isHomeTasksSavedAnimation: Boolean = false,
        val isHomeTasksErrorAnimation: Boolean = false,

        val isUpdateNeeded: Boolean = false,

        val hometasks: List<CreateReportHomeworkItem> = emptyList(),
        val homeTasksNewTabs: List<List<String>> = emptyList(),
        val homeTasksToEditIds: Set<Int> = emptySet<Int>(),

        val tabLogins: List<String>? = null,
        val newTabLogins: List<String> = listOf(),
//        val deletingColumnReasondId: String = ""

    )



    sealed interface SettingsTab {
        data object MarksTab : SettingsTab
        data object SetupTab : SettingsTab
        data object HomeWorkTab : SettingsTab
    }

    sealed interface Intent {

        data object OnTasksTabAcceptClick : Intent

        data class UpdateTabLoginsId(val tabLogins: List<String>?) : Intent


        data object SaveHomeTasks : Intent


        data class AddEmptyHomeTask(val studentLogins: List<String>?) : Intent
        data class ChangeHomeTaskType(val id: Int, val type: String, val isNew: Boolean) : Intent
        data class ChangeHomeTaskIsNec(val id: Int, val isNec: Boolean, val isNew: Boolean) : Intent
        data class ChangeHomeTaskText(val id: Int, val text: String, val isNew: Boolean) : Intent
        data class ChangeHomeTaskAward(val id: Int, val award: Int, val isNew: Boolean) : Intent

        data object Init: Intent

        data class IsSavedAnimation(val isSaved: Boolean): Intent
        data class IsErrorAnimation(val isError: Boolean): Intent

        data class IsHomeTasksSavedAnimation(val isSaved: Boolean): Intent
        data class IsHomeTasksErrorAnimation(val isError: Boolean): Intent

        data class CreateColumn(val columnName: String, val reasonId: String) : Intent

        data class DeleteColumnInit(val reportColumn: ReportColumn) : Intent

        data object DeleteColumn : Intent

        data class OpenSetMarksMenu(val reasonId: String, val studentLogin: String, val x: Float, val y: Float) :
            Intent

        data object DeleteMark : Intent

        data class ChangeStups(val login: String, val value: Int, val columnReason: String) : Intent

        data class OpenDeleteMarkMenu(val reasonId: String, val studentLogin: String, val markValue: Int, val selectedDeploy: String) :
            Intent

        data class OpenSetLateTimeMenu(val studentLogin: String, val x: Float, val y: Float) :
            Intent
        data object ClearSelection : Intent
        data class SetMark(val mark: String) : Intent
        data class ChangeSettingsTab(val settingsTab: SettingsTab) : Intent

        data class ChangeTopic(val topic: String) : Intent
        data class ChangeDescription(val description: String) : Intent

        data class LikeStudent(val studentLogin: String) : Intent
        data class DislikeStudent(val studentLogin: String) : Intent
        data class SetLateTime(val studentLogin: String, val chosenTime: String) : Intent
        data class ChangeAttendance(val studentLogin: String, val attendedType: String) : Intent

        data object ChangeInfoShowing : Intent
        data object ChangeIsMentorWas: Intent
        data class ChangeStatus(val status: Boolean) : Intent

        data object UpdateWholeReport : Intent

        data class OpenDetailedMarks(val studentLogin: String) : Intent

        data class AddLoginToNewTab(val login: String) : Intent
        data class DeleteLoginFromNewTab(val login: String) : Intent
    }

    sealed interface Message {
        data object InvisibleStupAdd: Message

        data class TabLoginsIdUpdated(val tabLogins: List<String>?) : Message
        data class NewTabsLoginsUpdated(val logins: List<String>) : Message
        data class SaveTabLoginsUpdated(val tabs: List<List<String>>) : Message

        data class HomeTasksUpdated(val homeTasks: List<CreateReportHomeworkItem>) : Message
        data class HomeTasksToEditIdsUpdated(val homeTasksToEditIds: Set<Int>) : Message
        data class IsSavedAnimation(val isSaved: Boolean): Message
        data class IsErrorAnimation(val isError: Boolean): Message

        data class IsHomeTasksSavedAnimation(val isSaved: Boolean): Message
        data class IsHomeTasksErrorAnimation(val isError: Boolean): Message
        data class Inited(val students: List<StudentLine>, val likedList: List<String>, val dislikedList: List<String>):
            Message

//        data class HeaderUpdated(val header: ReportHeader) : Message
        data class ColumnsUpdated(val columns: List<ReportColumn>) : Message
        data class DeleteColumnInited(val reportColumn: ReportColumn) : Message

        data class MarksMenuOpened(val reasonId: String, val studentLogin: String, val markValue: String, val selectedDeploy: String) :
            Message
        data class LateTimeMenuOpened(val studentLogin: String) : Message
//        data class DeleteMarkMenuOpened(val reasonId: String, val studentLogin: String, val markValue: Int) : Message
        data object SelectionCleared : Message
        data class StudentsUpdated(val students: List<StudentLine>) : Message

        data class SettingsTabChanged(val settingsTab: SettingsTab) : Message

        data class TopicChanged(val topic: String) : Message
        data class DescriptionChanged(val description: String) : Message

        data class RepUpdated(val likedList: List<String>, val dislikedList: List<String>) : Message
        data object InfoShowingChanged : Message
        data object IsMentorWasChanged : Message
        data class StatusChanged(val status: Boolean) : Message
        data class EditTimeChanged(val editTime: String) : Message

        data class DetailedMarksOpened(val login: String) : Message
        data class DetailedMarksFetched(val marks: List<UserMark>) : Message

//        data object UpdateNeeded : Message
//        data object NoUpdateNeeded : Message
//        data class isFABShowing(val isShowing: Boolean) : Message
    }

    sealed interface Label

}

data class ReportColumn(
    val title: String,
    val type: String
)

object ColumnTypes {
    const val prisut = "!pr"
    const val srBall = "!sr"
    const val opozdanie = "!la"
}

//sealed interface ColumnTypes {
//    data object prisut : ColumnTypes
//    data object srBall : ColumnTypes
//    data object opozdanie : ColumnTypes
//    data class mark(
////        val type: String,
//        val reasonId: String,
////        val customReason: String? = null
//    ) : ColumnTypes
//}

const val prisut = "Присутствие"
const val srBall = "Балл"
const val opozdanie = "Опоздание"

data class MarkColumn(
    val title: String,
    val reasonId: String
)

data class StudentLine(
//    val fio: FIO,
    val shortFio: String,
    val login: String,

    val attended: Attended?,

    val lateTime: String,

    val avgMark: AvgMark,

    val marksOfCurrentLesson: List<Mark>,

    val stupsOfCurrentLesson: List<Stup>
)

data class Stup(
    val value: Int,
    //val category: String,
    val reason: String,
    val id: Int,
    val deployTime: String,
    val deployLogin: String,
    val deployDate: String
)


data class AvgMark(
    val previousSum: Int,
//    val value: Float,
    val countOfMarks: Int,
)

data class Mark(
    val value: Int,
    val reason: String,
    val isGoToAvg: Boolean,
    val id: Int,
    val date: String,
    val deployTime: String,
    val deployLogin: String,
    val deployDate: String
)
