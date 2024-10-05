package mentoring

import admin.groups.Group
import admin.groups.Subject
import admin.groups.forms.CutedGroup
import admin.groups.forms.CutedGroupViaSubject
import kotlinx.serialization.Serializable
import report.StudentNka
import report.UserMarkPlus

@Serializable
data class RFetchJournalBySubjectsReceive(
    val forms: List<Int>
)

@Serializable
data class RFetchJournalBySubjectsResponse(
    val groups: List<CutedGroupViaSubject>,
    val subjects: Map<Int, String>,
    val studentsGroups: Map<String, List<Int>>,
    val studentsMarks: Map<String, List<UserMarkPlus>>,
    val studentsNki: Map<String, List<StudentNka>>
)