package profile

import AuthRepository
import auth.RChangeStatsSettingsReceive
import auth.RCheckGIASubjectReceive
import auth.RFetchAboutMeReceive
import auth.StatsSettingsDTO
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import components.networkInterface.NetworkInterface
import deviceSupport.launchIO
import deviceSupport.withMain
import di.Inject
import profile.ProfileStore.Intent
import profile.ProfileStore.Label
import profile.ProfileStore.Message
import profile.ProfileStore.State

class ProfileExecutor(
    private val authRepository: AuthRepository = Inject.instance(),
    private val nAvatarInterface: NetworkInterface,
    private val nAboutMeInterface: NetworkInterface,
    private val changeAvatarOnMain: (Int) -> Unit
) : CoroutineExecutor<Intent, Unit, State, Message, Label>() {
    override fun executeAction(action: Unit) {
        init()
    }


    override fun executeIntent(intent: Intent) {
        when (intent) {
            is Intent.ChangeTab -> dispatch(Message.TabChanged(intent.index))
            is Intent.SetNewAvatarId -> dispatch(Message.NewAvatarIdChanged(intent.avatarId))
            is Intent.SaveAvatarId -> saveAvatarId(intent.avatarId, price = intent.price)
            Intent.Init -> init()
            is Intent.ClickOnGIASubject -> clickOnGia(subjectId = intent.subjectId, isChecked = intent.isChecked)
            is Intent.ChangeStatsSettings -> changeStatsSettings()
        }
    }

    private fun changeStatsSettings() {
        val newStatsOpened = !(state().isStatsOpened ?: false)
        scope.launchIO {
            try {
                nAboutMeInterface.nStartLoading()
                authRepository.changeStatsSettings(RChangeStatsSettingsReceive(
                    dto = StatsSettingsDTO(
                        login = state().studentLogin,
                        isOpened = newStatsOpened
                    )
                ))
                withMain {
                    dispatch(Message.StatsSettingsChanged(newStatsOpened))
                }
            } catch (_: Throwable) {

            }
        }
    }

    private fun clickOnGia(subjectId: Int, isChecked: Boolean) {
        scope.launchIO {
            try {
                authRepository.checkGIASubject(RCheckGIASubjectReceive(subjectId = subjectId, isChecked = isChecked, login = state().studentLogin))
                val newList = state().giaSubjects.toMutableList()
                if(isChecked) {
                    newList.add(subjectId)
                } else {
                    newList.remove(element = subjectId)
                }
                withMain {
                    dispatch(Message.GIASubjectsUpdated(newList))
                }
            } catch (_: Throwable) {

            }
        }
    }

    private fun init() {
        scope.launchIO {
            nAboutMeInterface.nStartLoading()
            try {
                val aboutMe = authRepository.fetchAboutMe(
                    RFetchAboutMeReceive(
                        studentLogin = state().studentLogin,
                        edYear = state().edYear
                    )
                )
                withMain {
                    dispatch(Message.AboutMeUpdated(
                        form = aboutMe.form,
                        groups = aboutMe.groups,
                        subjects = aboutMe.subjects,
                        teachers = aboutMe.teachers,
                        likes = aboutMe.likes,
                        dislikes = aboutMe.dislikes,
                        giaSubjects = aboutMe.giaSubjects,
                        ministryId = aboutMe.ministryId,
                        ministryLvl = aboutMe.ministryLevel,
                        pansCoins = aboutMe.pansCoins,
                        avatars = aboutMe.avatars,
                        isStatsOpened = aboutMe.isStatsOpened
                    ))
                    nAboutMeInterface.nSuccess()
                }
            } catch (e: Throwable) {
                with(nAboutMeInterface) {
                    nError("Не удалось загрузить 'обо мне' =/", e, onFixErrorClick = {
                        init()
                    })
                }
            }
        }
    }

    private fun saveAvatarId(avatarId: Int, price: Int) {
        scope.launchIO {
            nAvatarInterface.nStartLoading()
            try {
                authRepository.changeAvatarId(avatarId = avatarId, price = price)
                nAvatarInterface.nSuccess()
                authRepository.saveAvatarId(avatarId = avatarId)
                withMain {
                    changeAvatarOnMain(avatarId)
                    dispatch(Message.AvatarIdSaved(price = price, avatarId = avatarId))
                }
            } catch (e: Throwable) {
                with(nAvatarInterface) {
                    nError("Что-то пошло не так =/", e, onFixErrorClick = {
                        goToNone()
                    })
                }
            }
        }
    }
}
