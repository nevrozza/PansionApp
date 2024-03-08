import com.arkivanov.mvikotlin.core.store.Reducer

object LessonReportReducer : Reducer<LessonReportStore.State, LessonReportStore.Message> {
    override fun LessonReportStore.State.reduce(msg: LessonReportStore.Message): LessonReportStore.State {
        return when (msg) {
            else -> TODO()
        }
    }
}