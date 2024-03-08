import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.AppBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pullRefresh.PullRefreshIndicator
import pullRefresh.pullRefresh
import pullRefresh.rememberPullRefreshState
import view.LocalViewManager
import view.rememberImeState

@ExperimentalMaterial3Api
@ExperimentalLayoutApi
@Composable
fun LessonReportContent(
    component: LessonReportComponent
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
//    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val refreshState = rememberPullRefreshState(refreshing, ::refresh)
    BoxWithConstraints {

        val isFullView by mutableStateOf(this.maxWidth > 600.dp)
        println(isFullView)
        Scaffold(
            Modifier.fillMaxSize()
//                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .onKeyEvent {
                    if (it.key == Key.F5 && it.type == KeyEventType.KeyDown) {
                        refresh()
                    }
                    false
                },
            topBar = {
                LessonReportTopBar(component, isFullView) //, scrollBehavior
            }
        ) { padding ->
            Box(Modifier.fillMaxSize().padding(padding)) {
                LazyColumn(
                    Modifier
                        .padding(horizontal = 15.dp)
                        .fillMaxSize()
                        .consumeWindowInsets(padding)
                        .imePadding()
                        .pullRefresh(refreshState),
                    state = lazyListState
                ) {
                    if (!isFullView) {
                        item {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val image = Icons.Rounded.Person
                                    // Please provide localized description for accessibility services
                                    val description = "Teacher"
                                    Icon(
                                        imageVector = image,
                                        description,
                                        modifier = Modifier.size(20.dp).offset(y = 1.dp)
                                    )
                                    Spacer(Modifier.width(5.dp))
                                    Text(
                                        buildAnnotatedString {
                                            withStyle(
                                                SpanStyle(
                                                    fontWeight = FontWeight.SemiBold,
                                                )
                                            ) {
                                                append(model.teacherName)
                                            }
                                            withStyle(
                                                SpanStyle(
                                                    fontWeight = FontWeight.Black,
                                                    color = MaterialTheme.colorScheme.onSurface.copy(
                                                        alpha = .2f
                                                    ),
                                                )
                                            ) {
                                                append(" в ${model.time}")
                                            }
                                        },
                                        fontSize = 14.sp
                                    )
                                }
                                var text: String by remember { mutableStateOf("") }
                                OutlinedTextField(
                                    value = text+"dsa",
                                    onValueChange = {
                                        text = it
                                    },
                                    readOnly = true,
                                    colors = TextFieldDefaults.outlinedTextFieldColors(
                                        disabledBorderColor = Color.Transparent,
                                        focusedBorderColor = Color.Transparent,
                                        unfocusedBorderColor = Color.Transparent,
                                        errorBorderColor = Color.Transparent
                                    ),
                                    modifier = Modifier.width(IntrinsicSize.Min)
                                        .widthIn(min = 100.dp)
                                )
                            }
                        }
                    }
                    items(200) { num ->
                        Text("sad")
                    }
                }


                PullRefreshIndicator(
                    modifier = Modifier.align(alignment = Alignment.TopCenter),
                    refreshing = refreshing,
                    state = refreshState,
                )
//            ListDialogContent(component.groupListDialogComponent)
//            CAlertDialogContent(
//                component.studentsInGroupCAlertDialogComponent
//            ) {
//                LazyColumn {
//                    items(model.studentsInGroup) {
//                        for (i in 1..10) {
//                            TextButton(
//                                modifier = Modifier.fillMaxWidth(),
//                                onClick = {},
//                                enabled = false,
//                                colors = ButtonDefaults.textButtonColors(
//                                    disabledContentColor = MaterialTheme.colorScheme.onSurface
//                                ),
//                                shape = RoundedCornerShape(15.dp)
//                            ) {
//                                Text("${it.surname} ${it.name} ${it.praname}")
//                            }
//                        }
//
//                    }
//                }
//            }

            }
        }
    }
}

