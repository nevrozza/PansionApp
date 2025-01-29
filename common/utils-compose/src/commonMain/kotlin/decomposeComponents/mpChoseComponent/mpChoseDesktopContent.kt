package decomposeComponents.mpChoseComponent

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.DefaultErrorView
import components.DefaultErrorViewPos
import components.GetAsyncIcon
import components.LoadingAnimation
import components.hazeHeader
import components.hazeUnder
import components.mpChose.MpChoseComponent
import components.mpChose.MpChoseStore
import components.networkInterface.NetworkInterface
import components.networkInterface.NetworkState
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import resources.RIcons
import view.LocalViewManager
import view.ViewManager

//USING ONLY FOR SCHEDULE (why..)
@Composable
fun mpChoseDesktopContent(
    component: MpChoseComponent,
    offset: DpOffset = DpOffset(x = 40.dp, y = -25.dp),
    backButton: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val model by component.model.subscribeAsState()
    val nModel by component.nModel.subscribeAsState()
    rememberCoroutineScope()
    val viewManager = LocalViewManager.current
    val isTooltip = true// viewManager.orientation.value != WindowScreen.Vertical

//    if (isTooltip) {
        DropdownVariant(
            component = component,
            viewManager = viewManager,
            model = model,
            nModel = nModel,
            isTooltip = isTooltip,
            offset = offset,
            backButton = backButton
        ) {
            content()
        }
//    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalHazeMaterialsApi::class)
@Composable
fun DropdownVariant(
    component: MpChoseComponent,
    viewManager: ViewManager,
    model: MpChoseStore.State,
    nModel: NetworkInterface.NetworkModel,
    isTooltip: Boolean,
    offset: DpOffset,
    backButton: (() -> Unit)?,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(16.dp))
    ) {
        DropdownMenu(
            expanded = model.isDialogShowing && isTooltip,
            onDismissRequest = {
                component.onEvent(MpChoseStore.Intent.HideDialog)
            },
            modifier = Modifier.sizeIn(maxWidth = viewManager.size!!.maxWidth - 50.dp, maxHeight = viewManager.size!!.maxHeight - 100.dp).animateContentSize().hazeHeader(
                viewManager = viewManager,
                isMasked = false,
                customStyle = HazeMaterials.regular()
            ).hazeUnder(
                viewManager,
                zIndex = 3f
            ), //.sizeIn(maxHeight = 200.dp)
            offset = offset
        ) {
            Row {
                if (backButton != null) {
                    IconButton(
                        onClick = {
                            backButton()
                        },
                        modifier = Modifier.padding(top = (9.2f).dp).size(30.dp).offset(x = 8.dp)
                    ) {
                        GetAsyncIcon(
                            path = RIcons.ChevronLeft
                        )
                    }
                }
                Crossfade(
                    nModel
                ) {
                    Column() {
                        when (it.state) {
                            NetworkState.None -> {
                                content()
                            }

                            NetworkState.Loading -> {
                                Box(
                                    Modifier.size(width = 50.dp, height = 25.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    LoadingAnimation(
                                        circleSize = 8.dp,
                                        spaceBetween = 5.dp,
                                        travelDistance = 3.5.dp
                                    )
                                }
                            }

                            else -> {
                                DefaultErrorView(
                                    model = nModel,
                                    pos = DefaultErrorViewPos.CenteredNotFull
                                )
                            }
                        }
                    }
                }
            }


        }
    }
}