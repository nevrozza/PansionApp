package components

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.LocalHazeStyle
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
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
            state = hazeState,
            //style = viewManager.hazeStyle!!.value
        )
    } else this


@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
fun Modifier.hazeHeader(
    viewManager: ViewManager,
    hazeState: HazeState?,
    isTransparentHaze: Boolean = true,
    isProgressive: Boolean = true,
//    isActivated: Boolean,
    elseColor: Color = MaterialTheme.colorScheme.background
) =
    if (hazeState != null && viewManager.hazeHardware.value) {
//        val alpha = if (isActivated) 1f else 0f
        this.hazeChild(
            state = hazeState,
            style = if (isProgressive) LocalHazeStyle.current else HazeMaterials.ultraThin()
        ) {
            if (isProgressive) {
                progressive = view.hazeProgressive
            }
//            this.
        }
            .background(Color.Transparent)
    } else this.background(if (isTransparentHaze) Color.Transparent else elseColor)
