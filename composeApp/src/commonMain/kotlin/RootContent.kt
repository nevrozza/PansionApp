@file:OptIn(ExperimentalSplitPaneApi::class)

//import root.RootComponent.Child.AdminMentors
import admin.AdminComponent
import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import animations.iosSlide
import animations.slideEnterModifier
import animations.slideExitModifier
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.materialPredictiveBackAnimatable
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.predictiveBackAnimatable
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.predictiveBackAnimation
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.essenty.backhandler.BackEvent
import components.CustomTextButton
import components.GetAsyncIcon
import components.hazeHeader
import components.hazeUnder
import components.networkInterface.NetworkState
import dev.chrisbanes.haze.LocalHazeStyle
import dev.chrisbanes.haze.hazeChild
import forks.splitPane.ExperimentalSplitPaneApi
import forks.splitPane.HorizontalSplitPane
import forks.splitPane.dSplitter
import groups.GroupsContent
import home.HomeStore
import journal.JournalComponent
import journal.JournalStore
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import mentoring.MentoringComponent
import mentoring.MentoringContent
import resources.RIcons
import root.RootComponent
import root.RootComponent.Child
import root.RootComponent.Child.*
import root.RootComponent.Config
import root.RootComponent.RootCategories.*
import root.store.QuickRoutings
import root.store.RootStore
import school.SchoolComponent
import server.Moderation
import server.Roles
import server.cut
import server.getDate
import view.*

