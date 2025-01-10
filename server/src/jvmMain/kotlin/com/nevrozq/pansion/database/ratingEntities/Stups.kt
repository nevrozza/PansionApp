package com.nevrozq.pansion.database.ratingEntities

import com.nevrozq.pansion.database.calendar.CalendarDTO
import com.nevrozq.pansion.database.reportHeaders.ReportHeaders
import com.nevrozq.pansion.utils.getModuleByDate
import main.school.RUploadMinistryStup
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import server.getCurrentDate
import server.getSixTime

object Stups : RatingEntity() {

    fun uploadMinistryStup(r: RUploadMinistryStup, deployLogin: String) {
        transaction {
            Stups.deleteWhere {
                (
                        (Stups.login eq r.studentLogin)
                                and (Stups.reason eq r.stup.reason)
                                and (Stups.reportId eq r.stup.reportId)
                                and (Stups.date eq r.date)
                        )
            }
            val report = if(r.stup.reportId != null) ReportHeaders.fetchHeader(reportId = r.stup.reportId!!) else null
            val id = if(r.stup.reason == "!ds1") 0
            else if (r.stup.reason == "!ds2") 1
            else 999

            val part = (getModuleByDate(r.date) ?: CalendarDTO(
                num = 1,
                start = "01.01.2000",
                halfNum = 1
            )).num
            if ((r.stup.content.toIntOrNull() ?: 0) != 0) {
                Stups.insert(
                    RatingEntityDTO(
                        groupId = report?.groupId,
                        subjectId = report?.subjectId,
                        reportId = r.stup.reportId,
                        login = r.studentLogin,
                        content = r.stup.content.removeSuffix("+"),
                        reason = r.stup.reason,
                        id = id,
                        part = part.toString(),
                        isGoToAvg = true,
                        date = r.date,
                        deployDate = getCurrentDate().second,
                        deployTime = getSixTime(),
                        deployLogin = deployLogin,
                        custom = r.stup.custom,
                        edYear = r.edYear
                    ),
                    isDelete = false
                )
            }

        }
    }
}