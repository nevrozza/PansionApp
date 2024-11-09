import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cabinets.CabinetsComponent
import cabinets.CabinetsStore
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.AppBar
import components.CustomTextField
import components.hazeUnder
import components.networkInterface.NetworkState
import dev.chrisbanes.haze.HazeState
import view.LocalViewManager
import view.rememberImeState

@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
fun CabinetsContent(
    component: CabinetsComponent
) {
    val model by component.model.subscribeAsState()
    val nModel by component.nInterface.networkModel.subscribeAsState()
    val viewManager = LocalViewManager.current
    val hazeState = remember { HazeState() }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AppBar(
                navigationRow = {
                    IconButton(
                        onClick = { component.onOutput(CabinetsComponent.Output.Back) }
                    ) {
                        Icon(
                            Icons.Rounded.ArrowBackIosNew, null
                        )
                    }
                },
                title = {
                    Text(
                        "Кабинеты",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                hazeState = hazeState
            )
        },
        floatingActionButton = {
            Crossfade(nModel.state) {
                SmallFloatingActionButton(
                    onClick = {
                        if (it != NetworkState.Loading) {
                            component.onEvent(CabinetsStore.Intent.SendItToServer)
                        }
                    }
                ) {
                    when (it) {
                        NetworkState.None -> {
                            Icon(
                                Icons.Rounded.Save,
                                null
                            )
                        }

                        NetworkState.Loading -> {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp))
                        }

                        NetworkState.Error -> {
                            Text("Попробовать ещё раз")
                        }
                    }
                }

//                    AnimatedContent(nModel.state) {
//                    when (it) {
//                        NetworkState.None -> {
//                            SmallFloatingActionButton(
//                                onClick = {
//                                    component.onEvent(LessonReportStore.Intent.UpdateWholeReport)
//                                }
//                            ) {
//                                Icon(
//                                    Icons.Rounded.Save,
//                                    null
//                                )
//                            }
//                        }
//
//                        NetworkState.Loading -> {
//                            SmallFloatingActionButton(
//                                onClick = {}
//                            ) {
//                                CircularProgressIndicator()
//                            }
//                        }
//
//                        NetworkState.Error -> {}
//                    }
//                }
            }
        }
    ) { padding ->
//        Box(contentAlignment = Alignment.Center) {
        FlowRow(
            modifier = Modifier
                .padding(horizontal = 15.dp)
                .consumeWindowInsets(padding)
                .fillMaxSize()
                .imePadding()
                .hazeUnder(viewManager, hazeState = hazeState)
                .verticalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.Center

        ) {

            Spacer(Modifier.fillMaxWidth().height(padding.calculateTopPadding()))
            model.teachers.forEach { p ->
                val cabinet = model.cabinets.firstOrNull { it.login == p.login }
                Column(
                    Modifier.width(200.dp)
                ) {
                    Text(
                        buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold
                            )) {
                                append(p.fio.surname + " ")
                            }
                            append("${p.fio.name} ${p.fio.praname}")
                        },
                        textAlign = TextAlign.Center
                    )

                    Row {
                        CustomTextField(
                            value = cabinet?.cabinet?.toString() ?: "",
                            onValueChange = {
                                if(it == "") {
                                    component.onEvent(
                                        CabinetsStore.Intent.UpdateCabinet(
                                            p.login,
                                            0
                                        )
                                    )
                                } else if (it.matches(Regex("^[1-3]?[0-1]?[0-9]?$"))) {
                                    component.onEvent(
                                        CabinetsStore.Intent.UpdateCabinet(
                                            p.login,
                                            it.toInt()
                                        )
                                    )
                                }
                            },
                            text = "Номер кабинета",
                            isEnabled = nModel.state == NetworkState.None,
                            isMoveUpLocked = true,
                            autoCorrect = false,
                            keyboardType = KeyboardType.Number
                        )
                    }

                }
            }
        }
//        }

    }
}
