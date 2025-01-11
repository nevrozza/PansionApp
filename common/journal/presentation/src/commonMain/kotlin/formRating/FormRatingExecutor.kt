package formRating

import CDispatcher
import JournalRepository
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import components.cAlertDialog.CAlertDialogComponent
import components.cAlertDialog.CAlertDialogStore
import components.listDialog.ListComponent
import components.listDialog.ListDialogStore
import components.listDialog.ListItem
import components.networkInterface.NetworkInterface
import formRating.FormRatingStore.Intent
import formRating.FormRatingStore.Label
import formRating.FormRatingStore.State
import formRating.FormRatingStore.Message
import getWeeks
import kotlinx.coroutines.launch
import rating.PansionPeriod
import rating.RFetchFormRatingReceive
import rating.toStr
import server.Roles
import server.getCurrentEdYear

class FormRatingExecutor(
    private val nInterface: NetworkInterface,
    private val journalRepository: JournalRepository,
    private val stupsDialog: CAlertDialogComponent,
    private val formPickerDialog: ListComponent,
    private val weeksListComponent: ListComponent,
    private val moduleListComponent: ListComponent,
    private val periodListComponent: ListComponent,
) : CoroutineExecutor<Intent, Unit, State, Message, Label>() {
    override fun executeIntent(intent: Intent) {
        when (intent) {
            Intent.Init -> {
                init(
                    formId = state().formId,
                    period = state().period,
                    formNum = state().formNum
                )
                initFormPicker()
            }

            is Intent.ChangeForm -> scope.launch {
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
            scope.launch(CDispatcher) {
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
                    val topEd: MutableMap<Int, List<String>> = mutableMapOf()
                    val topMarks: MutableMap<Int, List<String>> = mutableMapOf()
                    val topStups: MutableMap<Int, List<String>> = mutableMapOf()

                    val students = r.students.filter { it.avg.count >= 4 && it.edStups.isNotEmpty() }


                    val stups = students.map { it.edStups.sumOf { it.content.toIntOrNull() ?: 0 } }.toSet()
                    val marks = students.map { (it.avg.sum / it.avg.count.toFloat()) }.toSet()


                    stups.sortedBy { it }.reversed().forEachIndexed { i, stp ->
                        topStups[i] = students.filter { it.edStups.sumOf { it.content.toIntOrNull() ?: 0 } == stp }
                            .map { it.login }
                    }
                    marks.sortedBy { it }.reversed().forEachIndexed { i, mrk ->
                        topMarks[i] = students.filter { (it.avg.sum / it.avg.count.toFloat()) == mrk }.map { it.login }
                    }
                    val eds = students.map { s ->
                        ((topMarks.filterValues { s.login in it }.keys.first()) + topStups.filterValues { s.login in it }.keys.first()) / 2.0f
                    }.toSet()
                    eds.sortedBy { it }.forEachIndexed { i, d ->
                        topEd[i] =
                            students.filter { s -> d == (((topMarks.filterValues { s.login in it }.keys.first()) + topStups.filterValues { s.login in it }.keys.first()) / 2.0f) }
                                .map { it.login }
                    }


                    newPages.add(
                        FormRatingPage(
                            period = period ?: PansionPeriod.Week(r.currentWeek),
                            formId = formId,
                            students = r.students,
                            topEd = topEd,
                            topMarks = topMarks,
                            topStups = topStups
                        )
                    )
                    scope.launch {
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
        if (state().role != Roles.student) {
            scope.launch(CDispatcher) {
                formPickerDialog.nInterface.nStartLoading()
                try {
                    val r = journalRepository.fetchFormsForFormRating()
                    scope.launch {
                        formPickerDialog.onEvent(
                            ListDialogStore.Intent.InitList(
                                r.forms.map {
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
                                r.forms
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
