@file:OptIn(ExperimentalSplitPaneApi::class)

import admin.AdminComponent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Diversity1
import androidx.compose.material.icons.rounded.EditCalendar
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LibraryBooks
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import animations.iosSlide
import animations.slideEnterModifier
import animations.slideExitModifier
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.predictiveBackAnimatable
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.predictiveBackAnimation
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.router.stack.ChildStack
import components.CustomTextButton
import components.networkInterface.NetworkState
import dev.chrisbanes.haze.hazeChild
import forks.splitPane.ExperimentalSplitPaneApi
import forks.splitPane.HorizontalSplitPane
import forks.splitPane.dSplitter
import forks.splitPane.rememberSplitPaneState
import journal.JournalComponent
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import root.RootComponent
import root.RootComponent.Child
import root.RootComponent.Child.AdminGroups
//import root.RootComponent.Child.AdminMentors
import root.RootComponent.Child.AdminUsers
import root.RootComponent.Child.LessonReport
import root.RootComponent.Child.MainAdmin
import root.RootComponent.Child.MainHome
import root.RootComponent.Child.MainJournal
import root.store.RootStore
import server.Moderation
import server.Roles
import view.LocalViewManager
import view.ViewManager
import view.WindowScreen
import groups.GroupsContent
import home.HomeStore
import journal.JournalStore
import mentoring.MentoringComponent
import mentoring.MentoringContent
import root.RootComponent.Child.HomeSettings
import root.RootComponent.Child.MainMentoring
import root.RootComponent.Child.MainRating
import root.RootComponent.Config
import root.RootComponent.RootCategories.Admin
import root.RootComponent.RootCategories.Home
import root.RootComponent.RootCategories.Journal
import root.RootComponent.RootCategories.Mentoring
import root.RootComponent.RootCategories.Rating
import server.getDate
import view.WindowCalculator
import androidx.compose.material.icons.rounded.Cake
import androidx.compose.material3.NavigationBarDefaults
import server.cut

@ExperimentalAnimationApi
@OptIn(
    ExperimentalLayoutApi::class, ExperimentalDecomposeApi::class,
    ExperimentalMaterial3Api::class
)
@ExperimentalFoundationApi
@Composable
fun RootContent(component: RootComponent, isJs: Boolean = false) {
    val viewManager = LocalViewManager.current
    val childStack by component.childStack.subscribeAsState()

    val model by component.model.subscribeAsState()
    val nCheckModel by component.checkNInterface.networkModel.subscribeAsState()
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
                icon = Icons.Rounded.Home,
                label = "Главная",
                category = if (isExpanded && getCategory(childStack.active.configuration as Config) == Journal) Journal
                else if (isExpanded && getCategory(childStack.active.configuration as Config) == Rating && model.role != Roles.teacher) Rating
                else Home,
                onClickOutput = RootComponent.Output.NavigateToHome
            ),
            if (model.moderation in listOf(
                    Moderation.both,
                    Moderation.mentor
                ) && component.isMentoring == null
            ) NavigationItem(
                icon = Icons.Rounded.Diversity1,
                label = "Наставник",
                category = Mentoring,
                onClickOutput = RootComponent.Output.NavigateToMentoring
            ) else null,
            if (!isExpanded && (model.role == Roles.teacher || model.moderation in listOf(
                    Moderation.moderator,
                    Moderation.mentor,
                    Moderation.both
                ) ) && component.isMentoring == null) NavigationItem(
                icon = Icons.Rounded.LibraryBooks,
                label = "Журнал",
                category = Journal,
                onClickOutput = RootComponent.Output.NavigateToJournal
            ) else null,
            if (model.moderation in listOf(
                    Moderation.moderator,
                    Moderation.mentor,
                    Moderation.both
                )  && component.isMentoring == null
            ) NavigationItem(
                icon = Icons.Rounded.GridView,
                label = "Модерация",
                category = Admin,
                onClickOutput = RootComponent.Output.NavigateToAdmin
            ) else null,
            if ((!isExpanded) || (isExpanded && model.role == Roles.teacher && component.isMentoring == null )) NavigationItem(
                icon = Icons.Rounded.Star,
                label = "Рейтинг",
                category = Rating,
                onClickOutput = RootComponent.Output.NavigateToRating
            ) else null,
        )
