package ktor

import ReportData
import RequestPaths
import achievements.RFetchAchievementsForStudentReceive
import achievements.RFetchAchievementsResponse
import checkOnNoOk
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
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.path
import main.school.RFetchMinistryHeaderInitResponse
import main.school.RMinistryListReceive
import main.school.RMinistryListResponse
import main.school.RUploadMinistryStup
import rating.RFetchFormRatingReceive
import rating.RFetchFormRatingResponse
import rating.RFetchFormsForFormResponse
import report.*

class KtorJournalRemoteDataSource(
    private val hc: HttpClient
) {
    suspend fun markLesson(r: RMarkLessonReceive): Boolean =
        hc.dPost(RequestPaths.Reports.MarkLesson, r).check()

    suspend fun uploadMinistryStup(r: RUploadMinistryStup): Boolean =
        hc.dPost(RequestPaths.Main.UploadMinistryStup, r).check()

    suspend fun fetchMinistryList(r: RMinistryListReceive): RMinistryListResponse =
        hc.dPost(RequestPaths.Main.FetchMinistryList, r).dBody()

    suspend fun fetchMinistryHeaderInit(): RFetchMinistryHeaderInitResponse =
        hc.dPost(RequestPaths.Main.FetchMinistryHeaderInit).dBody()

    suspend fun fetchFormsForFormRating(): RFetchFormsForFormResponse =
        hc.dPost(RequestPaths.Main.FetchFormsForFormRating).dBody()

    suspend fun fetchFormRating(r: RFetchFormRatingReceive): RFetchFormRatingResponse =
        hc.dPost(RequestPaths.Main.FetchFormRating, r).dBody()


    suspend fun fetchAchievementsForStudent(r: RFetchAchievementsForStudentReceive): RFetchAchievementsResponse =
        hc.dPost(RequestPaths.Achievements.FetchForStudent, r).dBody()

    suspend fun fetchStudentReport(r: RFetchStudentReportReceive): RFetchStudentReportResponse =
        hc.dPost(RequestPaths.Reports.FetchStudentReport, r).dBody()


    suspend fun fetchStudentLines(r: RFetchStudentLinesReceive): RFetchStudentLinesResponse =
        hc.dPost(RequestPaths.Reports.FetchStudentLines, r).dBody()

    suspend fun checkHomeTask(r: RCheckHomeTaskReceive): Boolean =
        hc.dPost(RequestPaths.HomeTasks.CheckTask, r).check()


    suspend fun fetchHomeTasks(r: RFetchHomeTasksReceive): RFetchHomeTasksResponse =
        hc.dPost(RequestPaths.HomeTasks.FetchHomeTasks, r).dBody()

    suspend fun fetchHomeTasksInit(r: RFetchTasksInitReceive): RFetchTasksInitResponse =
        hc.dPost(RequestPaths.HomeTasks.FetchHomeTasksInit, r).dBody()

    suspend fun saveReportHomeTasks(r: RSaveReportHomeTasksReceive): RFetchReportHomeTasksResponse =
        hc.dPost(RequestPaths.HomeTasks.SaveReportHomeTasks, r).dBody()

    suspend fun fetchReportHomeTasks(r: RFetchReportHomeTasksReceive): RFetchReportHomeTasksResponse =
        hc.dPost(RequestPaths.HomeTasks.FetchReportHomeTasks, r).dBody()

    suspend fun fetchGroupHomeTasks(r: RFetchGroupHomeTasksReceive): RFetchGroupHomeTasksResponse =
        hc.dPost(RequestPaths.HomeTasks.FetchGroupHomeTasks, r).dBody()

    suspend fun fetchFullReportData(r: RFetchFullReportData): ReportData =
        hc.dPost(RequestPaths.Reports.FetchFullReportData, r).dBody()

    suspend fun fetchReportStudents(r: RFetchReportStudentsReceive): RFetchReportStudentsResponse =
        hc.dPost(RequestPaths.Reports.FetchReportStudents, r).dBody()

    suspend fun fetchAllStups(r: RFetchDetailedStupsReceive): RFetchDetailedStupsResponse =
        hc.dPost(RequestPaths.Reports.FetchDetailedStups, r).dBody()

    suspend fun fetchAllGroupMarks(r: RFetchAllGroupMarksReceive): RFetchAllGroupMarksResponse =
        hc.dPost(RequestPaths.Reports.FetchAllGroupMarks, r).dBody()

    suspend fun updateWholeReport(r: RUpdateReportReceive) : Boolean =
        hc.dPost(RequestPaths.Reports.UpdateReport, r).check()

    suspend fun fetchDnevnikRuMarks(r: RFetchDnevnikRuMarksReceive): RFetchDnevnikRuMarksResponse =
        hc.dPost(RequestPaths.Reports.FetchDnevnikRuMarks, r).dBody()

    suspend fun fetchIsQuarter(r: RIsQuartersReceive): RIsQuartersResponse =
        hc.dPost(RequestPaths.Reports.FetchIsQuarters, r).dBody()

    suspend fun fetchSubjectQuarterMarks(r: RFetchSubjectQuarterMarksReceive): RFetchSubjectQuarterMarksResponse =
        hc.dPost(RequestPaths.Reports.FetchSubjectQuarterMarks, r).dBody()
}