import admin.AdminComponent
import admin.AdminItem
import admin.AdminStore
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.PlaylistAddCheckCircle
import androidx.compose.material.icons.rounded.ArrowForwardIos
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.AppBar
import components.CustomTextButton
import components.LoadingAnimation
import home.HomeComponent
import home.HomeStore
import journal.JournalComponent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pullRefresh.PullRefreshIndicator
import pullRefresh.pullRefresh
import pullRefresh.rememberPullRefreshState
import view.LocalViewManager
import view.WindowScreen
import view.rememberImeState

@ExperimentalLayoutApi
@Composable
fun AdminContent(
    component: AdminComponent,
    isActive: Boolean = false,
    currentRouting: AdminComponent.Output? = null
) {
    val model by component.model.subscribeAsState()
    val focusManager = LocalFocusManager.current
    val viewManager = LocalViewManager.current
//    val scrollState = rememberScrollState()
    val imeState = rememberImeState()
    val lazyListState = rememberLazyListState()

    Scaffold(
        Modifier.fillMaxSize(),
        topBar = {
            AppBar(
                title = {

                    Text(
                        "Модерация",
                        modifier = Modifier.padding(start = 10.dp),
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Black, maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                },
//                actionRow = {
//                    if(isActive) {
//                        IconButton(
//                            onClick = {
//
//                            }
//                        ) {
//                            Icon(Icons.Outlined.Info, null)
//                        }
//                    }
//                }
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            if (model.items != null) {
                LazyColumn(
                    Modifier
                        .padding(horizontal = 15.dp)
                        .fillMaxSize()
                        .consumeWindowInsets(padding)
                        .imePadding()
                ) {
                    items(model.items!!) { item ->
                        AdminItemCompose(item.title, currentRouting == item.routing, isActive) {
                            component.onOutput(item.routing)
                        }
                        Spacer(Modifier.height(10.dp))
                    }
                }
            } else {

            }
        }

    }
}

@Composable
fun AdminItemCompose(title: String, isEnabled: Boolean, isActive: Boolean, onClick: () -> Unit) {
    FilledTonalButton(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            onClick()
        },
        shape = RoundedCornerShape(30),
        colors = ButtonDefaults.filledTonalButtonColors(containerColor = if (isEnabled && !isActive) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp), contentColor = if (isEnabled && !isActive) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface)
    ) {
        Row(Modifier.fillMaxWidth().padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Icon(Icons.Rounded.ArrowForwardIos, null)
        }
    }
}