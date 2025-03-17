package formRating

import JournalRepository
import admin.groups.forms.formSort
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import components.cAlertDialog.CAlertDialogComponent
import components.cAlertDialog.CAlertDialogStore
import components.listDialog.ListComponent
import components.listDialog.ListDialogStore
import components.listDialog.ListItem
import components.networkInterface.NetworkInterface
import deviceSupport.launchIO
import deviceSupport.withMain
import di.Inject
import formRating.FormRatingStore.Intent
import formRating.FormRatingStore.Label
import formRating.FormRatingStore.Message
import formRating.FormRatingStore.State
import rating.PansionPeriod
import rating.RFetchFormRatingReceive
import rating.toStr
import server.Roles

class FormRatingExecutor(
    private val nInterface: NetworkInterface,
    private val journalRepository: JournalRepository = Inject.instance(),
    private val stupsDialog: CAlertDialogComponent,
    private val formPickerDialog: ListComponent,
    private val weeksListComponent: ListComponent,
    private val moduleListComponent: ListComponent,
    private val periodListComponent: ListComponent,
) : CoroutineExecutor<Intent, Unit, State, Message, Label>() {

    override fun executeAction(action: Unit) {
        executeIntent(Intent.Init)

        periodListComponent.onEvent(
            ListDialogStore.Intent.InitList(
                listOf(Pair(PansionPeriod.Year, "За год")).map {
                    ListItem(
                        id = it.first.toStr(),
                        text = it.second
                    )
                }
            )
        )
    }

    override fun executeIntent(intent: Intent) {
        when (intent) {
            Intent.ChangeIsDetailed -> dispatch(Message.IsDetailedChanged)
            Intent.Init -> {
                init(
                    formId = state().formId,
                    period = state().period,
                    formNum = state().formNum
                )
                initFormPicker()
            }

            is Intent.ChangeForm -> {
                val form = state().availableForms.first { it.id == intent.formId }
                dispatch(
                    Message.FormChanged(
                        formId = intent.formId,
                        formNum = form.classNum,
                        formName = form.title
                    )
                )
                formPickerDialog.onEvent(ListDialogStore.Intent.HideDialog)
                init(
                    formId = intent.formId,
                    period = state().period,
                    formNum = form.classNum
                )
            }

            is Intent.ChangePeriod -> {
                dispatch(Message.PeriodChanged(intent.period))
                init(
                    formId = state().formId,
                    period = intent.period,
                    formNum = state().formNum
                )
            }

            is Intent.SelectStupsLogin -> {
                dispatch(Message.StupsLoginSelected(intent.login))
                stupsDialog.onEvent(CAlertDialogStore.Intent.ShowDialog)
            }
        }
    }

    private fun init(formId: Int?, period: PansionPeriod?, formNum: Int?) {
        if (formId != null) {
            scope.launchIO {
                nInterface.nStartLoading()
                try {
                    val r = journalRepository.fetchFormRating(
                        RFetchFormRatingReceive(
                            formId = formId,
                            formNum = formNum!!,
                            period = period
                        )
                    )
                    val newPages = state().formRatingPages.toMutableList()
                    newPages.remove(newPages.firstOrNull { it.formId == formId && it.period == period })
//                    val topEd: MutableMap<Int, List<String>> = mutableMapOf()
//                    val topMarks: MutableMap<Int, List<String>> = mutableMapOf()
//                    val topStups: MutableMap<Int, List<String>> = mutableMapOf()

//                    val students = r.students

                    val avgAlgTypes =
                        r.students.map { it.avgAlg }
                            .sortedByDescending { it }.toSet()
                    val stupsAlgTypes =
                        r.students.map { it.stupsAlg }
                            .sortedByDescending { it }.toSet()

                    val newDto = r.students.map { x ->
                        x.copy(
                            topAvg = avgAlgTypes
                                .indexOfFirst { it == x.avgAlg }+1,
                            topStups = stupsAlgTypes
                                .indexOfFirst { it == x.stupsAlg }+1,
                        )
                    }

                    var top = 0
                    var previousStups = -111.0f
                    var previousAvg = -10.0f
                    val items = newDto.sortedWith( // .filter { it.stups > 0 && it.avg.toFloat() >= 4 }
                        compareBy(
                            { it.avgAlg >= 0 },
                            { -(it.topAvg + it.topStups) },
                            { it.stupsAlg },
                            { it.avgAlg },
                        )
                    ).reversed().map { x ->
                        if (previousAvg != x.avgAlg || previousStups != x.stupsAlg) top++
                        previousAvg = x.avgAlg
                        previousStups = x.stupsAlg
                        x.copy(top = top)
                    }


                    newPages.add(
                        FormRatingPage(
                            period = period ?: PansionPeriod.Week(r.currentWeek),
                            formId = formId,
                            students = items
                        )
                    )
                    withMain {
                        if (period == null) dispatch(Message.PeriodChanged(PansionPeriod.Week(r.currentWeek)))
                        dispatch(Message.FormRatingPagesUpdated(newPages, r.subjects))

                        weeksListComponent.onEvent(
                            ListDialogStore.Intent.InitList(
                                (1..r.currentWeek).map {
                                    ListItem(
                                        id = PansionPeriod.Week(it).toStr(),
                                        text = "${it} неделя"
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


                        nInterface.nSuccess()
                    }
                } catch (e: Throwable) {
                    println("ss ${e}")
                    nInterface.nError(
                        "Что-то пошло не так", e
                    ) {
                        init(
                            formId = formId,
                            formNum = formNum,
                            period = period
                        )
                    }
                }
            }
        }
    }

    private fun initFormPicker() {
        if (state().role != Roles.STUDENT) {
            scope.launchIO {
                formPickerDialog.nInterface.nStartLoading()
                try {
                    val r = journalRepository.fetchFormsForFormRating()
                    val forms = r.forms.formSort()
                    withMain {
                        formPickerDialog.onEvent(
                            ListDialogStore.Intent.InitList(
                                forms.map {
                                    ListItem(
                                        id = it.id.toString(),
                                        text = it.title
                                    )
                                }
                            )
                        )
                        formPickerDialog.nInterface.nSuccess()
                        dispatch(
                            Message.AvailableFormsUpdated(
                                forms
//                        r.forms.size > 1 || (state().formId == null && r.forms.size == 1)
                            )
                        )
                    }
                } catch (e: Throwable) {
                    println("wtf: ${e}")
                    formPickerDialog.nInterface.nError(
                        "Что-то пошло не так", e
                    ) {
                        initFormPicker()
                    }
                }
            }
        }
    }
}
