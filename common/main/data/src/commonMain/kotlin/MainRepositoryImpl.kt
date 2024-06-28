import io.ktor.client.request.post
import io.ktor.http.path
import journal.init.RFetchStudentsInGroupReceive
import journal.init.RFetchStudentsInGroupResponse
import journal.init.RFetchTeacherGroupsResponse
import ktor.KtorMainRemoteDataSource
import main.RFetchMainAVGReceive
import main.RFetchMainAVGResponse
import rating.RFetchScheduleSubjectsResponse
import rating.RFetchSubjectRatingReceive
import rating.RFetchSubjectRatingResponse
import report.RCreateReportReceive
import report.RCreateReportResponse
import report.RFetchHeadersResponse
import report.RFetchRecentGradesReceive
import report.RFetchRecentGradesResponse
import report.RFetchReportDataReceive
import report.RFetchReportDataResponse
import schedule.RFetchScheduleDateReceive
import schedule.RPersonScheduleList
import schedule.RScheduleList

class MainRepositoryImpl(
    private val remoteDataSource: KtorMainRemoteDataSource
) : MainRepository {


    override suspend fun fetchTeacherGroups(): RFetchTeacherGroupsResponse {
        return remoteDataSource.fetchTeacherGroups()
    }

    override suspend fun fetchStudentsInGroup(groupId: Int): RFetchStudentsInGroupResponse {
        return remoteDataSource.fetchStudentInGroup(
            RFetchStudentsInGroupReceive(
                groupId = groupId
            )
        )
    }

    override suspend fun fetchMainAvg(login: String, reason: String): RFetchMainAVGResponse {
        return remoteDataSource.fetchMainAvg(
            RFetchMainAVGReceive(
                login,
                reason
            )
        )
    }

    override suspend fun fetchReportHeaders(): RFetchHeadersResponse {
        return remoteDataSource.fetchReportHeaders()
    }

    override suspend fun createReport(reportReceive: RCreateReportReceive): RCreateReportResponse {
        return remoteDataSource.createReport(reportReceive)
    }

    override suspend fun fetchReportData(reportId: Int): RFetchReportDataResponse {
        return remoteDataSource.fetchReportData(RFetchReportDataReceive(reportId))
    }

    override suspend fun fetchRecentGrades(login: String): RFetchRecentGradesResponse {
        return remoteDataSource.fetchRecentGrades(RFetchRecentGradesReceive(login))
    }

    override suspend fun fetchPersonSchedule(dayOfWeek: String, date: String): RPersonScheduleList {
        return remoteDataSource.fetchPersonSchedule(
            RFetchScheduleDateReceive(
                dayOfWeek = dayOfWeek,
                day = date
            )
        )
    }

    override suspend fun fetchScheduleSubjects(): RFetchScheduleSubjectsResponse {
        return remoteDataSource.fetchScheduleSubjects()
    }

    override suspend fun fetchSubjectRating(
        login: String,
        subjectId: Int,
        period: Int,
        forms: Int
    ): RFetchSubjectRatingResponse {
        return remoteDataSource.fetchSubjectRating(
            RFetchSubjectRatingReceive(
                login = login,
                subjectId = subjectId,
                period = period,
                forms = forms
            )
        )
    }
}