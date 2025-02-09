@file:OptIn(
    ExperimentalLayoutApi::class, ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class
)

import admin.groups.Group
import admin.groups.Subject
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.desktop.ui.tooling.preview.utils.esp
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.GetAsyncAvatar
import components.GetAsyncIcon
import components.cBottomSheet.CBottomSheetStore
import components.foundation.AppBar
import components.foundation.CCheckbox
import components.foundation.CLazyColumn
import components.foundation.DefaultErrorView
import components.foundation.DefaultErrorViewPos
import components.foundation.cClickable
import components.foundation.hazeHeader
import components.networkInterface.NetworkState
import decomposeComponents.CBottomSheetContent
import profile.ProfileComponent
import profile.ProfileStore
import resources.Images
import resources.PricedAvatar
import resources.RIcons
import server.Ministries
import utils.hv
import view.LocalViewManager
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class, ExperimentalSharedTransitionApi::class)
@ExperimentalMaterial3Api
@ExperimentalLayoutApi
@Composable
fun SharedTransitionScope.ProfileContent(
    component: ProfileComponent,
    isSharedVisible: Boolean
) {
    val model by component.model.subscribeAsState()
    val nAboutMeModel by component.nAboutMeInterface.networkModel.subscribeAsState()
    val nAvatarModel by component.nAvatarInterface.networkModel.subscribeAsState()
    val viewManager = LocalViewManager.current
    val lazyListState = rememberLazyListState()
    var isFullHeader by remember { mutableStateOf(true) } //!lazyListState.canScrollBackward || model.tabIndex == 2


    LaunchedEffect(lazyListState.firstVisibleItemScrollOffset) {
        isFullHeader =
            (!lazyListState.lastScrolledForward) && lazyListState.firstVisibleItemScrollOffset == 0 || model.tabIndex == 2
    }


    val headerAvatar = if (model.tabIndex == 2) model.newAvatarId else model.avatarId
    val avatarsList: List<Pair<String, List<Pair<Int, PricedAvatar>>>> = if (model.avatars != null) remember {
        listOf(
            "Символы" to Images.symbolsCostedAvatars.map {
                it.key to it.value.copy(
                    price = if (model.avatars?.contains(
                            it.key
                        ) == true
                    ) 0 else it.value.price
                )
            }
                .sortedBy { it.second.price }
                .filter { it.first != Images.Avatars.Symbols.pansionPrint.first || model.ministryId == Ministries.PRINT } + (0 to PricedAvatar(
                path = null,
                price = 0
            )),
            "Картины" to Images.picturesCostedAvatars.map {
                it.key to it.value.copy(
                    price = if (model.avatars?.contains(
                            it.key
                        ) == true
                    ) 0 else it.value.price
                )
            }
                .sortedBy { it.second.price },
            "Котики" to Images.catsCostedAvatars.map { it.key to it.value.copy(price = if (model.avatars?.contains(it.key) == true) 0 else it.value.price) }
                .sortedBy { it.second.price },
            "Котяо" to Images.catsMCostedAvatars.map { it.key to it.value.copy(price = if (model.avatars?.contains(it.key) == true) 0 else it.value.price) }
                .sortedBy { it.second.price },
            "Смешарики" to Images.smesharikiCostedAvatars.map {
                it.key to it.value.copy(
                    price = if (model.avatars?.contains(
                            it.key
                        ) == true
                    ) 0 else it.value.price
                )
            }.sortedBy { it.second.price },
            "Аниме?.." to Images.animeCostedAvatars.map { it.key to it.value.copy(price = if (model.avatars?.contains(it.key) == true) 0 else it.value.price) }
                .sortedBy { it.second.price },
            "Другое" to Images.othersCostedAvatars.map { it.key to it.value.copy(price = if (model.avatars?.contains(it.key) == true) 0 else it.value.price) }
                .sortedBy { it.second.price }
        ) + if (model.fio.name in listOf("Артём", "Артëм") && model.fio.surname == "Маташков") listOf(
            "nevrozq" to Images.nevrozqCostedAvatars.map {
                it.key to it.value.copy(
                    price = if (model.avatars?.contains(
                            it.key
                        ) == true
                    ) 0 else it.value.price
                )
            }
                .sortedBy { it.second.price }
        ) else listOf()
    } else listOf()


    //PullToRefresh
//    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        Modifier.fillMaxSize(),
        topBar = {
            Column(
                Modifier
                    .hazeHeader(
                        viewManager,
                        isMasked = false
                    ) //, isActivated = isSharedVisible
                    .clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = null
                    ) {
                        isFullHeader = true
                    }
            ) {
                AppBar(
                    title = {
                        Box(modifier = Modifier.fillMaxWidth().padding(end = 10.dp)) {
                            AnimatedContent(
                                if (!isFullHeader) model.fio.name else if (model.isOwner) "Профиль" else "Просмотр",
                                modifier = Modifier.align(Alignment.CenterStart)
                            ) {
                                Text(
                                    it,//"Успеваемость",
                                    fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                                    fontWeight = FontWeight.Black,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            this@Column.AnimatedVisibility(
                                !isFullHeader,
                                enter = fadeIn() + expandVertically(
                                    expandFrom = Alignment.Top, clip = false
                                ),
                                exit = fadeOut() + shrinkVertically(
                                    shrinkTowards = Alignment.Top, clip = false
                                ),
                                modifier = Modifier.align(Alignment.Center)
                                    .offset(x = -17.5.dp, y = 2.dp)
                            ) {
                                GetAsyncAvatar(
                                    avatarId = headerAvatar,
                                    name = model.fio.name,
                                    size = 40.dp,
                                    textSize = MaterialTheme.typography.titleSmall.fontSize
                                )
                            }
                            this@Column.AnimatedVisibility(
                                !isFullHeader,
                                enter = fadeIn() + expandVertically(
                                    expandFrom = Alignment.Top, clip = false
                                ),
                                exit = fadeOut() + shrinkVertically(
                                    shrinkTowards = Alignment.Top, clip = false
                                ),
                                modifier = Modifier.align(Alignment.CenterEnd)
                                    .offset(x = -17.5.dp, y = 2.dp)
                            ) {
                                Text(
                                    buildAnnotatedString {
                                        withStyle(SpanStyle(Color.Green)) {
                                            append("+${model.likes}")
                                        }
                                        append("/")
                                        withStyle(SpanStyle(Color.Red)) {
                                            append("-${model.dislikes}")
                                        }
                                    }
                                )
                            }

                        }
                    },
                    navigationRow = {
                        IconButton(
                            onClick = { component.onOutput(ProfileComponent.Output.Back) }
                        ) {
                            GetAsyncIcon(
                                path = RIcons.CHEVRON_LEFT
                            )
                        }
                    },
                    actionRow = {
                        AnimatedVisibility(
                            visible = model.tabIndex !in listOf(0, 1),
                            enter = fadeIn() + scaleIn(),
                            exit = fadeOut() + scaleOut(),
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                GetAsyncIcon(
                                    RIcons.COINS
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    model.pansCoins.toString(),
                                    fontSize = 17.esp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    },
                    isTransparentHaze = true
                )
                AnimatedVisibility(
                    isFullHeader,
                    enter = fadeIn() + expandVertically(clip = false) + scaleIn(),
                    exit = fadeOut() + shrinkVertically(clip = false) + scaleOut(),
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box() {
                            GetAsyncAvatar(
                                avatarId = headerAvatar,
                                name = model.fio.name,
                                size = 150.dp,
                                textSize = 75.esp,
                                modifier = Modifier.sharedElementWithCallerManagedVisibility(
                                    sharedContentState = rememberSharedContentState(key = model.studentLogin + "avatar"),
                                    visible = isSharedVisible
                                )
                            )
                            val delay = 300
                            this@Column.AnimatedVisibility(
                                visible = model.ministryLvl != "0" && isSharedVisible,
                                enter = expandIn(
                                    expandFrom = Alignment.TopEnd,
                                    animationSpec = tween(delayMillis = delay)
                                ) + scaleIn(animationSpec = tween(delayMillis = delay)),
                                exit = fadeOut() + scaleOut(),
                                modifier = Modifier.offset(x = -5.dp, y = -5.dp)
                            ) {
                                GetAsyncIcon(
                                    path = RIcons.ROCKET_LAUNCH,
                                    size = 50.dp,
                                    tint = MaterialTheme.colorScheme.inversePrimary.hv()
                                )
                            }
                            this@Column.AnimatedVisibility(
                                visible = model.ministryId != "0" && isSharedVisible,
                                enter = fadeIn(animationSpec = tween(delayMillis = delay)) + scaleIn(
                                    animationSpec = tween(
                                        delayMillis = delay
                                    )
                                ),
                                exit = fadeOut() + scaleOut(),
                                modifier = Modifier.align(Alignment.BottomEnd)
                            ) {
                                GetAsyncIcon(
                                    path = when (model.ministryId) {
                                        Ministries.MVD -> {
                                            RIcons.SHIELD
                                        }

                                        Ministries.CULTURE -> {
                                            RIcons.CELEBRATION
                                        }

                                        Ministries.DRESS_CODE -> {
                                            RIcons.STYLER
                                        }

                                        Ministries.EDUCATION -> {
                                            RIcons.SCHOOL_CAP
                                        }

                                        Ministries.PRINT -> {
                                            RIcons.NEWSPAPER
                                        }

                                        Ministries.SOCIAL -> {
                                            RIcons.COMMENT
                                        }

                                        Ministries.SPORT -> {
                                            RIcons.BALL
                                        }

                                        else -> {
                                            RIcons.QUESTION_CIRCLE
                                        }
                                    },
                                    size = 50.dp,
                                    tint = MaterialTheme.colorScheme.inversePrimary.hv()
                                )
                            }
                        }
                        Spacer(Modifier.height(15.dp))
                        Text(
                            text = "${model.fio.surname} ${model.fio.name} ${if (model.fio.praname.isNullOrEmpty()) "" else "\n${model.fio.praname}"}",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 10.dp).fillMaxWidth(),
                            fontWeight = FontWeight.Black,
                            fontSize = MaterialTheme.typography.headlineSmall.fontSize
                        )

                        Spacer(Modifier.height(5.dp)) //3.dp
                        Crossfade(
                            model.avatarId != model.newAvatarId && model.avatars != null && model.tabIndex !in listOf(
                                0,
                                1
                            ),
                            modifier = Modifier.animateContentSize()
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                                if (it) {

                                    val price =
                                        if (model.avatars?.contains(model.newAvatarId) == true) 0 else (avatarsList.flatMap { it.second }
                                            .firstOrNull { it.first == model.newAvatarId }?.second?.price?.toString()
                                            ?: "???").toIntOrNull()

                                    Crossfade(nAvatarModel.state) {

                                        SmallFloatingActionButton(
                                            onClick = {
                                                if (it != NetworkState.Loading && model.avatarId != model.newAvatarId && model.avatars != null && (price
                                                        ?: 0) <= model.pansCoins
                                                ) {
                                                    component.onEvent(
                                                        ProfileStore.Intent.SaveAvatarId(
                                                            avatarId = model.newAvatarId,
                                                            price = price ?: 0
                                                        )
                                                    )
                                                }
                                            },
                                            containerColor = if ((price
                                                    ?: 0) <= model.pansCoins
                                            ) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer
                                        ) {
                                            when (it) {
                                                NetworkState.None -> {
                                                    AnimatedContent(price) {

                                                        Row(
                                                            modifier = Modifier.padding(horizontal = 15.dp),
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            if ((model.avatars?.contains(model.newAvatarId) == true || it == 0) && (price
                                                                    ?: 0) <= model.pansCoins
                                                            ) {
                                                                Text("Сохранить  ")
                                                                GetAsyncIcon(
                                                                    path = RIcons.SAVE
                                                                )
                                                            } else {

                                                                Text(
                                                                    (if ((price
                                                                            ?: 0) <= model.pansCoins
                                                                    ) "Купить за $it"
                                                                    else "Не хватает ${(price ?: 0) - model.pansCoins}") + "  "
                                                                )

                                                                GetAsyncIcon(
                                                                    path = RIcons.COINS
                                                                )
                                                            }
                                                        }
                                                    }
                                                }

                                                NetworkState.Loading -> {
                                                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                                                }

                                                NetworkState.Error -> {
                                                    Text(nAvatarModel.error)
                                                }
                                            }
                                        }
                                    }

                                } else {
                                    if (model.likes.absoluteValue != 0 || model.dislikes.absoluteValue != 0) {
                                        Text(
                                            buildAnnotatedString {
                                                withStyle(SpanStyle(Color.Green)) {
                                                    append("+${model.likes}")
                                                }
                                                append("/")
                                                withStyle(SpanStyle(Color.Red)) {
                                                    append("-${model.dislikes}")
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(5.dp))
                        //            HorizontalDivider(Modifier.width(340.dp).height(1.dp).padding(vertical = 15.dp, horizontal = 30.dp), color = MaterialTheme.colorScheme.onBackground.copy(alpha = .1f))
                    }
                }

                TabRow( //Scrollable
                    selectedTabIndex = model.tabIndex,
                    containerColor = Color.Transparent
                ) {
                    for (i in if (model.isOwner && model.isCanEdit) (0..2) else (0..1)) {
                        val text = when (i) {
                            0 -> "Обо мне"
                            1 -> "Статистика"
                            else -> "Аватарки"
                        }
                        Tab(
                            selected = model.tabIndex == i,
                            onClick = {
                                component.onEvent(ProfileStore.Intent.ChangeTab(i))
                                if (i == 2) {
                                    isFullHeader = true
                                }
                            },
                            text = {
                                Text(
                                    text = text,
                                    maxLines = 1, overflow = TextOverflow.Ellipsis
                                )
                            }
                        )
                    }
                }
            }

            //LessonReportTopBar(component, isFullView) //, scrollBehavior
        }
    ) { padding ->
        CLazyColumn(
            padding = PaddingValues(
                top = animateDpAsState(padding.calculateTopPadding()).value,
                bottom = padding.calculateBottomPadding()
            ),
            state = lazyListState
        ) {
            when (model.tabIndex) {
                0 -> {
                    item {
                        Crossfade(nAboutMeModel.state) {
                            when (it) {
                                NetworkState.Loading -> {
                                    Box(
                                        Modifier.fillMaxWidth()
                                            .height(viewManager.size!!.maxHeight - padding.calculateTopPadding()),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }

                                NetworkState.Error -> DefaultErrorView(
                                    nAboutMeModel,
                                    DefaultErrorViewPos.CenteredFull
                                )

                                NetworkState.None -> {
                                    Column {
                                        Row(Modifier.fillMaxWidth().padding(top = 10.dp)) {
                                            if (model.form != null) {
                                                ElevatedCard(
                                                    Modifier.fillMaxWidth()
                                                        .clip(CardDefaults.elevatedShape)
                                                        .weight(1f)
                                                        .clickable() {
                                                            component.giaCBottomSheetComponent.onEvent(
                                                                CBottomSheetStore.Intent.ShowSheet
                                                            )
                                                        }
                                                ) {
                                                    val modifier = Modifier
                                                        .fillMaxWidth()
                                                        .defaultMinSize(minHeight = 80.dp)
                                                    Box(
                                                        modifier = modifier.padding(
                                                            vertical = 10.dp,
                                                            horizontal = 15.dp
                                                        )
                                                    ) {
                                                        Column(
                                                            modifier = modifier
                                                        ) {
                                                            Text(
                                                                "Класс",
                                                                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                                                fontWeight = FontWeight.Bold
                                                            )
                                                            Spacer(Modifier.height(2.dp))
                                                            Column(
                                                                Modifier.horizontalScroll(
                                                                    rememberScrollState()
                                                                )
                                                            ) {
                                                                Text(
                                                                    "${model.form!!.form.classNum}${if (model.form!!.form.title.length > 1) " " else "-"}${model.form!!.form.title} ${if (model.form!!.form.shortTitle.length > 1) "(${model.form!!.form.shortTitle})" else ""}",
                                                                    maxLines = 1
                                                                )
                                                                Spacer(Modifier.height(2.dp))
                                                                val mentorName =
                                                                    model.teachers[model.form!!.form.mentorLogin]
                                                                if (mentorName != null) {
                                                                    Text(
                                                                        mentorName
                                                                    )
                                                                }
                                                            }
                                                        }
                                                        GetAsyncIcon(
                                                            path = RIcons.SCHOOL_CAP_OUTLINE,
                                                            modifier = Modifier.align(Alignment.TopEnd)
                                                        )
                                                    }
                                                }
                                                Spacer(Modifier.width(15.dp))
                                            }
                                            ElevatedCard(
                                                Modifier.fillMaxWidth()
                                                    .clip(CardDefaults.elevatedShape)
                                                    .weight(1f)
                                                    .clickable() {
                                                        component.onOutput(
                                                            ProfileComponent.Output.OpenAchievements(
                                                                login = model.studentLogin,
                                                                name = model.fio.name,
                                                                avatarId = model.avatarId
                                                            )
                                                        )
//
                                                    }
                                            ) {
                                                Column(
                                                    Modifier.padding(
                                                        vertical = 10.dp,
                                                        horizontal = 15.dp
                                                    )
                                                        .fillMaxWidth()
                                                        .defaultMinSize(minHeight = 80.dp),
                                                    verticalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Box(
                                                        Modifier.fillMaxWidth()
                                                            .sharedElementWithCallerManagedVisibility(
                                                                sharedContentState = rememberSharedContentState(key = "EventsTitle"),
                                                                visible = isSharedVisible
                                                            )
                                                    ) {
                                                        Text(
                                                            "События",
                                                            fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                                            fontWeight = FontWeight.Bold,
                                                        )
                                                        Spacer(Modifier.height(5.dp))
                                                    }
                                                    Box(
                                                        Modifier.fillMaxWidth()
                                                            .padding(end = 5.dp, bottom = 5.dp),
                                                        contentAlignment = Alignment.CenterEnd
                                                    ) {
                                                        GetAsyncIcon(
                                                            RIcons.CUTE_CHECK,
                                                            tint = MaterialTheme.colorScheme.secondary,
                                                            size = 26.dp
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                        Spacer(Modifier.height(10.dp))
                                        ElevatedCard(
                                            Modifier.fillMaxWidth()
                                                .clip(CardDefaults.elevatedShape)
//                                            .weight(1f)
                                        ) {
                                            Column(
                                                Modifier.padding(
                                                    vertical = 10.dp,
                                                    horizontal = 15.dp
                                                )
                                            ) {
                                                Text(
                                                    "Предметы",
                                                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                                    fontWeight = FontWeight.Bold
                                                )
//                                                Spacer(Modifier.height(2.dp))
                                                model.groups.sortedBy { it.group.subjectId }
                                                    .forEach {
                                                        GroupsItem(
                                                            subjects = model.subjects,
                                                            teachers = model.teachers,
                                                            group = it
                                                        )
                                                    }
                                            }
                                        }
                                        Spacer(Modifier.height(30.dp))
                                    }
                                }
                            }
                        }
                    }
                }
                //was statistika
                1 -> {

                    item {

                        Box(Modifier.fillMaxWidth().height(((viewManager.size?.maxHeight ?: 0.dp) - padding.calculateTopPadding()).coerceAtLeast(0.dp)), contentAlignment = Alignment.Center) {
                            if (model.isStatsOpened != null) {

                                val isOpened = model.isStatsOpened ?: false
                                if (model.isOwner) {
                                    TextButton(
                                        onClick = {
                                            component.onEvent(ProfileStore.Intent.ChangeStatsSettings)
                                        },
                                        colors = ButtonDefaults.textButtonColors(
                                            containerColor = Color.Transparent,
                                            contentColor = MaterialTheme.colorScheme.onBackground//colorRed
                                        ),
                                        contentPadding = PaddingValues(horizontal = 15.dp)
                                    ) {
                                        AnimatedContent(isOpened) { opened ->
                                            Row(verticalAlignment = Alignment.CenterVertically) {

                                                Text(
                                                    if (opened) "Ваша статистику могут смотреть другие" else "Вашу статистику видите только* Вы",
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontSize = 15.esp
                                                )

                                                Spacer(Modifier.width(5.dp))
                                                GetAsyncIcon(
                                                    path = if (opened) RIcons.VISIBILITY else RIcons.VISIBILITY_OFF,
                                                    size = 25.dp
                                                )
                                            }
                                        }
                                    }

                                } else if (!model.isOwner) {
                                    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                                        GetAsyncIcon(
                                            path = RIcons.VISIBILITY_OFF,
                                            size = 50.dp
                                        )
                                        Spacer(Modifier.height(10.dp))
                                        Text("Пользователь скрыл свою статистику", modifier = Modifier.alpha(.5f), textAlign = TextAlign.Center)
                                    }
                                }
                            } else {
                                Text("Подождите немного. Данные загружаются", modifier = Modifier.alpha(.5f), textAlign = TextAlign.Center)
                            }
                        }
                    }

                }

                else -> {

                    items(
                        avatarsList
                    ) {
                        AvatarsBlock(
                            title = it.first,
                            avatars = it.second,
                            model = model,
                            component = component
                        )
                    }
                }
            }
        }
    }

    CBottomSheetContent(
        component = component.giaCBottomSheetComponent
    ) {
        val necessarySubjects = if ((model.form?.form?.classNum
                ?: 0) > 9
        ) egeNecessarySubjects else ogeNecessarySubjects
        val subjects = if ((model.form?.form?.classNum ?: 0) > 9) egeSubjects else ogeSubjects
        val finalSubjects = subjects.sortedByDescending { it.first in model.giaSubjects }

        LazyColumn(
            Modifier.padding(horizontal = 15.dp)
        ) {
            items(necessarySubjects) { s ->
                SubjectItem(
                    title = s.second,
                    isChecked = true,
                    modifier = Modifier.alpha(.5f)
                ) {
                }
            }
            items(
                finalSubjects,
                key = { it.first }) { s ->
                Column(
                    modifier = Modifier.animateItem()
                ) {
                    Spacer(Modifier.height(15.dp))
                    SubjectItem(
                        title = s.second,
                        isChecked = s.first in model.giaSubjects
                    ) {

                        if (model.isOwner && model.isCanEdit) {
                            component.onEvent(
                                ProfileStore.Intent.ClickOnGIASubject(
                                    subjectId = s.first,
                                    it
                                )
                            )
                        }
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
private fun AvatarsBlock(
    title: String,
    avatars: List<Pair<Int, PricedAvatar>>,
    model: ProfileStore.State,
    component: ProfileComponent
) {
    val headerAvatar = if (model.tabIndex == 2) model.newAvatarId else model.avatarId
    Text(title, fontSize = 24.esp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 10.dp))
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        FlowRow(
            Modifier.padding(vertical = 10.dp),
            //        horizontalArrangement = Arrangement.SpaceBetween
        ) {
            for (a in avatars) {
//                val image = if ((a.second.image as Any?) != null) imageResource(a.second.image!!) else null
                AvatarButton(
                    currentAvatar = headerAvatar,
                    i = a.first,
                    path = a.second.path,
                    name = model.fio.name,
                    price = if (model.avatars?.contains(a.first) == true) 0 else a.second.price
                ) {
                    component.onEvent(ProfileStore.Intent.SetNewAvatarId(a.first))
                }
            }
        }
    }
}

@Composable
private fun SubjectItem(
    title: String,
    isChecked: Boolean,
    modifier: Modifier = Modifier,
    onClick: (Boolean) -> Unit
) {
    Row(
        modifier = modifier.cClickable { onClick(!isChecked) }.fillMaxWidth(.8f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title)
        CCheckbox(
            checked = isChecked,
            modifier = Modifier.size(25.dp)
        )
    }
}

val egeNecessarySubjects = mapOf<Int, String>(
    0 to "Русский язык"
).toList()

val egeSubjects = mapOf<Int, String>(
    1 to "Математика БАЗА",
    2 to "Математика ПРОФИЛЬ",
    3 to "Информатика",
    4 to "Физика",
    5 to "Обществознание",
    6 to "История",
    7 to "География",
    8 to "Биология",
    9 to "Химия",
    10 to "Литература",
    11 to "Английский язык",
    12 to "Французский язык",
    13 to "Испанский язык",
    14 to "Немецкий язык",
    15 to "Китайский язык",
).toList()
val ogeNecessarySubjects = mapOf<Int, String>(
    0 to "Русский язык",
    1 to "Математика"
).toList()

val ogeSubjects = mapOf<Int, String>(
    3 to "Информатика",
    4 to "Физика",
    5 to "Обществознание",
    6 to "История",
    7 to "География",
    8 to "Биология",
    9 to "Химия",
    10 to "Литература",
    11 to "Английский язык",
    12 to "Французский язык",
    13 to "Испанский язык",
    14 to "Немецкий язык",
).toList()

@Composable
private fun GroupsItem(subjects: List<Subject>, teachers: HashMap<String, String>, group: Group) {
    val subject = subjects.firstOrNull { it.id == group.group.subjectId }?.name ?: "Название"
    val teacher = teachers[group.group.teacherLogin] ?: "Учитель"
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(2f, false)) {
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(subject)
                    }
                    append(" ${group.group.name}")
                },
                modifier = Modifier.padding(start = 3.dp)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {

                Spacer(Modifier.width(4.dp))
                GetAsyncIcon(
                    path = RIcons.USER,
                    size = 15.dp
                )
                Spacer(Modifier.width(4.dp))
                Text(teacher)
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(.5f, false)) {
            GetAsyncIcon(
                path = RIcons.FIRE,
                size = 22.dp
            )
            Spacer(Modifier.width(4.dp))
            Text(group.group.difficult, fontSize = 19.esp)
        }
    }
}


@Composable
private fun AvatarButton(
    currentAvatar: Int,
    i: Int,
    path: String?,
    price: Int,
    name: String,
    onClick: () -> Unit
) {
    Box() {
        GetAsyncAvatar(
            avatarId = i,
            name = name,
            modifier = Modifier.padding(5.dp).padding(top = 5.dp).clip(CircleShape).clickable { onClick() },
            isHighQuality = false,
            prePath = path
        )
        if (price != 0) {
            Box(
                modifier = Modifier.rotate(-30f).align(Alignment.BottomEnd)
                    .padding(5.dp).background(
                        if (currentAvatar == i) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                        shape = RoundedCornerShape(15.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("11", modifier = Modifier.alpha(0f).padding(horizontal = 10.dp))
                Text(
                    price.toString(),
                    fontSize = 15.esp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (currentAvatar == i) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary
                )
            }
        } else if (currentAvatar == i) {
            GetAsyncIcon(
                RIcons.CHECK_CIRCLE_OUTLINE,
                modifier = Modifier.align(Alignment.BottomEnd)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
                    .padding(2.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

