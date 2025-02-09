@file:OptIn(
    ExperimentalLayoutApi::class, ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class
)

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.*
import components.foundation.AppBar
import components.foundation.CLazyColumn
import components.foundation.CTextButton
import components.foundation.DefaultErrorView
import components.foundation.DefaultErrorViewPos
import components.journal.BorderStup
import components.journal.StupsButtons
import components.networkInterface.NetworkState
import components.networkInterface.isLoading
import detailedStups.DetailedStupsComponent
import detailedStups.DetailedStupsStore
import report.UserMark
import resources.RIcons
import server.fetchReason
import server.getLocalDate

@OptIn(ExperimentalFoundationApi::class)
@ExperimentalMaterial3Api
@ExperimentalLayoutApi
@Composable
fun DetailedStupsContent(
    component: DetailedStupsComponent
) {




    val model by component.model.subscribeAsState()
    val nModel by component.nInterface.networkModel.subscribeAsState()


    LaunchedEffect(Unit) {
        if(!nModel.isLoading) component.onEvent(DetailedStupsStore.Intent.Init)
    }

    //PullToRefresh
//    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

//    val isFullView by mutableStateOf(this.maxWidth > 600.dp)


    Scaffold(
        Modifier.fillMaxSize(),
//                .nestedScroll(scrollBehavior.nestedScrollConnection)
        topBar = {
            AppBar(
                title = {
                    Text(
                        "Ступени",
//                        modifier = Modifier.padding(start = 10.dp),
                        fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationRow = {
                    IconButton(
                        onClick = { component.onOutput(DetailedStupsComponent.Output.Back) }
                    ) {
                        GetAsyncIcon(
                            path = RIcons.CHEVRON_LEFT
                        )
                    }
                },
                actionRow = {
                    AnimatedContent(
                        if (model.reason == "0") "За неделю" else "За год",
                        modifier = Modifier.padding(end = 5.dp)
                    ) {
                        CTextButton(
                            text = it
                        ) {
                            component.onEvent(DetailedStupsStore.Intent.ChangeReason)
                        }
                    }
                }
            )
            //LessonReportTopBar(component, isFullView) //, scrollBehavior
        }
    ) { padding ->
        Column(Modifier.fillMaxSize()) {
            Crossfade(nModel.state) { state ->
                when (state) {
                    NetworkState.None -> CLazyColumn(padding) {
                        item {
                            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                FilledTonalButton(
                                    onClick = {
                                        component.onOutput(
                                            DetailedStupsComponent.Output.NavigateToAchievements(
                                                login = model.login,
                                                name = model.name,
                                                avatarId = model.avatarId
                                            )
                                        )
                                    }
                                ) {
                                    Text("Открыть события")
                                }
                            }
                        }
                        items(model.subjects.sortedBy {
                            it.stups.filter {
                                it.reason.subSequence(
                                    0,
                                    3
                                ) != "!ds"
                            }
                                .filter { if (model.reason == "0") it.date in model.weekDays else true }
                                .sumOf { it.content.toInt() }
                        }.reversed()) { s ->
                            val showDs = remember { mutableStateOf(true) }
                            val stups = s.stups
                                .filter { if (model.reason == "0") it.date in model.weekDays else true }
                            if (stups.isNotEmpty()) {
                                DetailedStupsSubjectItem(
                                    title = s.subjectName,
                                    stups = stups,
                                    showDs = showDs
                                )
                            }
                        }
                    }

                    NetworkState.Loading -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }

                    NetworkState.Error -> DefaultErrorView(nModel, DefaultErrorViewPos.CenteredFull)
                }
            }
        }

    }

}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DetailedStupsSubjectItem(
    title: String,
    stups: List<UserMark>,
    showDs: MutableState<Boolean>,
) {
    val isFullView = remember { mutableStateOf(false) }

    ElevatedCard(
        Modifier.fillMaxWidth().padding(top = 10.dp) //.padding(horizontal = 10.dp)
            .animateContentSize().clip(CardDefaults.elevatedShape).clickable {
                isFullView.value = !isFullView.value
            }) {
        Column(Modifier.padding(5.dp).padding(start = 5.dp)) {
            Row(
                Modifier.fillMaxWidth().padding(end = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(title, fontWeight = FontWeight.Medium, fontSize = MaterialTheme.typography.headlineSmall.fontSize)
                    StupsButtons(
                        stups = stups.map {
                            Pair(it.content.toInt(), it.reason)
                        }
                    )
                }

                IconButton(
                    onClick = {
                        showDs.value = !showDs.value
                    }
                ) {
                    GetAsyncIcon(
                        path = if (showDs.value) RIcons.STAR else RIcons.STAR_OUTLINE
                    )
                }
            }
            if (isFullView.value) {
                Column(Modifier.padding(end = 10.dp)) {
                    stups.filter {
                        if (!showDs.value) it.reason.subSequence(
                            0,
                            3
                        ) != "!ds" else true
                    }
                        .sortedBy { getLocalDate(it.date).toEpochDays() }.reversed().forEach {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                                    .padding(horizontal = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(it.date)
                                Text(fetchReason(it.reason))
                                BorderStup(it.content, reason = it.reason)
                            }
                        }
                }
            }

        }

    }
}