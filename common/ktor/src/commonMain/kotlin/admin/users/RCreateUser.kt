package admin.users

import kotlinx.serialization.Serializable

@Serializable
data class RRegisterUserReceive(
    val userInit: UserInit
)


@Serializable
data class RCreateUserResponse(
    val login: String,
//    val users: List<User>
)

