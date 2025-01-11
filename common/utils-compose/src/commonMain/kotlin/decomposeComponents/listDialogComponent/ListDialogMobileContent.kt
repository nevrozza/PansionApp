package decomposeComponents.listDialogComponent

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.onPlaced
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.listDialog.ListComponent
import components.listDialog.ListDialogStore
import components.listDialog.ListItem
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.launch
import view.LocalViewManager
import view.WindowScreen

@ExperimentalMaterial3Api
@Composable
fun ListDialogMobileContent(
    component: ListComponent,
    title: String = "Выберите",
    modifier: Modifier = Modifier,
    onClick: (ListItem) -> Unit = {}
//    hazeState: HazeState?
) {
    val model by component.model.subscribeAsState()
    val nModel by component.nModel.subscribeAsState()
    val coroutineScope = rememberCoroutineScope()
    val viewManager = LocalViewManager.current
    val focusRequester = remember { FocusRequester() }

    val isTooltip = viewManager.orientation.value != WindowScreen.Vertical
//    if(model.isDialogShowing) {
//        AlertDialog({}){}
//    }
    if(!isTooltip) {
        val isShowingCostil = remember { mutableStateOf(false) }
        val modalBottomSheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )
//        LaunchedEffect(modalBottomSheetState.isVisible) {
//            if (!modalBottomSheetState.isVisible) {
//                component.onEvent(ListDialogStore.Intent.HideDialog)
//            }
//        }
        LaunchedEffect(model.isDialogShowing) {
//            onDispose {
                if (!model.isDialogShowing) {
                    coroutineScope.launch {

                        modalBottomSheetState.hide()
                    }.invokeOnCompletion {
                        isShowingCostil.value = false
                    }
                } else {
                    isShowingCostil.value = true
                }
//            }

        }

        BottomSheetVariant(
            component = component,
            model = model,
            nModel = nModel,
            isShowingCostil = isShowingCostil,
            coroutineScope = coroutineScope,
            modalBottomSheetState = modalBottomSheetState,
            title = title,
            modifier = modifier.focusRequester(focusRequester).onPlaced {
                focusRequester.requestFocus()
            },
            onClick = onClick
        )
    }

}