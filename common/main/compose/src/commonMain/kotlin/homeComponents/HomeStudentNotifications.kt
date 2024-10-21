package homeComponents

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import components.NotificationItem
import home.HomeComponent
import home.HomeStore
import studentReportDialog.StudentReportDialogStore
import view.ViewManager

@OptIn(ExperimentalFoundationApi::class)
fun LazyListScope.homeStudentNotifications(
    model: HomeStore.State,
    component: HomeComponent,
    viewManager: ViewManager
) {
    items(model.notifications, key = { it.key }) { not ->
        val notificationState = remember {
            MutableTransitionState(false).apply {
                // Start the animation immediately.
                targetState = true
            }
        }
        AnimatedVisibility(
            visibleState = notificationState,
            enter = fadeIn(initialAlpha = .2f) + scaleIn(initialScale = .9f),
            modifier = Modifier.animateItem()
        ) {
            NotificationItem(
                not,
                viewManager = viewManager,
                onClick = {
                    component.studentReportDialog.onEvent(
                        StudentReportDialogStore.Intent.OpenDialog(
                            login = model.login,
                            reportId = it
                        )
                    )
                }
            ) { key ->
                component.onEvent(HomeStore.Intent.CheckNotification(null, key))
            }
        }
    }
}