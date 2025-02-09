package decomposeComponents

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.*
import components.cBottomSheet.CBottomSheetComponent
import components.cBottomSheet.CBottomSheetStore
import components.foundation.DefaultErrorView
import components.foundation.DefaultErrorViewPos
import components.foundation.LoadingAnimation
import components.networkInterface.NetworkState
import kotlinx.coroutines.launch

@ExperimentalMaterial3Api
@Composable
fun CBottomSheetContent(
    component: CBottomSheetComponent,
    customLoadingScreen: Boolean = false,
    customMaxHeight: Dp = 500.dp,
    content: @Composable () -> Unit
) {
    val model by component.model.subscribeAsState()
    val nModel by component.nModel.subscribeAsState()

    val isShowingCostil = remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    val modalBottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

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
//    DisposableEffect(model.isDialogShowing) {
//        onDispose {
//            if (!model.isDialogShowing) {
//                coroutineScope.launch {
//                    modalBottomSheetState.hide()
//                }.invokeOnCompletion {
//                    isShowingCostil.value = false
//                }
//            } else {
//                isShowingCostil.value = true
//            }
//        }
//    }

    if (isShowingCostil.value) {

        DefaultModalBottomSheet(
            additionalModifier = if (customMaxHeight != 0.dp) Modifier.sizeIn(maxHeight = customMaxHeight) else Modifier,
            modalBottomSheetState = modalBottomSheetState,
            onDismissRequest = {
                component.onEvent(CBottomSheetStore.Intent.HideSheet)
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
                    if (customLoadingScreen) {
                        when (it.state) {
                            NetworkState.Error -> DefaultErrorView(
                                nModel
                            )

                            else -> {
                                content()
                            }
                        }
                    } else {
                        when (it.state) {
                            NetworkState.None -> {
                                content()
                            }

                            NetworkState.Loading -> {
                                LoadingAnimation()
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