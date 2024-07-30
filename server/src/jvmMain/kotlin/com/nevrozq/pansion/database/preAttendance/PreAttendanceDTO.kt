package com.nevrozq.pansion.database.preAttendance


data class PreAttendanceDTO(
    val id: Int,
    val studentLogin: String,
    val date: String,
    val start: String,
    val end: String,
    val reason: String,
    val isGood: Boolean
)