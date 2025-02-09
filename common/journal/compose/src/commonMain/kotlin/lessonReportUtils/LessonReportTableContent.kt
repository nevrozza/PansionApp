package lessonReportUtils

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import components.foundation.CTextButton
import components.GetAsyncIcon
import components.journal.MarkContent
import components.journal.Stepper
import decomposeComponents.listDialogComponent.ListDialogDesktopContent
import lessonReport.ColumnTypes
import lessonReport.LessonReportComponent
import lessonReport.LessonReportStore
import lessonReport.ReportColumn
import lessonReport.StudentLine
import lessonReport.Stup
import resources.RIcons
import server.getDate
import server.getSixTime
import server.roundTo
import server.st
import server.toMinutes
import setMarksBind
import utils.hv


@Composable
fun LessonReportTableCell(
    student: StudentLine,
    column: ReportColumn,
    model: LessonReportStore.State,
    component: LessonReportComponent
) {
    val isPersonWasOnLesson = student.attended?.attendedType in listOf("0", null)
    when (column.type) {
        ColumnTypes.PRISUT -> PrisutBox(
            student, model, component
        )
        ColumnTypes.OPOZDANIE -> OpozdanieBox(
            student, isPersonWasOnLesson, model, component
        )
        ColumnTypes.SR_BALL -> SrBallBox(
            student, component
        )
        else -> RatingEntityCell(
            student, column, model, component
        )
    }
}


@Composable
private fun RatingEntityCell(
    student: StudentLine,
    column: ReportColumn,
    model: LessonReportStore.State,
    component: LessonReportComponent
) {
    if (column.type.st !in listOf("!st", "!ds")) {
        MarksBox(
            student, column, model, component
        )
    } else {
        StupsBox(
            student, column, model, component
        )
    }
}