@ExperimentalAnimationApi
@OptIn(
    ExperimentalLayoutApi::class, ExperimentalDecomposeApi::class,
    ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class
)
@ExperimentalFoundationApi
@Composable
fun RootContent(component: RootComponent, isJs: Boolean = false) {
    val viewManager = LocalViewManager.current
    val childStack by component.childStack.subscribeAsState()
    val stack by component.childStack.subscribeAsState()
    val isNewVersionDialogShowing = remember { mutableStateOf(true) }
    val model by component.model.subscribeAsState()
    val nCheckModel by component.checkNInterface.networkModel.subscribeAsState()


    LaunchedEffect(model.startRouting) {
        if (model.startRouting != null) {
            if (model.startRouting !in listOf(QuickRoutings.HomeAllGroupMarks, QuickRoutings.LessonReport)) {
                val login = component.urlArgs["login"]!!
                val config = if (model.startUser != null) {
                    val user = model.startUser!!
                    if (user.fio != null) {
                        when (model.startRouting) {
                            QuickRoutings.HomeAchievements -> Config.HomeAchievements(
                                studentLogin = login,
                                avatarId = user.avatarId,
                                name = user.fio!!.name
                            )

                            QuickRoutings.SecondView -> {
                                val isMentoring = component.urlArgs["isM"]?.toBoolean() ?: false
                                Config.SecondView(
                                    login = login,
                                    fio = user.fio!!,
                                    avatarId = user.avatarId,
                                    config = Config.MainHome,
                                    isMentoring = isMentoring
                                )
                            }

                            null -> null
                            QuickRoutings.HomeAllGroupMarks -> null
                            QuickRoutings.HomeDetailedStups -> {
                                val reason = component.urlArgs["reason"] ?: "0"
                                Config.HomeDetailedStups(
                                    studentLogin = login,
                                    name = user.fio!!.name,
                                    avatarId = user.avatarId,
                                    reason = reason
                                )
                            }

                            QuickRoutings.HomeDnevnikRuMarks -> Config.HomeDnevnikRuMarks(login)
                            QuickRoutings.HomeProfile -> {
                                val deviceLogin = component.authRepository.fetchLogin()
                                Config.HomeProfile(
                                    studentLogin = login,
                                    fio = user.fio!!,
                                    avatarId = user.avatarId,
                                    isOwner = deviceLogin == login,
                                    isCanEdit = deviceLogin == login
                                )
                            }

                            QuickRoutings.HomeStudentLines -> Config.HomeStudentLines(login)
                            QuickRoutings.HomeTasks -> Config.HomeTasks(
                                studentLogin = login,
                                avatarId = user.avatarId,
                                name = user.fio!!.name
                            )

                            QuickRoutings.LessonReport -> null
                        }
                    } else {
                        Config.ErrorScreen(
                            reason = "Пользователь не найден",
                            path = component.wholePath
                        )
                    }
                } else {
                    Config.ErrorScreen(
                        reason = "Произошла ошибка",
                        path = component.wholePath
                    )
                }
                component.onEvent(RootStore.Intent.DeleteStart)
                component.startOutput(config!!)
            } else if (model.startRouting is QuickRoutings.HomeAllGroupMarks) {
                val groupId = component.urlArgs["groupId"]!!.toIntOrNull() ?: 0
                val config = if (model.startGroup != null) {
                    val group = model.startGroup!!
                    if (group.subjectId != null) {
                        Config.HomeAllGroupMarks(
                            groupId = groupId,
                            groupName = group.groupName,
                            subjectId = group.subjectId!!,
                            subjectName = group.subjectName,
                            teacherLogin = group.teacherLogin
                        )
                    } else {
                        Config.ErrorScreen(
                            reason = "Группа не найдена",
                            path = component.wholePath
                        )
                    }
                } else {
                    Config.ErrorScreen(
                        reason = "Произошла ошибка",
                        path = component.wholePath
                    )
                }
                component.onEvent(RootStore.Intent.DeleteStart)
                component.startOutput(config)
            } else if (model.startRouting is QuickRoutings.LessonReport) {
//                val reportId = component.urlArgs["id"]!!.toIntOrNull() ?: 0
                val config = if (model.startReport != null) {
                    val report = model.startReport!!

                    Config.LessonReport(report)
//                    if (report.subjectId != null) {
//                    } else {
//                        Config.ErrorScreen(
//                            reason = "Группа не найдена",
//                            path = component.wholePath
//                        )
//                    }
                } else {
                    Config.ErrorScreen(
                        reason = "Произошла ошибка\nВозможно, введён неправильный id",
                        path = component.wholePath
                    )
                }
                component.onEvent(RootStore.Intent.DeleteStart)
                component.startOutput(config)
            }
        }
    }



    BoxWithConstraints {
        val isExpanded =
            WindowCalculator.calculateScreen(
                size = DpSize(
                    this.maxWidth,
                    this.maxHeight
                )
            ) == WindowScreen.Expanded
        val isVertical =
            WindowCalculator.calculateScreen(
                size = DpSize(
                    this.maxWidth,
                    this.maxHeight
                )
            ) == WindowScreen.Vertical
        val items = listOf<NavigationItem?>(
            NavigationItem(
                iconPath = RIcons.Home,
                label = "Главная",
                category = if (isExpanded && getCategory(childStack.active.configuration as Config) == Journal) Journal
                else Home,
                onClickOutput = RootComponent.Output.NavigateToHome
            ),
            if (!isExpanded && (model.role == Roles.teacher || model.moderation in listOf(
                    Moderation.moderator,
                    Moderation.mentor,
                    Moderation.both
                )) && component.isMentoring == null
            ) NavigationItem(
                iconPath = RIcons.Book,//Icons.AutoMirrored.Rounded.LibraryBooks,
                label = "Журнал",
                size = 20.dp,
                category = Journal,
                onClickOutput = RootComponent.Output.NavigateToJournal
            ) else null,
            NavigationItem(
                iconPath = RIcons.School,//Icons.Rounded.Token,
                size = 24.dp,
                label = "Пансион",
                category = School,
                onClickOutput = RootComponent.Output.NavigateToSchool
            ),
            if (model.moderation != Moderation.nothing
                && component.isMentoring == null
            ) NavigationItem(
                iconPath = RIcons.Group,//Icons.Rounded.Diversity1,
                        label = "Ученики",
                size = 24.dp,
                category = Mentoring,
                onClickOutput = RootComponent.Output.NavigateToMentoring
            ) else null,
            if (model.moderation in listOf(
                    Moderation.moderator,
                    Moderation.both
                ) && component.isMentoring == null
            ) NavigationItem(
                iconPath = RIcons.SovietSettings, //Icons.Rounded.GridView,
                        label = "Админ",
                category = Admin,
                size = 20.dp,
                onClickOutput = RootComponent.Output.NavigateToAdmin
            ) else null,
        )
//    var bottomBarAnimationScope: AnimatedVisibilityScope? = null

        Scaffold(
            Modifier.fillMaxSize(),
            bottomBar = {
                //sber
                Box(Modifier.animateContentSize().fillMaxWidth()) {
                    AnimatedVisibility(

                        visible = isBottomBarShowing(childStack.active.configuration as Config) &&
                                ((isVertical && (component.secondLogin == null))), //was ((isVertical && (component.secondLogin == null)) || component.isMentoring == false)
                        enter = fadeIn(animationSpec = tween(300)) +
                                slideInVertically { it },
                        exit = fadeOut(animationSpec = tween(300)) + slideOutVertically { it },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CustomNavigationBar(
                            viewManager, component, model, childStack, items
                        )
//                    bottomBarAnimationScope = this
                    }
                }
            }
        ) { padding ->

            val aniPadding by animateDpAsState(
                if ((!isVertical) && childStack.active.configuration !is Config.AuthLogin && childStack.active.configuration !is Config.AuthActivation
                ) 80.dp else 0.dp
            )
            SharedTransitionLayout(
                modifier = Modifier.fillMaxSize().then(
                    if (component.secondLogin == null) Modifier.hazeUnder(
                        viewManager,
                        GlobalHazeState.current
                    ) else Modifier
                ).padding(
                    top = 0.dp,
                    start = 0.dp,// padding.calculateStartPadding(LocalLayoutDirection.current),
                    end = 0.dp,//padding.calculateEndPadding(LocalLayoutDirection.current),
                    bottom = 0.dp// if (model.isBottomBarShowing) padding.calculateBottomPadding() else (padding.calculateBottomPadding() - 80.dp).coerceAtLeast(
                    //                    0.dp
                )
            )
            {


                Children(
                    modifier = Modifier.fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(start = aniPadding),
                    stack = childStack,
                    animation = predictiveBackAnimation(
                        backHandler = component.backHandler,
                        onBack = component::onBackClicked,
                        fallbackAnimation = stackAnimation { child ->
                            if (viewManager.isTransitionsEnabled.value) {
                                when (child.instance) {
                                    is RootComponent.Child.MainJournal -> fade()
                                    is RootComponent.Child.MainSchool -> fade()
                                    is RootComponent.Child.MainHome -> fade()
                                    is RootComponent.Child.MainAdmin -> fade()
                                    is RootComponent.Child.MainMentoring -> fade()
                                    is RootComponent.Child.LessonReport -> iosSlide() + fade()
                                    is RootComponent.Child.HomeSettings -> iosSlide() + fade()
                                    is RootComponent.Child.AdminSchedule -> iosSlide() + fade()
                                    is RootComponent.Child.QRScanner -> iosSlide() + fade()
                                    is RootComponent.Child.HomeProfile -> fade()
                                    is RootComponent.Child.HomeAchievements -> fade()
                                    else -> if (isExpanded) fade() else iosSlide() + fade()
                                }
                            } else {
                                null
                            }
                        },
                        selector = { initialBackEvent, exit, enter ->
                            if (
                                initialBackEvent.swipeEdge == BackEvent.SwipeEdge.RIGHT ||
                                (
                                        exit.instance is Child.MainAdmin ||
                                                exit.instance is Child.MainHome ||
                                                exit.instance is MainJournal ||
                                                exit.instance is MainMentoring ||
                                                exit.instance is MainSchool
                                        )
                            ) {

                                materialPredictiveBackAnimatable(initialBackEvent = initialBackEvent)

                            } else {
                                predictiveBackAnimatable(
                                    initialBackEvent = initialBackEvent,
                                    exitModifier = { progress, _ -> Modifier.slideExitModifier(progress = progress) },
                                    enterModifier = { progress, _ ->
                                        Modifier.slideEnterModifier(
                                            progress = progress
                                        )
                                    },
                                )
                            }

                        },

                        )//backAnimation(component)
                ) {
                    when (val child = it.instance) {

                        is Child.ErrorLoad -> {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(child.reason, textAlign = TextAlign.Center)
                                    Spacer(Modifier.height(35.dp))
                                    CustomTextButton(
                                        "Перейти на главный экран"
                                    ) {
                                        component.onOutput(RootComponent.Output.NavigateToHome)
                                    }
                                }
                            }
                        }

                        is Child.AuthLogin -> LoginContent(child.component)
                        is Child.AuthActivation -> ActivationContent(child.component)


                        is MainHome -> MultiPaneSplit(
                            isExpanded = isExpanded,
                            viewManager = viewManager,
                            currentScreen = {
                                HomeContent(
                                    child.homeComponent,
                                    sharedTransitionScope = this@SharedTransitionLayout,
                                    isSharedVisible = stack.active.instance is Child.MainHome
                                )
                            },
                            firstScreen = {
                                HomeContent(
                                    child.homeComponent,
                                    sharedTransitionScope = this@SharedTransitionLayout,
                                    isSharedVisible = stack.active.instance is Child.MainHome
                                )
                            },
                            secondScreen = {
                                if ((model.moderation != Moderation.nothing || model.role == Roles.teacher && component.isMentoring == null
                                            ) && component.secondLogin == null
                                ) {
                                    JournalContent(
                                        child.journalComponent,
                                        role = model.role,
                                        moderation = model.moderation,
                                        onRefresh = {
                                            child.journalComponent.onEvent(JournalStore.Intent.Refresh); child.homeComponent.onEvent(
                                            HomeStore.Intent.Init
                                        )
                                        }
                                    )
                                } else {
                                    RatingContent(
                                        child.ratingComponent,
                                        isSharedVisible = stack.active.instance is Child.MainRating
                                    )
                                }
                            }
                        )

                        is MainJournal -> MultiPaneSplit(
                            isExpanded = isExpanded,
                            viewManager = viewManager,
                            currentScreen = {
                                JournalContent(
                                    child.journalComponent,
                                    role = model.role,
                                    moderation = model.moderation,
                                    onRefresh = {
                                        child.journalComponent.onEvent(JournalStore.Intent.Refresh); child.homeComponent.onEvent(
                                        HomeStore.Intent.Init
                                    )
                                    }
                                )
                            },
                            firstScreen = {
                                HomeContent(
                                    child.homeComponent,
                                    sharedTransitionScope = this@SharedTransitionLayout,
                                    isSharedVisible = stack.active.instance is Child.MainHome
                                )
                            },
                            secondScreen = {
                                JournalContent(
                                    child.journalComponent,
                                    role = model.role,
                                    moderation = model.moderation,
                                    onRefresh = {
                                        child.journalComponent.onEvent(JournalStore.Intent.Refresh); child.homeComponent.onEvent(
                                        HomeStore.Intent.Init
                                    )
                                    }
                                )
                            }
                        )

                        is Child.HomeDnevnikRuMarks -> {
                            MultiPaneSplit(
                                isExpanded = isExpanded,
                                viewManager = viewManager,
                                currentScreen = {
                                    DnevnikRuMarkContent(
                                        child.dnevnikRuMarksComponent,
                                        //                                        isVisible = stack.active.instance is Child.HomeDnevnikRuMarks
                                    )
                                },
                                firstScreen = {
                                    HomeContent(
                                        child.homeComponent,
                                        sharedTransitionScope = this@SharedTransitionLayout,
                                        isSharedVisible = stack.active.instance is Child.MainHome
                                    )
                                },
                                secondScreen = {
                                    DnevnikRuMarkContent(
                                        child.dnevnikRuMarksComponent,
                                        //                                        isVisible = stack.active.instance is Child.HomeDnevnikRuMarks
                                    )
                                }
                            )
                        }

                        is Child.HomeStudentLines -> {
                            MultiPaneSplit(
                                isExpanded = isExpanded,
                                viewManager = viewManager,
                                currentScreen = {
                                    StudentLinesContent(
                                        child.studentLinesComponent,
                                        //                                        isVisible = stack.active.instance is Child.HomeStudentLines
                                    )
                                },
                                firstScreen = {
                                    HomeContent(
                                        child.homeComponent,
                                        sharedTransitionScope = this@SharedTransitionLayout,
                                        isSharedVisible = stack.active.instance is Child.MainHome
                                    )
                                },
                                secondScreen = {
                                    StudentLinesContent(
                                        child.studentLinesComponent,
                                        //                                        isVisible = stack.active.instance is Child.HomeStudentLines
                                    )
                                }
                            )
                        }

                        is Child.HomeDetailedStups -> {
                            MultiPaneSplit(
                                isExpanded = isExpanded,
                                viewManager = viewManager,
                                currentScreen = {
                                    DetailedStupsContent(
                                        child.detailedStups,
                                        //                                        isVisible = stack.active.instance is Child.HomeDetailedStups
                                    )
                                },
                                firstScreen = {
                                    HomeContent(
                                        child.homeComponent,
                                        sharedTransitionScope = this@SharedTransitionLayout,
                                        isSharedVisible = stack.active.instance is Child.MainHome
                                    )
                                },
                                secondScreen = {
                                    DetailedStupsContent(
                                        child.detailedStups,
                                        //                                        isVisible = stack.active.instance is Child.HomeDetailedStups
                                    )
                                }
                            )
                        }

                        is Child.QRScanner -> {
                            QRContentActual(
                                component = child.qrComponent
                            )
                        }

                        is MainAdmin ->
                            Scaffold(
                                floatingActionButtonPosition = if (isExpanded) FabPosition.Center else FabPosition.End,
                                floatingActionButton = {
                                    ExtendedFloatingActionButton(
                                        onClick = {
                                            component.onOutput(RootComponent.Output.NavigateToSchedule)
                                        },
                                        modifier = Modifier.padding(bottom = if (!isExpanded) 80.dp else 10.dp)
                                    ) {
                                        GetAsyncIcon(
                                            path = RIcons.MagicWand
                                        )
                                        Spacer(Modifier.width(10.dp))
                                        Text("Расписание БЕТА")
                                    }
                                }
                            ) {
                                MultiPaneSplit(
                                    isExpanded = isExpanded,
                                    viewManager = viewManager,
                                    currentScreen = {
                                        AdminContent(
                                            child.adminComponent,
                                            isActive = true
                                        )
                                    },
                                    firstScreen = {
                                        AdminContent(
                                            child.adminComponent,
                                            isActive = true
                                        )
                                    },
                                    secondScreen = {
                                        Box(
                                            Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                "Выберите категорию"
                                            )

                                        }
                                    }
                                )
                            }


                        /*is AdminMentors ->
                    MultiPaneAdmin(
                        isExpanded,
                        adminComponent = child.adminComponent,
                        currentRouting = AdminComponent.Output.NavigateToMentors,
                        secondScreen = { MentorsContent(child.mentorsComponent) }
                    )*/


                        is AdminUsers ->
                            MultiPaneAdmin(
                                isExpanded,
                                viewManager = viewManager,
                                adminComponent = child.adminComponent,
                                currentRouting = AdminComponent.Output.NavigateToUsers,
                                secondScreen = { UsersContent(child.usersComponent) }
                            )

                        is AdminGroups ->
                            MultiPaneAdmin(
                                isExpanded,
                                viewManager = viewManager,
                                adminComponent = child.adminComponent,
                                currentRouting = AdminComponent.Output.NavigateToGroups,
                                secondScreen = {
                                    GroupsContent(
                                        child.groupsComponent,
                                        //                                        isVisible = stack.active.instance is Child.AdminGroups
                                    )
                                }
                            )

                        is LessonReport ->
                            MultiPaneJournal(
                                isExpanded,
                                viewManager = viewManager,
                                journalComponent = child.journalComponent,
                                currentReportId = 0,
                                role = model.role,
                                moderation = model.moderation,
                                secondScreen = { LessonReportContent(child.lessonReport) }
                            )

                        is HomeSettings ->
                            SettingsContent(
                                isExpanded,
                                child.settingsComponent,
                                isVisible = stack.active.instance is Child.HomeSettings
                            )

                        is Child.HomeAllGroupMarks ->
                            MultiPaneSplit(
                                isExpanded = isExpanded,
                                viewManager = viewManager,
                                currentScreen = {
                                    AllGroupMarksContent(
                                        child.allGroupMarksComponent,
                                        //                                        isVisible = stack.active.instance is Child.HomeAllGroupMarks
                                    )
                                },
                                firstScreen = {
                                    AllGroupMarksContent(
                                        child.allGroupMarksComponent,
                                        //                                        isVisible = stack.active.instance is Child.HomeAllGroupMarks
                                    )
                                },
                                secondScreen = {
                                    JournalContent(
                                        child.journalComponent,
                                        role = model.role,
                                        moderation = model.moderation,
                                        onRefresh = { child.journalComponent.onEvent(JournalStore.Intent.Refresh) }
                                    )
                                }
                            )

                        is Child.AdminSchedule -> ScheduleContent(child.scheduleComponent)
                        is Child.HomeProfile -> MultiPaneSplit(
                            isExpanded = isExpanded,
                            viewManager = viewManager,
                            currentScreen = {
                                ProfileContent(
                                    child.profileComponent,
                                    isSharedVisible = stack.active.instance is Child.HomeProfile
                                )
                            },
                            firstScreen = {
                                HomeContent(
                                    child.homeComponent,
                                    sharedTransitionScope = this@SharedTransitionLayout,
                                    isSharedVisible = stack.active.instance is Child.MainHome
                                )
                            },
                            secondScreen = {
                                ProfileContent(
                                    child.profileComponent,
                                    isSharedVisible = stack.active.instance is Child.HomeProfile
                                )
                            }
                        )

                        is Child.AdminCabinets -> MultiPaneAdmin(
                            isExpanded,
                            viewManager = viewManager,
                            adminComponent = child.adminComponent,
                            currentRouting = AdminComponent.Output.NavigateToCabinets,
                            secondScreen = {
                                CabinetsContent(
                                    child.cabinetsComponent,
                                    //                                    isVisible = stack.active.instance is Child.AdminCabinets
                                )
                            }
                        )

                        is MainRating ->
                            MultiPaneSchool(
                                isExpanded = isExpanded,
                                schoolComponent = child.schoolComponent,
                                currentRouting = SchoolRoutings.SchoolRating,
                                viewManager = viewManager,
                                secondScreen = {
                                    RatingContent(
                                        child.ratingComponent,
                                        isSharedVisible = stack.active.instance is Child.MainRating
                                    )
                                }
                            )


                        is Child.HomeTasks -> MultiPaneSplit(
                            isExpanded = isExpanded,
                            viewManager = viewManager,
                            currentScreen = {
                                HomeTasksContent(
                                    child.homeTasksComponent,
                                    isVisible = stack.active.instance is Child.HomeTasks
                                )
                            },
                            firstScreen = {
                                HomeContent(
                                    child.homeComponent,
                                    sharedTransitionScope = this@SharedTransitionLayout,
                                    isSharedVisible = stack.active.instance is Child.MainHome
                                )
                            },
                            secondScreen = {
                                HomeTasksContent(
                                    child.homeTasksComponent,
                                    isVisible = stack.active.instance is Child.HomeTasks
                                )
                            }
                        )

                        is Child.AdminCalendar -> MultiPaneAdmin(
                            isExpanded,
                            viewManager = viewManager,
                            adminComponent = child.adminComponent,
                            currentRouting = AdminComponent.Output.NavigateToCalendar,
                            secondScreen = {
                                CalendarContent(
                                    child.calendarComponent,
                                    //                                    isVisible = stack.active.instance is Child.AdminCalendar
                                )
                            }
                        )

                        is MainMentoring -> MultiPaneMentoring(
                            isExpanded,
                            mentoringComponent = child.mentoringComponent,
                            rootComponent = null,
                            viewManager = viewManager,
                            isVisible = childStack.active.instance is Child.MainMentoring
                        )

                        is Child.SecondView ->
                            if (child.isMentoring) {
                                MultiPaneMentoring(
                                    isExpanded,
                                    mentoringComponent = child.mentoringComponent,
                                    rootComponent = child.rootComponent,
                                    viewManager = viewManager,
                                    isVisible = childStack.active.instance is Child.SecondView
                                )
                            } else {
                                MultiPaneSplit(
                                    isExpanded = isExpanded,
                                    viewManager = viewManager,
                                    currentScreen = { RootContent(child.rootComponent) },
                                    firstScreen = {
                                        if (child.homeComponent != null) HomeContent(
                                            child.homeComponent!!,
                                            pickedLogin = child.rootComponent.secondLogin ?: "",
                                            sharedTransitionScope = this@SharedTransitionLayout,
                                            isSharedVisible = stack.active.instance is Child.MainHome
                                        )
                                    },
                                    secondScreen = {
                                        RootContent(child.rootComponent)
                                    }
                                )
                            }


                        is Child.AdminAchievements -> {
                            val previousScreen = stack.items.getOrNull(stack.items.size - 2)?.instance
                            if (previousScreen is Child.MainMentoring || stack.active.instance is Child.MainMentoring) {
                                AdminAchievementsContent(
                                    child.adminAchievementsComponent,
                                    //                                    isVisible = stack.active.instance is Child.AdminAchievements
                                )
                            } else {
                                MultiPaneAdmin(
                                    isExpanded,
                                    viewManager = viewManager,
                                    adminComponent = child.adminComponent,
                                    currentRouting = AdminComponent.Output.NavigateToAchievements,
                                    secondScreen = {
                                        AdminAchievementsContent(
                                            child.adminAchievementsComponent,
                                            //                                            isVisible = stack.active.instance is Child.AdminAchievements
                                        )
                                    }
                                )
                            }
                        }

                        is Child.HomeAchievements ->
                            MultiPaneSplit(
                                isExpanded = isExpanded,
                                viewManager = viewManager,
                                currentScreen = {
                                    HomeAchievementsContent(
                                        child.achievementsComponent,
                                        isVisible = stack.active.instance is Child.HomeAchievements
                                    )
                                },
                                firstScreen = {
                                    HomeContent(
                                        child.homeComponent,
                                        sharedTransitionScope = this@SharedTransitionLayout,
                                        isSharedVisible = stack.active.instance is Child.MainHome
                                    )
                                },
                                secondScreen = {
                                    HomeAchievementsContent(
                                        child.achievementsComponent,
                                        isVisible = stack.active.instance is Child.HomeAchievements
                                    )
                                }
                            )

                        is Child.AdminParents ->
                            MultiPaneAdmin(
                                isExpanded,
                                viewManager = viewManager,
                                adminComponent = child.adminComponent,
                                currentRouting = AdminComponent.Output.NavigateToParents,
                                secondScreen = {
                                    AdminParentsContent(
                                        child.parentsComponent,
                                        //                                        isVisible = stack.active.instance is Child.AdminParents
                                    )
                                }
                            )

                        is MainSchool -> MultiPaneSplit(
                            isExpanded = isExpanded,
                            viewManager = viewManager,
                            currentScreen = {
                                SchoolContent(
                                    child.schoolComponent,
                                    currentRouting = SchoolRoutings.SchoolRating
                                )
                            },
                            firstScreen = {
                                SchoolContent(
                                    child.schoolComponent,
                                    currentRouting = SchoolRoutings.SchoolRating
                                )
                            },
                            secondScreen = {
                                RatingContent(
                                    child.ratingComponent,
                                    isSharedVisible = stack.active.instance is Child.MainRating
                                )
                            }
                        )

                        is Child.SchoolFormRating -> MultiPaneSchool(
                            isExpanded = isExpanded,
                            schoolComponent = child.schoolComponent,
                            currentRouting = SchoolRoutings.FormRating,
                            viewManager = viewManager,
                            secondScreen = {
                                FormRatingContent(
                                    child.formRatingComponent,
                                    isVisible = stack.active.instance is Child.SchoolFormRating
                                )
                            }
                        )

                        is Child.SchoolMinistry -> MultiPaneSchool(
                            isExpanded = isExpanded,
                            schoolComponent = child.schoolComponent,
                            currentRouting = SchoolRoutings.Ministry,
                            viewManager = viewManager,
                            secondScreen = {
                                MinistryContent(
                                    child.ministryComponent,
                                    isVisible = stack.active.instance is Child.SchoolMinistry
                                )
                            }
                        )
                    }
                }
                if (component.secondLogin == null) {
                    CustomNavigationRail(isVertical, component, model, childStack, items)
                }
            }
        }

        if (component.secondLogin == null) {
            val time = 600
            AnimatedVisibility(
                model.isStartUserGreetingsShowing || model.isGreetingsShowing || nCheckModel.state != NetworkState.None || !model.isTokenValid,
                enter = fadeIn(animationSpec = tween(time)) + slideInVertically(
                    animationSpec = tween(
                        time
                    )
                ) { -it / 2 },
                exit = fadeOut(animationSpec = tween(time)) + slideOutVertically(
                    animationSpec = tween(
                        time
                    )
                ) { -it / 2 }
            ) {
                Box(
                    Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
                ) {
                    val isBirthday = model.birthday.cut(4) == getDate().replace(".", "")
                        .cut(4) && model.birthday != "01012000"
                    Column(
                        modifier = Modifier.fillMaxWidth().align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        AnimatedVisibility(isBirthday) {
                            Column {
                                GetAsyncIcon(
                                    path = RIcons.Gift,
                                    size = 100.dp
                                )
                                Spacer(Modifier.height(20.dp))
                            }
                        }
                        Text(
                            when (Clock.System.now().toLocalDateTime(TimeZone.of("UTC+3")).hour) {
                                in 5..10 -> "Доброе утро!"
                                in 11..18 -> "Добрый день!"
                                in 19..21 -> "Добрый вечер!"
                                else -> "Доброй ночи!"
                            },
                            fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(bottom = if (isBirthday) 120.dp else 0.dp)
                        )
                        Text(
                            text = applicationVersionString,
                            modifier = Modifier.fillMaxWidth().alpha(.5f),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Black,
                            fontSize = 10.esp
                        )
                    }
                    Crossfade(
                        targetState = nCheckModel.state,
                        modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter)
                    ) {
                        Column(
                            Modifier.fillMaxWidth().animateContentSize().padding(bottom = 100.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            when {

                                it is NetworkState.Error -> {
                                    Text(text = if (nCheckModel.error != "") nCheckModel.error else "Загрузка...")
                                    Spacer(Modifier.height(7.dp))
                                    CustomTextButton(text = "Попробовать ещё раз") {
                                        nCheckModel.onFixErrorClick()
                                    }
                                    Spacer(Modifier.height(7.dp))
                                    CustomTextButton(text = "Продолжить без синхронизации") {
                                        component.checkNInterface.nSuccess()
                                        component.onOutput(RootComponent.Output.NavigateToHome)
                                    }
                                }

                                it is NetworkState.None && !model.isTokenValid -> {
                                    Text("Ваш токен недействителен!")
                                    Spacer(Modifier.height(7.dp))
                                    CustomTextButton(text = "Перезайти в аккаунт") {
                                        component.onOutput(RootComponent.Output.NavigateToAuth)
                                        component.onEvent(
                                            RootStore.Intent.ChangeTokenValidationStatus(
                                                true
                                            )
                                        )
                                    }
                                }

                                it is NetworkState.Loading || model.isStartUserGreetingsShowing -> {
                                    CircularProgressIndicator()
                                }


                            }
                        }
                    }
                }
            }
        }
    }

    if (isNewVersionDialogShowing.value && model.version > applicationVersion) {
        AlertDialog(
            onDismissRequest = { isNewVersionDialogShowing.value = false },
            confirmButton = {
                CustomTextButton(
                    text = "Понятно"
                ) {
                    isNewVersionDialogShowing.value = false
                }
            },
            text = {
                Text(
                    buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, fontSize = 18.esp)) {
                            append("Доступна новая версия!\n")
                        }
                        append("Игнорирование приведёт к проблемам при загрузке данных")
                    }
                )
            },
            modifier = Modifier.clip(MaterialTheme.shapes.large).hazeHeader(
                viewManager = viewManager,
                hazeState = GlobalHazeState.current,
                isMasked = false
            ),
            containerColor = if (viewManager.hazeHardware.value) Color.Transparent else AlertDialogDefaults.containerColor

        )
    }
}

