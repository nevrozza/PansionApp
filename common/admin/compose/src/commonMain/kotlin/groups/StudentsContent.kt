@file:OptIn(ExperimentalFoundationApi::class)

package groups

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.*
import components.listDialog.ListDialogStore
import components.networkInterface.NetworkState
import decomposeComponents.listDialogComponent.ListDialogDesktopContent
import decomposeComponents.listDialogComponent.ListDialogMobileContent
import groups.students.StudentsComponent
import groups.students.StudentsStore
import resources.RIcons
import view.LocalViewManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentsContent(
    component: StudentsComponent,
    topPadding: Dp,
//    hazeState: HazeState
) {
    val gModel by component.groupModel.subscribeAsState()
    val model by component.model.subscribeAsState()
    val nSModel by component.nStudentsModel.subscribeAsState()
    val nSGModel by component.nStudentGroupsModel.subscribeAsState()
    val viewManager = LocalViewManager.current
    Box(Modifier.fillMaxSize()) {

        Crossfade(nSModel.state) {
            when (it) {
                NetworkState.Loading -> {
                    Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        LoadingAnimation()
                    }
                }

                NetworkState.None -> {
                    if (model.studentsInForm.isNotEmpty()) {

                        CLazyColumn(padding = PaddingValues(top = topPadding), hazeState = null) { // + 45.dp
                            item {
                                Spacer(Modifier.height(7.dp))
                            }
                            items(model.studentsInForm) { student ->
                                Column(
                                    Modifier//.padding(horizontal = 10.dp)
                                        .padding(bottom = if (student.login != model.studentsInForm.last().login) 7.dp else 80.dp)
                                        .clip(CardDefaults.elevatedShape)
                                        .animateContentSize()
                                        .background(
                                            MaterialTheme.colorScheme.surfaceColorAtElevation(
                                                (0.1).dp
                                            )
                                        ),
                                ) {

                                    ElevatedCard(
                                        modifier = Modifier.defaultMinSize(minHeight = TextFieldDefaults.MinHeight)
                                            .wrapContentHeight()
                                            .fillMaxWidth().clip(CardDefaults.elevatedShape)
                                            .clickable(enabled = model.chosenFormTabId != 0) {
                                                if (model.chosenFormTabId != 0) {
                                                    component.onEvent(
                                                        StudentsStore.Intent.ClickOnStudent(
                                                            studentLogin = student.login
                                                        )
                                                    )
                                                }
                                            }
                                    ) {
                                        Row(
                                            Modifier.fillMaxWidth().padding(7.dp)
                                                .padding(start = 10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                "${student.fio.surname} ${student.fio.name} ${student.fio.praname ?: ""}",
                                                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                                fontWeight = FontWeight.SemiBold,
                                                modifier = Modifier.weight(1f, false)
                                            )
                                            Box(Modifier.weight(0.1f,false)) {
                                                IconButton(
                                                    onClick = {
                                                        component.formsListComponent.onEvent(
                                                            ListDialogStore.Intent.ShowDialog
                                                        )
                                                        component.onEvent(
                                                            StudentsStore.Intent.ClickOnStudentPlus(
                                                                student.login
                                                            )
                                                        )
                                                    }
                                                ) {
                                                    Crossfade(model.chosenFormTabId != 0) {
                                                        Box(
                                                            modifier = Modifier.size(25.dp),
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            if (it) Crossfade(nSGModel.state == NetworkState.Loading) { state ->
                                                                if (state && student.login == model.chosenStudentLogin) {
                                                                    CircularProgressIndicator(
                                                                        modifier = Modifier.size(
                                                                            20.dp
                                                                        )
                                                                    )
                                                                } else {
                                                                    GetAsyncIcon(
                                                                        path = RIcons.ChevronLeft,
                                                                        modifier = Modifier.rotate(180f)
                                                                    )
                                                                }
                                                            }
                                                            else {
                                                                GetAsyncIcon(
                                                                    RIcons.Add
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                                if(student.login == model.chosenStudentPlusLogin) {
                                                    ListDialogDesktopContent(
                                                        component.formsListComponent
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    //BOTTOM
                                    if (model.chosenStudentLogin == student.login) {
                                        Crossfade(nSGModel.state) { ns ->

                                            when (ns) {
                                                NetworkState.Error -> {
                                                    Column(
                                                        Modifier.fillMaxSize(),
                                                        horizontalAlignment = Alignment.CenterHorizontally
                                                    ) {
                                                        Text(nSGModel.error)
                                                        Spacer(Modifier.height(7.dp))
                                                        CustomTextButton("Попробовать ещё раз") {
                                                            nSGModel.onFixErrorClick()
                                                        }
                                                    }
                                                }

                                                else -> {
                                                    if (model.studentGroups.isNotEmpty() || ns == NetworkState.None) {
                                                        Column(
                                                            Modifier.padding(vertical = 5.dp)
                                                                .fillMaxWidth(),
                                                            horizontalAlignment = Alignment.CenterHorizontally,
                                                            verticalArrangement = Arrangement.Center
                                                        ) {
                                                            model.studentGroups.forEach { sg ->
                                                                Row(
                                                                    modifier = Modifier.fillMaxWidth(),
                                                                    horizontalArrangement = Arrangement.Center,
                                                                    verticalAlignment = Alignment.CenterVertically
                                                                ) {
                                                                    Text(
                                                                        gModel.subjects.find { it.id == sg.group.subjectId }?.name
                                                                            ?: "null",
                                                                        fontWeight = FontWeight.Bold
                                                                    )
                                                                    Spacer(Modifier.width(5.dp))
                                                                    Text(sg.group.name)
                                                                    Spacer(Modifier.width(5.dp))
                                                                    IconButton(
                                                                        onClick = {
                                                                            component.onEvent(
                                                                                StudentsStore.Intent.DeleteStudentGroup(
                                                                                    subjectId = sg.group.subjectId,
                                                                                    groupId = sg.id,
                                                                                    login = model.chosenStudentLogin,
                                                                                    afterAll = {}
                                                                                )
                                                                            )
                                                                        },
                                                                        modifier = Modifier.size(25.dp)
                                                                    ) {
                                                                        GetAsyncIcon(
                                                                            RIcons.Close
                                                                        )
                                                                    }
                                                                }
                                                            }
                                                            if (!model.isFormGroupCreatingMenu) {
                                                                IconButton(
                                                                    onClick = {
                                                                        component.onEvent(
                                                                            StudentsStore.Intent.OpenFormGroupCreationMenu
                                                                        )
                                                                    }
                                                                ) {
                                                                    GetAsyncIcon(
                                                                        RIcons.Add
                                                                    )
                                                                }
                                                            } else {
                                                                GroupPicker(
                                                                    isLoading = (nSGModel.state == NetworkState.Loading),
                                                                    subjects = gModel.subjects.map {
                                                                        NSSubject(
                                                                            id = it.id,
                                                                            name = it.name,
                                                                            isActive = it.isActive
                                                                        )
                                                                    },
                                                                    chosenSubjectId = model.cFormGroupSubjectId,
                                                                    chosenGroupId = model.cFormGroupGroupId,
                                                                    cutedGroups = model.cutedGroups.map {
                                                                        NSCutedGroup(
                                                                            groupId = it.groupId,
                                                                            groupName = it.groupName,
                                                                            isActive = it.isActive
                                                                        )
                                                                    },
                                                                    sortedList = model.studentGroups.map { it.group.subjectId },
                                                                    onSubjectClick = {
                                                                        component.onEvent(
                                                                            StudentsStore.Intent.ChangeCFormGroupSubjectId(
                                                                                it
                                                                            )
                                                                        )
                                                                    },
                                                                    onGroupClick = {
                                                                        component.onEvent(
                                                                            StudentsStore.Intent.ChangeCFormGroupGroupId(
                                                                                it
                                                                            )
                                                                        )
                                                                    },
                                                                    onCloseClick = {
                                                                        component.onEvent(
                                                                            StudentsStore.Intent.CloseFormGroupCreationMenu
                                                                        )
                                                                    },
                                                                    onSubmitClick = {
                                                                        component.onEvent(
                                                                            StudentsStore.Intent.CreateFormGroup
                                                                        )

                                                                    }
                                                                )

                                                            }
                                                        }
                                                    }
                                                }

                                            }

                                        }
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
                        component.nStudentsInterface
                    )
                }
            }
        }
        ListDialogMobileContent(
            component.formsListComponent
        )
    }
}