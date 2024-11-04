package schedule

import kotlinx.serialization.Serializable

@Serializable
data class RScheduleList(
    val list: HashMap<String, List<ScheduleItem>>,
    val conflictList: HashMap<String, MutableMap<Int, List<String>>>,
)

@Serializable
data class RPersonScheduleList(
    val list: HashMap<String, List<PersonScheduleItem>>
)