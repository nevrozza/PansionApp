package com.nevrozq.pansion.database.achievements

import achievements.AchievementsDTO
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import server.getDate
import server.getLocalDate

object Achievements: Table() {
    //val id: Int,
    //    val studentLogin: String,
    //    val creatorLogin: String,
    //    val date: String,
    //    val time: String,
    //
    //    val text: String,
    //    val showInProfile: Boolean,
    //    val showDate: String?,
    //
    //    val subject: String,
    //    val stups: Int

    private val id = this.integer("id").autoIncrement().uniqueIndex()
    private val studentLogin = this.varchar("studentLogin", 30)
    private val creatorLogin = this.varchar("creatorLogin", 30)
    private val date = this.varchar("date", 10)

    private val text = this.text("text")
    private val showDate = this.varchar("showDate", 10).nullable()

    private val subjectId = this.integer("subjectId") //mvd-2 social-3 creative-3
    private val stups = this.integer("stups")

    fun insert(dto: AchievementsDTO) {
        transaction {
            Achievements.insert {
                it[studentLogin] = dto.studentLogin
                it[creatorLogin] = dto.creatorLogin
                it[date] = dto.date
                it[text] = dto.text
                it[showDate] = dto.showDate
                it[subjectId] = dto.subjectId
                it[stups] = dto.stups
            }
        }
    }

    fun fetchAll(): List<AchievementsDTO> {
        return transaction {
            Achievements.selectAll().map {
                AchievementsDTO(
                    id = it[Achievements.id],
                    studentLogin = it[studentLogin],
                    creatorLogin = it[creatorLogin],
                    date = it[date],
                    text = it[text],
                    showDate = it[showDate],
                    subjectId = it[subjectId],
                    stups = it[stups]
                )
            }
        }
    }
    fun fetchAllByLogin(login: String): List<AchievementsDTO> {
        return transaction {
            Achievements.select { (Achievements.studentLogin eq login) }.mapNotNull {
                val xDate = if((it[showDate]?.length ?: 0 ) > 5) it[showDate] ?: it[date] else it[date]
                if (getLocalDate(xDate) <= getLocalDate(getDate())) {
                    AchievementsDTO(
                        id = it[Achievements.id],
                        studentLogin = it[studentLogin],
                        creatorLogin = it[creatorLogin],
                        date = it[date],
                        text = it[text],
                        showDate = it[showDate],
                        subjectId = it[subjectId],
                        stups = it[stups]
                    )
                } else null
            }
        }
    }
    fun editGroup(oldText: String,
                  oldShowDate: String,
                  oldDate: String,
                  newText: String,
                  newShowDate: String,
                  newDate: String) {
        transaction {
            Achievements.update({ (text eq oldText) and (showDate eq oldShowDate) and (date eq oldDate) }) {
                it[date] = newDate
                it[showDate] = newShowDate
                it[text] = newText
            }
        }
    }

    fun edit(id: Int,
             studentLogin: String,
             subjectId: Int,
             stups: Int) {
        transaction {
            Achievements.update({Achievements.id eq id}){
                it[Achievements.studentLogin] = studentLogin
                it[Achievements.subjectId] = subjectId
                it[Achievements.stups] = stups
            }
        }
    }
}