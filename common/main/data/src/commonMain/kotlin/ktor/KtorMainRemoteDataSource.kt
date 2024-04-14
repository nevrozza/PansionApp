package ktor

import RequestPaths
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.path
import journal.init.RFetchStudentsInGroupReceive
import journal.init.RFetchStudentsInGroupResponse
import journal.init.RFetchTeacherGroupsResponse
import main.RFetchMainAVGReceive
import main.RFetchMainAVGResponse
import report.RCreateReportReceive
import report.RCreateReportResponse
import report.RFetchHeadersResponse
import report.RFetchRecentGradesReceive
import report.RFetchRecentGradesResponse
import report.RFetchReportDataReceive
import report.RFetchReportDataResponse

class KtorMainRemoteDataSource(
    private val httpClient: HttpClient
) {

    suspend fun fetchRecentGrades(r: RFetchRecentGradesReceive) : RFetchRecentGradesResponse {
        return httpClient.post {
            url {
                bearer()
                path(RequestPaths.Reports.FetchRecentGrades)
                setBody(r)
            }
        }.body()
    }
    suspend fun fetchTeacherGroups(): RFetchTeacherGroupsResponse {
        val response = httpClient.post {
            bearer()
            url {
                path(RequestPaths.Lessons.FetchTeacherGroups)
            }
        }
        return response.body()
    }

    suspend fun fetchMainAvg(request: RFetchMainAVGReceive): RFetchMainAVGResponse {
        val response = httpClient.post {
            bearer()
            url {
                contentType(ContentType.Application.Json)
                path(RequestPaths.Main.FetchMainAVG)
                setBody(request)
            }
        }
        return response.body()
    }

    suspend fun fetchStudentInGroup(request: RFetchStudentsInGroupReceive): RFetchStudentsInGroupResponse {
        val response = httpClient.post {
            bearer()
            url {
                contentType(ContentType.Application.Json)
                path(RequestPaths.Lessons.FetchStudentsInGroup)
                setBody(request)
            }
        }

        return response.body()
    }

    suspend fun fetchReportHeaders(): RFetchHeadersResponse {
        val response = httpClient.post {
            bearer()
            url {
//                contentType(ContentType.Application.Json)
                path(RequestPaths.Reports.FetchReportHeaders)
            }
        }
        return response.body()
    }

    suspend fun createReport(r: RCreateReportReceive): RCreateReportResponse {
        val response = httpClient.post {
            bearer()
            url {
//                contentType(ContentType.Application.Json)
                path(RequestPaths.Reports.CreateReport)
                setBody(r)
            }
        }
        return response.body()
    }

    suspend fun fetchReportData(r: RFetchReportDataReceive): RFetchReportDataResponse {
        val response = httpClient.post {
            bearer()
            url {
//                contentType(ContentType.Application.Json)
                path(RequestPaths.Reports.FetchReportData)
                setBody(r)
            }
        }
        return response.body()
    }
}