package dnevnikRuMarks

import JournalRepository
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.networkInterface.NetworkInterface
import dnevnikRuMarks.DnevnikRuMarkStore.Intent
import dnevnikRuMarks.DnevnikRuMarkStore.Label
import dnevnikRuMarks.DnevnikRuMarkStore.State
import dnevnikRuMarks.DnevnikRuMarkStore.Message

class DnevnikRuMarkStoreFactory(
    private val storeFactory: StoreFactory,
    private val login: String,
    private val nInterface: NetworkInterface,
    private val journalRepository: JournalRepository
) {

    fun create(): DnevnikRuMarkStore {
        return DnevnikRuMarkStoreImpl()
    }

    private inner class DnevnikRuMarkStoreImpl :
        DnevnikRuMarkStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "DnevnikRuMarkStore",
            initialState = DnevnikRuMarkStore.State(studentLogin = login),
            executorFactory = { DnevnikRuMarkExecutor(journalRepository = journalRepository, nInterface = nInterface) },
            reducer = DnevnikRuMarkReducer
        )
}