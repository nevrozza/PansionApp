import com.arkivanov.mvikotlin.core.store.Store

interface LessonReportStore : Store<LessonReportStore.Intent, LessonReportStore.State, LessonReportStore.Label> {
    data class State(
        val lessonReportId: Int = 11232,
        val subjectName: String = "Математика",
        val groupName: String = "10кл усиленная",
        val teacherName: String = "Гурко А. В.",
        val date: String = "20.02.2024",
        val time: String = "17:16",
        val topic: String = "Тематика и проблематика лирики А. А. Ахматовой/Develop reading and speaking skills (Progressive design)"
    )

    sealed interface Intent

    sealed interface Message

    sealed interface Label

}
