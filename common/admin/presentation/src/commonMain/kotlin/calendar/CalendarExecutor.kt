package calendar

import AdminRepository
import CDispatcher
import admin.calendar.CalendarModuleItem
import admin.calendar.Holiday
import admin.calendar.RUpdateCalendarReceive
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
            is Intent.DeleteHoliday -> deleteHoliday(intent.id)
            is Intent.IsSavedAnimation -> dispatch(Message.IsAnimationSaved(intent.isSaved))


            is Intent.CreateHoliday -> createHoliday(intent.start, intent.end, intent.isForAll)
            is Intent.OpenRangePicker -> dispatch(Message.DateRangePickerOpened(intent.selectedHolidayId))
        }
    }


    private fun createHoliday(startDate: String, endDate: String, isForAll: Boolean) {
        scope.launch(CDispatcher) {
            val newHolidays = state().holidays.toMutableList()
            val previousHoliday = state().holidays.firstOrNull { it.id == state().selectedHolidayId }
            if (previousHoliday != null) {
                newHolidays.remove(previousHoliday)
            }
            newHolidays.add(
                Holiday(
                    id = state().selectedHolidayId!!,
                    start = startDate,
                    end = endDate,
                    isForAll = isForAll,
                    edYear = state().edYear
                )
            )
            scope.launch {
                dispatch(Message.HolidaysUpdated(newHolidays))
            }
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
    private fun deleteHoliday(id: Int) {
        scope.launch(CDispatcher) {
            val newHolidays = state().holidays.toMutableList()
            newHolidays.removeAll { it.id == id}
            scope.launch {
                dispatch(Message.HolidaysUpdated(newHolidays))
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
                    dispatch(Message.HolidaysUpdated(r.holidays))
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
                adminRepository.updateCalendar(RUpdateCalendarReceive(
                    items = state().modules,
                    holidays = state().holidays
                ))
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
