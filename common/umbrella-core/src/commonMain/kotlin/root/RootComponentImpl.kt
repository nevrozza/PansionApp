package root

import AuthRepository
import LessonReportComponent
import activation.ActivationComponent
import admin.AdminComponent
import asValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.value.Value

import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.popWhile
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.router.stack.replaceAll
import com.arkivanov.decompose.router.stack.webhistory.WebHistoryController
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import di.Inject
import groups.GroupsComponent
import home.HomeComponent
import journal.JournalComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import login.LoginComponent
import mentors.MentorsComponent
import root.RootComponent.Child
import root.RootComponent.Companion.WEB_PATH_ADMIN_GROUPS
import root.RootComponent.Companion.WEB_PATH_ADMIN_MENTORS
import root.RootComponent.Companion.WEB_PATH_ADMIN_STUDENTS
import root.RootComponent.Companion.WEB_PATH_ADMIN_USERS
import root.RootComponent.Companion.WEB_PATH_AUTH_ACTIVATION
import root.RootComponent.Config
import root.RootComponent.Companion.WEB_PATH_AUTH_LOGIN
import root.RootComponent.Companion.WEB_PATH_JOURNAL_LESSON_REPORT
import root.RootComponent.Companion.WEB_PATH_MAIN_ADMIN
import root.RootComponent.Companion.WEB_PATH_MAIN_HOME
import root.RootComponent.Companion.WEB_PATH_MAIN_JOURNAL
import root.RootComponent.RootCategories.Admin
import root.RootComponent.RootCategories.Home
import root.RootComponent.RootCategories.Journal
import root.store.RootStore
import root.store.RootStoreFactory
import students.StudentsComponent
import users.UsersComponent

