package home

import AuthRepository
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.arkivanov.mvikotlin.core.store.StoreFactory
import home.HomeStore.Intent
import home.HomeStore.Label
import home.HomeStore.State
import home.HomeStore.Message

class HomeStoreFactory(private val storeFactory: StoreFactory, private val authRepository: AuthRepository) {

    fun create(): HomeStore {
        return HomeStoreImpl()
    }

    private inner class HomeStoreImpl :
        HomeStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "HomeStore",
            initialState = State(name = authRepository.fetchName(), surname = authRepository.fetchSurname(), praname = authRepository.fetchPraname()),
            executorFactory = { HomeExecutor() },
            reducer = HomeReducer
        )
}