package com.nevrozq.pansion.database.schedule

import admin.groups.forms.Form
import admin.groups.forms.FormInit
import kotlinx.serialization.Serializable
import schedule.ScheduleItem
import schedule.ScheduleTiming

data class ScheduleDTO(
    val date: String,
    val teacherLogin: String,
    val teacherLoginBefore: String,
    val groupId: Int,
    val start: String,
    val end: String,
    val cabinet: String,
    val formId: Int?,
    val custom: String,
    val subjectId: Int?,
    val id: Int,
    val isMarked: Boolean
)


fun ScheduleDTO.mapToItem() =
        ScheduleItem(
            teacherLogin = this.teacherLogin,
            groupId = this.groupId,
            t = ScheduleTiming(
                start = this.start,
                end = this.end
            ),
            cabinet = this.cabinet.toInt(),
            teacherLoginBefore = this.teacherLoginBefore,
            formId = this.formId,
            custom = this.custom,
            index = this.id,
            subjectId = this.subjectId,
            isMarked = this.isMarked
        )

