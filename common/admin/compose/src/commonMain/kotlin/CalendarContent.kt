
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import calendar.CalendarComponent
import calendar.CalendarStore
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.AppBar
import components.CLazyColumn
import components.CustomTextButton
import components.SaveAnimation
import components.networkInterface.NetworkState
import dev.chrisbanes.haze.HazeState
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import server.twoNums
import view.LocalViewManager
import view.rememberImeState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarContent(
    component: CalendarComponent,
    isVisible: Boolean
) {
    val model by component.model.subscribeAsState()
    val nModel by component.nInterface.networkModel.subscribeAsState()
    val viewManager = LocalViewManager.current
    val scrollState = rememberScrollState()
    val imeState = rememberImeState()
    val lazyListState = rememberLazyListState()
    val density = LocalDensity.current
    val hazeState = remember { HazeState() }
    var datePickerState = rememberDatePickerState()


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AppBar(
                navigationRow = {
                    IconButton(
                        onClick = { component.onOutput(CalendarComponent.Output.Back) }
                    ) {
                        Icon(
                            Icons.Rounded.ArrowBackIosNew, null
                        )
                    }
                },
                title = {
                    Text(
                        "Календарь",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                hazeState = hazeState,
                isHazeActivated = isVisible
            )
        },
        floatingActionButton = {
            Crossfade(nModel.state) {
                SmallFloatingActionButton(
                    onClick = {
                        if (it != NetworkState.Loading) {
                            component.onEvent(CalendarStore.Intent.SendItToServer)
                        }
                    }
                ) {
                    when (it) {
                        NetworkState.None -> {
                            Icon(
                                Icons.Rounded.Save,
                                null
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
        val state = when {
            nModel.state == NetworkState.Error -> "Error"
            nModel.state == NetworkState.Loading && model.modules.isEmpty() -> "Loading"
            else -> "None"
        }
        Crossfade(state) {
            when (it) {
                "Error" -> {
                    Column(
                        Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(nModel.error)
                        Spacer(Modifier.height(7.dp))
                        CustomTextButton("Попробовать ещё раз") {
                            nModel.onFixErrorClick()
                        }
                    }
                }
                "Loading" -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
               else -> CLazyColumn(padding = padding, hazeState = hazeState) {
                    item {
                        Text(
                            text = "1 полугодие",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(5.dp))
                        LazyRow {
                            items(model.modules.filter { it.halfNum == 1 }) { m ->
                                ModuleButton(
                                    component,
                                    startDate = m.start,
                                    isCanBeDeleted = model.modules.size == m.num,
                                    num = m.num
                                ) {
                                    datePickerState = it
                                    component.onEvent(
                                        CalendarStore.Intent.OpenCalendar(
                                            creatingHalfNum = 1,
                                            selectedModuleNum = m.num
                                        )
                                    )
                                }
                            }
                            item {
                                if (model.modules.filter { it.halfNum == 2 }.isEmpty() && model.modules.size < 9) {
                                    ModuleButton(component, null, null, false) {
                                        datePickerState = it
                                        component.onEvent(
                                            CalendarStore.Intent.OpenCalendar(
                                                creatingHalfNum = 1,
                                                selectedModuleNum = model.modules.size + 1
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                    item {
                        Text(
                            text = "2 полугодие",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(5.dp))
                        LazyRow {
                            items(model.modules.filter { it.halfNum == 2 }) { m ->
                                ModuleButton(
                                    component,
                                    startDate = m.start,
                                    num = m.num,
                                    isCanBeDeleted = model.modules.size == m.num
                                ) {
                                    datePickerState = it
                                    component.onEvent(
                                        CalendarStore.Intent.OpenCalendar(
                                            creatingHalfNum = 2,
                                            selectedModuleNum = m.num
                                        )
                                    )
                                }
                            }
                            item {
                                if (model.modules.filter { it.halfNum == 1 }.isNotEmpty() && model.modules.size < 9) {
                                    ModuleButton(component, null, null, false) {
                                        datePickerState = it
                                        component.onEvent(
                                            CalendarStore.Intent.OpenCalendar(
                                                creatingHalfNum = 2,
                                                selectedModuleNum = model.modules.size + 1
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }
        if (model.isCalendarShowing) {
            DatePickerDialog(
                onDismissRequest = {
                    component.onEvent(
                        CalendarStore.Intent.CloseCalendar
                    )
                },
                confirmButton = {
                    CustomTextButton(
                        "Ок",
                        modifier = Modifier.padding(
                            end = 30.dp,
                            start = 20.dp,
                            bottom = 10.dp
                        ),
                        color = if (datePickerState.selectedDateMillis != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(
                            alpha = 0.3f
                        )
                    ) {
                        if (datePickerState.selectedDateMillis != null) {
                            val date =
                                Instant.fromEpochMilliseconds(
                                    datePickerState.selectedDateMillis!!
                                )
                                    .toLocalDateTime(TimeZone.of("UTC+3"))
                            component.onEvent(
                                CalendarStore.Intent.CreateModule(
                                    "${date.dayOfMonth.twoNums()}.${date.monthNumber.twoNums()}.${date.year}"
                                )
                            )
                            component.onEvent(
                                CalendarStore.Intent.CloseCalendar
                            )
                        }
                    }
                },
                dismissButton = {
                    CustomTextButton(
                        "Отмена",
                        modifier = Modifier.padding(bottom = 10.dp)
                    ) {
                        component.onEvent(
                            CalendarStore.Intent.CloseCalendar
                        )
                    }
                }
            ) {
                DatePicker(
                    state = datePickerState,
                    showModeToggle = true,
                    title = {
                        Text(
                            "Выберите день старта модуля",
                            modifier = Modifier.padding(
                                top = 15.dp,
                                start = 20.dp
                            )
                        )
                    }
                )
            }
        }

        SaveAnimation(model.isSavedAnimation) {
            component.onEvent(CalendarStore.Intent.IsSavedAnimation(false))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModuleButton(
    component: CalendarComponent,
    startDate: String?,
    num: Int?,
    isCanBeDeleted: Boolean,
    onClick: (DatePickerState) -> Unit
) {
    val isGoToDelete = remember { mutableStateOf(false) }
    ElevatedCard(
        modifier = Modifier.width(110.dp).height(90.dp).padding(end = 10.dp),
//        colors = CardDefaults.cardColors(
//            containerColor = MaterialTheme.colorScheme.secondaryContainer
//        )
        onClick = {
            if (!isGoToDelete.value) {
                val today = Clock.System.now().toLocalDateTime(TimeZone.of("UTC+3"))
                val datePickerState = DatePickerState(
                    initialSelectedDateMillis =
                    if (startDate != null && startDate.length == 10) {
                        val s = startDate.split(".")
                        val day = s[0].toInt()
                        val month = s[1].toInt()
                        val year = s[2].toInt()
                        LocalDate(
                            year = year,
                            monthNumber = month,
                            dayOfMonth = day
                        ).atStartOfDayIn(
                            TimeZone.UTC
                        ).toEpochMilliseconds()
                    } else null,
                    yearRange = IntRange(today.year, today.year + 1),
                    selectableDates = getSelectableDates(
                        prevDate = if (num == null) {
                            component.model.value.modules.lastOrNull()?.start ?: "01.01.2000"
                        } else {
                            component.model.value.modules.firstOrNull { it.num == num - 1 }?.start
                                ?: "01.01.2000"
                        }
                    ),
                    locale = getCalendarLocale()
                )
                onClick(
                    datePickerState
                )
            }
        }
    ) {
        if (startDate == null || num == null) {
            Box(Modifier.fillMaxSize()) {
                Icon(
                    Icons.Rounded.Add, null,
                    modifier = Modifier.align(Alignment.Center)
                )
//                Text(
//                    text = "Создать модуль",
//                    fontSize = 10.sp,
//                    lineHeight = 11.sp,
//                    modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).padding(bottom = 10.dp),
//                    textAlign = TextAlign.Center
//                )
            }
        } else {
            Crossfade(isGoToDelete.value) {
                if (!it) {
                    Box() {
                        Column(
                            Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = num.toString(),
                                fontSize = 16.sp,
                                lineHeight = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(text = "модуль", fontSize = 14.sp, lineHeight = 15.sp)
                            Text(
                                text = startDate,
                                fontSize = 14.sp,
                                fontStyle = FontStyle.Italic,
                                lineHeight = 15.sp
                            )
//Day Of Week?
                        }
                        if (isCanBeDeleted) {
                            IconButton(
                                onClick = { isGoToDelete.value = true },
                                modifier = Modifier.align(Alignment.TopEnd)
                                    .padding(top = 2.dp, end = 2.dp)
                                    .size(20.dp)
                            ) {
                                Icon(
                                    Icons.Rounded.DeleteOutline, null
                                )
                            }
                        }
                    }
                } else {
                    Column(
                        Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Удалить модуль ${num}?", textAlign = TextAlign.Center)
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            IconButton(
                                onClick = { isGoToDelete.value = false }
                            ) {
                                Icon(
                                    Icons.Rounded.Close, null
                                )
                            }
                            IconButton(
                                onClick = { component.onEvent(CalendarStore.Intent.DeleteModule) }
                            ) {
                                Icon(
                                    Icons.Rounded.Check, null
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
private fun getSelectableDates(prevDate: String): SelectableDates {
    return object : SelectableDates {
        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
            val s = prevDate.split(".")
            val day = s[0].toInt()
            val month = s[1].toInt()
            val year = s[2].toInt()
            val date = LocalDate(
                year = year,
                monthNumber = month,
                dayOfMonth = day
            ).atStartOfDayIn(
                TimeZone.UTC
            ).toEpochMilliseconds()
            return utcTimeMillis > date
        }

        override fun isSelectableYear(year: Int): Boolean {
            return year >= Clock.System.now().toLocalDateTime(TimeZone.of("UTC+3")).year
        }
    }
}