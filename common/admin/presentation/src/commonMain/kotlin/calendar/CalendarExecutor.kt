package calendar

import AdminRepository
import CDispatcher
import admin.calendar.CalendarModuleItem
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import calendar.CalendarStore.Intent
import calendar.CalendarStore.Label
import calendar.CalendarStore.State
import calendar.CalendarStore.Message
import components.networkInterface.NetworkInterface
import kotlinx.coroutines.launch

class CalendarExecutor(
    private val adminRepository: AdminRepository,
    private val nInterface: NetworkInterface
) : CoroutineExecutor<Intent, Unit, State, Message, Label>() {
    override fun executeIntent(intent: Intent) {
        when (intent) {
            Intent.Init -> init()
            Intent.SendItToServer -> send()
            is Intent.CreateModule -> createModule(intent.date)
            Intent.CloseCalendar -> dispatch(Message.CalendarClosed)
            is Intent.OpenCalendar -> dispatch(
                Message.CalendarOpened(
                    creatingHalfNum = intent.creatingHalfNum,
                    selectedModuleNum = intent.selectedModuleNum
                )
            )

            Intent.DeleteModule -> deleteModule()
            is Intent.IsSavedAnimation -> dispatch(Message.IsAnimationSaved(intent.isSaved))
        }
    }



    private fun createModule(startDate: String) {
        scope.launch(CDispatcher) {
            val newModules = state().modules.toMutableList()
            if (state().selectedModuleNum in state().modules.map { it.num }) {
                newModules.removeAt(state().selectedModuleNum!! - 1)
            }
            newModules.add(
                CalendarModuleItem(
                    num = state().selectedModuleNum!!,
                    start = startDate,
                    halfNum = state().creatingHalfNum!!
                )
            )
            scope.launch {
                dispatch(Message.ModulesUpdated(newModules))
            }
        }
    }
    private fun deleteModule() {
        scope.launch(CDispatcher) {
            val newModules = state().modules.toMutableList()
            newModules.removeLast()
            scope.launch {
                dispatch(Message.ModulesUpdated(newModules))
            }
        }
    }

    private fun init() {
        scope.launch(CDispatcher) {
            nInterface.nStartLoading()
            try {
                val r = adminRepository.fetchCalendar()
                scope.launch {
                    dispatch(Message.ModulesUpdated(r.items))
                    nInterface.nSuccess()
//                    dispatch(Message.IsAnimationSaved(true))
                }
            } catch (e: Throwable) {
                nInterface.nError("Не удалось загрузить календарь", e) {
                    init()
                }
            }
        }
    }

    private fun send() {
        scope.launch(CDispatcher) {
            nInterface.nStartLoading()
            try {
                adminRepository.updateCalendar(state().modules)
                scope.launch {
                    nInterface.nSuccess()
                    dispatch(Message.IsAnimationSaved(true))
                }
            } catch (e: Throwable) {
                nInterface.nError("Не удалось обновить календарь", e) {
                    send()
                }
            }
        }
    }
}
