import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.*
import components.listDialog.ListDialogStore
import components.networkInterface.NetworkState
import decomposeComponents.listDialogComponent.ListDialogDesktopContent
import decomposeComponents.listDialogComponent.ListDialogMobileContent
import dev.chrisbanes.haze.HazeState
import parents.AdminParentsComponent
import parents.AdminParentsStore
import resources.RIcons
import view.esp

@OptIn(
    ExperimentalLayoutApi::class, ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun AdminParentsContent(
    component: AdminParentsComponent
) {
    val model by component.model.subscribeAsState()
    val nModel by component.nInterface.networkModel.subscribeAsState()

    val hazeState = remember { HazeState() }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AppBar(
                navigationRow = {
                    IconButton(
                        onClick = { component.onOutput(AdminParentsComponent.Output.Back) }
                    ) {
                        GetAsyncIcon(
                            path = RIcons.ChevronLeft
                        )
                    }
                },
                title = {
                    Text(
                        "Родители",
                        fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                actionRow = {

                    IconButton(
                        onClick = { component.onEvent(AdminParentsStore.Intent.Init) }
                    ) {
                        GetAsyncIcon(
                            RIcons.Refresh
                        )
                    }
                    Box() {
                        IconButton(
                            onClick = {
                                component.childCreatePicker.onEvent(ListDialogStore.Intent.ShowDialog)
                            }
                        ) {
                            GetAsyncIcon(
                                RIcons.Add
                            )
                        }
                        ListDialogDesktopContent(
                            component = component.childCreatePicker
                        )
                    }
                },
                hazeState = hazeState
            )
        }
    ) { padding ->
        Crossfade(nModel.state, modifier = Modifier.fillMaxSize()) { state ->
            when (state) {
                NetworkState.None -> CLazyColumn(padding = padding, modifier = Modifier.horizontalScroll(
                    rememberScrollState()), hazeState = hazeState) {
                    items(model.kids) { s ->

                        val p = model.users.firstOrNull { it.login == s}
                        if (p != null) {

                            val parents = model.lines.filter { it.studentLogin == s }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("${p.fio.surname} ${p.fio.name} ${p.fio.praname} ($s)", fontWeight = FontWeight.Bold, fontSize = 18.esp)
                                Spacer(Modifier.width(5.dp))
                                if (parents.size < 2) {
                                    Box {
                                        IconButton(
                                            onClick = {
                                                component.onEvent(
                                                    AdminParentsStore.Intent.AddToStudent(
                                                        p.login
                                                    )
                                                )
                                            },
                                            modifier = Modifier.size(20.dp)
                                        ) {
                                            GetAsyncIcon(
                                                RIcons.Add
                                            )
                                        }
                                        if (model.addToStudent == p.login) {
                                            ListDialogDesktopContent(
                                                component = component.parentEditPicker
                                            )
                                        }
                                    }
                                }
                            }
                            parents.forEach { x ->
                                val xp = model.users.firstOrNull { it.login == x.parentLogin }
                                if (xp != null) {
                                    Row {
                                        Text(" * ${xp.fio.surname} ${xp.fio.name} ${xp.fio.praname} (${x.parentLogin})")
                                        Spacer(Modifier.width(5.dp))
                                        Box() {
                                            IconButton(
                                                onClick = {
                                                    component.onEvent(
                                                        AdminParentsStore.Intent.EditId(
                                                            x.id
                                                        )
                                                    )
                                                },
                                                modifier = Modifier.size(20.dp)
                                            ) {
                                                GetAsyncIcon(
                                                    RIcons.Edit,
                                                    size = 17.dp
                                                )
                                            }
                                            if (model.editId == x.id) {
                                                ListDialogDesktopContent(
                                                    component = component.parentEditPicker
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

//                        Spacer(Modifier.height(6.dp))
                    }
                }

                NetworkState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                NetworkState.Error -> DefaultErrorView(nModel, DefaultErrorViewPos.CenteredFull)
            }
        }


        ListDialogMobileContent(
            component = component.parentEditPicker
        )

        ListDialogMobileContent(
            component = component.childCreatePicker
        )
    }
}