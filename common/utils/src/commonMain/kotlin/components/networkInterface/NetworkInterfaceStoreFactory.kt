package components.networkInterface

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.networkInterface.NetworkInterfaceStore.Intent
import components.networkInterface.NetworkInterfaceStore.Label
import components.networkInterface.NetworkInterfaceStore.State
import components.networkInterface.NetworkInterfaceStore.Message

class NetworkInterfaceStoreFactory(private val storeFactory: StoreFactory) {

    fun create(): NetworkInterfaceStore {
        return NetworkInterfaceStoreImpl()
    }

    private inner class NetworkInterfaceStoreImpl :
        NetworkInterfaceStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "NetworkInterfaceStore",
            initialState = NetworkInterfaceStore.State,
            executorFactory = { NetworkInterfaceExecutor() },
            reducer = NetworkInterfaceReducer
        )
}