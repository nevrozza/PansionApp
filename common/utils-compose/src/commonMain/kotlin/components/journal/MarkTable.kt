package components.journal

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.desktop.ui.tooling.preview.utils.esp
import androidx.compose.desktop.ui.tooling.preview.utils.popupPositionProvider
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.times
import components.MarkTableItem
import components.foundation.CTextButton
import components.journal.tableUtils.MarkTableContent
import components.journal.tableUtils.MarkTableTableHeader
import components.journal.tableUtils.MarkTableTitle
import components.journal.tableUtils.MarkTableUnderTitleContent
import eu.wewox.minabox.MinaBox
import eu.wewox.minabox.MinaBoxItem
import layouts.TableCellOutline
import layouts.defaultMinaBoxTableModifier
import layouts.defaultScrollbarData
import report.StudentNka
import server.fetchReason
import server.getLocalDate
import server.roundTo
import utils.cursor.handy
import view.colorScheme
import view.consts.Paddings
import kotlin.math.max


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarkTableUnit(m: MarkTableItem, markSize: Dp) {
    val tState = rememberTooltipState(isPersistent = true)
    TooltipBox(
        state = tState,
        tooltip = {
            PlainTooltip() {
                Text(
                    "${
                        if (m.deployLogin != null) if (m.isTransparent) "Выставил ${m.deployLogin}\nв ${m.deployDate}-${m.deployTime}\n" else "" else ""
                    }Об уроке:\n${if (m.date != null) "${m.date} " else ""}№${m.reportId}\n${
                        fetchReason(
                            m.reason
                        )
                    }", textAlign = TextAlign.Center
                )
            }
        },
        positionProvider = popupPositionProvider,
        enableUserInput = true
    ) {

        MarkContent(
            m.content,
            size = markSize,
//            textYOffset = yOffset,
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
    nki: Map<String, List<StudentNka>>? = null,
    isDs1Init: Boolean
) {
    if (fields.isNotEmpty()) {
        var isDs1 by remember { mutableStateOf(isDs1Init) }
        var dateMarks = dms.toMutableMap()
        nki?.forEach { n ->
            n.value.forEach { d ->
                if (dateMarks[d.date] == null) {
                    dateMarks[d.date] = listOf()
                }
            }
        }
        dateMarks = dateMarks.toList().toMap().toMutableMap()

        val filteredDateMarks = dateMarks.mapNotNull {
            val marks = it.value.filter {
                isDs1 || !(it.reason.subSequence(
                    0,
                    3
                ) == "!ds" && it.content == "1")
            }
            if (marks.isNotEmpty()) {
                it.key to marks
            } else null
        }


        val density = LocalDensity.current
        val lP = 170.dp //TODO: Make it related to font
        val markSize = 40.dp//30.dp
        val minWidth = max(50.dp, with(density) { ((4.5f) * 14).esp.toDp() })


        val fieldsList = fields.toList()
        val marksList = dateMarks.flatMap { it.value }
        val filteredDateMarksList =
            filteredDateMarks.sortedByDescending { getLocalDate(it.first).toEpochDays() }.toList()
        val widths: MutableList<Dp> = mutableListOf()
        filteredDateMarksList.map { dm ->
            var maxSize = 0
            fields.keys.forEach { login ->
                val size = dm.second.filter {
                    (isDs1 || !(it.reason.subSequence(
                        0,
                        3
                    ) == "!ds" && it.content == "1")) && it.login == login
                }.size
                maxSize = max(size, maxSize)
            }
            widths.add(max(maxSize * markSize, minWidth))
        }
        if (widths.size > 0) {
            widths[0] = widths[0] + lP
        }
        Column {
            AnimatedVisibility(!isDs1) {
                CTextButton("Отобразить +1 за МВД") {
                    isDs1 = true
                }
            }
            if (fields.isEmpty()) {
                Box(
                    Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                    contentAlignment = Alignment.Center
                ) {
                    OutlinedCard(Modifier.fillMaxWidth().padding(20.dp).height(80.dp)) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Здесь пусто")
                        }
                    }
                }

            }


            val columnsCount = filteredDateMarks.size
            val rowsCount = fields.size

            val cellHeight = 57.dp
            val bigCellHeight = 65.dp
            val headerHeight = 30.dp
            val headerPadding = with(density) { (cellHeight - headerHeight).toPx() }

            MinaBox(
                modifier = defaultMinaBoxTableModifier,
                scrollBarData = defaultScrollbarData
            ) {
                items(
                    count = columnsCount * rowsCount,
                    layoutInfo = {
                        val index = it + columnsCount
                        val column = index % columnsCount
                        val row = index / columnsCount

                        val itemSizePx = with(density) {
                            DpSize(
                                width = widths[column],
                                height = bigCellHeight
                            ).toSize()
                        }
                        val prevX = (0 until column)
                            .map { with(density) { widths[it].toPx() } }.sum() //0 until column
                        MinaBoxItem(
                            x = prevX,
                            y = itemSizePx.height * row - headerPadding,
                            width = itemSizePx.width,
                            height = itemSizePx.height
                        )
                    },
                    key = { it }
                ) { index ->
                    val columnIndex = index % columnsCount
                    val studentIndex = (index / (columnsCount))


                    val student = fieldsList[studentIndex]

                    val currentDateMarks = filteredDateMarksList[columnIndex]

                    val marks = currentDateMarks.second.filter { it.login == student.first }

                    var nka = ""
                    nki?.get(student.first)?.filter { it.date == currentDateMarks.first }?.forEach {
                        nka += if (it.isUv) "Ув" else "Н"
                    }


                    TableCellOutline {
                        Row {
                            if (columnIndex == 0) {
                                Spacer(Modifier.width(lP))
                            }
                            Column {
                                Spacer(Modifier.height(Paddings.medium))
                                MarkTableContent(
                                    marks = marks,
                                    markSize = markSize,
                                    nka = nka
                                )
                            }
                        }

                    }
                }


                items(
                    count = rowsCount,
                    layoutInfo = {
                        val itemSizePx = with(density) {
                            DpSize(
                                width = 400.dp,
                                height = bigCellHeight
                            ).toSize()
                        }
                        MinaBoxItem(
                            x = 0f,
                            y = itemSizePx.height * it,
                            width = itemSizePx.width,
                            height = itemSizePx.height,
                            lockHorizontally = true
                        )

                    }
                ) { index ->
                    Box(
                        Modifier.fillMaxSize().padding(bottom = 3.dp),
                        contentAlignment = Alignment.BottomStart
                    ) {
                        MarkTableTitle(fieldsList[index].second)
                    }
                }

                items(
                    count = rowsCount,
                    layoutInfo = {
                        val itemSizePx = with(density) {
                            DpSize(
                                width = 400.dp,
                                height = bigCellHeight
                            ).toSize()
                        }
                        MinaBoxItem(
                            x = 0f,
                            y = itemSizePx.height * it + headerPadding,
                            width = itemSizePx.width,
                            height = itemSizePx.height
                        )

                    }
                ) { index ->
                    val allMarks = marksList.filter { it.login == fieldsList[index].first }
                    val marks = allMarks.filter {
                        it.reason.subSequence(0, 3).toString() !in listOf(
                            "!st",
                            "!ds"
                        )
                    }
                    // goddamn
                    val avg = (marks.sumOf { it.content.toInt() } / marks.size.toFloat()).roundTo(2)
                    val normStupsCount =
                        allMarks.filter { it.reason.subSequence(0, 3) in listOf("!st") }
                            .sumOf { it.content.toInt() }
                    val dsStupsCount =
                        allMarks.filter { it.reason.subSequence(0, 3) in listOf("!ds") }
                            .sumOf { it.content.toInt() }

                    Box(
                        Modifier.fillMaxSize().padding(bottom = 3.dp),
                        contentAlignment = Alignment.BottomStart
                    ) {
                        MarkTableUnderTitleContent(
                            avg = avg,
                            normStupsCount = normStupsCount,
                            dsStupsCount = dsStupsCount
                        )
                    }
                }

                items(
                    count = columnsCount,
                    layoutInfo = {
                        val index = it + columnsCount
                        val column = index % columnsCount

                        val itemSizePx = with(density) {
                            DpSize(
                                width = widths[column],
                                headerHeight
                            ).toSize()
                        }

                        val prevX = (0 until column)
                            .map { with(density) { widths[it].toPx() } }.sum() //0 until column
                        MinaBoxItem(
                            x = prevX,
                            y = 0f,
                            width = itemSizePx.width,
                            height = itemSizePx.height,
                            lockVertically = true
                        )
                    }
                ) {
                    val index = it + columnsCount
                    val column = index % columnsCount
                    TableCellOutline(backgroundColor = colorScheme.background) {

                        MarkTableTableHeader(
                            date = filteredDateMarksList[column].first,
                            startColumnPadding = if (column == 0) lP else 0.dp
                        )
                    }
                }


            }
        }
    } else {

        Text("Не данный момент, таблица пустая\n")
    }
}
