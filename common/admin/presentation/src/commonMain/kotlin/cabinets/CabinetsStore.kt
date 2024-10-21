package cabinets

import TeacherPerson
import admin.cabinets.CabinetItem
import cabinets.CabinetsStore.Intent
import cabinets.CabinetsStore.Label
import cabinets.CabinetsStore.State
import com.arkivanov.mvikotlin.core.store.Store

interface CabinetsStore : Store<Intent, State, Label> {
    data class State(
        val cabinets: List<CabinetItem> = emptyList(),
        val teachers: List<TeacherPerson> = emptyList()
    )

    sealed interface Intent {
        data object Init : Intent
        data class UpdateCabinet(val login: String, val cabinet: Int) : Intent

        data object SendItToServer : Intent
    }

    sealed interface Message {
        data class ListUpdated(val cabinets: List<CabinetItem>) : Message
        data class TeachersInited(val teachers: List<TeacherPerson>) : Message
    }

    sealed interface Label

}
