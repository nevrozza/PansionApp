package components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import main.ClientMainNotification
import view.LocalViewManager
import view.ThemeTint
import view.ViewManager
import view.blend

@Composable
fun NotificationItem(
    not: ClientMainNotification,
    modifier: Modifier = Modifier.fillMaxWidth().padding(horizontal = (2.5).dp).padding(top = 5.dp),
    viewManager: ViewManager,
    onClick: (Int) -> Unit,
    onDismissClick: (String) -> Unit
) {
    val data = not.reason.split(".")
    val textColor =
        if (viewManager.colorMode.value == "3") Color.White else MaterialTheme.colorScheme.onBackground
    val type = data[0]
    val backColor = getColor(type, data[1])
    Surface(
        modifier.clip(
            RoundedCornerShape(15.dp)).clickable(enabled = not.reportId != null) {
            if (not.reportId != null) {
                onClick(not.reportId!!)
            }
        },
        shape = RoundedCornerShape(15.dp),
        color = if (viewManager.colorMode.value == "3") MaterialTheme.colorScheme.surfaceColorAtElevation(
            10.dp
        ).blend(backColor, .6f) else MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
    ) {
        Box() {
            Column(Modifier.padding(horizontal = 10.dp, vertical = 5.dp)) {
                Text(
                    buildAnnotatedString {
                        append(not.subjectName)
                        withStyle(
                            SpanStyle(
                                color = textColor.copy(alpha = .5f),
                                fontSize = 17.sp
                            )
                        ) {
                            append(" ${not.date}")
                        }
                    },
                    fontWeight = FontWeight.Black, fontSize = 20.sp, color = textColor
                )
                if (type != "A") { //groupName + time
                    Text(
                        buildAnnotatedString {
                            append(not.groupName)
                            withStyle(SpanStyle()) {
                                append(" в ${not.reportTime}")
                            }

                        },
                        fontWeight = FontWeight.Bold, fontSize = 18.sp, color = textColor
                    )
                }
                if (type == "A") {
                    val text = data[1]
                    val stups = data[2]
                    Text(
                        buildAnnotatedString {
                            append(text)
                            if (stups.toInt() != 0) {
                                withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                                    append(" +${stups}")
                                }
                            }
                        },
                        fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = textColor
                    )
                }
                else if (type == "N") {
                    val isGood = data[1] == "2"
                    Text(
                        "Отсутствие по ${if (!isGood) "не" else ""}уважительной причине",
                        fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = textColor
                    )
                }
                else if (type == "Op") {
                    val lateTime = data[1].removeSuffix(" мин").removePrefix("0")
                    Text(
                        buildAnnotatedString {
                            append("Опоздание на ")
                            withStyle(SpanStyle(fontSize = 18.sp)) {
                                append("$lateTime мин.")
                            }
                        },
                        fontWeight = FontWeight.SemiBold, fontSize = (16.5).sp, color = textColor
                    )
                }
                else if (type == "L") {
                    Text(
                        "Отмечено ${if(data[1] == "T") "хорошее" else "плохое"} поведение",
                        fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = textColor
                    )
                }
            }
            Box(
                Modifier.height(30.dp).width(60.dp).align(Alignment.TopEnd).clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = null
                ) { onDismissClick(not.key) }
            ) {
                Row(
                    Modifier.padding(top = 5.dp, end = 10.dp).align(Alignment.TopEnd),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (viewManager.colorMode.value !in listOf("2", "3", "4")) {
                        Box(
                            Modifier.size(5.dp).clip(
                                CircleShape
                            ).background(
                                backColor ?: Color.Transparent
                            ) //MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(2.5.dp))
                    }
                    IconButton(
                        onClick = {
                            onDismissClick(not.key)
                        },
                        modifier = Modifier
                            .size(15.dp)
                    ) {
                        Icon(
                            Icons.Rounded.Close,
                            null,
                            modifier = Modifier.background(
                                MaterialTheme.colorScheme.surfaceColorAtElevation(15.dp).copy(.3f)
                            )
                        )
                    }
                }
            }
        }
    }
}

//private fun getTypeName

private fun getColor(type: String, isUv: String): Color {
    return when (type) {
        "A" -> Color(0xff3CB371)
        "N" -> if (isUv == "1") Color.Black else Color(0xffffff00)
        "Op" -> Color.Red
        "L" -> if (isUv == "T") Color(0xff3CB371) else Color.Red
        else -> Color.Red
    }
}