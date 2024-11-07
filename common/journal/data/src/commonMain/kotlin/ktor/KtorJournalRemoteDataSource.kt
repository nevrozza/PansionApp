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
    private val httpClient: HttpClient
) {
    suspend fun markLesson(r: RMarkLessonReceive) {
        return httpClient.post {
            url {
                bearer()
                setBody(r)
                path(RequestPaths.Reports.MarkLesson)
            }
        }.status.value.checkOnNoOk()
    }
    suspend fun uploadMinistryStup(r: RUploadMinistryStup) {
        return httpClient.post {
            url {
                bearer()
                setBody(r)
                path(RequestPaths.Main.UploadMinistryStup)
            }
        }.status.value.checkOnNoOk()
    }

    suspend fun fetchMinistryList(r: RMinistryListReceive) : RMinistryListResponse {
        return httpClient.post {
            url {
                bearer()
                setBody(r)
                path(RequestPaths.Main.FetchMinistryList)
            }
        }.body()
    }

    suspend fun fetchMinistryHeaderInit() : RFetchMinistryHeaderInitResponse {
        return httpClient.post {
            url {
                bearer()
                path(RequestPaths.Main.FetchMinistryHeaderInit)
            }
        }.body()
    }
    suspend fun fetchFormsForFormRating() : RFetchFormsForFormResponse {
        return httpClient.post {
            url {
                bearer()
                path(RequestPaths.Main.FetchFormsForFormRating)
            }
        }.body()
    }
    suspend fun fetchFormRating(r: RFetchFormRatingReceive) : RFetchFormRatingResponse {
        return httpClient.post {
            url {
                bearer()
                path(RequestPaths.Main.FetchFormRating)
                setBody(r)
            }
        }.body()
    }


    suspend fun fetchAchievementsForStudent(r: RFetchAchievementsForStudentReceive): RFetchAchievementsResponse {
        return httpClient.post {
            url {
                bearer()
                path(RequestPaths.Achievements.FetchForStudent)
                setBody(r)
            }
        }.body()
    }

    suspend fun fetchStudentReport(r: RFetchStudentReportReceive): RFetchStudentReportResponse {
        return httpClient.post {
            url {
                bearer()
                path(RequestPaths.Reports.FetchStudentReport)
                setBody(r)
            }
        }.body()
    }

    suspend fun fetchStudentLines(r: RFetchStudentLinesReceive): RFetchStudentLinesResponse {
        return httpClient.post {
            url {
                bearer()
                path(RequestPaths.Reports.FetchStudentLines)
                setBody(r)
            }
        }.body()
    }

    suspend fun checkHomeTask(r: RCheckHomeTaskReceive) {
        httpClient.post {
            url {
                bearer()
                path(RequestPaths.HomeTasks.CheckTask)
                setBody(r)
            }
        }.status.value.checkOnNoOk()
    }


    suspend fun fetchHomeTasks(r: RFetchHomeTasksReceive) : RFetchHomeTasksResponse {
        return httpClient.post {
            url {
                bearer()
                path(RequestPaths.HomeTasks.FetchHomeTasks)
                setBody(r)
            }
        }.body()
    }

    suspend fun fetchHomeTasksInit(r: RFetchTasksInitReceive) : RFetchTasksInitResponse {
        return httpClient.post {
            url {
                bearer()
                path(RequestPaths.HomeTasks.FetchHomeTasksInit)
                setBody(r)
            }
        }.body()
    }

    suspend fun saveReportHomeTasks(r: RSaveReportHomeTasksReceive) : RFetchReportHomeTasksResponse {
        return httpClient.post {
            url {
                bearer()
                path(RequestPaths.HomeTasks.SaveReportHomeTasks)
                setBody(r)
            }
        }.body()
    }

    suspend fun fetchReportHomeTasks(r: RFetchReportHomeTasksReceive) : RFetchReportHomeTasksResponse {
        return httpClient.post {
            url {
                bearer()
                path(RequestPaths.HomeTasks.FetchReportHomeTasks)
                setBody(r)
            }
        }.body()
    }
    suspend fun fetchGroupHomeTasks(r: RFetchGroupHomeTasksReceive) : RFetchGroupHomeTasksResponse {
        return httpClient.post {
            url {
                bearer()
                path(RequestPaths.HomeTasks.FetchGroupHomeTasks)
                setBody(r)
            }
        }.body()
    }

    suspend fun fetchFullReportData(r: RFetchFullReportData): ReportData {
        return httpClient.post {
            url {
                bearer()
                path(RequestPaths.Reports.FetchFullReportData)
                setBody(r)
            }
        }.body()
    }

    suspend fun fetchReportStudents(r: RFetchReportStudentsReceive): RFetchReportStudentsResponse {
        return httpClient.post {
            url {
                bearer()
                path(RequestPaths.Reports.FetchReportStudents)
                setBody(r)
            }
        }.body()
    }

    suspend fun fetchAllStups(r: RFetchDetailedStupsReceive): RFetchDetailedStupsResponse {
        return httpClient.post {
            url {
                bearer()
                path(RequestPaths.Reports.FetchDetailedStups)
                setBody(r)
            }
        }.body()
    }

    suspend fun fetchAllGroupMarks(r: RFetchAllGroupMarksReceive): RFetchAllGroupMarksResponse {
        return httpClient.post {
            url {
                bearer()
                path(RequestPaths.Reports.FetchAllGroupMarks)
                setBody(r)
            }
        }.body()
    }

    suspend fun updateWholeReport(r: RUpdateReportReceive) {
        httpClient.post {
            url {
                bearer()
                path(RequestPaths.Reports.UpdateReport)
                setBody(r)
            }
        }.status.value.checkOnNoOk()
    }

    suspend fun fetchDnevnikRuMarks(r: RFetchDnevnikRuMarksReceive): RFetchDnevnikRuMarksResponse {
        return httpClient.post {
            url {
                bearer()
                path(RequestPaths.Reports.FetchDnevnikRuMarks)
                setBody(r)
            }
        }.body()
    }

    suspend fun fetchIsQuarter(r: RIsQuartersReceive): RIsQuartersResponse {
        return httpClient.post {
            url {
                bearer()
                path(RequestPaths.Reports.FetchIsQuarters)
                setBody(r)
            }
        }.body()
    }

    suspend fun fetchSubjectQuarterMarks(r: RFetchSubjectQuarterMarksReceive): RFetchSubjectQuarterMarksResponse {
        return httpClient.post {
            url {
                bearer()
                path(RequestPaths.Reports.FetchSubjectQuarterMarks)
                setBody(r)
            }
        }.body()
    }

//    suspend fun logout(token: String) {
//        httpClient.post {
//            url {
//
//                header("Bearer-Authorization", token)
//                path(RequestPaths.Tokens.logout)
//            }
//        }
//    }
//    suspend fun performLogin(request: LoginReceive) : LoginResponse {
//        return httpClient.post {
//            url {
//                path(RequestPaths.Auth.PerformLogin)
//                setBody(request)
//            }
//        }.body()
//    }
//
//    suspend fun checkUserActivation(request: CheckActivationReceive) : CheckActivationResponse {
//        return httpClient.post {
//            url {
//                path(RequestPaths.Auth.CheckActivation)
//                setBody(request)
//            }
//        }.body()
//    }
//
//    suspend fun activate(request: ActivationReceive) : ActivationResponse {
//        return httpClient.post {
//            url {
//                path(RequestPaths.Auth.ActivateProfile)
//                setBody(request)
//            }
//        }.body()
//    }

//    suspend fun performCheckActivation(request: CheckActivationReceive) : CheckActivationResponse {
//        return httpClient.post {
//            url {
//                path("check/auth")
//                setBody(request)
//            }
//        }.body()
//    }
}