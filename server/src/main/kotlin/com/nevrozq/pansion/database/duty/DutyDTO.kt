package com.nevrozq.pansion.database.duty

data class DutyDTO(
    val mentorLogin: String,
    val studentLogin: String
)
//data class DutyCountDTO(
//    val studentLogin: String,
//    val dutyCount: Int
//)
data class DutySettingsDTO(
    val mentorLogin: String,
    val peopleCount: Int
)