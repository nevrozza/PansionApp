package main

import Person
import PersonPlus
import kotlinx.serialization.Serializable

@Serializable
data class RFetchChildrenResponse(
    val children: List<PersonPlus>
)