package com.nevrozq.pansion.database.ratingTable

data class RatingTableDTO(
    val login: String,
    val name: String,
    val surname: String,
    val praname: String?,
    val avatarId: Int,
    val stups: Int,
    val avg: String,
    val top: Int,
    val groupName: String,
    val formNum: Int,
    val subjectId: Int,
    val formShortTitle: String,
)
