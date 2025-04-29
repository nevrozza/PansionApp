@file:OptIn(ExperimentalSplitPaneApi::class)

//import root.RootComponent.Child.AdminMentors

import admin.AdminComponent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.desktop.ui.tooling.preview.utils.esp
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.Child
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.materialPredictiveBackAnimatable
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.predictiveBackAnimatable
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.predictiveBackAnimation
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.essenty.backhandler.BackEvent
import components.GetAsyncIcon
import components.foundation.CTextButton
import components.foundation.DefaultErrorView
import components.foundation.hazeHeader
import components.foundation.hazeUnder
import components.networkInterface.NetworkState
import dev.chrisbanes.haze.ExperimentalHazeApi
import forks.splitPane.ExperimentalSplitPaneApi
import forks.splitPane.HorizontalSplitPane
import forks.splitPane.dSplitter
import groups.GroupsScreen
import home.HomeStore
import io.github.alexzhirkevich.compottie.CompottieException
import io.github.alexzhirkevich.compottie.LottieCancellationBehavior
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.animateLottieCompositionAsState
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import journal.JournalStore
import kotlinx.datetime.Clock
import kotlinx.datetime.toLocalDateTime
import mentoring.MentoringComponent
import mentoring.MentoringContent
import resources.Images
import resources.RIcons
import root.RootComponent
import root.RootComponent.Child.AdminAchievements
import root.RootComponent.Child.AdminCabinets
import root.RootComponent.Child.AdminCalendar
import root.RootComponent.Child.AdminGroups
import root.RootComponent.Child.AdminParents
import root.RootComponent.Child.AdminSchedule
import root.RootComponent.Child.AdminUsers
import root.RootComponent.Child.AuthActivation
import root.RootComponent.Child.AuthLogin
import root.RootComponent.Child.ErrorLoad
import root.RootComponent.Child.HomeAchievements
import root.RootComponent.Child.HomeAllGroupMarks
import root.RootComponent.Child.HomeDetailedStups
import root.RootComponent.Child.HomeDnevnikRuMarks
import root.RootComponent.Child.HomeProfile
import root.RootComponent.Child.HomeSettings
import root.RootComponent.Child.HomeStudentLines
import root.RootComponent.Child.HomeTasks
import root.RootComponent.Child.LessonReport
import root.RootComponent.Child.MainAdmin
import root.RootComponent.Child.MainHome
import root.RootComponent.Child.MainJournal
import root.RootComponent.Child.MainMentoring
import root.RootComponent.Child.MainRating
import root.RootComponent.Child.MainSchool
import root.RootComponent.Child.QRScanner
import root.RootComponent.Child.SchoolFormRating
import root.RootComponent.Child.SchoolMinistry
import root.RootComponent.Child.SecondView
import root.RootComponent.Config
import root.store.RootStore
import school.SchoolComponent
import school.SchoolStore
import server.Moderation
import server.Roles
import server.cut
import server.getDate
import transitions.iosSlide
import transitions.slideEnterModifier
import transitions.slideExitModifier
import view.LocalViewManager
import view.ViewManager
import view.WindowCalculator
import view.WindowScreen

