package admin.users

import kotlinx.serialization.Serializable

@Serializable
data class RClearUserPasswordReceive(
    val login: String
)

//
//@Serializable
//data class RClearUserPasswordResponse(
//    val isGood: Boolean
//)