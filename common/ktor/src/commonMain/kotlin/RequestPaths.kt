object RequestPaths {
    object Tokens {
        const val logout = "server/profile/logout"
    }

    object UserManage {
        const val CreateUser = "server/user/create"
        const val FetchAllUsers = "server/user/fetchAll"
        const val ClearPasswordAdmin = "server/user/clearPassword"
        const val EditUser = "server/user/edit"
    }

    object Auth {
        const val ActivateProfile = "server/auth/activate"
        const val CheckActivation = "server/auth/check"
        const val PerformLogin = "server/auth/login"
    }

    object Lessons {
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
        const val FetchFormGroups = "server/lessons/fetchFormGroups"
        const val FetchTeachersForGroup = "server/lessons/fetchTeachersForGroup"
        const val FetchMentorsForGroup = "server/lessons/fetchMentorsForGroup"
        const val FetchAllForms = "server/lessons/fetchAllForms"
    }
}
