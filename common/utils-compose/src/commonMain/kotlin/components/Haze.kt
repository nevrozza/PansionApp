package components

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import view.ViewManager

@Composable
fun Modifier.hazeUnder(
    viewManager: ViewManager,
    isHaze: Boolean = true
) =
    if (isHaze && viewManager.hazeStyle != null) {
        this.haze(
            state = viewManager.hazeState,
            style = viewManager.hazeStyle!!.value
        )
    } else this


@Composable
fun Modifier.hazeHeader(
    viewManager: ViewManager, isHaze: Boolean = true,
    isTransparentHaze: Boolean = true,
    elseColor: Color = MaterialTheme.colorScheme.background
) =
    if (isHaze && viewManager.hazeStyle != null) {
        this.hazeChild(
            state = viewManager.hazeState,
            style = viewManager.hazeStyle!!.value
        )
            .background(Color.Transparent)
    } else this.background(if (isTransparentHaze) Color.Transparent else elseColor)
