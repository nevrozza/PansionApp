package registration

import kotlinx.serialization.Serializable

@Serializable
data class FetchLoginsResponse(
    val logins: List<String>
)
@Serializable
data class FetchLoginsReceive(
    val deviceId: String
)

@Serializable
data class RegistrationRequest(
    val name: String,
    val surname: String,
    val praname: String,

    val birthday: String,


    val fioFather: String,
    val fioMother: String,

    val avatarId: Int,

    val formId: Int
)