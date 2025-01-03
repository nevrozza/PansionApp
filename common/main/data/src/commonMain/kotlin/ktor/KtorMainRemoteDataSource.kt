package ktor

import RequestPaths
import checkOnNoOk
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.path
import journal.init.RFetchMentorGroupIdsResponse
import journal.init.RFetchStudentsInGroupReceive
import journal.init.RFetchStudentsInGroupResponse
import journal.init.RFetchTeacherGroupsResponse
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
import report.RFetchRecentGradesReceive
import report.RFetchRecentGradesResponse
import report.RFetchReportDataReceive
import report.RFetchReportDataResponse
import schedule.RFetchPersonScheduleReceive
import schedule.RPersonScheduleList

class KtorMainRemoteDataSource(
    private val hc: HttpClient
) {
    suspend fun updateTodayDuty(r: RUpdateTodayDuty): Boolean =
        hc.dPost(RequestPaths.Main.UpdateDuty, r).check()


    suspend fun startNewDayDuty(r: RStartNewDayDuty): Boolean =
        hc.dPost(RequestPaths.Main.StartNewDayDuty, r).check()


    suspend fun fetchDuty(r: RFetchDutyReceive): RFetchDutyResponse =
        hc.dPost(RequestPaths.Main.FetchDuty, r).body()


    suspend fun fetchMinistrySettings(r: RFetchMinistryStudentsReceive): RFetchMinistrySettingsResponse =
        hc.dPost(RequestPaths.Main.FetchMinistrySettings, r).body()

    suspend fun createMinistryStudent(r: RCreateMinistryStudentReceive): RFetchMinistrySettingsResponse =
        hc.dPost(RequestPaths.Main.CreateMinistryStudent, r).body()


    suspend fun changeToUv(r: RChangeToUv): Boolean =
        hc.dPost(RequestPaths.Main.ChangeToUv, r).check()


    suspend fun fetchSchoolData(r: RFetchSchoolDataReceive): RFetchSchoolDataResponse =
        hc.dPost(RequestPaths.Main.FetchSchoolData, r).body()


    suspend fun fetchJournalBySubjects(r: RFetchJournalBySubjectsReceive): RFetchJournalBySubjectsResponse =
        hc.dPost(RequestPaths.Mentoring.FetchJournalBySubjects, r).body()

    suspend fun openRegistrationQR(r: OpenRequestQRReceive): Boolean =
        hc.dPost(RequestPaths.Registration.OpenQR, r).check()


    suspend fun solveRegistrationRequest(r: SolveRequestReceive): Boolean =
        hc.dPost(RequestPaths.Registration.SolveRequest, r).check()


    suspend fun closeRegistrationQR(r: CloseRequestQRReceive): Boolean =
        hc.dPost(RequestPaths.Registration.CloseQR, r).check()

    suspend fun fetchMentorGroupIds(): RFetchMentorGroupIdsResponse =
        hc.dPost(RequestPaths.Main.FetchMentorGroupIds).body()


    suspend fun fetchMainNotifications(r: RFetchMainNotificationsReceive): RFetchMainNotificationsResponse =
        hc.dPost(RequestPaths.Main.FetchNotifications, r).body()

    suspend fun fetchChildrenMainNotifications(): RFetchChildrenMainNotificationsResponse =
        hc.dPost(RequestPaths.Main.FetchChildrenNotifications).body()

    suspend fun fetchChildren(): RFetchChildrenResponse =
        hc.dPost(RequestPaths.Main.FetchChildren).body()

    suspend fun deleteMainNotification(r: RDeleteMainNotificationsReceive): Boolean =
        hc.dPost(RequestPaths.Main.CheckNotification, r).check()


    suspend fun savePreAttendanceDay(r: RSavePreAttendanceDayReceive): Boolean =
        hc.dPost(RequestPaths.Mentoring.SavePreAttendanceDay, r).check()

    suspend fun fetchPreAttendanceDay(r: RFetchPreAttendanceDayReceive): RFetchPreAttendanceDayResponse =
        hc.dPost(RequestPaths.Mentoring.FetchPreAttendanceDay, r).body()


    suspend fun fetchMentorStudents(): RFetchMentoringStudentsResponse =
        hc.dPost(RequestPaths.Mentoring.FetchMentoringStudents).body()


    suspend fun fetchScheduleSubjects(): RFetchScheduleSubjectsResponse =
        hc.dPost(RequestPaths.Main.FetchScheduleSubjects).body()


    suspend fun fetchSubjectRating(r: RFetchSubjectRatingReceive): RFetchSubjectRatingResponse =
        hc.dPost(RequestPaths.Main.FetchSubjectRating, r).body()

    suspend fun fetchPersonSchedule(r: RFetchPersonScheduleReceive): RPersonScheduleList =
        hc.dPost(RequestPaths.Lessons.FetchPersonSchedule, r).body()


    suspend fun fetchRecentGrades(r: RFetchRecentGradesReceive): RFetchRecentGradesResponse =
        hc.dPost(RequestPaths.Reports.FetchRecentGrades, r).body()

    suspend fun fetchTeacherGroups(): RFetchTeacherGroupsResponse =
        hc.dPost(RequestPaths.Lessons.FetchTeacherGroups).body()

    suspend fun fetchMainAvg(r: RFetchMainAVGReceive): RFetchMainAVGResponse =
        hc.dPost(RequestPaths.Main.FetchMainAVG, r).body()

    suspend fun fetchMainHomeTasksCount(r: RFetchMainHomeTasksCountReceive): RFetchMainHomeTasksCountResponse =
        hc.dPost(RequestPaths.Main.FetchHomeTasksCount, r).body()

    suspend fun fetchStudentInGroup(r: RFetchStudentsInGroupReceive): RFetchStudentsInGroupResponse =
        hc.dPost(RequestPaths.Lessons.FetchStudentsInGroup, r).body()

    suspend fun fetchReportHeaders(): RFetchHeadersResponse =
        hc.dPost(RequestPaths.Reports.FetchReportHeaders).body()

    suspend fun createReport(r: RCreateReportReceive): RCreateReportResponse =
        hc.dPost(RequestPaths.Reports.CreateReport, r).body()

    suspend fun fetchReportData(r: RFetchReportDataReceive): RFetchReportDataResponse =
        hc.dPost(RequestPaths.Reports.FetchReportData, r).body()
}