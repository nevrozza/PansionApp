package com.nevrozq.pansion.database.schedule

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import schedule.ScheduleItem
import server.getLocalDate
import server.toMinutes


object Schedule : Table() {
    private val id = Schedule.integer("id").autoIncrement().uniqueIndex()
    val date = Schedule.varchar("date", 10) //12.45.78
    private val teacherLogin = Schedule.varchar("teacherLogin", 30)
    private val teacherLoginBefore = Schedule.varchar("teacherLoginBefore", 30)
    private val groupId = Schedule.integer("groupId")
    private val start = Schedule.varchar("start", 5)
    private val end = Schedule.varchar("end", 5)
    private val cabinet = Schedule.varchar("cabinet", 3)

    fun insert(dto: ScheduleDTO) {
        try {
            transaction {
                Schedule.insert {
                    it[teacherLogin] = dto.teacherLogin
                    it[groupId] = dto.groupId
                    it[start] = dto.start
                    it[end] = dto.end
                    it[cabinet] = dto.cabinet
                    it[date] = dto.date
                    it[teacherLoginBefore] = dto.teacherLoginBefore
                }
            }
        } catch (e: Throwable) {
            println(e)
        }
    }

    fun insertList(list: List<ScheduleDTO>) {
        try {
            transaction {
                list.forEach {
                    insert(it)
                }
            }
        } catch (e: Throwable) {
            println(e)
        }
    }

    fun getOnNext(date: String, time: String): List<ScheduleDTO> {
        val minutes = time.toMinutes()
        val days = getLocalDate(date).toEpochDays()
        return transaction {
            Schedule.selectAll().filter {
                val scheduleMinutes = it[Schedule.start].toMinutes()
                val scheduleDays = getLocalDate(it[Schedule.date]).toEpochDays()
                ( scheduleDays > days || (scheduleDays == days && scheduleMinutes > minutes)  )
            }.map {
                ScheduleDTO(
                    date = it[Schedule.date],
                    teacherLogin = it[teacherLogin],
                    groupId = it[groupId],
                    start = it[start],
                    end = it[end],
                    cabinet = it[cabinet],
                    teacherLoginBefore = it[teacherLoginBefore]
                )
            }
        }
    }

    fun getOnDate(date: String): List<ScheduleItem> {
        return transaction {
            Schedule.select { Schedule.date eq date }.map {
                ScheduleDTO(
                    date = it[Schedule.date],
                    teacherLogin = it[teacherLogin],
                    groupId = it[groupId],
                    start = it[start],
                    end = it[end],
                    cabinet = it[cabinet],
                    teacherLoginBefore = it[teacherLoginBefore]
                ).mapToItem()
            }
        }
    }

//    fun getAllItems(): List<Pair<String, ScheduleItem>> {
//        return transaction {
//            Schedule.selectAll().map {
//                ScheduleDTO(
//                    date = it[date],
//                    teacherLogin = it[teacherLogin],
//                    groupId = it[groupId],
//                    start = it[start],
//                    end = it[end],
//                    cabinet = it[cabinet]
//                ).mapToItem()
//            }
//        }
//    }

//    fun fetchById(formId: Int): FormDTO {
//        return transaction {
//            val it = Schedule.select { Schedule.id eq formId }.first()
//            FormDTO(
//                formId = it[Schedule.id],
//                classNum = it[classNum],
//                title = it[title],
//                shortTitle = it[shortTitle],
//                mentorLogin = it[mentorLogin],
//                isActive = it[isActive]
//            )
//
//        }
//    }
}