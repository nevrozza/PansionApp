package decomposeComponents

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.OverscrollEffect
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.overscroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.CustomTextButton
import components.LoadingAnimation
import components.listDialog.ListDialogComponent
import components.listDialog.ListDialogStore
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import view.LocalViewManager
import view.WindowScreen
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@ExperimentalMaterial3Api
@Composable
fun ListDialogContent(
    component: ListDialogComponent
) {
    val model by component.model.subscribeAsState()
    val coroutineScope = rememberCoroutineScope()
    val viewManager = LocalViewManager.current
    val modalBottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val isShowingCostil = remember { mutableStateOf(false) }
    val isTooltip = viewManager.orientation.value != WindowScreen.Vertical

    val density = LocalDensity.current


    val lazyListState = rememberLazyListState()
    
    val connection = remember {
        object : NestedScrollConnection {

            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                return if (source in listOf(
                        NestedScrollSource.Drag,
                        NestedScrollSource.Fling
                    ) && (!lazyListState.canScrollBackward && available.y > 0 || !lazyListState.canScrollForward && available.y < 0)
                ) {
                    available
                } else {
                    Offset.Zero
                }
            }

            //            override fun onPostScroll(
//                consumed: Offset,
//                available: Offset,
//                source: NestedScrollSource
//            ): Offset {
//                val delta = available.y
//                return delta.toOffset()
//            }
//
//            override suspend fun onPreFling(available: Velocity): Velocity {
//                return available
//            }
//
            override suspend fun onPostFling(
                consumed: Velocity,
                available: Velocity
            ): Velocity {
                return available
//                return super.onPostFling(consumed, available)
            }

            private fun Float.toOffset() = Offset(0f, this)
        }
    }

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

    DropdownMenu(
        expanded = model.isDialogShowing && isTooltip,
        onDismissRequest = {
//            if(!model.isInProcess && model.error.isBlank()) {
            component.onEvent(ListDialogStore.Intent.HideDialog)
//            }
        },
        modifier = Modifier.sizeIn(maxHeight = 200.dp).animateContentSize(),
        offset = DpOffset(
            x = with(density) { model.x.toDp() + 50.dp; },
            y = with(density) {
                if (viewManager.size!!.maxHeight - model.y.toDp() >= 250.dp) -(viewManager.size!!.maxHeight - model.y.toDp())
                else {
                    0.dp
                }
            }
        )
    ) {
        Crossfade(
            model
        ) {
            Column {
                when {
                    it.error.isBlank() && !it.isInProcess -> {

                        model.list.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption.text) },
                                onClick = {
                                    component.onClick(selectionOption)
//                    component.onEvent(ListDialogStore.Intent.HideDialog)
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                            )
                        }

                    }

                    it.isInProcess -> {
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
                        Text(model.error)
                        Spacer(Modifier.height(7.dp))
                        CustomTextButton("Попробовать ещё раз") {
                            component.onEvent(ListDialogStore.Intent.ClearError())
                        }
                    }
                }
            }
        }
    }
    // menu items
    if (isShowingCostil.value && !isTooltip) {

//            if (modalBottomSheetState.isVisible) {
//                LaunchedEffect(lazyListState.canScrollBackward) {
//                    if (lazyListState.canScrollBackward) {
//                        modalBottomSheetState.expand()
//                    } else {
//                        modalBottomSheetState.partialExpand()
//                    }
//                }
//            }

        ModalBottomSheet(
            onDismissRequest = {
//                if(!model.isInProcess && model.error.isBlank()) {
                component.onEvent(ListDialogStore.Intent.HideDialog)
//                } else {
//                    coroutineScope.launch {
//                        modalBottomSheetState.show()
//                    }
//                }
            },
            sheetState = modalBottomSheetState,
            windowInsets = WindowInsets.ime

        ) {
//                val focusManager = LocalFocusManager.current
            Crossfade(
                model,
                modifier = Modifier.animateContentSize()
            ) {
                Column(
                    Modifier.padding(bottom = 10.dp).padding(horizontal = 10.dp)
                        .fillMaxWidth().defaultMinSize(minHeight = 100.dp)
                        .sizeIn(maxHeight = 500.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    when {
                        it.error.isBlank() && !it.isInProcess -> {

                            Text(
                                buildAnnotatedString {
                                    withStyle(
                                        SpanStyle(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 20.sp
                                        )
                                    ) {
                                        append("Выберите")
                                    }
//                                withStyle(
//                                    SpanStyle(
//                                        fontSize = 15.sp,
//                                        color = MaterialTheme.colorScheme.primary,
//                                        fontWeight = FontWeight.SemiBold
//                                    )
//                                ) {
//                                    append("$num/${properties.size}")
//                                }
                                }
                            )
                            LazyColumn(
                                Modifier.imePadding().padding(top = 5.dp)
                                    .nestedScroll(connection),
                                state = lazyListState
                            ) {
                                items(model.list) {
                                    TextButton(
                                        modifier = Modifier.fillMaxWidth(),
                                        onClick = {
                                            component.onClick(it)
                                        },
                                        colors = ButtonDefaults.textButtonColors(
                                            contentColor = MaterialTheme.colorScheme.onSurface
                                        ),
                                        shape = RoundedCornerShape(15.dp)
                                    ) {
                                        Text(it.text)
                                    }
                                }

                                item {
                                    val interactionSource =
                                        remember { MutableInteractionSource() }
                                    val isDark =
                                        (interactionSource.collectIsHoveredAsState().value || interactionSource.collectIsPressedAsState().value)
                                    val color by animateColorAsState(
                                        animationSpec = spring(stiffness = Spring.StiffnessHigh),
                                        targetValue = if (isDark) MaterialTheme.colorScheme.errorContainer.copy(
                                            alpha = .3f
                                        ) else Color.Transparent
                                    )
                                    TextButton(
                                        modifier = Modifier.fillMaxWidth()
                                            .clip(RoundedCornerShape(15.dp))
                                            .hoverable(interactionSource),

                                        onClick = {
                                            coroutineScope.launch {
                                                modalBottomSheetState.hide()
                                            }.invokeOnCompletion {
                                                component.onEvent(ListDialogStore.Intent.HideDialog)
                                            }
                                        },
                                        shape = RoundedCornerShape(15.dp),
                                        colors = ButtonDefaults.textButtonColors(
                                            containerColor = color,
                                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                    ) {
                                        Text("Отмена")
                                    }
                                }
                            }


                        }

                        model.isInProcess -> {
//                            Box(
//                                Modifier.fillMaxSize(),
//                                contentAlignment = Alignment.Center
//                            ) {
                            LoadingAnimation()
//                            }
                        }

                        else -> {

                            Text(model.error)
                            Spacer(Modifier.height(7.dp))
                            CustomTextButton("Попробовать ещё раз") {
                                component.onEvent(ListDialogStore.Intent.ClearError(model.onRetrySpecialClick))
                            }
                        }
                    }

                }
            }
        }



    }
}