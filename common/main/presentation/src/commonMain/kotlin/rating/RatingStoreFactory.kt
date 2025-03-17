package rating

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import rating.RatingStore.Intent
import rating.RatingStore.Label
import rating.RatingStore.State

class RatingStoreFactory(
    private val storeFactory: StoreFactory,
    private val state: State,
    private val executor: RatingExecutor
) {

    fun create(): RatingStore {
        return RatingStoreImpl()
    }

    private inner class RatingStoreImpl :
        RatingStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "RatingStore",
            initialState = state,
            executorFactory = ::executor,
            reducer = RatingReducer
        )
}