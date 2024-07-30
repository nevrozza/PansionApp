package achievements

import com.arkivanov.mvikotlin.core.store.Store
import achievements.HomeAchievementsStore.Intent
import achievements.HomeAchievementsStore.Label
import achievements.HomeAchievementsStore.State

interface HomeAchievementsStore : Store<Intent, State, Label> {
    data class State(
        val achievements: List<AchievementsDTO> = emptyList(),
        val subjects: Map<Int, String> = emptyMap(),
        val login: String
    )

    sealed interface Intent {
        data object Init : Intent
    }

    sealed interface Message {
        data class Inited(val achievements: List<AchievementsDTO>, val subjects: Map<Int, String>) : Message
    }

    sealed interface Label

}
