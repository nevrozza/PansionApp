import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.*
import components.listDialog.ListDialogStore
import components.networkInterface.NetworkState
import decomposeComponents.listDialogComponent.ListDialogDesktopContent
import decomposeComponents.listDialogComponent.ListDialogMobileContent
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.LocalHazeStyle
import dev.chrisbanes.haze.hazeChild
import ministry.MinistryComponent
import ministry.MinistryStore
import server.Ministries
import view.LocalViewManager


private val headerTitles = mapOf(
    "0" to "...",
    Ministries.MVD to "МВД",
    Ministries.Culture to "Культура",
    Ministries.DressCode to "Здравоохранение",
    Ministries.Education to "Образование",
    Ministries.Print to "Печать",
    Ministries.Social to "Соц опрос",
    Ministries.Sport to "Спорт",
)

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SharedTransitionScope.MinistryContent(
    component: MinistryComponent,
    isVisible: Boolean
) {
    val model by component.model.subscribeAsState()
    val nModel by component.nInterface.networkModel.subscribeAsState()
//    val coroutineScope = rememberCoroutineScope()
//    val focusManager = LocalFocusManager.current
    val viewManager = LocalViewManager.current
//    val scrollState = rememberScrollState()
//    val imeState = rememberImeState()
    val lazyListState = rememberLazyListState()
    val hazeState = remember { HazeState() }

    //PullToRefresh
//    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    Scaffold(
        Modifier.fillMaxSize(),
//                .nestedScroll(scrollBehavior.nestedScrollConnection)
        topBar = {
            val isHaze = viewManager.hazeHardware.value
            Column(
                Modifier.then(
                    if (isHaze) Modifier.hazeChild(
                        state = hazeState,
                        style = LocalHazeStyle.current
                    ) {
                        progressive = view.hazeProgressive
                    }
                    else Modifier
                )
            ) {
                AppBar(
                    containerColor = if (isHaze) Color.Transparent else MaterialTheme.colorScheme.surface,
                    navigationRow = {
                        IconButton(
                            onClick = { component.onOutput(MinistryComponent.Output.Back) }
                        ) {
                            Icon(
                                Icons.Rounded.ArrowBackIosNew, null
                            )
                        }
                    },
                    title = {
                        AnimatedContent(
                            if (model.isMultiMinistry == true && model.pickedMinistry == "0") "Выберите"
                            else headerTitles[model.pickedMinistry].toString(),
                            modifier = Modifier.sharedElementWithCallerManagedVisibility(
                                sharedContentState = rememberSharedContentState(key = "Ministry"),
                                visible = isVisible
                            )
                        ) { text ->
                            Box(contentAlignment = Alignment.BottomEnd) {
                                Text(
                                    text,
                                    fontSize = 25.sp,
                                    fontWeight = FontWeight.Black,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.then(
                                        if (model.isMultiMinistry == true) {
                                            Modifier.cClickable {
                                                component.ministriesListComponent.onEvent(ListDialogStore.Intent.ShowDialog)
                                            }
                                        } else {
                                            Modifier
                                        }
                                    )
                                )
                                ListDialogDesktopContent(
                                    component = component.ministriesListComponent
                                )
                            }
                        }

                    },
                    isTransparentHaze = isHaze,
                    isHazeActivated = true,
                    hazeState = hazeState
                )
                DatesLine(
                    dates = model.dates.reversed(),
                    currentDate = model.currentDate,
                    firstItemWidth = 30.dp
                ) {
                    component.onEvent(MinistryStore.Intent.ChangeDate(it))
                }
                Text(
                    text = "Выставлять ступени в конце дня!",
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

            }

            //LessonReportTopBar(component, isFullView) //, scrollBehavior
        }
    ) { padding ->
        Column(Modifier.fillMaxSize()) {
            Crossfade(nModel.state) { state ->
                when (state) {
                    NetworkState.None -> CLazyColumn(padding = padding, state = lazyListState, hazeState = hazeState) {
//                        items(items = model.dates) { i, date ->
//                            DateTasksItem(
//                                date = date,
//                                tasks = model.homeTasks.filter { it.date == date },
//                                groups = model.groups,
//                                subjects = model.subjects,
//                                component = component,
//                                model = model
//                            )
//                            if (i == model.dates.size - 1) {
//                                Spacer(Modifier.height(100.dp))
//                            }
//                        }
                    }

                    NetworkState.Loading -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }

                    NetworkState.Error -> {
                        Column(
                            Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(nModel.error)
                            Spacer(Modifier.height(7.dp))
                            CustomTextButton("Попробовать ещё раз") {
                                nModel.onFixErrorClick()
                            }
                        }
                    }
                }
            }
        }
    }

    ListDialogMobileContent(
        component = component.ministriesListComponent,
        title = "Министерства",
        hazeState = null
    )
}