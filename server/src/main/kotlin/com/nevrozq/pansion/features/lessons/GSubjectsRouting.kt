package com.nevrozq.pansion.features.lessons

import RequestPaths
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureLessonsRouting() {
    routing {
        val lessonsController = LessonsController()

        post(RequestPaths.WebLoad.FetchGroupData) {
            lessonsController.fetchGroupData(call)
        }

        post(RequestPaths.Reports.MarkLesson) {
            lessonsController.markLesson(call)
        }

        post(RequestPaths.Lessons.AddStudentToGroupFromSubject) {
            lessonsController.addStudentToGroupFromSubject(call)
        }

        post(RequestPaths.Main.FetchChildrenNotifications) {
            lessonsController.fetchMainChildrenNotifications(call)
        }

        post(RequestPaths.Main.CheckNotification) {
            lessonsController.checkMainNotification(call)
        }

        post(RequestPaths.Main.FetchNotifications) {
            lessonsController.fetchMainNotifications(call)
        }

        post(RequestPaths.Lessons.FetchCalendar) {
            lessonsController.fetchCalendar(call)
        }

        post(RequestPaths.Lessons.UpdateCalendar) {
            lessonsController.updateCalendar(call)
        }

        post(RequestPaths.Main.FetchScheduleSubjects) {
            lessonsController.fetchScheduleSubjects(call)
        }

        post(RequestPaths.Main.FetchSubjectRating) {
            lessonsController.fetchRating(call)
        }

        post(RequestPaths.Lessons.FetchSchedule) {
            lessonsController.fetchSchedule(call)
        }

        post(RequestPaths.Lessons.SaveSchedule) {
            lessonsController.saveSchedule(call)
        }

        post(RequestPaths.Lessons.FetchPersonSchedule) {
            lessonsController.fetchPersonSchedule(call)
        }

        post(RequestPaths.Lessons.FetchCabinets) {
            lessonsController.fetchAllCabinets(call)
        }

        post(RequestPaths.Lessons.UpdateCabinets) {
            lessonsController.updateCabinets(call)
        }


        post(RequestPaths.Lessons.FetchInitSchedule) {
            lessonsController.fetchInitSchedule(call)
        }

        post(RequestPaths.Lessons.FetchAllSubjects) {
            lessonsController.fetchAllSubjects(call)
        }
        post(RequestPaths.Lessons.CreateSubject) {
            lessonsController.createSubject(call)
        }

        post(RequestPaths.Lessons.FetchGroups) {
            lessonsController.fetchGroups(call)
        }

        post(RequestPaths.Lessons.FetchCutedGroups) {
            lessonsController.fetchCutedGroups(call)
        }

        post(RequestPaths.Lessons.FetchStudentGroups) {
            lessonsController.fetchStudentGroups(call)
        }

        post(RequestPaths.Lessons.CreateStudentGroup) {
            lessonsController.createStudentGroup(call)
        }

        post(RequestPaths.Lessons.DeleteStudentGroup) {
            lessonsController.deleteStudentGroup(call)
        }

        post(RequestPaths.Lessons.DeleteFormGroup) {
            lessonsController.deleteFormGroup(call)
        }

        post(RequestPaths.Lessons.FetchTeacherGroups) {
            lessonsController.fetchTeacherGroups(call)
        }

        post(RequestPaths.Lessons.FetchStudentsInGroup) {
            lessonsController.fetchStudentsInGroup(call)
        }

        post(RequestPaths.Lessons.FetchStudentsInForm) {
            lessonsController.fetchStudentsInForm(call)
        }

        post(RequestPaths.Lessons.BindStudentToForm) {
            lessonsController.bindStudentToForm(call)
        }


        post(RequestPaths.Lessons.CreateGroup) {
            lessonsController.createGroup(call)
        }
        post(RequestPaths.Lessons.EditGroup) {
            lessonsController.editGroup(call)
        }
        post(RequestPaths.Lessons.EditForm) {
            lessonsController.editForm(call)
        }
        post(RequestPaths.Lessons.DeleteSubject) {
            lessonsController.deleteSubject(call)
        }
        post(RequestPaths.Lessons.EditSubject) {
            lessonsController.editSubject(call)
        }
        post(RequestPaths.Lessons.CreateForm) {
            lessonsController.createForm(call)
        }
        post(RequestPaths.Lessons.CreateFormGroup) {
            lessonsController.createFormGroup(call)
        }

        post(RequestPaths.Lessons.FetchFormGroups) {
            lessonsController.fetchFormGroups(call)
        }

        post(RequestPaths.Lessons.FetchTeachersForGroup) {
            lessonsController.fetchAllTeachersForGroups(call)
        }

        post(RequestPaths.Lessons.FetchMentorsForGroup) {
            lessonsController.fetchAllMentorsForGroups(call)
        }

        post(RequestPaths.Lessons.FetchAllForms) {
            lessonsController.fetchAllForms(call)
        }

    }
}