@OptIn(
    ExperimentalAnimationApi::class,
    ExperimentalFoundationApi::class,
    ExperimentalLayoutApi::class
)
@Composable
fun MultiPaneMentoring(
    isExpanded: Boolean,
    viewManager: ViewManager,
    mentoringComponent: MentoringComponent?,
    rootComponent: RootComponent?,
    isVisible: Boolean
) {
    val model = mentoringComponent?.model?.subscribeAsState()
    Crossfade(model?.value?.isTableView ?: false) { cfState ->
        if (cfState) {
            MentoringContent(
                mentoringComponent!!,
//                isVisible = isVisible
            )
        } else {
            MultiPaneSplit(
                isExpanded = isExpanded,
                viewManager = viewManager,
                currentScreen = {
                    if (rootComponent == null && mentoringComponent != null) MentoringContent(
                        mentoringComponent,
//                        isVisible = isVisible
                    )
                    else if (rootComponent != null) Box {
                        val model by mentoringComponent!!.model.subscribeAsState()
                        val fio = model.students.firstOrNull { it.login == model.chosenLogin }?.fio
                        Column {
                            Spacer(Modifier.height(10.dp))
                            RootContent(rootComponent)
                        }
                        Box(
                            Modifier.fillMaxWidth().padding(top = viewManager.topPadding),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("${fio?.surname} ${fio?.name}")
                        }
                    }
                    else Text("IDK")
                },
                firstScreen = {
                    if (mentoringComponent != null) MentoringContent(
                        mentoringComponent,
//                        isVisible = isVisible
                    ) else Text(
                        "IDK"
                    )
                },
                secondScreen = {
                    if (rootComponent == null) Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { Text("Выберите ученика") } else RootContent(rootComponent)
                }
            )
        }
    }

}


