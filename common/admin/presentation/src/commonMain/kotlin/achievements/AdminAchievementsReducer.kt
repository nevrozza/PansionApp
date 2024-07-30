package achievements

import com.arkivanov.mvikotlin.core.store.Reducer
import achievements.AdminAchievementsStore.State
import achievements.AdminAchievementsStore.Message

object AdminAchievementsReducer : Reducer<State, Message> {
    override fun State.reduce(msg: Message): State {
        return when (msg) {
            is Message.Inited -> copy(
                achievements = msg.achievements,
                students = msg.students,
                subjects = msg.subjects
            )

            is Message.BSInit -> copy(
                bsId = msg.id,
                bsText = msg.text,
                bsDate = msg.date,
                bsShowDate = msg.showDate,
                bsSubjectId = msg.subjectId,
                bsStups = msg.stups,
                bsStudentLogin = msg.studentLogin,
                bsOldShowDate = msg.oldShowDate,
                bsOldDate = msg.oldDate,
                bsOldText = msg.oldText
            )

            is Message.StudentLoginChanged -> copy(bsStudentLogin = msg.login)
            is Message.DateChanged -> copy(bsDate = msg.date)
            is Message.ShowDateChanged -> copy(bsShowDate = msg.date)
            is Message.StupsChanged -> copy(bsStups = msg.stups)
            is Message.SubjectIdChanged -> copy(bsSubjectId = msg.id)
            is Message.TextChanged -> copy(bsText = msg.text)
        }
    }
}