import kotlinx.serialization.Serializable
import report.ReportHeader

@Serializable
data class ReportData(
    val header: ReportHeader,
//    val topic: String,
    val description: String,
    val editTime: String,
    val ids: Int,
    val isMentorWas: Boolean,
    val isEditable: Boolean,
    val customColumns: List<String>
)