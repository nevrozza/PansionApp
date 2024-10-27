import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.AppBar
import components.CLazyColumn
import components.CustomTextButton
import components.listDialog.ListDialogStore
import components.networkInterface.NetworkState
import decomposeComponents.listDialogComponent.ListDialogDesktopContent
import decomposeComponents.listDialogComponent.ListDialogMobileContent
import dev.chrisbanes.haze.HazeState
import parents.AdminParentsComponent
import parents.AdminParentsStore
import view.LocalViewManager
import view.rememberImeState

@OptIn(
    ExperimentalLayoutApi::class, ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun AdminParentsContent(
    component: AdminParentsComponent,
    isVisible: Boolean
) {
    val model by component.model.subscribeAsState()
    val nModel by component.nInterface.networkModel.subscribeAsState()

    val hazeState = remember { HazeState() }

    val viewManager = LocalViewManager.current
    val scrollState = rememberScrollState()
    val imeState = rememberImeState()
    val lazyListState = rememberLazyListState()
    val density = LocalDensity.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AppBar(
                navigationRow = {
                    IconButton(
                        onClick = { component.onOutput(AdminParentsComponent.Output.Back) }
                    ) {
                        Icon(
                            Icons.Rounded.ArrowBackIosNew, null
                        )
                    }
                },
                title = {
                    Text(
                        "Родители",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                actionRow = {

                    IconButton(
                        onClick = { component.onEvent(AdminParentsStore.Intent.Init) }
                    ) {
                        Icon(
                            Icons.Rounded.Refresh, null
                        )
                    }
                    Box() {
                        IconButton(
                            onClick = {
                                component.childCreatePicker.onEvent(ListDialogStore.Intent.ShowDialog)
                            }
                        ) {
                            Icon(
                                Icons.Rounded.Add, null
                            )
                        }
                        ListDialogDesktopContent(
                            component = component.childCreatePicker
                        )
                    }
                },
                hazeState = hazeState,
                isHazeActivated = isVisible
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
                                Text("${p.fio.surname} ${p.fio.name} ${p.fio.praname} ($s)", fontWeight = FontWeight.Bold, fontSize = 18.sp)
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
                                            Icon(
                                                Icons.Rounded.Add, null
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
                                                Icon(
                                                    Icons.Rounded.Edit,
                                                    null
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

                NetworkState.Error -> {
                    Column(
                        Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(nModel.error)
                        Spacer(Modifier.height(7.dp))
                        CustomTextButton("Попробовать ещё раз") {
                            nModel.onFixErrorClick()
                        }
                    }
                }
            }
        }


        ListDialogMobileContent(
            component = component.parentEditPicker,
            hazeState = hazeState
        )

        ListDialogMobileContent(
            component = component.childCreatePicker,
            hazeState = hazeState
        )
    }
}