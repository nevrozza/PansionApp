package students

import AdminRepository
import asValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import di.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import users.UsersStore

class StudentsComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    private val output: (Output) -> Unit
) : ComponentContext by componentContext {
    //    private val settingsRepository: SettingsRepository = Inject.instance()
    private val adminRepository: AdminRepository = Inject.instance()
    private val studentsStore =
        instanceKeeper.getStore {
            StudentsStoreFactory(
                storeFactory = storeFactory,
                adminRepository = adminRepository
//                authRepository = authRepository
            ).create()
        }

    private val backCallback = BackCallback {
        onOutput(Output.BackToAdmin)
    }


    init {
        backHandler.register(backCallback)    }

    val model = studentsStore.asValue()

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<StudentsStore.State> = studentsStore.stateFlow

    fun onEvent(event: StudentsStore.Intent) {
        studentsStore.accept(event)
    }

    private fun onOutput(output: Output) {
        output(output)
    }

    sealed class Output {
        data object BackToAdmin : Output()
    }
}