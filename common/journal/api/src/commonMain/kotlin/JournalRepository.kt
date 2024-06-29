import report.RFetchAllGroupMarksResponse
import report.RFetchDetailedStupsResponse
import report.RFetchDnevnikRuMarksResponse
import report.RFetchReportStudentsReceive
import report.RFetchReportStudentsResponse
import report.RFetchSubjectQuarterMarksResponse
import report.RIsQuartersResponse
import report.RUpdateReportReceive

interface JournalRepository {
    suspend fun updateWholeReport(r: RUpdateReportReceive)
    suspend fun fetchReportStudents(r: RFetchReportStudentsReceive) : RFetchReportStudentsResponse

    suspend fun fetchDnevnikRuMarks(login: String, quartersNum: String, isQuarters: Boolean) : RFetchDnevnikRuMarksResponse

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