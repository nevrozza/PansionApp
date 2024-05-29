@file:OptIn(ExperimentalSplitPaneApi::class)

import admin.AdminComponent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
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
import androidx.compose.foundation.clipScrollableContainer
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.EditCalendar
import androidx.compose.material.icons.rounded.Fax
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LibraryBooks
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.NativeKeyEvent
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import animations.iosSlide
import animations.slideEnterModifier
import animations.slideExitModifier
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.androidPredictiveBackAnimatable
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.predictiveBackAnimatable
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.predictiveBackAnimation
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.AlphaTestZatichka
import components.ThemePreview
import components.onBackButtonClicked
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeChild
import dev.chrisbanes.haze.materials.HazeMaterials
import forks.splitPane.ExperimentalSplitPaneApi
import forks.splitPane.HorizontalSplitPane
import forks.splitPane.SplitPaneScope
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
import root.RootComponent.Child.HomeSettings
import root.RootComponent.Child.MainRating

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
    val isExpanded =
        viewManager.orientation.value == WindowScreen.Expanded
    val items = listOf<NavigationItem?>(
        NavigationItem(
            icon = Icons.Rounded.Home,
            label = "Главная",
            category = if (isExpanded && model.currentCategory == RootComponent.RootCategories.Journal) RootComponent.RootCategories.Journal
            else if (isExpanded && model.currentCategory == RootComponent.RootCategories.Rating && model.role != Roles.teacher) RootComponent.RootCategories.Rating
            else RootComponent.RootCategories.Home,
            onClickOutput = RootComponent.Output.NavigateToHome
        ),
        if (!isExpanded && model.role == Roles.teacher) NavigationItem(
            icon = Icons.Rounded.LibraryBooks,
            label = "Журнал",
            category = RootComponent.RootCategories.Journal,
            onClickOutput = RootComponent.Output.NavigateToJournal
        ) else null,
        if (model.moderation in listOf(
                Moderation.moderator,
                Moderation.mentor,
                Moderation.both
            )
        ) NavigationItem(
            icon = Icons.Rounded.GridView,
            label = "Модерация",
            category = RootComponent.RootCategories.Admin,
            onClickOutput = RootComponent.Output.NavigateToAdmin
        ) else null,
        if ((!isExpanded && model.role != Roles.teacher) || model.role == Roles.teacher) NavigationItem(
            icon = Icons.Rounded.Star,
            label = "Рейтинг",
            category = RootComponent.RootCategories.Rating,
            onClickOutput = RootComponent.Output.NavigateToRating
        ) else null,
    )
