package homeComponents

import DotsFlashing
import FIO
import HomeRoutings
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import cGrade
import components.CustomTextButton
import components.DefaultErrorView
import components.DefaultErrorViewPos
import components.FeatureButton
import components.GetAsyncAvatar
import components.GetAsyncIcon
import components.GetAsyncImage
import components.LoadingAnimation
import components.networkInterface.NetworkInterface
import components.networkInterface.NetworkState
import home.HomeComponent
import home.HomeStore
import kotlinx.coroutines.CoroutineScope
import main.Period
import resources.Images
import resources.RIcons
import server.roundTo
import studentReportDialog.StudentReportDialogStore
import view.LocalViewManager
import view.WindowScreen
import view.esp


@OptIn(ExperimentalSharedTransitionApi::class)
fun LazyListScope.homeStudentBar(
    model: HomeStore.State,
    nGradesModel: NetworkInterface.NetworkModel,
    nTeacherModel: NetworkInterface.NetworkModel,
    nQuickTabModel: NetworkInterface.NetworkModel,
    component: HomeComponent,
    coroutineScope: CoroutineScope,
    sharedTransitionScope: SharedTransitionScope,
    isSharedVisible: Boolean,
    currentRouting: HomeRoutings
//    isExpanded: Boolean
) {
    item {
        val viewManager = LocalViewManager.current
        if (viewManager.hardwareStatus.value.isNotBlank()) {
            Text(
                viewManager.hardwareStatus.value,
                modifier = Modifier.padding(start = 5.dp, bottom = 5.dp),
                fontSize = 14.esp,
                lineHeight = 15.esp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = .7f)
            )
        }
        ElevatedCard(Modifier.fillMaxWidth()) {
            Box() {
                Row(
                    Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.clip(CircleShape).clickable {
                            component.onOutput(
                                HomeComponent.Output.NavigateToProfile(
                                    studentLogin = model.login,
                                    fio = FIO(
                                        name = model.name,
                                        praname = model.praname,
                                        surname = model.surname
                                    ),
                                    avatarId = model.avatarId
                                )
                            )
                        }
                    ) {
                        with(sharedTransitionScope) {
                            GetAsyncAvatar(
                                avatarId = model.avatarId,
                                name = model.name,
                                modifier = Modifier.then(
                                    if (viewManager.orientation.value != WindowScreen.Expanded) Modifier.sharedElementWithCallerManagedVisibility(
                                        sharedContentState = rememberSharedContentState(key = model.login + "avatar"),
                                        visible = isSharedVisible
                                    )
                                    else Modifier
                                ),
                                ignoreShowAvatars = true
                            )
                        }
                    }
                    Spacer(Modifier.width(15.dp))
                    Column {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                model.name,
                                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                fontWeight = FontWeight.SemiBold,
                            )
                            AnimatedContent(
                                when (model.period) {
                                    Period.WEEK -> "неделя"
                                    Period.MODULE -> "модуль"
                                    Period.HALF_YEAR -> "полугодие"
                                    Period.YEAR -> "год"
                                },
                                transitionSpec = {
                                    fadeIn().togetherWith(fadeOut())
                                }
                            ) {
                                CustomTextButton(
                                    text = it,
                                    fontWeight = FontWeight.SemiBold,
                                    //color = MaterialTheme.colorScheme.primary//secondary
                                ) {
                                    component.onEvent(HomeStore.Intent.ChangePeriod)
                                }
                            }
                        }
                        Crossfade(nQuickTabModel.state) {
                            Column {
                                when (it) {
                                    NetworkState.Error -> DefaultErrorView(
                                        nQuickTabModel,
                                        pos = DefaultErrorViewPos.Unspecified,
                                        text = "Ошибка"
                                    )

                                    else -> {
                                        QuickTabItem(
                                            "Средний балл",
                                            value = model.averageGradePoint[model.period]
                                        ) {

                                        }
                                        val achievementsPairAdd =
                                            if (model.achievements[model.period] != null) Pair(
                                                model.achievements[model.period]!!.first,
                                                model.achievements[model.period]!!.second
                                            ) else Pair(0, 0)
                                        val ladders =
                                            if (model.ladderOfSuccess[model.period] != null)
                                                (model.ladderOfSuccess[model.period]!! + achievementsPairAdd.first).toFloat()
                                            else null

                                        QuickTabItem(
                                            "Ступени",
                                            value = ladders
                                        ) {
                                            component.onOutput(
                                                HomeComponent.Output.NavigateToDetailedStups(
                                                    studentLogin = model.login,
                                                    reason = model.period.ordinal,
                                                    name = model.name,
                                                    avatarId = model.avatarId
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
                IconButton(
                    onClick = {
                        component.onOutput(
                            HomeComponent.Output.NavigateToStudentLines(
                                model.login
                            )
                        )
                    },
                    modifier = Modifier.align(Alignment.BottomEnd)
                ) {
                    GetAsyncIcon(
                        RIcons.ManageSearch
                    )
                }
            }
        }

        Spacer(Modifier.height(15.dp))
        Row(Modifier.fillMaxWidth().height(IntrinsicSize.Max)) {
            FeatureButton(
                text = "Оценки",
                decoration = RIcons.PlaylistAddCheckCircle,
                isActive = currentRouting == HomeRoutings.Dnevnik
            ) {
                component.onOutput(
                    HomeComponent.Output.NavigateToDnevnikRuMarks(
                        model.login
                    )
                )
            }
            Spacer(Modifier.width(15.dp))
            FeatureButton(
                text = "Домашние задания",
                decoration = {
                    if (nTeacherModel.state != NetworkState.Loading) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            val deathsCount = (((model.homeWorkEmojiCount ?: 4) - 4) / 2)
                            if (deathsCount > 0) {
                                for (i in 0..<deathsCount) {
                                    GetAsyncImage(
                                        Images.Emoji.emoji6
                                    )
                                }
                            } else {
                                val emoji = when (model.homeWorkEmojiCount) {
                                    0 -> Images.Emoji.emoji0
                                    1 -> Images.Emoji.emoji1
                                    2 -> Images.Emoji.emoji2
                                    3 -> Images.Emoji.emoji3
                                    4 -> Images.Emoji.emoji4
                                    null -> Images.Emoji.emoji7
                                    else -> Images.Emoji.emoji5
                                }

                                GetAsyncImage(
                                    emoji
                                )
                            }
                        }
                    } else {

                        LoadingAnimation(
                            circleColor = MaterialTheme.colorScheme.onSurface,
                            circleSize = 8.dp,
                            spaceBetween = 5.dp,
                            travelDistance = 3.5.dp
                        )
//                                        DotsFlashing(Modifier)
                    }

                },
                isActive = currentRouting == HomeRoutings.Tasks
            ) {
                component.onOutput(
                    HomeComponent.Output.NavigateToTasks(
                        studentLogin = model.login,
                        avatarId = model.avatarId,
                        name = model.name
                    )
                )
            }
        }
        Spacer(Modifier.height(5.dp))
        Crossfade(
            nGradesModel.state,
            modifier = Modifier.animateContentSize()
        ) {
            Box(
                Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                when (it) {
                    NetworkState.None ->
                        LazyRow(Modifier.fillMaxWidth()) {
                            items(model.grades.reversed()) {
                                cGrade(it, coroutineScope) {
                                    if (it.reportId != null) {
                                        component.studentReportDialog.onEvent(
                                            StudentReportDialogStore.Intent.OpenDialog(
                                                login = model.login,
                                                reportId = it.reportId!!
                                            )
                                        )
                                    }
                                }
                            }
                        }

                    NetworkState.Loading -> {
                        Box(
                            Modifier.height(60.dp).offset(y = 5.dp)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            LoadingAnimation()
                        }
                    }

                    NetworkState.Error -> DefaultErrorView(
                        nGradesModel,
                        DefaultErrorViewPos.CenteredNotFull
                    )
                }
            }
        }
        if (model.notifications.isNotEmpty()) {
            Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
private fun QuickTabItem(
    title: String,
    value: Float?,
    onClick: () -> Unit
) {


    Row(verticalAlignment = Alignment.CenterVertically) {
        val endValue = if (value != null) {
            if (value.isNaN()) {
                "NaN"
            } else {
                if (value == value.toInt().toFloat()) {
                    value.toInt().toString()
                } else {
                    value.roundTo(2)
                }
            }
            //, fontWeight = FontWeight.Bold, fontSize = 25.sp
        } else {
            ""
        }
        QuickTabNotNull(
            title = title,
            value = endValue
        ) {
            onClick()
        }
        if (value == null) {
            DotsFlashing(Modifier.padding(top = 5.dp))
        }
    }
}

@Composable
private fun QuickTabNotNull(
    title: String,
    value: String,
    onClick: () -> Unit
) {
    CustomTextButton(
        text = buildAnnotatedString {
            withStyle(SpanStyle(fontWeight = FontWeight.Normal)) {
                append("$title: ")
            }
            append(value)
        },
        color = MaterialTheme.colorScheme.onSurface
    ) {
        onClick()
    }
}