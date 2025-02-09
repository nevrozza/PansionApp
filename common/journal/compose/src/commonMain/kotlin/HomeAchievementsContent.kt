
import achievements.HomeAchievementsComponent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.*
import components.networkInterface.NetworkState
import resources.RIcons
import androidx.compose.desktop.ui.tooling.preview.utils.esp
import components.foundation.AppBar
import components.foundation.CLazyColumn
import components.foundation.DefaultErrorView
import components.foundation.DefaultErrorViewPos

@OptIn(
    ExperimentalLayoutApi::class, ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class
)
@Composable
fun SharedTransitionScope.HomeAchievementsContent(
    component: HomeAchievementsComponent,
    isVisible: Boolean
) {

    val model by component.model.subscribeAsState()
    val nModel by component.nInterface.networkModel.subscribeAsState()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AppBar(
                title = {
                    Box(
                        Modifier.sharedElementWithCallerManagedVisibility(
                            sharedContentState = rememberSharedContentState(key = "EventsTitle"),
                            visible = isVisible
                        )
                    ) {
                        Text(
                            "События",
                            fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                navigationRow = {
                    IconButton(
                        onClick = { component.onOutput(HomeAchievementsComponent.Output.Back) }
                    ) {
                        GetAsyncIcon(
                            path = RIcons.CHEVRON_LEFT
                        )
                    }
                },
                actionRow = {
                    GetAsyncAvatar(
                        avatarId = model.avatarId,
                        name = model.name,
                        size = 35.dp,
                        textSize = 13.esp,
                        modifier = Modifier.padding(end = 10.dp)
                                                    .sharedElementWithCallerManagedVisibility(
                                                        sharedContentState = rememberSharedContentState(key = model.login + "avatar"),
                                                        visible = isVisible
                                                    )
                    )
                }
            )
        }
    ) { padding ->
        Crossfade(nModel.state, modifier = Modifier.fillMaxSize()) { state ->
            when (state) {
                NetworkState.None -> CLazyColumn(padding = padding) {
                    items(model.achievements.sortedBy { it.id }.reversed()) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp).padding(horizontal = 5.dp)
                        ) {
                            Text(buildAnnotatedString {
                                append("${it.text} ")
                                withStyle(
                                    SpanStyle(
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.onBackground.copy(
                                            alpha = .5f
                                        )
                                    )
                                ) {
                                    append(it.date)
                                }
                            }, fontSize = MaterialTheme.typography.titleLarge.fontSize, fontWeight = FontWeight.Black)
                            Spacer(Modifier.height(3.dp))
                            Text(buildAnnotatedString {
                                append(model.subjects[it.subjectId].toString())
                                withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                                    append(" +${it.stups}")
                                }
                            }, fontWeight = FontWeight.SemiBold, modifier = Modifier.align(Alignment.End))
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
}