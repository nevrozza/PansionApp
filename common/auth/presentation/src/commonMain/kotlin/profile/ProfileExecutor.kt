package profile

import AuthRepository
import CDispatcher
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import components.networkInterface.NetworkInterface
import kotlinx.coroutines.launch
import profile.ProfileStore.Intent
import profile.ProfileStore.Label
import profile.ProfileStore.State
import profile.ProfileStore.Message

class ProfileExecutor(
    private val authRepository: AuthRepository,
    private val nAvatarInterface: NetworkInterface,
    private val nAboutMeInterface: NetworkInterface,
    private val changeAvatarOnMain: (Int) -> Unit
) : CoroutineExecutor<Intent, Unit, State, Message, Label>() {
    override fun executeIntent(intent: Intent) {
        when (intent) {
            is Intent.ChangeTab -> dispatch(Message.TabChanged(intent.index))
            is Intent.SetNewAvatarId -> dispatch(Message.NewAvatarIdChanged(intent.avatarId))
            Intent.SaveAvatarId -> saveAvatarId()
            Intent.Init -> init()
        }
    }

    private fun init() {
        scope.launch(CDispatcher) {
            nAboutMeInterface.nStartLoading()
            try {
                val aboutMe = authRepository.fetchAboutMe(state().studentLogin)
                scope.launch {
                    dispatch(Message.AboutMeUpdated(
                        form = aboutMe.form,
                        groups = aboutMe.groups,
                        subjects = aboutMe.subjects,
                        teachers = aboutMe.teachers
                    ))
                    nAboutMeInterface.nSuccess()
                }
            } catch (_: Throwable) {
                with(nAboutMeInterface) {
                    nError("Не удалось загрузить 'обо мне' =/", onFixErrorClick = {
                        init()
                    })
                }
            }
        }
    }

    private fun saveAvatarId() {
        scope.launch(CDispatcher) {
            nAvatarInterface.nStartLoading()
            try {
                println(state().newAvatarId)
                authRepository.changeAvatarId(avatarId = state().newAvatarId)
                nAvatarInterface.nSuccess()
                authRepository.saveAvatarId(avatarId = state().newAvatarId)
                scope.launch {
                    changeAvatarOnMain(state().newAvatarId)
                    dispatch(Message.AvatarIdSaved)
                }
            } catch (e: Throwable) {
                with(nAvatarInterface) {
                    nError("Что-то пошло не так =/", onFixErrorClick = {
                        goToNone()
                    })
                }
                println(e)
            }
        }
    }
}
