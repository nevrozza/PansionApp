package com.nevrozq.pansion.database.formGroups

import admin.groups.forms.FormGroup
import com.nevrozq.pansion.database.groups.Groups

data class FormGroupDTO(
    val formId: Int,
    val subjectId: Int, //Т.К. уже есть список =)
    val groupId: Int,
)

fun FormGroupDTO.mapToFormGroup() =
    FormGroup(
        groupId = this.groupId,
        groupName = Groups.getName(this.groupId),
        subjectId = this.subjectId
    )