package cabinets

import AdminRepository
import admin.cabinets.CabinetItem
import cabinets.CabinetsStore.Intent
import cabinets.CabinetsStore.Label
import cabinets.CabinetsStore.Message
import cabinets.CabinetsStore.State
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import components.networkInterface.NetworkInterface
import deviceSupport.launchIO
import deviceSupport.withMain
import di.Inject
import kotlinx.coroutines.isActive

class CabinetsExecutor(
    private val adminRepository: AdminRepository = Inject.instance(),
    private val nInterface: NetworkInterface,
) : CoroutineExecutor<Intent, Unit, State, Message, Label>() {

    override fun executeAction(action: Unit) {
        init()
    }

    override fun executeIntent(intent: Intent) {
        when (intent) {
            Intent.Init -> init()
            Intent.SendItToServer -> sendIt()
            is Intent.UpdateCabinet -> updateCabinet(login = intent.login, cabinet = intent.cabinet)
        }
    }

    private fun sendIt() {
        scope.launchIO {
            nInterface.nStartLoading()
            try {
                adminRepository.updateCabinets(state().cabinets)
                withMain {
                    nInterface.nSuccess()
                }
            } catch (e: Throwable) {
                nInterface.nError(
                    "Не удалось отправить на сервер", e
                ) { sendIt() }
            }

        }
    }

    private fun updateCabinet(login: String, cabinet: Int) {
        scope.launchIO {
            try {
                val newList = state().cabinets.toMutableList()
                val oldCabinet = newList.firstOrNull { it.login == login }

                if (cabinet != 0) {
                    if (oldCabinet == null) {
                        newList.add(CabinetItem(login = login, cabinet = cabinet))
                    } else {
                        newList.remove(oldCabinet)
                        newList.add(CabinetItem(login = login, cabinet = cabinet))
                    }
                } else {
                    newList.remove(oldCabinet)
                }
                withMain {
                    dispatch(Message.ListUpdated(newList))
                }
            } catch (_: Throwable) {
            }
        }
    }

    private fun init() {
        scope.launchIO {
            nInterface.nStartLoading()
            try {
                val r = adminRepository.fetchCabinets()
                val tR = adminRepository.fetchAllTeachers().teachers.filter { isActive }
                withMain {
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
                    "Что-то пошло не так", e
                ) { init() }

            }

        }
    }
}

