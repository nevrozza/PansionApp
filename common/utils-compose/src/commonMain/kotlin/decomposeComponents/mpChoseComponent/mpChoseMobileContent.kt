package decomposeComponents.mpChoseComponent

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.CustomTextButton
import components.DefaultModalBottomSheet
import components.LoadingAnimation
import components.listDialog.ListComponent
import components.listDialog.ListDialogStore
import components.mpChose.mpChoseComponent
import components.mpChose.mpChoseStore
import components.networkInterface.NetworkInterface
import components.networkInterface.NetworkState
import decomposeComponents.listDialogComponent.BottomSheetVariant
import decomposeComponents.listDialogComponent.customConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import view.LocalViewManager
import view.WindowScreen

@ExperimentalMaterial3Api
@Composable
fun mpChoseMobileContent(
    component: ListComponent,
    title: String = "Выберите"
) {
    val model by component.model.subscribeAsState()
    val nModel by component.nModel.subscribeAsState()
    val coroutineScope = rememberCoroutineScope()
    val viewManager = LocalViewManager.current

    val isTooltip = viewManager.orientation.value != WindowScreen.Vertical
//    if(model.isDialogShowing) {
//        AlertDialog({}){}
//    }

    if(!isTooltip) {
        val isShowingCostil = remember { mutableStateOf(false) }
        val modalBottomSheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )

        DisposableEffect(model.isDialogShowing) {
            onDispose {
                if (!model.isDialogShowing) {
                    coroutineScope.launch {
                        modalBottomSheetState.hide()
                    }.invokeOnCompletion {
                        isShowingCostil.value = false
                    }
                } else {
                    isShowingCostil.value = true
                }
            }

        }

        BottomSheetVariant(
            component = component,
            model = model,
            nModel = nModel,
            isShowingCostil = isShowingCostil,
            coroutineScope = coroutineScope,
            modalBottomSheetState = modalBottomSheetState,
            title = title
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetVariant(
    component: mpChoseComponent,
    model: mpChoseStore.State,
    nModel: NetworkInterface.NetworkModel,
    isShowingCostil: MutableState<Boolean>,
    coroutineScope: CoroutineScope,
    modalBottomSheetState: SheetState,
    title: String = "Выберите",
    content: @Composable () -> Unit
) {


    val lazyListState = rememberLazyListState()

    if (isShowingCostil.value) {
        DefaultModalBottomSheet(
            additionalModifier = Modifier.sizeIn(maxHeight = 500.dp),
            modalBottomSheetState = modalBottomSheetState,
            onDismissRequest = {
                component.onEvent(mpChoseStore.Intent.HideDialog)
            }
        ) {
            Crossfade(
                nModel,
                modifier = Modifier.animateContentSize()
            ) {
                Column(
                    Modifier.fillMaxWidth().defaultMinSize(minHeight = 100.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    when (it.state) {
                        NetworkState.None -> {

                            Text(
                                buildAnnotatedString {
                                    withStyle(
                                        SpanStyle(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 20.sp
                                        )
                                    ) {
                                        append(title)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                            LazyColumn(
                                Modifier.imePadding().padding(top = 5.dp)
                                    .nestedScroll(lazyListState.customConnection),
                                state = lazyListState
                            ) {


                                item {
//                                    val interactionSource =
//                                        remember { MutableInteractionSource() }
//                                    val isDark =
//                                        (interactionSource.collectIsHoveredAsState().value || interactionSource.collectIsPressedAsState().value)
//                                    val color by animateColorAsState(
//                                        animationSpec = spring(stiffness = Spring.StiffnessHigh),
//                                        targetValue = if (isDark)  else Color.Transparent
//                                    )
                                    TextButton(
                                        modifier = Modifier.fillMaxWidth()
                                            .clip(RoundedCornerShape(15.dp))
                                                ,
//                                            .hoverable(interactionSource),

                                        onClick = {
                                            coroutineScope.launch {
                                                modalBottomSheetState.hide()
                                            }.invokeOnCompletion {
                                                component.onEvent(mpChoseStore.Intent.HideDialog)
                                            }
                                        },
                                        shape = RoundedCornerShape(15.dp),
                                        colors = ButtonDefaults.textButtonColors(
                                            containerColor = Color.Transparent,
                                            contentColor = MaterialTheme.colorScheme.errorContainer.copy(
                                                alpha = .3f
                                            )
                                        ),
                                    ) {
                                        Text("Отмена")
                                    }
                                }
                            }


                        }

                        NetworkState.Loading -> {
                            LoadingAnimation()
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
