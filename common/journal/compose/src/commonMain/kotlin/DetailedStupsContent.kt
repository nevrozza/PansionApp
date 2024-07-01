@file:OptIn(
    ExperimentalLayoutApi::class, ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class
)

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.AppBar
import components.BorderStup
import components.CLazyColumn
import components.CustomTextButton
import components.StupsButtons
import components.networkInterface.NetworkState
import detailedStups.DetailedStupsComponent
import detailedStups.DetailedStupsStore
import report.UserMark
import server.fetchReason
import server.getLocalDate
import view.LocalViewManager
import view.rememberImeState

@OptIn(ExperimentalFoundationApi::class)
@ExperimentalMaterial3Api
@ExperimentalLayoutApi
@Composable
fun DetailedStupsContent(
    component: DetailedStupsComponent
) {
    val model by component.model.subscribeAsState()
    val nModel by component.nInterface.networkModel.subscribeAsState()
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val viewManager = LocalViewManager.current
//    val scrollState = rememberScrollState()
    val imeState = rememberImeState()
    val lazyListState = rememberLazyListState()
    //PullToRefresh
//    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

//    val isFullView by mutableStateOf(this.maxWidth > 600.dp)


    Scaffold(
        Modifier.fillMaxSize(),
//                .nestedScroll(scrollBehavior.nestedScrollConnection)
        topBar = {
            AppBar(
                navigationRow = {
                    IconButton(
                        onClick = { component.onOutput(DetailedStupsComponent.Output.BackToHome) }
                    ) {
                        Icon(
                            Icons.Rounded.ArrowBackIosNew, null
                        )
                    }
                },
                title = {
                    Text(
                        "Ступени",
//                        modifier = Modifier.padding(start = 10.dp),
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                actionRow = {
                    AnimatedContent(
                        if (model.reason == "0") "За неделю" else "За год",
                        modifier = Modifier.padding(end = 5.dp)
                    ) {
                        CustomTextButton(
                            text = it
                        ) {
                            component.onEvent(DetailedStupsStore.Intent.ChangeReason)
                        }
                    }
                },
                isHaze = true
            )
            //LessonReportTopBar(component, isFullView) //, scrollBehavior
        }
    ) { padding ->
        Column(Modifier.fillMaxSize()) {
            Crossfade(nModel.state) { state ->
                when (state) {
                    NetworkState.None -> CLazyColumn(padding) {
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
                    Text(title, fontWeight = FontWeight.Bold, fontSize = 25.sp)
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
                    Icon(if (showDs.value) Icons.Rounded.Star else Icons.Rounded.StarOutline, null)
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
                                BorderStup(it.content)
                            }
                        }
                }
            }

        }

    }
}