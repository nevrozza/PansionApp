package components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LocalPolice
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.compose.ui.util.fastSumBy
import decomposeComponents.listDialogComponent.ListDialogDesktopContent
import report.StudentNka
import report.UserMark
import report.UserMarkPlus
import server.cut
import server.fetchReason
import server.getDate
import server.getLocalDate
import server.getSixTime
import server.roundTo
import server.toMinutes
import view.blend
import view.handy
import kotlin.math.max
import kotlin.math.roundToInt



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarkTableUnit(m: MarkTableItem, markSize: Dp) {
    val yOffset = 0.dp
    val tState = rememberTooltipState(isPersistent = true)
    TooltipBox(
        state = tState,
        tooltip = {
            PlainTooltip() {
                Text(
                    "${
                        if (m.deployLogin != null) "${ if (m.isTransparent) "Выставил ${m.deployLogin}\nв ${m.deployDate}-${m.deployTime}\n" else ""}" else ""
                    }Об уроке:\n${if (m.date != null) "${m.date} " else ""}№${m.reportId}\n${
                        fetchReason(
                            m.reason
                        )
                    }", textAlign = TextAlign.Center
                )
            }
        },
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        enableUserInput = true
    ) {

        MarkContent(
            m.content,
            size = markSize,
            textYOffset = yOffset,
            addModifier = Modifier.handy().clickable {
                m.onClick(m.reportId)
            }.alpha(if (m.isTransparent) .2f else 1f),
            paddingValues = PaddingValues(start = 2.5.dp, end = 2.5.dp),
            reason = m.reason
        )
    }
}

