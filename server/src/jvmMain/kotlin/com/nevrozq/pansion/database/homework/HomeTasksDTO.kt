package com.nevrozq.pansion.database.homework

import org.jetbrains.exposed.sql.Query

//val id = HomeTasks.integer("id").autoIncrement().uniqueIndex()
//private val date = this.varchar("date", 10)
//private val time = this.varchar("time", 5)
//private val type = this.varchar("type", 5)
//private val subjectId = this.integer("subjectId")
//private val groupId = this.integer("groupId")
//private val studentLogin = this.varchar("studentLogin", 30).nullable()
//private val teacherLogin = this.varchar("teacherLogin", 30)
//private val stups = this.integer("stups")
//private val text = this.text("text")
//private val filesId = this.text("filesId").nullable()
//private val reportId = this.integer("reportId")

data class HomeTasksDTO(
    val id: Int,
    val date: String,
    val time: String,
    val type: String,
    val subjectId: Int,
    val groupId: Int,
    val reportId: Int,
    val studentLogin: String?,
    val teacherLogin: String,
    val stups: Int,
    val text: String,
    val filesId: String?//List<Int>
)

