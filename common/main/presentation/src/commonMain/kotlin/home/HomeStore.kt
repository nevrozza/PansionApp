package home

import com.arkivanov.mvikotlin.core.store.Store
import home.HomeStore.Intent
import home.HomeStore.Label
import home.HomeStore.State

interface HomeStore : Store<Intent, State, Label> {
    data class State(
        val name: String,
        val surname: String,
        val praname: String,
        val period: Period = Period.WEEK,
        val averageGradePoint: HashMap<Period, Float?> = hashMapOf(
            Period.WEEK to null,
            Period.MODULE to null,
            Period.HALF_YEAR to null,
            Period.YEAR to null
        ),
        val ladderOfSuccess: HashMap<Period, Int?> = hashMapOf(
            Period.WEEK to null,
            Period.MODULE to null,
            Period.HALF_YEAR to null,
            Period.YEAR to null
        ),
        val homeWorkEmoji: String? = null
    )

    sealed interface Intent

    sealed interface Message

    sealed interface Label


    enum class Period {
        WEEK, MODULE, HALF_YEAR, YEAR
    }

    object Emojis {
        const val check: String = "✅"
        const val smileTeeth: String = "\uD83D\uDE01"
        const val smile: String = "\uD83D\uDE42"
        const val normal: String = "\uD83D\uDE10"
        const val scared: String = "\uD83D\uDE28"
        const val horror: String = "\uD83D\uDE31"
        const val death: String = "☠\uFE0F"
    }
}

