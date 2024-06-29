import ktor.KtorJournalRemoteDataSource
import report.RFetchAllGroupMarksReceive
import report.RFetchAllGroupMarksResponse
import report.RFetchDetailedStupsReceive
import report.RFetchDetailedStupsResponse
import report.RFetchDnevnikRuMarksReceive
import report.RFetchDnevnikRuMarksResponse
import report.RFetchReportStudentsReceive
import report.RFetchReportStudentsResponse
import report.RFetchSubjectQuarterMarksReceive
import report.RFetchSubjectQuarterMarksResponse
import report.RIsQuartersReceive
import report.RIsQuartersResponse
import report.RUpdateReportReceive

class JournalRepositoryImpl(
    private val remoteDataSource: KtorJournalRemoteDataSource,
//    private val cacheDataSource: SettingsAuthDataSource
) : JournalRepository {
    override suspend fun updateWholeReport(r: RUpdateReportReceive) {
        remoteDataSource.updateWholeReport(r)
    }

    override suspend fun fetchReportStudents(r: RFetchReportStudentsReceive): RFetchReportStudentsResponse {
        return remoteDataSource.fetchReportStudents(r)
    }

    override suspend fun fetchDnevnikRuMarks(login: String, quartersNum: String, isQuarters: Boolean): RFetchDnevnikRuMarksResponse {
        return remoteDataSource.fetchDnevnikRuMarks(RFetchDnevnikRuMarksReceive(login, quartersNum, isQuarters))
    }

    override suspend fun fetchSubjectQuarterMarks(
        login: String,
        subjectId: Int,
        quartersNum: String
    ): RFetchSubjectQuarterMarksResponse {
        return remoteDataSource.fetchSubjectQuarterMarks(RFetchSubjectQuarterMarksReceive(
            subjectId = subjectId,
            login = login,
            quartersNum = quartersNum
        ))
    }

    override suspend fun fetchIsQuarter(login: String): RIsQuartersResponse {
        return remoteDataSource.fetchIsQuarter(RIsQuartersReceive(login))
    }

    override suspend fun fetchAllStups(login: String): RFetchDetailedStupsResponse {
        return remoteDataSource.fetchAllStups(RFetchDetailedStupsReceive(login))
    }

    override suspend fun fetchAllGroupMarks(
        groupId: Int,
        subjectId: Int
    ): RFetchAllGroupMarksResponse {
        return remoteDataSource.fetchAllGroupMarks(
            RFetchAllGroupMarksReceive(
                subjectId = subjectId,
                groupId = groupId
            )
        )
    }
}