@file:OptIn(
    ExperimentalLayoutApi::class, ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class
)

import admin.groups.Group
import admin.groups.Subject
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.CheckCircleOutline
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.AppBar
import components.CLazyColumn
import components.CustomCheckbox
import components.CustomTextButton
import components.GetAvatar
import components.cBottomSheet.CBottomSheetStore
import components.cClickable
import components.hazeHeader
import components.networkInterface.NetworkState
import decomposeComponents.CBottomSheetContent
import profile.ProfileComponent
import profile.ProfileStore
import resources.Images
import view.LocalViewManager

@OptIn(ExperimentalFoundationApi::class)
@ExperimentalMaterial3Api
@ExperimentalLayoutApi
@Composable
fun ProfileContent(
    component: ProfileComponent
) {
    val model by component.model.subscribeAsState()
    val nAboutMeModel by component.nAboutMeInterface.networkModel.subscribeAsState()
    val nAvatarModel by component.nAvatarInterface.networkModel.subscribeAsState()
    val viewManager = LocalViewManager.current
    val lazyListState = rememberLazyListState()
    var isFullHeader by remember { mutableStateOf(true) } //!lazyListState.canScrollBackward || model.tabIndex == 2

    LaunchedEffect(lazyListState.firstVisibleItemScrollOffset) {
        isFullHeader = (!lazyListState.lastScrolledForward) && lazyListState.firstVisibleItemScrollOffset == 0 || model.tabIndex == 2
    }

    val headerAvatar = if (model.tabIndex == 2) model.newAvatarId else model.avatarId

    //PullToRefresh
//    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        Modifier.fillMaxSize(),
        topBar = {
            Column(
                Modifier
                    .hazeHeader(viewManager)
                    .clickable(interactionSource = MutableInteractionSource(), indication = null) {
                        isFullHeader = true
                    }
            ) {
                AppBar(
                    navigationRow = {
                        IconButton(
                            onClick = { component.onOutput(ProfileComponent.Output.Back) }
                        ) {
                            Icon(
                                Icons.Rounded.ArrowBackIosNew, null
                            )
                        }
                    },
                    title = {
                        Box(modifier = Modifier.fillMaxWidth().padding(end = 10.dp)) {
                            AnimatedContent(
                                if (!isFullHeader) model.fio.name else if (model.isOwner) "Профиль" else "Просмотр",
                                modifier = Modifier.align(Alignment.CenterStart)
                            ) {
                                Text(
                                    it,//"Успеваемость",
                                    fontSize = 25.sp,
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
                                GetAvatar(
                                    avatarId = headerAvatar,
                                    name = model.fio.name,
                                    size = 40.dp,
                                    textSize = 15.sp
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
                    isHaze = false,
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
                        GetAvatar(
                            avatarId = headerAvatar,
                            name = model.fio.name,
                            size = 150.dp,
                            textSize = 75.sp
                        )
                        Spacer(Modifier.height(15.dp))
                        Text(
                            text = "${model.fio.surname} ${model.fio.name} ${if (model.fio.praname.isNullOrEmpty()) "" else "\n${model.fio.praname}"}",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 10.dp).fillMaxWidth(),
                            fontWeight = FontWeight.Black,
                            fontSize = 25.sp
                        )

                        Spacer(Modifier.height(5.dp)) //3.dp
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
                                    maxLines = 1
                                )
                            }
                        )
                    }
                }
            }
            //LessonReportTopBar(component, isFullView) //, scrollBehavior
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible =
                model.avatarId != model.newAvatarId,
                enter = slideInVertically(initialOffsetY = { it * 2 }),
                exit = slideOutVertically(targetOffsetY = { it * 2 }),
            ) {
                Crossfade(nAvatarModel.state) {
                    SmallFloatingActionButton(
                        onClick = {
                            if (it != NetworkState.Loading && model.avatarId != model.newAvatarId) {
                                component.onEvent(ProfileStore.Intent.SaveAvatarId)
                            }
                        }
                    ) {
                        when (it) {
                            NetworkState.None -> {
                                Icon(
                                    Icons.Rounded.Save,
                                    null
                                )
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
            }
        }
    ) { padding ->
        CLazyColumn(
            padding = PaddingValues(
                top = padding.calculateTopPadding(),
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

                                NetworkState.Error -> {
                                    Column(
                                        Modifier.fillMaxSize(),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(nAboutMeModel.error)
                                        Spacer(Modifier.height(7.dp))
                                        CustomTextButton("Попробовать ещё раз") {
                                            nAboutMeModel.onFixErrorClick()
                                        }
                                    }
                                }

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
                                                                fontSize = 17.sp,
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
                                                        Icon(
                                                            Icons.Outlined.School,
                                                            null,
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
                                                    Text(
                                                        "События",
                                                        fontSize = 17.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    Spacer(Modifier.height(5.dp))
                                                    Box(
                                                        Modifier.fillMaxWidth()
                                                            .padding(end = 5.dp, bottom = 5.dp),
                                                        contentAlignment = Alignment.CenterEnd
                                                    ) {
                                                        Icon(
                                                            Icons.Outlined.EmojiEvents,
                                                            null,
                                                            tint = MaterialTheme.colorScheme.secondary
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
                                                    fontSize = 17.sp,
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
                        Text(
                            "В разработке",
                            modifier = Modifier.fillMaxWidth().padding(top = 50.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                else -> {
                    item {
                        FlowRow(
                            Modifier.fillMaxWidth().padding(top = 10.dp, bottom = 30.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            for (i in Images.Avatars.avatarIds) {
                                AvatarButton(
                                    currentAvatar = headerAvatar,
                                    i = i,
                                    name = model.fio.name
                                ) {
                                    component.onEvent(ProfileStore.Intent.SetNewAvatarId(i))
                                }
                            }
                        }
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
                subjects.sortedByDescending { it.first in model.giaSubjects },
                key = { it.first }) { s ->
                Spacer(Modifier.height(15.dp))
                SubjectItem(
                    title = s.second,
                    isChecked = s.first in model.giaSubjects,
                    modifier = Modifier.animateItem(fadeInSpec = null, fadeOutSpec = null)
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
            item {
                Spacer(Modifier.height(40.dp))
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
        CustomCheckbox(
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
            Row {
                Icon(Icons.Rounded.Person, null)
                Text(teacher)
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(.5f, false)) {
            Icon(
                Icons.Rounded.LocalFireDepartment,
                null,
                modifier = Modifier.size(30.dp)
            )
            Text(group.group.difficult, fontSize = 19.sp)
        }
    }
}


@Composable
private fun AvatarButton(currentAvatar: Int, i: Int, name: String, onClick: () -> Unit) {
    Box() {
        GetAvatar(
            avatarId = i,
            name = name,
            modifier = Modifier.padding(5.dp).clip(CircleShape).clickable { onClick() }
        )
        if (currentAvatar == i) {
            Icon(
                Icons.Rounded.CheckCircleOutline,
                null,
                modifier = Modifier.align(Alignment.BottomEnd)
                    .padding(5.dp).background(
                        MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    ),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

