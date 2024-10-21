package homeComponents

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import components.CustomTextButton
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
    if (!(model.childrenNotifications.flatMap { it.value }.isEmpty() && model.notChildren.isEmpty() && nQuickTabModel.state == NetworkState.None ) && itShouldBe) {
        item {
            Text(
                "Уведомления",
                modifier = Modifier.fillMaxWidth(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
        item {

            AnimatedVisibility(
                nQuickTabModel.state != NetworkState.None || model.childrenNotifications.isEmpty(),
                enter = fadeIn() + expandVertically(
                    expandFrom = Alignment.Top, clip = false
                ),
                exit = fadeOut() + shrinkVertically(
                    shrinkTowards = Alignment.Top, clip = false
                )
            ) {
                Column {
                    Spacer(Modifier.height(7.5.dp))
                    Crossfade(
                        nQuickTabModel.state,
                        modifier = Modifier.fillMaxSize()
                    ) { state ->
                        when (state) {
                            NetworkState.None -> {
                                if (model.childrenNotifications.isEmpty()) {
                                    Text("Нет никаких уведомлений")
                                }
                            }

                            NetworkState.Loading -> {
                                Box(
                                    Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }

                            NetworkState.Error -> {
                                Column(
                                    Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(nQuickTabModel.error)
                                    Spacer(Modifier.height(7.dp))
                                    CustomTextButton("Попробовать ещё раз") {
                                        nQuickTabModel.onFixErrorClick()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        items(items = model.notChildren) { s ->
            val list = model.childrenNotifications[s.login] ?: listOf()
            if(list.isNotEmpty()) {
                Spacer(Modifier.height(5.5.dp))
                Text(
                    "${s.fio.surname} ${s.fio.name}",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                list.forEach {
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
                }
            }
        }
    }
}