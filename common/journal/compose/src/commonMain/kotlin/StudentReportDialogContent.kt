
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.desktop.ui.tooling.preview.utils.esp
import androidx.compose.desktop.ui.tooling.preview.utils.popupPositionProvider
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.foundation.CTextButton
import components.foundation.DefaultErrorView
import components.foundation.DefaultErrorViewPos
import components.GetAsyncIcon
import components.journal.MarkContent
import components.journal.dashedBorder
import components.networkInterface.NetworkState
import decomposeComponents.CBottomSheetContent
import kotlinx.coroutines.launch
import lessonReportUtils.PrisutCheckBox
import resources.RIcons
import server.fetchReason
import server.getLocalDate
import server.toMinutes
import studentReportDialog.StudentReportComponent
import utils.cursor.handy

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun StudentReportDialogContent(
    component: StudentReportComponent,
    openReport: ((Int) -> Unit)? = null,
    changeToUV: ((Int, String) -> Unit)? = null,
) {

    val size = 37.dp
    val offset = 7.dp

    val model by component.model.subscribeAsState()
    val nModel by component.dialog.nInterface.networkModel.subscribeAsState()
    val coroutineScope = rememberCoroutineScope()
//    val lazyList = rememberLazyListState()
    CBottomSheetContent(
        component = component.dialog,
        customMaxHeight = 0.dp
    ) {
        Crossfade(nModel.state) {
            Box(Modifier.fillMaxWidth()) {
                when (it) {
                    NetworkState.None ->
                        AnimatedVisibility(model.studentLine != null && model.info != null) {
                            Column(
                                Modifier.fillMaxWidth().padding(horizontal = 10.dp)
                                    .padding(bottom = 25.dp)
                                    .alpha(if (nModel.state == NetworkState.Error) 0.4f else 1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "${model.info!!.date}-${model.info!!.time}",
                                    modifier = Modifier.alpha(.5f)
                                )
                                Text(
                                    if (model.info!!.theme.isNotBlank()) model.info!!.theme else "Тема не выставлена",
                                    fontWeight = FontWeight.Black,
                                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    "${model.studentLine!!.subjectName} ${model.studentLine!!.groupName}",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.alpha(.5f)
                                )


                                Spacer(Modifier.height(2.5.dp))
                                Text(model.info!!.module + " модуль")
                                Spacer(Modifier.height(5.dp))
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    (model.marks + model.stups).sortedWith(
                                        compareBy(
                                            { getLocalDate(it.deployDate).toEpochDays() },
                                            { it.deployTime.toMinutes() })
                                    ).reversed().forEach { x ->
                                        val m = x.mark


                                        val tState = rememberTooltipState(isPersistent = false)

                                        val onClickMark = {
                                            coroutineScope.launch {
                                                tState.show()
                                            }
                                        }
                                        TooltipBox(
                                            state = tState,
                                            tooltip = {
                                                PlainTooltip(modifier = Modifier.clickable {}) {
                                                    Text(
                                                        fetchReason(m.reason),
                                                        textAlign = TextAlign.Center
                                                    )
                                                }
                                            },
                                            positionProvider = popupPositionProvider
                                        ) {

                                            if (m.reason.subSequence(0, 3) != "!st" &&
                                                m.reason.subSequence(0, 3) != "!ds"
                                            ) {
                                                MarkContent(
                                                    m.content,
                                                    size = size,
//                                                    textYOffset = offset,
                                                    addModifier = Modifier.clickable {
                                                        onClickMark()
                                                    }.handy()
                                                )
                                            } else {
                                                StupContent(
                                                    m.content,
                                                    size = size,
                                                    textYOffset = offset,
                                                    addModifier = Modifier.clickable {
                                                        onClickMark()
                                                    }.handy(),
                                                    isDs = m.reason.subSequence(0, 3) == "!ds"
                                                )
                                            }
                                        }
                                    }
                                }
                                if (model.studentLine!!.isLiked in listOf("t", "f") ||
                                    (model.studentLine!!.lateTime.isNotBlank() && model.studentLine!!.lateTime != "00 мин" && model.studentLine!!.lateTime != "0") ||
                                    model.studentLine!!.attended !in listOf("0", null)
                                ) {
                                    Spacer(Modifier.height(10.dp))
                                    Row(
                                        Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        if (model.studentLine!!.lateTime.isNotBlank() && model.studentLine!!.lateTime != "00 мин" && model.studentLine!!.lateTime != "0") {
                                            GetAsyncIcon(
                                                RIcons.HOURGLASS_BOTTOM,
                                                size = 18.dp
                                            )
                                            Spacer(Modifier.width(5.dp))
                                            Text(model.studentLine!!.lateTime.removePrefix("0"))

                                            Spacer(Modifier.width(10.dp))
                                        }

                                        if (model.studentLine!!.isLiked in listOf("t", "f")) {
                                            GetAsyncIcon(
                                                RIcons.LIKE,
                                                modifier = Modifier.rotate(if (model.studentLine!!.isLiked != "t") 180f else 0f)
                                            )
                                            Spacer(Modifier.width(10.dp))
                                        }
                                        PrisutCheckBox(
                                            modifier = Modifier.size(27.dp),
                                            attendedType = model.studentLine!!.attended ?: "0",
                                            reason = null,
                                            enabled = false
                                        ) {}
                                    }
                                    if (model.studentLine != null && model.studentLine!!.attended == "1" && changeToUV != null) {
                                        CTextButton(
                                            text = "Изменить на ув",
                                            modifier = Modifier.align(Alignment.End)
                                        ) {
                                            changeToUV(model.studentLine!!.reportId, model.studentLine!!.login)
                                        }
                                    }
                                }
                                if (model.homeTasks.isNotEmpty()) {
                                    Spacer(Modifier.height(10.dp))
                                    Text("Домашние задания", fontWeight = FontWeight.SemiBold)
                                    model.homeTasks.forEachIndexed { i, ht ->
                                        Text("${i + 1}. ${ht}", textAlign = TextAlign.Center)
                                    }
                                }


                            }
                        }

                    NetworkState.Error -> DefaultErrorView(
                        nModel,
                        pos = DefaultErrorViewPos.Centered,
                        modifier = Modifier.height(200.dp)
                    )

                    NetworkState.Loading -> Box(
                        Modifier.height(200.dp).fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                if (openReport != null) {
                    IconButton(
                        onClick = {
                            if (model.info != null) {
                                openReport.invoke(model.info!!.reportId)
                            }
                        },
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        GetAsyncIcon(
                            RIcons.LOGOUT
                        )
                    }
                }
            }
        }

    }
}

@Composable
private fun StupContent(
    mark: String,
    addModifier: Modifier = Modifier,
    offset: DpOffset = DpOffset(0.dp, 0.dp),
    paddingValues: PaddingValues = PaddingValues(start = 5.dp, top = 5.dp),
    size: Dp = 25.dp,
    textYOffset: Dp = 0.dp,
    isDs: Boolean
) {
    Box(
        Modifier.padding(paddingValues)
            .offset(offset.x, offset.y)
            .size(size)

//            .border(
//                1.5.dp,
//                shape = RoundedCornerShape(percent = 30),
//                color = MaterialTheme.colorScheme.onBackground.copy(alpha = if (!isDs) .5f else .25f)
//            )
            .then(
                if (isDs) Modifier.dashedBorder(
                    (1.5f).dp,
                    color = MaterialTheme.colorScheme.outline,
                    cornerRadiusDp = (size * 0.3f)
                ) else Modifier.border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,//.copy(
//                        if (!isDs) 1f else 0f
//                    ),
                    shape = RoundedCornerShape(30)
                )
            )

            .clip(RoundedCornerShape(percent = 30))
            .then(addModifier),
        contentAlignment = Alignment.Center
    ) {
        Text(
            (if (mark.first() !in listOf('-', '+')) "+" else "") + mark,
            fontSize = size.value.esp / 1.6f,
            modifier = Modifier.fillMaxSize().offset(y = textYOffset),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Black,
        )
    }
}