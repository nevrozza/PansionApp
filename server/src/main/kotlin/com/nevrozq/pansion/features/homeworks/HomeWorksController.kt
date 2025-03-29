package com.nevrozq.pansion.features.homeworks

import com.nevrozq.pansion.database.homework.HomeTasks
import com.nevrozq.pansion.database.homework.HomeTasksDone
import com.nevrozq.pansion.database.schedule.Schedule
import com.nevrozq.pansion.database.schedule.ScheduleDTO
import com.nevrozq.pansion.database.studentGroups.StudentGroups
import com.nevrozq.pansion.database.subjects.Subjects
import com.nevrozq.pansion.utils.dRes
import com.nevrozq.pansion.utils.done
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
        val perm = call.isMember && r.login == call.login
        call.dRes(perm, "Can't check Task") {
            HomeTasksDone.checkTask(login = r.login, homeWorkId = r.homeWorkId, isDone = r.isCheck, id = r.id)
            this.respond(
                HttpStatusCode.OK
            ).done
        }
    }

    suspend fun fetchHomeTasks(call: ApplicationCall) {
        val perm = call.isMember
        call.dRes(perm, "Can't fetch homeTasks") {
            val r = this.receive<RFetchHomeTasksReceive>()
            val groupIDS = StudentGroups.fetchGroupOfStudentIDS(r.login) //
            val homeTasks = HomeTasks.getClientHomeTasks(groupIds = groupIDS, login = r.login, date = r.date)

            this.respond(
                RFetchHomeTasksResponse(
                    tasks = homeTasks
                )
            ).done
        }
    }

    suspend fun fetchHomeTasksCount(call: ApplicationCall) {
        val perm = call.isMember
        call.dRes(perm, "Can't fetch count") {
            val r = this.receive<RFetchMainHomeTasksCountReceive>()
            val groupIDS = StudentGroups.fetchGroupOfStudentIDS(r.studentLogin) //
            val count = HomeTasks.getCountNOTDoneNecHomeTasks(groupIds = groupIDS, login = r.studentLogin)

            this.respond(
                RFetchMainHomeTasksCountResponse(
                    count = count
                )
            ).done
        }
    }

    suspend fun fetchHomeTasksInit(call: ApplicationCall) {
        val perm = call.isMember
        call.dRes(perm, "Can't fetch homeTasksInit") {
            val r = this.receive<RFetchTasksInitReceive>()
            val groups = StudentGroups.fetchGroupsOfStudent(r.login)
            val subjects = Subjects.fetchAllSubjectsAsMap().filter { it.key in groups.map { g -> g.subjectId } }
            val schedule: List<ScheduleDTO> =
                Schedule.getOnNext(
                    getDate(), getSixTime()
                ).sortedBy {
                    getLocalDate(it.date).toEpochDays() + (it.start.toMinutes() / 1000f)
                }
            val cutedDateTimeGroups = groups.map { g ->
                val lesson = schedule.firstOrNull { it.groupId == g.id }
                CutedDateTimeGroup(
                    id = g.id,
                    name = g.name,
                    localDateTime = if (lesson != null) getLocalDateTime(
                        date = lesson.date,
                        time = lesson.start
                    ) else null
                )
            }
            this.respond(
                RFetchTasksInitResponse(
                    groups = cutedDateTimeGroups,
                    subjects = subjects,
                    dates = HomeTasks.getHomeTasksDateForGroupsLogin(groups.map { it.id }, r.login)
                )
            ).done
        }
    }
}

private fun getLocalDateTime(date: String, time: String): LocalDateTime =
    LocalDateTime(
        date = getLocalDate(date),
        time = LocalTime.fromSecondOfDay(time.toMinutes() * 60)
    )
