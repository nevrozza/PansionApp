
import achievements.RCreateAchievementReceive
import achievements.RDeleteAchievementReceive
import achievements.REditAchievementReceive
import achievements.RFetchAchievementsResponse
import achievements.RUpdateGroupOfAchievementsReceive
import admin.cabinets.CabinetItem
import admin.cabinets.RFetchCabinetsResponse
import admin.cabinets.RUpdateCabinetsReceive
import admin.calendar.CalendarModuleItem
import admin.calendar.RFetchCalendarResponse
import admin.calendar.RUpdateCalendarReceive
import admin.groups.GroupInit
import admin.groups.forms.FormInit
import admin.groups.forms.RCreateFormGroupReceive
import admin.groups.forms.RFetchCutedGroupsResponse
import admin.groups.forms.RFetchFormGroupsReceive
import admin.groups.forms.RFetchFormGroupsResponse
import admin.groups.forms.outside.CreateFormReceive
import admin.groups.forms.outside.REditFormReceive
import admin.groups.forms.outside.RFetchFormsResponse
import admin.groups.forms.outside.RFetchMentorsResponse
import admin.groups.students.RBindStudentToFormReceive
import admin.groups.students.RFetchStudentsInFormReceive
import admin.groups.students.RFetchStudentsInFormResponse
import admin.groups.students.deep.RCreateStudentGroupReceive
import admin.groups.students.deep.RFetchStudentGroupsReceive
import admin.groups.students.deep.RFetchStudentGroupsResponse
import admin.groups.subjects.RAddStudentToGroup
import admin.groups.subjects.RCreateGroupReceive
import admin.groups.subjects.REditGroupReceive
import admin.groups.subjects.RFetchGroupsReceive
import admin.groups.subjects.RFetchGroupsResponse
import admin.groups.subjects.RFetchTeachersResponse
import admin.groups.subjects.topBar.RCreateSubjectReceive
import admin.groups.subjects.topBar.RDeleteSubject
import admin.groups.subjects.topBar.REditSubjectReceive
import admin.groups.subjects.topBar.RFetchAllSubjectsResponse
import admin.parents.RFetchParentsListResponse
import admin.parents.RUpdateParentsListReceive
import admin.schedule.RFetchInitScheduleResponse
import admin.users.RClearUserPasswordReceive
import admin.users.RCreateExcelStudentsReceive
import admin.users.RCreateUserResponse
import admin.users.RDeleteUserReceive
import admin.users.REditUserReceive
import admin.users.RFetchAllUsersResponse
import admin.users.RRegisterUserReceive
import admin.users.ToBeCreatedStudent
import admin.users.UserInit
import ktor.KtorAdminRemoteDataSource
import schedule.RFetchScheduleDateReceive
import schedule.RScheduleList

