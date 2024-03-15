import journal.init.RFetchStudentsInGroupResponse
import journal.init.RFetchTeacherGroupsResponse

interface MainRepository {
    suspend fun fetchTeacherGroups(): RFetchTeacherGroupsResponse
    suspend fun fetchStudentsInGroup(groupId: Int): RFetchStudentsInGroupResponse
}