@ExperimentalDecomposeApi
class RootComponentImpl(
    componentContext: ComponentContext,
    private val storeFactory: StoreFactory,
    deepLink: DeepLink = DeepLink.None,
    private val path: String = "",
    private val webHistoryController: WebHistoryController? = null,
) : RootComponent, ComponentContext by componentContext {

    private val authRepository: AuthRepository = Inject.instance()
    private val rootStore =
        instanceKeeper.getStore {
            RootStoreFactory(
                storeFactory = storeFactory,
                isBottomBarShowing = authRepository.isUserLoggedIn(),
                currentScreen = getFirstScreen(),
                role = authRepository.fetchRole(),
                moderation = authRepository.fetchModeration()
            ).create()
        }

    private fun getFirstScreen(): Config {
        return if (authRepository.isUserLoggedIn()) Config.MainHome else Config.AuthActivation
    }

    override fun onBackClicked() {
        navigation.pop()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val state: StateFlow<RootStore.State> = rootStore.stateFlow
    override val model = rootStore.asValue()
    private val navigation = StackNavigation<Config>()
    private val stack = childStack(
        source = navigation,
        initialStack = {
            getInitialStack(
                webHistoryPaths = webHistoryController?.historyPaths,
                deepLink = deepLink
            )
        },
        serializer = Config.serializer(),
        handleBackButton = true,
        childFactory = ::child
    )
    override val childStack: Value<ChildStack<*, Child>> = stack


    private fun child(config: Config, componentContext: ComponentContext): Child {
        val mainHomeComponent = HomeComponent(
            componentContext = componentContext,
            storeFactory = storeFactory,
            output = ::onHomeOutput
        )

        val mainJournalComponent = JournalComponent(
            componentContext = componentContext,
            storeFactory = storeFactory,
            output = ::onJournalOutput
        )

        val mainAdminComponent = AdminComponent(
            componentContext = componentContext,
            storeFactory = storeFactory,
            output = ::onAdminOutput
        )

        return when (config) {
            is Config.AuthLogin -> {
                Child.AuthLogin(
                    LoginComponent(
                        componentContext = componentContext,
                        storeFactory = storeFactory,
                        output = ::onLoginOutput
                    )
                )
            }

            is Config.AuthActivation -> {
                Child.AuthActivation(
                    ActivationComponent(
                        componentContext = componentContext,
                        storeFactory = storeFactory,
                        output = ::onActivationOutput
                    )
                )
            }

            is Config.MainHome -> {
                Child.MainHome(
                    homeComponent = mainHomeComponent,
                    mainJournalComponent
                )
            }

            is Config.MainJournal -> {
                Child.MainJournal(
                    mainHomeComponent,
                    mainJournalComponent
                )
            }

            is Config.MainAdmin -> {
                Child.MainAdmin(
                    mainAdminComponent
                )
            }

            is Config.AdminMentors -> {
                Child.AdminMentors(
                    adminComponent = mainAdminComponent,
                    mentorsComponent = MentorsComponent(
                        componentContext,
                        storeFactory,
                        output = ::onAdminMentorsOutput
                    )
                )
            }

            is Config.AdminUsers -> {
                Child.AdminUsers(
                    adminComponent = mainAdminComponent,
                    UsersComponent(
                        componentContext = componentContext,
                        storeFactory = storeFactory,
                        output = ::onAdminUsersOutput
                    )
                )
            }

            Config.AdminGroups -> {
                Child.AdminGroups(
                    adminComponent = mainAdminComponent,
                    GroupsComponent(
                        componentContext = componentContext,
                        storeFactory = storeFactory,
                        output = ::onAdminGroupsOutput
                    )
                )
            }

            Config.AdminStudents -> TODO()
            is Config.LessonReport -> {
                Child.LessonReport(
                    lessonReport = LessonReportComponent(
                        componentContext = componentContext,
                        storeFactory = storeFactory,
                        output = ::onLessonReportOutput,
                        lessonReportId = config.lessonReportId
                    ),
                    journalComponent = mainJournalComponent
                )
            }
        }
    }

    private fun onLessonReportOutput(output: LessonReportComponent.Output): Unit =
        when (output) {
            LessonReportComponent.Output.BackToJournal -> if(model.value.currentCategory == RootComponent.RootCategories.Journal) {
                navigateToJournal {
                    navigation.popWhile { topOfStack: Config -> topOfStack !is Config.MainJournal }
                }
            } else {
                navigateToHome {
                    navigation.popWhile { topOfStack: Config -> topOfStack !is Config.MainHome  }
                }
            }
        }
    private fun onAdminStudentsOutput(output: StudentsComponent.Output): Unit =
        when (output) {
            StudentsComponent.Output.BackToAdmin -> navigateToAdmin {
                navigation.popWhile { topOfStack: Config -> topOfStack !is Config.MainAdmin}
            }
        }

    private fun onAdminGroupsOutput(output: GroupsComponent.Output): Unit =
        when (output) {
            GroupsComponent.Output.BackToAdmin -> navigateToAdmin {
                navigation.popWhile { topOfStack: Config -> topOfStack !is Config.MainAdmin}
            }
        }

    private fun onAdminUsersOutput(output: UsersComponent.Output): Unit =
        when (output) {
            UsersComponent.Output.BackToAdmin -> navigateToAdmin {
                navigation.popWhile { topOfStack: Config -> topOfStack !is Config.MainAdmin}
            }
        }

    private fun onJournalOutput(output: JournalComponent.Output): Unit =
        when (output) {
            is JournalComponent.Output.NavigateToLessonReport -> navigateToLessonReport(output.lessonReportId) {
                navigation.bringToFront(it)
            }
        }

    private fun onAdminOutput(output: AdminComponent.Output): Unit =
        when (output) {
            AdminComponent.Output.NavigateToMentors -> navigateToAdminMentors {
                navigation.bringToFront(it)
            }

            AdminComponent.Output.NavigateToUsers -> navigateToAdminUsers {
                navigation.bringToFront(it)
            }

            AdminComponent.Output.NavigateToGroups -> navigateToAdminGroups {
                navigation.bringToFront(it)
            }

            AdminComponent.Output.NavigateToStudents -> navigateToAdminStudents {
                navigation.bringToFront(it)
            }
            else -> {}
        }

    private fun onAdminMentorsOutput(output: MentorsComponent.Output): Unit =
        when (output) {
            else -> {}
        }

    private fun onLoginOutput(output: LoginComponent.Output): Unit =
        when (output) {
            LoginComponent.Output.BackToActivation -> navigation.pop()
            LoginComponent.Output.NavigateToMain -> navigateAfterAuth()
        }

    private fun onActivationOutput(output: ActivationComponent.Output): Unit =
        when (output) {
            ActivationComponent.Output.NavigateToLogin -> navigation.push(Config.AuthLogin)
            ActivationComponent.Output.NavigateToMain -> navigateAfterAuth()
        }

    private fun onHomeOutput(output: HomeComponent.Output): Unit =
        when (output) {
            else -> {}
        }

    override fun onOutput(output: RootComponent.Output): Unit =
        when (output) {

            RootComponent.Output.NavigateToHome -> {
                navigateToHome {
                    navigation.bringToFront(it)
                }
            }

            RootComponent.Output.NavigateToJournal -> {
                navigateToJournal {
                    navigation.bringToFront(it)
                }
            }

            RootComponent.Output.NavigateToAdmin -> {
                navigateToAdmin {
                    navigation.bringToFront(it)
                }
            }
        }




    private fun navigateAfterAuth() {
        navigateToHome {
            navigation.replaceAll(it)
            val authRepository: AuthRepository = Inject.instance()
            rootStore.accept(
                RootStore.Intent.UpdatePermissions(
                    role = authRepository.fetchRole(),
                    moderation = authRepository.fetchModeration()
                )
            )
        }
    }

    private fun navigateToHome(post: (Config) -> Unit) {
        val d = Config.MainHome
        rootStore.accept(RootStore.Intent.BottomBarShowing(true))
        rootStore.accept(RootStore.Intent.ChangeCurrentScreen(Home, d))
        post(d)
    }

    private fun navigateToAdminStudents(post: (Config) -> Unit) {
        val d = Config.AdminStudents
        rootStore.accept(RootStore.Intent.BottomBarShowing(false))
        rootStore.accept(RootStore.Intent.ChangeCurrentScreen(Admin, d))
        post(d)
    }

    private fun navigateToLessonReport(lessonReportId: Int, post: (Config) -> Unit) {
        val d = Config.LessonReport(lessonReportId)
        rootStore.accept(RootStore.Intent.BottomBarShowing(false))
        val currentCategory = if(model.value.currentCategory == Journal) Journal else Home
        rootStore.accept(RootStore.Intent.ChangeCurrentScreen(currentCategory, d))
        post(d)
    }

    private fun navigateToAdminMentors(post: (Config) -> Unit) {
        val d = Config.AdminMentors
        rootStore.accept(RootStore.Intent.BottomBarShowing(false))
        rootStore.accept(RootStore.Intent.ChangeCurrentScreen(Admin, d))
        post(d)
    }

    private fun navigateToAdminUsers(post: (Config) -> Unit) {
        val d = Config.AdminUsers
        rootStore.accept(RootStore.Intent.BottomBarShowing(false))
        rootStore.accept(RootStore.Intent.ChangeCurrentScreen(Admin, d))
        post(d)
    }

    private fun navigateToAdminGroups(post: (Config) -> Unit) {
        val d = Config.AdminGroups
        rootStore.accept(RootStore.Intent.BottomBarShowing(false))
        rootStore.accept(RootStore.Intent.ChangeCurrentScreen(Admin, d))
        post(d)
    }

    private fun navigateToJournal(post: (Config) -> Unit) {
        val d = Config.MainJournal
        rootStore.accept(RootStore.Intent.BottomBarShowing(true))
        rootStore.accept(RootStore.Intent.ChangeCurrentScreen(Journal, d))
        post(d)
    }

    private fun navigateToAdmin(post: (Config) -> Unit) {
        val d = Config.MainAdmin
        rootStore.accept(RootStore.Intent.BottomBarShowing(true))
        rootStore.accept(RootStore.Intent.ChangeCurrentScreen(Admin, d))
        post(d)
    }

    sealed interface DeepLink {
        data object None : DeepLink
        class Web(val path: String) : DeepLink
    }


    init {
//        authRepository.deleteToken()
        println("x1")
        webHistoryController?.attach(
            navigator = navigation,
            stack = stack,
            getPath = ::getPathForConfig,
            getConfiguration = ::getConfigForPath,
            serializer = Config.serializer()
        )
        println("xxx")

        rootStore.accept(RootStore.Intent.HideGreetings())

    }

    private fun getInitialStack(webHistoryPaths: List<String>?, deepLink: DeepLink): List<Config> =
        webHistoryPaths
            ?.takeUnless(List<*>::isEmpty)
            ?.map(::getConfigForPath)
            ?: getInitialStack(deepLink)

    private fun getInitialStack(deepLink: DeepLink): List<Config> =
        when (deepLink) {
            is DeepLink.None -> listOf(getFirstScreen())
            is DeepLink.Web -> listOf(getConfigForPath(deepLink.path))
        }

    private fun getPathForConfig(config: Config): String =
        when (config) {
            Config.AuthLogin -> "/$WEB_PATH_AUTH_LOGIN"
            Config.AuthActivation -> {
                println("gogo") ; "/$WEB_PATH_AUTH_ACTIVATION" }

            Config.MainHome -> "/$WEB_PATH_MAIN_HOME"
            Config.MainJournal -> "/$WEB_PATH_MAIN_JOURNAL"
            Config.MainAdmin -> "/$WEB_PATH_MAIN_ADMIN"

            Config.AdminMentors -> "/$WEB_PATH_ADMIN_MENTORS"
            Config.AdminUsers -> "/$WEB_PATH_ADMIN_USERS"
            Config.AdminGroups -> "/$WEB_PATH_ADMIN_GROUPS"
            Config.AdminStudents -> "/$WEB_PATH_ADMIN_STUDENTS"
            is Config.LessonReport -> "/$WEB_PATH_JOURNAL_LESSON_REPORT/${config.lessonReportId}"
        }

    private fun getConfigForPath(path: String): Config {
        return when (path.removePrefix("/")) {
            WEB_PATH_AUTH_LOGIN -> Config.AuthLogin
            WEB_PATH_AUTH_ACTIVATION -> {println("sad") ; Config.AuthActivation}

            WEB_PATH_MAIN_HOME -> Config.MainHome
            WEB_PATH_MAIN_JOURNAL -> Config.MainJournal
            WEB_PATH_MAIN_ADMIN -> Config.MainAdmin

            WEB_PATH_ADMIN_MENTORS -> Config.AdminMentors
            WEB_PATH_ADMIN_USERS -> Config.AdminUsers
            WEB_PATH_ADMIN_GROUPS -> Config.AdminGroups
            WEB_PATH_ADMIN_STUDENTS -> Config.AdminStudents
            WEB_PATH_JOURNAL_LESSON_REPORT.split("/")[0] -> Config.LessonReport(
                path.removePrefix("/").split("/")[1].toInt()
            )
            else -> Config.AuthActivation
        }
    }

}