package homeTasks

import admin.groups.Subject
import admin.groups.forms.CutedGroup
import com.arkivanov.mvikotlin.core.store.Store
import homeTasks.HomeTasksStore.Intent
import homeTasks.HomeTasksStore.Label
import homeTasks.HomeTasksStore.State
import homework.ClientHomeworkItem
import homework.CutedDateTimeGroup
import kotlinx.datetime.LocalDateTime

interface HomeTasksStore : Store<Intent, State, Label> {
    data class State(
        val login: String,
        val name: String,
        val avatarId: Int,
        val loadingDate: String? = null,
        val dates: List<String> = listOf("16.07.2024", "15.07.2024", "14.07.2024", "13.07.2024"),
        val groups: List<CutedDateTimeGroup> = listOf(
            CutedDateTimeGroup(
                id = 0,
                name = "10 кл усиленная",
                localDateTime = LocalDateTime(
                    year = 2024,
                    monthNumber = 7,
                    dayOfMonth = 25,
                    hour = 11,
                    minute = 0,
                    second = 0,
                    nanosecond = 0
                )
            ),
            CutedDateTimeGroup(
                id = 1,
                name = "11 кл усиленная",
                localDateTime = LocalDateTime(
                    year = 2024,
                    monthNumber = 7,
                    dayOfMonth = 25,
                    hour = 11,
                    minute = 0,
                    second = 0,
                    nanosecond = 0
                )
            ),
        ),
        val subjects: Map<Int, String> = mapOf(
            0 to "Математика",
            1 to "Русский"
        ),
        val homeTasks: List<ClientHomeworkItem> = listOf(
            ClientHomeworkItem(
                id = 0,
                date = "16.07.2024",
                time = "22:00",
                subjectId = 0,
                type = "dz4",
                groupId = 0,
                text = "Задачи №1, 2, 3",
                stups = 0,
                fileIds = listOf<Int>(),
                seconds = 0,
                done = false
            ),
            ClientHomeworkItem(
                id = 1,
                date = "16.07.2024",
                time = "22:00",
                subjectId = 0,
                type = "dz4",
                groupId = 0,
                text = "Напишите программу на Kotlin, которая будет конвертировать время, заданное в миллисекундах с начала эпохи (epoch milliseconds), в минуты и часы.",
                stups = 0,
                fileIds = listOf<Int>(),
                seconds = 0,
                done = false
            ),
            ClientHomeworkItem(
                id = 2,
                date = "16.07.2024",
                time = "22:00",
                subjectId = 0,
                type = "dz4",
                groupId = 1,
                text = "Напишите программу на Kotlin, которая будет конвертировать время, заданное в миллисекундах с начала эпохи (epoch milliseconds), в минуты и часы.",
                stups = 11,
                fileIds = listOf<Int>(),
                seconds = 0,
                done = false
            ),
            ClientHomeworkItem(
                id = 3,
                date = "16.07.2024",
                time = "22:00",
                subjectId = 1,
                type = "dz4",
                groupId = 0,
                text = "Напишите программу на Kotlin, которая будет конвертировать время, заданное в миллисекундах с начала эпохи (epoch milliseconds), в минуты и часы.",
                stups = 0,
                fileIds = listOf<Int>(),
                seconds = 0,
                done = false
            ),
        )
    )

    sealed interface Intent {
        data class CheckTask(val taskId: Int, val isCheck: Boolean) : Intent
    }

    sealed interface Message {
        data class TasksUpdated(val tasks: List<ClientHomeworkItem>) : Message
    }

    sealed interface Label

}
