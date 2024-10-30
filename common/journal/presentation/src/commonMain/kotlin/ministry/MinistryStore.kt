package ministry

import com.arkivanov.mvikotlin.core.store.Store
import main.school.MinistryKid
import ministry.MinistryStore.Intent
import ministry.MinistryStore.Label
import ministry.MinistryStore.State
import server.getCurrentDate
import server.getDates


data class MinistryListItem(
    val date: String,
    val ministryId: String,
    val kids: List<MinistryKid>
)

interface MinistryStore : Store<Intent, State, Label> {
    data class State(
        val dates: List<Pair<Int, String>> = getDates(minus = 14, plus = 0),
        val currentDate: Pair<Int, String> = getCurrentDate(),
        val isMultiMinistry: Boolean? = null,
        val pickedMinistry: String = "0",
        val ministryList: List<MinistryListItem> = emptyList()
    )

    sealed interface Intent {
        data object Init : Intent
        data class ChangeMinistry(val ministryId: String) : Intent
        data class ChangeDate(val date: Pair<Int, String>) : Intent
    }

    sealed interface Message {
        data class MinistryHeaderInited(val isMultiMinistry: Boolean, val pickedMinistry: String) : Message
        data class MinistryChanged(val ministryId: String) : Message
        data class DateChanged(val date: Pair<Int, String>) : Message
        data class ListUpdated(val list: List<MinistryListItem>) : Message
    }

    sealed interface Label

}
