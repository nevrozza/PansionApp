import admin.ClearUserPasswordResponse
import admin.CreateFormGroupsResponse
import admin.CreateNewFormResponse
import admin.CreateNewGSubjectResponse
import admin.CreateNewGroupResponse
import admin.CreateUserFormResponse
import admin.EditUserResponse
import admin.FetchAllFormsResponse
import admin.FetchAllGSubjectsResponse
import admin.FetchAllMentorsForGroupsResponse
import admin.FetchAllTeachersForGroupsResponse
import admin.FetchAllUsersResponse
import admin.FetchFormGroupsOfSubjectResponse
import admin.FetchFormGroupsResponse
import admin.FetchStudentGroupsOfStudentResponse
import admin.FetchStudentsInFormResponse
import admin.FetchSubjectGroupsResponse
import admin.RegisterResponse
import admin.UserForRegistration

interface AdminRepository {


    suspend fun registerUser(user: UserForRegistration): RegisterResponse
    suspend fun fetchAllUsers(): FetchAllUsersResponse

    suspend fun clearUserPassword(login: String): ClearUserPasswordResponse
    suspend fun editUser(login: String, user: UserForRegistration): EditUserResponse

    suspend fun fetchAllGSubject(): FetchAllGSubjectsResponse
    suspend fun createGSubject(name: String): CreateNewGSubjectResponse

    suspend fun fetchSubjectGroups(id: Int): FetchSubjectGroupsResponse
    suspend fun fetchStudentGroups(login: String): FetchStudentGroupsOfStudentResponse
    suspend fun fetchSubjectFormGroups(id: Int): FetchFormGroupsOfSubjectResponse
    suspend fun fetchFormGroups(id: Int): FetchFormGroupsResponse
    suspend fun fetchStudentsInForm(formId: Int): FetchStudentsInFormResponse
    suspend fun createUserForm(login: String, formId: Int, currentFormIdToGetList: Int): CreateUserFormResponse
    suspend fun createFormGroup(
        formId: Int,
        subjectId: Int,
        groupId: Int,
    ): CreateFormGroupsResponse
    suspend fun createGroup(
        name: String,
        mentorLogin: String,
        subjectId: Int,
        difficult: String
    ): CreateNewGroupResponse

    suspend fun createForm(
        name: String,
        mentorLogin: String,
        classNum: Int,
        shortName: String
    ): CreateNewFormResponse
    suspend fun fetchAllForms(): FetchAllFormsResponse

    suspend fun fetchAllTeachersForGroups(): FetchAllTeachersForGroupsResponse
    suspend fun fetchAllMentorsForGroups(): FetchAllMentorsForGroupsResponse
}