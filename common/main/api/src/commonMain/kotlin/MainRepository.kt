import journal.init.RFetchStudentsInGroupResponse
import journal.init.RFetchTeacherGroupsResponse
import main.RFetchMainAVGResponse
import report.RCreateReportReceive
import report.RCreateReportResponse
import report.RFetchHeadersResponse
import report.RFetchRecentGradesResponse
import report.RFetchReportDataResponse

interface MainRepository {
    suspend fun fetchTeacherGroups(): RFetchTeacherGroupsResponse
    suspend fun fetchStudentsInGroup(groupId: Int): RFetchStudentsInGroupResponse

    suspend fun fetchMainAvg(login: String, reason: String) : RFetchMainAVGResponse

    suspend fun fetchReportHeaders(): RFetchHeadersResponse
    suspend fun createReport(reportReceive: RCreateReportReceive): RCreateReportResponse

    suspend fun fetchReportData(reportId: Int) : RFetchReportDataResponse

    suspend fun fetchRecentGrades(login: String) : RFetchRecentGradesResponse
}