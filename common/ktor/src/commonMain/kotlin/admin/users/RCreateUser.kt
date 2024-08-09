package admin.users

import kotlinx.serialization.Serializable

@Serializable
data class RRegisterUserReceive(
    val userInit: UserInit,
    val parentFIOs: List<String>?
)


@Serializable
data class RCreateUserResponse(
    val login: String,
    val parents: List<String>?
//    val users: List<User>
)

