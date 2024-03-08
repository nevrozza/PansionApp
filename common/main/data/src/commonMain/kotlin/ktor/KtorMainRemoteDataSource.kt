package ktor

import admin.ClearUserPasswordReceive
import admin.ClearUserPasswordResponse
import admin.CreateFormGroupsReceive
import admin.CreateFormGroupsResponse
import admin.CreateNewFormReceive
import admin.CreateNewFormResponse
import admin.CreateNewGSubjectReceive
import admin.CreateNewGSubjectResponse
import admin.CreateNewGroupReceive
import admin.CreateNewGroupResponse
import admin.CreateUserFormReceive
import admin.CreateUserFormResponse
import admin.EditUserReceive
import admin.EditUserResponse
import admin.FetchAllFormsResponse
import admin.FetchAllGSubjectsResponse
import admin.FetchAllMentorsForGroupsResponse
import admin.FetchAllTeachersForGroupsResponse
import admin.FetchAllUsersResponse
import admin.FetchFormGroupsOfSubjectReceive
import admin.FetchFormGroupsOfSubjectResponse
import admin.FetchFormGroupsReceive
import admin.FetchFormGroupsResponse
import admin.FetchStudentGroupsOfStudentReceive
import admin.FetchStudentGroupsOfStudentResponse
import admin.FetchStudentsInFormReceive
import admin.FetchStudentsInFormResponse
import admin.FetchSubjectGroupsReceive
import admin.FetchSubjectGroupsResponse
import admin.RegisterReceive
import admin.RegisterResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.path
import journal.init.FetchStudentsInGroupReceive
import journal.init.FetchStudentsInGroupResponse
import journal.init.FetchTeacherGroupsResponse

class KtorMainRemoteDataSource(
    private val httpClient: HttpClient
) {
    suspend fun fetchTeacherGroups(): FetchTeacherGroupsResponse {
        val response = httpClient.post {
            bearer()
            url {
                path("server/lessons/fetchTeacherGroups")
            }
        }

        return response.body()
    }
    suspend fun fetchStudentInGroup(request: FetchStudentsInGroupReceive): FetchStudentsInGroupResponse {
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