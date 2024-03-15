import journal.init.RFetchStudentsInGroupReceive
import journal.init.RFetchStudentsInGroupResponse
import journal.init.RFetchTeacherGroupsResponse
import ktor.KtorMainRemoteDataSource

class MainRepositoryImpl(
    private val remoteDataSource: KtorMainRemoteDataSource
) : MainRepository {
    override suspend fun fetchTeacherGroups(): RFetchTeacherGroupsResponse {
        return remoteDataSource.fetchTeacherGroups()
    }

    override suspend fun fetchStudentsInGroup(groupId: Int): RFetchStudentsInGroupResponse {
        return remoteDataSource.fetchStudentInGroup(
            RFetchStudentsInGroupReceive(
                groupId = groupId
            )
        )
    }
}