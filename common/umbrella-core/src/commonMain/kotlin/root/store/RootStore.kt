package root.store

import RFetchGroupDataResponse
import ReportData
import applicationVersion
import com.arkivanov.mvikotlin.core.store.Store
import root.RootComponent
import root.store.RootStore.Intent
import root.store.RootStore.Label
import root.store.RootStore.State
import webload.RFetchUserDataResponse


sealed interface QuickRoutings {
    data object SecondView : QuickRoutings
    data object HomeAchievements : QuickRoutings
    data object HomeStudentLines : QuickRoutings
    data object HomeTasks : QuickRoutings
    data object HomeProfile : QuickRoutings
    data object HomeAllGroupMarks : QuickRoutings
    data object HomeDetailedStups : QuickRoutings
    data object HomeDnevnikRuMarks : QuickRoutings
    data object LessonReport : QuickRoutings
}


interface RootStore : Store<Intent, State, Label> {
    sealed interface Intent {
        data class HideGreetings(val time: Long = 1000) : Intent
        data class UpdatePermissions(val role: String, val moderation: String, val birthday: String, val version: Int) :
            Intent

        data object CheckConnection : Intent
        data class ChangeTokenValidationStatus(val isTokenValid: Boolean) : Intent
        data class FetchStartUser(val login: String, val routing: QuickRoutings) : Intent
        data class FetchStartGroup(val groupId: Int) : Intent
        data class FetchStartReport(val reportId: Int) : Intent

        data object DeleteStart : Intent
    }

    data class State(
        val isGreetingsShowing: Boolean,
        val role: String,
        val moderation: String,
        val birthday: String,
        val isTokenValid: Boolean = true,
        val version: Int = applicationVersion,

        val startUser: RFetchUserDataResponse? = null,
        val startGroup: RFetchGroupDataResponse? = null,
        val startReport: ReportData? = null,
        val startRouting: QuickRoutings? = null,

        val isStartUserGreetingsShowing: Boolean = false
    )

    sealed interface Message {
        data object GreetingsHided : Message
        data class PermissionsUpdated(val role: String, val moderation: String, val birthday: String) : Message
        data class TokenValidationStatusChanged(val isTokenValid: Boolean) : Message
        data class VersionFetched(val version: Int) : Message

        data object StartIsNeeded : Message

        data class StartFetched(
            val rUser: RFetchUserDataResponse?,
            val rGroup: RFetchGroupDataResponse?,
            val rReportData: ReportData?,
            val routing: QuickRoutings?,
        ) : Message
    }

    sealed interface Label

}
