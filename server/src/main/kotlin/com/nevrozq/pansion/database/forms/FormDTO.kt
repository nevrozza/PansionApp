package com.nevrozq.pansion.database.forms

import admin.groups.forms.Form
import admin.groups.forms.FormInit
import kotlinx.serialization.Serializable

data class FormDTO(
    val formId: Int = -1,
    val title: String,
    val shortTitle: String,
    val classNum: Int,
    val mentorLogin: String,
    val isActive: Boolean
)

fun FormDTO.mapToForm() =
    Form(
        id = this.formId,
        form = FormInit(
            title = this.title,
            shortTitle = this.shortTitle,
            mentorLogin = this.mentorLogin,
            classNum = this.classNum
        ),
        isActive = this.isActive
    )

