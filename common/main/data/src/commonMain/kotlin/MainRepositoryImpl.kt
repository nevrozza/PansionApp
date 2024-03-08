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
import admin.UserForRegistration
import journal.init.FetchStudentsInGroupReceive
import journal.init.FetchStudentsInGroupResponse
import journal.init.FetchTeacherGroupsResponse
import ktor.KtorMainRemoteDataSource

class MainRepositoryImpl(
    private val remoteDataSource: KtorMainRemoteDataSource
) : MainRepository {
    override suspend fun fetchTeacherGroups(): FetchTeacherGroupsResponse {
        return remoteDataSource.fetchTeacherGroups()
    }

    override suspend fun fetchStudentsInGroup(groupId: Int): FetchStudentsInGroupResponse {
        return remoteDataSource.fetchStudentInGroup(
            FetchStudentsInGroupReceive(
                groupId = groupId
            )
        )
    }
}