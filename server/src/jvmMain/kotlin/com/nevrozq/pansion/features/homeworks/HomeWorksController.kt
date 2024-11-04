package com.nevrozq.pansion.features.homeworks

import com.nevrozq.pansion.database.homework.HomeTasks
import com.nevrozq.pansion.database.homework.HomeTasksDone
import com.nevrozq.pansion.database.schedule.Schedule
import com.nevrozq.pansion.database.schedule.ScheduleDTO
import com.nevrozq.pansion.database.studentGroups.StudentGroups
import com.nevrozq.pansion.database.subjects.Subjects
import com.nevrozq.pansion.utils.isMember
import com.nevrozq.pansion.utils.login
import homework.CutedDateTimeGroup
import homework.RCheckHomeTaskReceive
import homework.RFetchHomeTasksReceive
import homework.RFetchHomeTasksResponse
import homework.RFetchTasksInitReceive
import homework.RFetchTasksInitResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import main.RFetchMainHomeTasksCountReceive
import main.RFetchMainHomeTasksCountResponse
import server.getDate
import server.getLocalDate
import server.getSixTime
import server.toMinutes

class HomeWorksController {

    suspend fun checkHomeTask(call: ApplicationCall) {

        val r = call.receive<RCheckHomeTaskReceive>()
        if (call.isMember && r.login == call.login) {
            try {
                HomeTasksDone.checkTask(login = r.login, homeWorkId = r.homeWorkId, isDone = r.isCheck, id = r.id)
                call.respond(
                    HttpStatusCode.OK
                )
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't check Task: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    suspend fun fetchHomeTasks(call: ApplicationCall) {
        if (call.isMember) {
            try {

                val r = call.receive<RFetchHomeTasksReceive>()
                val groupIDS = StudentGroups.fetchGroupOfStudentIDS(r.login) //
                val homeTasks = HomeTasks.getClientHomeTasks(groupIds = groupIDS, login = r.login, date = r.date)

                call.respond(
                    RFetchHomeTasksResponse(
                        tasks = homeTasks
                    )
                )
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't fetch homeTasks: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    suspend fun fetchHomeTasksCount(call: ApplicationCall) {
        if (call.isMember) {
            try {

                val r = call.receive<RFetchMainHomeTasksCountReceive>()
                val groupIDS = StudentGroups.fetchGroupOfStudentIDS(r.studentLogin) //
                val count = HomeTasks.getCountNOTDoneNecHomeTasks(groupIds = groupIDS, login = r.studentLogin)

                call.respond(
                    RFetchMainHomeTasksCountResponse(
                        count = count
                    )
                )
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't fetch count: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }

    suspend fun fetchHomeTasksInit(call: ApplicationCall) {
        if(call.isMember) {
            try {
                val r = call.receive<RFetchTasksInitReceive>()
                val groups = StudentGroups.fetchGroupsOfStudent(r.login)
                val subjects = Subjects.fetchAllSubjectsAsMap().filter { it.key in groups.map { g -> g.subjectId } }
                val schedule: List<ScheduleDTO> =
                    Schedule.getOnNext(
                    getDate(), getSixTime()).sortedBy { getLocalDate(it.date).toEpochDays() + (it.start.toMinutes() / 1000f)
                    }
                val cutedDateTimeGroups = groups.map { g ->
                    val lesson = schedule.firstOrNull { it.groupId == g.id }
                    CutedDateTimeGroup(
                        id = g.id,
                        name = g.name,
                        localDateTime = if(lesson != null) getLocalDateTime(date = lesson.date, time = lesson.start) else null
                        )
                }
                call.respond(
                    RFetchTasksInitResponse(
                        groups = cutedDateTimeGroups,
                        subjects = subjects,
                        dates = HomeTasks.getHomeTasksDateForGroupsLogin(groups.map { it.id }, r.login)
                    )
                )

            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Can't fetch homeTasksInit: ${e.localizedMessage}"
                )
            }
        } else {
            call.respond(HttpStatusCode.Forbidden, "No permission")
        }
    }
}

private fun getLocalDateTime(date: String, time: String) : LocalDateTime =
    LocalDateTime(
        date = getLocalDate(date),
        time = LocalTime.fromSecondOfDay(time.toMinutes() * 60)
    )
