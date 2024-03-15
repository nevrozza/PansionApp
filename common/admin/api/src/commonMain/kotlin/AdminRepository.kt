import admin.groups.forms.RFetchCutedGroupsResponse
import admin.groups.forms.outside.RFetchFormsResponse
import admin.groups.subjects.topBar.RFetchAllSubjectsResponse
import admin.groups.forms.outside.RFetchMentorsResponse
import admin.groups.subjects.RFetchTeachersResponse
import admin.groups.forms.RFetchFormGroupsResponse
import admin.groups.students.deep.RFetchStudentGroupsResponse
import admin.groups.students.RFetchStudentsInFormResponse
import admin.groups.subjects.RFetchGroupsResponse
import admin.users.RCreateUserResponse
import admin.users.RFetchAllUsersResponse
import admin.users.UserInit

interface AdminRepository {


    suspend fun registerUser(user: UserInit): RCreateUserResponse
    suspend fun fetchAllUsers(): RFetchAllUsersResponse

    suspend fun clearUserPassword(login: String)//: RClearUserPasswordReceive
    suspend fun editUser(login: String, user: UserInit)//: REditUserReceive

    suspend fun fetchAllSubjects(): RFetchAllSubjectsResponse
    suspend fun createSubject(name: String)//: R

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
    suspend fun createGroup(
        name: String,
        mentorLogin: String,
        subjectId: Int,
        difficult: String
    )//: CreateNewGroupResponse

    suspend fun createForm(
        title: String,
        mentorLogin: String,
        classNum: Int,
        shortTitle: String
    )//: CreateNewFormResponse
    suspend fun fetchAllForms(): RFetchFormsResponse

    suspend fun fetchAllTeachers(): RFetchTeachersResponse
    suspend fun fetchAllMentors(): RFetchMentorsResponse
}