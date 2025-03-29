package com.nevrozq.pansion.database.subjects

import admin.groups.Group
import admin.groups.GroupInit
import admin.groups.Subject
import com.nevrozq.pansion.database.groups.GroupDTO

data class SubjectDTO(
    val id: Int = -1,
    val name: String,
    val isActive: Boolean
)


fun SubjectDTO.mapToSubject() =
    Subject(
        id = this.id,
        name = this.name,
        isActive = this.isActive
    )