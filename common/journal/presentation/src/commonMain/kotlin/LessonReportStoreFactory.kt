import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.arkivanov.mvikotlin.core.store.StoreFactory

class LessonReportStoreFactory(private val storeFactory: StoreFactory) {

    fun create(): LessonReportStore {
        return LessonReportStoreImpl()
    }

    private inner class LessonReportStoreImpl :
        LessonReportStore,
        Store<LessonReportStore.Intent, LessonReportStore.State, LessonReportStore.Label> by storeFactory.create(
            name = "LessonReportStore",
            initialState = LessonReportStore.State(),
            executorFactory = { LessonReportExecutor() },
            reducer = LessonReportReducer
        )
}