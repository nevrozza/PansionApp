package schedule

import kotlinx.serialization.Serializable

@Serializable
data class RScheduleList(
    val list: List<Pair<String, List<ScheduleItem>>>
)