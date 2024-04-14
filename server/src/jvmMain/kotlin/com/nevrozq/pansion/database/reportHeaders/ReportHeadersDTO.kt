package com.nevrozq.pansion.database.reportHeaders

import admin.groups.Group
import admin.groups.GroupInit
import admin.groups.forms.CutedGroup
import com.nevrozq.pansion.database.subjects.Subjects
import journal.init.TeacherGroup


data class ReportHeadersDTO(
    val id: Int = -1,
    val subjectName: String,
    val subjectId: Int,
    val groupName: String,
    val groupId: Int,
    val teacherLogin: String,
    val teacherName: String,
    val date: String,
    val time: String,
    val topic: String,
    val description: String,
    val customColumns: List<String>,
    val isMentorWas: Boolean,
    val status: String,
    val ids: Int,
    val editTime: String
)

//fun GroupDTO.mapToGroup() =
//    Group(
//        id = this.id,
//        group = GroupInit(
//            name = this.name,
//            teacherLogin = this.teacherLogin,
//            subjectId = this.subjectId,
//            difficult = this.difficult
//        ),
//        isActive = this.isActive
//    )
//
//fun GroupDTO.mapToCutedGroup() =
//    CutedGroup(
//        groupId = this.id,
//        groupName = this.name,
//        isActive = this.isActive
//    )
//fun GroupDTO.mapToTeacherGroup() =
//    TeacherGroup(
//        cutedGroup = CutedGroup(
//            groupId = this.id,
//            groupName = this.name,
//            isActive = this.isActive
//        ),
//        subjectId = this.subjectId,
//        subjectName = Subjects.fetchAllSubjects().find { it.id == this.subjectId }?.name ?: "Урок"
//    )