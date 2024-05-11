@file:OptIn(ExperimentalFoundationApi::class)

package groups

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowForwardIos
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.CLazyColumn
import components.CustomTextButton
import components.GroupPicker
import components.LoadingAnimation
import components.networkInterface.NetworkState
import components.listDialog.ListDialogStore
import components.nSCutedGroup
import components.nSSubject
import dev.chrisbanes.haze.hazeChild
import groups.forms.FormsStore
import groups.students.StudentsComponent
import groups.students.StudentsStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import view.LocalViewManager

@Composable
fun StudentsContent(
    component: StudentsComponent,
    coroutineScope: CoroutineScope,
    topPadding: Dp,
    isFabShowing: MutableState<Boolean>
) {
    val gModel = component.groupModel.subscribeAsState().value
    val model = component.model.subscribeAsState().value
    val nSModel = component.nStudentsModel.subscribeAsState().value
    val nSGModel = component.nStudentGroupsModel.subscribeAsState().value
    val viewManager = LocalViewManager.current
    println(component.formsListComponent.model.subscribeAsState().value.isDialogShowing)
    Box(Modifier.fillMaxSize()) {

        Crossfade(nSModel.state) {
            when (it) {
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

                        CLazyColumn(padding = PaddingValues(top = topPadding + 45.dp)) {
                            isFabShowing.value = false
                            item {
                                Spacer(Modifier.height(7.dp))
                            }
                            items(model.studentsInForm) { student ->
                                var x by remember { mutableStateOf(0.0f) }
                                var y by remember { mutableStateOf(0.0f) }

                                Column(
                                    Modifier.padding(horizontal = 10.dp)
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
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            IconButton(
                                                modifier = Modifier.onGloballyPositioned {
                                                    x = it.positionInParent().x
                                                    y = it.positionInRoot().y
                                                },
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
                                                                Icon(
                                                                    Icons.AutoMirrored.Rounded.ArrowForwardIos,
                                                                    null
                                                                )
                                                            }
                                                        }
                                                        else {
                                                            Icon(Icons.Rounded.Add, null)
                                                        }
                                                    }
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
                                                                                StudentsStore.Intent.DeleteFormGroup(
                                                                                    subjectId = sg.group.subjectId,
                                                                                    groupId = sg.id
                                                                                )
                                                                            )
                                                                        },
                                                                        modifier = Modifier.size(25.dp)
                                                                    ) {
                                                                        Icon(
                                                                            Icons.Rounded.Close,
                                                                            null
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
                                                                    Icon(
                                                                        Icons.Rounded.Add,
                                                                        null
                                                                    )
                                                                }
                                                            } else {
                                                                GroupPicker(
                                                                    isLoading = (nSGModel.state == NetworkState.Loading),
                                                                    subjects = gModel.subjects.map {
                                                                        nSSubject(
                                                                            id = it.id,
                                                                            name = it.name,
                                                                            isActive = it.isActive
                                                                        )
                                                                    },
                                                                    chosenSubjectId = model.cFormGroupSubjectId,
                                                                    chosenGroupId = model.cFormGroupGroupId,
                                                                    cutedGroups = model.cutedGroups.map {
                                                                        nSCutedGroup(
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
                        isFabShowing,
                        component.nStudentsInterface
                    )
                }
            }
        }


        LazyRow(
            Modifier.fillMaxWidth()
                .then(
                    if(viewManager.hazeState != null && viewManager.hazeStyle != null) Modifier.hazeChild(state = viewManager.hazeState!!.value, style = viewManager.hazeStyle!!.value)
                    else Modifier
                )
                .padding(top = topPadding).padding(horizontal = 10.dp)
                .height(35.dp)
        ) {
            item {
                val bringIntoViewRequester =
                    BringIntoViewRequester()
                Box(
                    Modifier.bringIntoViewRequester(
                        bringIntoViewRequester
                    ).height(30.dp)
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
                    ).height(30.dp)
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

    }
}