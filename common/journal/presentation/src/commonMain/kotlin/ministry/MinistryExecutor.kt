package ministry

import CDispatcher
import JournalRepository
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import components.networkInterface.NetworkInterface
import kotlinx.coroutines.launch
import main.school.RMinistryListReceive
import ministry.MinistryStore.Intent
import ministry.MinistryStore.Label
import ministry.MinistryStore.Message
import ministry.MinistryStore.State

class MinistryExecutor(
    private val nInterface: NetworkInterface,
    private val journalRepository: JournalRepository
) : CoroutineExecutor<Intent, Unit, State, Message, Label>() {
    override fun executeIntent(intent: Intent) {
        when (intent) {
            Intent.Init -> init()
            is Intent.ChangeMinistry -> {
                dispatch(Message.MinistryChanged(intent.ministryId))
                updateList(ministryId = intent.ministryId, date = state().currentDate.second)
            }
            is Intent.ChangeDate -> {
                dispatch(Message.DateChanged(intent.date))
                updateList(ministryId = state().pickedMinistry, date = intent.date.second)
            }
        }
    }

    private fun updateList(ministryId: String, date: String) {
        if (ministryId != "0") {
            scope.launch(CDispatcher) {
                try {
                    nInterface.nStartLoading()
                    val r = journalRepository.fetchMinistryList(
                        RMinistryListReceive(
                            date = date,
                            ministryId = ministryId
                        )
                    )
                    val newList = state().ministryList.toMutableList()
                    val oldItem = newList.firstOrNull { it.date == date && it.ministryId == ministryId }
                    newList.remove(oldItem)
                    newList.add(
                        MinistryListItem(
                            date = date,
                            ministryId = ministryId,
                            kids = r.kids
                        )
                    )
                    scope.launch {
                        dispatch(Message.ListUpdated(newList))
                    }
                    nInterface.nSuccess()
                } catch (_: Throwable) {
                    nInterface.nError("Не удалось загрузить list") {
                        updateList(ministryId, date)
                    }
                }
            }
        }
    }

    private fun init() {
        scope.launch(CDispatcher) {
            try {
                nInterface.nStartLoading()
                val r = journalRepository.fetchMinistryHeaderInit()
                scope.launch {
                    dispatch(Message.MinistryHeaderInited(
                        isMultiMinistry = r.isMultiMinistry,
                        pickedMinistry = r.pickedMinistry
                    ))
                }
                nInterface.nSuccess()
                updateList(ministryId = r.pickedMinistry, date = state().currentDate.second)
            } catch (_: Throwable) {
                nInterface.nError("Не удалось загрузить header") {
                    init()
                }
            }
        }
    }
}
