package decomposeComponents.mpChoseComponent

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.CustomTextButton
import components.LoadingAnimation
import components.mpChose.MpChoseComponent
import components.mpChose.MpChoseStore
import components.networkInterface.NetworkInterface
import components.networkInterface.NetworkState
import view.LocalViewManager
import view.ViewManager
import view.WindowScreen

@Composable
fun mpChoseDesktopContent(
    component: MpChoseComponent,
    offset: DpOffset = DpOffset(x = 40.dp, y = -25.dp),
    backButton: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val model by component.model.subscribeAsState()
    val nModel by component.nModel.subscribeAsState()
    val coroutineScope = rememberCoroutineScope()
    val viewManager = LocalViewManager.current
    val isTooltip = viewManager.orientation.value != WindowScreen.Vertical

    if (isTooltip) {
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
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
            modifier = Modifier.animateContentSize(), //.sizeIn(maxHeight = 200.dp)
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
                        Icon(
                            Icons.Rounded.ArrowBackIosNew, "back"
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
                                Text(nModel.error)
                                Spacer(Modifier.height(7.dp))
                                CustomTextButton("Попробовать ещё раз") {
                                    nModel.onFixErrorClick()
                                }
                            }
                        }
                    }
                }
            }


        }
    }
}