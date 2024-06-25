package home

import com.arkivanov.mvikotlin.core.store.Reducer
import home.HomeStore.State
import home.HomeStore.Message

object HomeReducer : Reducer<State, Message> {
    override fun State.reduce(msg: Message): State {
        return when (msg) {
            is Message.QuickTabUpdated -> copy(averageGradePoint = msg.avg, ladderOfSuccess = msg.stups)
//            is Message.Inited -> copy(
//                avatarId = msg.avatarId,
//                login = msg.login,
//                name = msg.name,
//                praname = msg.praname,
//                surname = msg.surname
//            )

            is Message.TeacherGroupUpdated -> copy(teacherGroups = msg.teacherGroups)
            is Message.GradesUpdated -> copy(grades = msg.grades)
            is Message.ItemsUpdated -> copy(items = msg.items)
            Message.IsDatesShownChanged -> copy(isDatesShown = !isDatesShown)
            is Message.DateChanged -> copy(currentDate = msg.date)
            is Message.SomeHeadersUpdated -> copy(someHeaders = msg.someHeaders)
            is Message.UpdateAfterCheck -> copy(
                avatarId = msg.r.avatarId,
                name = msg.r.name,
                surname = msg.r.surname,
                praname = msg.r.praname ?: ""
            )
        }
    }
}