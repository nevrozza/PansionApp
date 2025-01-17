package components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun StupsButtons(
    stups: List<Pair<Int, String>>, onMainClick: () -> Unit = {}, onDiciplineClick: () -> Unit = {}
) {
    Spacer(Modifier.width(5.dp))
    StupsButton(
        stups.filter {
            it.second.subSequence(
                0,
                3
            ) != "!ds"
        }.sumOf { it.first }
    ) {
        onMainClick()
    }
    Spacer(Modifier.width(5.dp))
    OutlinedButton(
        onClick = {
            onDiciplineClick()
        },
        contentPadding = PaddingValues(0.dp),
        modifier = Modifier.height(20.dp).offset(y = 2.dp),
    ) {
        CostilText(
            stups.filter {
                it.second.subSequence(
                    0,
                    3
                ) == "!ds"
            }.sumOf { it.first }
                .toString()
        )
    }
}

@Composable
fun StupsButton(count: Int, onClick: () -> Unit) {
    FilledTonalButton(
        onClick = {
            onClick()
        },
        contentPadding = PaddingValues(0.dp),
        modifier = Modifier.height(20.dp).offset(y = 2.dp)
    ) {
        CostilText(count.toString())
    }
}

@Composable
fun BorderStup(
    string: String,
    reason: String,
    size: Dp = 25.dp,
    addModifier: Modifier = Modifier
) {
    Box(
        addModifier.then(
            Modifier.size(size).then(
                if (reason.subSequence(0, 3) == "!ds") Modifier.dashedBorder(
                    (1.5f).dp,
                    color = MaterialTheme.colorScheme.outline,
                    cornerRadiusDp = (size * 0.3f)
                )
                else Modifier.border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,//.copy(if (reason.subSequence(0, 3) != "!ds") 1f else 0f),
                    shape = RoundedCornerShape(30)
                )
            )
        ).clip(RoundedCornerShape(30)),
        contentAlignment = Alignment.Center
    ) {
        CostilText(string)
    }
}

@Composable
private fun CostilText(string: String) {

    Text(
        getStupString(string)//, modifier = Modifier.offset(x = -2.dp)
        , maxLines = 1
    )
}

fun getStupString(string: String): String {
    return "${if (!string.contains("-") && !string.contains("+")) "+" else ""}${string}"
}

fun getStupString(int: Int): String {
    return "${if (!int.toString().contains("-") && !int.toString().contains("+")) "+" else ""}${int.toString()}"
}


fun Modifier.dashedBorder(strokeWidth: Dp, color: Color, cornerRadiusDp: Dp) = composed(
    factory = {
        val density = LocalDensity.current
        val strokeWidthPx = density.run { strokeWidth.toPx() }
        val cornerRadiusPx = density.run { cornerRadiusDp.toPx() }

        this.then(
            Modifier.drawWithCache {
                onDrawBehind {
                    val stroke = Stroke(
                        width = strokeWidthPx,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    )

                    drawRoundRect(
                        color = color,
                        style = stroke,
                        cornerRadius = CornerRadius(cornerRadiusPx)
                    )
                }
            }
        )
    }
)