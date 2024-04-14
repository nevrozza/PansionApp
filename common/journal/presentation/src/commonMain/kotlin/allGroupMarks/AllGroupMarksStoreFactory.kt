package allGroupMarks

import JournalRepository
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.arkivanov.mvikotlin.core.store.StoreFactory
import allGroupMarks.AllGroupMarksStore.Intent
import allGroupMarks.AllGroupMarksStore.Label
import allGroupMarks.AllGroupMarksStore.State
import allGroupMarks.AllGroupMarksStore.Message
import components.networkInterface.NetworkInterface

class AllGroupMarksStoreFactory(
    private val storeFactory: StoreFactory,
    private val groupId: Int,
    private val subjectId: Int,
    private val groupName: String,
    private val subjectName: String,
    private val nInterface: NetworkInterface,
    private val journalRepository: JournalRepository
) {

    fun create(): AllGroupMarksStore {
        return AllGroupMarksStoreImpl()
    }

    private inner class AllGroupMarksStoreImpl :
        AllGroupMarksStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "AllGroupMarksStore",
            initialState = AllGroupMarksStore.State(
                groupId = groupId,
                groupName = groupName,
                subjectId = subjectId,
                subjectName = subjectName,
            ),
            executorFactory = { AllGroupMarksExecutor(
                journalRepository = journalRepository,
                nInterface = nInterface
            ) },
            reducer = AllGroupMarksReducer
        )
}