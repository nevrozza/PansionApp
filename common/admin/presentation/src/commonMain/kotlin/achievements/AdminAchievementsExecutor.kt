package achievements

import AdminRepository
import CDispatcher
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import achievements.AdminAchievementsStore.Intent
import achievements.AdminAchievementsStore.Label
import achievements.AdminAchievementsStore.State
import achievements.AdminAchievementsStore.Message
import components.cBottomSheet.CBottomSheetComponent
import components.cBottomSheet.CBottomSheetStore
import components.networkInterface.NetworkInterface
import components.networkInterface.NetworkState
import kotlinx.coroutines.launch
import server.ExtraSubjectsId
import server.getDate

class AdminAchievementsExecutor(
    private val adminRepository: AdminRepository,
    private val bottomSheetComponent: CBottomSheetComponent,
    private val hugeBottomSheetComponent: CBottomSheetComponent,
    private val editBottomSheetComponent: CBottomSheetComponent,
    private val nInterface: NetworkInterface,
    private val nBSInterface: NetworkInterface,
) : CoroutineExecutor<Intent, Unit, State, Message, Label>() {
    override fun executeIntent(intent: Intent) {
        when (intent) {
            Intent.Init -> init()
            Intent.OpenCreateBS -> scope.launch {
                bottomSheetComponent.onEvent(CBottomSheetStore.Intent.ShowSheet)
                if (state().bsId != null || (nBSInterface.networkModel.value.state == NetworkState.None && state().bsId == null && state().bsStudentLogin.isEmpty())) {
                    dispatch(Message.BSInit(
                        id = null,
                        studentLogin = "",
                        date = getDate(),
                        text = "",
                        showDate = "",
                        subjectId = null,
                        stups = 0
                    ))
                }
            }
            is Intent.OpenAddBS -> scope.launch {
                bottomSheetComponent.onEvent(CBottomSheetStore.Intent.ShowSheet)
                dispatch(Message.BSInit(
                    id = null,
                    studentLogin = "",
                    date = intent.date,
                    text = intent.text,
                    showDate = intent.showDate,
                    subjectId = intent.subjectId,
                    stups = intent.stups
                ))
            }
            is Intent.OpenHugeBS -> scope.launch {
                hugeBottomSheetComponent.onEvent(CBottomSheetStore.Intent.ShowSheet)
                dispatch(Message.BSInit(
                    id = null,
                    studentLogin = "",
                    date = intent.date,
                    text = intent.text,
                    showDate = intent.showDate,
                    subjectId = null,
                    stups = 0,
                    oldDate = intent.oldDate,
                    oldText = intent.oldText,
                    oldShowDate = intent.oldShowDate
                ))
            }

            is Intent.OpenEditBS -> scope.launch {
                editBottomSheetComponent.onEvent(CBottomSheetStore.Intent.ShowSheet)
                dispatch(Message.BSInit(
                    id = intent.id,
                    studentLogin = intent.studentLogin,
                    subjectId = intent.subjectId,
                    stups = intent.stups,
                    date = intent.date,
                    showDate = "",
                    text = intent.text
                ))
            }


            is Intent.ChangeStudentLogin -> dispatch(Message.StudentLoginChanged(intent.login))
            is Intent.ChangeDate -> dispatch(Message.DateChanged(intent.date))
            is Intent.ChangeShowDate -> dispatch(Message.ShowDateChanged(intent.date))
            is Intent.ChangeStups -> dispatch(Message.StupsChanged(intent.stups))
            is Intent.ChangeSubjectId -> dispatch(Message.SubjectIdChanged(intent.id))
            is Intent.ChangeText -> dispatch(Message.TextChanged(intent.text))
            Intent.CreateAchievement -> createAchievement()
            Intent.EditAchievement -> editAchievement()
            Intent.UpdateGroupAchievement -> updateGroupAchievement()
            Intent.DeleteAchievement -> deleteAchievement()
        }
    }
    private fun editAchievement() {
        scope.launch(CDispatcher) {
            nBSInterface.nStartLoading()
            try {
                val r = adminRepository.editAchievement(
                    REditAchievementReceive(
                        id = state().bsId!!,
                        studentLogin = state().bsStudentLogin,
                        subjectId = state().bsSubjectId!!,
                        stups = state().bsStups
                    )
                )
                scope.launch {
                    dispatch(
                        Message.Inited(
                            achievements = r.list,
                            students = state().students,
                            subjects = state().subjects
                        )
                    )
                    nBSInterface.nSuccess()
                    bottomSheetComponent.onEvent(CBottomSheetStore.Intent.HideSheet)
                    hugeBottomSheetComponent.onEvent(CBottomSheetStore.Intent.HideSheet)
                    editBottomSheetComponent.onEvent(CBottomSheetStore.Intent.HideSheet)
                }
            } catch (e: Throwable) {
                nBSInterface.nError(
                    "Что-то пошло не так", e
                ) {
                    nBSInterface.goToNone()
                }
            }
        }
    }
    private fun deleteAchievement() {
        scope.launch(CDispatcher) {
            nBSInterface.nStartLoading()
            try {
                val r = adminRepository.deleteAchievement(
                    RDeleteAchievementReceive(
                        state().bsId!!
                    )
                )
                scope.launch {
                    dispatch(
                        Message.Inited(
                            achievements = r.list,
                            students = state().students,
                            subjects = state().subjects
                        )
                    )
                    nBSInterface.nSuccess()
                    bottomSheetComponent.onEvent(CBottomSheetStore.Intent.HideSheet)
                    hugeBottomSheetComponent.onEvent(CBottomSheetStore.Intent.HideSheet)
                    editBottomSheetComponent.onEvent(CBottomSheetStore.Intent.HideSheet)
                }
            } catch (e: Throwable) {
                nBSInterface.nError(
                    "Что-то пошло не так", e
                ) {
                    nBSInterface.goToNone()
                }
            }
        }
    }
    private fun updateGroupAchievement() {
        scope.launch(CDispatcher) {
            nBSInterface.nStartLoading()
            try {
                val r = adminRepository.updateGroupAchievement(
                    RUpdateGroupOfAchievementsReceive(
                        oldText = state().bsOldText,
                        oldDate = state().bsOldDate,
                        oldShowDate = state().bsOldShowDate,
                        newText = state().bsText,
                        newDate = state().bsDate,
                        newShowDate = state().bsShowDate
                    )
                )
                scope.launch {
                    dispatch(
                        Message.Inited(
                            achievements = r.list,
                            students = state().students,
                            subjects = state().subjects
                        )
                    )
                    nBSInterface.nSuccess()
                    bottomSheetComponent.onEvent(CBottomSheetStore.Intent.HideSheet)
                    hugeBottomSheetComponent.onEvent(CBottomSheetStore.Intent.HideSheet)
                    editBottomSheetComponent.onEvent(CBottomSheetStore.Intent.HideSheet)
                }
            } catch (e: Throwable) {
                nBSInterface.nError(
                    "Что-то пошло не так", e
                ) {
                    nBSInterface.goToNone()
                }
            }
        }
    }


    private fun createAchievement() {
        scope.launch(CDispatcher) {
            nBSInterface.nStartLoading()
            try {
                val r = adminRepository.createAchievement(
                    RCreateAchievementReceive(
                        AchievementsDTO(
                            id = 0,
                            studentLogin = state().bsStudentLogin,
                            creatorLogin = "",
                            date = state().bsDate,
                            text = state().bsText,
                            showDate = state().bsShowDate,
                            subjectId = state().bsSubjectId!!,
                            stups = state().bsStups
                        )
                    )
                )
                scope.launch {
                    dispatch(
                        Message.Inited(
                            achievements = r.list,
                            students = state().students,
                            subjects = state().subjects
                        )
                    )
                    nBSInterface.nSuccess()
                    bottomSheetComponent.onEvent(CBottomSheetStore.Intent.HideSheet)
                    hugeBottomSheetComponent.onEvent(CBottomSheetStore.Intent.HideSheet)
                    editBottomSheetComponent.onEvent(CBottomSheetStore.Intent.HideSheet)
                }
            } catch (e: Throwable) {
                nBSInterface.nError(
                    "Что-то пошло не так", e
                ) {
                    nBSInterface.goToNone()
                }
            }
        }
    }

    private fun init() {
        scope.launch(CDispatcher) {
            nInterface.nStartLoading()
            try {
                val r = adminRepository.fetchAllAchievements()
                scope.launch {
                    dispatch(
                        Message.Inited(
                            achievements = r.list,
                            students = r.students ?: listOf(),
                            subjects = mapOf(ExtraSubjectsId.mvd to "Дисциплина", ExtraSubjectsId.social to "Общественная работа", ExtraSubjectsId.creative to "Творчество") + r.subjects //mvd-2 social-3 creative-3
                        )
                    )
                    nInterface.nSuccess()
                }
            } catch (e: Throwable) {

                nInterface.nError(
                    "Что-то пошло не так", e
                ) {
                    init()
                }
            }
        }
    }
}
