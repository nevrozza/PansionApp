import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import cabinets.CabinetsComponent
import cabinets.CabinetsStore
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.AppBar
import components.CustomTextField
import components.GetAsyncIcon
import components.hazeUnder
import components.networkInterface.NetworkState
import components.networkInterface.isLoading
import dev.chrisbanes.haze.HazeState
import resources.RIcons
import view.LocalViewManager

@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
fun CabinetsContent(
    component: CabinetsComponent
) {

    

    val model by component.model.subscribeAsState()
    val nModel by component.nInterface.networkModel.subscribeAsState()


    LaunchedEffect(Unit) {
        if(!nModel.isLoading) component.onEvent(CabinetsStore.Intent.Init)
    }

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
                        GetAsyncIcon(
                            path = RIcons.ChevronLeft
                        )
                    }
                },
                title = {
                    Text(
                        "Кабинеты",
                        fontSize = MaterialTheme.typography.headlineSmall.fontSize,
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
                            GetAsyncIcon(
                                path = RIcons.Save
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
