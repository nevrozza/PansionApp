package admin

import com.arkivanov.mvikotlin.core.store.Store
import admin.AdminStore.Intent
import admin.AdminStore.Label
import admin.AdminStore.State

interface AdminStore : Store<Intent, State, Label> {
    data class State(
        val items: List<AdminItem>? = getItems()
    )

    sealed interface Intent {
    }

    sealed interface Message {
    }

    sealed interface Label

}

fun getItems(): List<AdminItem> {
    return listOf(
        AdminItem(title = "Пользователи", routing = AdminComponent.Output.NavigateToUsers),
        AdminItem(title = "Группы", routing = AdminComponent.Output.NavigateToGroups),
        AdminItem(title = "Кабинеты", routing = AdminComponent.Output.NavigateToCabinets),
        AdminItem(title = "Календарь", routing = AdminComponent.Output.NavigateToCalendar),
//        AdminItem(title = "Ученики", routing = AdminComponent.Output.NavigateToUsers),
//        AdminItem(title = "Наставники", routing = AdminComponent.Output.NavigateToMentors),
    )
}
