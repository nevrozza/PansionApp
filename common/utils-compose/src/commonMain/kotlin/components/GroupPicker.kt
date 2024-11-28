package components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import resources.RIcons

data class NSCutedGroup(
    val groupId: Int,
    val groupName: String,
    val isActive: Boolean
)

data class NSSubject(
    val id: Int,
    val name: String,
    val isActive: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupPicker(
    isLoading: Boolean,
    subjects: List<NSSubject>,
    chosenSubjectId: Int,
    chosenGroupId: Int,
    cutedGroups: List<NSCutedGroup>,
    sortedList: List<Int>,
    onSubjectClick: (Int) -> Unit,
    onGroupClick: (Int) -> Unit,
    onCloseClick: () -> Unit,
    onSubmitClick: () -> Unit
 ) {
    Column(horizontalAlignment = Alignment.End) {
        Row {
            var expandedGSubjects by remember {
                mutableStateOf(
                    false
                )
            }
            val subjectsMap =
                subjects.filter { it.isActive }
                    .sortedBy { it.id in sortedList }
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
                   subjects.find { it.id == chosenSubjectId }

                OutlinedTextField(
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
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
                        val chevronRotation = animateFloatAsState(if (expandedGSubjects) 90f else -90f)
                        GetAsyncIcon(
                            path = RIcons.ChevronLeft,
                            modifier = Modifier.padding(end = 10.dp).rotate(chevronRotation.value),
                            size = 15.dp
                        )
                    },
                    shape = RoundedCornerShape(
                        15.dp
                    ),
                    enabled = !isLoading//!model.isCreatingFormInProcess
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
                                    selectionOption.value,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (selectionOption.key in sortedList) 0.3f else 1f)
                                )
                            },
                            onClick = {
                                onSubjectClick(selectionOption.key)
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
                cutedGroups.associate { it.groupId to it.groupName }

            ExposedDropdownMenuBox(
                expanded = expandedGroups,
                onExpandedChange = {
                    expandedGroups =
                        !expandedGroups
                }
            ) {
                // textfield
                val group =
                    cutedGroups.find { it.groupId == chosenGroupId }

                OutlinedTextField(
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
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
                        val chevronRotation = animateFloatAsState(if (expandedGroups) 90f else -90f)
                        GetAsyncIcon(
                            path = RIcons.ChevronLeft,
                            modifier = Modifier.padding(end = 10.dp).rotate(chevronRotation.value),
                            size = 15.dp
                        )
                    },
                    shape = RoundedCornerShape(
                        15.dp
                    ),
                    enabled = !isLoading
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
                                onGroupClick(selectionOption.key)
                                expandedGroups =
                                    false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }
            }
        }
        if (chosenGroupId != 0) {
            Row() {
                IconButton(
                    onClick = {
                        onCloseClick()
                    }
                ) {
                    GetAsyncIcon(
                        RIcons.Close
                    )
                }


                Button(
                    onClick = {
                        onSubmitClick()
                    }
                ) {
                    Text("Создать")
                }
            }
        }
    }
}