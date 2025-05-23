package decomposeComponents

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.cAlertDialog.CAlertDialogComponent
import components.cAlertDialog.CAlertDialogStore
import components.foundation.CTextButton
import components.foundation.DefaultErrorView
import components.foundation.DefaultErrorViewPos
import components.foundation.LoadingAnimation
import components.foundation.hazeHeader
import components.foundation.hazeUnder
import components.networkInterface.NetworkState
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import utils.HideKeyboardLayout
import view.LocalViewManager
import view.consts.Paddings

@OptIn(ExperimentalMaterial3Api::class, ExperimentalHazeMaterialsApi::class)
@Composable
fun CAlertDialogContent(
    component: CAlertDialogComponent,
    customIf: Boolean? = null,
    isSaveButtonEnabled: Boolean = true,
    isCustomButtons: Boolean = true,
    standardCustomButton: @Composable (() -> Unit)? = null,
    acceptColor: Color = MaterialTheme.colorScheme.primary,
    title: String = "",
    titleXOffset: Dp = 0.dp,
    acceptText: String = "Ок",
    declineText: String = "Отмена",
    dialogProperties: DialogProperties = DialogProperties(
        usePlatformDefaultWidth = false
    ),
    isClickOutsideEqualsDecline: Boolean = true,
    content: @Composable (() -> Unit)
) {
    val model by component.model.subscribeAsState()
    val nModel by component.nModel.subscribeAsState()
    val isShowing = customIf ?: model.isDialogShowing
    val viewManager = LocalViewManager.current
    
    val onDismissRequest = {
        if (isClickOutsideEqualsDecline) model.onDeclineClick.invoke()
        else component.onEvent(CAlertDialogStore.Intent.HideDialog)
    }

    if (isShowing) {
        BasicAlertDialog(
            onDismissRequest = onDismissRequest,
            properties = dialogProperties,
            // android fix
            modifier = Modifier.animateContentSize()
        ) {
            Box(modifier = Modifier, contentAlignment = Alignment.Center) {
                Box(Modifier.fillMaxSize().clickable(
                    indication = null,
                    interactionSource = null
                ) {
                    onDismissRequest()
                }) {}
                Surface(
                    modifier = Modifier
                        .imePadding()
                        .padding(horizontal = Paddings.large)
                        .animateContentSize()
                        .clip(MaterialTheme.shapes.large)
                        .hazeHeader(
                            viewManager = viewManager,
                            isMasked = false,
                            customStyle = HazeMaterials.regular()
                        ).hazeUnder(
                            viewManager,
                            zIndex = 4f
                        ),
                    color = if (viewManager.hazeHardware.value) Color.Transparent else MaterialTheme.colorScheme.surface,
                    shape = MaterialTheme.shapes.large
                ) {
                    Column {
                        if (title.isNotBlank()) {
                            Text(
                                title,
                                fontWeight = FontWeight.Bold,
                                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                modifier = Modifier.fillMaxWidth().offset(x = titleXOffset)
                                    .padding(top = 20.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                        Crossfade(
                            nModel.state,
                            modifier = Modifier.animateContentSize().heightIn(max = 600.dp)
                                .widthIn(min = TextFieldDefaults.MinWidth)
                        ) {

                            when (it) {
                                NetworkState.None -> {
                                    Column(Modifier.padding(6.dp)) {
                                        Box(Modifier.heightIn(max = 350.dp)) {
                                            content()
                                        }
                                        if (!isCustomButtons) {
                                            when (standardCustomButton) {
                                                null -> Row(
                                                    Modifier.fillMaxWidth()
                                                        .padding(vertical = 10.dp)
                                                        .padding(end = 20.dp),
                                                    horizontalArrangement = Arrangement.End
                                                ) {
                                                    CTextButton(
                                                        acceptText,
                                                        modifier = Modifier.padding(
                                                            end = 20.dp
                                                        ),
                                                        color = acceptColor,
                                                        isButtonEnabled = isSaveButtonEnabled
                                                    ) {
                                                        model.onAcceptClick.invoke()
                                                    }
                                                    CTextButton(
                                                        declineText
                                                    ) {
                                                        model.onDeclineClick.invoke()
                                                    }
//                                    AnimatedCommonButton(
//                                        text = "Создать",
//                                        isEnabled = model.isButtonEnabled,
//                                        modifier = Modifier.fillMaxWidth()
//                                    ) {
//                                        if (model.isButtonEnabled) {
//                                            model.onAcceptClick?.invoke()
//                                        }
//                                    }
                                                }

                                                else -> standardCustomButton()
                                            }

                                        }

                                    }
                                }

                                NetworkState.Loading -> {
                                    LoadingAnimation()
                                }

                                else -> {
                                    Column(
                                        Modifier.width(TextFieldDefaults.MinWidth).padding(6.dp)
                                            .padding(vertical = 6.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        DefaultErrorView(
                                            model = nModel,
                                            pos = DefaultErrorViewPos.Centered
                                        )
                                    }
                                }
                            }

                        }
                    }
                }
                HideKeyboardLayout()
            }
        }
    }
}