@Composable
private fun PrisutBox(
    student: StudentLine,
    model: LessonReportStore.State,
    component: LessonReportComponent
) {
    val isDot = student.attended?.reason != null
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(end = if (isDot) 6.dp else 0.dp)
    ) {
        if (isDot) {
            Box(
                Modifier
                    .size(5.dp).clip(
                        CircleShape
                    )
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
        Spacer(Modifier.width(3.dp))
        PrisutCheckBox(
            modifier = Modifier.size(25.dp),
            attendedType = student.attended?.attendedType
                ?: "0",
            reason = student.attended?.reason,
            enabled = model.isEditable
        ) {
            component.onEvent(
                LessonReportStore.Intent.ChangeAttendance(
                    studentLogin = student.login,
                    attendedType = it
                )
            )
        }
    }
}

@Composable
private fun OpozdanieBox(
    student: StudentLine,
    isPersonWasOnLesson: Boolean,
    model: LessonReportStore.State,
    component: LessonReportComponent
) {
    val h = 25.dp
    Crossfade(student.lateTime) {
        when (it) {
            "0" -> Row(
                Modifier.fillMaxWidth().height(h),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                val lessonMinutes =
                    model.time.toMinutes()
                val currentMinutes =
                    getSixTime().toMinutes()
                if ((model.date == getDate() && lessonMinutes <= currentMinutes && currentMinutes - lessonMinutes <= 40) && model.isEditable) {
                    FilledTonalButton(
                        enabled = isPersonWasOnLesson,
                        contentPadding = PaddingValues(
                            horizontal = 5.dp
                        ), onClick = {
                            component.onEvent(
                                LessonReportStore.Intent.SetLateTime(
                                    student.login,
                                    "auto"
                                )
                            )
                        }) {
                        Text("Опозд.")
                    }
                }
                if (component.model.value.isEditable) {
                    Box() {
                        IconButton(
                            enabled = isPersonWasOnLesson,
                            modifier = Modifier.width(
                                30.dp
                            ), onClick = {
                                component.onEvent(
                                    LessonReportStore.Intent.OpenSetLateTimeMenu(
                                        student.login,
                                        x = 0f,
                                        y = 0f
                                    )
                                )
                            }) {
                            GetAsyncIcon(
                                RIcons.MORE_VERT
                            )
                        }
                        if (model.selectedLogin == student.login) {
                            ListDialogDesktopContent(
                                component.setLateTimeMenuComponent,
                                offset = DpOffset(
                                    x = 27.dp,
                                    y = -18.dp
                                ),
                                isFullHeight = true
                            )
                        }
                    }
                }
            }

            else -> Row(
                Modifier.fillMaxWidth().height(h),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    it,
                    fontWeight = FontWeight.Bold
                )
                if (component.model.value.isEditable) {
                    IconButton(
                        modifier = Modifier.width(
                            30.dp
                        ),
                        onClick = {
                            component.onEvent(
                                LessonReportStore.Intent.SetLateTime(
                                    student.login,
                                    "0"
                                )
                            )
                        }) {
                        GetAsyncIcon(
                            RIcons.CLOSE
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SrBallBox(
    student: StudentLine,
    component: LessonReportComponent
) {
    val marks =
        student.marksOfCurrentLesson.filter { it.isGoToAvg }
    val value =
        (student.avgMark.previousSum + marks.sumOf { it.value.toInt() }) / (student.avgMark.countOfMarks + marks.size).toFloat()

    if (value.isNaN()) {
        Text(
            text = "NaN",
            fontWeight = FontWeight.Black
        )
    } else {
        CTextButton(
            text = value.roundTo(2),
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onSurface
        ) {
            component.onEvent(
                LessonReportStore.Intent.OpenDetailedMarks(
                    student.login
                )
            )
        }
    }
}


@Composable
private fun MarksBox(
    student: StudentLine,
    column: ReportColumn,
    model: LessonReportStore.State,
    component: LessonReportComponent
) {
    val marks =
        student.marksOfCurrentLesson.filter { it.reason == column.type }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        marks.forEachIndexed { index, mark ->
            Box() {
                MarkContent(
                    mark = if (mark.value == "+2") "Д" else mark.value,
                    offset = DpOffset(0.dp, -2.dp),
                    background = if (student.login == model.selectedLogin && column.type == model.selectedMarkReason && index.toString() == model.selectedMarkValue) {
                        MaterialTheme.colorScheme.primary.copy(
                            alpha = .2f
                        ).hv()
                    } else {
                        MaterialTheme.colorScheme.primary.copy(
                            alpha = .2f
                        )
                    },
                    addModifier = Modifier
                        .clickable(enabled = model.isEditable) {
                            component.onEvent(
                                LessonReportStore.Intent.OpenDeleteMarkMenu(
                                    reasonId = column.type,
                                    studentLogin = student.login,
                                    markValue = index,
                                    selectedDeploy = "${mark.deployLogin}: ${mark.deployDate} (${mark.deployTime})"
                                )
                            )
                        },
                    paddingValues = PaddingValues(end = if (index != 3) 5.dp else 0.dp)
                )
                if (model.selectedMarkValue == index.toString()
                    && (model.selectedLogin == student.login)
                    && (model.selectedMarkReason == column.type)
                ) {
                    ListDialogDesktopContent(
                        component.deleteMarkMenuComponent,
                        offset = DpOffset(
                            x = 27.dp,
                            y = (-18).dp
                        ),
                        title = if (model.isModer) "Выставил ${mark.deployLogin}\nв ${mark.deployDate} (${mark.deployTime})" else null,
                        isFullHeight = true
                    )
                }
            }
        }

        if (marks.size != 4 && model.isEditable) {
            Box() {
                Box(
                    Modifier.offset(y = -2.dp)
                        //.padding(start = 5.dp)
                        .size(25.dp)
                        .clip(RoundedCornerShape(percent = 30))
                        .background(
                            if (student.login == model.selectedLogin && column.type == model.selectedMarkReason && model.selectedMarkValue.isBlank()) {
                                MaterialTheme.colorScheme.primary.copy(
                                    alpha = .2f
                                ).hv()
                            } else {
                                MaterialTheme.colorScheme.primary.copy(
                                    alpha = .2f
                                )
                            }

                        )
                        .clickable {
                            component.onEvent(
                                LessonReportStore.Intent.OpenSetMarksMenu(
                                    reasonId = column.type,
                                    studentLogin = student.login,
                                    x = 0f,
                                    y = 0f
                                )
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    GetAsyncIcon(
                        RIcons.ADD,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                if (model.selectedMarkReason == column.type && model.selectedLogin == student.login) {
                    ListDialogDesktopContent(
                        if (column.type.st == "!dz") component.setDzMarkMenuComponent else component.setMarkMenuComponent,
                        offset = DpOffset(
                            x = 27.dp,
                            y = (-18).dp
                        ),
                        isFullHeight = true,
                        modifier = Modifier.setMarksBind(
                            component
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun StupsBox(
    student: StudentLine,
    column: ReportColumn,
    model: LessonReportStore.State,
    component: LessonReportComponent
) {
    val reason =
        column.type
    Stepper(
        isEditable = model.isEditable,
        count = (student.stupsOfCurrentLesson.firstOrNull { it.reason == column.type }
            ?: Stup(
                0,
                "",
                id = model.ids,
                deployTime = "",
                deployLogin = "",
                deployDate = "",
                custom = null
            )).value,
        maxCount =
        getMaxStupsCount(reason),
        minCount =
        when (reason) {
            "!st1" -> -1
            "!ds1" -> -1
            "!ds2" -> -3
            "!ds3" -> -10
            else -> 0
        }
    ) {
        component.onEvent(
            LessonReportStore.Intent.ChangeStups(
                login = student.login,
                value = it,
                columnReason = column.type
            )
        )
    }
}

fun getMaxStupsCount(reason: String) = when (reason) {
    "!ds1" -> 1
    "!ds2" -> 1
    "!ds3" -> 0
    "!st5" -> 1
    else -> 3
}