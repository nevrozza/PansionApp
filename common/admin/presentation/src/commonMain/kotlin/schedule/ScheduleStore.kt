package schedule

import admin.cabinets.CabinetItem
import admin.schedule.ScheduleFormValue
import admin.schedule.ScheduleGroup
import admin.schedule.SchedulePerson
import admin.schedule.ScheduleSubject
import com.arkivanov.mvikotlin.core.store.Store
import schedule.ScheduleStore.Intent
import schedule.ScheduleStore.Label
import schedule.ScheduleStore.State
import server.getCurrentDate
import server.getDates

interface ScheduleStore : Store<Intent, State, Label> {
    data class State(
        val login: String,


        val teachers: List<SchedulePerson> = emptyList(),
        val students: List<SchedulePerson> = emptyList(),
        val subjects: List<ScheduleSubject> = emptyList(),
        val cabinets: List<CabinetItem> = emptyList(),
        val groups: List<ScheduleGroup> = emptyList(),
        val forms: HashMap<Int, ScheduleFormValue> = hashMapOf(),
        val isTeachersView: Boolean = true,
        val activeTeachers: HashMap<String, List<String>> = hashMapOf(), // changed List<Pair<String, List<String>>> to HashMap<String, List<String>>
        val items: HashMap<String, List<ScheduleItem>> = hashMapOf(), // changed List<Pair<String, List<ScheduleItem>>> to HashMap<String, List<ScheduleItem>>
        val solveConflictItems: HashMap<String, MutableMap<Int, List<String>>> = hashMapOf(), // changed List<Pair<String, List<ScheduleItem>>> to HashMap<String, List<ScheduleItem>>
        val ciLogin: String? = null,
        val ciId: Int? = null,
        val ciCabinet: Int = 0,
        val ciTimings: List<ScheduleTiming>? = null,
        val ciTiming: ScheduleTiming? = null,
        val ciPreview: Boolean = false,
        val ciIsPair: Boolean = false,
        val ciFormId: Int? = null,

        val ciSubjectId: Int? = null,
        val ciCustom: String = "",


        val eiState: EditState = EditState.Preview,
        val eiIndex: Int? = null,
        val eiFormId: Int? = null,
        val eiCabinet: Int? = null,
        val eiGroupId: Int? = null,
        val eiTiming: Pair<String, String>? = null,
        val eiNewLogin: String? = null,

        val isEditItemCouldBeSavedWithDeletedLogins: Boolean = true,

        val niFormId: Int? = null,
        val niId: Int? = null,
        val niGroupId: Int? = null,
        val niCustom: String? = null,
        val niTeacherLogin: String? = null,
        val niErrors: List<StudentError> = emptyList(),
        val isNiCreated: Boolean = false,
        val niOnClick: () -> Unit = {},

        val eiCabinetErrorGroupId: Int = 0,
        val eiStudentErrors: List<StudentError> = emptyList(),

        val isDefault: Boolean = false,
        val defaultDate: Int = 1,
        val dates: List<Pair<Int, String>> = getDates(),
        val currentDate: Pair<Int, String> = getCurrentDate(),
        val isSavedAnimation: Boolean = false
    )

    sealed interface Intent {

        data class SolveConflict(val lessonId: Int, val studentLogins: List<String>) : Intent

        data object CopyFromStandart: Intent

        data object Init : Intent
        data class IsSavedAnimation(val isSavedAnimation: Boolean): Intent

        data object ChangeIsTeacherView: Intent


        data object ChangeEditMode : Intent

        data class ChangeDefaultDate(val date: Int) : Intent
        data class ChangeCurrentDate(val date: Pair<Int, String>) : Intent


        data class StartEdit(val index: Int, val formId: Int) : Intent

        data class eiChooseGroup(val groupId: Int) : Intent

        data class eiChangeState(val state: EditState) : Intent

        data class eiChangeTiming(val timing: Pair<String, String>) : Intent
        data class eiChangeCabinet(val cabinet: Int) : Intent
        data class eiChangeLogin(val login: String) : Intent


        data class eiCheck(
            val cabinet: Int,
            val login: String,
            val id: Int,
            val s: Pair<String, String>
        ) : Intent

