import admin.schedule.ScheduleFormValue
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import components.CustomTextButton
import components.CustomTextField
import components.mpChose.mpChoseStore
import components.networkInterface.NetworkInterface
import components.networkInterface.NetworkState
import decomposeComponents.mpChoseComponent.mpChoseDesktopContent
import schedule.ScheduleComponent
import schedule.ScheduleStore
import schedule.ScheduleStore.EditState
import schedule.ScheduleTiming
import schedule.timingsPairs
import server.isTimeFormat
import server.toMinutes

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LazyItemScope.ScheduleColumnForForms(
    component: ScheduleComponent,
    model: ScheduleStore.State,
    nModel: NetworkInterface.NetworkModel,
    mpModel: mpChoseStore.State,
    scrollState: ScrollState,
    minuteHeight: Dp,
    dayStartTime: String,
    form: ScheduleFormValue,
    formId: Int,
    key: String,
    headerP: Dp,
    density: Density
) {
    val groups = model.groups.filter {
        it.id in model.students.filter { s -> s.login in form.logins }.flatMap { s -> s.groups.map { it.first } }
    }

    Box(
        Modifier.width(200.dp).padding(end = 5.dp)
            .animateItemPlacement()
    ) {
        val headerState = remember {
            MutableTransitionState(false).apply {
                // Start the animation immediately.
                targetState = true
            }
        }
        AnimatedVisibility(
            visibleState = headerState,
            enter = fadeIn() + scaleIn(),
            modifier = Modifier.zIndex(1000f)
        ) {
            Box(
                Modifier.zIndex(1000f).height(headerP)
                    .offset(y = with(density) { scrollState.value.toDp() }).zIndex(1000f)
            ) {
//                Column(
//                    modifier = Modifier.fillMaxSize()
//                        .align(Alignment.Center),
//                    verticalArrangement = Arrangement.Center,
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
                Box(Modifier.fillMaxSize()) {
                    Text(
                        "${form.num}${if (form.shortTitle.length < 2) "-" else " "}${form.shortTitle}",
                        modifier = Modifier.align(Alignment.Center),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        lineHeight = 15.sp
                    )
                    Text(
                        text = "${form.logins.size}",
                        modifier = Modifier.align(Alignment.BottomEnd).padding(end = 5.dp, bottom = 2.dp)
                    )
                }
//                }
            }
        }
        Box(Modifier.padding(top = headerP)) {
            val trueItems =
                model.items[key]?.filter { it.groupId in groups.map { it.id } }
            trueItems?.forEach { e ->
                val index = trueItems.indexOf(e)
                val aState = remember {
                    MutableTransitionState(false).apply {
                        // Start the animation immediately.
                        targetState = true
                    }
                }
                AnimatedVisibility(
                    visibleState = aState,
                    enter = fadeIn() + scaleIn()
                ) {
                    val tPadding by animateDpAsState(
                        minuteHeight * (e.t.start.toMinutes() - dayStartTime.toMinutes())
                    )
                    val height by animateDpAsState(minuteHeight * (e.t.end.toMinutes() - e.t.start.toMinutes()))

                    Card(
                        modifier = Modifier
                            .padding(top = tPadding)
                            .fillMaxWidth()
                            .height(height),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        ),
                        onClick = {
                            component.onEvent(
                                ScheduleStore.Intent.StartEdit(
                                    index,
                                    formId
                                )
                            )
                        }
                    ) {
                        Box(Modifier.fillMaxSize()) {
                            if (e.groupId == -11) {
                                Text(
                                    modifier = Modifier.fillMaxSize(),
                                    textAlign = TextAlign.Center,
                                    text = "Обед",
                                    lineHeight = 14.sp,
                                    fontSize = 14.sp,
                                )

                                Text(
                                    e.t.start,
                                    modifier = Modifier.align(
                                        Alignment.BottomStart
                                    )
                                        .padding(start = 5.dp),
                                    lineHeight = 13.sp,
                                    fontSize = 13.sp,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    e.t.end,
                                    modifier = Modifier.align(
                                        Alignment.BottomEnd
                                    )
                                        .padding(end = 5.dp),
                                    lineHeight = 13.sp,
                                    fontSize = 13.sp,
                                    textAlign = TextAlign.Center
                                )

                            } else {
                                val group =
                                    model.groups.first { it.id == e.groupId }
                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(
                                            style = SpanStyle(
                                                fontWeight = FontWeight.Bold
                                            )
                                        ) {
                                            append(model.subjects.first { it.id == group.subjectId }.name)
                                        }
                                        append("\n" + group.name)
                                    },
                                    lineHeight = 14.sp,
                                    fontSize = 14.sp,
                                    modifier = Modifier.align(
                                        Alignment.Center
                                    ),
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    e.t.start,
                                    modifier = Modifier.align(
                                        Alignment.BottomStart
                                    )
                                        .padding(start = 5.dp),
                                    lineHeight = 13.sp,
                                    fontSize = 13.sp,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    e.t.end,
                                    modifier = Modifier.align(
                                        Alignment.BottomEnd
                                    )
                                        .padding(end = 5.dp),
                                    lineHeight = 13.sp,
                                    fontSize = 13.sp,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    e.cabinet.toString(),
                                    modifier = Modifier.align(
                                        Alignment.TopEnd
                                    )
                                        .padding(end = 5.dp),
                                    lineHeight = 13.sp,
                                    fontSize = 13.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                            if (model.eiIndex == index && model.eiFormId == formId) {
                                mpChoseDesktopContent(
                                    component = component.mpEditItem,
                                    offset = DpOffset(
                                        x = 130.dp,
                                        y = (-35).dp
                                    )
                                ) {
                                    val kids = model.students.filter { it.login in form.logins }
                                        .filter { e.groupId in it.groups.map { it.first } }
                                    Column(Modifier.padding(horizontal = 10.dp, vertical = 5.dp)) {
                                        Text("В этой группе:")
                                        if (kids.isNotEmpty()) {
                                            kids.forEach {
                                                Text("${it.fio.surname} ${it.fio.name.first()}. ${(it.fio.praname ?: " ").first()}.")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}