package com.nevrozq.pansion.database.scheduleConflicts

data class ScheduleConflictsDTO(
    val date: String,
    val lessonIndex: Int,
    val logins: List<String>
)
