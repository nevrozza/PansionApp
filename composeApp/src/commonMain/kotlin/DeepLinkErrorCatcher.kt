import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import root.RootComponent
import root.RootComponent.Config
import root.store.QuickRoutings
import root.store.RootStore

@Composable
fun DeepLinkErrorCatcher(
    component: RootComponent
) {
    val model by component.model.subscribeAsState()

    LaunchedEffect(model.startRouting) {
        if (model.startRouting != null) {
            if (model.startRouting !in listOf(QuickRoutings.HomeAllGroupMarks, QuickRoutings.LessonReport)) {
                val login = component.urlArgs["login"]!!
                val config = if (model.startUser != null) {
                    val user = model.startUser!!
                    if (user.fio != null) {
                        when (model.startRouting) {
                            QuickRoutings.HomeAchievements -> Config.HomeAchievements(
                                studentLogin = login,
                                avatarId = user.avatarId,
                                name = user.fio!!.name
                            )

                            QuickRoutings.SecondView -> {
                                val isMentoring = component.urlArgs["isM"]?.toBoolean() ?: false
                                Config.SecondView(
                                    login = login,
                                    fio = user.fio!!,
                                    avatarId = user.avatarId,
                                    config = Config.MainHome,
                                    isMentoring = isMentoring
                                )
                            }

                            null -> null
                            QuickRoutings.HomeAllGroupMarks -> null
                            QuickRoutings.HomeDetailedStups -> {
                                val reason = component.urlArgs["reason"] ?: "0"
                                Config.HomeDetailedStups(
                                    studentLogin = login,
                                    name = user.fio!!.name,
                                    avatarId = user.avatarId,
                                    reason = reason
                                )
                            }

                            QuickRoutings.HomeDnevnikRuMarks -> Config.HomeDnevnikRuMarks(login)
                            QuickRoutings.HomeProfile -> {
                                val deviceLogin = component.authRepository.fetchLogin()
                                Config.HomeProfile(
                                    studentLogin = login,
                                    fio = user.fio!!,
                                    avatarId = user.avatarId,
                                    isOwner = deviceLogin == login,
                                    isCanEdit = deviceLogin == login
                                )
                            }

                            QuickRoutings.HomeStudentLines -> Config.HomeStudentLines(login)
                            QuickRoutings.HomeTasks -> Config.HomeTasks(
                                studentLogin = login,
                                avatarId = user.avatarId,
                                name = user.fio!!.name
                            )

                            QuickRoutings.LessonReport -> null
                        }
                    } else {
                        Config.ErrorScreen(
                            reason = "Пользователь не найден",
                            path = component.wholePath
                        )
                    }
                } else {
                    Config.ErrorScreen(
                        reason = "Произошла ошибка",
                        path = component.wholePath
                    )
                }
                component.onEvent(RootStore.Intent.DeleteStart)
                component.startOutput(config!!)
            } else if (model.startRouting is QuickRoutings.HomeAllGroupMarks) {
                val groupId = component.urlArgs["groupId"]!!.toIntOrNull() ?: 0
                val config = if (model.startGroup != null) {
                    val group = model.startGroup!!
                    if (group.subjectId != null) {
                        Config.HomeAllGroupMarks(
                            groupId = groupId,
                            groupName = group.groupName,
                            subjectId = group.subjectId!!,
                            subjectName = group.subjectName,
                            teacherLogin = group.teacherLogin
                        )
                    } else {
                        Config.ErrorScreen(
                            reason = "Группа не найдена",
                            path = component.wholePath
                        )
                    }
                } else {
                    Config.ErrorScreen(
                        reason = "Произошла ошибка",
                        path = component.wholePath
                    )
                }
                component.onEvent(RootStore.Intent.DeleteStart)
                component.startOutput(config)
            } else if (model.startRouting is QuickRoutings.LessonReport) {
//                val reportId = component.urlArgs["id"]!!.toIntOrNull() ?: 0
                val config = if (model.startReport != null) {
                    val report = model.startReport!!

                    Config.LessonReport(report)
//                    if (report.subjectId != null) {
//                    } else {
//                        Config.ErrorScreen(
//                            reason = "Группа не найдена",
//                            path = component.wholePath
//                        )
//                    }
                } else {
                    Config.ErrorScreen(
                        reason = "Произошла ошибка\nВозможно, введён неправильный id",
                        path = component.wholePath
                    )
                }
                component.onEvent(RootStore.Intent.DeleteStart)
                component.startOutput(config)
            }
        }
    }
}