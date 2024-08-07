object RequestPaths {
    const val ip = "192.168.0.107:8081"

    object Achievements {
        const val Create = "server/achievements/create"
        const val FetchAll = "server/achievements/fetchAll"
        const val FetchForStudent = "server/achievements/fetchByLogin"
        const val Edit = "server/achievements/edit"
        const val UpdateGroup = "server/achievements/updateGroup"
    }

    object UserManage {
        const val CreateUser = "server/user/create"
        const val FetchAllUsers = "server/user/fetchAll"
        const val ClearPasswordAdmin = "server/user/clearPassword"
        const val EditUser = "server/user/edit"
    }

    object Main {
        const val FetchMainAVG = "server/main/fetchAVG"
        const val FetchHomeTasksCount = "server/main/fetchHomeTasksCount"

        const val FetchNotifications = "server/main/fetchNotifications"
        const val CheckNotification = "server/main/checkNotification"
        const val FetchScheduleSubjects = "server/main/fetchSubjects"
        const val FetchSubjectRating = "server/main/fetchSubjectRating"
    }

    object Mentoring {
        const val FetchMentoringStudents = "server/mentoring/fetchStudents"

        const val FetchPreAttendanceDay = "server/mentoring/fetchPreAttendanceDay"
        const val SavePreAttendanceDay = "server/mentoring/savePreAttendanceDay"
    }

    object HomeTasks {
        const val SaveReportHomeTasks = "server/reports/saveHomeTasks"
        const val FetchReportHomeTasks = "server/reports/fetchHomeTasks"
        const val FetchGroupHomeTasks = "server/groups/fetchHomeTasks"

        const val FetchHomeTasksInit = "server/homeTasks/Init"
        const val FetchHomeTasks = "server/homeTasks/Fetch"
        const val CheckTask = "server/homeTasks/Check"
    }

    object Reports {
        const val FetchReportHeaders = "server/reports/fetchHeaders"
        const val FetchDetailedStups = "server/reports/fetchDetailedStups"
        const val FetchReportData = "server/reports/fetchData"
        const val FetchFullReportData = "server/reports/fetchFullData"
        const val FetchAllGroupMarks = "server/reports/fetchAllGroupMarks"
        const val CreateReport = "server/reports/create"
        const val UpdateReport = "server/reports/update"
        const val FetchReportStudents = "server/reports/fetchStudents"
        const val FetchSubjectQuarterMarks = "server/reports/fetchSubjectQuarterMarks"
        const val FetchRecentGrades = "server/reports/fetchRecentGrades"

        const val FetchDnevnikRuMarks = "server/reports/user/fetchDnevnikRuMarks"
        const val FetchIsQuarters = "server/reports/user/fetchIsQuarters"
    }

    object Auth {
        const val ActivateProfile = "server/auth/activate"
        const val CheckActivation = "server/auth/check"
        const val PerformLogin = "server/auth/login"
        const val CheckConnection = "server/checkConnection"

        const val ChangeAvatarId = "server/changeAvatarId"

        const val FetchAboutMe = "server/fetchAboutMe"

        const val Logout = "server/profile/logout"
        const val FetchAllDevices = "server/devices/fetchAll"
        const val TerminateDevice = "server/devices/terminate"
    }

    object Lessons {
        const val FetchSchedule = "server/admin/schedule/fetch"
        const val SaveSchedule = "server/admin/schedule/save"
        const val FetchPersonSchedule = "server/PersonSchedule"

        const val FetchCalendar = "server/admin/fetchCalendar"
        const val UpdateCalendar = "server/admin/updateCalendar"


        const val FetchCabinets = "server/admin/fetchCabinets"
        const val UpdateCabinets = "server/admin/updateCabinets"
        const val FetchAllSubjects = "server/lessons/fetchAllSubjects"
        const val CreateSubject = "server/lessons/createSubject"
        const val FetchGroups = "server/lessons/fetchGroups"
        const val FetchCutedGroups = "server/lessons/fetchCutedGroups"
        const val FetchStudentGroups = "server/lessons/fetchStudentGroups"
        const val FetchTeacherGroups = "server/lessons/fetchTeacherGroups"
        const val FetchStudentsInGroup = "server/lessons/fetchStudentsInGroup"
        const val FetchStudentsInForm = "server/lessons/fetchStudentsInForm"
        const val BindStudentToForm = "server/lessons/bindStudentToForm"
        const val CreateGroup = "server/lessons/createGroup"
        const val CreateForm = "server/lessons/createForm"
        const val CreateFormGroup = "server/lessons/createFormGroup"
        const val CreateStudentGroup = "server/lessons/createStudentGroup"
        const val FetchFormGroups = "server/lessons/fetchFormGroups"
        const val FetchTeachersForGroup = "server/lessons/fetchTeachersForGroup"
        const val FetchMentorsForGroup = "server/lessons/fetchMentorsForGroup"
        const val FetchAllForms = "server/lessons/fetchAllForms"
        const val DeleteStudentGroup = "server/lessons/deleteStudentGroup"
        const val DeleteFormGroup = "server/lessons/deleteFormGroup"

        const val FetchInitSchedule = "server/lessons/fetchInitSchedule"
    }
}
