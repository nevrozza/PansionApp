package decomposeComponents.listDialogComponent

import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.listDialog.ListComponent
import kotlinx.coroutines.launch
import view.LocalViewManager
import view.WindowScreen

@Composable
fun ListDialogDesktopContent(
    component: ListComponent,
    isFullHeight: Boolean = false,
    offset: DpOffset = DpOffset(x = 40.dp, y = -25.dp),
    modifier: Modifier = Modifier
) {
    val model by component.model.subscribeAsState()
    val nModel by component.nModel.subscribeAsState()
    val coroutineScope = rememberCoroutineScope()
    val viewManager = LocalViewManager.current
    val isShowingCostil = remember { mutableStateOf(false) }
    val isTooltip = viewManager.orientation.value != WindowScreen.Vertical
    val focusRequester = remember { FocusRequester() }
//    if(model.isDialogShowing) {
//        AlertDialog({}){}
//    }

    if(isTooltip) {
        MaterialTheme(
            shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(16.dp))
        ) {
            DropdownVariant(
                component = component,
                viewManager = viewManager,
                model = model,
                nModel = nModel,
                isTooltip = isTooltip,
                offset = offset,
                isFullHeight = isFullHeight,
                modifier = modifier.then(
                    Modifier.focusRequester(focusRequester)
                        .onPlaced {
                            focusRequester.requestFocus()
                        }
                )
            )
        }

//        focusRequester.requestFocus()
    }
}