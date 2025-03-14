package components.foundation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOutExpo
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun AnimatedCommonButton(
    text: String,
    modifier: Modifier = Modifier,
    isEnabled: Boolean,
    shape: Shape = ButtonDefaults.shape,

    onClick: () -> Unit
) {
    val animatedButtonContainerColor = animateColorAsState(
        if (isEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(
            alpha = 0.12f
        ),
        animationSpec = tween(400, 0, EaseInOutExpo)
    )
    val animatedButtonContentColor = animateColorAsState(
        if (isEnabled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface.copy(
            alpha = 0.38f
        ),
        animationSpec = tween(400, 0, EaseInOutExpo)
    )
    Button(
        modifier = modifier,
        onClick = { onClick() },
        colors = ButtonDefaults.buttonColors(
            contentColor = animatedButtonContentColor.value,
            disabledContentColor = animatedButtonContentColor.value,
            containerColor = animatedButtonContainerColor.value,
            disabledContainerColor = animatedButtonContainerColor.value
        ),
        enabled = isEnabled,
        shape = shape
    ) {

        Text(text, textAlign = TextAlign.Center)
    }

}

@Composable
fun AnimatedElevatedButton(
    text: String,
    modifier: Modifier = Modifier,
    isEnabled: Boolean,
    onClick: () -> Unit,
) {
    val animatedButtonContainerColor = animateColorAsState(
        if (isEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(
            alpha = 0.12f
        ),
        animationSpec = tween(400, 0, EaseInOutExpo)
    )
    val animatedButtonContentColor = animateColorAsState(
        if (isEnabled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface.copy(
            alpha = 0.38f
        ),
        animationSpec = tween(400, 0, EaseInOutExpo)
    )
    Box(contentAlignment = Alignment.Center) {
        ElevatedButton(
            modifier = modifier,
            onClick = { onClick() },
            colors = ButtonDefaults.elevatedButtonColors(
                contentColor = animatedButtonContentColor.value,
                disabledContentColor = animatedButtonContentColor.value,
                containerColor = animatedButtonContainerColor.value,
                disabledContainerColor = animatedButtonContainerColor.value
            ),
            enabled = isEnabled
        ) {
            Text(text)
        }
    }
}

@Composable
fun LoadingAnimation(
    modifier: Modifier = Modifier,
    circleSize: Dp = 25.dp,
    circleColor: Color = MaterialTheme.colorScheme.primary,
    spaceBetween: Dp = 10.dp,
    travelDistance: Dp = 20.dp
) {
    val circles = listOf(
        remember { Animatable(initialValue = 0f) },
        remember { Animatable(initialValue = 0f) },
        remember { Animatable(initialValue = 0f) }
    )

    circles.forEachIndexed { index, animatable ->
        LaunchedEffect(key1 = animatable) {
            delay(index * 100L)
            animatable.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = 1200
                        0.0f at 0 using LinearOutSlowInEasing
                        1.0f at 300 using LinearOutSlowInEasing
                        0.0f at 600 using LinearOutSlowInEasing
                        0.0f at 1200 using LinearOutSlowInEasing
                    },
                    repeatMode = RepeatMode.Restart
                )
            )
        }
    }

    val circleValues = circles.map { it.value }
    val distance = with(LocalDensity.current) { travelDistance.toPx() }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spaceBetween)
    ) {
        circleValues.forEach { value ->
            Box(
                modifier = Modifier
                    .size(circleSize)
                    .graphicsLayer {
                        translationY = -value * distance
                    }
                    .background(
                        color = circleColor,
                        shape = CircleShape
                    )
            )
        }
    }

}