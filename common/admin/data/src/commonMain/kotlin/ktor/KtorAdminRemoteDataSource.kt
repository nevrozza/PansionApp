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
    private val hc: HttpClient
) {

    suspend fun addStudentToGroup(r: RAddStudentToGroup): Boolean =
        hc.dPost(RequestPaths.Lessons.AddStudentToGroupFromSubject, r).check()

    suspend fun createExcelStudents(r: RCreateExcelStudentsReceive): Boolean =
        hc.dPost(RequestPaths.UserManage.CreateStudentsFromExcel, r).check()


    suspend fun fetchParents(): RFetchParentsListResponse =
        hc.dPost(RequestPaths.Parents.FetchParents).dBody()

    suspend fun updateParents(r: RUpdateParentsListReceive): RFetchParentsListResponse =
        hc.dPost(RequestPaths.Parents.UpdateParent, r).dBody()

    suspend fun createAchievement(r: RCreateAchievementReceive): RFetchAchievementsResponse =
        hc.dPost(RequestPaths.Achievements.Create, r).dBody()

    suspend fun editAchievement(r: REditAchievementReceive): RFetchAchievementsResponse =
        hc.dPost(RequestPaths.Achievements.Edit, r).dBody()

    suspend fun updateGroupAchievement(r: RUpdateGroupOfAchievementsReceive): RFetchAchievementsResponse =
        hc.dPost(RequestPaths.Achievements.UpdateGroup, r).dBody()

    suspend fun deleteAchievement(r: RDeleteAchievementReceive): RFetchAchievementsResponse =
        hc.dPost(RequestPaths.Achievements.Delete, r).dBody()

    suspend fun fetchAllAchievements(): RFetchAchievementsResponse =
        hc.dPost(RequestPaths.Achievements.FetchAll).dBody()

    suspend fun fetchSchedule(r: RFetchScheduleDateReceive): RScheduleList =
        hc.dPost(RequestPaths.Lessons.FetchSchedule, r).dBody()

    suspend fun saveSchedule(r: RScheduleList) : Boolean =
        hc.dPost(RequestPaths.Lessons.SaveSchedule, r).check()

    suspend fun performRegistrationUser(r: RRegisterUserReceive): RCreateUserResponse =
        hc.dPost(RequestPaths.UserManage.CreateUser, r).dBody()

    suspend fun performFetchAllUsers(): RFetchAllUsersResponse =
        hc.dPost(RequestPaths.UserManage.FetchAllUsers).dBody()

    suspend fun clearUserPassword(r: RClearUserPasswordReceive) : Boolean =
        hc.dPost(RequestPaths.UserManage.ClearPasswordAdmin, r).check()

    suspend fun performEditUser(r: REditUserReceive) : Boolean =
        hc.dPost(RequestPaths.UserManage.EditUser, r).check()

    suspend fun performDeleteUser(r: RDeleteUserReceive) : Boolean =
        hc.dPost(RequestPaths.UserManage.DeleteUser, r).check()

    suspend fun updateCalendar(r: RUpdateCalendarReceive) : Boolean =
        hc.dPost(RequestPaths.Lessons.UpdateCalendar, r).check()

    suspend fun fetchCalendar(): RFetchCalendarResponse =
        hc.dPost(RequestPaths.Lessons.FetchCalendar).dBody()

    suspend fun updateCabinets(r: RUpdateCabinetsReceive) : Boolean =
        hc.dPost(RequestPaths.Lessons.UpdateCabinets, r).check()

    suspend fun fetchCabinets(): RFetchCabinetsResponse =
        hc.dPost(RequestPaths.Lessons.FetchCabinets).dBody()

    suspend fun fetchInitSchedule(): RFetchInitScheduleResponse =
        hc.dPost(RequestPaths.Lessons.FetchInitSchedule).dBody()


    suspend fun performFetchAllSubjects(): RFetchAllSubjectsResponse =
        hc.dPost(RequestPaths.Lessons.FetchAllSubjects).dBody()

    suspend fun performStudentsInForm(r: RFetchStudentsInFormReceive): RFetchStudentsInFormResponse =
        hc.dPost(RequestPaths.Lessons.FetchStudentsInForm, r).dBody()

    suspend fun performFormGroups(r: RFetchFormGroupsReceive): RFetchFormGroupsResponse =
        hc.dPost(RequestPaths.Lessons.FetchFormGroups, r).dBody()

    suspend fun createNewSubject(r: RCreateSubjectReceive) : Boolean =
        hc.dPost(RequestPaths.Lessons.CreateSubject, r).check()

    suspend fun deleteSubject(r: RDeleteSubject) : Boolean =
        hc.dPost(RequestPaths.Lessons.DeleteSubject, r).check()

    suspend fun editSubject(r: REditSubjectReceive) : Boolean =
        hc.dPost(RequestPaths.Lessons.EditSubject, r).check()

    suspend fun createFormGroup(r: RCreateFormGroupReceive) : Boolean =
        hc.dPost(RequestPaths.Lessons.CreateFormGroup, r).check()

    suspend fun deleteFormGroup(r: RCreateFormGroupReceive) : Boolean =
        hc.dPost(RequestPaths.Lessons.DeleteFormGroup, r).check()

    suspend fun createStudentGroup(r: RCreateStudentGroupReceive) : Boolean =
        hc.dPost(RequestPaths.Lessons.CreateStudentGroup, r).check()

    suspend fun deleteStudentGroup(r: RCreateStudentGroupReceive) : Boolean =
        hc.dPost(RequestPaths.Lessons.DeleteStudentGroup, r).check()


    suspend fun bindStudentToForm(r: RBindStudentToFormReceive) : Boolean =
        hc.dPost(RequestPaths.Lessons.BindStudentToForm, r).check()

    suspend fun createGroup(r: RCreateGroupReceive) : Boolean =
        hc.dPost(RequestPaths.Lessons.CreateGroup, r).check()

    suspend fun createGroup(r: REditGroupReceive) : Boolean =
        hc.dPost(RequestPaths.Lessons.EditGroup, r).check()

    suspend fun createForm(r: CreateFormReceive) : Boolean =
        hc.dPost(RequestPaths.Lessons.CreateForm, r).check()

    suspend fun editForm(r: REditFormReceive) : Boolean =
        hc.dPost(RequestPaths.Lessons.EditForm, r).check()

    suspend fun performFetchGroups(r: RFetchGroupsReceive): RFetchGroupsResponse =
        hc.dPost(RequestPaths.Lessons.FetchGroups, r).dBody()

    suspend fun performFetchStudentGroups(r: RFetchStudentGroupsReceive): RFetchStudentGroupsResponse =
        hc.dPost(RequestPaths.Lessons.FetchStudentGroups, r).dBody()

    suspend fun performFetchAllForms(): RFetchFormsResponse =
        hc.dPost(RequestPaths.Lessons.FetchAllForms).dBody()

    suspend fun performFetchTeachersForGroup(): RFetchTeachersResponse =
        hc.dPost(RequestPaths.Lessons.FetchTeachersForGroup).dBody()

    suspend fun performFetchMentorsForGroups(): RFetchMentorsResponse =
        hc.dPost(RequestPaths.Lessons.FetchMentorsForGroup).dBody()

    suspend fun performFetchCutedGroups(r: RFetchGroupsReceive): RFetchCutedGroupsResponse =
        hc.dPost(RequestPaths.Lessons.FetchCutedGroups, r).dBody()
}