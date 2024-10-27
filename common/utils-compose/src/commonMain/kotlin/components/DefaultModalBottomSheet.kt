package components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import view.GlobalHazeState
import view.LocalViewManager
import view.ViewManager

@ExperimentalMaterial3Api
@Composable
fun DefaultModalBottomSheet(
    modifier: Modifier = Modifier//.padding(bottom = 10.dp).padding(horizontal = 10.dp)
        .fillMaxWidth().defaultMinSize(minHeight = 100.dp),
    additionalModifier: Modifier = Modifier,
    modalBottomSheetState: SheetState,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit
) {
    val hazeState = GlobalHazeState.current
    val viewManager = LocalViewManager.current
    println("sad: ${hazeState.toString()}")
    ModalBottomSheet(
        onDismissRequest = {
            onDismissRequest()
        },
        sheetState = modalBottomSheetState,
        contentWindowInsets = { WindowInsets.ime },
        containerColor = if(viewManager.hazeHardware.value) Color.Transparent else BottomSheetDefaults.ContainerColor,
        dragHandle = null
//        windowInsets = WindowInsets.ime
    ) {
        Column(
            Modifier.hazeHeader(
                viewManager = viewManager,
                hazeState = hazeState,
                isProgressive = false
            ), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BottomSheetDefaults.DragHandle()
            Box(
                modifier.then(additionalModifier)
            ) {
                content()
            }
        }
    }
}