package ktor

import RequestPaths
import admin.groups.forms.RCreateFormGroupReceive
import admin.groups.forms.RFetchCutedGroupsResponse
import admin.users.RClearUserPasswordReceive
import admin.groups.forms.outside.CreateFormReceive
import admin.groups.subjects.RCreateGroupReceive
import admin.groups.students.RBindStudentToFormReceive
import admin.users.REditUserReceive
import admin.groups.forms.outside.RFetchFormsResponse
import admin.groups.subjects.topBar.RFetchAllSubjectsResponse
import admin.groups.forms.outside.RFetchMentorsResponse
import admin.groups.subjects.RFetchTeachersResponse
import admin.groups.subjects.RFetchGroupsReceive
import admin.groups.forms.RFetchFormGroupsReceive
import admin.groups.forms.RFetchFormGroupsResponse
import admin.groups.students.deep.RFetchStudentGroupsReceive
import admin.groups.students.deep.RFetchStudentGroupsResponse
import admin.groups.students.RFetchStudentsInFormReceive
import admin.groups.students.RFetchStudentsInFormResponse
import admin.groups.students.deep.RCreateStudentGroupReceive
import admin.groups.subjects.RFetchGroupsResponse
import admin.groups.subjects.topBar.RCreateSubjectReceive
import admin.users.RRegisterUserReceive
import admin.users.RCreateUserResponse
import admin.users.RFetchAllUsersResponse
import checkOnNoOk
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.path

class KtorAdminRemoteDataSource(
    private val httpClient: HttpClient
) {
    suspend fun performRegistrationUser(request: RRegisterUserReceive): RCreateUserResponse {
        val response = httpClient.post {
            bearer()
            url {

                path(RequestPaths.UserManage.CreateUser)
                setBody(request)
            }
        }

        return response.body()
    }

    suspend fun performFetchAllUsers(): RFetchAllUsersResponse {
        return httpClient.post {
            bearer()
            url {
//                contentType(ContentType.Application.Json)
                path(RequestPaths.UserManage.FetchAllUsers)
            }
        }.body()
    }

    suspend fun clearUserPassword(request: RClearUserPasswordReceive) {
        httpClient.post {
            bearer()
            url {
                path(RequestPaths.UserManage.ClearPasswordAdmin)
                setBody(request)
            }
        }.status.value.checkOnNoOk()
    }

    suspend fun performEditUser(request: REditUserReceive) {
        httpClient.post {
            bearer()
            url {
                path(RequestPaths.UserManage.EditUser)
                setBody(request)
            }
        }.status.value.checkOnNoOk()

    }


    suspend fun performFetchAllSubjects(): RFetchAllSubjectsResponse {
        return httpClient.post {
            bearer()
            url {
                path(RequestPaths.Lessons.FetchAllSubjects)
            }
        }.body()

    }
    suspend fun performStudentsInForm(request: RFetchStudentsInFormReceive): RFetchStudentsInFormResponse {
        return httpClient.post {
            bearer()
            url {
                path(RequestPaths.Lessons.FetchStudentsInForm)
                setBody(request)
            }
        }.body()

    }

    suspend fun performFormGroups(request: RFetchFormGroupsReceive): RFetchFormGroupsResponse {
        return httpClient.post {
            bearer()
            url {
                path(RequestPaths.Lessons.FetchFormGroups)
                setBody(request)
            }
        }.body()

    }

    suspend fun createNewSubject(request: RCreateSubjectReceive) {
        httpClient.post {
            bearer()
            url {
                path(RequestPaths.Lessons.CreateSubject)
                setBody(request)
            }
        }.status.value.checkOnNoOk()
    }

    suspend fun createFormGroup(request: RCreateFormGroupReceive){
        httpClient.post {
            bearer()
            url {
                path(RequestPaths.Lessons.CreateFormGroup)
                setBody(request)
            }
        }.status.value.checkOnNoOk()
    }

    suspend fun deleteFormGroup(request: RCreateFormGroupReceive){
        httpClient.post {
            bearer()
            url {
                path(RequestPaths.Lessons.DeleteFormGroup)
                setBody(request)
            }
        }.status.value.checkOnNoOk()
    }

    suspend fun createStudentGroup(request: RCreateStudentGroupReceive){
        httpClient.post {
            bearer()
            url {
                path(RequestPaths.Lessons.CreateStudentGroup)
                setBody(request)
            }
        }.status.value.checkOnNoOk()
    }

    suspend fun deleteStudentGroup(request: RCreateStudentGroupReceive){
        httpClient.post {
            bearer()
            url {
                path(RequestPaths.Lessons.DeleteStudentGroup)
                setBody(request)
            }
        }.status.value.checkOnNoOk()
    }


    suspend fun bindStudentToForm(request: RBindStudentToFormReceive){
        return httpClient.post {
            bearer()
            url {
                path(RequestPaths.Lessons.BindStudentToForm)
                setBody(request)
            }
        }.status.value.checkOnNoOk()
    }

    suspend fun createGroup(request: RCreateGroupReceive) {
        return httpClient.post {
            bearer()
            url {
                path(RequestPaths.Lessons.CreateGroup)
                setBody(request)
            }
        }.status.value.checkOnNoOk()
    }

    suspend fun createForm(request: CreateFormReceive) {
        return httpClient.post {
            bearer()
            url {
                path(RequestPaths.Lessons.CreateForm)
                setBody(request)
            }
        }.status.value.checkOnNoOk()
    }

    suspend fun performFetchGroups(request: RFetchGroupsReceive): RFetchGroupsResponse {
        return httpClient.post {
            bearer()
            url {
                path(RequestPaths.Lessons.FetchGroups)
                setBody(request)
            }
        }.body()
    }
    suspend fun performFetchStudentGroups(request: RFetchStudentGroupsReceive): RFetchStudentGroupsResponse {
        return httpClient.post {
            bearer()
            url {
                path(RequestPaths.Lessons.FetchStudentGroups)
                setBody(request)
            }
        }.body()
    }

    suspend fun performFetchAllForms(): RFetchFormsResponse {
        return httpClient.post {
            bearer()
            url {
                path(RequestPaths.Lessons.FetchAllForms)
            }
        }.body()
    }

    suspend fun performFetchTeachersForGroup(): RFetchTeachersResponse {
        return httpClient.post {
            bearer()
            url {
                path(RequestPaths.Lessons.FetchTeachersForGroup)
            }
        }.body()
    }

    suspend fun performFetchMentorsForGroups(): RFetchMentorsResponse {
        return httpClient.post {
            bearer()
            url {
                path(RequestPaths.Lessons.FetchMentorsForGroup)
            }
        }.body()
    }

    suspend fun performFetchCutedGroups(request: RFetchGroupsReceive): RFetchCutedGroupsResponse {
        return httpClient.post {
            bearer()
            url {
                contentType(ContentType.Application.Json)
                path(RequestPaths.Lessons.FetchCutedGroups)
                setBody(request)
            }
        }.body()
    }
//    suspend fun performCheckActivation(request: CheckActivationReceive) : CheckActivationResponse {
//        return httpClient.post {
//            url {
//                path("check/auth")
//                setBody(request)
//            }
//        }.body()
//    }
}