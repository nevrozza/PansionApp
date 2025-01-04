
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import calendar.CalendarComponent
import calendar.CalendarStore
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.*
import components.networkInterface.NetworkState
import dev.chrisbanes.haze.HazeState
import kotlinx.datetime.*
import resources.RIcons
import server.twoNums
import view.esp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarContent(
    component: CalendarComponent
) {
    val model by component.model.subscribeAsState()
    val nModel by component.nInterface.networkModel.subscribeAsState()

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
                        GetAsyncIcon(
                            path = RIcons.ChevronLeft
                        )
                    }
                },
                title = {
                    Text(
                        "Календарь",
                        fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                hazeState = hazeState
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
                            GetAsyncIcon(
                                RIcons.Save
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
                "Error" -> DefaultErrorView(
                    nModel,
                    DefaultErrorViewPos.CenteredFull
                )
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
                            fontSize = 18.esp,
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
                            fontSize = 18.esp,
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
                GetAsyncIcon(
                    path = RIcons.Add,
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
                                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                lineHeight = MaterialTheme.typography.titleMedium.fontSize,
                                fontWeight = FontWeight.Bold
                            )
                            Text(text = "модуль", fontSize = MaterialTheme.typography.titleSmall.fontSize, lineHeight = MaterialTheme.typography.titleSmall.fontSize)
                            Text(
                                text = startDate,
                                fontSize = MaterialTheme.typography.titleSmall.fontSize,
                                fontStyle = FontStyle.Italic,
                                lineHeight = MaterialTheme.typography.titleSmall.fontSize
                            )
//Day Of Week?
                        }
                        if (isCanBeDeleted) {
                            IconButton(
                                onClick = { isGoToDelete.value = true },
                                modifier = Modifier.align(Alignment.TopEnd)
                                    .padding(top = 2.dp, end = 2.dp)
                                    .size(35.dp)
                            ) {
                                GetAsyncIcon(
                                    RIcons.TrashCanRegular,
                                    size = 20.dp,
                                    modifier = Modifier.align(Alignment.Center)
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
                                GetAsyncIcon(
                                    RIcons.Close
                                )
                            }
                            IconButton(
                                onClick = { component.onEvent(CalendarStore.Intent.DeleteModule) }
                            ) {
                                GetAsyncIcon(RIcons.Check)
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