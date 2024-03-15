package admin.users

import kotlinx.serialization.Serializable



@Serializable
data class RFetchAllUsersResponse(
    val users: List<User>
)



