package cabinets

import AdminRepository
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.arkivanov.mvikotlin.core.store.StoreFactory
import cabinets.CabinetsStore.Intent
import cabinets.CabinetsStore.Label
import cabinets.CabinetsStore.State
import cabinets.CabinetsStore.Message
import components.networkInterface.NetworkInterface

class CabinetsStoreFactory(
    private val storeFactory: StoreFactory,
    private val adminRepository: AdminRepository,
    private val nInterface: NetworkInterface
) {

    fun create(): CabinetsStore {
        return CabinetsStoreImpl()
    }

    private inner class CabinetsStoreImpl :
        CabinetsStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "CabinetsStore",
            initialState = State(),
            executorFactory = { CabinetsExecutor(
                adminRepository = adminRepository,
                nInterface = nInterface
            ) },
            reducer = CabinetsReducer
        )
}