
package studentLines

import JournalRepository
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.networkInterface.NetworkInterface
import studentLines.StudentLinesStore.Intent
import studentLines.StudentLinesStore.Label
import studentLines.StudentLinesStore.State
import studentLines.StudentLinesStore.Message

class StudentLinesStoreFactory(
    private val storeFactory: StoreFactory,
    private val login: String,
    private val journalRepository: JournalRepository,
    private val nInterface: NetworkInterface,
) {

    fun create(): StudentLinesStore {
        return StudentLinesStoreImpl()
    }

    private inner class StudentLinesStoreImpl :
        StudentLinesStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "StudentLinesStore",
            initialState = StudentLinesStore.State(login = login),
            executorFactory = { StudentLinesExecutor(
                journalRepository = journalRepository,
                nInterface = nInterface
            ) },
            reducer = StudentLinesReducer
        )
}