package com.nevrozq.pansion.database.groups

import admin.groups.Group
import admin.groups.GroupInit
import admin.groups.forms.CutedGroup
import admin.groups.forms.FormGroup
import com.nevrozq.pansion.database.formGroups.FormGroupDTO
import com.nevrozq.pansion.database.subjects.Subjects
import journal.init.TeacherGroup

data class GroupDTO(
    val id: Int = -1,
    val name: String,
    val teacherLogin: String,
    val subjectId: Int,
    val difficult: String,
    val isActive: Boolean
)

fun GroupDTO.mapToGroup() =
    Group(
        id = this.id,
        group = GroupInit(
            name = this.name,
            teacherLogin = this.teacherLogin,
            subjectId = this.subjectId,
            difficult = this.difficult
        ),
        isActive = this.isActive
    )

fun GroupDTO.mapToCutedGroup() =
    CutedGroup(
        groupId = this.id,
        groupName = this.name,
        isActive = this.isActive
    )
fun GroupDTO.mapToTeacherGroup(login: String) =
    TeacherGroup(
        cutedGroup = CutedGroup(
            groupId = this.id,
            groupName = this.name,
            isActive = this.isActive
        ),
        subjectId = this.subjectId,
        subjectName = Subjects.fetchAllSubjects().find { it.id == this.subjectId }?.name ?: "Урок",
        teacherLogin = this.teacherLogin
    )