package schedule

import kotlinx.serialization.Serializable

@Serializable
data class RScheduleList(
    val list: HashMap<String, List<ScheduleItem>>
)

@Serializable
data class RPersonScheduleList(
    val list: HashMap<String, List<PersonScheduleItem>>
)