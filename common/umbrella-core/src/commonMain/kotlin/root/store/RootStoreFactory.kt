package root.store

import AuthRepository
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.networkInterface.NetworkInterface
import home.HomeComponent
import root.RootComponent
import root.store.RootStore.Intent
import root.store.RootStore.Label
import root.store.RootStore.State
import root.store.RootStore.Message

class RootStoreFactory(
    private val storeFactory: StoreFactory,
    private val isBottomBarShowing: Boolean,
    private val currentScreen: RootComponent.Config,
//    private val role: String,
//    private val moderation: String,
    private val authRepository: AuthRepository,
    private val checkNInterface: NetworkInterface,
    private val gotoHome: () -> Unit
) {

    fun create(): RootStore {
        return RootStoreImpl()
    }

    private inner class RootStoreImpl :
        RootStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "RootStore",
            initialState = State(isBottomBarShowing = isBottomBarShowing, currentScreen = currentScreen, role = authRepository.fetchRole(), moderation = authRepository.fetchModeration(), isGreetingsShowing = authRepository.isUserLoggedIn()),
            executorFactory = { RootExecutor(authRepository = authRepository, checkNInterface = checkNInterface, gotoHome = gotoHome) },
            reducer = RootReducer
        )


}