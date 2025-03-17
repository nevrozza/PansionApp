package decomposeComponents.listDialogComponent

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.desktop.ui.tooling.preview.utils.esp
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import components.DefaultModalBottomSheet
import components.foundation.DefaultErrorView
import components.foundation.DefaultErrorViewPos
import components.foundation.LoadingAnimation
import components.foundation.hazeHeader
import components.foundation.hazeUnder
import components.listDialog.ListComponent
import components.listDialog.ListDialogStore
import components.listDialog.ListItem
import components.networkInterface.NetworkInterface
import components.networkInterface.NetworkState
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import view.ViewManager


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetVariant(
    component: ListComponent,
    model: ListDialogStore.State,
    nModel: NetworkInterface.NetworkModel,
    isShowingCostil: MutableState<Boolean>,
    coroutineScope: CoroutineScope,
    modalBottomSheetState: SheetState,
    title: String = "Выберите",
    modifier: Modifier = Modifier,
    onClick: (ListItem) -> Unit = {}
) {


    val lazyListState = rememberLazyListState()

    if (isShowingCostil.value) {
        DefaultModalBottomSheet(
            additionalModifier = modifier.then(Modifier.sizeIn(maxHeight = 500.dp)),
            modalBottomSheetState = modalBottomSheetState,
            onDismissRequest = {
                component.onEvent(ListDialogStore.Intent.HideDialog)
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
                                            fontSize = MaterialTheme.typography.titleLarge.fontSize
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
                                items(model.list) {
                                    TextButton(
                                        modifier = Modifier.fillMaxWidth(),
                                        onClick = {
                                            component.onClick(it)
                                            onClick(it)
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalHazeMaterialsApi::class)
@Composable
fun DropdownVariant(
    component: ListComponent,
    viewManager: ViewManager,
    model: ListDialogStore.State,
    nModel: NetworkInterface.NetworkModel,
    isTooltip: Boolean,
    isFullHeight: Boolean,
    offset: DpOffset,
    modifier: Modifier = Modifier,
    title: String?,
    onClick: (ListItem) -> Unit = {}
) {
    DropdownMenu(
        expanded = model.isDialogShowing && isTooltip,
        onDismissRequest = {
            component.onEvent(ListDialogStore.Intent.HideDialog)
        },
        modifier = modifier.then(if (!isFullHeight) Modifier.sizeIn(maxHeight = 200.dp) else Modifier)
            .animateContentSize().hazeHeader(
                viewManager = viewManager,
                isMasked = false,
                customStyle = HazeMaterials.regular()
            ).hazeUnder(
                viewManager,
                zIndex = 3f
            ),
        containerColor = if (viewManager.hazeHardware.value) Color.Transparent else MenuDefaults.containerColor,
        offset = offset
    ) {
        Crossfade(
            nModel
        ) {
            Column() {
                if(title != null) {
                    Text(title, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 10.dp).padding(top = 5.dp), fontSize = 11.esp, lineHeight = 12.esp)
                }
                when (it.state) {
                    NetworkState.None -> {

                        model.list.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption.text) },
                                onClick = {
                                    component.onClick(selectionOption)
                                    onClick(selectionOption)
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                            )
                        }

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


val LazyListState.customConnection: NestedScrollConnection
    @Composable get() {
        return remember {
            object : NestedScrollConnection {
                override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                    return if (source in listOf(
                            NestedScrollSource.UserInput,
                            NestedScrollSource.SideEffect
                        ) && (!this@customConnection.canScrollBackward && available.y > 0 || !this@customConnection.canScrollForward && available.y < 0)
                    ) {
                        available
                    } else {
                        Offset.Zero
                    }
                }

                override suspend fun onPostFling(
                    consumed: Velocity,
                    available: Velocity
                ): Velocity {
                    return available
                }

//                private fun Float.toOffset() = Offset(0f, this)
            }
        }
    }

//@Composable
//fun () : NestedScrollConnection {
//
//}
