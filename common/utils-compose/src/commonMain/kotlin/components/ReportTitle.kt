package components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import resources.RIcons
import view.esp

@Composable
fun ReportTitle(
    subjectName: String,
    groupName: String,
    lessonReportId: Int,
//    isLarge: Boolean,
    date: String,
    teacher: String,
    time: String,
    isFullView: Boolean,
    isStartPadding: Boolean,
    isEnded: Boolean,
    module: Int,
    onClick: (() -> Unit)?
) {
    val bigTextSize = MaterialTheme.typography.titleLarge.fontSize// if (!isLarge) else 40.sp
    val smallTextSize = MaterialTheme.typography.titleSmall.fontSize//if (!isLarge)  else 28.sp
    val startPadding = if (isStartPadding) 10.dp else 0.dp//if (!isLarge)  else 5.dp
    Box() {
        if(isEnded) {
            Box(Modifier.offset(x = (-8).dp, y = (-10).dp).align(Alignment.CenterStart).size(5.dp).clip(
                CircleShape).background(MaterialTheme.colorScheme.primary))
        }
        Text(
            text = module.toString(),
            modifier = Modifier.offset(x = (-8).dp).align(Alignment.CenterStart)
        )
        Box(
            Modifier.padding(start = startPadding).clip(RoundedCornerShape(15.dp)).then(
                if (onClick != null) {
                    Modifier.clickable(enabled = !isFullView) {
                        onClick()
                    }
                } else {
                    Modifier
                }
            )
        ) {
            Column(
                Modifier.padding(horizontal = 3.dp)
            ) {
                Row {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                SpanStyle(
                                    fontWeight = FontWeight.SemiBold
                                )
                            ) {
                                append(subjectName)
                            }
                            withStyle(
                                SpanStyle(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = smallTextSize,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = .5f)
                                )
                            ) {
                                append(" $date")
                            }
                            if (isFullView) {


                                withStyle(
                                    SpanStyle(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface.copy(
                                            alpha = .2f
                                        )
                                    )
                                ) {
                                    append(" в ${time}")
                                }
                            }
                        },
                        overflow = TextOverflow.Ellipsis,
                        fontSize = bigTextSize,
                        maxLines = 1,
                        style = androidx.compose.material3.LocalTextStyle.current.copy(
                            lineHeightStyle = LineHeightStyle(
                                alignment = LineHeightStyle.Alignment.Bottom,
                                trim = LineHeightStyle.Trim.LastLineBottom
                            )
                        )
                    )


                }
                Row {
                    Text(

                        text = buildAnnotatedString {
                            withStyle(
                                SpanStyle(
                                    fontWeight = FontWeight.SemiBold
                                )
                            ) {
                                append(groupName)
                            }
                            withStyle(
                                SpanStyle(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = .2f)
                                )
                            ) {
                                append(" №$lessonReportId")
                            }
                        },
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        style = androidx.compose.material3.LocalTextStyle.current.copy(
                            fontSize = smallTextSize,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = .5f),
                            lineHeightStyle = LineHeightStyle(
                                alignment = LineHeightStyle.Alignment.Top,
                                trim = LineHeightStyle.Trim.FirstLineTop
                            )
                        )
                    )
                    if (isFullView) {
                        Spacer(Modifier.width(4.dp))
                        Box(Modifier.offset(y = -2.dp)) {
                            TeacherTime(teacher, time, false)
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun TeacherTime(teacherName: String, time: String, withTime: Boolean = true, separator: String = " ", yTextOffset: Dp = 0.dp
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(2.dp))
        GetAsyncIcon(
            RIcons.User,
            size = 14.dp,
            modifier = Modifier.offset(y = 1.dp)
        )
        Spacer(Modifier.width(5.dp))
        Text(
            buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        fontWeight = FontWeight.Normal
                    )
                ) {
                    append(teacherName)
                }
                if (withTime) {
                    withStyle(
                        SpanStyle(
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface.copy(
                                alpha = .2f
                            )
                        )
                    ) {
                        append("${separator}в ${time}")
                    }
                }
            },
            fontSize = 14.esp,
            lineHeight = 14.esp,
            style = androidx.compose.material3.LocalTextStyle.current.copy(
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Top,
                    trim = LineHeightStyle.Trim.FirstLineTop
                )
            ),
            modifier = Modifier.offset(y = yTextOffset)
        )
    }
}
