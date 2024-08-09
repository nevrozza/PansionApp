package root


import FIO
import lessonReport.LessonReportComponent
import ReportData
import SettingsComponent
import achievements.AdminAchievementsComponent
import achievements.HomeAchievementsComponent
import activation.ActivationComponent
import admin.AdminComponent
import allGroupMarks.AllGroupMarksComponent
import cabinets.CabinetsComponent
import calendar.CalendarComponent
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackHandlerOwner
import components.networkInterface.NetworkInterface
import detailedStups.DetailedStupsComponent
import dnevnikRuMarks.DnevnikRuMarksComponent
import groups.GroupsComponent
import kotlinx.coroutines.flow.StateFlow
import login.LoginComponent
import home.HomeComponent
import homeTasks.HomeTasksComponent
import journal.JournalComponent
import kotlinx.serialization.Serializable
import mentoring.MentoringComponent
import parents.AdminParentsComponent
import profile.ProfileComponent
import rating.RatingComponent
import root.store.RootStore
import schedule.ScheduleComponent
import users.UsersComponent


interface   RootComponent : BackHandlerOwner {
    val secondLogin: String?
    val secondAvatarId: Int?
    val secondFIO: FIO?
    val childStack: Value<ChildStack<*, Child>>
//    val state: StateFlow<RootStore.State>

    val model: Value<RootStore.State>

    val checkNInterface: NetworkInterface

    sealed class Child {
        class AuthLogin(val component: LoginComponent) : Child()
        class AuthActivation(val component: ActivationComponent) : Child()
        class MainHome(val homeComponent: HomeComponent, val journalComponent: JournalComponent, val ratingComponent: RatingComponent) :
            Child()

        class HomeSettings(val settingsComponent: SettingsComponent) : Child()
        class MainJournal(
            val homeComponent: HomeComponent,
            val journalComponent: JournalComponent
        ) : Child()

        class MainMentoring(
            val mentoringComponent: MentoringComponent
        ) : Child()

        class SecondView(
            val mentoringComponent: MentoringComponent?,
            val rootComponent: RootComponent
        ) : Child()

        class MainRating(val homeComponent: HomeComponent, val ratingComponent: RatingComponent) :
            Child()

        class MainAdmin(val adminComponent: AdminComponent) : Child()

        //        class AdminMentors(val adminComponent: AdminComponent, val mentorsComponent: MentorsComponent) : Child()
        class AdminUsers(val adminComponent: AdminComponent, val usersComponent: UsersComponent) :
            Child()

        class AdminGroups(
            val adminComponent: AdminComponent,
            val groupsComponent: GroupsComponent
        ) : Child()

        //        class AdminStudents(val adminComponent: AdminComponent, val studentsComponent: StudentsComponent) : Child()
        class LessonReport(
            val lessonReport: LessonReportComponent,
            val journalComponent: JournalComponent
        ) : Child()

        class HomeDnevnikRuMarks(
            val homeComponent: HomeComponent,
            val dnevnikRuMarksComponent: DnevnikRuMarksComponent
        ) : Child()

        class HomeDetailedStups(
            val homeComponent: HomeComponent,
            val detailedStups: DetailedStupsComponent
        ) : Child()

        class HomeAllGroupMarks(
            val journalComponent: JournalComponent,
            val allGroupMarksComponent: AllGroupMarksComponent
        ) : Child()

        class HomeProfile(
            val homeComponent: HomeComponent,
            val profileComponent: ProfileComponent
        ) : Child()

        class HomeTasks(
            val homeComponent: HomeComponent,
            val homeTasksComponent: HomeTasksComponent
        ) : Child()

        class AdminSchedule(
            val scheduleComponent: ScheduleComponent
        ) : Child()

        class AdminCabinets(
            val adminComponent: AdminComponent,
            val cabinetsComponent: CabinetsComponent
        ) : Child()

        class AdminCalendar(
            val adminComponent: AdminComponent,
            val calendarComponent: CalendarComponent
        ) : Child()

        class AdminAchievements(
            val adminComponent: AdminComponent,
            val adminAchievementsComponent: AdminAchievementsComponent
        ) : Child()

        class HomeAchievements(
            val homeComponent: HomeComponent,
            val achievementsComponent: HomeAchievementsComponent
        ) : Child()

