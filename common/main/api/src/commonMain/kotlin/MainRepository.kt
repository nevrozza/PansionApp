import journal.init.FetchStudentsInGroupResponse
import journal.init.FetchTeacherGroupsResponse

interface MainRepository {
    suspend fun fetchTeacherGroups(): FetchTeacherGroupsResponse
    suspend fun fetchStudentsInGroup(groupId: Int): FetchStudentsInGroupResponse
}