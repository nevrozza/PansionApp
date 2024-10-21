package schedule

import AdminRepository
import CDispatcher
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import components.listDialog.ListComponent
import components.listDialog.ListDialogStore
import components.listDialog.ListItem
import components.mpChose.MpChoseComponent
import components.mpChose.MpChoseStore
import components.networkInterface.NetworkInterface
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import schedule.ScheduleStore.Intent
import schedule.ScheduleStore.Label
import schedule.ScheduleStore.State
import schedule.ScheduleStore.Message
import server.toMinutes

class ScheduleExecutor(
    private val adminRepository: AdminRepository,
    private val nInterface: NetworkInterface,
    private val mpCreateItem: MpChoseComponent,
    private val mpEditItem: MpChoseComponent,
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
                val key =
                    if (state().isDefault) state().defaultDate.toString() else state().currentDate.second
                val new = state().activeTeachers.toMutableMap()
                new[key] =
                    (state().activeTeachers[key]?: emptyList()).plus(intent.login)
                scope.launch {
                    dispatch(Message.TeacherCreated(new.toMap(HashMap())))
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
                mpEditItem.onEvent(MpChoseStore.Intent.ShowDialog)
                dispatch(Message.EditStarted(index = intent.index, formId = intent.formId))
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
                    val key =
                        if (state().isDefault) state().defaultDate.toString() else state().currentDate.second
                    val newItems = (state().items[key] ?: emptyList()).toMutableList()
                    newItems[intent.index] = ScheduleItem(
                        teacherLogin = intent.login,
                        groupId = intent.id,
                        t = ScheduleTiming(
                            start = intent.s.first,
                            end = intent.s.second
                        ),
                        cabinet = intent.cabinet,
                        teacherLoginBefore = newItems[intent.index].teacherLoginBefore
                    )
                    scope.launch {
                        dispatch(
                            Message.ItemsUpdated(
                                newItems
                            )
                        )
                        mpEditItem.onEvent(MpChoseStore.Intent.HideDialog)
                    }
                }
            }

            is Intent.eiDelete -> {
                scope.launch(CDispatcher) {
                    val key = if (state().isDefault) state().defaultDate.toString() else state().currentDate.second
                    val list = (state().items[key] ?: emptyList()).toMutableList()
                    list.removeAt(intent.index)
                    scope.launch {
                        dispatch(Message.ItemsUpdated(list))
                        mpEditItem.onEvent(MpChoseStore.Intent.HideDialog)
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
            is Intent.IsSavedAnimation -> dispatch(Message.IsSavedAnimation(intent.isSavedAnimation))
            Intent.ChangeIsTeacherView -> dispatch(Message.ChangeIsTeacherView)
            is Intent.eiChangeLogin -> dispatch(Message.eiLoginChanged(intent.login))
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun saveItems() {
        GlobalScope.launch(CDispatcher) {
            try {
                nInterface.nStartLoading()
                adminRepository.saveSchedule(
                    RScheduleList(
                        list = state().items
                    )
                )
                scope.launch {
                    dispatch(Message.IsSavedAnimation(true))
                    nInterface.nSuccess()
                }
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
            if ((state().items[date] ?: listOf()).isEmpty()) {
                val commonList =
                    state().activeTeachers.toMutableMap()
                try {
                    nInterface.nStartLoading()
                    val items = adminRepository.fetchSchedule(
                        dayOfWeek = dayOfWeek.toString(),
                        date = date
                    ).list
                    items.forEach {
                        it.value.forEach { item ->
                            val teacher =
                                state().teachers.firstOrNull { item.groupId in it.groups.map { it.first } }?.login
                            if (teacher != null) {
                                val list =
                                    (commonList[date])
                                        ?: emptyList()

                                if (teacher !in list) {
                                    val newList = list.toMutableList()
                                    newList.add(teacher)

                                    val old = (commonList[date]
                                        ?: emptyList())
                                    commonList[date] = old + teacher
                                }
                            }
                        }
                    }
                    scope.launch {
                        scope.launch {
                            dispatch(
                                Message.TeacherListUpdated(
                                    activeTeachers = commonList.toMap(HashMap())
                                )
                            )

                        }
                        dispatch(Message.ListUpdated((state().items + items).toMap(HashMap())))
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
                val key = if (state().isDefault) state().defaultDate.toString() else state().currentDate.second
                val activeTeachers =
                    state().activeTeachers[key]
                val newList =
                    state().teachers.filter { if (activeTeachers != null) it.login !in activeTeachers else true }
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
            cabinet = state().ciCabinet,
            teacherLoginBefore = state().ciLogin!!
        )
        scope.launch(CDispatcher) {
            val key = if (state().isDefault) state().defaultDate.toString() else state().currentDate.second
            val items =
                (state().items[key]
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
            val key = if (state().isDefault) state().defaultDate.toString() else state().currentDate.second
            val trueItems =
                state().items[key]
                    ?: emptyList()
            val coItems =
                (trueItems - trueItems[state().eiIndex!!]).filter {
                    // ! (закончилось раньше чем началось наше) или (началось позже чем началось наше)
                    ((!((it.t.end.toMinutes() < s.first.toMinutes() ||
                            it.t.start.toMinutes() > s.second.toMinutes())) && it.groupId != -11) ||
                            (!((it.t.end.toMinutes() <= s.first.toMinutes() ||
                                    it.t.start.toMinutes() >= s.second.toMinutes())) && it.groupId == -11))
                }
            val students = state().students.filter { id in it.groups.map { it.first } }
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
                coItems.filter { it.groupId in students.flatMap { student -> student.groups.map { it.first } } }

            studentErrorItems.forEach { item ->
                val error = StudentError(
                    groupId = item.groupId,
                    logins = students.filter { item.groupId in it.groups.map { it.first } }.map { it.login }
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
        val key = if (state().isDefault) state().defaultDate.toString() else state().currentDate.second
        val trueItems =
            state().items[key]
                ?: emptyList()
        val coItems =
            trueItems.filter {
                // ! (закончилось раньше чем началось наше) или (началось позже чем началось наше)
                ((!((it.t.end.toMinutes() < s.first.toMinutes() ||
                        it.t.start.toMinutes() > s.second.toMinutes())) && it.groupId != -11) ||
                        (!((it.t.end.toMinutes() <= s.first.toMinutes() ||
                                it.t.start.toMinutes() >= s.second.toMinutes())) && it.groupId == -11))
            }
        val students = state().students.filter { state().ciId in it.groups.map { it.first } }
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
            coItems.filter { it.groupId in students.flatMap { student -> student.groups.map { it.first } } }

        studentErrorItems.forEach { item ->
            val error = StudentError(
                groupId = item.groupId,
                logins = students.filter { item.groupId in it.groups.map { it.first } }.map { it.login }
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
                            cabinets = cabinets,
                            forms = r.forms
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
