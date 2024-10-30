
import journal.init.RFetchMentorGroupIdsResponse
import journal.init.RFetchStudentsInGroupReceive
import journal.init.RFetchStudentsInGroupResponse
import journal.init.RFetchTeacherGroupsResponse
import ktor.KtorMainRemoteDataSource
import main.RChangeToUv
import main.RDeleteMainNotificationsReceive
import main.RFetchChildrenMainNotificationsResponse
import main.RFetchChildrenResponse
import main.RFetchMainAVGReceive
import main.RFetchMainAVGResponse
import main.RFetchMainHomeTasksCountReceive
import main.RFetchMainHomeTasksCountResponse
import main.RFetchMainNotificationsReceive
import main.RFetchMainNotificationsResponse
import main.RFetchSchoolDataReceive
import main.RFetchSchoolDataResponse
import main.school.RCreateMinistryStudentReceive
import main.school.RFetchMinistrySettingsResponse
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
import report.RFetchRecentGradesReceive
import report.RFetchRecentGradesResponse
import report.RFetchReportDataReceive
import report.RFetchReportDataResponse
import schedule.RFetchPersonScheduleReceive
import schedule.RPersonScheduleList

class MainRepositoryImpl(
    private val remoteDataSource: KtorMainRemoteDataSource
) : MainRepository {
    override suspend fun createMinistryStudent(r: RCreateMinistryStudentReceive): RFetchMinistrySettingsResponse {
        return remoteDataSource.createMinistryStudent(r)
    }

    override suspend fun fetchMinistrySettings(): RFetchMinistrySettingsResponse {
        return remoteDataSource.fetchMinistrySettings()
    }

    override suspend fun fetchSchoolData(r: RFetchSchoolDataReceive): RFetchSchoolDataResponse {
        return remoteDataSource.fetchSchoolData(r)
    }

    override suspend fun fetchJournalBySubjects(r: RFetchJournalBySubjectsReceive): RFetchJournalBySubjectsResponse {
        return remoteDataSource.fetchJournalBySubjects(r)
    }

    override suspend fun openRegistrationQR(r: OpenRequestQRReceive) {
        remoteDataSource.openRegistrationQR(r)
    }

    override suspend fun closeRegistrationQR(r: CloseRequestQRReceive) {
        remoteDataSource.closeRegistrationQR(r)
    }

    override suspend fun solveRegistrationRequest(r: SolveRequestReceive) {
        remoteDataSource.solveRegistrationRequest(r)
    }


    override suspend fun fetchMentorGroupIds(): RFetchMentorGroupIdsResponse {
        return remoteDataSource.fetchMentorGroupIds()
    }

    override suspend fun fetchMainNotifications(r: RFetchMainNotificationsReceive): RFetchMainNotificationsResponse {
        return remoteDataSource.fetchMainNotifications(r)
    }

    override suspend fun fetchChildrenMainNotifications(): RFetchChildrenMainNotificationsResponse {
        return remoteDataSource.fetchChildrenMainNotifications()
    }

    override suspend fun deleteMainNotification(r: RDeleteMainNotificationsReceive) {
        remoteDataSource.deleteMainNotification(r)
    }

    override suspend fun changeToUv(r: RChangeToUv) {
        remoteDataSource.changeToUv(r)
    }

    override suspend fun fetchChildren(): RFetchChildrenResponse {
        return remoteDataSource.fetchChildren()
    }

    override suspend fun fetchMentorStudents(): RFetchMentoringStudentsResponse {
        return remoteDataSource.fetchMentorStudents()
    }

    override suspend fun fetchPreAttendanceDay(r: RFetchPreAttendanceDayReceive): RFetchPreAttendanceDayResponse {
        return remoteDataSource.fetchPreAttendanceDay(r)
    }

    override suspend fun savePreAttendanceDay(r: RSavePreAttendanceDayReceive) {
        remoteDataSource.savePreAttendanceDay(r)
    }


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

    override suspend fun fetchMainAvg(login: String, reason: String, isFirst: Boolean): RFetchMainAVGResponse {
        return remoteDataSource.fetchMainAvg(
            RFetchMainAVGReceive(
                login,
                reason,
                isFirst
            )
        )
    }

    override suspend fun fetchMainHomeTasksCount(r: RFetchMainHomeTasksCountReceive): RFetchMainHomeTasksCountResponse {
        return remoteDataSource.fetchMainHomeTasksCount(r)
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

    override suspend fun fetchPersonSchedule(dayOfWeek: String, date: String, login: String): RPersonScheduleList {
        return remoteDataSource.fetchPersonSchedule(
            RFetchPersonScheduleReceive(
                dayOfWeek = dayOfWeek,
                day = date,
                login = login
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