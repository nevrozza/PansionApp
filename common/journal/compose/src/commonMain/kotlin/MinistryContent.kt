import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.*
import components.listDialog.ListDialogStore
import components.networkInterface.NetworkState
import decomposeComponents.CAlertDialogContent
import decomposeComponents.listDialogComponent.ListDialogDesktopContent
import decomposeComponents.listDialogComponent.ListDialogMobileContent
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.LocalHazeStyle
import dev.chrisbanes.haze.hazeChild
import ministry.MinistryComponent
import ministry.MinistryStore
import resources.RIcons
import server.headerTitlesForMinistry
import view.LocalViewManager
import view.esp


@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SharedTransitionScope.MinistryContent(
    component: MinistryComponent,
    isVisible: Boolean
) {
    val model by component.model.subscribeAsState()
    val nModel by component.nInterface.networkModel.subscribeAsState()
    val nUploadModel by component.nUploadInterface.networkModel.subscribeAsState()

    val viewManager = LocalViewManager.current

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
                        mask = view.hazeMask//Brush.verticalGradient(colors = listOf(Color.Magenta, Color.Transparent))
//                        progressive = view.hazeProgressive
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
                            GetAsyncIcon(
                                path = RIcons.ChevronLeft
                            )
                        }
                    },
                    title = {
                        AnimatedContent(
                            if (model.isMultiMinistry == true && model.pickedMinistry == "0") "Выберите"
                            else headerTitlesForMinistry[model.pickedMinistry].toString(),
                            modifier = Modifier.sharedElementWithCallerManagedVisibility(
                                sharedContentState = rememberSharedContentState(key = "Ministry"),
                                visible = isVisible
                            )
                        ) { text ->
                            Box(contentAlignment = Alignment.BottomEnd) {
                                Text(
                                    text,
                                    fontSize = MaterialTheme.typography.headlineSmall.fontSize,
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
                                    component = component.ministriesListComponent,
                                    isFullHeight = true
                                )
                            }
                        }

                    },
                    actionRow = {
                        AnimatedVisibility(
                            nUploadModel.state != NetworkState.None,
                            enter = fadeIn(animationSpec = tween(300)) +
                                    slideInHorizontally { it },
                            exit = fadeOut(animationSpec = tween(300)) + slideOutHorizontally { it }
                        ) {
                            Row {
                                Crossfade(
                                    nUploadModel.state,
                                    modifier = Modifier.animateContentSize()
                                ) {
                                    when (it) {
                                        NetworkState.None -> {}
                                        NetworkState.Loading ->
                                            CircularProgressIndicator(modifier = Modifier.size(20.dp))

                                        NetworkState.Error ->
                                            DefaultErrorView(nUploadModel, pos = DefaultErrorViewPos.CenteredNotFull, text = "Ошибка")
                                    }
                                }
                                Spacer(Modifier.width(10.dp))
                            }
                        }


                    },
                    isTransparentHaze = isHaze,
                    hazeState = null
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
                    NetworkState.None -> {
                        val ministryList =
                            model.ministryList.firstOrNull { it.ministryId == model.pickedMinistry && it.date == model.currentDate.second }
                        CLazyColumn(padding = padding, state = lazyListState, hazeState = hazeState) {
                            if (ministryList == null) {
                                item {
                                    Text("meow")
                                }
                            } else {

                                items(items = model.forms.sortedBy { it.id }, key = {it.id}) { form ->
                                    val kidList = ministryList.kids.filter { it.formId == form.id }.sortedWith(
                                        compareBy(
                                            { it.fio.surname })
                                    )
                                    Text(
                                        "${form.form.classNum}-${form.form.shortTitle}",
                                        fontSize = 22.esp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.cClickable {
                                            if(kidList.isEmpty()) {
                                                component.onEvent(MinistryStore.Intent.PickFormId(form.id))
                                            }
                                        }.padding(start = 10.dp, bottom = 5.dp, top = 8.dp)
                                    )
                                    kidList.forEach { item ->
                                        MinistryKidItem(
                                            item = item,
                                            pickedMinistry = model.pickedMinistry,
                                            mvdLogin = model.mvdLogin,
                                            mvdReportId = model.mvdReportId,
                                            ds1ListComponent = component.ds1ListComponent,
                                            ds2ListComponent = component.ds2ListComponent,
                                            uploadStup = { reason, login, content, reportId, custom ->
                                                component.onEvent(
                                                    MinistryStore.Intent.UploadStup(
                                                        reason = reason,
                                                        login = login,
                                                        content = content,
                                                        reportId = reportId,
                                                        custom = custom
                                                    )
                                                )
                                            },
                                            //openMVDEvent: (login: String, reason: String, reportId: Int?, custom: String, stups: Int) -> Unit
                                            openMVDEvent = { login, reason, reportId, custom, stups ->
                                                component.onEvent(
                                                    MinistryStore.Intent.OpenMVDEdit(
                                                        login = login,
                                                        reason = reason,
                                                        reportId = reportId,
                                                        custom = custom,
                                                        stups = stups
                                                    )
                                                )
                                            }
                                        )
                                        Spacer(Modifier.height(10.dp))
                                    }
                                }

                            }
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
                    }

                    NetworkState.Loading -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }

                    NetworkState.Error -> DefaultErrorView(
                        nModel,
                        DefaultErrorViewPos.CenteredFull
                    )
                }
            }
        }
    }

    ListDialogMobileContent(
        component = component.ministriesListComponent,
        title = "Министерства"
    )

    ListDialogMobileContent(
        component = component.ds1ListComponent,
        title = "Готовность"
    )
    ListDialogMobileContent(
        component = component.ds2ListComponent,
        title = "Поведение"
    )


    CAlertDialogContent(
        component = component.ds3DialogComponent,
        isCustomButtons = false,
        title = "Нарушение",
        acceptText = "Сохранить"
    ) {
        Column(Modifier.verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
            Stepper(
                isEditable = true,
                count = model.mvdStups,
                maxCount = 0,
                minCount = -10
            ) {
                component.onEvent(
                    MinistryStore.Intent.ChangeDs3Stepper(it)
                )
            }

            CustomTextField(
                value = model.mvdCustom,
                onValueChange = {
                    component.onEvent(
                        MinistryStore.Intent.ChangeDs3Custom(it)
                    )
                },
                text = "Причина",
                isEnabled = true,
                isMoveUpLocked = true,
                autoCorrect = true,
                keyboardType = KeyboardType.Text,
                modifier = Modifier.fillMaxWidth(),
                isSingleLine = false
            )
        }
    }
}
