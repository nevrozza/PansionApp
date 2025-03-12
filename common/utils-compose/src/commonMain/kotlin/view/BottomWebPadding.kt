package view

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity

val LocalBottomWebPadding: ProvidableCompositionLocal<MutableState<Float>> = compositionLocalOf {
    mutableStateOf(0.0f)
}


@Composable
fun Modifier.webPadding(): Modifier {
    val density = LocalDensity.current
    val bottomWebPadding = LocalBottomWebPadding.current

    return this.padding(
        bottom = animateDpAsState(
            with(density) { bottomWebPadding.value.toDp() },
            animationSpec = spring(stiffness = Spring.StiffnessVeryLow)
        ).value
    )
}