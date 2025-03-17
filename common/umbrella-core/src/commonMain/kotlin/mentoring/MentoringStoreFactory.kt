package mentoring

import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import mentoring.MentoringStore.Intent
import mentoring.MentoringStore.Label
import mentoring.MentoringStore.State

class MentoringStoreFactory(
    private val storeFactory: StoreFactory,
    private val executor: MentoringExecutor
) {

    fun create(): MentoringStore {
        return MentoringStoreImpl()
    }

    private inner class MentoringStoreImpl :
        MentoringStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "MentoringStore",
            initialState = State(),
            executorFactory = ::executor,
            reducer = MentoringReducer,
            bootstrapper = SimpleBootstrapper(Unit)
        )
}