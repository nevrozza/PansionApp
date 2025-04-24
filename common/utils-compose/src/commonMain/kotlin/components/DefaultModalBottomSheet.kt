package components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import components.foundation.hazeHeader
import components.foundation.hazeUnder
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import view.LocalViewManager

@OptIn(ExperimentalHazeMaterialsApi::class)
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
    val viewManager = LocalViewManager.current
    ModalBottomSheet(
        onDismissRequest = {
            onDismissRequest()
        },
        sheetState = modalBottomSheetState,
        contentWindowInsets = { WindowInsets.ime },
        containerColor = if (viewManager.hazeHardware.value) Color.Transparent else BottomSheetDefaults.ContainerColor,
        dragHandle = null
//        windowInsets = WindowInsets.ime
    ) {
        Column(
            Modifier.hazeHeader(
                viewManager = viewManager,
                isMasked = false,
                customStyle = HazeMaterials.regular()
            ).hazeUnder(
                viewManager,
                zIndex = 2f
            ).windowInsetsPadding(WindowInsets.navigationBars),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BottomSheetDefaults.DragHandle()
            Box(
                modifier.then(additionalModifier)
                    .animateContentSize(
                        spring(
                            stiffness = Spring.StiffnessMediumLow),
                        alignment = Alignment.Center
                    )
            ) {
                content()
            }
        }
    }
}