package ktor

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

class KtorMainRemoteDataSource(
    private val httpClient: HttpClient
) {
    suspend fun fetchTeacherGroups(): RFetchTeacherGroupsResponse {
        val response = httpClient.post {
            bearer()
            url {
                path("server/lessons/fetchTeacherGroups")
            }
        }

        return response.body()
    }
    suspend fun fetchStudentInGroup(request: RFetchStudentsInGroupReceive): RFetchStudentsInGroupResponse {
        val response = httpClient.post {
            bearer()
            url {
                contentType(ContentType.Application.Json)
                path("server/lessons/fetchStudentsInGroup")
                setBody(request)
            }
        }

        return response.body()
    }
}