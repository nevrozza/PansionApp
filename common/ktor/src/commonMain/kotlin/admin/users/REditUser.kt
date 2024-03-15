package admin.users

import kotlinx.serialization.Serializable

@Serializable
data class REditUserReceive(
    val login: String,
    val user: UserInit
)

//
//
//@Serializable
//data class REditUserResponse(
//    val users: List<User>
//)