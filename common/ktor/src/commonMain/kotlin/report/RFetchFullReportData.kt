package report

import kotlinx.serialization.Serializable

@Serializable
data class RFetchFullReportData(
    val reportId: Int
)