@Composable
fun MarkTable(
    fields: Map<String, String>,
    dms: Map<String, List<MarkTableItem>>,
    nki: Map<String, List<StudentNka>>? = null
) {
    var dateMarks = dms.toMutableMap()
    nki?.forEach { n ->
        println("nnik: ${n}")
        n.value.forEach { d ->
            println("xxxi: ${d.date}")
            if (dateMarks[d.date] == null) {
                println("xxxik: ${d.date}")
                dateMarks[d.date] = listOf()
            }
        }
    }
    dateMarks = dateMarks.toList().sortedBy {
        println("xx: ${it.first}")
        getLocalDate(it.first).toEpochDays()
    }.toMap().toMutableMap()

    val vScrollState = rememberLazyListState()
    val hScrollState = rememberScrollState()

    val density = LocalDensity.current
    val allHeight = remember { mutableStateOf(0.dp) }
    val allWidth = remember { mutableStateOf(0.dp) }
    val lP = 150.dp

    val markSize = 30.dp
    val minWidth = 50.dp
    allWidth.value = dateMarks.map { dm ->
        var maxSize = 0
        fields.keys.forEach { login ->
            val size = dm.value.filter { it.login == login }.size
            maxSize = max(size, maxSize)
        }
        max(maxSize * markSize, minWidth)
    }.fastSumBy { it.value.toInt() }.dp + lP

    allHeight.value = 25.dp + (fields.size * 55.dp)

    ScrollBaredBox(
        vState = vScrollState, hState = hScrollState,
        height = allHeight, width = allWidth,
        modifier = Modifier.animateContentSize()
    ) {
        Box(Modifier.horizontalScroll(hScrollState)) {
            Row() {//modifier = Modifier.horizontalScroll(hhScrollState)
//            Divider(Modifier.height(allHeight.value).width(1.dp))
                Spacer(Modifier.width(lP))
                (dateMarks).onEachIndexed { i, (date, marks) ->
                    if (i != dateMarks.size - 1) {
                        var maxSize = 0
                        fields.keys.forEach { login ->
                            val size = marks.filter { it.login == login }.size
                            maxSize = max(size, maxSize)
                        }
                        val width: Dp =
                            max(maxSize * markSize, minWidth)
                        Spacer(Modifier.width(width))
                        VerticalDivider(
                            Modifier.height(allHeight.value).padding(vertical = 1.dp),
                            thickness = (1.5).dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = .4f)
                        )
                    }
                }


            }
            Column(
                modifier = Modifier
            ) {
                Row(
                    modifier = Modifier,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Spacer(Modifier.width(lP))

                    dateMarks.forEach { (date, marks) ->

//                        val isChecked = remember { mutableStateOf(false) }
                        var maxSize = 0
                        fields.keys.forEach { login ->
                            val size = marks.filter { it.login == login }.size
                            maxSize = max(size, maxSize)
                        }
                        val width: Dp =
                            max(maxSize * markSize, minWidth)

                        Box(
                            modifier = Modifier.width(
                                width
                            ),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = date.cut(5),
                                    fontWeight = FontWeight.ExtraBold,
                                    textAlign = TextAlign.Center,
                                    overflow = TextOverflow.Ellipsis,
                                    softWrap = false
                                )
                            }
                        }
                    }
                }


                HorizontalDivider(
                    Modifier.padding(start = 1.dp).width(allWidth.value - 1.dp)//.height(1.dp)
                    , color = MaterialTheme.colorScheme.outline.copy(alpha = .4f),
                    thickness = 1.5.dp
                )

                LazyColumn(
                    modifier = Modifier,
                    state = vScrollState,
                ) {
                    itemsIndexed(items = fields.toList()) { index, f ->
                        val fioColor =
                            MaterialTheme.colorScheme
                                .onSurface

                        Column {
                            Text(
                                text = f.second,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = fioColor,
                                modifier = Modifier
                                    .padding(start = 10.dp)
                                    .offset(with(density) { hScrollState.value.toDp() })
                            )
                            Row() {
                                val allMarks =
                                    dateMarks.flatMap { it.value.filter { it.login == f.first && it.content.toIntOrNull() != null } }
                                val marks = allMarks.filter {
                                    it.reason.subSequence(0, 3).toString() !in listOf(
                                        "!st",
                                        "!ds"
                                    )
                                }
                                val avg = (marks.sumOf { it.content.toInt() } / marks.size.toFloat()).roundTo(2)

                                val normStupsCount =
                                    allMarks.filter { it.reason.subSequence(0, 3) in listOf("!st") }
                                        .sumOf { it.content.toInt() }
                                val dsStupsCount =
                                    allMarks.filter { it.reason.subSequence(0, 3) in listOf("!ds") }
                                        .sumOf { it.content.toInt() }

                                val underNameWidth = remember { mutableStateOf(0.dp) }
                                Row(modifier = Modifier.onGloballyPositioned { c ->
                                    underNameWidth.value =
                                        with(density) { c.size.width.toFloat().toDp() }
                                    println(underNameWidth.value)
                                }) {
                                    Spacer(Modifier.width(20.dp))
                                    Text(
                                        (avg).toString(),
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(Modifier.width(7.dp))
                                    Text(
                                        "${if (normStupsCount > 0) "+" else ""}$normStupsCount/${if (dsStupsCount > 0) "+" else ""}$dsStupsCount",
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(Modifier.width((lP - underNameWidth.value)))
                                dateMarks.forEach { (date, marks) ->
                                    var maxSize = 0
                                    fields.keys.forEach { login ->
                                        val size = marks.filter { it.login == login }.size
                                        maxSize = max(size, maxSize)
                                    }
                                    val width: Dp =
                                        max(maxSize * markSize, minWidth)
                                    Box(
                                        modifier = Modifier.width(
                                            width
                                        ).height(25.dp),
                                        contentAlignment = Alignment.Center
                                    ) {

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center,
                                        ) {
                                            println(marks)
                                            marks.filter { it.login == f.first }
                                                .forEach { mark ->
                                                    MarkTableUnit(
                                                        m = mark,
                                                        markSize = (markSize - 5.dp) //because of start padding
                                                    )


                                                }
                                        }

                                        var nka = ""
                                        nki?.get(f.first)?.filter { it.date == date }?.forEach {
                                            nka += if (it.isUv) "Ув" else "Н"
                                        }
                                        Text(
                                            nka,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.align(Alignment.TopEnd).offset(y = (-30).dp)
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(5.dp))
                        if (index != fields.toList().lastIndex) {
                            Divider(
                                Modifier.padding(start = 1.dp)
                                    .width(allWidth.value - 1.dp)
                                    .height(1.dp),
                                color = MaterialTheme.colorScheme.outline.copy(alpha = .4f)
                            )
                        }
                    }
                }
            }
        }


    }
}
