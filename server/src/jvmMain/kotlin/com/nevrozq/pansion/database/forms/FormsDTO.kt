package com.nevrozq.pansion.database.forms

data class FormsDTO(
    val name: String,
    val shortName: String,
    val classNum: Int,
    val mentorLogin: String,
    val isActivated: Boolean
)
