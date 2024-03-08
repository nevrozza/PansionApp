package students

import AdminRepository
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import students.StudentsStore.Intent
import students.StudentsStore.Label
import students.StudentsStore.State

class StudentsStoreFactory(private val storeFactory: StoreFactory, adminRepository: AdminRepository) {

    fun create(): StudentsStore {
        return StudentsStoreImpl()
    }

    private inner class StudentsStoreImpl :
        StudentsStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "StudentsStore",
            initialState = StudentsStore.State,
            executorFactory = { StudentsExecutor() },
            reducer = StudentsReducer
        )
}