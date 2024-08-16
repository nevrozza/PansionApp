package rating

import CDispatcher
import MainRepository
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import components.listDialog.ListComponent
import components.listDialog.ListDialogStore
import components.listDialog.ListItem
import components.networkInterface.NetworkInterface
import kotlinx.coroutines.launch
import rating.RatingStore.Intent
import rating.RatingStore.Label
import rating.RatingStore.State
import rating.RatingStore.Message

class RatingExecutor(
    private val mainRepository: MainRepository,
    private val nInterface: NetworkInterface,
    private val subjectsListComponent: ListComponent,
) : CoroutineExecutor<Intent, Unit, State, Message, Label>() {
    override fun executeIntent(intent: Intent) {
        when (intent) {
            Intent.Init -> init()
            is Intent.ClickOnSubject -> {
                dispatch(Message.OnSubjectClicked(intent.subjectId))
                fetchRating(intent.subjectId, state().period, state().forms)
            }

            is Intent.ClickOnForm -> {
                dispatch(Message.OnFormClicked(intent.formNum))
                fetchRating(state().currentSubject, state().period, intent.formNum)
            }

            is Intent.ClickOnPeriod -> {
                dispatch(Message.OnPeriodClicked(intent.period))
                fetchRating(state().currentSubject, intent.period, forms = state().forms)
            }
        }
    }

    private fun init() {
        fetchSubjects()
        fetchRating(state().currentSubject, state().period, state().forms)
    }

    private fun fetchRating(subjectId: Int, period: Int, forms: Int) {
        scope.launch(CDispatcher) {
            try {
                nInterface.nStartLoading()
                val r = mainRepository.fetchSubjectRating(
                    login = state().login,
                    subjectId = subjectId,
                    period = period,
                    forms = forms
                )

                scope.launch {
                    dispatch(
                        Message.RatingUpdated(
                            items = state().items + r.hash,
                            me = state().me + r.me,
                            lastEditTime = r.lastTimeEdit
                        )
                    )
                    nInterface.nSuccess()
                }
            } catch (e: Throwable) {
                println(e)
                nInterface.nError("Не удалось загрузить рейтинг") {
                    fetchRating(subjectId, period = period, forms = forms)
                }
//                groupListComponent.onEvent(ListDialogStore.Intent.CallError("Не удалось загрузить список групп =/") { fetchTeacherGroups() })
            }
        }
    }

    private fun fetchSubjects() {
        scope.launch(CDispatcher) {
            try {
                subjectsListComponent.nInterface.nStartLoading()
                val subjects =
                    mainRepository.fetchScheduleSubjects().subjects.toMutableList()
                subjects.add(0, startSubject)
                subjects.add(1, mvdSubject)
                subjects.add(2, socialWorkSubject)
                subjects.add(3, creativeSubject)
                scope.launch {
                    dispatch(Message.SubjectsUpdated(subjects))
                    subjectsListComponent.onEvent(ListDialogStore.Intent.InitList(
                        subjects.mapNotNull {
                            if(it.isActive) {
                                ListItem(
                                    id = it.id.toString(),
                                    text = it.name
                                )
                            } else null
                        }
                    ))
                    subjectsListComponent.nInterface.nSuccess()
                }
            } catch (e: Throwable) {
                println(e)
                subjectsListComponent.nInterface.nError("Не удалось загрузить предметы") {
                    fetchSubjects()
                }
//                groupListComponent.onEvent(ListDialogStore.Intent.CallError("Не удалось загрузить список групп =/") { fetchTeacherGroups() })
            }
        }
    }
}
