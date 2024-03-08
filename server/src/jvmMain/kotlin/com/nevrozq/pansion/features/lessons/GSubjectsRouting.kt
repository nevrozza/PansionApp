package com.nevrozq.pansion.features.lessons

import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureLessonsRouting() {
    routing {
        val lessonsController = LessonsController()
        post("server/lessons/fetchAllGSubjects") {
//            val registerController = RegisterController(call)
            lessonsController.fetchAllSubjects(call)
        }
        post("server/lessons/createGSubject") {
            lessonsController.createNewGSubject(call)
        }

        post("server/lessons/fetchSubjectGroups") {
            lessonsController.fetchSubjectGroups(call)
        }

        post("server/lessons/fetchSubjectFormGroups") {
            lessonsController.fetchSubjectGroupsButFormGroup(call)
        }

        post("server/lessons/fetchStudentGroups") {
            lessonsController.fetchStudentGroups(call)
        }
        post("server/lessons/fetchTeacherGroups") {
            lessonsController.fetchTeacherGroups(call)
        }

        post("server/lessons/fetchStudentsInGroup") {
            lessonsController.fetchStudentsInGroup(call)
        }

        post("server/lessons/fetchStudentsInForm") {
            lessonsController.fetchStudentsInForm(call)
        }

        post("server/lessons/createUserForm") {
            lessonsController.createUserForm(call)
        }


        post("server/lessons/createGroup") {
            lessonsController.createNewGroup(call)
        }
        post("server/lessons/createForm") {
            lessonsController.createNewForm(call)
        }
        post("server/lessons/createFormGroup") {
            lessonsController.createNewFormGroup(call)
        }

        post("server/lessons/fetchFormGroups") {
            lessonsController.fetchFormGroups(call)
        }

        post("server/lessons/fetchTeachersForGroup") {
            lessonsController.fetchAllTeachersForGroups(call)
        }

        post("server/lessons/fetchMentorsForGroup") {
            lessonsController.fetchAllMentorsForGroups(call)
        }

        post("server/lessons/fetchAllForms") {
            lessonsController.fetchAllForms(call)
        }

    }
}