package rating

import AuthRepository
import MainRepository
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.listDialog.ListComponent
import components.networkInterface.NetworkInterface
import rating.RatingStore.Intent
import rating.RatingStore.Label
import rating.RatingStore.State
import rating.RatingStore.Message

class RatingStoreFactory(
    private val storeFactory: StoreFactory,
    private val mainRepository: MainRepository,
    private val authRepository: AuthRepository,
    private val nInterface: NetworkInterface,
    private val subjectsListComponent: ListComponent
) {

    fun create(): RatingStore {
        return RatingStoreImpl()
    }

    private inner class RatingStoreImpl :
        RatingStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "RatingStore",
            initialState = State(
                avatarId = authRepository.fetchAvatarId(),
                login = authRepository.fetchLogin()
            ),
            executorFactory = { RatingExecutor(
                mainRepository = mainRepository,
                nInterface = nInterface,
                subjectsListComponent = subjectsListComponent
            ) },
            reducer = RatingReducer
        )
}