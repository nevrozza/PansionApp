package homeComponents

import DotsFlashing
import FIO
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ManageSearch
import androidx.compose.material.icons.outlined.PlaylistAddCheckCircle
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import cGrade
import components.CustomTextButton
import components.GetAvatar
import components.LoadingAnimation
import components.networkInterface.NetworkInterface
import components.networkInterface.NetworkState
import home.HomeComponent
import home.HomeStore
import kotlinx.coroutines.CoroutineScope
import main.Period
import resources.Images
import server.roundTo
import studentReportDialog.StudentReportDialogStore
import view.LocalViewManager
import view.WindowScreen
import view.handy


@OptIn(ExperimentalSharedTransitionApi::class)
fun LazyListScope.homeStudentBar(
    model: HomeStore.State,
    nGradesModel: NetworkInterface.NetworkModel,
    nQuickTabModel: NetworkInterface.NetworkModel,
    component: HomeComponent,
    coroutineScope: CoroutineScope,
    sharedTransitionScope: SharedTransitionScope,
    isSharedVisible: Boolean,
//    isExpanded: Boolean
) {
    item {
        val viewManager = LocalViewManager.current
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
                            GetAvatar(
                                avatarId = model.avatarId,
                                name = model.name,
                                modifier = Modifier.then(
                                    if (viewManager.orientation.value != WindowScreen.Expanded) Modifier.sharedElementWithCallerManagedVisibility(
                                        sharedContentState = rememberSharedContentState(key = model.login + "avatar"),
                                        visible = isSharedVisible
                                    )
                                    else Modifier
                                )
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
                                fontWeight = FontWeight.Bold,
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
                                    fontWeight = FontWeight.Bold,
                                    //color = MaterialTheme.colorScheme.primary//secondary
                                ) {
                                    component.onEvent(HomeStore.Intent.ChangePeriod)
                                }
                            }
                        }
                        Crossfade(nQuickTabModel.state) {
                            Column {
                                when (it) {
                                    NetworkState.Error -> {
                                        Text("Ошибка")
                                        CustomTextButton("Попробовать ещё раз") {
                                            nQuickTabModel.onFixErrorClick()
                                        }
                                    }

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
                    Icon(
                        Icons.AutoMirrored.Rounded.ManageSearch, null
                    )
                }
            }
        }
        Spacer(Modifier.height(15.dp))
        Row(Modifier.fillMaxWidth()) {
            ElevatedCard(
                Modifier.fillMaxWidth().clip(CardDefaults.elevatedShape)
                    .weight(1f)
                    .handy()
                    .clickable() {
                        component.onOutput(
                            HomeComponent.Output.NavigateToDnevnikRuMarks(
                                model.login
                            )
                        )
                    }
            ) {
                Column(
                    Modifier.padding(vertical = 10.dp, horizontal = 15.dp)
                        .fillMaxWidth().defaultMinSize(minHeight = 80.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Оценки",
                        fontSize = MaterialTheme.typography.titleMedium.fontSize,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(5.dp))
                    Box(
                        Modifier.fillMaxWidth()
                            .padding(end = 5.dp, bottom = 5.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Icon(
                            Icons.Outlined.PlaylistAddCheckCircle,
                            null,
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
            Spacer(Modifier.width(15.dp))
            ElevatedCard(
                Modifier.fillMaxWidth().clip(CardDefaults.elevatedShape)
                    .weight(1f)
                    .clickable() {
                        component.onOutput(
                            HomeComponent.Output.NavigateToTasks(
                                studentLogin = model.login,
                                avatarId = model.avatarId,
                                name = model.name
                            )
                        )
                    }
            ) {
                Column(
                    Modifier.padding(vertical = 10.dp, horizontal = 15.dp)
                        .fillMaxWidth().defaultMinSize(minHeight = 80.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Домашние задания",
                        fontSize = MaterialTheme.typography.titleMedium.fontSize,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(5.dp))

                    if (model.homeWorkEmojiCount != null) {
                        Row(
                            Modifier.fillMaxWidth()
                                .padding(end = 5.dp, bottom = 5.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            val deathsCount = ((model.homeWorkEmojiCount!! - 4) / 2)
                            if (deathsCount > 0) {
                                for (i in 0..<deathsCount) {
                                    Image(
                                        Images.Emoji.emoji6,
                                        null,
                                        modifier = Modifier.size(25.dp),
                                        contentScale = ContentScale.Crop,
                                        filterQuality = FilterQuality.Low
                                    )
                                }
                            } else {
                                val emoji = when (model.homeWorkEmojiCount!!) {
                                    0 -> Images.Emoji.emoji0
                                    1 -> Images.Emoji.emoji1
                                    2 -> Images.Emoji.emoji2
                                    3 -> Images.Emoji.emoji3
                                    4 -> Images.Emoji.emoji4
                                    else -> Images.Emoji.emoji5
                                }
                                Image(
                                    emoji,
                                    null,
                                    modifier = Modifier.size(25.dp),
                                    contentScale = ContentScale.Crop,
                                    filterQuality = FilterQuality.None
                                )
                            }
                            //fun getEmoji(count: Int): String {
//    return when(count) {
//        0 -> Emojis.check
//        1 -> Emojis.smileTeeth
//        2 -> Emojis.smile
//        3 -> Emojis.normal
//        4 -> Emojis.scared
//        5 -> Emojis.horror
//        else -> {
//            val deathsCount = ((count - 6) / 2)
//            var d = Emojis.death
//            for (i in 0..<deathsCount) {
//                d += Emojis.death
//            }
//            return d
//        }
//    }
//}
                        }
                    } else {
                        Box(
                            Modifier.fillMaxWidth()
                                .padding(end = 5.dp, bottom = 5.dp),
                            contentAlignment = Alignment.BottomEnd
                        ) {
                            LoadingAnimation(
                                circleColor = MaterialTheme.colorScheme.onSurface,
                                circleSize = 8.dp,
                                spaceBetween = 5.dp,
                                travelDistance = 3.5.dp
                            )
//                                        DotsFlashing(Modifier)
                        }
                    }


                }
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

                    NetworkState.Error -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(nGradesModel.error)
                        Spacer(Modifier.height(7.dp))
                        CustomTextButton("Попробовать ещё раз") {
                            nGradesModel.onFixErrorClick()
                        }
                    }
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
                if (value == value?.toInt()?.toFloat()) {
                    value.toInt().toString()
                } else {
                    value.roundTo(2).toString()
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
            withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
                append("$title: ")
            }
            append(value)
        },
        color = MaterialTheme.colorScheme.onSurface
    ) {
        onClick()
    }
}