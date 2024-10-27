import admin.schedule.ScheduleFormValue
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import components.cClickable
import components.mpChose.MpChoseStore
import components.networkInterface.NetworkInterface
import decomposeComponents.mpChoseComponent.mpChoseDesktopContent
import schedule.ScheduleComponent
import schedule.ScheduleItem
import schedule.ScheduleStore
import server.toMinutes


//data class ScheduleForFormsItem(
//    val lessons:
//)

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LazyItemScope.ScheduleColumnForForms(
    component: ScheduleComponent,
    model: ScheduleStore.State,
    nModel: NetworkInterface.NetworkModel,
    mpModel: MpChoseStore.State,
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
        Modifier.width(200.dp).padding(end = 5.dp).animateItem(fadeInSpec = null, fadeOutSpec = null)
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
                        modifier = Modifier.align(Alignment.BottomEnd)
                            .padding(end = 5.dp, bottom = 2.dp)
                    )
                    print("Sadiss: ${form.logins}")
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
                val coItems =
                    (trueItems).filter {
                        // ! (закончилось раньше чем началось наше) или (началось позже чем началось наше)
                        ((!((it.t.end.toMinutes() < e.t.start.toMinutes() ||
                                it.t.start.toMinutes() > e.t.end.toMinutes())) && it.groupId != -11) ||
                                (!((it.t.end.toMinutes() <= e.t.start.toMinutes() ||
                                        it.t.start.toMinutes() >= e.t.end.toMinutes())) && it.groupId == -11))
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
                        if (coItems.size <= 1) {
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
                                    ScheduleForFormsContent(
                                        e = e,
                                        model = model
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
                        } else {
                            Box(Modifier.fillMaxSize()) {
                                Text(
                                    text = "Уроков: ${coItems.size}",
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                                Text(
                                    text = coItems.map { item -> model.subjects.first { it.id ==  model.groups.first { it.id == item.groupId }.subjectId}.name }.toSet().toString()
                                        .removePrefix("[")
                                        .removeSuffix("]"),
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontSize = 12.sp,
                                    modifier = Modifier.align(Alignment.BottomCenter)
                                )
                                Text(
                                    coItems.minByOrNull { it.t.start.toMinutes() }?.t?.start.toString(),
                                    modifier = Modifier.align(
                                        Alignment.TopStart
                                    )
                                        .padding(start = 5.dp),
                                    lineHeight = 13.sp,
                                    fontSize = 13.sp,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    coItems.maxByOrNull { it.t.end.toMinutes() }?.t?.end.toString(),
                                    modifier = Modifier.align(
                                        Alignment.TopEnd
                                    )
                                        .padding(end = 5.dp),
                                    lineHeight = 13.sp,
                                    fontSize = 13.sp,
                                    textAlign = TextAlign.Center
                                )
                                if (model.eiIndex in coItems.map { trueItems.indexOf(it) } && model.eiFormId == formId) {
                                    mpChoseDesktopContent(
                                        component = component.mpEditItem,
                                        offset = DpOffset(
                                            x = 130.dp,
                                            y = (-35).dp
                                        )
                                    ) {
                                        Row {
                                            coItems.forEachIndexed { index, item ->
                                                Column {
                                                    Box(Modifier.width(200.dp).height(60.dp)) {
                                                        ScheduleForFormsContent(
                                                            e = item,
                                                            model = model
                                                        )
                                                    }
                                                    val kids = model.students.filter { it.login in form.logins }
                                                        .filter { item.groupId in it.groups.map { it.first } }
                                                    Column(Modifier.padding(horizontal = 10.dp, vertical = 5.dp)) {
                                                        Text("В этой группе:")
                                                        if (kids.isNotEmpty()) {
                                                            kids.forEach {
                                                                Text("${it.fio.surname} ${it.fio.name.first()}. ${(it.fio.praname ?: " ").first()}.")
                                                            }
                                                        }

                                            }
                                                }
                                                if (index != coItems.lastIndex) {
                                                    Spacer(Modifier.width(15.dp))
                                                }
                                            }
                                        }
//                                        val kids = model.students.filter { it.login in form.logins }
//                                            .filter { e.groupId in it.groups.map { it.first } }
//                                        Column(Modifier.padding(horizontal = 10.dp, vertical = 5.dp)) {
//                                            Text("В этой группе:")
//                                            if (kids.isNotEmpty()) {
//                                                kids.forEach {
//                                                    Text("${it.fio.surname} ${it.fio.name.first()}. ${(it.fio.praname ?: " ").first()}.")
//                                                }
//                                            }
//                                        }
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

@Composable
private fun BoxScope.ScheduleForFormsContent(e: ScheduleItem, model: ScheduleStore.State) {
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
        e.t.start.toString(),
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
    Text(
        text = model.teachers.first { it.login == e.teacherLogin }.fio.surname,
        modifier = Modifier.align(
            Alignment.TopStart
        ).padding(start = 5.dp),
        lineHeight = 13.sp,
        fontSize = 13.sp,
        textAlign = TextAlign.Center
    )
}