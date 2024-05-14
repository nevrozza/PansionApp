import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.painter.BrushPainter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
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
import components.listDialog.ListDialogStore
import components.networkInterface.NetworkState
import decomposeComponents.listDialogComponent.ListDialogDesktopContent
import decomposeComponents.listDialogComponent.ListDialogMobileContent
import journal.JournalComponent
import journal.JournalStore
import pullRefresh.PullRefreshIndicator
import pullRefresh.pullRefresh
import pullRefresh.rememberPullRefreshState
import rating.RatingComponent
import rating.RatingItem
import rating.RatingStore
import view.LocalViewManager
import view.WindowScreen
import view.rememberImeState
import view.toColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingContent(
    component: RatingComponent
) {
    val model by component.model.subscribeAsState()
    val nModel by component.nInterface.networkModel.subscribeAsState()
    val focusManager = LocalFocusManager.current
    val viewManager = LocalViewManager.current
//    val scrollState = rememberScrollState()
    val imeState = rememberImeState()
    val lazyListState = rememberLazyListState()

    val refreshState = rememberPullRefreshState(
        nModel.state == NetworkState.Loading,
        { component.onEvent(RatingStore.Intent.Init) })

    Scaffold(
        Modifier.fillMaxSize(),
        topBar = {
            AppBar(
                title = {
                    AnimatedContent(
                        model.subjects.firstOrNull() { it.id == model.currentSubject }?.name
                            ?: "Загрузка"
                    ) {
                        Text(
                            it,
                            modifier = Modifier.padding(start = 10.dp),
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Black,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                actionRow = {
                    IconButton(
                        onClick = { component.onEvent(RatingStore.Intent.Init) }
                    ) {
                        Icon(
                            Icons.Filled.Refresh, null
                        )
                    }

                    Box() {
                        IconButton(
                            onClick = {
                                component.subjectsListComponent.onEvent(ListDialogStore.Intent.ShowDialog)
                            }
                        ) {
                            Icon(Icons.Outlined.Tune, null)
                        }
                        ListDialogDesktopContent(component.subjectsListComponent)
                    }
                    if (viewManager.orientation.value == WindowScreen.Expanded) {
                        IconButton(
                            onClick = {
                                component.onOutput(RatingComponent.Output.NavigateToSettings)
                            }
                        ) {
                            Icon(
                                Icons.Rounded.Settings, null
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize()) {
            val items = model.items[model.currentSubject]
            if (!items.isNullOrEmpty()) {
                CLazyColumn(
                    padding = padding,
                    isBottomPaddingNeeded = true,
                    modifier = Modifier.pullRefresh(refreshState)
                ) {
                    items(items) { i ->
                        RatingCard(i)
                        RatingCard(i.copy(top = 4))
                    }
                }
            } else {
                Text("Здесь пусто 0_0")
            }
            ListDialogMobileContent(component.subjectsListComponent)
            PullRefreshIndicator(
                modifier = Modifier.align(alignment = Alignment.TopCenter)
                    .padding(top = padding.calculateTopPadding()),
                refreshing = nModel.state == NetworkState.Loading,
                state = refreshState,
            )
        }
    }
}

@Composable
private fun RatingCard(item: RatingItem) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(elevation = 4.dp),
        shape = RoundedCornerShape(8.dp),
    ) {
        Row(modifier = Modifier.padding(end = 16.dp, start = 8.dp).padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(40.dp), contentAlignment = Alignment.Center) {
                if (item.top <= 3) {
                    // Show trophy icon for top 3 positions
                    Icon(
                        imageVector = Icons.Rounded.EmojiEvents, // Replace with your trophy icon resource
                        contentDescription = "Top position",
                        tint = when (item.top) {
                            1 -> "#ffd700".toColor()
                            2 -> "#c0c0c0".toColor()
                            else -> "#cd7f32".toColor()
                        },
                        modifier = Modifier.size(35.dp)
                    )
                } else {
                    // Show position number for other positions
                    Text(
                        text = item.top.toString(),
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
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
                    fontWeight = FontWeight.Bold // Make text bold for emphasis
                )
                Text(
                    text = "${item.formShortTitle}: ${item.groupName}",
                    fontSize = 14.sp, // Adjust font size for body text
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = item.avg,
                    fontSize = 18.sp
                )
                Text(
                    text = "+${item.stups}",
                    fontSize = 12.sp,
                    color = Color.Green
                )
            }
        }
    }
}