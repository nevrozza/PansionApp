package homeComponents

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.GetAsyncImage
import components.NotificationItem
import home.HomeComponent
import home.HomeStore
import main.school.DutyKid
import resources.Images
import resources.RIcons
import school.SchoolComponent
import school.SchoolStore
import studentReportDialog.StudentReportDialogStore
import view.ViewManager
import view.blend
import view.easedGradient


// macsmillian page 28-29
// 19) used
// 20) had been working
// 21) found
// 22) became
// 23) had lived -
// 24) would
// 25) did not offer -
// 26) enjoyable
// 27) knowledge
// 28) competitions
// 29)

@OptIn(ExperimentalFoundationApi::class)
fun LazyListScope.homeStudentNotifications(
    model: HomeStore.State,
    kids: List<DutyKid>?,
    component: HomeComponent,
    viewManager: ViewManager
) {
    item {
        if (kids != null) {
            AnimatedVisibility(model.login in kids.map { it.login }) {
                val filteredKids = kids.filter { it.login != model.login }
                val text = "Ты${
                    if (filteredKids.isEmpty()) "" else ", ${
                        filteredKids.mapIndexed { i, it -> "${(if (i == filteredKids.lastIndex) "i" else "")}${it.fio.surname} ${it.fio.name[0]}" }
                            .toString().removePrefix("[").removeSuffix("]")
                    }".replace(", i", " и ")
                } дежури${if (filteredKids.isEmpty()) "шь" else "те"} сегодня!"

                OutlinedCard(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp).padding(bottom = 3.dp),
                    shape = RoundedCornerShape(15.dp),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.linearGradient(
                            colors = if (viewManager.isDark.value) listOf(
                                MaterialTheme.colorScheme.onPrimary,
                                MaterialTheme.colorScheme.onSecondary,
                                MaterialTheme.colorScheme.onTertiary
                            )
                            else listOf(
                                //Container
                                MaterialTheme.colorScheme.primaryContainer.blend(MaterialTheme.colorScheme.primary),

                                MaterialTheme.colorScheme.primaryContainer.blend(MaterialTheme.colorScheme.secondary),
                                MaterialTheme.colorScheme.onSecondaryContainer.blend(MaterialTheme.colorScheme.primaryContainer),
                                MaterialTheme.colorScheme.onTertiaryContainer.blend(MaterialTheme.colorScheme.primary),
                            )
                        ), width = (1.5).dp
                    )
                ) {
                    Row(
                        Modifier.fillMaxWidth().height(IntrinsicSize.Max).padding(horizontal = 18.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        GetAsyncImage(Images.Emoji.emojiCook, modifier = Modifier.padding(vertical = (2.5f).dp).size(35.dp))
                        Text(text, modifier = Modifier.padding(start = 20.dp))

                    }
                }
            }
        }
    }
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