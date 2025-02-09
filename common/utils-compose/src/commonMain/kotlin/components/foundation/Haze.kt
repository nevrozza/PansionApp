package components.foundation

import androidx.compose.desktop.ui.tooling.preview.utils.GlobalHazeState
import androidx.compose.desktop.ui.tooling.preview.utils.hazeMask
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeInputScale
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.LocalHazeStyle
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import deviceSupport.androidVersion
import view.ViewManager

@Composable
fun Modifier.hazeUnder(
    viewManager: ViewManager,
//    hazeState: HazeState? = GlobalHazeState.current,
    zIndex: Float = 0f
) =
    if (viewManager.hazeHardware.value) {

        this.hazeSource(
            state = GlobalHazeState.current,
            zIndex = zIndex
            //style = viewManager.hazeStyle!!.value
        )
    } else this


@OptIn(ExperimentalHazeMaterialsApi::class, ExperimentalHazeApi::class)
@Composable
fun Modifier.hazeHeader(
    viewManager: ViewManager,
//    hazeState: HazeState? = GlobalHazeState.current,
    isTransparentHaze: Boolean = true,
    isMasked: Boolean = true,
    customStyle: HazeStyle? = null,
//    isActivated: Boolean,
    elseColor: Color = MaterialTheme.colorScheme.background
) =
    if (
        (isMasked || androidVersion > 30)) {
        if (viewManager.hazeHardware.value
            ) {
            //        val alpha = if (isActivated) 1f else 0f
            this.hazeEffect(
                state = GlobalHazeState.current,
                style = customStyle ?: (if (isMasked) LocalHazeStyle.current else HazeMaterials.ultraThin())
            ) {
                if (isMasked) {
                    mask = hazeMask//Brush.verticalGradient(colors = listOf(Color.Magenta, Color.Transparent))
                    inputScale = HazeInputScale.Fixed(0.7f)
                //                progressive = view.hazeProgressive
                }



            //            this.
            }.background(Color.Transparent)
        } else this.background(if (isTransparentHaze) Color.Transparent else elseColor)
    } else this.background(elseColor)
