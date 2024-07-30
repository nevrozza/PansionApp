import achievements.RFetchAchievementsForStudentReceive
import achievements.RFetchAchievementsResponse
import homework.RCheckHomeTaskReceive
import homework.RFetchHomeTasksReceive
import homework.RFetchHomeTasksResponse
import homework.RFetchReportHomeTasksReceive
import homework.RFetchReportHomeTasksResponse
import homework.RFetchTasksInitReceive
import homework.RFetchTasksInitResponse
import homework.RSaveReportHomeTasksReceive
import report.RFetchAllGroupMarksResponse
import report.RFetchDetailedStupsResponse
import report.RFetchDnevnikRuMarksResponse
import report.RFetchReportStudentsReceive
import report.RFetchReportStudentsResponse
import report.RFetchSubjectQuarterMarksResponse
import report.RIsQuartersResponse
import report.RUpdateReportReceive

interface JournalRepository {

    suspend fun fetchAchievementsForStudent(r: RFetchAchievementsForStudentReceive) : RFetchAchievementsResponse


    suspend fun checkHomeTask(r: RCheckHomeTaskReceive)

    suspend fun fetchHomeTasksInit(r: RFetchTasksInitReceive) : RFetchTasksInitResponse
    suspend fun fetchHomeTasks(r: RFetchHomeTasksReceive) : RFetchHomeTasksResponse

    suspend fun saveReportHomeTasks(r: RSaveReportHomeTasksReceive) : RFetchReportHomeTasksResponse
    suspend fun fetchReportHomeTasks(r: RFetchReportHomeTasksReceive) : RFetchReportHomeTasksResponse

    suspend fun updateWholeReport(r: RUpdateReportReceive)
    suspend fun fetchReportStudents(r: RFetchReportStudentsReceive) : RFetchReportStudentsResponse

    suspend fun fetchDnevnikRuMarks(login: String, quartersNum: String, isQuarters: Boolean) : RFetchDnevnikRuMarksResponse

    suspend fun fetchFullReportData(reportId: Int) : ReportData

    suspend fun fetchSubjectQuarterMarks(login: String, subjectId: Int, quartersNum: String) : RFetchSubjectQuarterMarksResponse

    suspend fun fetchIsQuarter(login: String) : RIsQuartersResponse

    suspend fun fetchAllStups(login: String) : RFetchDetailedStupsResponse

    suspend fun fetchAllGroupMarks(groupId: Int, subjectId: Int) : RFetchAllGroupMarksResponse
//    suspend fun activate(login: String, password: String): ActivationResponse
//    suspend fun checkActivation(login: String): CheckActivationResponse

//    fun isUserLoggedIn(): Boolean
//    fun fetchToken(): String
//    fun deleteToken()
////    fun saveName(name: String)
//    fun fetchName(): String
////    fun saveSurname(surname: String)
//    fun fetchSurname(): String
////    fun savePraname(praname: String)
//    fun fetchPraname(): String
//    fun fetchRole(): String
//    fun fetchModeration(): String
//    fun fetchLogin(): String
//
//    suspend fun logout()
}