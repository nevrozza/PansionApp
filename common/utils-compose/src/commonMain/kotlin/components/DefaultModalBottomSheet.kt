package components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import components.listDialog.ListDialogStore

@ExperimentalMaterial3Api
@Composable
fun DefaultModalBottomSheet(
    modifier: Modifier = Modifier.padding(bottom = 10.dp).padding(horizontal = 10.dp)
        .fillMaxWidth().defaultMinSize(minHeight = 100.dp),
    additionalModifier: Modifier = Modifier,
    modalBottomSheetState: SheetState,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = {
            onDismissRequest()
        },
        sheetState = modalBottomSheetState,
        windowInsets = WindowInsets.ime

    ) {
        Box(
            modifier.then(additionalModifier)
        ) {
            content()
        }
    }
}