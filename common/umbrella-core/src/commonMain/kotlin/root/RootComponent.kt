package root


import LessonReportComponent
import activation.ActivationComponent
import admin.AdminComponent
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackHandlerOwner
import groups.GroupsComponent
import kotlinx.coroutines.flow.StateFlow
import login.LoginComponent
import home.HomeComponent
import journal.JournalComponent
import kotlinx.serialization.Serializable
import root.store.RootStore
import users.UsersComponent


interface RootComponent: BackHandlerOwner {
    val childStack: Value<ChildStack<*, Child>>
    val state: StateFlow<RootStore.State>

    val model: Value<RootStore.State>
    sealed class Child {
        class AuthLogin(val component: LoginComponent) : Child()
        class AuthActivation(val component: ActivationComponent) : Child()
        class MainHome(val homeComponent: HomeComponent, val journalComponent: JournalComponent) : Child()
        class MainJournal(val homeComponent: HomeComponent, val journalComponent: JournalComponent) : Child()
        class MainAdmin(val adminComponent: AdminComponent) : Child()
//        class AdminMentors(val adminComponent: AdminComponent, val mentorsComponent: MentorsComponent) : Child()
        class AdminUsers(val adminComponent: AdminComponent, val usersComponent: UsersComponent) : Child()
        class AdminGroups(val adminComponent: AdminComponent, val groupsComponent: GroupsComponent) : Child()
//        class AdminStudents(val adminComponent: AdminComponent, val studentsComponent: StudentsComponent) : Child()
        class LessonReport(val lessonReport: LessonReportComponent, val journalComponent: JournalComponent) : Child()
    }
    fun onOutput(output: Output)

    fun onBackClicked()

    sealed class Output {
        data object NavigateToHome : Output()
        data object NavigateToJournal : Output()
        data object NavigateToAdmin : Output()
    }

    @Serializable
    sealed interface Config {
        @Serializable
        data object AuthLogin : Config
        @Serializable
        data object AuthActivation : Config
        @Serializable
        data object MainHome : Config
        @Serializable
        data object MainJournal : Config
        @Serializable
        data object MainAdmin : Config
//        @Serializable
//        data object AdminMentors : Config
        @Serializable
        data object AdminUsers : Config
        @Serializable
        data object AdminGroups : Config
//        @Serializable
//        data object AdminStudents : Config
        @Serializable
        data class LessonReport(val lessonReportId: Int) : Config
    }

    companion object {
        const val WEB_PATH_AUTH_LOGIN = "auth/login"
        const val WEB_PATH_AUTH_ACTIVATION = "auth/activation"
        const val WEB_PATH_MAIN_HOME = "main/home"
        const val WEB_PATH_MAIN_JOURNAL = "main/journal"
        const val WEB_PATH_MAIN_ADMIN = "main/admin"
//        const val WEB_PATH_ADMIN_MENTORS = "main/admin/teachers"
        const val WEB_PATH_ADMIN_USERS = "main/admin/teachers"
        const val WEB_PATH_ADMIN_GROUPS = "main/admin/students"
//        const val WEB_PATH_ADMIN_STUDENTS = "main/admin/students"
        const val WEB_PATH_JOURNAL_LESSON_REPORT = "main/journal/lesson_report"
    }

    sealed interface RootCategories {
        data object Home : RootCategories
        data object Journal : RootCategories
        data object Admin : RootCategories
    }

}