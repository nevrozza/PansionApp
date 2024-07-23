package com.nevrozq.pansion.database.homework

import com.nevrozq.pansion.database.homework.HomeTasksDone.autoIncrement
import com.nevrozq.pansion.database.homework.HomeTasksDone.uniqueIndex
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

data class HomeTasksDoneDTO(
    val id: Int,
    val studentLogin: String,
    val isDone: Boolean,
    val homeWorkId: Int,
    val seconds: Int,
    val zabil: Boolean
)

