package schedule

import AdminRepository
import CDispatcher
import admin.schedule.ScheduleFormValue
import admin.schedule.SchedulePerson
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

            is Intent.ciCreate -> {

                println("ITEM!!!")
                createItem(intent.cTiming ?: state().ciTiming!!)
                mpCreateItem.onEvent(MpChoseStore.Intent.HideDialog)
            }

            Intent.ciPreview -> scope.launch { dispatch(Message.ciPreviewed) }

            is Intent.ciStart -> scope.launch {
                if (intent.login != state().ciLogin) dispatch(Message.ciReset)
                val cabinet =
                    state().cabinets.firstOrNull { it.login == intent.login }?.cabinet ?: 0
                dispatch(Message.ciStarted(intent.login, cabinet, formId = intent.formId))
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
                    (state().activeTeachers[key] ?: emptyList()).plus(intent.login)
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
                println("MEOW")
                scope.launch(CDispatcher) {
                    val key =
                        if (state().isDefault) state().defaultDate.toString() else state().currentDate.second
                    val newItems = (state().items[key] ?: emptyList()).toMutableList()
                    val oldItem = newItems.first { it.index == intent.index }
                    val newItem = ScheduleItem(
                        teacherLogin = intent.login,
                        groupId = intent.id,
                        t = ScheduleTiming(
                            start = intent.s.first,
                            end = intent.s.second
                        ),
                        cabinet = intent.cabinet,
                        teacherLoginBefore = oldItem.teacherLoginBefore,
                        formId = null,
                        custom = oldItem.custom,
                        index = oldItem.index,
                        subjectId = oldItem.subjectId,
                        isMarked = oldItem.isMarked
                    )
                    newItems.remove(oldItem)
                    newItems.add(newItem)

                    if (state().isEditItemCouldBeSavedWithDeletedLogins) {
                        val newSolveConflictItems = state().solveConflictItems.toMutableMap()
                        newSolveConflictItems[key]?.set(intent.index, emptyList())

                        scope.launch {
                            dispatch(
                                Message.SolveConflictItemsUpdated(
                                    newSolveConflictItems.toMap(HashMap()),
                                    niErrors = null
                                )
                            )
                        }
                    }
                    scope.launch {
                        println("SSSS")
                        dispatch(
                            Message.ItemsUpdated(
                                newItems
                            )
                        )
                        mpEditItem.onEvent(MpChoseStore.Intent.HideDialog)
                        deleteEmptyLessons()
                    }

                }
            }

            is Intent.eiDelete -> eiDelete(intent.index)
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
            is Intent.ciChangeCustom -> dispatch(Message.ciCustomChanged(intent.custom))
            is Intent.CopyFromStandart -> copyFromStandart()
            is Intent.StartConflict -> {dispatch(
                Message.ConflictStarted(
                    niFormId = intent.niFormId,
                    niGroupId = intent.niGroupId,
                    niCustom = intent.niCustom,
                    niErrors = intent.niErrors,
                    niTeacherLogin = intent.niTeacherLogin,
                    niId = intent.niId,
                    niOnClick = {
                        intent.niOnClick()
                        dispatch(Message.NiOnClicked)
                    }
                )) }

            is Intent.SolveConflict -> solveConflict(lessonId = intent.lessonId, studentLogins = intent.studentLogins)

            is Intent.ciChangeSubjectId -> dispatch(Message.ciSubjectIdChanged(intent.subjectId))
        }
    }

    private fun eiDelete(index: Int) {
        scope.launch(CDispatcher) {
            val key = if (state().isDefault) state().defaultDate.toString() else state().currentDate.second
            val list = (state().items[key] ?: emptyList()).toMutableList()
            list.remove(list.first { it.index == index })
            val newSolveConflictItems = state().solveConflictItems.toMutableMap()
            newSolveConflictItems[key]?.set(index, emptyList())

            scope.launch {
                dispatch(Message.ItemsUpdated(list))
                dispatch(
                    Message.SolveConflictItemsUpdated(
                        newSolveConflictItems.toMap(HashMap()),
                        niErrors = null
                    )
                )
                mpEditItem.onEvent(MpChoseStore.Intent.HideDialog)
            }
        }
    }

    private fun solveConflict(lessonId: Int, studentLogins: List<String>) {
        scope.launch(CDispatcher) {
            try {
                val key = if (state().isDefault) state().defaultDate.toString() else state().currentDate.second
                val newSolveConflictItems = state().solveConflictItems.toMutableMap()
                if (newSolveConflictItems.containsKey(key)) {
                    newSolveConflictItems[key]!![lessonId] =
                        (newSolveConflictItems[key]?.get(1) ?: listOf()) + studentLogins
                } else {
                    newSolveConflictItems[key] =
                        mutableMapOf(lessonId to (newSolveConflictItems[key]?.get(1) ?: listOf()) + studentLogins)
                }
                val niErrors = state().niErrors.mapNotNull {
                    if (it.logins == studentLogins) {
                        null
                    } else it
                }
                scope.launch {
                    dispatch(
                        Message.SolveConflictItemsUpdated(
                            newSolveConflictItems.toMap(HashMap()),
                            niErrors = niErrors
                        )
                    )
                    if (!state().isNiCreated) {

                        println("SADD: ${state().niOnClick}")
                        state().niOnClick()
                    }
                    val lessons = state().items[key]!!.toMutableList()
                    val lesson = lessons.firstOrNull { it.index == lessonId }
                    if (lesson != null) {
                        if (lesson.groupId == -6) {
                            val newList = lesson.custom- studentLogins.toSet()
                            lessons.remove(lesson)
                            lessons.add(lesson.copy(custom = newList))
                            dispatch(
                                Message.ItemsUpdated(
                                    items = lessons
                                )
                            )
                        }
                    }
                }

            } catch (e: Throwable) {
                print("error when solving conflict ${e}")
            }
        }
    }

    private fun copyFromStandart() {
        scope.launch(CDispatcher) {
            nInterface.nStartLoading()
            try {
                val standartItems = state().items[state().currentDate.first.toString()]!!
                    .map {
                        it.copy(
                            index = it.index
                                    + (state().items.flatMap { it.value.map { it.index } }.maxByOrNull { it } ?: 1)
                        )
                    }
                val globalOldSolvedConflictItems =
                    (state().solveConflictItems)
                val oldSolvedConflictItems = mutableMapOf<Int, List<String>>()

                val a = (state().solveConflictItems[state().currentDate.first.toString()] ?: mapOf())
                a.forEach {
                    oldSolvedConflictItems[it.key
                            + (state().items.flatMap { it.value.map { it.index } }.maxByOrNull { it } ?: 1)
                    ] = it.value
                }
                globalOldSolvedConflictItems[state().currentDate.second.toString()] = oldSolvedConflictItems


                val items = hashMapOf(
                    state().currentDate.second to standartItems
                )
                val commonList =
                    state().activeTeachers.toMutableMap()

                standartItems.forEach { item ->
                    val teacher = if (item.teacherLogin in state().teachers.map { it.login }) item.teacherLogin
                    else null
                    if (teacher != null) {
                        val list =
                            (commonList[state().currentDate.second])
                                ?: emptyList()

                        if (teacher !in list) {
                            val newList = list.toMutableList()
                            newList.add(teacher)

                            val old = (commonList[state().currentDate.second]
                                ?: emptyList())
                            commonList[state().currentDate.second] = old + teacher
                        }
                    }
                }

                scope.launch {
                    dispatch(
                        Message.TeacherListUpdated(
                            activeTeachers = commonList.toMap(HashMap())
                        )
                    )
                    dispatch(
                        Message.SolveConflictItemsUpdated(
                            solveConflictItems = globalOldSolvedConflictItems,
                            niErrors = null
                        )
                    )

                    dispatch(Message.ListUpdated((state().items + items).toMap(HashMap())))
                    nInterface.nSuccess()
                    updateCreateTeacherList()
                }
            } catch (_: Throwable) {
                nInterface.nError("Не удалось скопировать") {
                    nInterface.goToNone()
                }
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun saveItems() {
        GlobalScope.launch(CDispatcher) {
            try {
                nInterface.nStartLoading()
                adminRepository.saveSchedule(
                    RScheduleList(
                        list = state().items,
                        conflictList = state().solveConflictItems
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

            try {
                val commonList =
                    state().activeTeachers.toMutableMap()
                if (!state().items.containsKey(date)) {

                    nInterface.nStartLoading()
                    val r = adminRepository.fetchSchedule(
                        dayOfWeek = dayOfWeek.toString(),
                        date = date,
                        isFirstTime = !state().items.containsKey("4")
                    )
                    r.list.forEach {
                        it.value.forEach { item ->
                            val teacher = if (item.teacherLogin in state().teachers.map { it.login }) item.teacherLogin
                            else null
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
                        dispatch(Message.ListUpdated((state().items + r.list).toMap(HashMap())))
                        dispatch(
                            Message.SolveConflictItemsUpdated(
                                (state().solveConflictItems + r.conflictList).toMap(HashMap()),
                                niErrors = null
                            )
                        )
                        nInterface.nSuccess()
                        updateCreateTeacherList()
                    }


                } else {
                    nInterface.nStartLoading()
                    val items = state().items[date]
                    items?.forEach { item ->
                        val teacher = if (item.teacherLogin in state().teachers.map { it.login }) item.teacherLogin
                        else null
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
                    scope.launch {
                        scope.launch {
                            dispatch(
                                Message.TeacherListUpdated(
                                    activeTeachers = commonList.toMap(HashMap())
                                )
                            )

                        }
                        nInterface.nSuccess()
                        updateCreateTeacherList()
                    }
                }
            } catch (e: Throwable) {
                println(e)
                nInterface.nError("Не удалось загрузить расписание") {
                    fetchItems(dayOfWeek, date)
                    updateCreateTeacherList(true)
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

    private fun createItem(t: ScheduleTiming) {
        val item = ScheduleItem(
            teacherLogin = state().ciLogin ?: state().login,
            groupId = state().ciId!!,
            t = t,
            cabinet = state().ciCabinet,
            teacherLoginBefore = state().ciLogin ?: state().login,
            formId = state().ciFormId,
            custom = state().ciCustom,
            index = (state().items.flatMap { it.value.map { it.index } }.maxByOrNull { it } ?: 1) + 1,
            subjectId = state().ciSubjectId,
            isMarked = false
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
                deleteEmptyLessons() //item
            }

            scope.launch {
                dispatch(Message.ciReset)
            }
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

    private fun deleteEmptyLessons(
        immuniItem: ScheduleItem? = null
    ) {
        println("IMMUNI ${immuniItem}")
        scope.launch(CDispatcher) {
            val key = if (state().isDefault) state().defaultDate.toString() else state().currentDate.second
            val trueItems =
                (state().items[key]
                    ?: emptyList())
            val items = trueItems.mapNotNull {
                println(it)
                if (fetchLoginsOfLesson(
                        trueItems = trueItems,
                        solvedConflictsItems = state().solveConflictItems[key],
                        students = state().students,
                        forms = state().forms,
                        lessonIndex = it.index,
                        state = state(),
                        timing = null
                    )?.okLogins?.isEmpty() == true && it != immuniItem
                ) {
                    eiDelete(it.index) //null
                } else it
            }

//            scope.launch {
//                dispatch(Message.ItemsUpdated(
//                    if (!items.contains(immuniItem) && immuniItem != null) {
//                        (items + immuniItem)
//                    } else items
//                ))
//            }

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
            val editItem = trueItems.first { it.index == state().eiIndex!! }
            val coItems =
                (trueItems - editItem).filter {
                    // ! (закончилось раньше чем началось наше) или (началось позже чем началось наше)
                    ((!((it.t.end.toMinutes() < s.first.toMinutes() ||
                            it.t.start.toMinutes() > s.second.toMinutes())) && it.groupId != -11) ||
                            (!((it.t.end.toMinutes() <= s.first.toMinutes() ||
                                    it.t.start.toMinutes() >= s.second.toMinutes())) && it.groupId == -11))
                }
            val logins = fetchLoginsOfLesson(
                trueItems = trueItems,
                solvedConflictsItems = state().solveConflictItems[key],
                students = state().students,
                forms = state().forms,
                lessonIndex = editItem.index,
                state = state()
            )
            val okStudents = logins?.okLogins?.mapNotNull { l ->
                state().students.firstOrNull { it.login == l }
            } ?: listOf()
            val allStudents =
                ((((logins?.deletedLogins ?: listOf()) + (logins?.okLogins)) ?: listOf())).mapNotNull { l ->
                    state().students.firstOrNull { it.login == l }
                } ?: listOf()

            val okErrors = getStudentErrors(
                coItems = coItems,
                students = okStudents,
                state = state()
            )

            val allErrors = getStudentErrors(
                coItems = coItems,
                students = allStudents,
                state = state()
            )

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

            scope.launch {
                dispatch(
                    Message.IsEditItemCouldBeBLABLABLAChanged(allErrors == okErrors)
                )
                dispatch(
                    Message.eiErrorsUpdated(
                        cabinetErrorGroupId = cabinetError,
                        studentErrors = okErrors
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
        val students = fetchLoginsOfLesson(
            trueItems = trueItems,
            solvedConflictsItems = state().solveConflictItems[key],
            students = state().students,
            forms = state().forms,
            lessonIndex = (state().items.flatMap { it.value.map { it.index } }.maxByOrNull { it } ?: 1) + 1,
            state = state(),
            timing = ScheduleTiming(start = s.first, end = s.second)
        )?.okLogins?.mapNotNull { l ->
            state().students.firstOrNull { it.login == l }
        } ?: listOf()
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
            coItems.filter { coItem ->
                coItem.groupId in students.flatMap { student -> student.groups.map { it.first } } ||
                        (students.map { student -> student.login }).any { coItem.custom.contains(it) } ||
                        coItem.formId in students.flatMap { student ->
                    state().forms.mapNotNull {
                        if (student.login in it.value.logins) {
                            it.key
                        } else null
                    }
                }
            }

        studentErrorItems.forEach { item ->
            val error = StudentError(
                groupId = item.groupId,
                logins = students.filter { s ->
                    item.groupId in s.groups.map { it.first }
                            || (item.custom.contains(s.login)) ||
                            item.formId == (state().forms.toList()
                        .firstOrNull { s.login in it.second.logins }?.first)
                }.map { it.login },
                teacherLogin = item.teacherLogin,
                id = item.index
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

data class LoginsOfLesson(
    val okLogins: List<String>,
    val deletedLogins: List<String>
)

fun getStudentErrors(
    coItems: List<ScheduleItem>,
    students: List<SchedulePerson>,
    state: ScheduleStore.State
): MutableList<StudentError> {
    val key = if (state.isDefault) state.defaultDate.toString() else state.currentDate.second
    val studentErrors: MutableList<StudentError> = mutableListOf()
    val studentErrorItems =
        coItems.filter { coItem ->
            coItem.groupId in students.filter {
               it.login !in (state.solveConflictItems[key]?.get(coItem.index) ?: listOf())
            }.flatMap { student -> student.groups.map { it.first } } ||
                    (students.map { student -> student.login }).any { coItem.custom.contains(it) }
                    ||
                    coItem.formId in students.flatMap { student ->
                state.forms.mapNotNull {
                    if (student.login in it.value.logins) {
                        it.key
                    } else null
                }
            }
        }
    studentErrorItems.forEach { item ->
        val error = StudentError(
            groupId = item.groupId,
            logins = students.filter { s ->
                (
                item.groupId in s.groups.map { it.first }
                || (item.custom.contains(s.login))
                || item.formId == (state.forms.toList().firstOrNull { s.login in it.second.logins }?.first)
                        ) && (s.login !in (state.solveConflictItems[key]?.get(item.index) ?: listOf()))
            }.map { it.login },
            teacherLogin = item.teacherLogin,
            id = item.index
        )
        try {
            studentErrors.remove(error)
        } catch (_: Throwable) {
        }
        studentErrors.add(
            error
        )
    }
    return studentErrors
}

fun fetchLoginsOfLesson(
    trueItems: List<ScheduleItem>,
    solvedConflictsItems: MutableMap<Int, List<String>>?,
    students: List<SchedulePerson>,
    forms: HashMap<Int, ScheduleFormValue>,
    lessonIndex: Int,
    state: ScheduleStore.State,
    timing: ScheduleTiming? = null
): LoginsOfLesson? {
    val newItemIndex = (state.items.flatMap { it.value.map { it.index } }.maxByOrNull { it } ?: 1) + 1
    val item = if (newItemIndex != lessonIndex) trueItems.firstOrNull { it.index == lessonIndex }
    else ScheduleItem(
        teacherLogin = state.ciLogin ?: state.login,
        groupId = state.ciId!!,
        t = timing!!,
        cabinet = state.ciCabinet,
        teacherLoginBefore = state.ciLogin ?: state.login,
        formId = state.ciFormId,
        custom = state.ciCustom,
        index = (state.items.flatMap { it.value.map { it.index } }.maxByOrNull { it } ?: 1) + 1,
        subjectId = state.ciSubjectId,
        isMarked = false
    )
    return if (item != null) {
        val minusLogins = solvedConflictsItems?.get(lessonIndex) ?: listOf()
        val beforeLogins = students.filter { s ->
            item.groupId in s.groups.map { it.first }
                    || item.custom.contains(s.login)
                    || item.formId == (forms.toList().firstOrNull { s.login in it.second.logins }?.first)
        }.map { it.login }
        LoginsOfLesson(
            okLogins = (beforeLogins - minusLogins.toSet()).toList(),
            deletedLogins = minusLogins.toSet().toList()
        )
    } else null
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
