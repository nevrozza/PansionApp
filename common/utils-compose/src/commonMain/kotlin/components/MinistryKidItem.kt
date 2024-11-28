package components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import components.listDialog.ListComponent
import decomposeComponents.listDialogComponent.ListDialogDesktopContent
import main.school.MinistryKid
import main.school.MinistryStup
import resources.RIcons
import server.Ministries

@Composable
fun MinistryKidItem(
    item: MinistryKid,
    pickedMinistry: String,
    mvdLogin: String?,
    mvdReportId: Int?,

    //login = item.login,
//    reason = "!ds3",
//    reportId = null,
//    custom = "",
//    stups = 0
    openMVDEvent: (login: String, reason: String, reportId: Int?, custom: String, stups: Int) -> Unit, //MinistryStore.Intent
    uploadStup: (reason: String, login: String, content: String, reportId: Int?, custom: String?) -> Unit,
    ds1ListComponent: ListComponent?,
    ds2ListComponent: ListComponent?,
//    model: MinistryStore.State,
//    component: MinistryComponent
) {
    val isCanBeEdited = ds1ListComponent != null
    val isFullOpened = remember { mutableStateOf(!isCanBeEdited) }
    val prev = item.dayStups.firstOrNull { it.reportId == null && (it.content.toIntOrNull() ?: 0) != 0 }
    Surface(
        Modifier.fillMaxWidth(),
        tonalElevation = 2.dp,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(5.dp).padding(start = 11.dp).animateContentSize()) {
            Text(
                "${item.fio.surname} ${item.fio.name} ${item.fio.praname ?: ""}",
                fontWeight = FontWeight.Bold,
                fontSize = MaterialTheme.typography.titleMedium.fontSize
            )
            Row {
                Text(
                    buildAnnotatedString {
                        val textikToday = "${getStupString(item.dayStups.sumOf { it.content.toIntOrNull() ?: 0 })} "
                        val colorToday =
                            if (textikToday.contains("-")) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground


                        val textikWeek = "${getStupString(item.weekStupsCount)} "
                        val colorWeek =
                            if (textikWeek.contains("-")) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground


                        val textikModule = "${getStupString(item.moduleStupsCount)} "
                        val colorModule =
                            if (textikModule.contains("-")) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground


                        val textikYear = "${getStupString(item.yearStupsCount)} "
                        val colorYear =
                            if (textikYear.contains("-")) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground

                        withStyle(
                            SpanStyle(
                                color = colorToday
                            )
                        ) {
                            withStyle(
                                SpanStyle(
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append("День: ")
                            }
                            append(textikToday)
                        }

                        withStyle(SpanStyle(color = colorWeek)) {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("Неделя: ")
                            }
                            append(textikWeek)
                        }


                        append("\n")

                        withStyle(SpanStyle(color = colorModule)) {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("Модуль: ")
                            }
                            append(textikModule)
                        }

                        withStyle(SpanStyle(color = colorYear)) {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("Год: ")
                            }
                            append(textikYear)
                        }
                    }
                )
                if (isCanBeEdited) {
                    Row {
                        if (item.lessons.isNotEmpty() || pickedMinistry == Ministries.DressCode) {
                            IconButton(
                                onClick = {
                                    isFullOpened.value = !isFullOpened.value
                                }
                            ) {
                                val chevronRotation = animateFloatAsState(if (isFullOpened.value) 90f else -90f)
                                GetAsyncIcon(
                                    path = RIcons.ChevronLeft,
                                    modifier = Modifier.rotate(chevronRotation.value),
                                    size = 15.dp
                                )
                            }
                        }
                        if (pickedMinistry == Ministries.MVD) {

                            if (prev == null) {
                                IconButton(
                                    onClick = {
                                        openMVDEvent(
                                            item.login,
                                            "!ds3",
                                            null,
                                            "",
                                            0
                                        )
                                    }
                                ) {
                                    GetAsyncIcon(
                                        RIcons.Add
                                    )
                                }
                            } else {
                                IconButton(
                                    onClick = {
                                        openMVDEvent(
                                            item.login,
                                            prev.reason,
                                            prev.reportId,
                                            prev.custom ?: "",
                                            prev.content.toIntOrNull() ?: 0
                                        )
                                    }
                                ) {
                                    GetAsyncIcon(
                                        path =  RIcons.Edit,
                                        size = 20.dp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(isFullOpened.value && pickedMinistry == Ministries.MVD) {
                Column {
                    item.lessons.forEachIndexed { i, l ->
                        val stups = item.dayStups.filter { it.reportId == l.reportId }
                        val isGroupView = remember { mutableStateOf(false) }
                        val customText = stups.firstOrNull { !it.custom.isNullOrBlank() }?.custom
                        Column {
                            Row {
                                Text(
                                    text = "${(i + 1)} ",
                                    modifier = Modifier.alpha(.5f)
                                )
                                AnimatedContent(
                                    if (isGroupView.value) l.groupName else l.subjectName,
                                    modifier = Modifier.cClickable {
                                        isGroupView.value = !isGroupView.value
                                    }
                                ) {
                                    Text(it, fontWeight = FontWeight.Bold)
                                }
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = l.time +
                                            (if (l.isUvNka == true) " Ув" else if (l.isUvNka == false) " Н" else "") +
                                            (if (l.isLiked == "t") " +" else if (l.isLiked == "f") " -" else "") +
                                            (if (l.lateTime.isNotEmpty() && l.lateTime != "0") " ${l.lateTime}" else "")
                                )
                            }
                            Row {
                                Text(
                                    text = "${(i + 1)} ",
                                    modifier = Modifier.alpha(.0f)
                                )
                                stups.forEach {
                                    if (it.content != "0") {
                                        val reason = when (it.reason) {
                                            "!ds1" -> "Гот"
                                            "!ds2" -> "Пов"
                                            "!ds3" -> "Нар"
                                            else -> "???"
                                        }
                                        val textik = getStupString(it.content)
                                        val color =
                                            if (textik.contains("-")) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                        Spacer(Modifier.width(5.dp))
                                        Box() {
                                            AnimatedContent(
                                                "${textik} ($reason)"
                                            ) { text ->
                                                CustomTextButton(
                                                    text,
                                                    color = color
                                                ) {
                                                    openMVDEvent(
                                                        item.login,
                                                        it.reason,
                                                        l.reportId,
                                                        it.custom ?: "",
                                                        it.content.toIntOrNull() ?: 0
                                                    )
                                                }
                                            }
                                            if (mvdLogin == item.login && mvdReportId == l.reportId) {
                                                if (it.reason != "!ds3") {
                                                    ListDialogDesktopContent(
                                                        when (it.reason) {
                                                            "!ds1" -> ds1ListComponent!!
                                                            else -> ds2ListComponent!!
                                                        },
                                                        isFullHeight = true
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                                if (stups.none { it.reason == "!ds1" && it.content != "0" }) {
                                    Spacer(Modifier.width(5.dp))
                                    Box() {
                                        CustomTextButton("готовность?") {
                                            openMVDEvent(
                                                item.login,
                                                "!ds1",
                                                l.reportId,
                                                 "",
                                                0
                                            )
                                        }
                                        if (mvdLogin == item.login && mvdReportId == l.reportId) {
                                            ListDialogDesktopContent(
                                                component = ds1ListComponent!!,
                                                isFullHeight = true
                                            )
                                        }
                                    }
                                }
                                if (stups.none { it.reason == "!ds2" && it.content != "0" }) {
                                    Spacer(Modifier.width(5.dp))
                                    Box() {
                                        CustomTextButton("поведение?") {
                                            openMVDEvent(
                                                item.login,
                                                "!ds2",
                                                l.reportId,
                                                "",
                                                0
                                            )
                                        }
                                        if (mvdLogin == item.login && mvdReportId == l.reportId) {
                                            ListDialogDesktopContent(
                                                component = ds2ListComponent!!,
                                                isFullHeight = true
                                            )
                                        }
                                    }
                                }
                                if (stups.none { it.reason == "!ds3" && it.content != "0" }) {
                                    Spacer(Modifier.width(5.dp))
                                    CustomTextButton("нарушение?") {
                                        openMVDEvent(
                                            item.login,
                                            "!ds3",
                                            l.reportId,
                                            "",
                                            0
                                        )
                                    }
                                }
                            }
                            AnimatedVisibility(customText != null) {
                                Text(
                                    customText.toString(),
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(start = 20.dp),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(isFullOpened.value && pickedMinistry == Ministries.DressCode) {
                Column {
                    DressCodeBlock(
                        title = "Одежда",
                        map = mapOf(
                            "!zd1" to "Манжеты",
                            "!zd2" to "Ворот",
                            "!zd3" to "Как отутюжена",
                            "!zd4" to "Общ состояние",
                        ),
                        item = item,
                        uploadStup = { reason, login, content, reportId, custom ->
                            uploadStup(reason, login, content, reportId, custom)
                        }
                    )
                    DressCodeBlock(
                        title = "Состояние",
                        map = mapOf(
                            "!zd5" to "Обуви",
                            "!zd6" to "Причёски",
                            "!zd7" to "Ногти, макияж"
                        ),
                        item = item,
                        uploadStup = { reason, login, content, reportId, custom ->
                            uploadStup(reason, login, content, reportId, custom)
                        }
                    )
                }
            }

            AnimatedVisibility(prev != null) {

                Text(
                    "${prev?.content} ${prev?.custom}",
                    color = MaterialTheme.colorScheme.error,
//                    modifier = Modifier.padding(start = 20.dp),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun DressCodeBlock(
    title: String,
    map: Map<String, String>,
    item: MinistryKid,
    //reason = m.key,
//    login = item.login,
//    content = it.toString(),
//    reportId = null,
//    custom = null
    uploadStup: (reason: String, login: String, content: String, reportId: Int?, custom: String?) -> Unit
) {
    Text(title, fontWeight = FontWeight.Bold)
    map.forEach { m ->
        val stup = item.dayStups.firstOrNull { it.reason == m.key } ?: MinistryStup(
            reason = m.key,
            content = "0",
            reportId = null,
            custom = null
        )
        DressCodeRow(
            stup = stup,
            text = m.value,
        ) {
            uploadStup(
                m.key,
                item.login,
                it.toString(),
                null,
                null
            )
        }
    }
}

@Composable
private fun DressCodeRow(
    stup: MinistryStup,
    text: String,
    onValueChange: (Int) -> Unit
) {
    Row(
        Modifier.fillMaxWidth()
            .padding(horizontal = 7.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Stepper(
            count = stup.content.toIntOrNull() ?: 0,
            isEditable = true,
            maxCount = 1,
            minCount = -1
        ) {
            onValueChange(it)
        }
    }
}