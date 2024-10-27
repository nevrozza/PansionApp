@file:OptIn(ExperimentalMaterial3Api::class)

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.PlaylistAddCheckCircle
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Group
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.AppBar
import components.CLazyColumn
import components.CustomTextButton
import components.LoadingAnimation
import components.networkInterface.NetworkState
import dev.chrisbanes.haze.HazeState
import formRating.FormRatingStore
import home.HomeComponent
import home.HomeStore
import school.SchoolComponent
import school.SchoolStore
import view.LocalViewManager
import view.WindowScreen
import view.handy
import view.toColor
import kotlin.reflect.KClass


enum class SchoolRoutings {
    SchoolRating, FormRating
}

@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalLayoutApi
@Composable
fun SchoolContent(
    component: SchoolComponent,
    currentRouting: SchoolRoutings
) {
    val model by component.model.subscribeAsState()
    val nModel by component.nInterface.networkModel.subscribeAsState()
    val lazyListState = rememberLazyListState()
    val viewManager = LocalViewManager.current
    val isExpanded = viewManager.orientation.value == WindowScreen.Expanded
    val hazeState = remember { HazeState() }
    Scaffold(
        Modifier.fillMaxSize(),
        topBar = {
            AppBar(
                title = {

                    Text(
                        "Пансион",
                        modifier = Modifier.padding(start = 10.dp),
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    AnimatedVisibility(nModel.state == NetworkState.Loading) {
                        Row() {
                            Spacer(Modifier.width(10.dp))
                            CircularProgressIndicator(Modifier.size(25.dp))
                        }
                    }
                },
                actionRow = {
                    IconButton(
                        onClick = {
                            component.onEvent(SchoolStore.Intent.Init)
                        }
                    ) {
                        Icon(
                            Icons.Filled.Refresh, null
                        )
                    }
                },
                hazeState = hazeState,
                isHazeActivated = true
            )
        }
    ) { padding ->
        CLazyColumn(
            modifier = Modifier.animateContentSize(),
//                .pullRefresh(refreshState)
            state = lazyListState,
            padding = padding,
            isBottomPaddingNeeded = true,
            hazeState = hazeState
        ) {
            item {
                Row(Modifier.fillMaxWidth()) {
                    ElevatedCard(
                        Modifier.fillMaxWidth().clip(CardDefaults.elevatedShape)
                            .weight(1f)
                            .handy()
                            .clickable() {
                                component.onOutput(
                                    SchoolComponent.Output.NavigateToFormRating(
                                        login = model.login,
                                        formName = model.formName,
                                        formNum = model.formNum,
                                        formId = model.formId
                                    )
                                )
                            },
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = if (currentRouting == SchoolRoutings.FormRating && isExpanded) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceContainerLow
                        )
                    ) {
                        Column(
                            Modifier.padding(vertical = 10.dp, horizontal = 15.dp)
                                .fillMaxWidth().defaultMinSize(minHeight = 80.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                if (model.formName != null) "${model.formName} класс" else "Классы",
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
                                    Icons.Rounded.Group,
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
                            .handy()
                            .clickable(
                                enabled = !(currentRouting == SchoolRoutings.SchoolRating && isExpanded)
                            ) { //enabled = !isExpanded
                                component.onOutput(
                                    SchoolComponent.Output.NavigateToRating
                                )
                            },
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = if (currentRouting == SchoolRoutings.SchoolRating && isExpanded) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceContainerLow
                        )
                    ) {
                        Column(
                            Modifier.padding(vertical = 10.dp, horizontal = 15.dp)
                                .fillMaxWidth().defaultMinSize(minHeight = 80.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Школьный рейтинг",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
//                            Spacer(Modifier.height(5.dp))

                            val currentCFState = when {
                                nModel.state in listOf(
                                    NetworkState.Loading,
                                    NetworkState.Error
                                ) -> 0

                                model.top != null -> 1
                                else -> 2
                            }
                            Crossfade(currentCFState) { cf ->
                                Box(
                                    Modifier.fillMaxWidth()
                                        .padding(end = 5.dp, bottom = 5.dp)
                                        .height(24.dp),
                                    contentAlignment = Alignment.BottomEnd
                                ) {
                                    when (cf) {
                                        0 -> LoadingAnimation(
                                            circleColor = MaterialTheme.colorScheme.onSurface,
                                            circleSize = 8.dp,
                                            spaceBetween = 5.dp,
                                            travelDistance = 3.5.dp
                                        )

                                        1 -> {
                                            Box(contentAlignment = Alignment.Center) {
                                                if (model.top != null && model.top!! <= 3) {
                                                    // Show trophy icon for top 3 positions
                                                    Icon(
                                                        imageVector = Icons.Rounded.EmojiEvents, // Replace with your trophy icon resource
                                                        contentDescription = "Top position",
                                                        tint = when (model.top) {
                                                            1 -> "#ffd700".toColor()
                                                            2 -> "#c0c0c0".toColor()
                                                            else -> "#cd7f32".toColor()
                                                        }
                                                    )
                                                } else {
                                                    // Show position number for other positions
                                                    Text(
                                                        text = model.top.toString(),
                                                        fontSize = 23.sp,
                                                        fontWeight = FontWeight.Black,
                                                        fontStyle = FontStyle.Italic
                                                    )
                                                }
                                            }
                                        }

                                        2 ->
                                            Icon(
                                                Icons.Rounded.EmojiEvents,
                                                null,
                                                tint = MaterialTheme.colorScheme.secondary
                                            )
                                    }
                                }
//
                            }
                        }
                    }
                }
            }
            //errorItem
            item {
                AnimatedVisibility(
                    nModel.state == NetworkState.Error
                ) {
                    Column(
                        Modifier.fillMaxWidth(),//.height(100.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(Modifier.height(15.dp))
                        Text(nModel.error)
                        Spacer(Modifier.height(7.dp))
                        CustomTextButton("Попробовать ещё раз") {
                            nModel.onFixErrorClick()
                        }
                    }
                }
            }
            item {
                Spacer(Modifier.height(15.dp))
                ElevatedCard(
                    Modifier.fillMaxWidth().clip(CardDefaults.elevatedShape)
                ) {
                    Column(
                        Modifier.padding(vertical = 10.dp, horizontal = 15.dp)
                            .fillMaxWidth().defaultMinSize(minHeight = 80.dp),
//                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Дежурство",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = "В разработке",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            item {
                Spacer(Modifier.height(15.dp))
                Text(
                    "Министерства",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(start = 10.dp)
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = "В разработке",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}