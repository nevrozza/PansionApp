import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow

class LessonReportComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    private val output: (Output) -> Unit,
    val lessonReportId: Int
) : ComponentContext by componentContext {
    //    private val settingsRepository: SettingsRepository = Inject.instance()
//    private val authRepository: AuthRepository = Inject.instance()
    private val lessonReportStore =
        instanceKeeper.getStore(key = "lessonReportN$lessonReportId") {
            LessonReportStoreFactory(
                storeFactory = storeFactory,
//                authRepository = authRepository
            ).create()
        }

    val model = lessonReportStore.asValue()

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<LessonReportStore.State> = lessonReportStore.stateFlow

    fun onEvent(event: LessonReportStore.Intent) {
        lessonReportStore.accept(event)
    }

    fun onOutput(output: Output) {
        output(output)
    }

    private val backCallback = BackCallback {
        onOutput(Output.BackToJournal)
    }


    init {
        backHandler.register(backCallback)
    }

    sealed class Output {
        //        data object NavigateToMentors : Output()
//        data object NavigateToUsers : Output()
//        data object NavigateToGroups : Output()
//        data object NavigateToStudents : Output()
        data object BackToJournal: Output()

    }
}