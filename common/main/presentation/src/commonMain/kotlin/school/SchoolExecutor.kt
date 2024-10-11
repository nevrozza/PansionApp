package school

import CDispatcher
import MainRepository
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import components.networkInterface.NetworkInterface
import kotlinx.coroutines.launch
import main.RFetchSchoolDataReceive
import school.SchoolStore.Intent
import school.SchoolStore.Label
import school.SchoolStore.State
import school.SchoolStore.Message

class SchoolExecutor(
    private val nInterface: NetworkInterface,
    private val mainRepository: MainRepository
) : CoroutineExecutor<Intent, Unit, State, Message, Label>() {
    override fun executeIntent(intent: Intent) {
        when (intent) {
            Intent.Init -> init()
        }
    }
    private fun init() {
        scope.launch(CDispatcher) {
            nInterface.nStartLoading()
            try {
                val r = mainRepository.fetchSchoolData(
                    RFetchSchoolDataReceive(
                        login = state().login
                    )
                )
                scope.launch {
                    dispatch(
                        Message.Inited(
                            formId = r.formId,
                            formNum = r.formNum,
                            formName = r.formName,
                            top = r.top
                        )
                    )
                    nInterface.nSuccess()
                }
            } catch (e: Throwable) {

                nInterface.nError(
                    "Что-то пошло не так",
                ) {
                    init()
                }
            }
        }
    }
}
