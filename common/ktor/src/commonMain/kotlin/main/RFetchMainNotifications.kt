package main

import Person
import kotlinx.serialization.Serializable


@Serializable
data class RDeleteMainNotificationsReceive(
//    val studentLogin: String,
    val key: String
)

@Serializable
data class RFetchMainNotificationsReceive(
    val studentLogin: String
)

@Serializable
data class RFetchMainNotificationsResponse(
    val notifications: List<ClientMainNotification>
)
@Serializable
data class RFetchChildrenMainNotificationsResponse(
    val students: List<Person>,
    val notifications: Map<String, List<ClientMainNotification>>
)

@Serializable
data class ClientMainNotification(
    val key: String,
    val subjectName: String,
    val reason: String,
    val date: String,
    val reportTime: String?,
    val groupName: String?,
    val reportId: Int?
)