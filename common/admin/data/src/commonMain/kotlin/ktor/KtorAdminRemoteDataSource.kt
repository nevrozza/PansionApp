package ktor

import RequestPaths
import achievements.RCreateAchievementReceive
import achievements.RDeleteAchievementReceive
import achievements.REditAchievementReceive
import achievements.RFetchAchievementsResponse
import achievements.RUpdateGroupOfAchievementsReceive
import admin.cabinets.RFetchCabinetsResponse
import admin.cabinets.RUpdateCabinetsReceive
import admin.calendar.RFetchCalendarResponse
import admin.calendar.RUpdateCalendarReceive
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
import admin.groups.forms.outside.REditFormReceive
import admin.groups.students.deep.RFetchStudentGroupsReceive
import admin.groups.students.deep.RFetchStudentGroupsResponse
import admin.groups.students.RFetchStudentsInFormReceive
import admin.groups.students.RFetchStudentsInFormResponse
import admin.groups.students.deep.RCreateStudentGroupReceive
import admin.groups.subjects.RAddStudentToGroup
import admin.groups.subjects.REditGroupReceive
import admin.groups.subjects.topBar.RDeleteSubject
import admin.groups.subjects.topBar.REditSubjectReceive
import admin.groups.subjects.RFetchGroupsResponse
import admin.groups.subjects.topBar.RCreateSubjectReceive
import admin.parents.RFetchParentsListResponse
import admin.parents.RUpdateParentsListReceive
import admin.schedule.RFetchInitScheduleResponse
import admin.users.RCreateExcelStudentsReceive
import admin.users.RRegisterUserReceive
import admin.users.RCreateUserResponse
import admin.users.RDeleteUserReceive
import admin.users.RFetchAllUsersResponse
import checkOnNoOk
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.path
import schedule.RFetchScheduleDateReceive
import schedule.RScheduleList

class KtorAdminRemoteDataSource(
    private val httpClient: HttpClient
) {

    suspend fun addStudentToGroup(r: RAddStudentToGroup) {
        httpClient.post {
            url {
                bearer()
                path(RequestPaths.Lessons.AddStudentToGroupFromSubject)
                setBody(r)
            }
        }.status.value.checkOnNoOk()
    }

    suspend fun createExcelStudents(r: RCreateExcelStudentsReceive) {
       httpClient.post {
            url {
                bearer()
                path(RequestPaths.UserManage.CreateStudentsFromExcel)
                setBody(r)
            }
        }.status.value.checkOnNoOk()
    }

    suspend fun fetchParents(): RFetchParentsListResponse {
        return httpClient.post {
            url {
                bearer()
                path(RequestPaths.Parents.FetchParents)
            }
        }.body()
    }
    suspend fun updateParents(r: RUpdateParentsListReceive): RFetchParentsListResponse {
        return httpClient.post {
            url {
                bearer()
                path(RequestPaths.Parents.UpdateParent)
                setBody(r)
            }
        }.body()
    }

    suspend fun createAchievement(r: RCreateAchievementReceive) : RFetchAchievementsResponse{
        return httpClient.post {
            url {
                bearer()
                path(RequestPaths.Achievements.Create)
                setBody(r)
            }
        }.body()
    }
    suspend fun editAchievement(r: REditAchievementReceive) : RFetchAchievementsResponse{
        return httpClient.post {
            url {
                bearer()
                path(RequestPaths.Achievements.Edit)
                setBody(r)
            }
        }.body()
    }
    suspend fun updateGroupAchievement(r: RUpdateGroupOfAchievementsReceive) : RFetchAchievementsResponse{
        return httpClient.post {
            url {
                bearer()
                path(RequestPaths.Achievements.UpdateGroup)
                setBody(r)
            }
        }.body()
    }
    suspend fun deleteAchievement(r: RDeleteAchievementReceive) : RFetchAchievementsResponse{
        return httpClient.post {
            url {
                bearer()
                path(RequestPaths.Achievements.Delete)
                setBody(r)
            }
        }.body()
    }

    suspend fun fetchAllAchievements(): RFetchAchievementsResponse {
        return httpClient.post {
            url {
                bearer()
                path(RequestPaths.Achievements.FetchAll)
            }
        }.body()
    }

    suspend fun fetchSchedule(r: RFetchScheduleDateReceive) : RScheduleList {
        return httpClient.post {
            bearer()
            url {
                path(RequestPaths.Lessons.FetchSchedule)
                setBody(r)
            }
        }.body()
    }

    suspend fun saveSchedule(r: RScheduleList) {
        httpClient.post {
            bearer()
            url {
                path(RequestPaths.Lessons.SaveSchedule)
                setBody(r)
            }
        }.status.value.checkOnNoOk()
    }

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
    suspend fun performDeleteUser(request: RDeleteUserReceive) {
        httpClient.post {
            bearer()
            url {
                path(RequestPaths.UserManage.DeleteUser)
                setBody(request)
            }
        }.status.value.checkOnNoOk()

    }

    suspend fun updateCalendar(r: RUpdateCalendarReceive) {
        httpClient.post {
            bearer()
            url {
                path(RequestPaths.Lessons.UpdateCalendar)
                setBody(r)
            }
        }.status.value.checkOnNoOk()
    }

    suspend fun fetchCalendar() : RFetchCalendarResponse {
        return httpClient.post {
            bearer()
            url {
                path(RequestPaths.Lessons.FetchCalendar)
            }
        }.body()
    }

    suspend fun updateCabinets(r: RUpdateCabinetsReceive) {
        httpClient.post {
            bearer()
            url {
                path(RequestPaths.Lessons.UpdateCabinets)
                setBody(r)
            }
        }.status.value.checkOnNoOk()
    }

    suspend fun fetchCabinets() : RFetchCabinetsResponse {
        return httpClient.post {
            bearer()
            url {
                path(RequestPaths.Lessons.FetchCabinets)
            }
        }.body()
    }

    suspend fun fetchInitSchedule() : RFetchInitScheduleResponse {
        return httpClient.post {
            bearer()
            url {
                path(RequestPaths.Lessons.FetchInitSchedule)
            }
        }.body()
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
    suspend fun deleteSubject(r: RDeleteSubject) {
        httpClient.post {
            bearer()
            url {
                path(RequestPaths.Lessons.DeleteSubject)
                setBody(r)
            }
        }.status.value.checkOnNoOk()
    }
    suspend fun editSubject(r: REditSubjectReceive) {
        httpClient.post {
            bearer()
            url {
                path(RequestPaths.Lessons.EditSubject)
                setBody(r)
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
    suspend fun createGroup(request: REditGroupReceive) {
        return httpClient.post {
            bearer()
            url {
                path(RequestPaths.Lessons.EditGroup)
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
    suspend fun editForm(request: REditFormReceive) {
        return httpClient.post {
            bearer()
            url {
                path(RequestPaths.Lessons.EditForm)
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