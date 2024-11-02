package ministry

import CDispatcher
import JournalRepository
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import components.cAlertDialog.CAlertDialogComponent
import components.cAlertDialog.CAlertDialogStore
import components.listDialog.ListComponent
import components.listDialog.ListDialogStore
import components.networkInterface.NetworkInterface
import kotlinx.coroutines.launch
import main.school.MinistryStup
import main.school.RMinistryListReceive
import main.school.RUploadMinistryStup
import ministry.MinistryStore.Intent
import ministry.MinistryStore.Label
import ministry.MinistryStore.Message
import ministry.MinistryStore.State

class MinistryExecutor(
    private val nInterface: NetworkInterface,
    private val nUploadInterface: NetworkInterface,
    private val journalRepository: JournalRepository,
    private val ds1ListComponent: ListComponent,
    private val ds2ListComponent: ListComponent,
    private val ds3DialogComponent: CAlertDialogComponent
) : CoroutineExecutor<Intent, Unit, State, Message, Label>() {
    override fun executeIntent(intent: Intent) {
        when (intent) {
            Intent.Init -> init()
            is Intent.ChangeMinistry -> {
                dispatch(Message.MinistryChanged(intent.ministryId))
                updateList(ministryId = intent.ministryId, date = state().currentDate.second)
            }

            is Intent.ChangeDate -> {
                dispatch(Message.DateChanged(intent.date))
                updateList(ministryId = state().pickedMinistry, date = intent.date.second)
            }

            is Intent.OpenMVDEdit -> {
                when (intent.reason) {
                    "!ds1" -> {
                        ds1ListComponent.onEvent(ListDialogStore.Intent.ShowDialog)
                    }

                    "!ds2" -> {
                        ds2ListComponent.onEvent(ListDialogStore.Intent.ShowDialog)
                    }

                    "!ds3" -> {
                        dispatch(
                            Message.MVDDS3Opened(
                                custom = intent.custom,
                                stups = intent.stups
                            )
                        )
                        ds3DialogComponent.onEvent(CAlertDialogStore.Intent.ShowDialog)
                    }
                }
                dispatch(Message.MVDEditOpened(login = intent.login, reportId = intent.reportId))
            }

            is Intent.ChangeDs3Stepper -> {
                dispatch(Message.Ds3StepperChanged(intent.stups))
            }

            is Intent.ChangeDs3Custom -> {
                dispatch(Message.Ds3CustomChanged(intent.custom))
            }

            is Intent.UploadStup -> uploadStup(
                reason = intent.reason,
                login = intent.login,
                content = intent.content,
                reportId = intent.reportId,
                custom = intent.custom
            )
        }
    }

    private fun uploadStup(
        reason: String,
        login: String,
        content: String,
        reportId: Int?,
        custom: String?
    ) {
        scope.launch(CDispatcher) {
            try {
                nUploadInterface.nStartLoading()
                val newStup = MinistryStup(
                    reason = reason,
                    content = content,
                    reportId = reportId,
                    custom = custom
                )
                val newList = state().ministryList.toMutableList()
                val oldItem =
                    newList.first { it.date == state().currentDate.second && it.ministryId == state().pickedMinistry }
                val oldStup = oldItem.kids.first { it.login == login }
                    .dayStups.firstOrNull { s -> s.reportId == reportId && s.reason == reason }
                if (oldStup != newStup) {
                    print("GOGOGOOG")
                    journalRepository.uploadMinistryStup(
                        RUploadMinistryStup(
                            studentLogin = login,
                            stup = newStup,
                            date = state().currentDate.second
                        )
                    )

                    val newItem = oldItem.copy(
                        kids = oldItem.kids.map {
                            if (it.login == login) {
                                val oldDayStupContent =
                                    oldStup?.content?.toIntOrNull()
                                            ?: 0
                                val dif = -oldDayStupContent + (newStup.content.toIntOrNull() ?: 0)
                                var newKid = it.copy(
                                    dayStups = it.dayStups.map { s ->
                                        if (s.reportId == reportId && s.reason == reason) {
                                            newStup
                                        } else s
                                                               },
                                    moduleStupsCount = it.moduleStupsCount + dif,
                                    yearStupsCount = it.yearStupsCount + dif,
                                    weekStupsCount = it.weekStupsCount + dif
                                )
                                if (!newKid.dayStups.contains(newStup)) {
                                    val newDayStups = newKid.dayStups + newStup
                                    newKid = newKid.copy(dayStups = newDayStups)
                                }
                                newKid
                            } else it
                        }
                    )
                    newList.remove(oldItem)
                    newList.add(
                        newItem
                    )

                    scope.launch {
                        dispatch(Message.ListUpdated(newList))
                    }
                }
                nUploadInterface.nSuccess()
            } catch (_: Throwable) {
                nUploadInterface.nError("Не удалось upload Stup") {
                    uploadStup(
                        reason = reason,
                        login = login,
                        content = login,
                        reportId = reportId,
                        custom = custom
                    )
                }
            }
        }

    }

    private fun updateList(ministryId: String, date: String) {
        if (ministryId != "0") {
            scope.launch(CDispatcher) {
                try {
                    nInterface.nStartLoading()
                    val r = journalRepository.fetchMinistryList(
                        RMinistryListReceive(
                            date = date,
                            ministryId = ministryId
                        )
                    )
                    val newList = state().ministryList.toMutableList()
                    val oldItem = newList.firstOrNull { it.date == date && it.ministryId == ministryId }
                    newList.remove(oldItem)
                    newList.add(
                        MinistryListItem(
                            date = date,
                            ministryId = ministryId,
                            kids = r.kids
                        )
                    )
                    scope.launch {
                        dispatch(Message.ListUpdated(newList))
                    }
                    nInterface.nSuccess()
                } catch (_: Throwable) {
                    nInterface.nError("Не удалось загрузить list") {
                        updateList(ministryId, date)
                    }
                }
            }
        }
    }

    private fun init() {
        scope.launch(CDispatcher) {
            try {
                nInterface.nStartLoading()
                val r = journalRepository.fetchMinistryHeaderInit()
                scope.launch {
                    dispatch(
                        Message.MinistryHeaderInited(
                            isMultiMinistry = r.isMultiMinistry,
                            pickedMinistry = r.pickedMinistry
                        )
                    )
                }
                nInterface.nSuccess()
                updateList(ministryId = r.pickedMinistry, date = state().currentDate.second)
            } catch (_: Throwable) {
                nInterface.nError("Не удалось загрузить header") {
                    init()
                }
            }
        }
    }
}
