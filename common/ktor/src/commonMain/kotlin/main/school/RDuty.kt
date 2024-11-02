package main.school

import FIO
import Person
import PersonPlus
import kotlinx.serialization.Serializable


@Serializable
data class RStartNewDayDuty(
    val newDutiesCount: Int
)

@Serializable
data class RUpdateTodayDuty(
    val newDutiesCount: Int,
    val kids: List<String>
)

@Serializable
data class RFetchDutyReceive(
    val login: String
)
@Serializable
data class RFetchDutyResponse(
    val list: List<DutyKid>,
    val peopleCount: Int
)

@Serializable
data class DutyKid(
    val fio: FIO,
    val login: String,
    val avatarId: Int,
    val dutyCount: Int
)