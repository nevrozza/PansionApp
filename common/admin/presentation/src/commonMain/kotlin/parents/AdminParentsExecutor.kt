package parents

import AdminRepository
import admin.parents.RFetchParentsListResponse
import admin.parents.RUpdateParentsListReceive
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import components.listDialog.ListComponent
import components.listDialog.ListDialogStore
import components.listDialog.ListItem
import components.networkInterface.NetworkInterface
import deviceSupport.launchIO
import deviceSupport.withMain
import di.Inject
import kotlinx.coroutines.launch
import parents.AdminParentsStore.Intent
import parents.AdminParentsStore.Label
import parents.AdminParentsStore.Message
import parents.AdminParentsStore.State
import server.updateSafe

class AdminParentsExecutor(
    private val adminRepository: AdminRepository = Inject.instance(),
    private val nInterface: NetworkInterface,
    private val parentEditPicker: ListComponent,
    private val childCreatePicker: ListComponent,
) : CoroutineExecutor<Intent, Unit, State, Message, Label>() {
    override fun executeAction(action: Unit) {
        init()
    }


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
        val formId = state().users.firstOrNull { it.login == login }?.formId
        if (formId != null) {
            val newKids = state().kids.toMutableMap()
            newKids.updateSafe(formId, login)
            dispatch(
                Message.KidsUpdated(
                    newKids
                )
            )
        }
        childCreatePicker.onEvent(ListDialogStore.Intent.HideDialog)
        scope.launch {
            updateChildPicker(state())
        }
    }

    private fun pickParent(login: String) {
        scope.launchIO {
            nInterface.nStartLoading()
            try {
                if (login != "0" || state().editId != 0) {
                    val r = adminRepository.updateParents(
                        RUpdateParentsListReceive(
                            studentLogin = state().addToStudent,
                            id = state().editId,
                            parentLogin = login
                        )
                    )
                    withMain {
                        dispatch(
                            Message.Inited(
                                users = r.users,
                                lines = r.lines,
                                forms = r.forms
                            )
                        )



                        updateParentPicker(r)

                        nInterface.nSuccess()
                    }

                }
                withMain {
                    parentEditPicker.onEvent(ListDialogStore.Intent.HideDialog)
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
                state.users.filter { it.login !in state().kids.flatMap { it.value } && !it.isParent && it.isActive && it.isStudent}.map {
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
        scope.launchIO {
            nInterface.nStartLoading()
            try {
                val r = adminRepository.fetchParents()
                val oldKids = r.lines.map { it.studentLogin }.toSet().toList()
                withMain {
                    dispatch(Message.Inited(
                        users = r.users,
                        lines = r.lines,
                        forms = r.forms
                    ))
                    dispatch(
                        Message.KidsUpdated(
                            kids = r.forms.associate {
                                it.id to oldKids.filter { x ->
                                    val u = r.users.firstOrNull { it.login == x }
                                    u?.formId == it.id
                                }
                            }
                        )
                    )

                    updateParentPicker(r)

                    updateChildPicker(state())

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
