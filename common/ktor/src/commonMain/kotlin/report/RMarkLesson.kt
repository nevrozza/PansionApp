package report

import kotlinx.serialization.Serializable

@Serializable
data class RMarkLessonReceive(
    val date: String,
    val lessonId: Int
)
