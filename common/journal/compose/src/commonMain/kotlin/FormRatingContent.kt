import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.AppBar
import components.BorderStup
import components.CLazyColumn
import components.CustomTextButton
import components.GetAvatar
import components.listDialog.ListDialogStore
import components.networkInterface.NetworkState
import decomposeComponents.CAlertDialogContent
import decomposeComponents.listDialogComponent.ListDialogDesktopContent
import decomposeComponents.listDialogComponent.ListDialogMobileContent
import formRating.FormRatingComponent
import formRating.FormRatingStore
import kotlinx.coroutines.launch
import rating.FormRatingStudent
import server.Roles
import server.fetchReason
import server.getLocalDate
import server.roundTo
import view.LocalViewManager
import view.WindowScreen
import view.toColor

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun FormRatingContent(
    component: FormRatingComponent
) {
    val model by component.model.subscribeAsState()
    val nModel by component.nInterface.networkModel.subscribeAsState()
    val nFormPickerModel by component.formPickerDialog.nInterface.networkModel.subscribeAsState()
    val lazyListState = rememberLazyListState()
    val viewManager = LocalViewManager.current
    val isExpanded = viewManager.orientation.value == WindowScreen.Expanded
    val coroutineScope = rememberCoroutineScope()

    val page =
        model.formRatingPages.firstOrNull { it.formId == model.formId && it.period == model.period }

    Scaffold(
        Modifier.fillMaxSize(),
        topBar = {
            AppBar(
                title = {
                    AnimatedContent(
                        (model.formName ?: "Выберите") + " класс",
                        transitionSpec = { fadeIn().togetherWith(fadeOut()) }
                    ) { text ->
                        Text(
                            text,
                            modifier = Modifier.padding(start = 10.dp),
                            fontSize = 25.sp,
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
                            Icon(
                                Icons.Rounded.ArrowBackIosNew, null
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
                                                Icon(
                                                    Icons.Rounded.ErrorOutline, null
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
                                                        Icon(
                                                            Icons.Rounded.ExpandMore, null
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
                            Icon(
                                Icons.Filled.Refresh, null
                            )
                        }
                    }
                },
                isHaze = true
            )
        }
    ) { padding ->
        Crossfade(nModel.state) { state ->
            when (state) {
                NetworkState.None -> {
                    CLazyColumn(
                        padding = padding
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
                                    val bringIntoViewRequester =
                                        remember { BringIntoViewRequester() }
                                    LaunchedEffect(state) {
                                        if (model.period == item.first) {
                                            coroutineScope.launch {
                                                bringIntoViewRequester.bringIntoView()
                                            }
                                        }
                                    }
                                    FilterChip(
                                        selected = model.period == item.first,
                                        onClick = {
                                            component.onEvent(
                                                FormRatingStore.Intent.ChangePeriod(item.first)
                                            )
                                            coroutineScope.launch {
                                                bringIntoViewRequester.bringIntoView()
                                            }

                                        },
                                        label = { Text(item.second) },
                                        modifier = Modifier.bringIntoViewRequester(
                                            bringIntoViewRequester
                                        )
                                    )
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
                                        component = component
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
                                    component = component
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

@Composable
private fun FormRatingCard(
    item: FormRatingStudent,
    topEd: Int,
    topMark: Int,
    topStup: Int,
    meLogin: String,
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
                        // Show trophy icon for top 3 positions
                        Icon(
                            imageVector = Icons.Rounded.EmojiEvents, // Replace with your trophy icon resource
                            contentDescription = "Top position",
                            tint = when (topEd) {
                                1 -> "#ffd700".toColor()
                                2 -> "#c0c0c0".toColor()
                                else -> "#cd7f32".toColor()
                            },
                            modifier = Modifier.size(35.dp)
                        )
                    } else {
                        // Show position number for other positions
                        Text(
                            text = topEd.toString(),
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Black,
                            fontStyle = FontStyle.Italic
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
            GetAvatar(
                avatarId = item.avatarId,
                name = item.fio.name,
                size = 40.dp,
                textSize = 20.sp
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${item.fio.surname} ${item.fio.name}",
                    fontSize = 18.sp, // Adjust font size for heading
                    lineHeight = 19.sp,
                    fontWeight = FontWeight.Bold // Make text bold for emphasis
                )
                Spacer(Modifier.height(1.dp))
                Text(
                    text = buildAnnotatedString {
                        if (item.formTitle != null) {
                            append(item.formTitle)
                        }
                    },
                    fontSize = 14.sp, // Adjust font size for body text
                    lineHeight = 15.sp,
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
                    fontSize = 18.sp,
                    lineHeight = 19.sp
                )
                Spacer(Modifier.height(1.dp))
                Text(
                    text = if (topStup != 0) {
                        "+${stups} (${topStup})"
                    } else if (item.edStups.isNotEmpty()) (if (stups.toString().first() != '-') "+" else "")+stups.toString()
                    else "",
                    fontSize = 14.sp,
                    lineHeight = 15.sp,
                    color = MaterialTheme.colorScheme.primary,//Color.Green,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}