package homeComponents

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import components.CustomTextButton
import components.DefaultErrorView
import components.LoadingAnimation
import components.NotificationItem
import components.networkInterface.NetworkInterface
import components.networkInterface.NetworkState
import home.HomeComponent
import home.HomeStore
import studentReportDialog.StudentReportDialogStore
import view.ViewManager

fun LazyListScope.homeChildrenNotificationsContent(
    model: HomeStore.State,
    nQuickTabModel: NetworkInterface.NetworkModel,
    viewManager: ViewManager,
    component: HomeComponent
) {
    val itShouldBe = (model.isMentor || model.isParent)
    if (!(model.childrenNotifications.flatMap { it.value }
            .isEmpty() && model.notChildren.isEmpty() && nQuickTabModel.state == NetworkState.None) && itShouldBe) {
        item {
            Text(
                "Уведомления",
                modifier = Modifier.fillMaxWidth(),
                fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(2.dp))
        }
        item {

//            AnimatedVisibility(
//                nQuickTabModel.state != NetworkState.None || model.childrenNotifications.isEmpty(),
//                enter = fadeIn() + expandVertically(
//                    expandFrom = Alignment.Top, clip = false
//                ),
//                exit = fadeOut() + shrinkVertically(
//                    shrinkTowards = Alignment.Top, clip = false
//                )
//            ) {
            Column {
                Spacer(Modifier.height(7.5.dp))
                val state = nQuickTabModel.state
//                    Crossfade(
//                        nQuickTabModel.state,
//                        modifier = Modifier.fillMaxSize()
                when (state) {
                    NetworkState.None -> {
                        if (model.childrenNotifications.isEmpty()) {
                            Text("Нет никаких уведомлений")
                        }
                    }

                    NetworkState.Loading -> {
                        Box(
                            Modifier.height(60.dp).offset(y = 5.dp)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            LoadingAnimation()
                        }
                    }

                    NetworkState.Error -> DefaultErrorView(nQuickTabModel)
                }
            }
        }
        model.notChildren.forEach { s ->
            val list = model.childrenNotifications[s.login] ?: listOf()
            if (list.isNotEmpty()) {
                itemsIndexed(list, key = { i, it -> it.key }) { i, it ->
                    if (i == list.indexOf(list.first())) {
                        Text(
                            "${s.fio.surname} ${s.fio.name}",
                            modifier = Modifier.padding(start = 12.dp),
                            fontSize = MaterialTheme.typography.titleLarge.fontSize,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    NotificationItem(
                        not = it,
                        viewManager = viewManager,
                        onClick = { reportId ->
                            component.studentReportDialog.onEvent(
                                StudentReportDialogStore.Intent.OpenDialog(
                                    login = s.login,
                                    reportId = reportId
                                )
                            )
                        },
                        changeToUV = { reportId ->
                            component.onEvent(
                                HomeStore.Intent.ChangeToUv(
                                    reportId = reportId,
                                    login = s.login,
                                    isDeep = false
                                )
                            )
                        }
                    ) { key ->
                        component.onEvent(HomeStore.Intent.CheckNotification(s.login, key))
                    }
                    if (i == list.lastIndex) {
                        Spacer(Modifier.height(5.dp))
                    }
                }
            }
        }
    }
}