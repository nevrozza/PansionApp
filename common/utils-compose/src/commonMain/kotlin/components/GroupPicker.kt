package components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import components.networkInterface.NetworkState
import kotlinx.serialization.Serializable

data class nSCutedGroup(
    val groupId: Int,
    val groupName: String,
    val isActive: Boolean
)

data class nSSubject(
    val id: Int,
    val name: String,
    val isActive: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupPicker(
    isLoading: Boolean,
    subjects: List<nSSubject>,
    chosenSubjectId: Int,
    chosenGroupId: Int,
    cutedGroups: List<nSCutedGroup>,
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
                    Icon(
                        Icons.Rounded.Close,
                        null
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