        class AdminParents(
            val adminComponent: AdminComponent,
            val parentsComponent: AdminParentsComponent
        ) : Child()

    }

    fun onOutput(output: Output)
    fun onEvent(event: RootStore.Intent)

    fun onBackClicked()

    sealed class Output {
        data object NavigateToHome : Output()
        data object NavigateToJournal : Output()
        data object NavigateToMentoring : Output()
        data object NavigateToAdmin : Output()

        data object NavigateToSchedule : Output()

        data object NavigateToRating : Output()
        data object NavigateToAuth : Output()

    }

    @Serializable
    sealed interface Config {
        @Serializable
        data object AuthLogin : Config

        @Serializable
        data object AuthActivation : Config

        @Serializable
        data object MainHome : Config //val login: String, val fio: FIO, val role: String, val avatarId: Int
        @Serializable
        data object MainMentoring : Config

        @Serializable
        data object HomeSettings : Config

        @Serializable
        data object MainJournal : Config

        @Serializable
        data object MainAdmin : Config

        @Serializable
        data object MainRating : Config

        //        @Serializable
//        data object AdminMentors : Config
        @Serializable
        data object AdminUsers : Config

        @Serializable
        data object AdminGroups : Config

        @Serializable
        data object AdminSchedule : Config

        @Serializable
        data object AdminCalendar : Config
        @Serializable
        data object AdminAchievements : Config

        //        @Serializable
//        data object AdminStudents : Config
        @Serializable
        data class LessonReport(val reportData: ReportData) : Config

        @Serializable
        data class HomeDnevnikRuMarks(val studentLogin: String) : Config

        @Serializable
        data class HomeProfile(val studentLogin: String, val fio: FIO, val avatarId: Int) : Config
        data class HomeAchievements(val studentLogin: String) : Config
        @Serializable
        data class HomeTasks(val studentLogin: String, val avatarId: Int, val name: String) : Config


        @Serializable
        data class HomeDetailedStups(val studentLogin: String, val reason: String) : Config

        @Serializable
        data class HomeAllGroupMarks(
            val groupId: Int,
            val groupName: String,
            val subjectId: Int,
            val subjectName: String,
        ) : Config

        @Serializable
        data object AdminCabinets: Config
        @Serializable
        data object AdminParents: Config

        @Serializable
        data class SecondView(val login: String, val fio: FIO, val avatarId: Int, val config: Config): Config
    }

    companion object {
        const val WEB_PATH_AUTH_LOGIN = "auth/login"
        const val WEB_PATH_AUTH_ACTIVATION = "auth/activation"
        const val WEB_PATH_MAIN_HOME = "main/home"
        const val WEB_PATH_HOME_SETTINGS = "main/home/settings"
        const val WEB_PATH_MAIN_JOURNAL = "main/journal"
        const val WEB_PATH_MAIN_ADMIN = "main/admin"
        const val WEB_PATH_MAIN_RATING = "main/rating"

        //        const val WEB_PATH_ADMIN_MENTORS = "main/admin/teachers"
        const val WEB_PATH_ADMIN_USERS = "main/admin/teachers"
        const val WEB_PATH_ADMIN_GROUPS = "main/admin/students"
        const val WEB_PATH_ADMIN_CABINETS = "main/admin/cabinets"
        const val WEB_PATH_ADMIN_CALENDAR = "main/admin/calendar"
        const val WEB_PATH_ADMIN_SCHEDULE = "main/admin/schedule"

        //        const val WEB_PATH_ADMIN_STUDENTS = "main/admin/students"
        const val WEB_PATH_JOURNAL_LESSON_REPORT = "main/journal/lesson_report"
        const val WEB_PATH_HOME_DNEVNIK_RU_MARKS = "main/home/marks"
        const val WEB_PATH_HOME_DETAILED_STUPS = "main/home/stups"
        const val WEB_PATH_HOME_ALL_GROUP_MARKS = "main/home/allGroupMarks"
        const val WEB_PATH_HOME_PROFILE = "main/home/profile"
        const val WEB_PATH_HOME_TASKS = "main/home/tasks"
    }

    sealed interface RootCategories {
        data object Home : RootCategories
        data object Journal : RootCategories
        data object Mentoring : RootCategories
        data object Admin : RootCategories

        data object Rating : RootCategories
    }

}