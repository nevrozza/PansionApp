package rating

import MainRepository
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import components.listDialog.ListComponent
import components.listDialog.ListDialogStore
import components.listDialog.ListItem
import components.networkInterface.NetworkInterface
import deviceSupport.launchIO
import deviceSupport.withMain
import di.Inject
import getWeeks
import rating.RatingStore.Intent
import rating.RatingStore.Label
import rating.RatingStore.Message
import rating.RatingStore.State

class RatingExecutor(
    private val mainRepository: MainRepository = Inject.instance(),
    private val nInterface: NetworkInterface,
    private val subjectsListComponent: ListComponent,
    private val weeksListComponent: ListComponent,
    private val moduleListComponent: ListComponent,
    private val periodListComponent: ListComponent,
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
                dispatch(Message.OnPeriodClicked(intent.period.toPeriod()))
                fetchRating(state().currentSubject, intent.period.toPeriod(), forms = state().forms)
            }
            is Intent.ChangeIsDetailed -> dispatch(Message.IsDetailedChanged)
        }
    }

    private fun init() {
        fetchSubjects()
        fetchRating(state().currentSubject, state().period, state().forms)
    }

    private fun fetchRating(subjectId: Int, period: PansionPeriod?, forms: Int) {
        scope.launchIO {
            try {
                nInterface.nStartLoading()
                val r = mainRepository.fetchSubjectRating(
                    RFetchSubjectRatingReceive(
                        login = state().login,
                        subjectId = subjectId,
                        period = period,
                        forms = forms
                    )

                )

                withMain {
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
                nInterface.nError("Не удалось загрузить рейтинг", e) {
                    fetchRating(subjectId, period = period, forms = forms)
                }
//                groupListComponent.onEvent(ListDialogStore.Intent.CallError("Не удалось загрузить список групп =/") { fetchTeacherGroups() })
            }
        }
    }

    private fun fetchSubjects() {
        scope.launchIO {
            try {
                subjectsListComponent.nInterface.nStartLoading()
                val r = mainRepository.fetchScheduleSubjects()
                val subjects =
                    r.subjects.toMutableList()

                val weeks = getWeeks(
                    holidays = r.holiday.filter { it.isForAll }
                )

                subjects.add(0, startSubject)
                subjects.add(1, mvdSubject)
                subjects.add(2, zdravoohrSubject)
                subjects.add(3, socialWorkSubject)
                subjects.add(4, creativeSubject)
                withMain {
                    dispatch(Message.SubjectsUpdated(subjects, PansionPeriod.Week(weeks.last().num)))
                    subjectsListComponent.onEvent(
                        ListDialogStore.Intent.InitList(
                            subjects.mapNotNull {
                                if (it.isActive) {
                                    ListItem(
                                        id = it.id.toString(),
                                        text = it.name
                                    )
                                } else null
                            }
                        ))

                    weeksListComponent.onEvent(
                        ListDialogStore.Intent.InitList(
                            weeks.map {
                                ListItem(
                                    id = PansionPeriod.Week(it.num).toStr(),
                                    text = "${it.num} неделя"
                                )
                            }.reversed()
                        )
                    )
                    moduleListComponent.onEvent(
                        ListDialogStore.Intent.InitList(
                            (1..r.currentModule).map {
                                ListItem(
                                    id = PansionPeriod.Module(it).toStr(),
                                    text = "${it} модуль"
                                )
                            }.reversed()
                        )
                    )
                    if (r.currentHalf > 1) periodListComponent.onEvent(
                        ListDialogStore.Intent.InitList(
                            (
                                    (1..r.currentHalf).map {
                                        ListItem(
                                            id = PansionPeriod.Half(it).toStr(),
                                            text = "$it полугодие"
                                        )
                                    }
                                            +
                                            ListItem(
                                                id = PansionPeriod.Year.toStr(),
                                                text = "За год"
                                            )
                                    ).reversed()

                        )
                    )

                    subjectsListComponent.nInterface.nSuccess()
                }
            } catch (e: Throwable) {
                println(e)
                subjectsListComponent.nInterface.nError("Не удалось загрузить предметы", e) {
                    fetchSubjects()
                }
//                groupListComponent.onEvent(ListDialogStore.Intent.CallError("Не удалось загрузить список групп =/") { fetchTeacherGroups() })
            }
        }
    }
}
