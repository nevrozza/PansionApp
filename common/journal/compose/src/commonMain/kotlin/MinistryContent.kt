
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.desktop.ui.tooling.preview.utils.GlobalHazeState
import androidx.compose.desktop.ui.tooling.preview.utils.esp
import androidx.compose.desktop.ui.tooling.preview.utils.hazeMask
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.DatesLine
import components.GetAsyncIcon
import components.MinistryKidItem
import components.foundation.AppBar
import components.foundation.CLazyColumn
import components.foundation.CTextField
import components.foundation.DefaultErrorView
import components.foundation.DefaultErrorViewPos
import components.foundation.cClickable
import components.journal.Stepper
import components.listDialog.ListDialogStore
import components.networkInterface.NetworkState
import components.networkInterface.isLoading
import components.refresh.RefreshWithoutPullCircle
import components.refresh.rememberPullRefreshState
import decomposeComponents.CAlertDialogContent
import decomposeComponents.listDialogComponent.ListDialogDesktopContent
import decomposeComponents.listDialogComponent.ListDialogMobileContent
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeEffectScope
import dev.chrisbanes.haze.HazeInputScale
import dev.chrisbanes.haze.LocalHazeStyle
import dev.chrisbanes.haze.hazeEffect
import ministry.MinistryComponent
import ministry.MinistryStore
import resources.RIcons
import server.Ministries
import server.headerTitlesForMinistry
import view.LocalViewManager


@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalHazeApi::class
)
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

    val refreshing = nModel.isLoading || nUploadModel.isLoading
    val refreshState = rememberPullRefreshState(
        refreshing,
        { component.onEvent(MinistryStore.Intent.Init) }
    )

    val isFormsEmpty = model.forms.isEmpty()


    val ministryList =
        model.ministryList.firstOrNull { it.ministryId == model.pickedMinistry && it.date == model.currentDate.second }


    //PullToRefresh
//    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    Scaffold(
        Modifier.fillMaxSize(),
//                .nestedScroll(scrollBehavior.nestedScrollConnection)
        topBar = {
            val isHaze = viewManager.hazeHardware.value
            Column(
                Modifier.then(
                    if (isHaze) //Brush.verticalGradient(colors = listOf(Color.Magenta, Color.Transparent))
//                        progressive = view.hazeProgressive
                        Modifier.hazeEffect(
                            state = GlobalHazeState.current,
                            style = LocalHazeStyle.current,
                            block = fun HazeEffectScope.() {
                                inputScale = HazeInputScale.Fixed(0.7f)
                                mask =
                                    hazeMask//Brush.verticalGradient(colors = listOf(Color.Magenta, Color.Transparent))
                                //                        progressive = view.hazeProgressive
                            })
                    else Modifier
                )
            ) {
                AppBar(
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


                        RefreshWithoutPullCircle(refreshing, refreshState.position, !isFormsEmpty)
                    },
                    navigationRow = {
                        IconButton(
                            onClick = { component.onOutput(MinistryComponent.Output.Back) }
                        ) {
                            GetAsyncIcon(
                                path = RIcons.CHEVRON_LEFT
                            )
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
                                            DefaultErrorView(
                                                nUploadModel,
                                                pos = DefaultErrorViewPos.CenteredNotFull,
                                                text = "Ошибка"
                                            )
                                    }
                                }
                                Spacer(Modifier.width(10.dp))
                            }
                        }


                    },
                    isTransparentHaze = isHaze
                )
                DatesLine(
                    dates = model.dates.reversed(),
                    currentDate = model.currentDate,
                    firstItemWidth = 30.dp
                ) {
                    component.onEvent(MinistryStore.Intent.ChangeDate(it))
                }
                AnimatedVisibility(
                    model.pickedMinistry == Ministries.MVD
                ) {
                    Text(
                        text = "Выставлять ступени в конце дня!",
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }

            }

            //LessonReportTopBar(component, isFullView) //, scrollBehavior
        }
    ) { padding ->
        val state = nModel.state
        //        Crossfade(nModel.state) { state ->
        AnimatedVisibility(state == NetworkState.Loading, modifier = Modifier.fillMaxSize()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        AnimatedVisibility(state is NetworkState.Error, modifier = Modifier.fillMaxSize()) {
            DefaultErrorView(
                nModel,
                DefaultErrorViewPos.CenteredFull
            )
        }


        AnimatedVisibility(
            !isFormsEmpty || state == NetworkState.None,
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
        ) {

            CLazyColumn(
                modifier = Modifier.fillMaxSize(),
                padding = padding,
                state = lazyListState
            ) {
                items(model.forms, key = { it.id }) { form ->
                    val isOpened = remember(form.id) { mutableStateOf(true) }
                    Column(Modifier.animateContentSize()) {
                        val kidList = (ministryList?.kids?.get(form.id)) ?: listOf()
                        Text(
                            "${form.form.classNum}-${form.form.shortTitle}",
                            fontSize = 22.esp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.cClickable {
                                isOpened.value = !isOpened.value
                                if (kidList.isEmpty()) {
                                    component.onEvent(MinistryStore.Intent.PickFormId(form.id))
                                    isOpened.value = true
                                }
                            }.fillMaxWidth().padding(start = 10.dp, bottom = 7.5f.dp, top = 10.5.dp),
                            textAlign = TextAlign.Center,
                            color = if (model.pickedFormId == form.id) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                        )
                        AnimatedVisibility (isOpened.value) {
                            Column {
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
                                                    custom = custom,
                                                    formId = item.formId
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
                    }
                }
                item {
                    Spacer(Modifier.height(20.dp))
                }


            }


        }

        AnimatedVisibility(model.pickedMinistry == "0", modifier = Modifier.fillMaxSize()) {
            Column(
                Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ElevatedButton(
                    onClick = {
                        component.onEvent(MinistryStore.Intent.ChangeMinistry(Ministries.MVD))
                    }
                ) {
                    Text("МВД")
                    Spacer(Modifier.width(10.dp))
                    GetAsyncIcon(
                        RIcons.SHIELD
                    )
                }
                Spacer(Modifier.height(10.dp))
                ElevatedButton(
                    onClick = {
                        component.onEvent(MinistryStore.Intent.ChangeMinistry(Ministries.DRESS_CODE))
                    }
                ) {
                    Text("Здравоохранение")
                    Spacer(Modifier.width(10.dp))
                    GetAsyncIcon(
                        RIcons.STYLER
                    )
                }

            }
        }


//        }


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

            CTextField(
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
