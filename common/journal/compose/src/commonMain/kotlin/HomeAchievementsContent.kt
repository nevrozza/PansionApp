
import achievements.HomeAchievementsComponent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.AppBar
import components.CLazyColumn
import components.CustomTextButton
import components.GetAvatar
import components.networkInterface.NetworkState
import dev.chrisbanes.haze.HazeState

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
    val hazeState = remember { HazeState() }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AppBar(
                navigationRow = {
                    IconButton(
                        onClick = { component.onOutput(HomeAchievementsComponent.Output.Back) }
                    ) {
                        Icon(
                            Icons.Rounded.ArrowBackIosNew, null
                        )
                    }
                },
                title = {
                    Box(Modifier.sharedElementWithCallerManagedVisibility(
                        sharedContentState = rememberSharedContentState(key = "EventsTitle"),
                        visible = isVisible
                    )) {
                        Text(
                            "События",
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                actionRow = {
                    GetAvatar(
                        avatarId = model.avatarId,
                        name = model.name,
                        size = 35.dp,
                        textSize = 13.sp,
                        modifier = Modifier.padding(end = 10.dp).sharedElementWithCallerManagedVisibility(
                            sharedContentState = rememberSharedContentState(key = model.login + "avatar"),
                            visible = isVisible
                        )
                    )
                },
                hazeState = hazeState
            )
        }
    ) { padding ->
        Crossfade(nModel.state, modifier = Modifier.fillMaxSize()) { state ->
            when (state) {
                NetworkState.None -> CLazyColumn(padding = padding, hazeState = hazeState) {
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
                            }, fontSize = 20.sp, fontWeight = FontWeight.Black)
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

                NetworkState.Error -> {
                    Column(
                        Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
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