@ExperimentalMaterial3Api
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MultiPaneJournal(
    isExpanded: Boolean,
    journalComponent: JournalComponent,
    currentReportId: Int,
    role: String,
    moderation: String,
    viewManager: ViewManager,
    secondScreen: @Composable () -> Unit
) {
    if (isExpanded) {
        LaunchedEffect(viewManager.isFullScreen.value) {
            if (viewManager.isFullScreen.value) {
                viewManager.splitPaneState.dispatchRawMovement(-30000f)
            }
        }
        val x = animateDpAsState(if (viewManager.isFullScreen.value) 0.dp else 400.dp)
        HorizontalSplitPane(
            splitPaneState = viewManager.splitPaneState
        ) {
            first(minSize = x.value) {
                JournalContent(
                    journalComponent,
                    isNotMinimized = false,
                    role = role,
                    moderation = moderation,
                    onRefresh = { }
                )
            }

            dSplitter(viewManager.isFullScreen)


            second(minSize = 500.dp) {
                secondScreen()
            }
        }
    } else {
        secondScreen()
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MultiPaneSchool(
    isExpanded: Boolean,
    schoolComponent: SchoolComponent,
    currentRouting: SchoolRoutings,
    viewManager: ViewManager,
    secondScreen: @Composable () -> Unit
) {
    if (isExpanded) {
        HorizontalSplitPane(
            splitPaneState = viewManager.splitPaneState
        ) {
            first(minSize = 400.dp) {
                SchoolContent(schoolComponent, currentRouting = currentRouting)
            }
            dSplitter()
            second(minSize = 400.dp) {
                secondScreen()
            }
        }

    } else {
        secondScreen()
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MultiPaneAdmin(
    isExpanded: Boolean,
    adminComponent: AdminComponent,
    currentRouting: AdminComponent.Output,
    viewManager: ViewManager,
    secondScreen: @Composable () -> Unit
) {
    if (isExpanded) {
        HorizontalSplitPane(
            splitPaneState = viewManager.splitPaneState
        ) {
            first(minSize = 250.dp) {
                AdminContent(adminComponent, currentRouting = currentRouting)
            }
            dSplitter()
            second(minSize = 500.dp) {
                secondScreen()
            }
        }

    } else {
        secondScreen()
    }
}

@Composable
fun MultiPaneSplit(
    isExpanded: Boolean,
    viewManager: ViewManager,
    currentScreen: @Composable () -> Unit,
    firstScreen: @Composable () -> Unit,
    secondScreen: @Composable () -> Unit
) {
    if (isExpanded) {
        HorizontalSplitPane(
            splitPaneState = viewManager.splitPaneState
        ) {
            first(minSize = 400.dp) {
                firstScreen()
            }
            dSplitter()
            second(minSize = 400.dp) {
                secondScreen()
            }
        }
    } else {
        currentScreen()
    }
}


data class NavigationItem(
    val iconPath: String,
    val label: String,
    val category: RootComponent.RootCategories,
    val onClickOutput: RootComponent.Output,
    val size: Dp = 22.dp
)


@Composable
fun CustomNavigationBar(
    viewManager: ViewManager,
    component: RootComponent,
    model: RootStore.State,
    childStack: ChildStack<*, Child>,
    items: List<NavigationItem?>
) {
    val density = LocalDensity.current

    NavigationBar(
        modifier = Modifier.then(
            if (viewManager.hazeHardware.value) Modifier.hazeChild(
                GlobalHazeState.current,
                style = LocalHazeStyle.current
            ) {
//                this.blurRadius = 525.dp
                mask = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent, Color.Magenta.copy(.82f), Color.Magenta//.copy(alpha = 1.0f)
                    ),
//                    startY = with(density) { (5.dp).toPx() },
//                    endY = with(density) { (40.dp).toPx() }
                )
//                    Color.Transparent, Color.Transparent,
//                    Color.Magenta, Color.Magenta, Color.Magenta))
//                progressive = view.hazeProgressive.copy(endIntensity = 1f, startIntensity = 0f)
            }
            else Modifier
        ).fillMaxWidth(),
        containerColor = if (viewManager.hazeHardware.value) Color.Transparent else MaterialTheme.colorScheme.background,
        tonalElevation = 0.dp
    ) {
        items.filterNotNull().forEach { item ->
            NavigationBarItem(
                selected = getCategory(childStack.active.configuration as Config) == item.category,
                onClick = { component.onOutput(item.onClickOutput) },
                icon = {
                    Box(Modifier.size(24.dp), contentAlignment = Alignment.Center) {
                        GetAsyncIcon(
                            path = item.iconPath,
                            size = item.size
                        )
                    }
                },
                label = { Text(item.label, maxLines = 1, overflow = TextOverflow.Ellipsis) }
            )
        }
    }


}

@Composable
fun CustomNavigationRail(
    isVertical: Boolean,
    component: RootComponent,
    model: RootStore.State,
    childStack: ChildStack<*, Child>,
    items: List<NavigationItem?>
) {
    AnimatedVisibility(
        visible = !isVertical && childStack.active.configuration !is Config.AuthActivation && childStack.active.configuration !is Config.AuthLogin,
        enter = fadeIn(animationSpec = tween(300)) +
                slideInHorizontally { -it },
        exit = fadeOut(animationSpec = tween(300)) + slideOutHorizontally { -it },
        modifier = Modifier.width(80.dp)
    ) {
        NavigationRail() {
            Column(
                Modifier.fillMaxHeight().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
            ) {
                items.filterNotNull().forEach { item ->
                    NavigationRailItem(
                        selected = getCategory(childStack.active.configuration as Config) == item.category,
                        onClick = { component.onOutput(item.onClickOutput) },
                        icon = {
                            Box(Modifier.size(24.dp), contentAlignment = Alignment.Center) {
                                GetAsyncIcon(
                                    path = item.iconPath,
                                    size = item.size
                                )
                            }
                               },
                        label = { Text(item.label) }
                    )
                }
            }

        }
    }
}

private fun isBottomBarShowing(config: Config): Boolean {
    return config in listOf(
        Config.MainHome,
        Config.MainSchool,
        Config.MainAdmin,
        Config.MainJournal,
        Config.MainMentoring
    )
}

private fun getCategory(config: Config): RootComponent.RootCategories {
    return when (config) {
        Config.AdminCabinets -> Admin
        Config.AdminCalendar -> Admin
        Config.AdminGroups -> Admin
        is Config.AdminSchedule -> School
        Config.AdminUsers -> Admin
        Config.AdminAchievements -> Admin
        Config.AdminParents -> Admin

        Config.AuthActivation -> Home
        is Config.AuthLogin -> Home

        is Config.HomeAllGroupMarks -> Home
        is Config.HomeDetailedStups -> Home
        is Config.HomeDnevnikRuMarks -> Home
        is Config.HomeProfile -> Home
        Config.HomeSettings -> Home
        is Config.HomeTasks -> Home
        is Config.HomeAchievements -> Home
        is Config.HomeStudentLines -> Home

        Config.MainAdmin -> Admin
        Config.MainHome -> Home
        Config.MainJournal -> Journal
        Config.MainRating -> School

        is Config.LessonReport -> Journal
        Config.MainMentoring -> Mentoring
        is Config.SecondView -> Mentoring
        is Config.QRScanner -> Home
        Config.MainSchool -> School
        is Config.SchoolFormRating -> School
        Config.SchoolMinistry -> School
        is Config.ErrorScreen -> Home
    }
}