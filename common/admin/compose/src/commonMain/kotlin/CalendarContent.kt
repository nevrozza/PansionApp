import admin.calendar.Holiday
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import components.networkInterface.isLoading
import kotlinx.datetime.*
import resources.RIcons
import server.getEdYear
import server.to10
import utils.getCalendarLocale
import androidx.compose.desktop.ui.tooling.preview.utils.esp
import components.foundation.AppBar
import components.foundation.CLazyColumn
import components.foundation.CCheckbox
import components.foundation.CTextButton
import components.foundation.DefaultErrorView
import components.foundation.DefaultErrorViewPos
import components.foundation.TonalCard
import components.foundation.cClickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarContent(
    component: CalendarComponent
) {


    val model by component.model.subscribeAsState()
    val nModel by component.nInterface.networkModel.subscribeAsState()


    LaunchedEffect(Unit) {
        if (!nModel.isLoading) component.onEvent(CalendarStore.Intent.Init)
    }

    var datePickerState = rememberDatePickerState()
    var dateRangePickerState = rememberDateRangePickerState()

    val weeksLazyState = rememberLazyListState()
    val weeks = getWeeks(
        holidays = model.holidays.filter { it.isForAll == true },
        edYear = model.edYear,
        isWhole = true
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AppBar(
                title = {
                    val edYearNum = model.edYear % 100
                    Text(
                        "Календарь",
                        fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    CTextButton(
                        text = " ${edYearNum}/${edYearNum + 1}",
                        fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                        fontWeight = FontWeight.Black,
                    ) {}
                },
                navigationRow = {
                    IconButton(
                        onClick = { component.onOutput(CalendarComponent.Output.Back) }
                    ) {
                        GetAsyncIcon(
                            path = RIcons.CHEVRON_LEFT
                        )
                    }
                }
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
                                RIcons.SAVE
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

                else -> CLazyColumn(padding = padding) {
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
                                    num = m.num,
                                    edYear = model.edYear
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
                                    ModuleButton(component, null, null, false, edYear = model.edYear) {
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
                    if (model.modules.filter { it.halfNum == 1 }.isNotEmpty()) {
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
                                        isCanBeDeleted = model.modules.size == m.num,
                                        edYear = model.edYear
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
                                    if (model.modules.size < 9) {
                                        ModuleButton(component, null, null, false, edYear = model.edYear) {
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
                    item {
                        Text(
                            text = "Каникулы",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            fontSize = 18.esp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(5.dp))
                        LazyRow {
                            val holidays = model.holidays.sortedBy { it.id }
                            items(holidays.filter { model.edYear == it.edYear }) { h ->
                                RangeButton(
                                    component,
                                    h = h,
                                    isCreatingButton = false
                                ) {
                                    dateRangePickerState = it
                                    component.onEvent(
                                        CalendarStore.Intent.OpenRangePicker(
                                            selectedHolidayId = h.id
                                        )
                                    )
                                }
                            }
                            item {
                                RangeButton(
                                    component,
                                    h = Holiday(
                                        id = (holidays.lastOrNull()?.id ?: 0) + 1, edYear = model.edYear,
                                        start = "01.01.${model.edYear}", end = "01.01.${model.edYear}",
                                        isForAll = true
                                    ),
                                    isCreatingButton = true
                                ) {
                                    dateRangePickerState = it
                                    component.onEvent(
                                        CalendarStore.Intent.OpenRangePicker(
                                            selectedHolidayId = (holidays.lastOrNull()?.id ?: 0) + 1
                                        )
                                    )
                                }

                            }
                        }
                    }
                    item {
                        Text(
                            text = "Недели",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            fontSize = 18.esp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(5.dp))
                        LazyRow(state = weeksLazyState) {

                            items(weeks, key = { it.num }) { w ->
                                WeekItem(
                                    w,
                                    currentDate = model.today
                                )
                            }
                        }

                        LaunchedEffect(Unit) {
                            weeksLazyState.animateScrollToItem(
                                weeks.indexOfFirst { model.today in it.dates }.coerceAtLeast(0)
                            )
                        }
                    }
                }

            }
        }




        if (model.isCalendarShowing) {
            val isRangePicker = model.selectedHolidayId != null
            val isReady = (datePickerState.selectedDateMillis != null && !isRangePicker) ||
                    (
                            dateRangePickerState.selectedStartDateMillis != null && dateRangePickerState.selectedEndDateMillis != null && isRangePicker
                                    && dateRangePickerState.selectedStartDateMillis != dateRangePickerState.selectedEndDateMillis
                            )

            val isForAll = remember {
                mutableStateOf(
                    model.holidays.firstOrNull { it.id == model.selectedHolidayId }?.isForAll ?: true
                )
            }

            DatePickerDialog(
                onDismissRequest = {
                    component.onEvent(
                        CalendarStore.Intent.CloseCalendar
                    )
                },
                confirmButton = {
                    CTextButton(
                        "Ок",
                        modifier = Modifier.padding(
                            end = 30.dp,
                            start = 20.dp,
                            bottom = 10.dp
                        ),
                        color = if (isReady) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(
                            alpha = 0.3f
                        )
                    ) {
                        if (isReady) {
                            if (!isRangePicker) {
                                val date =
                                    Instant.fromEpochMilliseconds(
                                        datePickerState.selectedDateMillis!!
                                    ).toLocalDateTime(applicationTimeZone)
                                component.onEvent(
                                    CalendarStore.Intent.CreateModule(
                                        date.to10()
                                    )
                                )
                                component.onEvent(
                                    CalendarStore.Intent.CloseCalendar
                                )
                            } else {
                                val startDate = Instant.fromEpochMilliseconds(
                                    dateRangePickerState.selectedStartDateMillis!!
                                ).toLocalDateTime(applicationTimeZone)
                                val endDate = Instant.fromEpochMilliseconds(
                                    dateRangePickerState.selectedEndDateMillis!!
                                ).toLocalDateTime(applicationTimeZone)
                                println("wtf")
                                component.onEvent(
                                    CalendarStore.Intent.CreateHoliday(
                                        start = startDate.to10(),
                                        end = endDate.to10(),
                                        isForAll = isForAll.value
                                    )
                                )
                                component.onEvent(
                                    CalendarStore.Intent.CloseCalendar
                                )
                            }
                        }
                    }
                },
                dismissButton = {
                    if (isRangePicker) {
                        Row(
                            Modifier.padding(bottom = 10.dp, end = 5.dp)
                                .cClickable { isForAll.value = !isForAll.value }) {
                            CCheckbox(
                                checked = isForAll.value
                            )
                            Text("Для всех?")
                        }
                    }
                    CTextButton(
                        "Отмена",
                        modifier = Modifier.padding(bottom = 10.dp)
                    ) {
                        component.onEvent(
                            CalendarStore.Intent.CloseCalendar
                        )
                    }
                }
            ) {
                if (isRangePicker) {
                    DateRangePicker(
                        state = dateRangePickerState,
                        title = {
                            Text(
                                "Выберите период каникул",
                                modifier = Modifier.padding(
                                    top = 15.dp,
                                    start = 20.dp
                                )
                            )
                        },
                        headline = {
                            val start =
                                if (dateRangePickerState.selectedStartDateMillis != null) Instant.fromEpochMilliseconds(
                                    dateRangePickerState.selectedStartDateMillis!!
                                ).toLocalDateTime(
                                    applicationTimeZone
                                ).to10() else "?"
                            val end =
                                if (dateRangePickerState.selectedEndDateMillis != null) Instant.fromEpochMilliseconds(
                                    dateRangePickerState.selectedEndDateMillis!!
                                ).toLocalDateTime(
                                    applicationTimeZone
                                ).to10() else "?"
                            Text("$start-$end")
                        }
                    )
                } else {
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
        }

        SaveAnimation(model.isSavedAnimation) {
            component.onEvent(CalendarStore.Intent.IsSavedAnimation(false))
        }


    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WeekItem(
    w: Week,
    currentDate: String
) {
    // Not TonalCard!
    ElevatedCard(
        modifier = Modifier.width(160.dp).height(110.dp).padding(end = 10.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (currentDate in w.dates) MaterialTheme.colorScheme.secondaryContainer
            else MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = w.dates.first().toString(),
                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                lineHeight = MaterialTheme.typography.titleMedium.fontSize,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = w.dates.last().toString(),
                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                lineHeight = MaterialTheme.typography.titleMedium.fontSize,
                fontWeight = FontWeight.Bold
            )
            Row {
                Text("${w.num} неделя")
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RangeButton(
    component: CalendarComponent,
    h: Holiday,
    isCreatingButton: Boolean,
    onClick: (DateRangePickerState) -> Unit
) {
    val isGoToDelete = remember { mutableStateOf(false) }
    TonalCard(
        modifier = Modifier.width(160.dp).height(110.dp).padding(end = 10.dp),
        onClick = {
            val today = Clock.System.now().toLocalDateTime(applicationTimeZone)
            if (!isGoToDelete.value && (isCreatingButton || h.edYear == getEdYear(today.date))) {
                val dateRangePickerState = DateRangePickerState(
                    initialSelectedStartDateMillis =
                        if (h.start.length == 10) {
                            val s = h.start.split(".")
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
                    initialSelectedEndDateMillis =
                        if (h.end.length == 10) {
                            val s = h.end.split(".")
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
                    yearRange = IntRange(h.edYear, h.edYear + 1),
                    selectableDates = getSelectableDates(
                        prevDate = component.model.value.holidays.firstOrNull { it.id == h.id - 1 }?.end
                            ?: "01.01.2000",
                        edYear = h.edYear
                    ),
                    locale = getCalendarLocale()
                )
                onClick(
                    dateRangePickerState
                )
            }
        }
    ) {
        if (isCreatingButton) {
            Box(Modifier.fillMaxSize()) {
                GetAsyncIcon(
                    path = RIcons.ADD,
                    modifier = Modifier.align(Alignment.Center)
                )
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
                                text = h.start,
                                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                lineHeight = MaterialTheme.typography.titleMedium.fontSize,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = h.end,
                                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                lineHeight = MaterialTheme.typography.titleMedium.fontSize,
                                fontWeight = FontWeight.Bold
                            )
                            Row {
                                Text(if (h.isForAll) "Для всех" else "Не для всех")
                            }
//Day Of Week?
                        }
                        if (h.id == component.model.value.holidays.maxBy { it.id }.id) {
                            IconButton(
                                onClick = { isGoToDelete.value = true },
                                modifier = Modifier.align(Alignment.TopEnd)
                                    .padding(top = 2.dp, end = 2.dp)
                                    .size(35.dp)
                            ) {
                                GetAsyncIcon(
                                    RIcons.TRASH_CAN_REGULAR,
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
                        Text("Удалить эти каникулы?", textAlign = TextAlign.Center)
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            IconButton(
                                onClick = { isGoToDelete.value = false }
                            ) {
                                GetAsyncIcon(
                                    RIcons.CLOSE
                                )
                            }
                            IconButton(
                                onClick = { component.onEvent(CalendarStore.Intent.DeleteHoliday(h.id)) }
                            ) {
                                GetAsyncIcon(RIcons.CHECK)
                            }
                        }
                    }
                }
            }
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
    edYear: Int,
    onClick: (DatePickerState) -> Unit
) {
    val isGoToDelete = remember { mutableStateOf(false) }
    TonalCard(
        modifier = Modifier.width(160.dp).height(110.dp).padding(end = 10.dp),
        onClick = {
            val today = Clock.System.now().toLocalDateTime(applicationTimeZone)
            if (!isGoToDelete.value && edYear == getEdYear(today.date)) {
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
                    yearRange = IntRange(edYear, edYear + 1),
                    selectableDates = getSelectableDates(
                        prevDate = if (num == null) {
                            component.model.value.modules.lastOrNull()?.start ?: "01.01.2000"
                        } else {
                            component.model.value.modules.firstOrNull { it.num == num - 1 }?.start
                                ?: "01.01.2000"
                        },
                        edYear = edYear
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
                    path = RIcons.ADD,
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
                            Text(
                                text = "модуль",
                                fontSize = MaterialTheme.typography.titleSmall.fontSize,
                                lineHeight = MaterialTheme.typography.titleSmall.fontSize
                            )
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
                                    RIcons.TRASH_CAN_REGULAR,
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
                                    RIcons.CLOSE
                                )
                            }
                            IconButton(
                                onClick = { component.onEvent(CalendarStore.Intent.DeleteModule) }
                            ) {
                                GetAsyncIcon(RIcons.CHECK)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
private fun getSelectableDates(prevDate: String, edYear: Int): SelectableDates {
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
            return year >= edYear//Clock.System.now().toLocalDateTime(TimeZone.of("UTC+3")).year
        }
    }
}