package mentors

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.arkivanov.mvikotlin.core.store.StoreFactory
import mentors.MentorsStore.Intent
import mentors.MentorsStore.Label
import mentors.MentorsStore.State
import mentors.MentorsStore.Message

class MentorsStoreFactory(private val storeFactory: StoreFactory) {

    fun create(): MentorsStore {
        return MentorsStoreImpl()
    }

    private inner class MentorsStoreImpl :
        MentorsStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "MentorsStore",
            initialState = MentorsStore.State,
            executorFactory = { MentorsExecutor() },
            reducer = MentorsReducer
        )
}