        data class eiSave(
            val index: Int,
            val cabinet: Int,
            val login: String,
            val id: Int,
            val s: Pair<String, String>
        ) : Intent

        data class eiDelete(val index: Int) : Intent

        data class ciStart(val login: String, val formId: Int? = null) : Intent
        data class ciChooseGroup(val groupId: Int) : Intent

        data class ciChooseTime(val t: ScheduleTiming) : Intent
        data class ciChangeCabinet(val cabinet: Int) : Intent
        data class ciChangeCustom(val custom: String) : Intent
        data class ciChangeSubjectId(val subjectId: Int) : Intent

        data object ciPreview : Intent

        data class ciCreate(val cTiming : ScheduleTiming?) : Intent

        data object ciNullGroupId : Intent
        data object ciFalsePreview : Intent

        data object UpdateCTeacherList : Intent

        data class CreateTeacher(val login: String) : Intent


        data object ciChangeIsPair : Intent

        data object SaveSchedule : Intent

        data class StartConflict(
            val niFormId: Int,
            val niGroupId: Int,
            val niCustom: String,
            val niTeacherLogin: String,
            val niErrors: List<StudentError>,
            val niId: Int,
            val niOnClick: () -> Unit
        ) : Intent
    }

    sealed interface Message {
        data class IsEditItemCouldBeBLABLABLAChanged(val isEditItemCouldBeSavedWithDeletedLogins: Boolean) : Message
        data class SolveConflictItemsUpdated(
            val solveConflictItems: HashMap<String, MutableMap<Int, List<String>>>,
            val niErrors: List<StudentError>?
        ) : Message

        data object NiOnClicked : Message
        data class ConflictStarted(
            val niFormId: Int,
            val niGroupId: Int,
            val niCustom: String,
            val niTeacherLogin: String,
            val niErrors: List<StudentError>,
            val niId: Int,
            val niOnClick: () -> Unit
        ) : Message

        data object ChangeIsTeacherView: Message

        data class IsSavedAnimation(val isSavedAnimation: Boolean): Message

        data class ListUpdated(val list: HashMap<String, List<ScheduleItem>>) : Message

        data object EditModeChanged : Message

        data class DefaultDateChanged(val defaultDate: Int) : Message
        data class CurrentDateChanged(val currentDate: Pair<Int, String>) : Message

        data class Inited(
            val teachers: List<SchedulePerson>,
            val students: List<SchedulePerson>,
            val subjects: List<ScheduleSubject>,
            val groups: List<ScheduleGroup>,
            val cabinets: List<CabinetItem>,
            val forms: HashMap<Int, ScheduleFormValue>
        ) : Message

        data object ciIsPairChanged : Message

        data class EditStarted(val index: Int, val formId: Int) : Message
        data class eiGroupChosed(val groupId: Int) : Message
        data class eiTimingChanged(val timing: Pair<String, String>) : Message
        data class eiStateChanged(val state: EditState) : Message
        data class eiCabinetChanged(val cabinet: Int) : Message
        data class eiLoginChanged(val login: String) : Message
        data class eiErrorsUpdated(
            val cabinetErrorGroupId: Int,
            val studentErrors: List<StudentError>
        ) : Message


        data class ciStarted(val login: String, val cabinet: Int, val formId: Int?) : Message

        data class ciGroupChosed(val groupId: Int) : Message

        data class ciCustomChanged(val custom: String) : Message
        data class ciCabinetChanged(val cabinet: Int) : Message

        data class ciTimeChosed(val t: ScheduleTiming) : Message
        data class ciSubjectIdChanged(val subjectId: Int) : Message

        data object ciPreviewed : Message

        data object ciReset : Message

        data class ciTimingsGot(val timings: List<ScheduleTiming>) : Message

        data object ciGroupIdNulled : Message
        data object ciPreviewFalsed : Message

        data class TeacherCreated(val activeTeachers: HashMap<String, List<String>>) : Message // updated message to use HashMap

        data class TeacherListUpdated(val activeTeachers: HashMap<String, List<String>>) : Message // updated message to use HashMap
        data class ItemsUpdated(val items: List<ScheduleItem>) : Message


    }

    sealed interface Label


    sealed interface EditState {
        data object Preview : EditState
        data object Groups : EditState
        data object Timings : EditState
        data object Swap : EditState
    }
}