//    var bottomBarAnimationScope: AnimatedVisibilityScope? = null
    Scaffold(
        Modifier.fillMaxSize(),
        bottomBar = {
            Box(Modifier.animateContentSize().fillMaxWidth()) {
                AnimatedVisibility(

                    visible = model.isBottomBarShowing && (viewManager.orientation.value == WindowScreen.Vertical),
                    enter = fadeIn(animationSpec = tween(300)) +
                            slideInVertically { it },
                    exit = fadeOut(animationSpec = tween(300)) + slideOutVertically { it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CustomNavigationBar(
                        viewManager, component, model, items
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
                if ((isExpanded || viewManager.orientation.value == WindowScreen.Horizontal) && model.currentScreen !in listOf(
                        RootComponent.Config.AuthActivation,
                        RootComponent.Config.AuthLogin
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
//                            is AdminMentors -> if (isExpanded) fade() else slide()
                            is AdminUsers -> if (isExpanded) fade() else iosSlide()
                            is AdminGroups -> if (isExpanded) fade() else iosSlide()
                            is LessonReport -> iosSlide() //if (isExpanded) fade() else slide()
                            is HomeSettings -> iosSlide()//if (isExpanded) fade() else slide()
//                            else -> slide()
                            is Child.AuthActivation -> if (isExpanded) fade() else iosSlide()
                            is Child.AuthLogin -> if (isExpanded) fade() else iosSlide()
                            is Child.HomeDnevnikRuMarks -> if (isExpanded) fade() else iosSlide()
                            is Child.HomeDetailedStups -> if (isExpanded) fade() else iosSlide()
                            is Child.HomeAllGroupMarks -> if (isExpanded) fade() else iosSlide()
                            is Child.AdminSchedule -> iosSlide()
                            is Child.HomeProfile -> TODO()
                            is Child.AdminCabinets -> if (isExpanded) fade() else iosSlide()
                        }
                    },
                    selector = { initialBackEvent, _, _ ->
                        predictiveBackAnimatable(
                            initialBackEvent = initialBackEvent,
                            exitModifier = { progress, _ -> Modifier.slideExitModifier(progress = progress) },
                            enterModifier = { progress, _ -> Modifier.slideEnterModifier(progress = progress) },
                        )
                    },
                )
            ) {
                when (val child = it.instance) {
                    is Child.AuthLogin -> LoginContent(child.component)
                    is Child.AuthActivation -> ActivationContent(child.component)


                    is MainHome -> MultiPaneSplit(
                        isExpanded = isExpanded,
                        currentScreen = { HomeContent(child.homeComponent) },
                        firstScreen = { HomeContent(child.homeComponent) },
                        secondScreen = {
                            if (model.moderation != Moderation.nothing || model.role == Roles.teacher) {
                                JournalContent(
                                    child.journalComponent,
                                    role = model.role,
                                    moderation = model.moderation,
                                    onRefresh = { child.homeComponent.onEvent(HomeStore.Intent.Init) }
                                )
                            } else {
                                RatingContent(child.ratingComponent)
                            }
                        }
                    )

                    is MainJournal -> MultiPaneSplit(
                        isExpanded = isExpanded,
                        currentScreen = {
                            JournalContent(
                                child.journalComponent,
                                role = model.role,
                                moderation = model.moderation,
                                onRefresh = { child.homeComponent.onEvent(HomeStore.Intent.Init) }
                            )
                        },
                        firstScreen = { HomeContent(child.homeComponent) },
                        secondScreen = {
                            JournalContent(
                                child.journalComponent,
                                role = model.role,
                                moderation = model.moderation,
                                onRefresh = { child.homeComponent.onEvent(HomeStore.Intent.Init) }
                            )
                        }
                    )

                    is Child.HomeDnevnikRuMarks -> {
                        MultiPaneSplit(
                            isExpanded = isExpanded,
                            currentScreen = { DnevnikRuMarkContent(child.dnevnikRuMarksComponent) },
                            firstScreen = { HomeContent(child.homeComponent) },
                            secondScreen = { DnevnikRuMarkContent(child.dnevnikRuMarksComponent) }
                        )
                    }

                    is Child.HomeDetailedStups -> {
                        MultiPaneSplit(
                            isExpanded = isExpanded,
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
                            adminComponent = child.adminComponent,
                            currentRouting = AdminComponent.Output.NavigateToUsers,
                            secondScreen = { UsersContent(child.usersComponent) }
                        )

                    is AdminGroups ->
                        MultiPaneAdmin(
                            isExpanded,
                            adminComponent = child.adminComponent,
                            currentRouting = AdminComponent.Output.NavigateToGroups,
                            secondScreen = { GroupsContent(child.groupsComponent) }
                        )

//                    is Child.AdminStudents -> TODO()
                    is LessonReport ->
                        MultiPaneJournal(
                            isExpanded,
                            journalComponent = child.journalComponent,
                            currentReportId = 0, role = model.role, moderation = model.moderation,
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
                            currentScreen = { AllGroupMarksContent(child.allGroupMarksComponent) },
                            firstScreen = { AllGroupMarksContent(child.allGroupMarksComponent) },
                            secondScreen = {
                                JournalContent(
                                    child.journalComponent,
                                    role = model.role,
                                    moderation = model.moderation,
                                    onRefresh = {  }
                                )
                            }
                        )

                    is Child.AdminSchedule -> ScheduleContent(child.scheduleComponent)
                    is Child.HomeProfile -> TODO()
                    is Child.AdminCabinets -> MultiPaneAdmin(
                        isExpanded,
                        adminComponent = child.adminComponent,
                        currentRouting = AdminComponent.Output.NavigateToCabinets,
                        secondScreen = { CabinetsContent(child.cabinetsComponent) }
                    )

                    is MainRating -> MultiPaneSplit(
                        isExpanded = isExpanded,
                        currentScreen = { RatingContent(child.ratingComponent) },
                        firstScreen = { HomeContent(child.homeComponent) },
                        secondScreen = {
                            RatingContent(child.ratingComponent)
                        }
                    )
                }
            }
            CustomNavigationRail(viewManager, component, model, items)
        }
    }

    if (!isJs) {
        val time = 600
        AnimatedVisibility(
            model.isGreetingsShowing,
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
                Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    when (Clock.System.now().toLocalDateTime(TimeZone.of("UTC+3")).hour) {
                        in 5..10 -> "Доброе утро!"
                        in 11..18 -> "Добрый день!"
                        in 19..21 -> "Добрый вечер!"
                        else -> "Доброй ночи!"
                    },
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Black
                )
            }
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
    secondScreen: @Composable () -> Unit
) {

    val isFullScreen = remember { mutableStateOf(true) }

    if (isExpanded) {
        val splitterState = rememberSplitPaneState()

        LaunchedEffect(isFullScreen.value) {
            if (isFullScreen.value) {
                splitterState.dispatchRawMovement(-30000f)
            }
        }
        val x = animateDpAsState(if (isFullScreen.value) 0.dp else 250.dp)
        HorizontalSplitPane(
            splitPaneState = splitterState
        ) {
            first(minSize = x.value) {
                JournalContent(
                    journalComponent,
                    isNotMinimized = false,
                    role = role,
                    moderation = moderation,
                    onRefresh = {  }
                )
            }

            dSplitter(isFullScreen)


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
    secondScreen: @Composable () -> Unit
) {
    if (isExpanded) {
        val splitterState = rememberSplitPaneState()
        HorizontalSplitPane(
            splitPaneState = splitterState
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
    currentScreen: @Composable () -> Unit,
    firstScreen: @Composable () -> Unit,
    secondScreen: @Composable () -> Unit
) {
    if (isExpanded) {
        val splitterState = rememberSplitPaneState(.5f)
        HorizontalSplitPane(
            splitPaneState = splitterState
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
    items: List<NavigationItem?>
) {


    NavigationBar(
        modifier = Modifier.then(
            if (viewManager.hazeStyle != null) Modifier.hazeChild(
                viewManager.hazeState,
                style = viewManager.hazeStyle!!.value
            ) else Modifier
        ).fillMaxWidth(),
        containerColor = Color.Transparent
    ) {
        items.filterNotNull().forEach { item ->
            NavigationBarItem(
                selected = model.currentCategory == item.category,
                onClick = { component.onOutput(item.onClickOutput) },
                icon = { Icon(item.icon, null) },
                label = { Text(item.label) }
            )
        }
    }


}

@Composable
fun CustomNavigationRail(
    viewManager: ViewManager,
    component: RootComponent,
    model: RootStore.State,
    items: List<NavigationItem?>
) {
    AnimatedVisibility(
        visible = viewManager.orientation.value != WindowScreen.Vertical && model.currentScreen !in listOf(
            RootComponent.Config.AuthActivation,
            RootComponent.Config.AuthLogin
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
                        selected = model.currentCategory == item.category,
                        onClick = { component.onOutput(item.onClickOutput) },
                        icon = { Icon(item.icon, null) },
                        label = { Text(item.label) }
                    )
                }
            }

        }
    }
}