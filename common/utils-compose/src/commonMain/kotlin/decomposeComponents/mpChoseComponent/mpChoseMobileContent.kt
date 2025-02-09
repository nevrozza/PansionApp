package decomposeComponents.mpChoseComponent

//@ExperimentalMaterial3Api
//@Composable
//fun mpChoseMobileContent(
//    component: ListComponent,
//    title: String = "Выберите"
//) {
//    val model by component.model.subscribeAsState()
//    val nModel by component.nModel.subscribeAsState()
//    val coroutineScope = rememberCoroutineScope()
//    val viewManager = LocalViewManager.current
//
//    val isTooltip = viewManager.orientation.value != WindowScreen.Vertical
////    if(model.isDialogShowing) {
////        AlertDialog({}){}
////    }
//
//    if(!isTooltip) {
//        val isShowingCostil = remember { mutableStateOf(false) }
//        val modalBottomSheetState = rememberModalBottomSheetState(
//            skipPartiallyExpanded = true
//        )
//
//        DisposableEffect(model.isDialogShowing) {
//            onDispose {
//                if (!model.isDialogShowing) {
//                    coroutineScope.launch {
//                        modalBottomSheetState.hide()
//                    }.invokeOnCompletion {
//                        isShowingCostil.value = false
//                    }
//                } else {
//                    isShowingCostil.value = true
//                }
//            }
//
//        }
//
//        BottomSheetVariant(
//            component = component,
//            model = model,
//            nModel = nModel,
//            isShowingCostil = isShowingCostil,
//            coroutineScope = coroutineScope,
//            modalBottomSheetState = modalBottomSheetState,
//            title = title,
//            hazeState =
//        )
//    }
//
//}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun BottomSheetVariant(
//    component: MpChoseComponent,
//    model: MpChoseStore.State,
//    nModel: NetworkInterface.NetworkModel,
//    isShowingCostil: MutableState<Boolean>,
//    coroutineScope: CoroutineScope,
//    modalBottomSheetState: SheetState,
//    title: String = "Выберите",
//    content: @Composable () -> Unit
//) {
//
//
//    val lazyListState = rememberLazyListState()
//
//    if (isShowingCostil.value) {
//        DefaultModalBottomSheet(
//            additionalModifier = Modifier.sizeIn(maxHeight = 500.dp),
//            modalBottomSheetState = modalBottomSheetState,
//            onDismissRequest = {
//                component.onEvent(MpChoseStore.Intent.HideDialog)
//            }
//        ) {
//            Crossfade(
//                nModel,
//                modifier = Modifier.animateContentSize()
//            ) {
//                Column(
//                    Modifier.fillMaxWidth().defaultMinSize(minHeight = 100.dp),
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    verticalArrangement = Arrangement.Center
//                ) {
//                    when (it.state) {
//                        NetworkState.None -> {
//
//                            Text(
//                                buildAnnotatedString {
//                                    withStyle(
//                                        SpanStyle(
//                                            fontWeight = FontWeight.Bold,
//                                            fontSize = 20.sp
//                                        )
//                                    ) {
//                                        append(title)
//                                    }
//                                },
//                                modifier = Modifier.fillMaxWidth(),
//                                textAlign = TextAlign.Center
//                            )
//                            LazyColumn(
//                                Modifier.imePadding().padding(top = 5.dp)
//                                    .nestedScroll(lazyListState.customConnection),
//                                state = lazyListState
//                            ) {
//
//
//                                item {
////                                    val interactionSource =
////                                        remember { MutableInteractionSource() }
////                                    val isDark =
////                                        (interactionSource.collectIsHoveredAsState().value || interactionSource.collectIsPressedAsState().value)
////                                    val color by animateColorAsState(
////                                        animationSpec = spring(stiffness = Spring.StiffnessHigh),
////                                        targetValue = if (isDark)  else Color.Transparent
////                                    )
//                                    TextButton(
//                                        modifier = Modifier.fillMaxWidth()
//                                            .clip(RoundedCornerShape(15.dp))
//                                                ,
////                                            .hoverable(interactionSource),
//
//                                        onClick = {
//                                            coroutineScope.launch {
//                                                modalBottomSheetState.hide()
//                                            }.invokeOnCompletion {
//                                                component.onEvent(MpChoseStore.Intent.HideDialog)
//                                            }
//                                        },
//                                        shape = RoundedCornerShape(15.dp),
//                                        colors = ButtonDefaults.textButtonColors(
//                                            containerColor = Color.Transparent,
//                                            contentColor = MaterialTheme.colorScheme.errorContainer.copy(
//                                                alpha = .3f
//                                            )
//                                        ),
//                                    ) {
//                                        Text("Отмена")
//                                    }
//                                }
//                            }
//
//
//                        }
//
//                        NetworkState.Loading -> {
//                            LoadingAnimation()
//                        }
//
//                        else -> {
//
//                            Text(nModel.error)
//                            Spacer(Modifier.height(7.dp))
//                            CTextButton("Попробовать ещё раз") {
//                                nModel.onFixErrorClick()
//                            }
//                        }
//                    }
//
//                }
//            }
//        }
//    }
//}
