package com.nevrozq.pansion.database.schedule

import admin.groups.forms.Form
import admin.groups.forms.FormInit
import kotlinx.serialization.Serializable
import schedule.ScheduleItem
import schedule.ScheduleTiming

data class ScheduleDTO(
    val date: String,
    val teacherLogin: String,
    val groupId: Int,
    val start: String,
    val end: String,
    val cabinet: String
)


fun ScheduleDTO.mapToItem() =
        ScheduleItem(
            teacherLogin = this.teacherLogin,
            groupId = this.groupId,
            t = ScheduleTiming(
                start = this.start,
                end = this.end
            ),
            cabinet = this.cabinet.toInt()
        )

