import achievements.RCreateAchievementReceive
import achievements.RDeleteAchievementReceive
import achievements.REditAchievementReceive
import achievements.RFetchAchievementsForStudentReceive
import achievements.RFetchAchievementsResponse
import achievements.RUpdateGroupOfAchievementsReceive
import admin.cabinets.CabinetItem
import admin.cabinets.RFetchCabinetsResponse
import admin.calendar.CalendarModuleItem
import admin.calendar.RFetchCalendarResponse
import admin.groups.forms.RFetchCutedGroupsResponse
import admin.groups.forms.outside.RFetchFormsResponse
import admin.groups.subjects.topBar.RFetchAllSubjectsResponse
import admin.groups.forms.outside.RFetchMentorsResponse
import admin.groups.subjects.RFetchTeachersResponse
import admin.groups.forms.RFetchFormGroupsResponse
import admin.groups.forms.outside.REditFormReceive
import admin.groups.students.deep.RFetchStudentGroupsResponse
import admin.groups.students.RFetchStudentsInFormResponse
import admin.groups.subjects.REditGroupReceive
import admin.groups.subjects.RFetchGroupsResponse
import admin.parents.RFetchParentsListResponse
import admin.parents.RUpdateParentsListReceive
import admin.schedule.RFetchInitScheduleResponse
import admin.users.RCreateUserResponse
import admin.users.RFetchAllUsersResponse
import admin.users.UserInit
import schedule.RScheduleList

interface AdminRepository {

    suspend fun fetchParents(): RFetchParentsListResponse
    suspend fun updateParents(r: RUpdateParentsListReceive): RFetchParentsListResponse

    suspend fun createAchievement(r: RCreateAchievementReceive): RFetchAchievementsResponse
    suspend fun editAchievement(r: REditAchievementReceive): RFetchAchievementsResponse
    suspend fun updateGroupAchievement(r: RUpdateGroupOfAchievementsReceive): RFetchAchievementsResponse
    suspend fun deleteAchievement(r: RDeleteAchievementReceive): RFetchAchievementsResponse
    suspend fun fetchAllAchievements(): RFetchAchievementsResponse

    suspend fun fetchInitSchedule(): RFetchInitScheduleResponse


    suspend fun fetchCalendar(): RFetchCalendarResponse

    suspend fun updateCalendar(calendar: List<CalendarModuleItem>)

    suspend fun fetchCabinets(): RFetchCabinetsResponse

    suspend fun updateCabinets(cabinets: List<CabinetItem>)

    suspend fun registerUser(
        user: UserInit,
        parents: List<String>?,
        formId: Int
    ): RCreateUserResponse

    suspend fun fetchAllUsers(): RFetchAllUsersResponse

    suspend fun clearUserPassword(login: String)//: RClearUserPasswordReceive
    suspend fun editUser(login: String, user: UserInit)//: REditUserReceive
    suspend fun deleteUser(login: String, user: UserInit)//: REditUserReceive

    suspend fun fetchAllSubjects(): RFetchAllSubjectsResponse
    suspend fun createSubject(name: String)//: R
    suspend fun editSubject(subjectId: Int, name: String)//: R
    suspend fun deleteSubject(subjectId: Int)//: R

    suspend fun fetchGroups(subjectId: Int): RFetchGroupsResponse
    suspend fun fetchStudentGroups(login: String): RFetchStudentGroupsResponse
    suspend fun fetchCutedGroups(subjectId: Int): RFetchCutedGroupsResponse
    suspend fun fetchFormGroups(id: Int): RFetchFormGroupsResponse
    suspend fun fetchStudentsInForm(formId: Int): RFetchStudentsInFormResponse
    suspend fun bindStudentToForm(login: String, formId: Int)//: CreateUserFormResponse
    suspend fun createFormGroup(
        formId: Int,
        subjectId: Int,
        groupId: Int,
    )//: RCreateFormGroupReceive

    suspend fun editForm(
        r: REditFormReceive
    )

    suspend fun createStudentGroup(
        studentLogin: String,
        subjectId: Int,
        groupId: Int,
    )//: RCreateFormGroupReceive

    suspend fun deleteFormGroup(
        formId: Int,
        subjectId: Int,
        groupId: Int,
    )//: RCreateFormGroupReceive

    suspend fun deleteStudentGroup(
        studentLogin: String,
        subjectId: Int,
        groupId: Int,
    )//: RCreateFormGroupReceive

    suspend fun createGroup(
        name: String,
        mentorLogin: String,
        subjectId: Int,
        difficult: String
    )

    suspend fun editGroup(
        r: REditGroupReceive
    )

    suspend fun createForm(
        title: String,
        mentorLogin: String,
        classNum: Int,
        shortTitle: String
    )//: CreateNewFormResponse

    suspend fun fetchAllForms(): RFetchFormsResponse

    suspend fun fetchAllTeachers(): RFetchTeachersResponse
    suspend fun fetchAllMentors(): RFetchMentorsResponse

    suspend fun fetchSchedule(dayOfWeek: String, date: String): RScheduleList
    suspend fun saveSchedule(list: RScheduleList)
}