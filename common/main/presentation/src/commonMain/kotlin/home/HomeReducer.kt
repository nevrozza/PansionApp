package home

import com.arkivanov.mvikotlin.core.store.Reducer
import home.HomeStore.State
import home.HomeStore.Message

object HomeReducer : Reducer<State, Message> {
    override fun State.reduce(msg: Message): State {
        return when (msg) {
            is Message.QuickTabUpdated -> copy(averageGradePoint = msg.avg, ladderOfSuccess = msg.stups, achievements = msg.achievements
                ?: achievements)
            is Message.TeacherGroupUpdated -> copy(teacherGroups = msg.teacherGroups)
            is Message.GradesUpdated -> copy(grades = msg.grades)
            is Message.ItemsUpdated -> copy(items = msg.items)
            Message.IsDatesShownChanged -> copy(isDatesShown = !isDatesShown)
            is Message.DateChanged -> copy(currentDate = msg.date)
            is Message.SomeHeadersUpdated -> copy(someHeaders = msg.someHeaders)
            is Message.AvatarIdUpdated -> copy(avatarId = msg.avatarId)
            is Message.PeriodChanged -> copy(period = msg.period)
            is Message.UpdateHomeWorkEmoji ->
                copy(homeWorkEmojiCount = msg.emoji)
            is Message.NotificationsUpdated -> copy(notifications = msg.notifications)
            is Message.ChildrenUpdated -> copy(children = msg.children)
            is Message.ChildrenNotificationsInited -> copy(notChildren = msg.notChildren, childrenNotifications = msg.childrenNotifications)
        }
    }
}