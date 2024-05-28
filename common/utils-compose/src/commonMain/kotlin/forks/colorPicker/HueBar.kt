package forks.colorPicker

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.hsv
import androidx.compose.ui.graphics.LinearGradient
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.materialkolor.ktx.toHct
import com.materialkolor.utils.ColorUtils
import view.LocalViewManager
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorSlider(
    changeColor: (Color) -> Unit
) {
    val viewManager = LocalViewManager.current
    val value = remember { mutableStateOf(rgbToHue(viewManager.seedColor.value)) }



    val color = remember {
        derivedStateOf { getColor(value.value) }
    }

    val trackBrush = Brush.linearGradient(
        colors = listOf(
            Color.Red,
            Color.Yellow,
            Color.Green,
            Color.Cyan,
            Color.Blue,
            Color.Magenta,
            Color.Red
        ),
        start = Offset.Zero,
        end = Offset(Float.POSITIVE_INFINITY, 0f)
    )



    Slider(
        value = value.value,
        onValueChange = { newValue ->
            value.value = newValue
            val newColor = getColor(newValue)
            changeColor(newColor)
        },
        valueRange = 10f..350f,
        colors = SliderDefaults.colors(
            thumbColor = color.value,
            activeTrackColor = Color.Transparent,
            inactiveTrackColor = Color.Transparent
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(35.dp),
        thumb = {
            CustomThumb(
                color = color.value,
                modifier = Modifier.size(70.dp) // Custom thumb size
            )
        },
        track = {
            Box(
                Modifier.height(25.dp).fillMaxWidth().clip(RoundedCornerShape(50))
                    .background(trackBrush)
            ) {}
        }
        // No need for active/inactive track colors since we draw the track ourselves
    )
}


private fun getColor(v: Float): Color {
    return hsv(v, 1f, 1f)
}


fun rgbToHue(color: Color): Float {
    val red = color.red
    val green = color.green
    val blue = color.blue
    val max = maxOf(red, green, blue)
    val min = minOf(red, green, blue)
    val delta = max - min

    // Calculate hue
    val hue = when {
        delta.toInt() == 0 -> 0f
        red == max -> (green - blue) / delta.toFloat() % 6
        green == max -> (blue - red) / delta.toFloat() + 2
        else -> (red - green) / delta.toFloat() + 4
    }

    return (hue * 60).coerceIn(0f, 360f)
}

@Composable
fun CustomThumb(
    color: Color,
    modifier: Modifier = Modifier
) {


    Canvas(modifier = modifier) {
        drawCircle(
            color = color,
            radius = 18f, // Adjust for desired thumb size

        )
        drawCircle(
            color = Color.White, // Inner circle for white border
            radius = 16f,
            style = Stroke(6f)// Adjust for desired thumb size
//            style = Paint.Style.Stroke,
//            stroke = thumbPaint
        )
    }
}