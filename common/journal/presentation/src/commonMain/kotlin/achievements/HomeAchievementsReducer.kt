package achievements

import com.arkivanov.mvikotlin.core.store.Reducer
import achievements.HomeAchievementsStore.State
import achievements.HomeAchievementsStore.Message

object HomeAchievementsReducer : Reducer<State, Message> {
    override fun State.reduce(msg: Message): State {
        return when (msg) {
            is Message.Inited -> copy(achievements = msg.achievements, subjects = msg.subjects)
        }
    }
}