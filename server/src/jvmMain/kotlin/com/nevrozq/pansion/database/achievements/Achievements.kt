package com.nevrozq.pansion.database.achievements

import achievements.AchievementsDTO
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

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
    private val time = this.varchar("time", 5)

    private val text = this.text("text")
    private val showInProfile = this.bool("showInProfile")
    private val showDate = this.varchar("showDate", 10).nullable()

    private val subjectId = this.integer("subjectId") //mvd-2 social-3 creative-3
    private val stups = this.integer("stups")

    fun insert(dto: AchievementsDTO) {
        transaction {
            Achievements.insert {
                it[studentLogin] = dto.studentLogin
                it[creatorLogin] = dto.creatorLogin
                it[date] = dto.date
                it[time] = dto.time
                it[text] = dto.text
                it[showInProfile] = dto.showInProfile
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
                    time = it[time],
                    text = it[text],
                    showInProfile = it[showInProfile],
                    showDate = it[showDate],
                    subjectId = it[subjectId],
                    stups = it[stups]
                )
            }
        }
    }
    fun fetchAllByLogin(login: String): List<AchievementsDTO> {
        return transaction {
            Achievements.select { (Achievements.studentLogin eq login) }.map {
                AchievementsDTO(
                    id = it[Achievements.id],
                    studentLogin = it[studentLogin],
                    creatorLogin = it[creatorLogin],
                    date = it[date],
                    time = it[time],
                    text = it[text],
                    showInProfile = it[showInProfile],
                    showDate = it[showDate],
                    subjectId = it[subjectId],
                    stups = it[stups]
                )
            }
        }
    }
}