//    var bottomBarAnimationScope: AnimatedVisibilityScope? = null
        Scaffold(
            Modifier.fillMaxSize(),
            bottomBar = {
                //sber
                Box(Modifier.animateContentSize().fillMaxWidth()) {
                    AnimatedVisibility(

                        visible = isBottomBarShowing(childStack.active.configuration as Config) && ((isVertical && (component.secondLogin == null)) || component.isMentoring == false),
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
            Box(
                Modifier.fillMaxSize().padding(
                    top = 0.dp,
                    start = 0.dp,// padding.calculateStartPadding(LocalLayoutDirection.current),
                    end = 0.dp,//padding.calculateEndPadding(LocalLayoutDirection.current),
                    bottom = 0.dp// if (model.isBottomBarShowing) padding.calculateBottomPadding() else (padding.calculateBottomPadding() - 80.dp).coerceAtLeast(
                    //0.dp
                    //)
                )
            ) {
                val aniPadding by animateDpAsState(
                    if ((!isVertical) && childStack.active.configuration !in listOf(
                            Config.AuthActivation,
                            Config.AuthLogin
                        )
                    ) 80.dp else 0.dp
                )

                Children(
                    modifier = Modifier.fillMaxSize()
                        .padding(start = aniPadding),
                    stack = childStack,
                    animation = predictiveBackAnimation(
                        backHandler = component.backHandler,
                        onBack = component::onBackClicked,
                        fallbackAnimation = stackAnimation { child ->
                            when (child.instance) {
                                is MainJournal -> fade()
                                is MainRating -> fade()
                                is MainHome -> fade()
                                is MainAdmin -> fade()
                                is MainMentoring -> fade()
                                is LessonReport -> iosSlide()
                                is HomeSettings -> iosSlide()
                                is Child.AdminSchedule -> iosSlide()
                                else -> if (isExpanded) fade() else iosSlide()
                            }
                        },
                        selector = { initialBackEvent, _, _ ->
                            predictiveBackAnimatable(
                                initialBackEvent = initialBackEvent,
                                exitModifier = { progress, _ -> Modifier.slideExitModifier(progress = progress) },
                                enterModifier = { progress, _ ->
                                    Modifier.slideEnterModifier(
                                        progress = progress
                                    )
                                },
                            )
                        },
                    )
                ) {
                    when (val child = it.instance) {
                        is Child.AuthLogin -> LoginContent(child.component)
                        is Child.AuthActivation -> ActivationContent(child.component)


                        is MainHome -> MultiPaneSplit(
                            isExpanded = isExpanded,
                            viewManager = viewManager,
                            currentScreen = { HomeContent(child.homeComponent) },
                            firstScreen = { HomeContent(child.homeComponent) },
                            secondScreen = {
                                if (model.moderation != Moderation.nothing || model.role == Roles.teacher && component.isMentoring == null) {
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
                                    RatingContent(child.ratingComponent)
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
                            firstScreen = { HomeContent(child.homeComponent) },
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
                                currentScreen = { DnevnikRuMarkContent(child.dnevnikRuMarksComponent) },
                                firstScreen = { HomeContent(child.homeComponent) },
                                secondScreen = { DnevnikRuMarkContent(child.dnevnikRuMarksComponent) }
                            )
                        }
                        is Child.HomeStudentLines -> {
                            MultiPaneSplit(
                                isExpanded = isExpanded,
                                viewManager = viewManager,
                                currentScreen = { StudentLinesContent(child.studentLinesComponent) },
                                firstScreen = { HomeContent(child.homeComponent) },
                                secondScreen = { StudentLinesContent(child.studentLinesComponent)  }
                            )
                        }

                        is Child.HomeDetailedStups -> {
                            MultiPaneSplit(
                                isExpanded = isExpanded,
                                viewManager = viewManager,
                                currentScreen = { DetailedStupsContent(child.detailedStups) },
                                firstScreen = { HomeContent(child.homeComponent) },
                                secondScreen = { DetailedStupsContent(child.detailedStups) }
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
                                        Icon(
                                            Icons.Rounded.EditCalendar,
                                            null
                                        )
                                        Spacer(Modifier.width(10.dp))
                                        Text("Расписание")
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
                                secondScreen = { GroupsContent(child.groupsComponent) }
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
                                child.settingsComponent
                            )

                        is Child.HomeAllGroupMarks ->
                            MultiPaneSplit(
                                isExpanded = isExpanded,
                                viewManager = viewManager,
                                currentScreen = { AllGroupMarksContent(child.allGroupMarksComponent) },
                                firstScreen = { AllGroupMarksContent(child.allGroupMarksComponent) },
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
                            currentScreen = { ProfileContent(child.profileComponent) },
                            firstScreen = { HomeContent(child.homeComponent) },
                            secondScreen = {
                                ProfileContent(child.profileComponent)
                            }
                        )

                        is Child.AdminCabinets -> MultiPaneAdmin(
                            isExpanded,
                            viewManager = viewManager,
                            adminComponent = child.adminComponent,
                            currentRouting = AdminComponent.Output.NavigateToCabinets,
                            secondScreen = { CabinetsContent(child.cabinetsComponent) }
                        )

                        is MainRating -> MultiPaneSplit(
                            isExpanded = isExpanded,
                            viewManager = viewManager,
                            currentScreen = { RatingContent(child.ratingComponent) },
                            firstScreen = { HomeContent(child.homeComponent) },
                            secondScreen = {
                                RatingContent(child.ratingComponent)
                            }
                        )

                        is Child.HomeTasks -> MultiPaneSplit(
                            isExpanded = isExpanded,
                            viewManager = viewManager,
                            currentScreen = { HomeTasksContent(child.homeTasksComponent) },
                            firstScreen = { HomeContent(child.homeComponent) },
                            secondScreen = {
                                HomeTasksContent(child.homeTasksComponent)
                            }
                        )

                        is Child.AdminCalendar -> MultiPaneAdmin(
                            isExpanded,
                            viewManager = viewManager,
                            adminComponent = child.adminComponent,
                            currentRouting = AdminComponent.Output.NavigateToCalendar,
                            secondScreen = { CalendarContent(child.calendarComponent) }
                        )

                        is MainMentoring -> MultiPaneMentoring(
                            isExpanded,
                            mentoringComponent = child.mentoringComponent,
                            rootComponent = null,
                            viewManager = viewManager
                        )

                        is Child.SecondView -> if(component.isMentoring == true) {
                            MultiPaneMentoring(
                                isExpanded,
                                mentoringComponent = child.mentoringComponent,
                                rootComponent = child.rootComponent,
                                viewManager = viewManager
                            )
                        } else {
                            MultiPaneSplit(
                                isExpanded = isExpanded,
                                viewManager = viewManager,
                                currentScreen = { RootContent(child.rootComponent) },
                                firstScreen = { if(child.homeComponent != null) HomeContent(child.homeComponent!!, pickedLogin = child.rootComponent.secondLogin ?: "")  },
                                secondScreen = {
                                    RootContent(child.rootComponent)
                                }
                            )
                        }

                        is Child.AdminAchievements -> MultiPaneAdmin(
                            isExpanded,
                            viewManager = viewManager,
                            adminComponent = child.adminComponent,
                            currentRouting = AdminComponent.Output.NavigateToAchievements,
                            secondScreen = { AdminAchievementsContent(child.adminAchievementsComponent) }
                        )

                        is Child.HomeAchievements ->
                            MultiPaneSplit(
                                isExpanded = isExpanded,
                                viewManager = viewManager,
                                currentScreen = { HomeAchievementsContent(child.achievementsComponent) },
                                firstScreen = { HomeContent(child.homeComponent) },
                                secondScreen = { HomeAchievementsContent(child.achievementsComponent) }
                            )

                        is Child.AdminParents ->
                            MultiPaneAdmin(
                                isExpanded,
                                viewManager = viewManager,
                                adminComponent = child.adminComponent,
                                currentRouting = AdminComponent.Output.NavigateToParents,
                                secondScreen = { AdminParentsContent(child.parentsComponent) }
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
                model.isGreetingsShowing || nCheckModel.state != NetworkState.None || !model.isTokenValid,
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
                                Icon(
                                    imageVector = Icons.Rounded.Cake, contentDescription = null,
                                    modifier = Modifier.size(100.dp)
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
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(bottom = if (isBirthday) 120.dp else 0.dp)
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
                            when (it) {
                                is NetworkState.Loading -> {
                                    CircularProgressIndicator()
                                }

                                is NetworkState.Error -> {
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

                                NetworkState.None -> {
                                    if (!model.isTokenValid) {
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
                                }
                            }
                        }
                    }
                }
            }
        }
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
    rootComponent: RootComponent?
) {
    MultiPaneSplit(
        isExpanded = isExpanded,
        viewManager = viewManager,
        currentScreen = {
            if (rootComponent == null && mentoringComponent != null) MentoringContent(
                mentoringComponent
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
            if (mentoringComponent != null) MentoringContent(mentoringComponent) else Text(
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
    val icon: ImageVector,
    val label: String,
    val category: RootComponent.RootCategories,
    val onClickOutput: RootComponent.Output
)


@Composable
fun CustomNavigationBar(
    viewManager: ViewManager,
    component: RootComponent,
    model: RootStore.State,
    childStack: ChildStack<*, Child>,
    items: List<NavigationItem?>
) {


    NavigationBar(
        modifier = Modifier.then(
            if (viewManager.hazeStyle != null) Modifier.hazeChild(
                viewManager.hazeState,
                style = viewManager.hazeStyle!!.value
            ) else Modifier
        ).fillMaxWidth(),
        containerColor = if (viewManager.hazeStyle != null) Color.Transparent else MaterialTheme.colorScheme.background,
        tonalElevation = 0.dp
    ) {
        items.filterNotNull().forEach { item ->
            NavigationBarItem(
                selected = getCategory(childStack.active.configuration as Config) == item.category,
                onClick = { component.onOutput(item.onClickOutput) },
                icon = { Icon(item.icon, null) },
                label = { Text(item.label) }
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
        visible = !isVertical && childStack.active.configuration !in listOf(
            Config.AuthActivation,
            Config.AuthLogin
        ),
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
                        icon = { Icon(item.icon, null) },
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
        Config.MainRating,
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
        Config.AdminSchedule -> Admin
        Config.AdminUsers -> Admin
        Config.AdminAchievements -> Admin
        Config.AdminParents -> Admin

        Config.AuthActivation -> Home
        Config.AuthLogin -> Home

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
        Config.MainRating -> Rating

        is Config.LessonReport -> Journal
        Config.MainMentoring -> Mentoring
        is Config.SecondView -> Mentoring
    }
}