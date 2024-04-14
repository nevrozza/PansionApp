package com.nevrozq.pansion.database.studentLines

import admin.groups.Group
import admin.groups.GroupInit
import admin.groups.forms.CutedGroup
import com.nevrozq.pansion.database.subjects.Subjects
import journal.init.TeacherGroup


data class StudentLinesDTO(
    val reportId: Int,
    val groupId: Int,
    val login: String,
    val lateTime: String,
    val isLiked: String
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