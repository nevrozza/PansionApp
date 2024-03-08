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
import ktor.KtorAdminRemoteDataSource

class AdminRepositoryImpl(
    private val remoteDataSource: KtorAdminRemoteDataSource
) : AdminRepository {
    override suspend fun registerUser(user: UserForRegistration): RegisterResponse {
        return remoteDataSource.performRegistrationUser(
            RegisterReceive(
                name = user.name,
                surname = user.surname,
                praname = user.praname,
                birthday = user.birthday,
                role = user.role,
                moderation = user.moderation,
                isParent = user.isParent
            )
        )
    }

    override suspend fun fetchAllUsers(): FetchAllUsersResponse {
        return remoteDataSource.performFetchAllUsers()
    }

    override suspend fun clearUserPassword(login: String): ClearUserPasswordResponse {
        return remoteDataSource.clearUserPassword(ClearUserPasswordReceive(login))
    }

    override suspend fun editUser(login: String, user: UserForRegistration): EditUserResponse {
        return remoteDataSource.performEditUser(
            EditUserReceive(
                login = login,
                name = user.name,
                surname = user.surname,
                praname = user.praname,
                birthday = user.birthday,
                role = user.role,
                moderation = user.moderation,
                isParent = user.isParent
            )
        )
    }

    override suspend fun fetchAllGSubject(): FetchAllGSubjectsResponse {
        return remoteDataSource.performFetchAllSubject()
    }

    override suspend fun createGSubject(name: String): CreateNewGSubjectResponse {
        return remoteDataSource.createNewSubject(
            CreateNewGSubjectReceive(
                name = name
            )
        )
    }

    override suspend fun fetchSubjectGroups(id: Int): FetchSubjectGroupsResponse {
        return remoteDataSource.performFetchSubjectGroups(
            FetchSubjectGroupsReceive(
                id = id
            )
        )
    }

    override suspend fun fetchStudentGroups(login: String): FetchStudentGroupsOfStudentResponse {
        return remoteDataSource.performFetchStudentGroups(
            FetchStudentGroupsOfStudentReceive(
                studentLogin = login
            )
        )
    }

    override suspend fun fetchSubjectFormGroups(id: Int): FetchFormGroupsOfSubjectResponse {
        return remoteDataSource.performFetchSubjectFormGroups(
            FetchFormGroupsOfSubjectReceive(
                subjectId = id
            )
        )
    }

    override suspend fun fetchFormGroups(id: Int): FetchFormGroupsResponse {
        return remoteDataSource.performFormGroups(
            FetchFormGroupsReceive(
                formId = id
            )
        )
    }

    override suspend fun fetchStudentsInForm(formId: Int): FetchStudentsInFormResponse {
        return remoteDataSource.performStudentsInForm(
            FetchStudentsInFormReceive(
                formId = formId
            )
        )
    }

    override suspend fun createUserForm(
        login: String,
        formId: Int,
        currentFormIdToGetList: Int
    ): CreateUserFormResponse {
        return remoteDataSource.createUserForm(
            CreateUserFormReceive(
                currentFormId = currentFormIdToGetList,
                studentLogin = login,
                hisFormId = formId
            )
        )
    }

    override suspend fun createFormGroup(
        formId: Int,
        subjectId: Int,
        groupId: Int
    ): CreateFormGroupsResponse {
        return remoteDataSource.createFormGroup(
            CreateFormGroupsReceive(
                formId = formId,
                subjectId = subjectId,
                groupId = groupId
            )
        )
    }

    override suspend fun createGroup(
        name: String,
        mentorLogin: String,
        subjectId: Int,
        difficult: String
    ): CreateNewGroupResponse {
        return remoteDataSource.createGroup(
            CreateNewGroupReceive(
                name = name,
                mentorLogin = mentorLogin,
                gSubjectId = subjectId,
                difficult = difficult
            )
        )
    }

    override suspend fun createForm(
        name: String,
        mentorLogin: String,
        classNum: Int,
        shortName: String
    ): CreateNewFormResponse {
        return remoteDataSource.createForm(
            CreateNewFormReceive(
                name = name,
                mentorLogin = mentorLogin,
                classNum = classNum,
                shortName = shortName
            )
        )
    }

    override suspend fun fetchAllForms(): FetchAllFormsResponse {
        return remoteDataSource.performFetchAllForms()
    }

    override suspend fun fetchAllTeachersForGroups(): FetchAllTeachersForGroupsResponse {
        return remoteDataSource.performFetchTeachersForGroups()
    }

    override suspend fun fetchAllMentorsForGroups(): FetchAllMentorsForGroupsResponse {
        return remoteDataSource.performFetchMentorsForGroups()
    }
}