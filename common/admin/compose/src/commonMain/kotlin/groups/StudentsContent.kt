@file:OptIn(ExperimentalFoundationApi::class)

package groups

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.LoadingAnimation
import components.networkInterface.NetworkState
import components.listDialog.ListDialogStore
import groups.students.StudentsComponent
import groups.students.StudentsStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun StudentsContent(
    component: StudentsComponent,
    coroutineScope: CoroutineScope,
    isFabShowing: MutableState<Boolean>
) {
    val gModel = component.groupModel.subscribeAsState().value
    val model = component.model.subscribeAsState().value
    val nSModel = component.nStudentsModel.subscribeAsState().value
    val nSGModel = component.nStudentsModel.subscribeAsState().value

    Column(Modifier.fillMaxSize().padding(horizontal = 10.dp)) {
        LazyRow(Modifier.fillMaxWidth().height(30.dp)) {
            item {
                val bringIntoViewRequester =
                    BringIntoViewRequester()
                Box(
                    Modifier.bringIntoViewRequester(
                        bringIntoViewRequester
                    )
                ) {
                    SubjectItem(
                        title = "без класса",
                        isChosen = 0 == model.chosenFormTabId
                    ) {
                        component.onEvent(
//                            GroupsStore.Intent.ChangeCurrentClass(
//                                0
//                            )
                            StudentsStore.Intent.ClickOnFormTab(0)
                        )
                        coroutineScope.launch {
                            bringIntoViewRequester.bringIntoView()
                        }
                    }
                }
                Spacer(Modifier.width(5.dp))

            }
            items(gModel.forms) {
                val bringIntoViewRequester =
                    BringIntoViewRequester()
                Box(
                    Modifier.bringIntoViewRequester(
                        bringIntoViewRequester
                    )
                ) {
                    SubjectItem(
                        title = "${it.form.classNum}${if (it.form.shortTitle.length < 2) "-" else " "}${it.form.shortTitle} класс",
                        isChosen = it.id == model.chosenFormTabId
                    ) {
                        component.onEvent(
                            StudentsStore.Intent.ClickOnFormTab(it.id)
                        )
                        coroutineScope.launch {
                            bringIntoViewRequester.bringIntoView()
                        }
                    }
                }
                Spacer(Modifier.width(5.dp))
            }
        }
        isFabShowing.value = false

        when (nSModel.state) {
            NetworkState.Loading -> {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {

                    isFabShowing.value = false
                    LoadingAnimation()
                }
            }

            NetworkState.None -> {
                if (model.studentsInForm.isNotEmpty()) {
                    Spacer(Modifier.height(7.dp))
                    LazyColumn(Modifier.fillMaxSize()) {
                        isFabShowing.value = false

                        items(model.studentsInForm) { student ->
                            var x by remember { mutableStateOf(0.0f) }
                            var y by remember { mutableStateOf(0.0f) }

                            Row() {
                                Text(
                                    "${student.login} ${student.fio.surname} ${student.fio.name}",
                                    color = if (student.login == model.chosenStudentLogin) Color.Black else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.clickable {
                                        component.onEvent(
                                            StudentsStore.Intent.ClickOnStudent(
                                                studentLogin = student.login
                                            )
                                        )
                                    })
                                IconButton(
                                    modifier = Modifier.onGloballyPositioned {
                                        x = it.positionInParent().x
                                        y = it.positionInRoot().y
                                    },
                                    onClick = {
                                        component.formsListComponent.onEvent(
                                            ListDialogStore.Intent.ShowDialog(
                                                x = x,
                                                y = y
                                            )
                                        )
                                        component.onEvent(
                                            StudentsStore.Intent.ClickOnStudentPlus(
                                                student.login
                                            )
                                        )
                                    }
                                ) {
                                    Icon(
                                        Icons.Rounded.Add,
                                        null
                                    )
                                }
                            }
                            if (model.chosenStudentLogin == student.login) {
                                model.studentGroups.forEach { group ->
                                    Text(
                                        "${
                                            gModel.subjects.first { it.id == group.group.subjectId }.name
                                        } ${group.group.name}"
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Здесь пока никого нет")
                    }
                }
            }

            NetworkState.Error -> {
                DefaultGroupsErrorScreen(
                    isFabShowing,
                    component.nStudentsInterface
                )
            }
        }

    }
}