class AdminRepositoryImpl(
    private val remoteDataSource: KtorAdminRemoteDataSource
) : AdminRepository {
    override suspend fun addStudentToGroup(r: RAddStudentToGroup) {
        return remoteDataSource.addStudentToGroup(r)
    }

    override suspend fun fetchParents(): RFetchParentsListResponse {
        return remoteDataSource.fetchParents()
    }

    override suspend fun updateParents(r: RUpdateParentsListReceive): RFetchParentsListResponse {
        return remoteDataSource.updateParents(r)
    }

    override suspend fun createAchievement(r: RCreateAchievementReceive): RFetchAchievementsResponse {
        return remoteDataSource.createAchievement(r)
    }

    override suspend fun editAchievement(r: REditAchievementReceive): RFetchAchievementsResponse {
        return remoteDataSource.editAchievement(r)
    }

    override suspend fun updateGroupAchievement(r: RUpdateGroupOfAchievementsReceive): RFetchAchievementsResponse {
        return remoteDataSource.updateGroupAchievement(r)
    }

    override suspend fun deleteAchievement(r: RDeleteAchievementReceive): RFetchAchievementsResponse {
        return remoteDataSource.deleteAchievement(r)
    }

    override suspend fun fetchAllAchievements(): RFetchAchievementsResponse {
        return remoteDataSource.fetchAllAchievements()
    }

    override suspend fun fetchInitSchedule(): RFetchInitScheduleResponse {
        return remoteDataSource.fetchInitSchedule()
    }

    override suspend fun fetchCalendar(): RFetchCalendarResponse {
        return remoteDataSource.fetchCalendar()
    }

    override suspend fun updateCalendar(calendar: List<CalendarModuleItem>) {
        remoteDataSource.updateCalendar(RUpdateCalendarReceive(items = calendar))
    }

    override suspend fun fetchCabinets(): RFetchCabinetsResponse {
        return remoteDataSource.fetchCabinets()
    }

    override suspend fun updateCabinets(cabinets: List<CabinetItem>) {
        remoteDataSource.updateCabinets(
            RUpdateCabinetsReceive(
                cabinets = cabinets
            )
        )
    }

    override suspend fun registerUser(user: UserInit, parents: List<String>?, formId: Int, subjectId: Int?): RCreateUserResponse {
        return remoteDataSource.performRegistrationUser(
            RRegisterUserReceive(
                userInit = UserInit(
                    fio = FIO(
                        name = user.fio.name,
                        surname = user.fio.surname,
                        praname = user.fio.praname
                    ),
                    birthday = user.birthday,
                    role = user.role,
                    moderation = user.moderation,
                    isParent = user.isParent
                ),
                parentFIOs = parents,
                formId = formId,
                subjectId = subjectId
            )
        )
    }

    override suspend fun registerExcelStudents(students: List<ToBeCreatedStudent>) {
        remoteDataSource.createExcelStudents(
            RCreateExcelStudentsReceive(students)
        )
    }

    override suspend fun fetchAllUsers(): RFetchAllUsersResponse {
        return remoteDataSource.performFetchAllUsers()
    }

    override suspend fun clearUserPassword(login: String) {
        remoteDataSource.clearUserPassword(RClearUserPasswordReceive(login))
    }

    override suspend fun editUser(login: String, user: UserInit, subjectId: Int?) {
        remoteDataSource.performEditUser(
            REditUserReceive(
                login = login,
                user = UserInit(
                    fio = FIO(
                        name = user.fio.name,
                        surname = user.fio.surname,
                        praname = user.fio.praname
                    ),
                    birthday = user.birthday,
                    role = user.role,
                    moderation = user.moderation,
                    isParent = user.isParent
                ),
                subjectId = subjectId
            )
        )
    }
    override suspend fun deleteUser(login: String, user: UserInit) {
        remoteDataSource.performDeleteUser(
            RDeleteUserReceive(
                login = login,
                user = UserInit(
                    fio = FIO(
                        name = user.fio.name,
                        surname = user.fio.surname,
                        praname = user.fio.praname
                    ),
                    birthday = user.birthday,
                    role = user.role,
                    moderation = user.moderation,
                    isParent = user.isParent
                )
            )
        )
    }

    override suspend fun fetchAllSubjects(): RFetchAllSubjectsResponse {
        return remoteDataSource.performFetchAllSubjects()
    }

    override suspend fun createSubject(name: String) {
        remoteDataSource.createNewSubject(
            RCreateSubjectReceive(
                name = name
            )
        )
    }

    override suspend fun editSubject(subjectId: Int, name: String) {
        remoteDataSource.editSubject(
            REditSubjectReceive(
                subjectId = subjectId,
                name = name
            )
        )
    }

    override suspend fun deleteSubject(subjectId: Int) {
        remoteDataSource.deleteSubject(
            RDeleteSubject(
                subjectId = subjectId
            )
        )
    }

    override suspend fun fetchGroups(subjectId: Int): RFetchGroupsResponse {
        return remoteDataSource.performFetchGroups(
            RFetchGroupsReceive(
                subjectId = subjectId
            )
        )
    }

    override suspend fun fetchStudentGroups(login: String): RFetchStudentGroupsResponse {
        return remoteDataSource.performFetchStudentGroups(
            RFetchStudentGroupsReceive(
                studentLogin = login
            )
        )
    }

    override suspend fun fetchCutedGroups(id: Int): RFetchCutedGroupsResponse {
        return remoteDataSource.performFetchCutedGroups(
            RFetchGroupsReceive(
                subjectId = id
            )
        )
    }

    override suspend fun fetchFormGroups(id: Int): RFetchFormGroupsResponse {
        return remoteDataSource.performFormGroups(
            RFetchFormGroupsReceive(
                formId = id
            )
        )
    }

    override suspend fun fetchStudentsInForm(formId: Int): RFetchStudentsInFormResponse {
        return remoteDataSource.performStudentsInForm(
            RFetchStudentsInFormReceive(
                formId = formId
            )
        )
    }

    override suspend fun bindStudentToForm(
        login: String, formId: Int
    ) {
        remoteDataSource.bindStudentToForm(
            RBindStudentToFormReceive(
                studentLogin = login,
                formId = formId
            )
        )
    }

    override suspend fun createFormGroup(
        formId: Int,
        subjectId: Int,
        groupId: Int
    ) {
        remoteDataSource.createFormGroup(
            RCreateFormGroupReceive(
                formId = formId,
                subjectId = subjectId,
                groupId = groupId
            )
        )
    }

    override suspend fun editForm(r: REditFormReceive) {
        remoteDataSource.editForm(r)
    }

    override suspend fun deleteFormGroup(
        formId: Int,
        subjectId: Int,
        groupId: Int
    ) {
        remoteDataSource.deleteFormGroup(
            RCreateFormGroupReceive(
                formId = formId,
                subjectId = subjectId,
                groupId = groupId
            )
        )
    }

    override suspend fun createStudentGroup(studentLogin: String, subjectId: Int, groupId: Int) {
        remoteDataSource.createStudentGroup(
            RCreateStudentGroupReceive(
                studentLogin = studentLogin,
                subjectId = subjectId,
                groupId = groupId
            )
        )
    }

    override suspend fun deleteStudentGroup(studentLogin: String, subjectId: Int, groupId: Int) {
        remoteDataSource.deleteStudentGroup(
            RCreateStudentGroupReceive(
                studentLogin = studentLogin,
                subjectId = subjectId,
                groupId = groupId
            )
        )
    }

    override suspend fun createGroup(
        name: String,
        mentorLogin: String,
        subjectId: Int,
        difficult: String
    ) {
        remoteDataSource.createGroup(
            RCreateGroupReceive(
                group = GroupInit(
                    name = name,
                    teacherLogin = mentorLogin,
                    subjectId = subjectId,
                    difficult = difficult
                )
            )
        )
    }

    override suspend fun editGroup(r: REditGroupReceive) {
        remoteDataSource.createGroup(r)
    }

    override suspend fun createForm(
        title: String,
        mentorLogin: String,
        classNum: Int,
        shortTitle: String
    ) {
        remoteDataSource.createForm(
            CreateFormReceive(
                form = FormInit(
                    title = title,
                    mentorLogin = mentorLogin,
                    classNum = classNum,
                    shortTitle = shortTitle
                )
            )
        )
    }

    override suspend fun fetchAllForms(): RFetchFormsResponse {
        return remoteDataSource.performFetchAllForms()
    }

    override suspend fun fetchAllTeachers(): RFetchTeachersResponse {
        return remoteDataSource.performFetchTeachersForGroup()
    }

    override suspend fun fetchAllMentors(): RFetchMentorsResponse {
        return remoteDataSource.performFetchMentorsForGroups()
    }

    override suspend fun fetchSchedule(dayOfWeek: String, date: String, isFirstTime: Boolean): RScheduleList {
        return remoteDataSource.fetchSchedule(
            RFetchScheduleDateReceive(
                dayOfWeek = dayOfWeek,
                day = date,
                isFirstTime = isFirstTime
            )
        )
    }

    override suspend fun saveSchedule(list: RScheduleList) {
        remoteDataSource.saveSchedule(list)
    }
}