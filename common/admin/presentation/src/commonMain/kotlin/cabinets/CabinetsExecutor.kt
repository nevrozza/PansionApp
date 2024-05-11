package cabinets

import AdminRepository
import CDispatcher
import admin.cabinets.CabinetItem
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import cabinets.CabinetsStore.Intent
import cabinets.CabinetsStore.Label
import cabinets.CabinetsStore.State
import cabinets.CabinetsStore.Message
import components.networkInterface.NetworkInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class CabinetsExecutor(
    private val adminRepository: AdminRepository,
    private val nInterface: NetworkInterface,
) : CoroutineExecutor<Intent, Unit, State, Message, Label>() {
    override fun executeIntent(intent: Intent) {
        when (intent) {
            Intent.Init -> init()
            Intent.SendItToServer -> sendIt()
            is Intent.UpdateCabinet -> updateCabinet(login = intent.login, cabinet = intent.cabinet)
        }
    }

    private fun sendIt() {
        scope.launch() {
            nInterface.nStartLoading()
            try {
                adminRepository.updateCabinets(state().cabinets)
                nInterface.nSuccess()
            } catch (e: Throwable) {
                nInterface.nError(
                    "Не удалось отправить на сервер",
                ) { sendIt() }
            }

        }
    }

    private fun updateCabinet(login: String, cabinet: Int) {
        scope.launch {
            try {
                val newList = state().cabinets.toMutableList()
                val oldCabinet = newList.firstOrNull { it.login == login }

                if(cabinet != 0) {
                    if (oldCabinet == null) {
                        newList.add(CabinetItem(login = login, cabinet = cabinet))
                    } else {
                        newList.remove(oldCabinet)
                        newList.add(CabinetItem(login = login, cabinet = cabinet))
                    }
                } else {
                    newList.remove(oldCabinet)
                }
                dispatch(Message.ListUpdated(newList))
            } catch (_: Throwable) {}
        }
    }
    private fun init() {
        scope.launch(CDispatcher) {
            nInterface.nStartLoading()
            try {
                val r = adminRepository.fetchCabinets()
                val tR = adminRepository.fetchAllTeachers().teachers.filter { isActive }
                scope.launch {
                    dispatch(
                        Message.TeachersInited(tR)
                    )
                    dispatch(
                        Message.ListUpdated(r.cabinets)
                    )
                    nInterface.nSuccess()
                }
            } catch (e: Throwable) {
                nInterface.nError(
                    "Что-то пошло не так",
                ) { init() }
            }

        }
    }
}

