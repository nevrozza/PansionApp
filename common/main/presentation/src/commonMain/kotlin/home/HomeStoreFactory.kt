package home

import AuthRepository
import MainRepository
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.networkInterface.NetworkInterface
import home.HomeStore.Intent
import home.HomeStore.Label
import home.HomeStore.State
import home.HomeStore.Message

class HomeStoreFactory(
    private val storeFactory: StoreFactory,
    private val mainRepository: MainRepository,
    private val authRepository: AuthRepository,
    private val quickTabNInterface: NetworkInterface,
    private val teacherNInterface: NetworkInterface,
    private val gradesNInterface: NetworkInterface,
    private val scheduleNInterface: NetworkInterface,
) {

    fun create(): HomeStore {
        return HomeStoreImpl()
    }

    private inner class HomeStoreImpl :
        HomeStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "HomeStore",
            initialState = State(
                avatarId = authRepository.fetchAvatarId(),
                login = authRepository.fetchLogin(),
                name = authRepository.fetchName(),
                surname = authRepository.fetchSurname(),
                praname = authRepository.fetchPraname(),
                role = authRepository.fetchRole()
            ),
            executorFactory = { HomeExecutor(
                authRepository = authRepository,
                mainRepository = mainRepository,
                quickTabNInterface = quickTabNInterface,
                teacherNInterface = teacherNInterface,
                gradesNInterface = gradesNInterface,
                scheduleNInterface = scheduleNInterface
            ) },
            reducer = HomeReducer
        )
}