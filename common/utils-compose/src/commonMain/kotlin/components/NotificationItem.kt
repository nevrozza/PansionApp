package components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.material3.SwipeToDismissBoxState.Companion.Saver
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import main.ClientMainNotification
import view.ViewManager
import view.blend

@Composable
fun NotificationItem(
    not: ClientMainNotification,
    modifier: Modifier = Modifier.fillMaxWidth().padding(horizontal = (2.5).dp).padding(top = 5.dp),
    changeToUV: ((Int) -> Unit)? = null,
    viewManager: ViewManager,
    onClick: (Int) -> Unit,
    onDismissClick: (String) -> Unit
) {

    val textColor =
        if (viewManager.colorMode.value == "3") Color.White else MaterialTheme.colorScheme.onBackground
    val data = not.reason.split(".")
    val type = data[0]
    val backColor = getColor(type, data[1])
    val isChangeToUvButton = type == "N" && data[1] == "1" && changeToUV != null && not.reportId != null
//    val isShowing = remember { mutableStateOf(true) }
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            when(it) {
                SwipeToDismissBoxValue.EndToStart -> {

                    onDismissClick(not.key)
//                    isShowing.value = false
                }
                SwipeToDismissBoxValue.StartToEnd -> {

                    onDismissClick(not.key)
//                    isShowing.value = false
                }
                SwipeToDismissBoxValue.Settled -> {
//                    onDismissClick(not.key)
                    return@rememberSwipeToDismissBoxState false
                }
            }
            return@rememberSwipeToDismissBoxState true
        },
//        key = not.key
    )
    print("DS: ${dismissState.currentValue}")
    AnimatedVisibility(
        dismissState.currentValue == SwipeToDismissBoxValue.Settled
    ) {
        SwipeToDismissBox(
            state = dismissState,
            backgroundContent = {

                Row(Modifier.fillMaxSize().padding(horizontal = 30.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Icon(
                        Icons.Rounded.Check, null
                    )
                    Icon(
                        Icons.Rounded.Check, null
                    )
                }
            }
        ) {
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
                            fontWeight = FontWeight.Bold, fontSize = 20.sp, color = textColor
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
                                "Отсутствие по ${if (isGood) "уважительной" else "н-ой"} причине",
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
                        if (isChangeToUvButton) {
                            CustomTextButton(
                                text = "Изменить на ув",
                                modifier = Modifier.fillMaxWidth().padding(end = 5.dp),
                                textAlign = TextAlign.End
                            ) {

                                changeToUV!!(not.reportId!!)
                            }
                        }
                    }
                    Box(
                        Modifier.cClickable { onDismissClick(not.key)}.height(30.dp).width(60.dp).align(Alignment.TopEnd)
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
                                        backColor
                                    ) //MaterialTheme.colorScheme.primary
                                )
                                Spacer(Modifier.width(2.5.dp))
                            }
                                Icon(
                                    Icons.Rounded.Close,
                                    null,
                                    modifier = Modifier.size(15.dp).background(
                                        MaterialTheme.colorScheme.surfaceColorAtElevation(15.dp).copy(.3f)
                                    )
                                )
                        }
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