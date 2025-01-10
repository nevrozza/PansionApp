package components

import androidVersion
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import dev.chrisbanes.haze.HazeInputScale
import dev.chrisbanes.haze.*
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import view.ViewManager

@Composable
fun Modifier.hazeUnder(
    viewManager: ViewManager,
    hazeState: HazeState?
) =
    if (hazeState != null && viewManager.hazeHardware.value) {

        this.haze(
            state = hazeState
            //style = viewManager.hazeStyle!!.value
        )
    } else this


@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
fun Modifier.hazeHeader(
    viewManager: ViewManager,
    hazeState: HazeState?,
    isTransparentHaze: Boolean = true,
    isMasked: Boolean = true,
//    isActivated: Boolean,
    elseColor: Color = MaterialTheme.colorScheme.background
) =
    if (
        (isMasked || androidVersion > 30)) {
        if (hazeState != null && viewManager.hazeHardware.value
            ) {
            //        val alpha = if (isActivated) 1f else 0f
            this.hazeChild(
                state = hazeState,
                style = if (isMasked) LocalHazeStyle.current else HazeMaterials.ultraThin()
            ) {
                if (isMasked) {
                    mask = view.hazeMask//Brush.verticalGradient(colors = listOf(Color.Magenta, Color.Transparent))
                    inputScale = HazeInputScale.Fixed(0.7f)
                //                progressive = view.hazeProgressive
                }



            //            this.
            }.background(Color.Transparent)
        } else this.background(if (isTransparentHaze) Color.Transparent else elseColor)
    } else this.background(elseColor)
