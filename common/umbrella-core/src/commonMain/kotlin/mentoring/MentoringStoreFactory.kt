package mentoring

import MainRepository
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.networkInterface.NetworkInterface
import mentoring.MentoringExecutor
import mentoring.MentoringReducer
import mentoring.MentoringStore
import mentoring.MentoringStore.Intent
import mentoring.MentoringStore.Label
import mentoring.MentoringStore.State

class MentoringStoreFactory(
    private val storeFactory: StoreFactory,
    private val mainRepository: MainRepository,
    private val nInterface: NetworkInterface,
    private val nPreAttendance: NetworkInterface
) {

    fun create(): MentoringStore {
        return MentoringStoreImpl()
    }

    private inner class MentoringStoreImpl :
        MentoringStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "MentoringStore",
            initialState = MentoringStore.State(),
            executorFactory = { MentoringExecutor(
                mainRepository = mainRepository,
                nInterface = nInterface,
                nPreAttendance = nPreAttendance
            ) },
            reducer = MentoringReducer
        )
}