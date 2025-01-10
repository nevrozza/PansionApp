import achievements.RFetchAchievementsForStudentReceive
import achievements.RFetchAchievementsResponse
import homework.RCheckHomeTaskReceive
import homework.RFetchGroupHomeTasksReceive
import homework.RFetchGroupHomeTasksResponse
import homework.RFetchHomeTasksReceive
import homework.RFetchHomeTasksResponse
import homework.RFetchReportHomeTasksReceive
import homework.RFetchReportHomeTasksResponse
import homework.RFetchTasksInitReceive
import homework.RFetchTasksInitResponse
import homework.RSaveReportHomeTasksReceive
import main.school.RFetchMinistryHeaderInitResponse
import main.school.RMinistryListReceive
import main.school.RMinistryListResponse
import main.school.RUploadMinistryStup
import rating.RFetchFormRatingReceive
import rating.RFetchFormRatingResponse
import rating.RFetchFormsForFormResponse
import report.*

interface JournalRepository {


    suspend fun toMarkLesson(r: RMarkLessonReceive)

    suspend fun uploadMinistryStup(r: RUploadMinistryStup)

    suspend fun fetchMinistryList(r: RMinistryListReceive) : RMinistryListResponse

    suspend fun fetchMinistryHeaderInit() : RFetchMinistryHeaderInitResponse

    suspend fun fetchFormRating(r: RFetchFormRatingReceive) : RFetchFormRatingResponse

    suspend fun fetchFormsForFormRating() : RFetchFormsForFormResponse

    suspend fun fetchAchievementsForStudent(r: RFetchAchievementsForStudentReceive) : RFetchAchievementsResponse
    suspend fun fetchStudentLines(r: RFetchStudentLinesReceive) : RFetchStudentLinesResponse
    suspend fun fetchStudentReport(r: RFetchStudentReportReceive) : RFetchStudentReportResponse


    suspend fun checkHomeTask(r: RCheckHomeTaskReceive)

    suspend fun fetchHomeTasksInit(r: RFetchTasksInitReceive) : RFetchTasksInitResponse
    suspend fun fetchHomeTasks(r: RFetchHomeTasksReceive) : RFetchHomeTasksResponse

    suspend fun saveReportHomeTasks(r: RSaveReportHomeTasksReceive) : RFetchReportHomeTasksResponse
    suspend fun fetchReportHomeTasks(r: RFetchReportHomeTasksReceive) : RFetchReportHomeTasksResponse
    suspend fun fetchGroupHomeTasks(r: RFetchGroupHomeTasksReceive) : RFetchGroupHomeTasksResponse

    suspend fun updateWholeReport(r: RUpdateReportReceive)
    suspend fun fetchReportStudents(r: RFetchReportStudentsReceive) : RFetchReportStudentsResponse

    suspend fun fetchDnevnikRuMarks(r: RFetchDnevnikRuMarksReceive) : RFetchDnevnikRuMarksResponse

    suspend fun fetchFullReportData(reportId: Int) : ReportData

    suspend fun fetchSubjectQuarterMarks(r: RFetchSubjectQuarterMarksReceive) : RFetchSubjectQuarterMarksResponse

    suspend fun fetchIsQuarter(login: String) : RIsQuartersResponse

    suspend fun fetchAllStups(r: RFetchDetailedStupsReceive) : RFetchDetailedStupsResponse

    suspend fun fetchAllGroupMarks(r: RFetchAllGroupMarksReceive) : RFetchAllGroupMarksResponse

}