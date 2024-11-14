package auth

import admin.groups.Group
import admin.groups.Subject
import admin.groups.forms.Form
import kotlinx.serialization.Serializable

@Serializable
data class RFetchAboutMeReceive(
    val studentLogin: String
)

@Serializable
data class RFetchAboutMeResponse(
    val form: Form,
    val groups: List<Group>,
    val subjects: List<Subject>,
    val teachers: HashMap<String, String>,
    val likes: Int,
    val dislikes: Int,
    val giaSubjects: List<Int>,
    val ministryId: String,
    val ministryLevel: String,
    val pansCoins: Int,
    val avatars: List<Int>
)
