package cabinets

import Person
import admin.cabinets.CabinetItem
import admin.schedule.SchedulePerson
import com.arkivanov.mvikotlin.core.store.Store
import cabinets.CabinetsStore.Intent
import cabinets.CabinetsStore.Label
import cabinets.CabinetsStore.State

interface CabinetsStore : Store<Intent, State, Label> {
    data class State(
        val cabinets: List<CabinetItem> = emptyList(),
        val teachers: List<Person> = emptyList()
    )

    sealed interface Intent {
        data object Init : Intent
        data class UpdateCabinet(val login: String, val cabinet: Int) : Intent

        data object SendItToServer : Intent
    }

    sealed interface Message {
        data class ListUpdated(val cabinets: List<CabinetItem>) : Message
        data class TeachersInited(val teachers: List<Person>) : Message
    }

    sealed interface Label

}
