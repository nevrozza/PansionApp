package schedule

import AdminRepository
import admin.schedule.ScheduleFormValue
import admin.schedule.SchedulePerson
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import components.listDialog.ListComponent
import components.listDialog.ListDialogStore
import components.listDialog.ListItem
import components.mpChose.MpChoseComponent
import components.mpChose.MpChoseStore
import components.networkInterface.NetworkInterface
import deviceSupport.launchIO
import deviceSupport.withMain
import di.Inject
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import schedule.ScheduleStore.Intent
import schedule.ScheduleStore.Label
import schedule.ScheduleStore.Message
import schedule.ScheduleStore.State
import server.toMinutes

class ScheduleExecutor(
    private val adminRepository: AdminRepository = Inject.instance(),
    private val nInterface: NetworkInterface,
    private val mpCreateItem: MpChoseComponent,
    private val mpEditItem: MpChoseComponent,
    private val listCreateTeacher: ListComponent
) : CoroutineExecutor<Intent, Unit, State, Message, Label>() {

    override fun executeAction(action: Unit) {
        init()
    }


    override fun executeIntent(intent: Intent) {
        when (intent) {
            Intent.Init -> init()
            is Intent.ciChooseGroup -> chooseGroup(intent.groupId)
            is Intent.ciChooseTime -> scope.launchIO {
                val timing = getScheduleTiming(Pair(intent.t.start, intent.t.end))
                withMain {
                    dispatch(
                        Message.ciTimeChosed(
                            timing
                        )
                    )
                }
            }

            is Intent.ciCreate -> {

                createItem(intent.cTiming ?: state().ciTiming!!)
                if (!state().ciIsPair) {
                    mpCreateItem.onEvent(MpChoseStore.Intent.HideDialog)
                }
            }

            Intent.ciPreview -> dispatch(Message.ciPreviewed)

            is Intent.ciStart -> {
                if (intent.login != state().ciLogin) dispatch(Message.ciReset)
                val cabinet =
                    state().cabinets.firstOrNull { it.login == intent.login }?.cabinet ?: 0
                dispatch(Message.ciStarted(intent.login, cabinet, formId = intent.formId))
            }

            Intent.ciNullGroupId ->
                dispatch(Message.ciGroupIdNulled)


            Intent.ciFalsePreview ->
                dispatch(Message.ciPreviewFalsed)


            Intent.UpdateCTeacherList -> updateCreateTeacherList()
            is Intent.CreateTeacher -> scope.launchIO {
                val key =
                    if (state().isDefault) state().defaultDate.toString() else state().currentDate.second
                val new = state().activeTeachers.toMutableMap()
                new[key] =
                    (state().activeTeachers[key] ?: emptyList()).plus(intent.login)
                withMain {
                    dispatch(Message.TeacherCreated(new.toMap(HashMap())))
                    updateCreateTeacherList()
                }
            }

            is Intent.ciChangeCabinet -> {
                dispatch(Message.ciCabinetChanged(intent.cabinet))

                if (intent.cabinet.toString().length == 3) {
                    updateTimePeekList()
                }
            }

            is Intent.eiChangeCabinet ->
                dispatch(Message.eiCabinetChanged(intent.cabinet))


            is Intent.StartEdit -> {
                mpEditItem.onEvent(MpChoseStore.Intent.ShowDialog)
                dispatch(Message.EditStarted(index = intent.index, formId = intent.formId))
            }

            is Intent.eiChooseGroup ->
                dispatch(Message.eiGroupChosed(intent.groupId))


            is Intent.eiChangeState ->
                dispatch(Message.eiStateChanged(intent.state))


            is Intent.eiChangeTiming ->
                dispatch(Message.eiTimingChanged(intent.timing))


            is Intent.eiCheck -> updateEditErrors(
                cabinet = intent.cabinet,
                login = intent.login,
                s = intent.s
            )

            is Intent.eiSave -> {
                scope.launchIO {
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

                        withMain {
                            dispatch(
                                Message.SolveConflictItemsUpdated(
                                    newSolveConflictItems.toMap(HashMap()),
                                    niErrors = null
                                )
                            )
                        }
                    }
                    withMain {
                        dispatch(
                            Message.ItemsUpdated(
                                newItems
                            )
                        )
                        mpEditItem.onEvent(MpChoseStore.Intent.HideDialog)
                        deleteEmptyAndRepeatedLessons()
                    }

                }
            }

            is Intent.eiDelete -> eiDelete(intent.index)
            Intent.ciChangeIsPair ->
                dispatch(Message.ciIsPairChanged)


            Intent.ChangeEditMode -> {
                dispatch(Message.EditModeChanged)
                fetchItems(
                    state().currentDate.first,
                    if (state().isDefault) state().defaultDate.toString() else state().currentDate.second
                )
            }

            is Intent.ChangeDefaultDate -> {
                dispatch(Message.DefaultDateChanged(intent.date))
                fetchItems(intent.date, intent.date.toString())
//                updateCreateTeacherList()
            }

            is Intent.ChangeCurrentDate -> {
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
            is Intent.StartConflict -> scope.launch {
                val errors = intent.niErrors.filter { it.groupId != intent.niGroupId }
                dispatch(
                    Message.ConflictStarted(
                        niFormId = intent.niFormId,
                        niGroupId = intent.niGroupId,
                        niCustom = intent.niCustom,
                        niErrors = errors,
                        niTeacherLogin = intent.niTeacherLogin,
                        niId = intent.niId,
                        niOnClick = {
                            intent.niOnClick()
                            dispatch(Message.NiOnClicked)
                        }
                    ))
            }


            is Intent.SolveConflict -> solveConflict(
                fromLessonId = intent.fromLessonId,
                toLessonId = intent.toLessonId,
                studentLogins = intent.studentLogins
            )

            is Intent.ciChangeSubjectId -> dispatch(Message.ciSubjectIdChanged(intent.subjectId))
        }
    }

    private fun eiDelete(index: Int) {
        scope.launchIO {
            val key =
                if (state().isDefault) state().defaultDate.toString() else state().currentDate.second
            val list = (state().items[key] ?: emptyList()).toMutableList()

            val item = list.first { it.index == index }

            list.remove(item)
            println("CHECK: ${item.t.studentErrors}")
            val newSolveConflictItems = state().solveConflictItems.toMutableMap()
            newSolveConflictItems[key]?.set(index, emptyList())
            item.t.studentErrors.forEach { studentError ->
                if (newSolveConflictItems.containsKey(key)) {
                    val newSolveConflictItem = newSolveConflictItems[key]!!
                    if (newSolveConflictItem.containsKey(studentError.id)) {
                        newSolveConflictItems[key]?.set(
                            studentError.id,
                            newSolveConflictItem[studentError.id]!! - studentError.logins.toSet()
                        )
                    }
                }
            }
            withMain {
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

    private fun solveConflict(fromLessonId: Int, toLessonId: Int, studentLogins: List<String>) {
        scope.launchIO {
            try {
                val key =
                    if (state().isDefault) state().defaultDate.toString() else state().currentDate.second
                val newSolveConflictItems = state().solveConflictItems.toMutableMap()
                if (newSolveConflictItems.containsKey(key)) {
                    newSolveConflictItems[key]!![fromLessonId] =
                        (newSolveConflictItems[key]!![fromLessonId] ?: listOf()) + studentLogins
                } else {
                    newSolveConflictItems[key] =
                        mutableMapOf(
                            fromLessonId to (newSolveConflictItems[key]?.get(fromLessonId)
                                ?: listOf()) + studentLogins
                        )
                }
                newSolveConflictItems[key]!![toLessonId] =
                    (newSolveConflictItems[key]!![toLessonId] ?: listOf()) - studentLogins.toSet()

                val niErrors = state().niErrors.mapNotNull {
                    if (it.id in listOf(fromLessonId, toLessonId))  {
                        val newLogins = it.logins- studentLogins.toSet()
                        if (newLogins.isNotEmpty()) {
                            it.copy(logins = newLogins)
                        } else null
                    } else it
                }

                println("NI_ERRORS: ${fromLessonId} ${niErrors}")

                withMain {
                    dispatch(
                        Message.SolveConflictItemsUpdated(
                            newSolveConflictItems.toMap(HashMap()),
                            niErrors = niErrors
                        )
                    )
                    if (!state().isNiCreated) {

                        state().niOnClick()
                    }
                    val lessons = state().items[key]!!.toMutableList()
                    val lesson = lessons.firstOrNull { it.index == fromLessonId }
                    if (lesson != null) {
                        if (lesson.groupId == -6) {
                            val newList = lesson.custom - studentLogins.toSet()
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
        scope.launchIO {
            nInterface.nStartLoading()
            try {
                val standartItems = state().items[state().currentDate.first.toString()]!!
                    .map {
                        it.copy(
                            index = it.index
                                    + (state().items.flatMap { it.value.map { it.index } }
                                .maxByOrNull { it } ?: 1)
                        )
                    }
                val globalOldSolvedConflictItems =
                    (state().solveConflictItems)
                val oldSolvedConflictItems = mutableMapOf<Int, List<String>>()

                val a =
                    (state().solveConflictItems[state().currentDate.first.toString()] ?: mapOf())
                a.forEach {
                    oldSolvedConflictItems[it.key
                            + (state().items.flatMap { it.value.map { it.index } }
                        .maxByOrNull { it } ?: 1)
                    ] = it.value
                }
                globalOldSolvedConflictItems[state().currentDate.second] =
                    oldSolvedConflictItems


                val items = hashMapOf(
                    state().currentDate.second to standartItems
                )
                val commonList =
                    state().activeTeachers.toMutableMap()

                standartItems.forEach { item ->
                    val teacher =
                        if (item.teacherLogin in state().teachers.map { it.login }) item.teacherLogin
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

                withMain {
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
            } catch (e: Throwable) {
                nInterface.nError("Не удалось скопировать", e) {
                    nInterface.goToNone()
                }
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun saveItems() {
        GlobalScope.launchIO {
            try {
                nInterface.nStartLoading()
                adminRepository.saveSchedule(
                    RScheduleList(
                        list = state().items,
                        conflictList = state().solveConflictItems
                    )
                )
                withMain {
                    dispatch(Message.IsSavedAnimation(true))
                    nInterface.nSuccess()
                }
            } catch (e: Throwable) {
                nInterface.nError("Не удалось сохранить", e) {
                    saveItems()
                }
            }
        }
    }

    private fun fetchItems(dayOfWeek: Int, date: String) {
        scope.launchIO {
            withMain {
                dispatch(Message.ciReset)
            }
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
                            val teacher =
                                if (item.teacherLogin in state().teachers.map { it.login }) item.teacherLogin
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
                    withMain {
                        /// wtf launch
                        dispatch(
                            Message.TeacherListUpdated(
                                activeTeachers = commonList.toMap(HashMap())
                            )
                        )
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
                        val teacher =
                            if (item.teacherLogin in state().teachers.map { it.login }) item.teacherLogin
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
                    withMain {
                        /// wtf launch
                        dispatch(
                            Message.TeacherListUpdated(
                                activeTeachers = commonList.toMap(HashMap())
                            )
                        )
                        nInterface.nSuccess()
                        updateCreateTeacherList()
                    }
                }
            } catch (e: Throwable) {
                println(e)
                nInterface.nError("Не удалось загрузить расписание", e) {
                    fetchItems(dayOfWeek, date)
                    updateCreateTeacherList(true)
                }
            }
        }
    }


    private fun updateCreateTeacherList(isError: Boolean = false) {
        scope.launchIO {
            if (!isError) {
                val key =
                    if (state().isDefault) state().defaultDate.toString() else state().currentDate.second
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
                withMain {
                    listCreateTeacher.onEvent(ListDialogStore.Intent.InitList(newList))
                }
            } else {
                withMain {
                    listCreateTeacher.onEvent(ListDialogStore.Intent.InitList(listOf()))
                }
            }
        }
    }

    private fun createItem(t: ScheduleTiming) {
        // добавить проверку, чтобы такого предмета не было

        val item = ScheduleItem(
            teacherLogin = state().ciLogin ?: state().login,
            groupId = state().ciId!!,
            t = t,
            cabinet = state().ciCabinet,
            teacherLoginBefore = state().ciLogin ?: state().login,
            formId = state().ciFormId,
            custom = state().ciCustom,
            index = (state().items.flatMap { it.value.map { it.index } }.maxByOrNull { it }
                ?: 1) + 1,
            subjectId = state().ciSubjectId,
            isMarked = false
        )
        scope.launchIO {
            val key =
                if (state().isDefault) state().defaultDate.toString() else state().currentDate.second
            val items =
                (state().items[key]
                    ?: emptyList()).toMutableList()
            val time = item.t
            val coItems =
                (items).filter {
                    // ! (закончилось раньше чем началось наше) или (началось позже чем началось наше)
                    ((!((it.t.end.toMinutes() < time.start.toMinutes() ||
                            it.t.start.toMinutes() > time.end.toMinutes())) && it.groupId != -11) ||
                            (!((it.t.end.toMinutes() <= time.start.toMinutes() ||
                                    it.t.start.toMinutes() >= time.end.toMinutes())) && it.groupId == -11))
                }


            items.add(
                item
            )
            withMain {
                if (!coItems.any { it.groupId == item.groupId }) {
                    dispatch(Message.ItemsUpdated(items))
                }
                deleteEmptyAndRepeatedLessons() //item
            }

            withMain {
                dispatch(Message.ciReset)
            }

        }
    }

    private fun chooseGroup(groupId: Int) {
        dispatch(Message.ciGroupChosed(groupId))
        updateTimePeekList()
    }

    private fun updateTimePeekList() {
        scope.launchIO {
            val timings: MutableList<ScheduleTiming> = mutableListOf()

            timingsPairs.forEach { s ->
                timings.add(getScheduleTiming(s))
            }

            withMain {
                dispatch(Message.ciTimingsGot(timings))
            }
        }
    }


    private fun deleteEmptyAndRepeatedLessons(
        immuniItem: ScheduleItem? = null
    ) {
        scope.launchIO {
            val key =
                if (state().isDefault) state().defaultDate.toString() else state().currentDate.second
            val trueItems =
                (state().items[key]
                    ?: emptyList())
            // delete the oldest
            trueItems.reversed().map { item ->
                if ((fetchLoginsOfLesson(
                        trueItems = trueItems,
                        solvedConflictsItems = state().solveConflictItems[key],
                        students = state().students,
                        forms = state().forms,
                        lessonIndex = item.index,
                        state = state(),
                        timing = null
                    )?.okLogins?.isEmpty() == true && item != immuniItem) ||
                    trueItems.any {
                        it.index != item.index &&
                                it.groupId == item.groupId &&
                                it.teacherLogin == item.teacherLogin &&
                                it.t.start == item.t.start &&
                                it.t.end == item.t.end //TODO: что если время чуть другое, но перекрывает?
                    }
                ) {
                    // Because of this it works
                    eiDelete(item.index) //null
                } else item
            }
//            val items =

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
        s: Pair<String, String>
    ) {
        scope.launchIO {
            val key =
                if (state().isDefault) state().defaultDate.toString() else state().currentDate.second
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
                ((logins?.deletedLogins ?: listOf()) + (logins?.okLogins)).mapNotNull { l ->
                    state().students.firstOrNull { it.login == l }
                }

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

            withMain {
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
        val key =
            if (state().isDefault) state().defaultDate.toString() else state().currentDate.second
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
            lessonIndex = (state().items.flatMap { it.value.map { it.index } }.maxByOrNull { it }
                ?: 1) + 1,
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
                coItem.groupId != state().ciId &&
                        (
                                coItem.groupId in students.flatMap { student -> student.groups.map { it.first } } ||
                                        (students.map { student -> student.login }).any {
                                            coItem.custom.contains(
                                                it
                                            )
                                        } ||
                                        coItem.formId in students.flatMap { student ->
                                    state().forms.mapNotNull {
                                        if (student.login in it.value.logins) {
                                            it.key
                                        } else null
                                    }
                                }
                                )
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
        scope.launchIO {
            nInterface.nStartLoading()
            try {
//                val ar = async { adminRepository.fetchInitSchedule() }
//                val acR = async { adminRepository.fetchCabinets() }
                val r = adminRepository.fetchInitSchedule()//ar.await()
                val cabinets = adminRepository.fetchCabinets().cabinets//acR.await().cabinets
                withMain {
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
                    "Что-то пошло не так", e
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
    state: State
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
                                || item.formId == (state.forms.toList()
                            .firstOrNull { s.login in it.second.logins }?.first)
                        ) && (s.login !in (state.solveConflictItems[key]?.get(item.index)
                    ?: listOf()))
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
    state: State,
    timing: ScheduleTiming? = null
): LoginsOfLesson? {
    val newItemIndex =
        (state.items.flatMap { it.value.map { it.index } }.maxByOrNull { it } ?: 1) + 1
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
                    || (item.formId != null && item.formId == (forms.toList()
                .firstOrNull { s.login in it.second.logins }?.first))
        }.map { it.login }
        LoginsOfLesson(
            okLogins = (beforeLogins - minusLogins.toSet()).toList(),
            deletedLogins = minusLogins.toSet().toList()
        )
    } else null
}


fun tOverlap(a: ScheduleTiming?, b: ScheduleTiming?): Boolean {
    if (a == null || b == null) {
        return false
    }
    return a.start <= b.end && b.start <= a.end
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
