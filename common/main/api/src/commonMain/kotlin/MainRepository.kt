
import journal.init.RFetchMentorGroupIdsResponse
import journal.init.RFetchStudentsInGroupReceive
import journal.init.RFetchStudentsInGroupResponse
import journal.init.RFetchTeacherGroupsResponse
import main.RChangeToUv
import main.RDeleteMainNotificationsReceive
import main.RFetchChildrenMainNotificationsResponse
import main.RFetchChildrenResponse
import main.RFetchMainAVGResponse
import main.RFetchMainHomeTasksCountReceive
import main.RFetchMainHomeTasksCountResponse
import main.RFetchMainNotificationsReceive
import main.RFetchMainNotificationsResponse
import main.RFetchSchoolDataReceive
import main.RFetchSchoolDataResponse
import main.school.*
import mentoring.RFetchJournalBySubjectsReceive
import mentoring.RFetchJournalBySubjectsResponse
import mentoring.RFetchMentoringStudentsResponse
import mentoring.preAttendance.RFetchPreAttendanceDayReceive
import mentoring.preAttendance.RFetchPreAttendanceDayResponse
import mentoring.preAttendance.RSavePreAttendanceDayReceive
import rating.RFetchScheduleSubjectsResponse
import rating.RFetchSubjectRatingReceive
import rating.RFetchSubjectRatingResponse
import registration.CloseRequestQRReceive
import registration.OpenRequestQRReceive
import registration.SolveRequestReceive
import report.RCreateReportReceive
import report.RCreateReportResponse
import report.RFetchHeadersResponse
import report.RFetchRecentGradesResponse
import report.RFetchReportDataResponse
import schedule.RPersonScheduleList

interface MainRepository {

    suspend fun updateTodayDuty(r: RUpdateTodayDuty)
    suspend fun startNewDayDuty(r: RStartNewDayDuty)

    suspend fun fetchDuty(r: RFetchDutyReceive) : RFetchDutyResponse

    suspend fun createMinistryStudent(r: RCreateMinistryStudentReceive) : RFetchMinistrySettingsResponse

    suspend fun fetchMinistrySettings(r: RFetchMinistryStudentsReceive) : RFetchMinistrySettingsResponse


    suspend fun fetchSchoolData(r: RFetchSchoolDataReceive) : RFetchSchoolDataResponse

    suspend fun fetchJournalBySubjects(r: RFetchJournalBySubjectsReceive) : RFetchJournalBySubjectsResponse


    suspend fun openRegistrationQR(r: OpenRequestQRReceive)
    suspend fun closeRegistrationQR(r: CloseRequestQRReceive)
    suspend fun solveRegistrationRequest(r: SolveRequestReceive)


    suspend fun fetchMentorGroupIds(): RFetchMentorGroupIdsResponse

    suspend fun fetchMainNotifications(r: RFetchMainNotificationsReceive) : RFetchMainNotificationsResponse
    suspend fun fetchChildrenMainNotifications() : RFetchChildrenMainNotificationsResponse
    suspend fun deleteMainNotification(r: RDeleteMainNotificationsReceive)
    suspend fun changeToUv(r: RChangeToUv)

    suspend fun fetchChildren() : RFetchChildrenResponse

    suspend fun fetchMentorStudents(): RFetchMentoringStudentsResponse
    suspend fun fetchPreAttendanceDay(r: RFetchPreAttendanceDayReceive) : RFetchPreAttendanceDayResponse
    suspend fun savePreAttendanceDay(r: RSavePreAttendanceDayReceive)
    suspend fun fetchTeacherGroups(): RFetchTeacherGroupsResponse
    suspend fun fetchStudentsInGroup(r: RFetchStudentsInGroupReceive): RFetchStudentsInGroupResponse

    suspend fun fetchMainAvg(login: String, reason: String, isFirst: Boolean): RFetchMainAVGResponse
    suspend fun fetchMainHomeTasksCount(r: RFetchMainHomeTasksCountReceive) : RFetchMainHomeTasksCountResponse

    suspend fun fetchReportHeaders(): RFetchHeadersResponse
    suspend fun createReport(reportReceive: RCreateReportReceive): RCreateReportResponse

    suspend fun fetchReportData(reportId: Int): RFetchReportDataResponse

    suspend fun fetchRecentGrades(login: String): RFetchRecentGradesResponse

    suspend fun fetchPersonSchedule(dayOfWeek: String, date: String, login: String): RPersonScheduleList

    suspend fun fetchScheduleSubjects(): RFetchScheduleSubjectsResponse

    suspend fun fetchSubjectRating(r: RFetchSubjectRatingReceive): RFetchSubjectRatingResponse
}