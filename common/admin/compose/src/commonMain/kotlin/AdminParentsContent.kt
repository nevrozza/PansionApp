
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.*
import components.refresh.RefreshButton
import components.refresh.RefreshWithoutPullCircle
import components.refresh.keyRefresh
import components.listDialog.ListDialogStore
import components.networkInterface.NetworkState
import components.networkInterface.isLoading
import decomposeComponents.listDialogComponent.ListDialogDesktopContent
import decomposeComponents.listDialogComponent.ListDialogMobileContent
import parents.AdminParentsComponent
import parents.AdminParentsStore
import pullRefresh.PullRefreshIndicator
import pullRefresh.pullRefresh
import pullRefresh.rememberPullRefreshState
import resources.RIcons
import view.LocalViewManager
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

    val viewManager = LocalViewManager.current

    val clipboardManager = LocalClipboardManager.current

    val refreshing = nModel.isLoading

    val refreshState = rememberPullRefreshState(
        refreshing,
        { component.onEvent(AdminParentsStore.Intent.Init) }
    )

    LaunchedEffect(Unit) {
        refreshState.onRefreshState.value()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().keyRefresh(refreshState),
        topBar = {
            AppBar(
                title = {
                    Text(
                        "Родители",
                        fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    RefreshWithoutPullCircle(refreshing, refreshState.position, model.kids.isNotEmpty())
                },
                navigationRow = {
                    IconButton(
                        onClick = { component.onOutput(AdminParentsComponent.Output.Back) }
                    ) {
                        GetAsyncIcon(
                            path = RIcons.ChevronLeft
                        )
                    }
                },
                actionRow = {

                    RefreshButton(refreshState, viewManager)
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
                }
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().pullRefresh(refreshState)) {
            Crossfade(nModel.state, modifier = Modifier.fillMaxSize()) { state ->
                when {
                    state is NetworkState.None || model.kids.isNotEmpty() -> CLazyColumn(
                        padding = padding, modifier = Modifier.horizontalScroll(
                            rememberScrollState()
                        ),
                        refreshState = refreshState
                    ) {
                        items(model.forms, key = { it.id }) { form ->
                            val kids = model.kids[form.id]
                            val title = "${form.classNum} ${form.title}"
                            if (!kids.isNullOrEmpty()) {
                                Spacer(Modifier.height(15.dp))
                                Text(
                                    title,
                                    fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.cClickable {
                                        clipboardManager.setText(
                                            buildAnnotatedString {
                                                append("$title\n")
                                                kids.forEach { s ->
                                                    val p = model.users.firstOrNull { it.login == s }
                                                    if (p != null) {
                                                        append(
                                                            "Ребёнок:\n" +
                                                            "${p.fio.surname} ${p.fio.name} ${p.fio.praname} - $s\n"
                                                        )
                                                        val parents = model.lines.filter { it.studentLogin == s }
                                                        if (parents.isNotEmpty()) {
                                                            append(
                                                                "Родители:\n"
                                                            )
                                                            parents.forEach { x ->
                                                                val xp = model.users.firstOrNull { it.login == x.parentLogin }
                                                                if (xp != null) {
                                                                    append("${xp.fio.surname} ${xp.fio.name} ${xp.fio.praname} - ${x.parentLogin}\n")
                                                                } else {
                                                                    append("null\n")
                                                                }
                                                            }
                                                        }
                                                    } else {
                                                        append("Ребёнок не найден")
                                                    }
                                                    append("\n")
                                                }
                                            }
                                        )
                                    }
                                )
                                kids.forEach { s ->
                                    val p = model.users.firstOrNull { it.login == s }
                                    Column(Modifier.cClickable(shape = 24) {
                                        clipboardManager.setText(
                                            buildAnnotatedString {
                                                if (p != null) {
                                                    append(
                                                        "Ребёнок:\n" +
                                                        "${p.fio.surname} ${p.fio.name} ${p.fio.praname} - $s\n"
                                                    )
                                                    val parents = model.lines.filter { it.studentLogin == s }
                                                    if (parents.isNotEmpty()) {
                                                        append(
                                                            "Родители:\n"
                                                        )
                                                        parents.forEach { x ->
                                                            val xp = model.users.firstOrNull { it.login == x.parentLogin }
                                                            if (xp != null) {
                                                                append("${xp.fio.surname} ${xp.fio.name} ${xp.fio.praname} - ${x.parentLogin}\n")
                                                            } else {
                                                                append("null\n")
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    append("Ребёнок не найден")
                                                }
                                            }
                                        )
                                    }.padding(bottom = 10.dp)) {
                                        if (p != null) {

                                            val parents = model.lines.filter { it.studentLogin == s }
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    "${p.fio.surname} ${p.fio.name} ${p.fio.praname} ($s)",
                                                    fontWeight = FontWeight(460),
                                                    fontSize = 18.esp
                                                )
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
                                    }
                                }
                            }

                        //                        Spacer(Modifier.height(6.dp))
                        }
                    }

                    state is NetworkState.Loading && model.kids.isEmpty() -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }

                    state is NetworkState.Error -> DefaultErrorView(nModel, DefaultErrorViewPos.CenteredFull)
                }
            }

            PullRefreshIndicator(refreshState, padding.calculateTopPadding())
        }


        ListDialogMobileContent(
            component = component.parentEditPicker
        )

        ListDialogMobileContent(
            component = component.childCreatePicker
        )
    }
}