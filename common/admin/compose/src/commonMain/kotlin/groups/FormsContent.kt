package groups

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.CLazyColumn
import components.CustomTextButton
import components.GroupPicker
import components.LoadingAnimation
import components.nSCutedGroup
import components.nSSubject
import components.networkInterface.NetworkState
import groups.forms.FormsComponent
import groups.forms.FormsStore
import groups.students.StudentsStore

@ExperimentalMaterial3Api
@Composable
fun FormsContent(
    component: FormsComponent,
    isFabShowing: MutableState<Boolean>,
    topPadding: Dp,
    padding: PaddingValues
) {
    val gModel = component.groupModel.subscribeAsState().value
    val model = component.model.subscribeAsState().value
    val nFModel = component.nFormsModel.subscribeAsState().value
    val nFGModel = component.nFormGroupsModel.subscribeAsState().value
    Crossfade(nFModel.state) {
        Column(Modifier.fillMaxSize()) {
            when {
                gModel.forms.isNotEmpty() && it != NetworkState.Error -> {
                    Spacer(Modifier.height(7.dp))
                    CLazyColumn(padding = PaddingValues(top = topPadding)) {
                        isFabShowing.value = true

                        items(gModel.forms) { form ->
                            val mentor =
                                model.mentors.find { it.login == form.form.mentorLogin }
                            val mentorName =
                                try {
                                    "${mentor!!.fio.surname} ${mentor.fio.name.first()}. ${(mentor.fio.praname ?: " ").first()}."
                                } catch (_: Throwable) {
                                    ""
                                }
                            Column(
                                Modifier
                                    .padding(horizontal = 10.dp)
                                    .padding(bottom = if (form.id != gModel.forms.last().id) 7.dp else 80.dp + padding.calculateBottomPadding())
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
                                        .fillMaxWidth(),
                                    onClick = {
                                        val id = when (model.chosenFormId) {
                                            form.id -> 0
                                            else -> form.id
                                        }
                                        component.onEvent(FormsStore.Intent.ClickOnForm(id))
                                    }
                                ) {
                                    Row(
                                        Modifier.fillMaxWidth().padding(7.dp)
                                            .padding(start = 10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Row {
                                                Text(
                                                    text = "${form.form.classNum}${if (form.form.title.length < 2) "-" else " "}${form.form.title.lowercase()} класс",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 17.sp
                                                )
                                                Spacer(Modifier.padding(start = 4.dp))
                                                Text(
                                                    "${form.form.classNum}${if (form.form.shortTitle.length < 2) "-" else " "}${form.form.shortTitle}",
                                                    fontSize = 10.sp,
                                                    color = MaterialTheme.colorScheme.onSurface.copy(
                                                        alpha = 0.6f
                                                    ),
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                            Row {
                                                Icon(
                                                    Icons.Rounded.Person,
                                                    null,
                                                    modifier = Modifier.offset(x = (-4).dp)
                                                )
                                                Text(text = mentorName)
                                            }
                                        }

                                        Crossfade(nFGModel.state == NetworkState.Loading) {
                                            Box(
                                                modifier = Modifier.size(25.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                if (it && form.id == model.chosenFormId) {
                                                    CircularProgressIndicator(
                                                        modifier = Modifier.size(
                                                            20.dp
                                                        )
                                                    )
                                                } else {
                                                    Icon(
                                                        if (form.id == model.chosenFormId) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                                                        null
                                                    )
                                                }
                                            }
                                        }


                                    }
                                }
                                if (model.chosenFormId == form.id) {
                                    Crossfade(nFGModel.state) { ns ->
                                        when (ns) {
                                            NetworkState.Error -> {
                                                Column(
                                                    Modifier.fillMaxSize(),
                                                    horizontalAlignment = Alignment.CenterHorizontally
                                                ) {
                                                    Text(nFGModel.error)
                                                    Spacer(Modifier.height(7.dp))
                                                    CustomTextButton("Попробовать ещё раз") {
                                                        nFGModel.onFixErrorClick()
                                                    }
                                                }
                                            }

                                            else -> {
                                                if (model.formGroups.isNotEmpty() || ns == NetworkState.None) {
                                                    Column(
                                                        Modifier.padding(vertical = 5.dp)
                                                            .fillMaxWidth(),
                                                        horizontalAlignment = Alignment.CenterHorizontally,
                                                        verticalArrangement = Arrangement.Center
                                                    ) {
                                                        model.formGroups.forEach { formGroup ->
                                                            Row(
                                                                modifier = Modifier.fillMaxWidth(),
                                                                horizontalArrangement = Arrangement.Center,
                                                                verticalAlignment = Alignment.CenterVertically
                                                            ) {
                                                                Text(
                                                                    gModel.subjects.find { it.id == formGroup.subjectId }?.name
                                                                        ?: "null",
                                                                    fontWeight = FontWeight.Bold
                                                                )
                                                                Spacer(Modifier.width(5.dp))
                                                                Text(formGroup.groupName)
                                                                Spacer(Modifier.width(5.dp))
                                                                IconButton(
                                                                    onClick = {
                                                                        component.onEvent(
                                                                            FormsStore.Intent.DeleteFormGroup(
                                                                            subjectId = formGroup.subjectId,
                                                                            groupId = formGroup.groupId
                                                                        ))
                                                                    },
                                                                    modifier = Modifier.size(25.dp)
                                                                ) {
                                                                    Icon(
                                                                        Icons.Rounded.Close, null
                                                                    )
                                                                }
                                                            }
                                                        }
                                                        if (!model.isFormGroupCreatingMenu) {
                                                            IconButton(
                                                                onClick = {
                                                                    component.onEvent(FormsStore.Intent.OpenFormGroupCreationMenu)
                                                                }
                                                            ) {
                                                                Icon(
                                                                    Icons.Rounded.Add,
                                                                    null
                                                                )
                                                            }
                                                        } else {
                                                            GroupPicker(
                                                                isLoading = (nFGModel.state == NetworkState.Loading),
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
                                                                sortedList = model.formGroups.map { it.subjectId },
                                                                onSubjectClick = {
                                                                    component.onEvent(
                                                                        FormsStore.Intent.ChangeCFormGroupSubjectId(
                                                                            it
                                                                        )
                                                                    )
                                                                },
                                                                onGroupClick = {
                                                                    component.onEvent(
                                                                        FormsStore.Intent.ChangeCFormGroupGroupId(
                                                                            it
                                                                        )
                                                                    )
                                                                },
                                                                onCloseClick = {
                                                                    component.onEvent(
                                                                        FormsStore.Intent.CloseFormGroupCreationMenu
                                                                    )
                                                                },
                                                                onSubmitClick = {
                                                                    component.onEvent(
                                                                        FormsStore.Intent.CreateFormGroup
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
                }

                it != NetworkState.Error -> {
                    Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (gModel.subjects.isNotEmpty()) {
                            isFabShowing.value = true
                        }
                        Text("Здесь пустовато =)")
                    }
                }

                else -> {
                    DefaultGroupsErrorScreen(
                        isFabShowing,
                        component.nFormsInterface
                    )
                }
            }
        }
    }

}