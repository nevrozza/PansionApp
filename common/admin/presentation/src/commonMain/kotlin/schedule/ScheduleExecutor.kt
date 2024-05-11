package schedule

import AdminRepository
import CDispatcher
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import components.listDialog.ListComponent
import components.listDialog.ListDialogStore
import components.listDialog.ListItem
import components.mpChose.mpChoseComponent
import components.mpChose.mpChoseStore
import components.networkInterface.NetworkInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import schedule.ScheduleStore.Intent
import schedule.ScheduleStore.Label
import schedule.ScheduleStore.State
import schedule.ScheduleStore.Message
import server.toMinutes

class ScheduleExecutor(
    private val adminRepository: AdminRepository,
    private val nInterface: NetworkInterface,
    private val mpCreateItem: mpChoseComponent,
    private val mpEditItem: mpChoseComponent,
    private val listCreateTeacher: ListComponent
) : CoroutineExecutor<Intent, Unit, State, Message, Label>() {
    override fun executeIntent(intent: Intent) {
        when (intent) {
            Intent.Init -> init()
            is Intent.ciChooseGroup -> chooseGroup(intent.groupId)
            is Intent.ciChooseTime -> scope.launch(CDispatcher) {
                val timing = getScheduleTiming(Pair(intent.t.start, intent.t.end))
                scope.launch {
                    dispatch(
                        Message.ciTimeChosed(
                            timing
                        )
                    )
                }
            }

            Intent.ciCreate -> createItem()

            Intent.ciPreview -> scope.launch { dispatch(Message.ciPreviewed) }

            is Intent.ciStart -> scope.launch {
                if (intent.login != state().ciLogin) dispatch(Message.ciReset)
                val cabinet =
                    state().cabinets.firstOrNull { it.login == intent.login }?.cabinet ?: 0
                dispatch(Message.ciStarted(intent.login, cabinet))
            }

            Intent.ciNullGroupId -> scope.launch {
                dispatch(Message.ciGroupIdNulled)
            }

            Intent.ciFalsePreview -> scope.launch {
                dispatch(Message.ciPreviewFalsed)
            }

            Intent.UpdateCTeacherList -> updateCreateTeacherList()
            is Intent.CreateTeacher -> scope.launch(CDispatcher) {
                val newList =
                    (state().activeTeachers.firstOrNull { it.first == if (state().isDefault) state().defaultDate.toString() else state().currentDate.second }?.second
                        ?: emptyList()).toMutableList()
                newList.add(
                    intent.login
                )
                scope.launch {
                    dispatch(Message.TeacherCreated(newList))
                    updateCreateTeacherList()
                }
            }

            is Intent.ciChangeCabinet -> scope.launch {
                dispatch(Message.ciCabinetChanged(intent.cabinet))

                if (intent.cabinet.toString().length == 3) {
                    updateTimePeekList()
                }
            }

            is Intent.eiChangeCabinet -> scope.launch {
                dispatch(Message.eiCabinetChanged(intent.cabinet))
            }

            is Intent.StartEdit -> scope.launch {
                mpEditItem.onEvent(mpChoseStore.Intent.ShowDialog)
                dispatch(Message.EditStarted(index = intent.index))
            }

            is Intent.eiChooseGroup -> scope.launch {
                dispatch(Message.eiGroupChosed(intent.groupId))
            }

            is Intent.eiChangeState -> scope.launch {
                dispatch(Message.eiStateChanged(intent.state))
            }

            is Intent.eiChangeTiming -> scope.launch {
                dispatch(Message.eiTimingChanged(intent.timing))
            }

            is Intent.eiCheck -> updateEditErrors(
                cabinet = intent.cabinet,
                login = intent.login,
                id = intent.id,
                s = intent.s
            )

            is Intent.eiSave -> {
                scope.launch(CDispatcher) {
                    val newList =
                        state().items.first { it.first == if (state().isDefault) state().defaultDate.toString() else state().currentDate.second }.second.toMutableList()
                    newList.removeAt(intent.index)
                    newList.add(
                        intent.index,
                        ScheduleItem(
                            teacherLogin = intent.login,
                            groupId = intent.id,
                            t = ScheduleTiming(start = intent.s.first, end = intent.s.second),
                            cabinet = intent.cabinet
                        )
                    )
                    scope.launch {
                        dispatch(Message.ItemsUpdated(newList))
                        mpEditItem.onEvent(mpChoseStore.Intent.HideDialog)
                    }
                }
            }

            is Intent.eiDelete -> {
                scope.launch(CDispatcher) {
                    val newItemsIn =
                        state().items.first { it.first == if (state().isDefault) state().defaultDate.toString() else state().currentDate.second }.second.toMutableList()
                    newItemsIn.removeAt(intent.index)
                    scope.launch {
                        dispatch(Message.ItemsUpdated(newItemsIn))
                        mpEditItem.onEvent(mpChoseStore.Intent.HideDialog)
                    }
                }
            }

            Intent.ciChangeIsPair -> scope.launch {
                dispatch(Message.ciIsPairChanged)
            }

            Intent.ChangeEditMode -> scope.launch {
                dispatch(Message.EditModeChanged)
                fetchItems(
                    state().currentDate.first,
                    if (state().isDefault) state().defaultDate.toString() else state().currentDate.second
                )
//                updateCreateTeacherList()
            }

            is Intent.ChangeDefaultDate -> scope.launch {
                dispatch(Message.DefaultDateChanged(intent.date))
                fetchItems(intent.date, intent.date.toString())
//                updateCreateTeacherList()
            }

            is Intent.ChangeCurrentDate -> scope.launch {
                dispatch(Message.CurrentDateChanged(intent.date))
                fetchItems(intent.date.first, intent.date.second)
//                updateCreateTeacherList()
            }

            Intent.SaveSchedule -> saveItems()
        }
    }

    private fun saveItems() {
        scope.launch(CDispatcher) {
            try {
                nInterface.nStartLoading()
                adminRepository.saveSchedule(
                    RScheduleList(
                        list = state().items
                    )
                )
                nInterface.nSuccess()
            } catch (_: Throwable) {
                nInterface.nError("Не удалось сохранить") {
                    saveItems()
                }
            }
        }
    }

    private fun fetchItems(dayOfWeek: Int, date: String) {
        scope.launch {
            dispatch(Message.ciReset)
        }
        scope.launch(CDispatcher) {
            if ((state().items.find { it.first == date }?.second ?: listOf()).isEmpty()) {
                val commonList : MutableList<Pair<String, List<String>>> = state().activeTeachers.toMutableList()
                try {
                    nInterface.nStartLoading()
                    val items = adminRepository.fetchSchedule(
                        dayOfWeek = dayOfWeek.toString(),
                        date = date
                    ).list
                    items.forEach {
                        it.second.forEach { item ->
                            val teacher =
                                state().teachers.firstOrNull { item.groupId in it.groups }?.login
                            if(teacher != null) {
                                val list =
                                    (commonList.firstOrNull { it.first == date }?.second)
                                        ?: emptyList()

                                if (teacher !in list) {
                                    val newList = list.toMutableList()
                                    newList.add(teacher)


                                    val old = (commonList.firstOrNull { it.first == date }?.second
                                        ?: emptyList())
                                    commonList.removeAll { it.first == date }
                                    commonList.add(Pair(date, old + teacher))
                                    print("sad ")
                                    println(commonList)
                                }
                            }
                        }
                    }
                    scope.launch {
                        scope.launch {
                            dispatch(
                                Message.TeacherListUpdated(
                                    activeTeachers = commonList
                                )
                            )

                        }
                        dispatch(Message.ListUpdated(state().items + items))
                        nInterface.nSuccess()
                        updateCreateTeacherList()
                    }

                } catch (e: Throwable) {
                    println(e)
                    nInterface.nError("Не удалось загрузить расписание") {
                        fetchItems(dayOfWeek, date)
                        updateCreateTeacherList(true)
                    }
                }
            } else {
                scope.launch {
                    nInterface.nSuccess()
                    updateCreateTeacherList()
                }
            }
        }
    }

    private fun updateCreateTeacherList(isError: Boolean = false) {
        scope.launch(CDispatcher) {
            if (!isError) {
                val activeTeachers =
                    state().activeTeachers.firstOrNull { it.first == if (state().isDefault) state().defaultDate.toString() else state().currentDate.second }
                val newList =
                    state().teachers.filter { if (activeTeachers != null) it.login !in activeTeachers.second else true }
                        .map {
                            ListItem(
                                id = it.login,
                                text = "${it.fio.surname} ${it.fio.name} ${it.fio.praname}"
                            )
                        }
                scope.launch {
                    listCreateTeacher.onEvent(ListDialogStore.Intent.InitList(newList))
                }
            } else {
                scope.launch {
                    listCreateTeacher.onEvent(ListDialogStore.Intent.InitList(listOf()))
                }
            }
        }
    }

    private fun createItem() {
        val item = ScheduleItem(
            teacherLogin = state().ciLogin!!,
            groupId = state().ciId!!,
            t = state().ciTiming!!,
            cabinet = state().ciCabinet
        )
        scope.launch(CDispatcher) {
            val items =
                (state().items.firstOrNull { it.first == if (state().isDefault) state().defaultDate.toString() else state().currentDate.second }?.second
                    ?: emptyList()).toMutableList()


            items.add(
                item
            )
            scope.launch {
                dispatch(Message.ItemsUpdated(items))
            }
        }
        scope.launch {
            dispatch(Message.ciReset)
        }
    }

    private fun chooseGroup(groupId: Int) {
        scope.launch {
            dispatch(Message.ciGroupChosed(groupId))
            updateTimePeekList()
        }
    }

    private fun updateTimePeekList() {
        scope.launch(CDispatcher) {
            val timings: MutableList<ScheduleTiming> = mutableListOf()

            timingsPairs.forEach { s ->
                timings.add(getScheduleTiming(s))
            }

            scope.launch {
                dispatch(Message.ciTimingsGot(timings))
            }
        }
    }


    private fun updateEditErrors(
        cabinet: Int,
        login: String,
        id: Int,
        s: Pair<String, String>
    ) {
        scope.launch(CDispatcher) {
            val trueItems =
                state().items.firstOrNull { it.first == if (state().isDefault) state().defaultDate.toString() else state().currentDate.second }?.second
                    ?: emptyList()
            val coItems =
                (trueItems - trueItems[state().eiIndex!!]).filter {
                    // ! (закончилось раньше чем началось наше) или (началось позже чем началось наше)
                    ((!((it.t.end.toMinutes() < s.first.toMinutes() ||
                            it.t.start.toMinutes() > s.second.toMinutes())) && it.groupId != -11) ||
                            (!((it.t.end.toMinutes() <= s.first.toMinutes() ||
                                    it.t.start.toMinutes() >= s.second.toMinutes())) && it.groupId == -11))
                }
            val students = state().students.filter { id in it.groups }
            val cabinetError = coItems.firstOrNull {
                if (it.teacherLogin == login) {
                    true
                } else {
                    if (it.cabinet == 0 && it.teacherLogin != login) {
                        false
                    } else {
                        it.cabinet == cabinet
                    }
                }
            }?.groupId ?: 0
            val studentErrors: MutableList<StudentError> = mutableListOf()
            val studentErrorItems =
                coItems.filter { it.groupId in students.flatMap { student -> student.groups } }

            studentErrorItems.forEach { item ->
                val error = StudentError(
                    groupId = item.groupId,
                    logins = students.filter { item.groupId in it.groups }.map { it.login }
                )
                try {
                    studentErrors.remove(error)
                } catch (_: Throwable) {
                }
                studentErrors.add(
                    error
                )
            }
            scope.launch {
                dispatch(
                    Message.eiErrorsUpdated(
                        cabinetErrorGroupId = cabinetError,
                        studentErrors = studentErrors
                    )
                )
            }
        }
    }

    private fun getScheduleTiming(s: Pair<String, String>): ScheduleTiming {
        val trueItems =
            state().items.firstOrNull { it.first == if (state().isDefault) state().defaultDate.toString() else state().currentDate.second }?.second
                ?: emptyList()
        val coItems =
            trueItems.filter {
                // ! (закончилось раньше чем началось наше) или (началось позже чем началось наше)
                ((!((it.t.end.toMinutes() < s.first.toMinutes() ||
                        it.t.start.toMinutes() > s.second.toMinutes())) && it.groupId != -11) ||
                        (!((it.t.end.toMinutes() <= s.first.toMinutes() ||
                                it.t.start.toMinutes() >= s.second.toMinutes())) && it.groupId == -11))
            }
        val students = state().students.filter { state().ciId in it.groups }
        val cabinetError = coItems.firstOrNull {
            if (it.teacherLogin == state().ciLogin) {
                true
            } else {
                if (it.cabinet == 0 && it.teacherLogin != state().ciLogin) {
                    false
                } else {
                    it.cabinet == state().ciCabinet
                }
            }
        }?.groupId ?: 0
        val studentErrors: MutableList<StudentError> = mutableListOf()
        val studentErrorItems =
            coItems.filter { it.groupId in students.flatMap { student -> student.groups } }

        studentErrorItems.forEach { item ->
            val error = StudentError(
                groupId = item.groupId,
                logins = students.filter { item.groupId in it.groups }.map { it.login }
            )
            try {
                studentErrors.remove(error)
            } catch (_: Throwable) {
            }
            studentErrors.add(
                error
            )
        }

        return ScheduleTiming(
            start = s.first,
            end = s.second,
            cabinetErrorGroupId = cabinetError,
            studentErrors = studentErrors
        )

    }

    private fun init() {
        scope.launch(CDispatcher) {
            nInterface.nStartLoading()
            try {
//                val ar = async { adminRepository.fetchInitSchedule() }
//                val acR = async { adminRepository.fetchCabinets() }
                val r = adminRepository.fetchInitSchedule()//ar.await()
                val cabinets = adminRepository.fetchCabinets().cabinets//acR.await().cabinets
                scope.launch {
                    dispatch(
                        Message.Inited(
                            teachers = r.teachers,
                            students = r.students,
                            subjects = r.subjects,
                            groups = r.groups,
                            cabinets = cabinets
                        )
                    )
                    nInterface.nSuccess()

                    fetchItems(
                        state().currentDate.first,
                        state().currentDate.second
                    )
                }
            } catch (e: Throwable) {

                nInterface.nError(
                    "Что-то пошло не так",
                ) {
                    init()
                }

            }

        }
    }

}


val timingsPairs = listOf( //<Pair<String, String>>
    Pair("09:00", "09:40"),
    Pair("09:45", "10:25"),
    Pair("10:35", "11:15"),
    Pair("11:20", "12:00"),
    Pair("12:20", "13:00"),
    Pair("13:05", "13:45"),
    Pair("13:50", "14:30"),
    Pair("14:35", "15:15"),
    Pair("15:20", "16:00"),
    Pair("16:20", "17:00")
)
