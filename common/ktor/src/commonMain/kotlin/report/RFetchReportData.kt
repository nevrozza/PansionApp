package report

import kotlinx.serialization.Serializable

@Serializable
data class RFetchReportDataReceive(
    val reportId: Int
)

@Serializable
data class RFetchReportDataResponse(
    val topic: String,
    val description: String,
    val editTime: String,
    val ids: Int,
    val isMentorWas: Boolean,
    val isEditable: Boolean,
    val customColumns: List<String>
)