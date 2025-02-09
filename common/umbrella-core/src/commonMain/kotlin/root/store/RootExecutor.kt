package root.store

import AuthRepository
import JournalRepository
import RFetchGroupDataReceive
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import components.networkInterface.NetworkInterface
import deviceSupport.launchIO
import deviceSupport.withMain
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import root.store.RootStore.Intent
import root.store.RootStore.Label
import root.store.RootStore.Message
import root.store.RootStore.State
import webload.RFetchUserDataReceive

class RootExecutor(
    val authRepository: AuthRepository,
    private val journalRepository: JournalRepository,
    val checkNInterface: NetworkInterface,
    private val gotoHome: () -> Unit
) : CoroutineExecutor<Intent, Unit, State, Message, Label>() {

    override fun executeIntent(intent: Intent) {
        when (intent) {
            is Intent.HideGreetings -> hideGreetings(intent.time)
            is Intent.UpdatePermissions -> updatePermissions(
                intent.role,
                intent.moderation,
                intent.birthday
            )

            Intent.CheckConnection -> checkConnection()
            is Intent.ChangeTokenValidationStatus -> dispatch(
                Message.TokenValidationStatusChanged(
                    intent.isTokenValid
                )
            )

            is Intent.DeleteStart -> dispatch(Message.StartFetched(null, null, null, null))
            is Intent.FetchStartUser -> fetchStartUser(
                login = intent.login,
                routing = intent.routing
            )

            is Intent.FetchStartGroup -> fetchStartGroup(intent.groupId)
            is Intent.FetchStartReport -> fetchStartReport(intent.reportId)
        }
    }


    private fun fetchStartReport(reportId: Int) {
        dispatch(Message.StartIsNeeded)
        scope.launchIO {
            try {
                val r = journalRepository.fetchFullReportData(reportId)

                withMain {
                    dispatch(
                        Message.StartFetched(
                            rUser = null,
                            routing = QuickRoutings.LessonReport,
                            rGroup = null,
                            rReportData = r
                        )
                    )
                }

            } catch (_: Throwable) {
                withMain {
                    dispatch(
                        Message.StartFetched(
                            rUser = null,
                            routing = QuickRoutings.LessonReport,
                            rGroup = null,
                            rReportData = null
                        )
                    )
                }
            }
        }
    }

    private fun fetchStartGroup(groupId: Int) {
        dispatch(Message.StartIsNeeded)
        scope.launchIO {
            try {
                val r = authRepository.fetchGroupData(
                    RFetchGroupDataReceive(groupId)
                )

                withMain {
                    dispatch(
                        Message.StartFetched(
                            rUser = null,
                            routing = QuickRoutings.HomeAllGroupMarks,
                            rGroup = r,
                            rReportData = null
                        )
                    )
                }

            } catch (_: Throwable) {
                withMain {
                    dispatch(
                        Message.StartFetched(
                            rUser = null,
                            routing = QuickRoutings.HomeAllGroupMarks,
                            rGroup = null,
                            rReportData = null
                        )
                    )
                }
            }
        }
    }

    private fun fetchStartUser(login: String, routing: QuickRoutings) {
        dispatch(Message.StartIsNeeded)
        scope.launchIO {
            try {
                val r = authRepository.fetchUserData(
                    RFetchUserDataReceive(
                        login = login
                    )
                )

                withMain {
                    dispatch(
                        Message.StartFetched(
                            rUser = r,
                            routing = routing,
                            rGroup = null,
                            rReportData = null
                        )
                    )
                }

            } catch (_: Throwable) {
                withMain {
                    dispatch(
                        Message.StartFetched(
                            rUser = null,
                            routing = routing,
                            rGroup = null,
                            rReportData = null
                        )
                    )
                }
            }
        }
    }

    private fun updatePermissions(role: String, moderation: String, birthday: String) {

        dispatch(
            Message.PermissionsUpdated(
                role, moderation, birthday
            )
        )

    }

    private fun checkConnection() {
        checkNInterface.nStartLoading()
        scope.launchIO {
            try {
                val r = authRepository.checkConnection()

                withMain {
                    checkNInterface.nSuccess()
                    dispatch(Message.VersionFetched(r.version))
                    if (r.isTokenValid) {
                        updatePermissions(r.role, r.moderation, r.birthday)
                        authRepository.updateAfterFetch(r)

                        gotoHome()
                    } else {
                        dispatch(Message.TokenValidationStatusChanged(false))
                    }
                }

            } catch (e: Throwable) {
                checkNInterface.nError("Не удалось подключиться к серверу", e) {
                    checkConnection()
                }
            }
        }
    }

    private fun hideGreetings(time: Long) {
        scope.launch {
            delay(time)
            dispatch(Message.GreetingsHided)
        }
    }
}
