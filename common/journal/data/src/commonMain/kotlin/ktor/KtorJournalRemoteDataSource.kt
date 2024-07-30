package ktor

import ReportData
import RequestPaths
import checkOnNoOk
import homework.RCheckHomeTaskReceive
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
import report.RFetchAllGroupMarksReceive
import report.RFetchAllGroupMarksResponse
import report.RFetchDetailedStupsReceive
import report.RFetchDetailedStupsResponse
import report.RFetchDnevnikRuMarksReceive
import report.RFetchDnevnikRuMarksResponse
import report.RFetchFullReportData
import report.RFetchReportStudentsReceive
import report.RFetchReportStudentsResponse
import report.RFetchSubjectQuarterMarksReceive
import report.RFetchSubjectQuarterMarksResponse
import report.RIsQuartersReceive
import report.RIsQuartersResponse
import report.RUpdateReportReceive

class KtorJournalRemoteDataSource(
    private val httpClient: HttpClient
) {

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