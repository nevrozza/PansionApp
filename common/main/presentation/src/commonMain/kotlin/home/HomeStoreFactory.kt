package home

import JournalRepository
import MainRepository
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.networkInterface.NetworkInterface
import home.HomeStore.Intent
import home.HomeStore.Label
import home.HomeStore.State
import journal.JournalComponent
import server.Moderation

class HomeStoreFactory(
    private val storeFactory: StoreFactory,
    private val mainRepository: MainRepository,
    private val quickTabNInterface: NetworkInterface,
    private val teacherNInterface: NetworkInterface,
    private val gradesNInterface: NetworkInterface,
    private val scheduleNInterface: NetworkInterface,
    private val journalComponent: JournalComponent?,
    private val avatarId: Int,
    private val login: String,
    private val name: String,
    private val surname: String,
    private val praname: String,
    private val role: String,
    private val moderation: String,
    private val isParent: Boolean,
    private val journalRepository: JournalRepository,
) {

    fun create(): HomeStore {
        return HomeStoreImpl()
    }

    private inner class HomeStoreImpl :
        HomeStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "HomeStore",
            initialState = State(
                avatarId = avatarId,
                login = login,
                name = name,
                surname = surname,
                praname = praname,
                role = role,
                isParent = isParent,
                isMentor = moderation in listOf(Moderation.MENTOR, Moderation.BOTH),
                isModer = moderation in listOf(Moderation.MODERATOR, Moderation.BOTH)
            ),
            executorFactory = { HomeExecutor(
                mainRepository = mainRepository,
                journalRepository = journalRepository,
                quickTabNInterface = quickTabNInterface,
                teacherNInterface = teacherNInterface,
                gradesNInterface = gradesNInterface,
                scheduleNInterface = scheduleNInterface,
                journalComponent = journalComponent
            ) },
            reducer = HomeReducer
        )
}