
package formRating

import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import formRating.FormRatingStore.Intent
import formRating.FormRatingStore.Label
import formRating.FormRatingStore.State

class FormRatingStoreFactory(
    private val storeFactory: StoreFactory,
    private val state: State,
    private val executor: FormRatingExecutor
) {

    fun create(): FormRatingStore {
        return FormRatingStoreImpl()
    }

    private inner class FormRatingStoreImpl :
        FormRatingStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "FormRatingStore",
            initialState = state,
            executorFactory = ::executor,
            reducer = FormRatingReducer,
            bootstrapper = SimpleBootstrapper(Unit)
        )
}