@ExperimentalAnimationApi
@OptIn(
    ExperimentalLayoutApi::class, ExperimentalDecomposeApi::class,
    ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class,
    ExperimentalHazeApi::class, ExperimentalSplitPaneApi::class
)
@ExperimentalFoundationApi
@Composable
fun RootContent(
    component: RootComponent,
//    isJs: Boolean = false
) {
    val viewManager = LocalViewManager.current
    val childStack by component.childStack.subscribeAsState()
    val stack by component.childStack.subscribeAsState()
    val isNewVersionDialogShowing = remember { mutableStateOf(true) }
    val model by component.model.subscribeAsState()
    val nCheckModel by component.checkNInterface.networkModel.subscribeAsState()

    DeepLinkErrorCatcher(
        component,
//        model = model
    )
//    val isExpanded =
//        viewManager.orientation.value == WindowScreen.Expanded
//    val isVertical = viewManager.orientation.value == WindowScreen.Vertical


    BoxWithConstraints {
        val calculatedScreen = WindowCalculator.calculateScreen(
            size = DpSize(
                this.maxWidth,
                this.maxHeight
            )
        )
        val isExpanded =
            (viewManager.isLockedVerticalView.value != true) &&
                    if (component.secondLogin == null) viewManager.orientation.value == WindowScreen.Expanded
                    else calculatedScreen == WindowScreen.Expanded
        val isVertical =
            (viewManager.isLockedVerticalView.value == true) ||
                    if (component.secondLogin == null) viewManager.orientation.value == WindowScreen.Vertical
                    else calculatedScreen == WindowScreen.Vertical



        val items = getNavItems(
            isExpanded,
            component,
            model,
            childStack
        )


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
                            viewManager, component, childStack, items
                        )
//                    bottomBarAnimationScope = this
                    }
                }
            }
        ) { padding ->

            val aniPadding by animateDpAsState(
                if (component.secondLogin == null) {
                    if ((!isVertical) && childStack.active.configuration !is Config.AuthLogin && childStack.active.configuration !is Config.AuthActivation
                    ) 80.dp else 0.dp
                } else {
                    0.dp
                }
            )
            SharedTransitionLayout(
                modifier = Modifier.fillMaxSize()

                    .padding(
                        top = 0.dp,
                        start = 0.dp,// padding.calculateStartPadding(LocalLayoutDirection.current),
                        end = 0.dp,//padding.calculateEndPadding(LocalLayoutDirection.current),
                        bottom = 0.dp// if (model.isBottomBarShowing) padding.calculateBottomPadding() else (padding.calculateBottomPadding() - 80.dp).coerceAtLeast(
                        //                    0.dp
                    )
            )
            {
                val currentChild = mutableStateOf<Child<Any, RootComponent.Child>?>(null)
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
                                    is MainJournal -> fade()
                                    is MainSchool -> fade()
                                    is MainHome -> fade()
                                    is MainAdmin -> fade()
                                    is MainMentoring -> fade()

                                    is LessonReport -> if (currentChild.value?.instance is LessonReport) fade(
                                        tween(700)
                                    ) else iosSlide()

                                    is HomeSettings -> iosSlide()
                                    is AdminSchedule -> iosSlide()
                                    is QRScanner -> iosSlide()

                                    is HomeProfile -> fade()
                                    is HomeAchievements -> fade()
                                    else -> if (isExpanded) fade() else iosSlide()// + fade()
                                }
                            } else {
                                null
                            }
                        },
                        selector = { initialBackEvent, exit, enter ->
                            val shape = RoundedCornerShape(
                                size = 40.dp
                            )
                            if (
                                initialBackEvent.swipeEdge == BackEvent.SwipeEdge.RIGHT ||
                                (
                                        exit.instance is MainAdmin ||
                                                exit.instance is MainHome ||
                                                exit.instance is MainJournal ||
                                                exit.instance is MainMentoring ||
                                                exit.instance is MainSchool
                                        )
                            ) {

                                materialPredictiveBackAnimatable(
                                    initialBackEvent = initialBackEvent,
                                    shape = { progress, edge ->
                                        shape
                                    })

                            } else {
                                predictiveBackAnimatable(
                                    initialBackEvent = initialBackEvent,
                                    exitModifier = { progress, _ ->
                                        Modifier

                                            .slideExitModifier(progress = progress)
                                    },
                                    enterModifier = { progress, _ ->
                                        Modifier

                                            .slideEnterModifier(
                                                progress = progress
                                            )
                                    },
                                )
                            }

                        },

                        )//backAnimation(component)
                ) {

                    when (val child = it.instance) {

                        is ErrorLoad -> {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(child.reason, textAlign = TextAlign.Center)
                                    Spacer(Modifier.height(35.dp))
                                    CTextButton(
                                        "Перейти на главный экран"
                                    ) {
                                        component.onOutput(RootComponent.Output.NavigateToHome)
                                    }
                                }
                            }
                        }

                        is AuthLogin -> LoginContent(child.component)
                        is AuthActivation -> ActivationContent(child.component)


                        is MainHome -> {
                            MultiPaneSplit(
                                isExpanded = isExpanded,
                                viewManager = viewManager,
                                currentScreen = {
                                    HomeContent(
                                        child.homeComponent,
                                        sharedTransitionScope = this@SharedTransitionLayout,
                                        isSharedVisible = stack.active.instance is MainHome
                                    )
                                },
                                firstScreen = {
                                    HomeContent(
                                        child.homeComponent,
                                        sharedTransitionScope = this@SharedTransitionLayout,
                                        isSharedVisible = stack.active.instance is MainHome
                                    )
                                },
                                secondScreen = {
                                    if ((model.moderation != Moderation.NOTHING || model.role == Roles.TEACHER && component.isMentoring == null
                                                ) && component.secondLogin == null
                                    ) {
                                        JournalContent(
                                            child.journalComponent,
                                            role = model.role,
                                            moderation = model.moderation,
                                            onRefresh = {
                                                child.journalComponent.onEvent(JournalStore.Intent.Refresh)
                                                child.homeComponent.onRefreshClick()
                                            }
                                        )
                                    } else {
                                        RatingContent(
                                            child.ratingComponent,
                                            isSharedVisible = stack.active.instance is MainRating
                                        ) {
                                            child.homeComponent.onRefreshClick()
                                        }
                                    }
                                }
                            )
                        }

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
                                    isSharedVisible = stack.active.instance is MainHome
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

                        is HomeDnevnikRuMarks -> {
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
                                        isSharedVisible = stack.active.instance is MainHome,
                                        currentRouting = HomeRoutings.Dnevnik
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

                        is HomeStudentLines -> {
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
                                        isSharedVisible = stack.active.instance is MainHome
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

                        is HomeDetailedStups -> {
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
                                        isSharedVisible = stack.active.instance is MainHome
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

                        is QRScanner -> {
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
                                            path = RIcons.MAGIC_WAND
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
                            UsersScreen(
                                component = child.usersComponent,
                                isExpanded = isExpanded
                            ) {
                                AdminContent(
                                    component = child.adminComponent,
                                    isActive = false,
                                    currentRouting = AdminComponent.Output.NavigateToUsers
                                )
                            }

                        is AdminGroups ->
                            GroupsScreen(
                                component = child.groupsComponent,
                                isExpanded = isExpanded
                            ) {
                                AdminContent(
                                    component = child.adminComponent,
                                    isActive = false,
                                    currentRouting = AdminComponent.Output.NavigateToGroups
                                )
                            }

                        is LessonReport ->
                            LessonReportScreen(
                                child.lessonReport,
                                isExpanded = isExpanded
                            ) {
                                JournalContent(
                                    component = child.journalComponent,
                                    role = model.role,
                                    moderation = model.moderation,
                                    isNotMinimized = false,
                                    onRefresh = {}
                                )
                            }

                        is HomeSettings ->
                            SettingsContent(
                                isExpanded,
                                child.settingsComponent
                            )

                        is HomeAllGroupMarks -> {
                            DisposableEffect(Unit) {

                                viewManager.splitPaneState.dispatchRawMovement(30000f)
                                onDispose {
                                    viewManager.splitPaneState.dispatchRawMovement((0.5f - viewManager.splitPaneState.positionPercentage) * 1500)

                                }
                            }

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
                        }

                        is AdminSchedule -> ScheduleContent(child.scheduleComponent)
                        is HomeProfile -> MultiPaneSplit(
                            isExpanded = isExpanded,
                            viewManager = viewManager,
                            currentScreen = {
                                ProfileContent(
                                    child.profileComponent,
                                    isSharedVisible = stack.active.instance is HomeProfile
                                )
                            },
                            firstScreen = {
                                HomeContent(
                                    child.homeComponent,
                                    sharedTransitionScope = this@SharedTransitionLayout,
                                    isSharedVisible = stack.active.instance is MainHome
                                )
                            },
                            secondScreen = {
                                ProfileContent(
                                    child.profileComponent,
                                    isSharedVisible = stack.active.instance is HomeProfile
                                )
                            }
                        )

                        is AdminCabinets -> MultiPaneAdmin(
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
                                        isSharedVisible = stack.active.instance is MainRating
                                    ) {
                                        if (isExpanded) {
                                            child.schoolComponent.onEvent(SchoolStore.Intent.Init)
                                        }
                                    }
                                }
                            )


                        is HomeTasks -> {
                            val confettiIsPlaying = remember { mutableStateOf(false) }

                            val confettiComposition = rememberLottieComposition {
                                LottieCompositionSpec.JsonString(
                                    Images.confetti()
                                )
                            }

                            LaunchedEffect(confettiComposition) {
                                try {
                                    confettiComposition.await()
                                } catch (t: CompottieException) {
                                    t.printStackTrace()
                                }
                            }
                            val confettiProgress by animateLottieCompositionAsState(
                                confettiComposition.value,
                                isPlaying = confettiIsPlaying.value,
                                cancellationBehavior = LottieCancellationBehavior.OnIterationFinish,
                                iterations = 1
                            )

                            LaunchedEffect(confettiProgress > 0) {
                                confettiIsPlaying.value = false
                            }

                            val confettiPainter = rememberLottiePainter(
                                composition = confettiComposition.value,
                                progress = { confettiProgress },
                            )


                            MultiPaneSplit(
                                isExpanded = isExpanded,
                                viewManager = viewManager,
                                currentScreen = {
                                    HomeTasksContent(
                                        child.homeTasksComponent
                                    ) {
                                        if (confettiProgress in listOf(0.0f, 1.0f)) {
                                            confettiIsPlaying.value = true
                                        }

                                    }
                                },
                                firstScreen = {
                                    HomeContent(
                                        child.homeComponent,
                                        sharedTransitionScope = this@SharedTransitionLayout,
                                        isSharedVisible = stack.active.instance is MainHome,
                                        currentRouting = HomeRoutings.Tasks
                                    )
                                },
                                secondScreen = {
                                    HomeTasksContent(
                                        child.homeTasksComponent
                                    ) {
                                        if (confettiProgress in listOf(0.0f, 1.0f)) {
                                            confettiIsPlaying.value = true
                                        }

                                    }
                                }
                            )
                            Image(
                                painter = confettiPainter,
                                contentDescription = "Lottie animation",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        is AdminCalendar -> MultiPaneAdmin(
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
                            viewManager = viewManager,
                            mentoringComponent = child.mentoringComponent,
                            rootComponent = null
                        )

                        is SecondView ->
                            if (child.isMentoring) {
                                MultiPaneMentoring(
                                    isExpanded,
                                    viewManager = viewManager,
                                    mentoringComponent = child.mentoringComponent,
                                    rootComponent = child.rootComponent
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
                                            isSharedVisible = stack.active.instance is MainHome
                                        )
                                    },
                                    secondScreen = {
                                        RootContent(child.rootComponent)
                                    }
                                )
                            }


                        is AdminAchievements -> {
                            val previousScreen =
                                stack.items.getOrNull(stack.items.size - 2)?.instance
                            if (previousScreen is MainMentoring || stack.active.instance is MainMentoring) {
                                AdminAchievementsScreen(
                                    component = child.adminAchievementsComponent,
                                    isExpanded = false
                                ) {}
                            } else {
                                AdminAchievementsScreen(
                                    component = child.adminAchievementsComponent,
                                    isExpanded = isExpanded
                                ) {
                                    AdminContent(
                                        component = child.adminComponent,
                                        isActive = false,
                                        currentRouting = AdminComponent.Output.NavigateToAchievements
                                    )
                                }
                            }
                        }

                        is HomeAchievements ->
                            MultiPaneSplit(
                                isExpanded = isExpanded,
                                viewManager = viewManager,
                                currentScreen = {
                                    HomeAchievementsContent(
                                        child.achievementsComponent,
                                        isVisible = stack.active.instance is HomeAchievements
                                    )
                                },
                                firstScreen = {
                                    HomeContent(
                                        child.homeComponent,
                                        sharedTransitionScope = this@SharedTransitionLayout,
                                        isSharedVisible = stack.active.instance is MainHome
                                    )
                                },
                                secondScreen = {
                                    HomeAchievementsContent(
                                        child.achievementsComponent,
                                        isVisible = stack.active.instance is HomeAchievements
                                    )
                                }
                            )

                        is AdminParents ->
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
                                    isSharedVisible = stack.active.instance is MainRating
                                ) {
                                    child.schoolComponent.onEvent(SchoolStore.Intent.Init)
                                }
                            }
                        )

                        is SchoolFormRating -> MultiPaneSchool(
                            isExpanded = isExpanded,
                            schoolComponent = child.schoolComponent,
                            currentRouting = SchoolRoutings.FormRating,
                            viewManager = viewManager,
                            secondScreen = {
                                FormRatingContent(
                                    child.formRatingComponent,
                                    isVisible = stack.active.instance is SchoolFormRating
                                )
                            }
                        )

                        is SchoolMinistry -> MultiPaneSchool(
                            isExpanded = isExpanded,
                            schoolComponent = child.schoolComponent,
                            currentRouting = SchoolRoutings.Ministry,
                            viewManager = viewManager,
                            secondScreen = {
                                MinistryContent(
                                    child.ministryComponent,
                                    isVisible = stack.active.instance is SchoolMinistry
                                )
                            }
                        )
                    }

                    currentChild.value = it
                }
                if (component.secondLogin == null) {
                    CustomNavigationRail(isVertical, component, childStack, items)
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
                                    path = RIcons.GIFT,
                                    size = 100.dp
                                )
                                Spacer(Modifier.height(20.dp))
                            }
                        }
                        Text(
                            when (Clock.System.now().toLocalDateTime(applicationTimeZone).hour) {
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
                            text = if (viewManager.hardwareStatus.value.isBlank()) applicationVersionString else viewManager.hardwareStatus.value,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Black,
                            fontSize = 10.esp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = .5f)
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
                                    DefaultErrorView(
                                        nCheckModel,
                                        text = if (nCheckModel.error != "") nCheckModel.error else "Загрузка..."
                                    )
                                    Spacer(Modifier.height(7.dp))
                                    CTextButton(text = "Продолжить без синхронизации") {
                                        component.checkNInterface.nSuccess()
                                        component.onOutput(RootComponent.Output.NavigateToHome)
                                    }
                                }

                                it is NetworkState.None && !model.isTokenValid -> {
                                    Text("Ваш токен недействителен!")
                                    Spacer(Modifier.height(7.dp))
                                    CTextButton(text = "Перезайти в аккаунт") {
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
                CTextButton(
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
                isMasked = false
            ).hazeUnder(
                viewManager,
                zIndex = 10f
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
//    isVisible: Boolean
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
                        val modelNotNull by mentoringComponent!!.model.subscribeAsState()
                        val fio =
                            modelNotNull.students.firstOrNull { it.login == modelNotNull.chosenLogin }?.fio
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

@OptIn(ExperimentalSplitPaneApi::class)
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

