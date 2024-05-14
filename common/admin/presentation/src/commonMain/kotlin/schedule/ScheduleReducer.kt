package schedule

import com.arkivanov.mvikotlin.core.store.Reducer
import schedule.ScheduleStore.State
import schedule.ScheduleStore.Message

object ScheduleReducer : Reducer<State, Message> {
    override fun State.reduce(msg: Message): State {
        return when (msg) {
            is Message.Inited -> {
                copy(
                    teachers = msg.teachers,
                    students = msg.students,
                    subjects = msg.subjects,
                    groups = msg.groups,
                    cabinets = msg.cabinets
                )
            }

            is Message.ciGroupChosed -> copy(ciId = msg.groupId)
            Message.ciPreviewed -> copy(ciPreview = true)

            // IDK/IDC
            Message.ciReset -> copy(
                ciPreview = false,
                ciTimings = if (ciIsPair) {
                    if (ciTimings != null) {
                        if (Pair(ciTiming!!.start, ciTiming.end) in timingsPairs) {
                            val newList = ciTimings.toMutableList()
                            val element = ciTimings.firstOrNull { it.start == ciTiming.start }
                            newList.remove(element)
                            if (element != null) {
                                newList.add(element.copy(cabinetErrorGroupId = ciId!!))
                                newList
                            } else {
                                null
                            }
                        } else {
                            ciTimings
                        }
                    } else {
                        null
                    }
                } else {
                    null
                },
                ciTiming = null,
                ciId = if (ciIsPair) ciId else null,
                ciCabinet = if (ciIsPair) ciCabinet else 0,
                ciIsPair = false
            )

            is Message.ciTimeChosed -> copy(ciTiming = msg.t)
            is Message.ciTimingsGot -> copy(ciTimings = msg.timings)
            is Message.ciStarted -> copy(ciLogin = msg.login, ciCabinet = msg.cabinet)
            Message.ciGroupIdNulled -> copy(ciId = null)
            Message.ciPreviewFalsed -> copy(ciPreview = false, ciIsPair = false)
            is Message.TeacherCreated -> {
                copy(activeTeachers = msg.activeTeachers)
            }




            is Message.ciCabinetChanged -> copy(ciCabinet = msg.cabinet)
            is Message.ItemsUpdated -> {
                val newItems = items.toMutableMap()
                val key = if (isDefault) defaultDate.toString() else currentDate.second
                newItems[key] = msg.items
                copy(items = newItems.toMap(HashMap()))
            }

            is Message.EditStarted -> copy(
                eiIndex = msg.index,
                eiTiming = null,
                eiState = ScheduleStore.EditState.Preview,
                eiCabinetErrorGroupId = 0,
                eiCabinet = null,
                eiGroupId = null,
                eiStudentErrors = emptyList()
            )


            is Message.eiGroupChosed -> copy(
                eiGroupId = msg.groupId,
                eiState = ScheduleStore.EditState.Preview
            )

            is Message.eiStateChanged -> copy(eiState = msg.state)
            is Message.eiTimingChanged -> copy(
                eiState = ScheduleStore.EditState.Preview,
                eiTiming = msg.timing
            )

            is Message.eiCabinetChanged -> copy(eiCabinet = msg.cabinet)
            is Message.eiErrorsUpdated -> copy(
                eiStudentErrors = msg.studentErrors,
                eiCabinetErrorGroupId = msg.cabinetErrorGroupId
            )

            Message.ciIsPairChanged -> copy(
                ciIsPair = !ciIsPair
            )

            Message.EditModeChanged -> copy(
                isDefault = !isDefault,
                defaultDate = if (currentDate.first !in listOf(6, 7)) currentDate.first else 1
            )

            is Message.DefaultDateChanged -> copy(
                defaultDate = msg.defaultDate
            )

            is Message.CurrentDateChanged -> copy(
                currentDate = msg.currentDate
            )

            is Message.ListUpdated -> copy(items = msg.list)
//            is Message.TeacherListPairUpdated -> {
//                val new = activeTeachers.toMutableList()
//                new.removeAll { it.first == msg.activeTeachers.first }
//                val release = new + Pair(
//                    msg.activeTeachers.first,
//                    msg.activeTeachers.second +
//                            (activeTeachers.firstOrNull { it.first == msg.activeTeachers.first }?.second ?: listOf())
//                )
//                copy(
//                    activeTeachers = release
//                )
//            }
            is Message.TeacherListUpdated -> copy(activeTeachers = msg.activeTeachers)
        }
    }
}