import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.HourglassBottom
import androidx.compose.material.icons.rounded.ThumbDown
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
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
import components.CustomTextButton
import components.networkInterface.NetworkState
import report.ClientStudentLine
import studentLines.StudentLinesComponent
import studentReportDialog.StudentReportDialogStore
import view.LocalViewManager
import view.rememberImeState

@OptIn(ExperimentalFoundationApi::class)
@ExperimentalMaterial3Api
@ExperimentalLayoutApi
@Composable
fun StudentLinesContent(
    component: StudentLinesComponent
) {
    val model by component.model.subscribeAsState()
    val nModel by component.nInterface.networkModel.subscribeAsState()
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val viewManager = LocalViewManager.current
    val imeState = rememberImeState()
    val lazyListState = rememberLazyListState()


    Scaffold(
        Modifier.fillMaxSize(),
        topBar = {
            AppBar(
                navigationRow = {
                    IconButton(
                        onClick = { component.onOutput(StudentLinesComponent.Output.Back) }
                    ) {
                        Icon(
                            Icons.Rounded.ArrowBackIosNew, null
                        )
                    }
                },
                title = {
                    Text(
                        "Прошедшие занятия",
//                        modifier = Modifier.padding(start = 10.dp),
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                isHaze = true
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize()) {
            Crossfade(nModel.state) { state ->
                when (state) {
                    NetworkState.None -> CLazyColumn(padding) {
                        itemsIndexed(items = model.studentLines, key = { i, sl -> i  }) { i, sl ->

                            if (i == model.studentLines.indexOfFirst { it.date == sl.date }) {
                                if (i != 0) {
                                    Spacer(Modifier.height(15.dp))
                                }
                                Text(sl.date, fontSize = 20.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(start = 10.dp))
                            }
                            ClientStudentLineContent(sl = sl) {
                                component.studentReportDialog.onEvent(
                                    StudentReportDialogStore.Intent.OpenDialog(
                                        login = model.login,
                                        reportId = sl.reportId
                                    )
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

    StudentReportDialogContent(
        component = component.studentReportDialog
    )
}

@Composable
private fun ClientStudentLineContent(
    sl: ClientStudentLine,
    onClick: () -> Unit
) {

    Surface(
        shape = RoundedCornerShape(15.dp),
        tonalElevation = 3.dp,
        modifier = Modifier.fillMaxWidth().padding(horizontal = (2.5).dp).padding(top = 5.dp),
        onClick = { onClick() }
    ) {
        Column(Modifier.padding(horizontal = 10.dp, vertical = 5.dp)) {
            Text(
                buildAnnotatedString {
                    append(sl.subjectName)
                    append(" ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp)) {
                        append(sl.groupName)
                        withStyle(SpanStyle()) {
                            append(" в ${sl.time}")
                        }
                    }
                },
                fontWeight = FontWeight.Black, fontSize = 20.sp
            )
            Text(
                text = if (sl.topic.isNotBlank()) sl.topic else "Тема не выставлена"
            )
            Spacer(Modifier.height(2.dp))
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {

                if (sl.lateTime.isNotBlank() && sl.lateTime != "00 мин" && sl.lateTime != "0") {
                    Icon(Icons.Rounded.HourglassBottom, null)
                    Spacer(Modifier.width(5.dp))
                    Text(sl.lateTime.removePrefix("0"))
                    Spacer(Modifier.width(10.dp))
                }
                if (sl.isLiked in listOf("t", "f")) {
                    Icon(
                        Icons.Rounded.ThumbDown, null,
                        modifier = Modifier.rotate(if (sl.isLiked == "t") 180f else 0f)
                    )
                    Spacer(Modifier.width(10.dp))
                }
                PrisutCheckBox(
                    modifier = Modifier.size(27.dp),
                    attendedType = sl.attended ?: "0",
                    reason = null,
                    enabled = false
                ) {}
            }
        }
    }
}

//modifier: Modifier = ,
//    viewManager: ViewManager,
//    onDismissClick: (String) -> Unit
//val data = not.reason.split(".")
//    val textColor =  if (viewManager.colorMode.value == "3") Color.White else MaterialTheme.colorScheme.onBackground
//    val type = data[0]
//    val backColor = getColor(type, data[1])
//    Surface(
//        modifier,
//        shape = RoundedCornerShape(15.dp),
//        color = if (viewManager.colorMode.value == "3") MaterialTheme.colorScheme.surfaceColorAtElevation(10.dp).blend(backColor, .6f) else MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
//    ) {
//        Box() {
//            Column(Modifier.padding(horizontal = 10.dp, vertical = 5.dp)) {
//                Text(
//                    buildAnnotatedString {
//                        append(not.subjectName)
//                        withStyle(SpanStyle(color = textColor.copy(alpha = .5f), fontSize = 17.sp)) {
//                            append(" ${not.date}")
//                        }
//                    },
//                    fontWeight = FontWeight.Black, fontSize = 20.sp, color = textColor
//                )
//                if (type != "A") { //groupName + time
//                    Text(
//                        buildAnnotatedString {
//                            append(not.groupName)
//                            withStyle(SpanStyle()) {
//                                append(" в ${not.reportTime}")
//                            }
//
//                        },
//                        fontWeight = FontWeight.Black, fontSize = 18.sp, color = textColor
//                    )
//                }
//                if (type == "A") {
//                    val text = data[1]
//                    val stups = data[2]
//                    Text(
//                        buildAnnotatedString {
//                            append(text)
//                            if (stups.toInt() != 0) {
//                                withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) {
//                                    append(" +${stups}")
//                                }
//                            }
//                        },
//                        fontWeight = FontWeight.Black, fontSize = 18.sp, color = textColor
//                    )
//                } else if (type == "N") {
//                    val isGood = data[1] == "2"
//                    Text(
//                        "Отсутствие по ${if (!isGood) "не" else ""}уважительной причине",
//                        fontWeight = FontWeight.Black, fontSize = 18.sp, color = textColor
//                    )
//                } else if (type == "Op") {
//                    val lateTime = data[1].removeSuffix(" мин").removePrefix("0")
//                    Text(
//                        buildAnnotatedString {
//                            append("Опоздание на ")
//                            withStyle(SpanStyle(fontSize = 18.sp)) {
//                                append("$lateTime мин.")
//                            }
//                        },
//                        fontWeight = FontWeight.Black, fontSize = (16.5).sp, color = textColor
//                    )
//                }
//            }
//            Row(Modifier.padding(top = 5.dp, end = 10.dp).align(Alignment.TopEnd), verticalAlignment = Alignment.CenterVertically) {
//                if (viewManager.colorMode.value !in listOf("2", "3", "4")) {
//                    Box(
//                        Modifier.size(5.dp).clip(
//                            CircleShape
//                        ).background(backColor ?: Color.Transparent) //MaterialTheme.colorScheme.primary
//                    )
//                    Spacer(Modifier.width(2.5.dp))
//                }
//                IconButton(
//                    onClick = {
//                        onDismissClick(not.key)
//                    },
//                    modifier = Modifier
//                        .size(15.dp)
//                ) {
//                    Icon(
//                        Icons.Rounded.Close,
//                        null,
//                        modifier = Modifier.background(
//                            MaterialTheme.colorScheme.surfaceColorAtElevation(15.dp).copy(.3f)
//                        )
//                    )
//                }
//            }
//        }
//    }