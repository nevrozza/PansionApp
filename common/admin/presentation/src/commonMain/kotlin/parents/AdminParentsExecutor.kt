package parents

import AdminRepository
import CDispatcher
import achievements.AdminAchievementsStore
import admin.parents.RFetchParentsListResponse
import admin.parents.RUpdateParentsListReceive
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import components.listDialog.ListComponent
import components.listDialog.ListDialogStore
import components.listDialog.ListItem
import components.networkInterface.NetworkInterface
import kotlinx.coroutines.launch
import parents.AdminParentsStore.Intent
import parents.AdminParentsStore.Label
import parents.AdminParentsStore.State
import parents.AdminParentsStore.Message

class AdminParentsExecutor(
    private val adminRepository: AdminRepository,
    private val nInterface: NetworkInterface,
    private val parentEditPicker: ListComponent,
    private val childCreatePicker: ListComponent,
) : CoroutineExecutor<Intent, Unit, State, Message, Label>() {
    override fun executeIntent(intent: Intent) {
        when (intent) {
            Intent.Init -> init()
            is Intent.EditId -> editId(intent.editId)
            is Intent.AddToStudent -> addToStudent(intent.login)
            is Intent.PickParent -> pickParent(intent.login)
            is Intent.CreateChild -> createChild(intent.login)
        }
    }

    private fun createChild(login: String) {
        dispatch(
            Message.KidsUpdated(
                state().kids + login
            )
        )
        childCreatePicker.onEvent(ListDialogStore.Intent.HideDialog)
        scope.launch {
            updateChildPicker(state())
        }
    }

    private fun pickParent(login: String) {
        scope.launch(CDispatcher) {
            nInterface.nStartLoading()
            try {
                if(login == "0" && state().editId == 0) {

                } else {
                    val r = adminRepository.updateParents(
                        RUpdateParentsListReceive(
                            studentLogin = state().addToStudent,
                            id = state().editId,
                            parentLogin = login
                        )
                    )
                    scope.launch {
                        dispatch(
                            Message.Inited(
                                users = r.users,
                                lines = r.lines
                            )
                        )



                        updateParentPicker(r)

                        nInterface.nSuccess()
                    }

                }
                scope.launch {
                    parentEditPicker.onEvent(ListDialogStore.Intent.HideDialog)
                }
            } catch (e: Throwable) {

                nInterface.nError(
                    "Что-то пошло не так",
                ) {
                    init()
                }
            }
        }
    }

    private fun updateParentPicker(r: RFetchParentsListResponse) {
        parentEditPicker.onEvent(
            ListDialogStore.Intent.InitList(
                listOf(
                    ListItem(
                        id = "0",
                        text = "Удалить"
                    )
                ) + r.users.filter { it.isParent }.map {
                    ListItem(
                        id = it.login,
                        text = "${it.fio.surname} ${it.fio.name} ${it.fio.praname}"
                    )
                }
            )
        )
    }

    private fun updateChildPicker(state: State) {
        childCreatePicker.onEvent(
            ListDialogStore.Intent.InitList(
                state.users.filter { it.login !in state().kids && !it.isParent && it.isActive && it.isStudent}.map {
                    ListItem(
                        id = it.login,
                        text = "${it.fio.surname} ${it.fio.name} ${it.fio.praname}"
                    )
                }
            )
        )
    }

    private fun editId(id: Int) {
        parentEditPicker.onEvent(ListDialogStore.Intent.ShowDialog)
        dispatch(Message.EditId(
            editId = id
        ))
    }
    private fun addToStudent(login: String) {
        parentEditPicker.onEvent(ListDialogStore.Intent.ShowDialog)
        dispatch(Message.AddToStudent(
            login = login
        ))
    }

    private fun init() {
        scope.launch(CDispatcher) {
            nInterface.nStartLoading()
            try {
                val r = adminRepository.fetchParents()
                scope.launch {
                    dispatch(Message.Inited(
                        users = r.users,
                        lines = r.lines
                    ))
                    dispatch(
                        Message.KidsUpdated(
                            kids = r.lines.map { it.studentLogin }.toSet().toList()
                        )
                    )

                    updateParentPicker(r)

                    updateChildPicker(state())

                    nInterface.nSuccess()
                }
            } catch (e: Throwable) {

                nInterface.nError(
                    "Что-то пошло не так",
                ) {
                    init()
                }
            }
        }
    }
}
