package com.nevrozq.pansion.database.subjects

import admin.groups.Subject
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

object Subjects : Table() {
    private val id = Subjects.integer("id").autoIncrement().uniqueIndex()
    private val name = Subjects.varchar("name", 50)
    private val isActive = Subjects.bool("isActive")

    fun insert(subjectDTO: SubjectDTO) {
        try {
            transaction {
                Subjects.insert {
                    it[name] = subjectDTO.name
                    it[isActive] = true
                }
            }
        } catch (e: Throwable) {
            println(e)
        }
    }

    fun fetchAllSubjects(): List<SubjectDTO> {
        return transaction {
            Subjects.selectAll().map {
                SubjectDTO(name = it[name], id = it[Subjects.id], isActive = it[isActive])
            }
        }
    }

//    fun deleteSubject(id: Int) {
//        try {
//            transaction {
//                Subjects.update({Subjects.id eq id}) {
//                    it[isActive] = false
//                }
//            }
//        } catch (e: Throwable) {
//            println(e)
//        }
//    }


//    fun updateSubject(id: Int, subjectDTO: SubjectDTO) {
//        try {
//            transaction {
//                Subjects.update({ Subjects.id eq id }) {
//                    it[Subjects.name] = subjectDTO.name
//                    it[isActive] = subjectDTO.isActivated
//                }
//            }
//        } catch (e: Throwable) {
//            println(e)
//        }
//    }
}