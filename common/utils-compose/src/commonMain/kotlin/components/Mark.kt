package components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.materialkolor.ktx.blend
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import report.UserMark
import server.fetchReason
import view.LocalViewManager
import view.ViewManager
import view.esp
import view.handy

@Composable
fun Color.getMarkColor(mark: String, viewManager: ViewManager, alpha: Float = 1f) =
    if (viewManager.colorMode.value in listOf("3", "4")) {
        val colors = markColorsColored
        this.blend(
            when (mark) {
                "5" -> colors["5"]!!
                "4" -> colors["4"]!!
                "3" -> colors["3"]!!
                else -> colors["2"]!!
            },
            amount = 0.8f
        ).copy(.8f)
    } else this.copy(alpha = alpha)

val markColorsColored =
    mapOf("5" to Color.Red, "4" to Color.Blue, "3" to Color(0xFF138808), "2" to Color.Black)
val markColorsMono =
    mapOf("5" to Color.Green, "4" to Color.Yellow, "3" to Color(0xFFFF8000), "2" to Color.Red)

@Composable
fun MarkContent(
    mark: String,
    background: Color = MaterialTheme.colorScheme.inversePrimary.copy(
//        alpha = .2f
    ),
    addModifier: Modifier = Modifier,
    offset: DpOffset = DpOffset(0.dp, 0.dp),
    paddingValues: PaddingValues = PaddingValues(start = 5.dp, top = 5.dp),
    size: Dp = 25.dp,
//    textYOffset: Dp = 0.dp,
    reason: String? = null
) {
    if (reason != null && (reason.subSequence(0, 3) == "!st" || reason.subSequence(0, 3) == "!ds")) {
        Box() {
            BorderStup(
                mark,
                addModifier = Modifier.padding(paddingValues).clip(RoundedCornerShape(percent = 30)).then(addModifier),
                size = size,
                reason = reason
            )
        }
    } else {


        val viewManager = LocalViewManager.current
        val color = background.getMarkColor(mark, viewManager, .2f)
        Box(
            Modifier.padding(paddingValues)
                .offset(offset.x, offset.y)
                .size(size)
                .clip(RoundedCornerShape(percent = 30))
                .background(
                    color
                )
                .then(addModifier),
            contentAlignment = Alignment.Center
        ) {
            Text(
                if (mark == "+2") "Д" else mark,
                fontSize = size.value.esp / 1.6f,
                modifier = Modifier.fillMaxWidth().align(Alignment.Center), //.offset(y = textYOffset)
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Black,
                color = if (viewManager.colorMode.value == "3") color.blend(
                    Color.White,
                    1f
                ) else MaterialTheme.colorScheme.onBackground
            )
            if (viewManager.colorMode.value in listOf("0", "1")) {
                val colors =
                    if (viewManager.colorMode.value == "1") markColorsColored else markColorsMono
                Box(
                    Modifier.padding(top = 5.dp, end = 5.dp).align(Alignment.TopEnd).size(5.dp)
                        .clip(
                            CircleShape
                        ).background(
                        colors[mark] ?: if (viewManager.colorMode.value == "1") Color.Black else Color.Red
                    ) //MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun cMark(mark: UserMark, coroutineScope: CoroutineScope, showDate: Boolean = true, onClick: (() -> Unit)? = null) {
    val markSize = 30.dp
    val yOffset = 3.dp
    val tState = rememberTooltipState(isPersistent = false)
    TooltipBox(
        state = tState,
        tooltip = {
            PlainTooltip(modifier = Modifier.clickable {}) {
                Text(
                    "${if (showDate) "${mark.date} " else ""}№${mark.reportId}\n${fetchReason(mark.reason)}",
                    textAlign = TextAlign.Center
                )
            }
        },
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider()
    ) {
        MarkContent(
            mark.content,
            size = markSize,
//            textYOffset = yOffset,
            addModifier = Modifier.clickable {
                coroutineScope.launch {
                    tState.show()
                }
                onClick?.invoke()
            }.handy()
                .pointerInput(PointerEventType.Press) {
                    //smth
                }
        )
    }
}