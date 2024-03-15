package groups

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.LoadingAnimation
import components.networkInterface.NetworkState
import groups.forms.FormsComponent
import groups.forms.FormsStore

@ExperimentalMaterial3Api
@Composable
fun FormsContent(
    component: FormsComponent,
    isFabShowing: MutableState<Boolean>,
    padding: PaddingValues
) {
    val gModel = component.groupModel.subscribeAsState().value
    val model = component.model.subscribeAsState().value
    val nFModel = component.nFormsModel.subscribeAsState().value
    val nFGModel = component.nFormGroupsModel.subscribeAsState().value
    Column(Modifier.fillMaxSize().padding(horizontal = 10.dp)) {
        when {
            gModel.forms.isNotEmpty() && nFModel.state != NetworkState.Error -> {
                Spacer(Modifier.height(7.dp))
                LazyColumn(Modifier.fillMaxSize()) {
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


                                    Icon(
                                        if (form.id == model.chosenFormId) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                                        null
                                    )
                                }
                            }
                            if (model.chosenFormId == form.id) {
                                when (nFModel.state) {
                                    NetworkState.Loading -> {
                                        Box(
                                            Modifier.height(30.dp)
                                                .fillMaxWidth(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            LoadingAnimation(
                                                circleSize = 8.dp,
                                                spaceBetween = 5.dp,
                                                travelDistance = 3.5.dp
                                            )
                                        }
                                    }

                                    else -> {
                                        Column(
                                            Modifier.padding(vertical = 5.dp)
                                                .fillMaxWidth(),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            model.formGroups.forEach { formGroup ->
                                                Row() {
                                                    Text(
                                                        gModel.subjects.find { it.id == formGroup.subjectId }?.name
                                                            ?: "null"
                                                    )
                                                    Text(formGroup.groupName)
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
                                                Column(horizontalAlignment = Alignment.End) {
                                                    Row {
                                                        var expandedGSubjects by remember {
                                                            mutableStateOf(
                                                                false
                                                            )
                                                        }
                                                        val subjectsMap =
                                                            gModel.subjects.filter { it.isActive }
                                                                .associate { it.id to "${it.name}" }

                                                        ExposedDropdownMenuBox(
                                                            expanded = expandedGSubjects,
                                                            onExpandedChange = {
                                                                expandedGSubjects =
                                                                    !expandedGSubjects
                                                            }
                                                        ) {
                                                            // textfield
                                                            val gSubject =
                                                                gModel.subjects.find { it.id == model.cFormGroupSubjectId }

                                                            OutlinedTextField(
                                                                modifier = Modifier
                                                                    .menuAnchor()
                                                                    .defaultMinSize(
                                                                        minWidth = 5.dp
                                                                    ), // menuAnchor modifier must be passed to the text field for correctness.
                                                                readOnly = true,
                                                                value = (gSubject?.name)
                                                                    ?: "",
                                                                placeholder = {
                                                                    Text(
                                                                        "Выберите"
                                                                    )
                                                                },
                                                                onValueChange = {},
                                                                label = { Text("Предмет") },
                                                                trailingIcon = {
                                                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                                                        expanded = expandedGSubjects
                                                                    )
                                                                },
                                                                shape = RoundedCornerShape(
                                                                    15.dp
                                                                ),
                                                                enabled = !(nFGModel.state == NetworkState.Loading)//!model.isCreatingFormInProcess
                                                            )
                                                            // menu

                                                            ExposedDropdownMenu(
                                                                expanded = expandedGSubjects,
                                                                onDismissRequest = {
                                                                    expandedGSubjects =
                                                                        false
                                                                },
                                                            ) {
                                                                // menu items
                                                                subjectsMap.forEach { selectionOption ->
                                                                    DropdownMenuItem(
                                                                        text = {
                                                                            Text(
                                                                                selectionOption.value
                                                                            )
                                                                        },
                                                                        onClick = {
                                                                            component.onEvent(
                                                                                FormsStore.Intent.ChangeCFormGroupSubjectId(
                                                                                    selectionOption.key
                                                                                )
                                                                            )
                                                                            expandedGSubjects =
                                                                                false
                                                                        },
                                                                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                                                    )
                                                                }
                                                            }
                                                        }

                                                        Spacer(Modifier.width(5.dp))

                                                        var expandedGroups by remember {
                                                            mutableStateOf(
                                                                false
                                                            )
                                                        }
                                                        val formGroupsMap =
                                                            model.cutedGroups.associate { it.groupId to it.groupName }

                                                        ExposedDropdownMenuBox(
                                                            expanded = expandedGroups,
                                                            onExpandedChange = {
                                                                expandedGroups =
                                                                    !expandedGroups
                                                            }
                                                        ) {
                                                            // textfield
                                                            val group =
                                                                model.cutedGroups.find { it.groupId == model.cFormGroupGroupId }

                                                            OutlinedTextField(
                                                                modifier = Modifier
                                                                    .menuAnchor()
                                                                    .defaultMinSize(
                                                                        minWidth = 5.dp
                                                                    ),
                                                                readOnly = true,
                                                                value = (group?.groupName)
                                                                    ?: "",
                                                                placeholder = {
                                                                    Text(
                                                                        "Выберите"
                                                                    )
                                                                },
                                                                onValueChange = {},
                                                                label = { Text("Группа") },
                                                                trailingIcon = {
                                                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                                                        expanded = expandedGroups
                                                                    )
                                                                },
                                                                shape = RoundedCornerShape(
                                                                    15.dp
                                                                ),
                                                                enabled = !(nFGModel.state == NetworkState.Loading)
                                                            )
                                                            // menu

                                                            ExposedDropdownMenu(
                                                                expanded = expandedGroups,
                                                                onDismissRequest = {
                                                                    expandedGroups =
                                                                        false
                                                                },
                                                            ) {
                                                                // menu items
                                                                formGroupsMap.forEach { selectionOption ->
                                                                    DropdownMenuItem(
                                                                        text = {
                                                                            Text(
                                                                                selectionOption.value
                                                                            )
                                                                        },
                                                                        onClick = {
//
//                                                                            component.onEvent(
//                                                                                GroupsStore.Intent.ChangeCFormGroupGroupId(
//                                                                                    selectionOption.key
//                                                                                )
//                                                                            )
                                                                            component.onEvent(FormsStore.Intent.ChangeCFormGroupGroupId(selectionOption.key))
                                                                            expandedGroups =
                                                                                false
                                                                        },
                                                                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    }
                                                    if (model.cFormGroupGroupId != 0) {
                                                        Row() {
                                                            IconButton(
                                                                onClick = {
                                                                    component.onEvent(
                                                                        FormsStore.Intent.CloseFormGroupCreationMenu
                                                                    )
                                                                }
                                                            ) {
                                                                Icon(
                                                                    Icons.Rounded.Close,
                                                                    null
                                                                )
                                                            }


                                                            Button(
                                                                onClick = {
                                                                    component.onEvent(
                                                                        FormsStore.Intent.CreateFormGroup
                                                                    )
                                                                }
                                                            ) {
                                                                Text("Создать")
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

                }
            }

            nFModel.state != NetworkState.Error -> {
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