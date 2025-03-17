
package studentLines

import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.networkInterface.NetworkInterface
import studentLines.StudentLinesStore.Intent
import studentLines.StudentLinesStore.Label
import studentLines.StudentLinesStore.State

class StudentLinesStoreFactory(
    private val storeFactory: StoreFactory,
    private val login: String,
    private val nInterface: NetworkInterface,
) {

    fun create(): StudentLinesStore {
        return StudentLinesStoreImpl()
    }

    private inner class StudentLinesStoreImpl :
        StudentLinesStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "StudentLinesStore",
            initialState = State(login = login),
            executorFactory = { StudentLinesExecutor(
                nInterface = nInterface
            ) },
            reducer = StudentLinesReducer,
            bootstrapper = SimpleBootstrapper(Unit)
        )
}