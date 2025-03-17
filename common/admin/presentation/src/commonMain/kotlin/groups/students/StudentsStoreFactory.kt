package groups.students

import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import groups.students.StudentsStore.Intent
import groups.students.StudentsStore.Label
import groups.students.StudentsStore.State

class StudentsStoreFactory(
    private val storeFactory: StoreFactory,
    private val executor: StudentsExecutor
) {

    fun create(): StudentsStore {
        return StudentsStoreImpl()
    }

    private inner class StudentsStoreImpl :
        StudentsStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "StudentsStore",
            initialState = State(),
            executorFactory = ::executor,
            reducer = StudentsReducer,
            bootstrapper = SimpleBootstrapper(Unit)
        )
}