@file:OptIn(ExperimentalMaterial3Api::class)

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.*
import components.refresh.RefreshButton
import components.refresh.RefreshWithoutPullCircle
import components.refresh.keyRefresh
import components.networkInterface.NetworkState
import components.networkInterface.isLoading
import decomposeComponents.CBottomSheetContent
import dev.chrisbanes.haze.HazeState
import main.school.DutyKid
import main.school.MinistrySettingsReason
import main.school.MinistryStudent
import pullRefresh.PullRefreshIndicator
import pullRefresh.pullRefresh
import pullRefresh.rememberPullRefreshState
import resources.RIcons
import school.SchoolComponent
import school.SchoolStore
import server.Ministries
import server.Moderation
import server.Roles
import server.headerTitlesForMinistry
import view.*
import kotlin.math.ceil


enum class SchoolRoutings {
    SchoolRating, FormRating, Ministry
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@ExperimentalLayoutApi
@Composable
fun SchoolContent(
    component: SchoolComponent,
    currentRouting: SchoolRoutings
) {
    val model by component.model.subscribeAsState()
    val nModel by component.nInterface.networkModel.subscribeAsState()
    val nDutyModel by component.nDutyInterface.networkModel.subscribeAsState()
    val lazyListState = rememberLazyListState()
    val viewManager = LocalViewManager.current
    val isExpanded = viewManager.orientation.value == WindowScreen.Expanded
    val hazeState = remember { HazeState() }


    val refreshing = (nModel.isLoading || nDutyModel.isLoading)

    val refreshState = rememberPullRefreshState(
        refreshing,
        { component.onEvent(SchoolStore.Intent.Init) }
    )

    Scaffold(
        Modifier.fillMaxSize().keyRefresh(refreshState),
        topBar = {
            AppBar(
                title = {
                    Text(
                        "Пансион",
                        modifier = Modifier.padding(start = 10.dp),
                        fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (component.isSecondScreen) {
                        IconButton(
                            onClick = {
                                component.onOutput(SchoolComponent.Output.NavigateBack)
                            }
                        ) {
                            GetAsyncIcon(RIcons.Home)
                        }
                    }
                    RefreshWithoutPullCircle(refreshing, refreshState.position)
                },
                actionRow = {
                    if (viewManager.orientation.value != WindowScreen.Expanded) {
                        RefreshButton(refreshState, viewManager)
                    }
                },
                hazeState = hazeState
            )
        }
    ) { padding ->
        Box(
            Modifier.fillMaxSize()
                .pullRefresh(refreshState)
        ) {
            CLazyColumn(
                modifier = Modifier
                    .animateContentSize(),
                state = lazyListState,
                padding = padding,
                isBottomPaddingNeeded = true,
                hazeState = hazeState,
                refreshState = refreshState
            ) {
                item {
                    Row(Modifier.fillMaxWidth().height(IntrinsicSize.Max)) {
                        FeatureButton(
                            text = if (model.formName != null) "${model.formName} класс" else "Классы",
                            decoration = RIcons.SmallGroup,
                            isActive = currentRouting == SchoolRoutings.FormRating
                        ) {
                            component.onOutput(
                                SchoolComponent.Output.NavigateToFormRating(
                                    login = model.login,
                                    formName = model.formName,
                                    formNum = model.formNum,
                                    formId = model.formId
                                )
                            )
                        }
                        Spacer(Modifier.width(15.dp))

                        FeatureButton(
                            text = "Школьный рейтинг",
                            decoration = {
                                val currentCFState = when {
                                    nModel.state in listOf(
                                        NetworkState.Loading,
                                        NetworkState.Error
                                    ) -> 0

                                    model.top != null -> 1
                                    else -> 2
                                }
                                Crossfade(currentCFState) { cf ->
                                    Box(
                                        Modifier.fillMaxWidth()
                                            .height(24.dp),
                                        contentAlignment = Alignment.BottomEnd
                                    ) {
                                        when (cf) {
                                            0 -> LoadingAnimation(
                                                circleColor = MaterialTheme.colorScheme.onSurface,
                                                circleSize = 8.dp,
                                                spaceBetween = 5.dp,
                                                travelDistance = 3.5.dp
                                            )

                                            1 -> {
                                                Box(contentAlignment = Alignment.Center) {
                                                    if (model.top != null && model.top!! <= 3) {
                                                        GetAsyncIcon(
                                                            RIcons.Trophy,
                                                            tint = when (model.top) {
                                                                1 -> "#ffd700".toColor()
                                                                2 -> "#c0c0c0".toColor()
                                                                else -> "#cd7f32".toColor()
                                                            },
                                                            size = 23.dp
                                                        )
                                                    } else {
                                                        // Show position number for other positions
                                                        Text(
                                                            text = model.top.toString(),
                                                            fontSize = 23.esp,
                                                            fontWeight = FontWeight.Black,
                                                            fontStyle = FontStyle.Italic
                                                        )
                                                    }
                                                }
                                            }

                                            2 ->
                                                GetAsyncIcon(
                                                    RIcons.Trophy,
                                                    tint = MaterialTheme.colorScheme.secondary,
                                                    size = 23.dp
                                                )
                                        }
                                    }
                                }
                            },
                            isActive = currentRouting == SchoolRoutings.SchoolRating && isExpanded
                        ) {
                            component.onOutput(
                                SchoolComponent.Output.NavigateToRating
                            )
                        }
                    }
                }

                //errorItem
                item {
                    AnimatedVisibility(
                        nModel.state == NetworkState.Error
                    ) {
                        DefaultErrorView(
                            model = nModel,
                            pos = DefaultErrorViewPos.Centered,
                            isCompact = true
                        )
                    }
                }
                if (model.role in listOf(Roles.student) || model.moderation in listOf(
                        Moderation.both,
                        Moderation.mentor
                    )
                ) {
                    item {
                        Spacer(Modifier.height(15.dp))
                        val isEditDutyView = remember { mutableStateOf(false) }
                        val isFullDutyView = remember { mutableStateOf(false) }
                        ElevatedCard(
                            modifier = Modifier.fillMaxWidth().clip(CardDefaults.elevatedShape)
                                .clickable(
                                    enabled = !isEditDutyView.value,
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }) {
                                    isFullDutyView.value = !isFullDutyView.value
                                }
                        ) {
                            Crossfade(
                                targetState = nDutyModel.state,
                                modifier = Modifier.padding(vertical = 10.dp)
                                    .fillMaxWidth().animateContentSize(),
                                //                        verticalArrangement = Arrangement.SpaceBetween
                            ) { cf ->

                                Box(
                                    Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    when (cf) {
                                        NetworkState.Error -> DefaultErrorView(
                                            nDutyModel,
                                            modifier = Modifier.height(80.dp)
                                        )

                                        else -> {
                                            Crossfade(!isEditDutyView.value) { cf2 ->
                                                if (cf2) {
                                                    Column {
                                                        AnimatedVisibility(model.dutyKids.isNotEmpty()) {

                                                            if (model.dutyKids.isNotEmpty()) {
                                                                val sliceNum =
                                                                    if (model.dutyKids.size >= model.dutyPeopleCount) model.dutyPeopleCount else model.dutyKids.size
                                                                val todayKids = model.dutyKids.slice(0..<sliceNum)
                                                                Column {
                                                                    Row(
                                                                        verticalAlignment = Alignment.CenterVertically,
                                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                                        modifier = Modifier.fillMaxWidth()
                                                                            .padding(horizontal = 15.dp)
                                                                    ) {
                                                                        Text(
                                                                            "Сегодня дежурят",
                                                                            fontSize = 19.esp,
                                                                            fontWeight = FontWeight.Bold
                                                                        )
                                                                        if (model.role == Roles.student && todayKids.none { it.login == model.login }) {
                                                                            val daysFor =
                                                                                (ceil(((model.dutyKids.indexOfFirst { it.login == model.login } + 1) / model.dutyPeopleCount.toFloat()))).toInt() - 1
                                                                            Text(
                                                                                text = "Дней до: $daysFor",
                                                                                fontWeight = FontWeight.SemiBold,
                                                                                color = MaterialTheme.colorScheme.onBackground.copy(
                                                                                    alpha = .5f
                                                                                ),
                                                                                //                                                fontSize = 12.sp
                                                                            )
                                                                        } else if (model.moderation in listOf(
                                                                                Moderation.both,
                                                                                Moderation.mentor
                                                                            )
                                                                        ) {
                                                                            IconButton(
                                                                                onClick = {
                                                                                    isEditDutyView.value =
                                                                                        !isEditDutyView.value
                                                                                },
                                                                                modifier = Modifier.size(30.dp)
                                                                            ) {
                                                                                GetAsyncIcon(
                                                                                    RIcons.Edit,
                                                                                    size = 19.dp
                                                                                )
                                                                            }
                                                                        }
                                                                    }
                                                                    Spacer(Modifier.height(10.dp))
                                                                    todayKids.forEach { kid ->
                                                                        DutyCard(
                                                                            kid = kid,
                                                                            isHisTurn = true,
                                                                            isEditMode = false,
                                                                            myLogin = model.login
                                                                        )
                                                                        Spacer(Modifier.height(8.dp))
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        AnimatedVisibility(model.dutyKids.size - model.dutyPeopleCount > 0 && isFullDutyView.value) {
                                                            Column {
                                                                Text(
                                                                    "В другой раз",
                                                                    fontSize = 19.esp,
                                                                    fontWeight = FontWeight.Bold,
                                                                    modifier = Modifier.padding(horizontal = 15.dp)
                                                                )
                                                                Spacer(Modifier.height(10.dp))
                                                                if (model.dutyKids.size - model.dutyPeopleCount > 0) {
                                                                    model.dutyKids.slice(model.dutyPeopleCount..<model.dutyKids.size)
                                                                        .forEach { kid ->
                                                                            DutyCard(
                                                                                kid = kid,
                                                                                isHisTurn = false,
                                                                                isEditMode = false,
                                                                                myLogin = model.login
                                                                            )
                                                                            Spacer(Modifier.height(8.dp))
                                                                        }
                                                                }
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    val itemHeight = 50.dp
                                                    var items by remember { mutableStateOf(model.dutyKids) }
                                                    var countOfDuties by remember { mutableStateOf(model.dutyPeopleCount) }
                                                    Column(Modifier.fillMaxWidth()) {
                                                        Row(
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                            modifier = Modifier.fillMaxWidth()
                                                                .padding(horizontal = 15.dp)
                                                        ) {
                                                            Text(
                                                                "Дежурство",
                                                                fontSize = 19.esp,
                                                                fontWeight = FontWeight.Bold
                                                            )
                                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                                AnimatedVisibility(
                                                                    items == model.dutyKids
                                                                ) {
                                                                    Row {
                                                                        CustomTextButton("Новый день") {
                                                                            component.onEvent(
                                                                                SchoolStore.Intent.StartNewDayDuty(
                                                                                    newDutyPeopleCount = countOfDuties
                                                                                )
                                                                            )
                                                                            isEditDutyView.value = !isEditDutyView.value
                                                                        }
                                                                        Spacer(Modifier.width(10.dp))
                                                                    }
                                                                }
                                                                IconButton(
                                                                    onClick = {
                                                                        if (items != model.dutyKids || countOfDuties != model.dutyPeopleCount) {
                                                                            component.onEvent(
                                                                                SchoolStore.Intent.UpdateTodayDuty(
                                                                                    kids = items.map { it.login },
                                                                                    newDutyPeopleCount = countOfDuties
                                                                                )
                                                                            )
                                                                        }
                                                                        isEditDutyView.value = !isEditDutyView.value
                                                                    },
                                                                    modifier = Modifier.size(30.dp)
                                                                ) {
                                                                    GetAsyncIcon(
                                                                        RIcons.Check
                                                                    )
                                                                }
                                                            }
                                                        }
                                                        Spacer(Modifier.height(8.dp))
                                                        Row(
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                            modifier = Modifier.fillMaxWidth()
                                                                .padding(horizontal = 15.dp)
                                                        ) {
                                                            Text("Кол-во дежурных")
                                                            Spacer(Modifier.width(15.dp))
                                                            Stepper(
                                                                count = countOfDuties,
                                                                isEditable = true,
                                                                maxCount = 5,
                                                                minCount = 1
                                                            ) {
                                                                countOfDuties = it
                                                            }
                                                        }

                                                        Spacer(Modifier.height(6.dp))
                                                        Text(
                                                            text = "Зажмите и перетягивайте, чтобы измените очередь",
                                                            modifier = Modifier.fillMaxWidth().alpha(.5f),
                                                            textAlign = TextAlign.Center,
                                                            fontSize = 10.esp,
                                                            lineHeight = 10.esp
                                                        )
                                                        Box(Modifier.height(model.dutyKids.size * itemHeight)) {
                                                            DragDropList(
                                                                items = items.map { it.login to it },
                                                                onMove = { from, to ->
                                                                    val fromItem = items[from]
                                                                    val toItem = items[to]
                                                                    val newList = items.toMutableList()
                                                                    newList[from] = toItem
                                                                    newList[to] = fromItem
                                                                    items = newList
                                                                },
                                                                onDragFinished = {},
                                                                modifier = Modifier.fillMaxSize()
                                                            ) { i, it, isDragging ->
                                                                val color by animateColorAsState(
                                                                    if (isDragging) MaterialTheme.colorScheme.surfaceColorAtElevation(
                                                                        4.dp
                                                                    )
                                                                    else MaterialTheme.colorScheme.surfaceContainerLow,
                                                                    animationSpec = tween(500)
                                                                )
                                                                DutyCard(
                                                                    kid = it,
                                                                    isHisTurn = i + 1 <= model.dutyPeopleCount,
                                                                    isEditMode = true,
                                                                    modifier = Modifier.background(
                                                                        color
                                                                    ).height(itemHeight)//.animateItem()
                                                                        .padding(horizontal = 15.dp),
                                                                    myLogin = model.login
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
                }
                item {
                    Spacer(Modifier.height(7.dp))
                    //                CustomTextButton()
                    Box(
                        Modifier.fillMaxWidth().clip(CardDefaults.elevatedShape).clickable {
                            component.onOutput(
                                SchoolComponent.Output.NavigateToSchedule(
                                    isModer = model.moderation in listOf(
                                        Moderation.moderator,
                                        Moderation.both
                                    )
                                )
                            )
                        }
                    ) {
                        Row(
                            Modifier.fillMaxWidth().padding(vertical = 10.dp, horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Расписание БЕТА",
                                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.primary
                            )
                            GetAsyncIcon(
                                path = RIcons.ChevronLeft,
                                modifier = Modifier.rotate(180f),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                item {
                    Spacer(Modifier.height(7.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "Министерства",
                                fontSize = 19.esp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(start = 10.dp)
                            )
                            if (model.moderation in listOf(Moderation.moderator, Moderation.both)) {
                                IconButton(
                                    onClick = {
                                        component.onEvent(
                                            SchoolStore.Intent.OpenMinistrySettings(
                                                MinistrySettingsReason.School
                                            )
                                        )
                                    },
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    GetAsyncIcon(
                                        RIcons.Rocket
                                    )
                                }
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (model.moderation in listOf(Moderation.mentor, Moderation.both)) {
                                IconButton(
                                    onClick = {
                                        component.onEvent(
                                            SchoolStore.Intent.OpenMinistrySettings(
                                                MinistrySettingsReason.Form
                                            )
                                        )
                                    },
                                    modifier = Modifier.padding(end = 7.dp).size(30.dp)
                                ) {
                                    GetAsyncIcon(
                                        RIcons.Settings
                                    )
                                }
                            }
                            IconButton(
                                onClick = {
                                    component.onEvent(
                                        SchoolStore.Intent.OpenMinistrySettings(
                                            MinistrySettingsReason.Overview
                                        )
                                    )
                                },
                                modifier = Modifier.padding(end = 7.dp).size(30.dp)
                            ) {
                                GetAsyncIcon(
                                    RIcons.Menu
                                )
                            }
                        }
                    }
                }
                if (model.ministryId in listOf(
                        Ministries.DressCode,
                        Ministries.MVD,
                        Ministries.Culture
                    ) || model.moderation != Moderation.nothing
                ) {
                    item {
                        Spacer(Modifier.height(7.dp))
                        //                CustomTextButton()
                        Box(
                            Modifier.fillMaxWidth().clip(CardDefaults.elevatedShape).clickable {
                                if (model.ministryId !in listOf(Ministries.Culture) || model.role != Roles.student) {
                                    component.onOutput(SchoolComponent.Output.NavigateToMinistry)
                                } else if (model.ministryId == Ministries.Culture) {
                                    component.onOutput(SchoolComponent.Output.NavigateToAchievements)
                                }
                            }
                        ) {
                            Row(
                                Modifier.fillMaxWidth().padding(vertical = 10.dp, horizontal = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Редактировать",
                                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                GetAsyncIcon(
                                    path = RIcons.ChevronLeft,
                                    modifier = Modifier.rotate(180f),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
                if (model.role == Roles.student) {
                    item {
                        Spacer(Modifier.height(7.dp))
                        Row(Modifier.height(IntrinsicSize.Max)) {
                            MinistryCard(
                                ministry = "МВД",
                                nullIconPath = RIcons.Shield,
                                stupsCount = model.mvdStupsCount
                            ) {
                                component.onEvent(SchoolStore.Intent.OpenMinistryOverview(Ministries.MVD))
                            }
                            Spacer(Modifier.width(15.dp))
                            MinistryCard(
                                ministry = "Здраво-\nохранение",
                                nullIconPath = RIcons.Styler,
                                stupsCount = model.zdStupsCount
                            ) {
                                component.onEvent(SchoolStore.Intent.OpenMinistryOverview(Ministries.DressCode))
                            }
                        }
                        Spacer(Modifier.height(1.dp))
                        if (model.mvdStupsCount < 0 || model.zdStupsCount < 0) {
                            Text(
                                text = "Числа показывают, сколько минусов Вам поставили за неделю",
                                modifier = Modifier.fillMaxWidth().alpha(.5f),
                                textAlign = TextAlign.Center,
                                fontSize = 10.esp
                            )
                        }
                    }
                }
            }
            PullRefreshIndicator(
                refreshState, padding.calculateTopPadding()
            )
        }
    }


    CBottomSheetContent(
        component = component.ministrySettingsCBottomSheetComponent
    ) {
        val nMinistrySettingsModel by component.ministrySettingsCBottomSheetComponent.nModel.subscribeAsState()
        val ministryList = listOf<Pair<String, String>>(
            Ministries.MVD to "МВД",
            Ministries.DressCode to "Здравоохранение",
            Ministries.Education to "Образование",
            Ministries.Culture to "Культура",
            Ministries.Social to "Соц опросы",
            Ministries.Print to "Печать",
            Ministries.Sport to "Спорт"
        )
        LazyColumn(
            Modifier.padding(horizontal = 15.dp)
        ) {
            item {
                Text(
                    text = when (model.ministrySettingsReason) {
                        MinistrySettingsReason.Form -> "Министерства в классе"
                        MinistrySettingsReason.School -> "Главы министерств"
                        MinistrySettingsReason.Overview -> "Министерства"
                    },
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    fontWeight = FontWeight.Black
                )
            }
            items(
                ministryList, key = { it.first }) { s ->
                val ministryStudents = model.ministryStudents.filter { it.ministryId == s.first }
                val isAdding = remember { mutableStateOf(false) }
                val addingField = remember { mutableStateOf("") }
                val onEnterClick = {
                    if (addingField.value.isNotBlank()) {
                        component.onEvent(
                            SchoolStore.Intent.SetMinistryStudent(
                                ministryId = s.first,
                                login = null,
                                fio = addingField.value
                            )
                        )
                        addingField.value = ""
                        isAdding.value = false
                    }
                }
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(Modifier.height(5.dp))
                    Text(
                        text = s.second,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )

                    ministryStudents.sortedBy { it.lvl != "1" }.forEach { student ->
                        MinistrySettingsItem(
                            ministryStudent = student,
                            isDeleteButton = model.ministrySettingsReason != MinistrySettingsReason.Overview
                        ) {
                            if (model.ministrySettingsReason != MinistrySettingsReason.Overview) {
                                component.onEvent(
                                    SchoolStore.Intent.SetMinistryStudent(
                                        ministryId = "",
                                        login = student.login,
                                        fio = ""
                                    )
                                )
                            }
                        }
                    }
                    if (model.ministrySettingsReason != MinistrySettingsReason.Overview) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.animateContentSize().offset(y = (-5).dp)
                        ) {
                            IconButton(
                                onClick = {
                                    isAdding.value = !isAdding.value
                                },
                                modifier = Modifier.size(30.dp)
                            ) {
                                GetAsyncIcon(
                                    path = RIcons.Add,
                                    size = 20.dp
                                )
                            }

                            AnimatedVisibility(isAdding.value) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CustomTextField(
                                        value = addingField.value,
                                        onValueChange = {
                                            addingField.value = it
                                        },
                                        text = "ФИО",
                                        isEnabled = nMinistrySettingsModel.state == NetworkState.None,
                                        onEnterClicked = {
                                            onEnterClick()
                                        },
                                        isMoveUpLocked = true,
                                        autoCorrect = false,
                                        keyboardType = KeyboardType.Text
                                    )
                                    IconButton(onClick = { onEnterClick() }) {
                                        GetAsyncIcon(
                                            RIcons.Check
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            item {
                Spacer(Modifier.height(40.dp))
            }
        }
    }




    CBottomSheetContent(
        component = component.ministryOverviewComponent
    ) {

        LazyColumn(
            Modifier.padding(horizontal = 15.dp)
        ) {
            item {
                Text(
                    text = headerTitlesForMinistry[model.ministryOverviewId] ?: "Министерство?",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    fontWeight = FontWeight.Black
                )
            }
            item {
                Spacer(Modifier.height(10.dp))
                DatesLine(
                    dates = model.dates.reversed(),
                    currentDate = model.currentDate,
                    firstItemWidth = 0.dp
                ) {
                    component.onEvent(SchoolStore.Intent.ChangeDate(it))
                }
            }
            item {
                val ministryList =
                    model.ministryList.firstOrNull { it.ministryId == model.ministryOverviewId && it.date == model.currentDate.second }
                if (ministryList == null) {

                    Text("meow")

                } else {
                    ministryList.kids.forEachIndexed { i, item ->
                        MinistryKidItem(
                            item = item,
                            pickedMinistry = model.ministryOverviewId,
                            mvdLogin = null,
                            mvdReportId = null,
                            ds1ListComponent = null,
                            ds2ListComponent = null,
                            uploadStup = { reason, login, content, reportId, custom ->
                            },
                            openMVDEvent = { login, reason, reportId, custom, stups ->
                            }
                        )
                        Spacer(Modifier.height(10.dp))
                    }
                }
            }
            item {
                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DutyCard(
    kid: DutyKid,
    isHisTurn: Boolean,
    isEditMode: Boolean,
    myLogin: String,
    modifier: Modifier = Modifier.padding(horizontal = 15.dp)
) {

    FlowRow(
        verticalArrangement = Arrangement.Center,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.clip(RoundedCornerShape(15.dp)).then(modifier.fillMaxWidth())
    ) {
        Box(Modifier.size(40.dp).align(Alignment.CenterVertically), contentAlignment = Alignment.Center) {
            if (!isEditMode) {
                GetAsyncAvatar(
                    avatarId = kid.avatarId,
                    name = kid.fio.name,
                    size = 40.dp,
                    textSize = MaterialTheme.typography.titleSmall.fontSize
                )
            } else {
                GetAsyncIcon(
                    RIcons.Menu
                )
            }
            if (isHisTurn) {
                GetAsyncIcon(
                    RIcons.Dining,
                    size = 20.dp,
                    modifier = Modifier.align(Alignment.BottomEnd).offset(x = 4.dp),
                    tint = Color.White
                )
            }
        }
//        FlowRow(
//            horizontalArrangement = Arrangement.SpaceBetween,
//        ) {
        Text(
            text = "${kid.fio.surname} ${kid.fio.name}",
            fontWeight = if (myLogin == kid.login) FontWeight.Bold else FontWeight.SemiBold,
            color = if (myLogin == kid.login) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
        Text(
            text = "дежурств: ${kid.dutyCount}",
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = .5f),
            fontSize = 10.esp,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
//        }
    }
}

@Composable
private fun RowScope.MinistryCard(
    ministry: String,
    nullIconPath: String,
    stupsCount: Int,
    onClick: () -> Unit
) {
    ElevatedCard(
        Modifier.fillMaxWidth().clip(CardDefaults.elevatedShape)
            .weight(1f)
            .handy()
            .clickable {
                onClick()
            },
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            Modifier.fillMaxHeight().padding(vertical = 10.dp, horizontal = 15.dp)
                .fillMaxWidth().defaultMinSize(minHeight = 80.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                ministry,
                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(5.dp))
            Box(
                Modifier.fillMaxWidth()
                    .padding(end = 5.dp, bottom = 5.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Crossfade(
                    stupsCount >= 0
                ) { cf ->
                    if (cf) {
                        GetAsyncIcon(
                            nullIconPath,
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    } else {
                        Text(
                            text = stupsCount.toString(),
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold,
                            fontSize = MaterialTheme.typography.titleMedium.fontSize
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MinistrySettingsItem(
    ministryStudent: MinistryStudent,
    isDeleteButton: Boolean,
    onDeleteClick: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (ministryStudent.lvl == "1") {
            GetAsyncIcon(
                RIcons.Rocket,
                size = 25.dp
            )
        }
        Text("${ministryStudent.fio.surname} ${ministryStudent.fio.name}${if (ministryStudent.form.isNotBlank()) " ${ministryStudent.form}" else " ${ministryStudent.fio.praname}"}")
        if (isDeleteButton) {
            IconButton(
                onClick = { onDeleteClick() },
                modifier = Modifier.size(30.dp)
            ) {
                GetAsyncIcon(
                    path = RIcons.TrashCanRegular,
                    size = 19.dp
                )
            }
        }
    }
}