//val isCollapsed = remember { derivedStateOf { scrollBehavior.state.collapsedFraction > 0.5 } }
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonReportTopBar(
    component: LessonReportComponent,
    isFullView: Boolean,
//    scrollBehavior: TopAppBarScrollBehavior,
) {
    val model = component.model.value
    if (isFullView) {
        AppBar(
            navigationRow = {
                backAB(component)
            },
            title = {
                Row() {
                    titleAB(
                        subjectName = model.subjectName,
                        date = model.date,
                        groupName = model.groupName,
                        lessonReportId = model.lessonReportId,
                        isLarge = false
                    )
                }
            },
            actionRow = {
//                    var x by remember { mutableStateOf(0.0f) }
//                IconButton(
//
//                    onClick = {
////                            component.groupListDialogComponent.onEvent(
////                                ListDialogStore.Intent.ShowDialog(
////                                    x = if (viewManager.size!!.maxWidth - (viewManager.size!!.maxWidth / 4 + viewManager.size!!.maxWidth / 2) >= 250.dp) viewManager.size!!.maxWidth.value / 4 else viewManager.size!!.maxWidth.value / 5,
////                                    y = 50.0f
////                                )
////                            )
//                    }
//                ) {
//                    Icon(
//                        Icons.AutoMirrored.Outlined.FactCheck, null,
////                            modifier = Modifier.onGloballyPositioned {
////                                x =
////                            }
//                    )
//                }
                refreshAB(component)

            }
        )
    } else {
        AppBar(
            navigationRow = {
                backAB(component)
            },
            title = {
                Row() {
                    titleAB(
                        subjectName = model.subjectName,
                        groupName = model.groupName,
                        lessonReportId = model.lessonReportId,
                        isLarge = false,
                        date = model.date
                    )
                }
            },
            actionRow = {
//                    var x by remember { mutableStateOf(0.0f) }
//                IconButton(
//
//                    onClick = {
////                            component.groupListDialogComponent.onEvent(
////                                ListDialogStore.Intent.ShowDialog(
////                                    x = if (viewManager.size!!.maxWidth - (viewManager.size!!.maxWidth / 4 + viewManager.size!!.maxWidth / 2) >= 250.dp) viewManager.size!!.maxWidth.value / 4 else viewManager.size!!.maxWidth.value / 5,
////                                    y = 50.0f
////                                )
////                            )
//                    }
//                ) {
//                    Icon(
//                        Icons.AutoMirrored.Outlined.FactCheck, null,
////                            modifier = Modifier.onGloballyPositioned {
////                                x =
////                            }
//                    )
//                }
                refreshAB(component)

            }
        )
//        LargeTopAppBar(
//            navigationIcon = {
//                backAB(component)
//            },
//            title = {
//                titleAB(
//                    subjectName = model.subjectName,
//                    groupName = model.groupName,
//                    lessonReportId = model.lessonReportId,
//                    isLarge = false
//                )
//            },
//            actions = {
//                refreshAB(component)
//            },
//            //scrollBehavior = scrollBehavior
//        )


    }
}

@Composable
private fun backAB(
    component: LessonReportComponent
) {
    IconButton(
        onClick = { component.onOutput(LessonReportComponent.Output.BackToJournal) }
    ) {
        Icon(
            Icons.Rounded.ArrowBackIosNew, null
        )
    }
}

@Composable
private fun refreshAB(
    component: LessonReportComponent
) {
    IconButton(
        onClick = { }//*refresh()*// }
    ) {
        Icon(
            Icons.Filled.Refresh, null
        )
    }
}

@Composable
private fun titleAB(
    subjectName: String,
    groupName: String,
    lessonReportId: Int,
    isLarge: Boolean,
    date: String
) {
    val bigTextSize = if (!isLarge) 20.sp else 40.sp
    val smallTextSize = if (!isLarge) 14.sp else 28.sp
    val startPadding = if (!isLarge) 10.dp else 5.dp

    Column() {
        Text(
            text = buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        fontWeight = FontWeight.Bold,
                    )
                ) {
                    append(subjectName)
                }
                withStyle(
                    SpanStyle(
                        fontWeight = FontWeight.Black,
                        fontSize = smallTextSize,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = .5f),
                    )
                ) {
                    append(" $date")
                }
            },
            modifier = Modifier.padding(start = startPadding),
            overflow = TextOverflow.Ellipsis,
            fontSize = bigTextSize,
            maxLines = 1,
            style = TextStyle(
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Bottom,
                    trim = LineHeightStyle.Trim.LastLineBottom
                )
            )
        )
        Text(

            text = buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        fontWeight = FontWeight.Bold,
                    )
                ) {
                    append(groupName)
                }
                withStyle(
                    SpanStyle(
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = .2f),
                    )
                ) {
                    append(" №$lessonReportId")
                }
            },
            modifier = Modifier.padding(start = startPadding),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            style = TextStyle(
                fontSize = smallTextSize,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = .5f),
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Top,
                    trim = LineHeightStyle.Trim.FirstLineTop
                )
            )
        )
    }
}