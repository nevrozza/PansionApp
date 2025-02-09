import androidx.compose.animation.*
import androidx.compose.animation.core.EaseInQuart
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.router.stack.ChildStack
import components.GetAsyncIcon
import resources.RIcons
import root.RootComponent
import root.RootComponent.Child
import root.store.RootStore
import server.Moderation
import server.Roles
import root.RootComponent.RootCategories.*
import root.RootComponent.Config
import androidx.compose.desktop.ui.tooling.preview.utils.GlobalHazeState
import view.ViewManager
import androidx.compose.desktop.ui.tooling.preview.utils.easedVerticalGradient
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import components.foundation.hazeUnder
import dev.chrisbanes.haze.*


data class NavigationItem(
    val iconPath: String,
    val label: String,
    val category: RootComponent.RootCategories,
    val onClickOutput: RootComponent.Output,
    val size: Dp = 22.dp
)

fun getNavItems(
    isExpanded: Boolean,
    component: RootComponent,
    model: RootStore.State,
    childStack: ChildStack<*, Child>
): List<NavigationItem?> {
    val items = listOf<NavigationItem?>(
        NavigationItem(
            iconPath = RIcons.HOME,
            label = "Главная",
            category = if (isExpanded && getCategory(childStack.active.configuration as Config) == Journal) Journal
            else Home,
            onClickOutput = RootComponent.Output.NavigateToHome
        ),
        if (!isExpanded && (model.role == Roles.TEACHER || model.moderation in listOf(
                Moderation.MODERATOR,
                Moderation.MENTOR,
                Moderation.BOTH
            )) && component.isMentoring == null
        ) NavigationItem(
            iconPath = RIcons.BOOK,//Icons.AutoMirrored.Rounded.LibraryBooks,
            label = "Журнал",
            size = 20.dp,
            category = Journal,
            onClickOutput = RootComponent.Output.NavigateToJournal
        ) else null,
        NavigationItem(
            iconPath = RIcons.SCHOOL,//Icons.Rounded.Token,
            size = 24.dp,
            label = "Пансион",
            category = School,
            onClickOutput = RootComponent.Output.NavigateToSchool
        ),
        if (model.moderation != Moderation.NOTHING
            && component.isMentoring == null
        ) NavigationItem(
            iconPath = RIcons.GROUP,//Icons.Rounded.Diversity1,
            label = "Ученики",
            size = 24.dp,
            category = Mentoring,
            onClickOutput = RootComponent.Output.NavigateToMentoring
        ) else null,
        if (model.moderation in listOf(
                Moderation.MODERATOR,
                Moderation.BOTH
            ) && component.isMentoring == null
        ) NavigationItem(
            iconPath = RIcons.SOVIET_SETTINGS, //Icons.Rounded.GridView,
            label = "Админ",
            category = Admin,
            size = 20.dp,
            onClickOutput = RootComponent.Output.NavigateToAdmin
        ) else null,
    )
    return items
}

fun isBottomBarShowing(config: Config): Boolean {
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

@OptIn(ExperimentalHazeApi::class)
@Composable
fun CustomNavigationBar(
    viewManager: ViewManager,
    component: RootComponent,
    childStack: ChildStack<*, Child>,
    items: List<NavigationItem?>
) {
    NavigationBar(
        modifier = Modifier.then(
            if (viewManager.hazeHardware.value) Modifier.hazeEffect(
                GlobalHazeState.current,
                style = LocalHazeStyle.current
            ) {
                inputScale = HazeInputScale.Fixed(0.7f)
//                this.blurRadius = 525.dp
                mask = Brush.easedVerticalGradient(EaseInQuart, isReversed = true)
//                    Color.Transparent, Color.Transparent,
//                    Color.Magenta, Color.Magenta, Color.Magenta))
//                progressive = view.hazeProgressive.copy(endIntensity = 1f, startIntensity = 0f)
            }.hazeUnder(
                viewManager,
                zIndex = 1f
            )
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
        NavigationRail {
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

