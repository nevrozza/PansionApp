package main

import kotlinx.serialization.Serializable


@Serializable
data class RDeleteMainNotificationsReceive(
    val studentLogin: String,
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
data class ClientMainNotification(
    val key: String,
    val subjectName: String,
    val reason: String,
    val date: String,
    val reportTime: String?,
    val groupName: String?
)