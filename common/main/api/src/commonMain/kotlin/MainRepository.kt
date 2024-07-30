import admin.schedule.ScheduleSubject
import journal.init.RFetchStudentsInGroupResponse
import journal.init.RFetchTeacherGroupsResponse
import main.RFetchMainAVGResponse
import mentoring.RFetchMentoringStudentsResponse
import mentoring.preAttendance.RFetchPreAttendanceDayReceive
import mentoring.preAttendance.RFetchPreAttendanceDayResponse
import mentoring.preAttendance.RSavePreAttendanceDayReceive
import rating.RFetchScheduleSubjectsResponse
import rating.RFetchSubjectRatingResponse
import report.RCreateReportReceive
import report.RCreateReportResponse
import report.RFetchHeadersResponse
import report.RFetchRecentGradesResponse
import report.RFetchReportDataResponse
import schedule.RPersonScheduleList
import schedule.RScheduleList

interface MainRepository {

    suspend fun fetchMentorStudents(): RFetchMentoringStudentsResponse
    suspend fun fetchPreAttendanceDay(r: RFetchPreAttendanceDayReceive) : RFetchPreAttendanceDayResponse
    suspend fun savePreAttendanceDay(r: RSavePreAttendanceDayReceive)
    suspend fun fetchTeacherGroups(): RFetchTeacherGroupsResponse
    suspend fun fetchStudentsInGroup(groupId: Int): RFetchStudentsInGroupResponse

    suspend fun fetchMainAvg(login: String, reason: String): RFetchMainAVGResponse

    suspend fun fetchReportHeaders(): RFetchHeadersResponse
    suspend fun createReport(reportReceive: RCreateReportReceive): RCreateReportResponse

    suspend fun fetchReportData(reportId: Int): RFetchReportDataResponse

    suspend fun fetchRecentGrades(login: String): RFetchRecentGradesResponse

    suspend fun fetchPersonSchedule(dayOfWeek: String, date: String, login: String): RPersonScheduleList

    suspend fun fetchScheduleSubjects(): RFetchScheduleSubjectsResponse

    suspend fun fetchSubjectRating(login: String, subjectId: Int, period: Int, forms: Int): RFetchSubjectRatingResponse
}