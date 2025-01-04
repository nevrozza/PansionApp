
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
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
import formRating.FormRatingComponent
import formRating.FormRatingStore
import rating.FormRatingStudent
import resources.RIcons
import server.Roles
import server.fetchReason
import server.getLocalDate
import server.roundTo
import view.LocalViewManager
import view.WindowScreen
import view.esp
import view.toColor

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.FormRatingContent(
    component: FormRatingComponent,
    isVisible: Boolean
) {
    val model by component.model.subscribeAsState()
    val nModel by component.nInterface.networkModel.subscribeAsState()
    val nFormPickerModel by component.formPickerDialog.nInterface.networkModel.subscribeAsState()
    val viewManager = LocalViewManager.current
    val isExpanded = viewManager.orientation.value == WindowScreen.Expanded
    val coroutineScope = rememberCoroutineScope()
    val hazeState = remember { HazeState() }

    val page =
        model.formRatingPages.firstOrNull { it.formId == model.formId && it.period == model.period }

    Scaffold(
        Modifier.fillMaxSize(),
        topBar = {
            AppBar(
                title = {
                    AnimatedContent(
                        (model.formName ?: "Выберите") + " класс ",
                        transitionSpec = { fadeIn().togetherWith(fadeOut()) },
                        modifier = Modifier.cClickable { component.formPickerDialog.onEvent(
                            ListDialogStore.Intent.ShowDialog
                        ) }
                    ) { text ->
                        Text(
                            text,
                            modifier = Modifier.padding(start = 10.dp),
                            fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                            fontWeight = FontWeight.Black,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                navigationRow = {
                    if (!isExpanded) {
                        IconButton(
                            onClick = { component.onOutput(FormRatingComponent.Output.Back) }
                        ) {
                            GetAsyncIcon(
                                path = RIcons.ChevronLeft
                            )
                        }
                    }
                },
                actionRow = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (model.role != Roles.student) {
                            Box(contentAlignment = Alignment.Center) {
                                Crossfade(nFormPickerModel.state) { cf ->
                                    Box(Modifier.size(40.dp), contentAlignment = Alignment.Center) {
                                        when (cf) {
                                            NetworkState.Error -> IconButton(onClick = {
                                                nFormPickerModel.onFixErrorClick()
                                            }) {
                                                GetAsyncIcon(
                                                    RIcons.ErrorOutline
                                                )
                                            }

                                            NetworkState.Loading -> {
                                                CircularProgressIndicator(Modifier.size(25.dp))
                                            }

                                            NetworkState.None ->
                                                this@Row.AnimatedVisibility(
                                                    model.availableForms.size > 1 || (model.formId == null && model.availableForms.size == 1)
                                                ) {
                                                    IconButton(onClick = {
                                                        component.formPickerDialog.onEvent(
                                                            ListDialogStore.Intent.ShowDialog
                                                        )
                                                    }) {
                                                        GetAsyncIcon(
                                                            RIcons.ChevronLeft,
                                                            modifier = Modifier.rotate(90f)
                                                        )
                                                    }
                                                }

                                        }
                                    }
                                }

                                ListDialogDesktopContent(
                                    component = component.formPickerDialog
                                )
                            }
                        }
                        IconButton(
                            onClick = {
                                component.onEvent(FormRatingStore.Intent.Init)
                            }
                        ) {
                            GetAsyncIcon(
                                RIcons.Refresh
                            )
                        }
                    }
                },
                hazeState = hazeState
            )
        }
    ) { padding ->
        Crossfade(nModel.state) { state ->
            when (state) {
                NetworkState.None -> {
                    CLazyColumn(
                        padding = padding,
                        hazeState = hazeState
                    ) {
                        item {
                            Row(
                                modifier = Modifier.horizontalScroll(
                                    rememberScrollState()
                                )
                            ) {
                                listOf(
                                    Pair(0, "За неделю"),
                                    Pair(1, "За прошлую неделю"),
                                    Pair(2, "За модуль"),
                                    Pair(3, "За полугодие"),
                                    Pair(4, "За год")
                                ).forEach { item ->

                                    CFilterChip(
                                        label = item.second,
                                        isSelected = model.period == item.first,
                                        state = state,
                                        coroutineScope = coroutineScope
                                    ) {
                                        component.onEvent(
                                            FormRatingStore.Intent.ChangePeriod(item.first)
                                        )
                                    }
                                    Spacer(Modifier.width(5.dp))
                                }
                            }
                        }
                        if (page != null) {
                            items(page.topEd.toList(), key = { it }) { (top, students) ->
                                students.forEach { student ->
                                    FormRatingCard(
                                        item = page.students.first { it.login == student },
                                        topEd = top + 1,
                                        meLogin = model.login,
                                        topMark = page.topMarks.filterValues { student in it }.keys.first() + 1,
                                        topStup = page.topStups.filterValues { student in it }.keys.first() + 1,
                                        component = component,
                                        isVisible = isVisible
                                    )
                                }
                            }
                            itemsIndexed(
                                items = page.students.filter { it.login !in page.topEd.values.flatten() },
                                key = { _, item -> item.login }) { index, item ->
                                if (index == 0) {
                                    Text("Не участвуют в рейтинге: ")
                                }
                                FormRatingCard(
                                    item = item,
                                    topEd = 0,
                                    meLogin = model.login,
                                    topMark = 0,
                                    topStup = 0,
                                    component = component,
                                    isVisible = isVisible
                                )
                            }

                        }
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
    ListDialogMobileContent(
        component = component.formPickerDialog,
        title = "Классы"
    )

    val detailedStupsStudent =
        page?.students?.firstOrNull { it.login == model.stupsLogin }

    CAlertDialogContent(
        component = component.stupsDialogComponent,
        title = "Ступени: ${if (detailedStupsStudent != null) (detailedStupsStudent.fio.surname + " " + detailedStupsStudent.fio.name) else "null"}",
        titleXOffset = 5.dp
    ) {
        Column(Modifier.verticalScroll(rememberScrollState())) {
            page?.students?.firstOrNull { it.login == model.stupsLogin }?.edStups?.sortedBy {
                getLocalDate(
                    it.date
                ).toEpochDays()
            }?.reversed()?.forEach {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 1.dp)
                        .padding(horizontal = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(model.subjects[it.subjectId].toString(), modifier = Modifier.weight(1f, false), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(it.date.subSequence(0, 5).toString(), modifier = Modifier.weight(1f, false))
                    Text(fetchReason(it.reason).split(":")[1], modifier = Modifier.weight(.5f, false))
                    BorderStup(it.content, reason = it.reason, addModifier = Modifier.weight(.5f, false))
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.FormRatingCard(
    item: FormRatingStudent,
    topEd: Int,
    topMark: Int,
    topStup: Int,
    meLogin: String,
    isVisible: Boolean,
    component: FormRatingComponent
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        tonalElevation = if (meLogin == item.login) 24.dp else 2.dp,
        shadowElevation = 0.dp,
        onClick = {
            if (meLogin != item.login) {
                component.onOutput(
                    FormRatingComponent.Output.NavigateToProfile(
                        studentLogin = item.login,
                        fio = item.fio,
                        avatarId = item.avatarId
                    )
                )
            }
        }
    ) {
        Row(
            modifier = Modifier.padding(end = 16.dp, start = 8.dp).padding(vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (topEd != 0) {
                Box(Modifier.size(40.dp), contentAlignment = Alignment.Center) {
                    if (topEd <= 3) {
                        GetAsyncIcon(
                            path = RIcons.Trophy,
                            tint = when (topEd) {
                                1 -> "#ffd700".toColor()
                                2 -> "#c0c0c0".toColor()
                                else -> "#cd7f32".toColor()
                            },
                            size = 35.dp
                        )
                    } else {
                        // Show position number for other positions
                        Text(
                            text = topEd.toString(),
                            fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                            fontWeight = FontWeight.Black,
                            fontStyle = FontStyle.Italic
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
            GetAsyncAvatar(
                avatarId = item.avatarId,
                name = item.fio.name,
                size = 40.dp,
                textSize = MaterialTheme.typography.titleLarge.fontSize,
                modifier = Modifier.sharedElementWithCallerManagedVisibility(
                    sharedContentState = rememberSharedContentState(key = item.login + "avatar"),
                    visible = isVisible
                )
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${item.fio.surname} ${item.fio.name}",
                    fontSize = 18.esp, // Adjust font size for heading
                    lineHeight = 19.esp,
                    fontWeight = FontWeight.Bold // Make text bold for emphasis
                )
                Spacer(Modifier.height(1.dp))
                Text(
                    text = buildAnnotatedString {
                        if (item.formTitle != null) {
                            append(item.formTitle)
                            append(" ")
                        }
                        append("мвд: ${item.mvdStupsCount} ")
                        append("зд: ${item.zdStupsCount} ")
                    },
                    fontSize = MaterialTheme.typography.titleSmall.fontSize, // Adjust font size for body text
//                    lineHeight = 15.sp,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.width(8.dp))

            val avg = with(item.avg) {
                (sum / count.toFloat())
            }.roundTo(2)
            val stups = item.edStups.sumOf { it.content.toIntOrNull() ?: 0 }

            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clip(
                RoundedCornerShape(8.dp)).clickable {
                    component.onEvent(FormRatingStore.Intent.SelectStupsLogin(item.login))
            }) {
                Text(
                    text = if (topMark != 0) {
                        avg.toString() + " (${topMark})"
                    } else if (item.avg.count > 0) avg.toString()
                    else "",
                    fontSize = 18.esp,
                    lineHeight = 19.esp
                )
                Spacer(Modifier.height(1.dp))
                Text(
                    text = if (topStup != 0) {
                        "+${stups} (${topStup})"
                    } else if (item.edStups.isNotEmpty()) (if (stups.toString().first() != '-') "+" else "")+stups.toString()
                    else "",
                    fontSize = MaterialTheme.typography.titleSmall.fontSize,
//                    lineHeight = 15.sp,
                    color = MaterialTheme.colorScheme.primary,//Color.Green,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}