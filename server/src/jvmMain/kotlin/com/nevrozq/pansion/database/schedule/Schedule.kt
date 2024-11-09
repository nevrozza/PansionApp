package com.nevrozq.pansion.database.schedule

import com.nevrozq.pansion.utils.toList
import com.nevrozq.pansion.utils.toStr
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import schedule.ScheduleItem
import server.getLocalDate
import server.toMinutes


object Schedule : Table() {
    val id = Schedule.integer("id")//.autoIncrement().uniqueIndex()
    val date = Schedule.varchar("date", 10) //12.45.78
    private val teacherLogin = Schedule.varchar("teacherLogin", 30)
    private val teacherLoginBefore = Schedule.varchar("teacherLoginBefore", 30)
    private val groupId = Schedule.integer("groupId")
    private val start = Schedule.varchar("start", 5)
    private val end = Schedule.varchar("end", 5)
    private val cabinet = Schedule.varchar("cabinet", 3)
    private val formId = Schedule.integer("formId").nullable()
    private val custom = Schedule.text("custom")
    private val subjectId = Schedule.integer("subjectId").nullable()
    val isMarked = Schedule.bool("isMarked")

    fun markLesson(lessonId: Int?, lessonDate: String) {
        if (lessonId != null) {
            transaction {
                Schedule.update({ (Schedule.id eq (lessonId ?: -1)) and (Schedule.date eq lessonDate)}) {
                    it[Schedule.isMarked] = true
                }
            }
        }
    }


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
                    it[formId] = dto.formId
                    it[custom] = dto.custom.toStr() ?: ""
                    it[Schedule.id] = dto.id
                    it[subjectId] = dto.subjectId
                    it[isMarked] = dto.isMarked
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
        try {


            val minutes = time.toMinutes()
            val days = getLocalDate(date).toEpochDays()
            return transaction {
                Schedule.selectAll().filter { it[Schedule.date].length != 1 }.filter {
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
                        teacherLoginBefore = it[teacherLoginBefore],
                        formId = it[formId],
                        custom = it[custom].toList() ?: emptyList(),
                        id = it[Schedule.id],
                        subjectId = it[Schedule.subjectId],
                        isMarked = it[isMarked]
                    )
                }
            }
        } catch (e: Throwable) {
            println("ANIMEEE${e}")
            return emptyList()
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
                    teacherLoginBefore = it[teacherLoginBefore],
                    formId = it[formId],
                    custom = it[custom].toList() ?: listOf(),
                    id = it[Schedule.id],
                    subjectId = it [Schedule.subjectId],
                    isMarked = it[isMarked]
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