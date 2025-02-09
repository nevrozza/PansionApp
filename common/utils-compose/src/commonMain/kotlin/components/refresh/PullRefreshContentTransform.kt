package components.refresh

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.platform.inspectable
import androidx.compose.ui.unit.dp

/**
 * A modifier for translating the position and scaling the size of a pull-to-refresh indicator
 * based on the given [PullRefreshState].
 *
 *
 * @param state The [PullRefreshState] which determines the position of the indicator.
 * @param scale A boolean controlling whether the indicator's size scales with pull progress or not.
 */
// TODO: Consider whether the state parameter should be replaced with lambdas.
@Composable
fun Modifier.pullRefreshContentTransform(
    state: PullRefreshState?,
    scale: Boolean = false,
) = inspectable(inspectorInfo = debugInspectorInfo {
    name = "pullRefreshContentTransform"
    properties["state"] = state
    properties["scale"] = scale
}) {
    val density = LocalDensity.current
    if (state != null) {
        Modifier
            .padding(
                top = with(density) { ((state.position) * 2).toDp().coerceAtLeast(0.dp) }
            )
    } else Modifier
}