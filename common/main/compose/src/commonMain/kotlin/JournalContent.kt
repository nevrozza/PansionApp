import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.AppBar
import components.listDialog.ListDialogStore
import decomposeComponents.CAlertDialogContent
import decomposeComponents.ListDialogContent
import journal.JournalComponent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pullRefresh.PullRefreshIndicator
import pullRefresh.pullRefresh
import pullRefresh.rememberPullRefreshState
import view.LocalViewManager
import view.WindowScreen
import view.rememberImeState

@ExperimentalMaterial3Api
@ExperimentalLayoutApi
@Composable
fun JournalContent(
    component: JournalComponent,
    isNotMinimized: Boolean = true
) {
    val model by component.model.subscribeAsState()
    val focusManager = LocalFocusManager.current
    val viewManager = LocalViewManager.current
//    val scrollState = rememberScrollState()
    val imeState = rememberImeState()
    val lazyListState = rememberLazyListState()


    //PullToRefresh
    val refreshScope = rememberCoroutineScope()
    var refreshing by remember { mutableStateOf(false) }
    fun refresh() = refreshScope.launch {
        refreshing = true
        delay(1000)
        refreshing = false
    }

    val refreshState = rememberPullRefreshState(refreshing, ::refresh)

    Scaffold(
        Modifier.fillMaxSize()
            .onKeyEvent {
                if (it.key == Key.F5 && it.type == KeyEventType.KeyDown) {
                    refresh()
                }
                false
            },
        topBar = {
            AppBar(
                title = {

                    Text(
                        "Журнал",
                        modifier = Modifier.padding(start = 10.dp),
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                actionRow = {
//                    var x by remember { mutableStateOf(0.0f) }
                    IconButton(

                        onClick = {
                            component.groupListDialogComponent.onEvent(
                                ListDialogStore.Intent.ShowDialog(
                                    x = if (viewManager.size!!.maxWidth - (viewManager.size!!.maxWidth / 4 + viewManager.size!!.maxWidth / 2) >= 250.dp) viewManager.size!!.maxWidth.value / 4 else viewManager.size!!.maxWidth.value / 5,
                                    y = 50.0f
                                )
                            )
                        }
                    ) {
                        Icon(
                            Icons.Rounded.Add, null,
//                            modifier = Modifier.onGloballyPositioned {
//                                x =
//                            }
                        )
                    }

                    IconButton(
                        onClick = { refresh() }
                    ) {
                        Icon(
                            Icons.Filled.Refresh, null
                        )
                    }
                    if (viewManager.orientation.value == WindowScreen.Expanded && isNotMinimized) {
                        IconButton(
                            onClick = { }
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
        Box(Modifier.fillMaxSize().padding(padding)) {
            LazyColumn(
                Modifier
                    .padding(horizontal = 15.dp)
                    .fillMaxSize()
                    .consumeWindowInsets(padding)
                    .imePadding()
                    .pullRefresh(refreshState)
            ) {
                items(3) { num ->

                }
            }


            PullRefreshIndicator(
                modifier = Modifier.align(alignment = Alignment.TopCenter),
                refreshing = refreshing,
                state = refreshState,
            )
            ListDialogContent(component.groupListDialogComponent)
            CAlertDialogContent(
                component.studentsInGroupCAlertDialogComponent,
                title = "Ученики"
            ) {
                LazyColumn {
                    items(model.studentsInGroup) {
                        for (i in 1..10) {
                            TextButton(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {},
                                enabled = false,
                                colors = ButtonDefaults.textButtonColors(
                                    disabledContentColor = MaterialTheme.colorScheme.onSurface
                                ),
                                shape = RoundedCornerShape(15.dp)
                            ) {
                                Text("${it.surname} ${it.name} ${it.praname}")
                            }
                        }

                    }
                }
            }

        }
    }
}