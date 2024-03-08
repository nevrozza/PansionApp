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
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.path

class KtorAdminRemoteDataSource(
    private val httpClient: HttpClient
) {
    suspend fun performRegistrationUser(request: RegisterReceive): RegisterResponse {
        val response = httpClient.post {
            bearer()
            url {

                path("server/user/register")
                setBody(request)
            }
        }

        return response.body()
    }

    suspend fun performFetchAllUsers(): FetchAllUsersResponse {

        return httpClient.post {
            bearer()
            url {
                path("server/user/fetchAllUsers")
            }
        }.body()

    }

    suspend fun clearUserPassword(request: ClearUserPasswordReceive): ClearUserPasswordResponse {
        return httpClient.post {
            bearer()
            url {
                path("server/user/clearPassword")
                setBody(request)
            }
        }.body()

    }

    suspend fun performEditUser(request: EditUserReceive): EditUserResponse {
        return httpClient.post {
            bearer()
            url {
                path("server/user/edit")
                setBody(request)
            }
        }.body()

    }


    suspend fun performFetchAllSubject(): FetchAllGSubjectsResponse {
        return httpClient.post {
            bearer()
            url {
                path("server/lessons/fetchAllGSubjects")
            }
        }.body()

    }
    suspend fun performStudentsInForm(request: FetchStudentsInFormReceive): FetchStudentsInFormResponse {
        return httpClient.post {
            bearer()
            url {
                path("server/lessons/fetchStudentsInForm")
                setBody(request)
            }
        }.body()

    }

    suspend fun performFormGroups(request: FetchFormGroupsReceive): FetchFormGroupsResponse {
        return httpClient.post {
            bearer()
            url {
                path("server/lessons/fetchFormGroups")
                setBody(request)
            }
        }.body()

    }

    suspend fun createNewSubject(request: CreateNewGSubjectReceive): CreateNewGSubjectResponse {
        return httpClient.post {
            bearer()
            url {
                path("server/lessons/createGSubject")
                setBody(request)
            }
        }.body()
    }

    suspend fun createFormGroup(request: CreateFormGroupsReceive): CreateFormGroupsResponse {
        return httpClient.post {
            bearer()
            url {
                path("server/lessons/createFormGroup")
                setBody(request)
            }
        }.body()
    }

    suspend fun createUserForm(request: CreateUserFormReceive): CreateUserFormResponse {
        return httpClient.post {
            bearer()
            url {
                path("server/lessons/createUserForm")
                setBody(request)
            }
        }.body()
    }

    suspend fun createGroup(request: CreateNewGroupReceive): CreateNewGroupResponse {
        return httpClient.post {
            bearer()
            url {
                path("server/lessons/createGroup")
                setBody(request)
            }
        }.body()
    }

    suspend fun createForm(request: CreateNewFormReceive): CreateNewFormResponse {
        return httpClient.post {
            bearer()
            url {
                path("server/lessons/createForm")
                setBody(request)
            }
        }.body()
    }

    suspend fun performFetchSubjectGroups(request: FetchSubjectGroupsReceive): FetchSubjectGroupsResponse {
        return httpClient.post {
            bearer()
            url {
                path("server/lessons/fetchSubjectGroups")
                setBody(request)
            }
        }.body()
    }
    suspend fun performFetchStudentGroups(request: FetchStudentGroupsOfStudentReceive): FetchStudentGroupsOfStudentResponse {
        return httpClient.post {
            bearer()
            url {
                path("server/lessons/fetchStudentGroups")
                setBody(request)
            }
        }.body()
    }

    suspend fun performFetchAllForms(): FetchAllFormsResponse {
        return httpClient.post {
            bearer()
            url {
                path("server/lessons/fetchAllForms")
            }
        }.body()
    }

    suspend fun performFetchTeachersForGroups(): FetchAllTeachersForGroupsResponse {
        return httpClient.post {
            bearer()
            url {
                path("server/lessons/fetchTeachersForGroup")
            }
        }.body()
    }

    suspend fun performFetchMentorsForGroups(): FetchAllMentorsForGroupsResponse {
        return httpClient.post {
            bearer()
            url {
                path("server/lessons/fetchMentorsForGroup")
            }
        }.body()
    }

    suspend fun performFetchSubjectFormGroups(request: FetchFormGroupsOfSubjectReceive): FetchFormGroupsOfSubjectResponse {
        return httpClient.post {
            bearer()
            url {
                contentType(ContentType.Application.Json)
                path("server/lessons/fetchSubjectFormGroups")
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