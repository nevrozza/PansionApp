package com.nevrozq.pansion.database.subjects

import admin.groups.Subject
import com.nevrozq.pansion.database.groups.GroupDTO
import com.nevrozq.pansion.database.groups.Groups
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

object Subjects : Table() {
    val id = Subjects.integer("id").autoIncrement().uniqueIndex()
    val name = Subjects.varchar("name", 50)
    val isActive = Subjects.bool("isActive")


    fun update(subjectId: Int, name: String?, isActive: Boolean = true) {
        transaction {
            Subjects.update({Subjects.id eq subjectId}) {
                if(name != null) {
                    it[Subjects.name] = name
                }
                it[Subjects.isActive] = isActive
            }
        }
    }

    fun getSubjectById(subjectId: Int): SubjectDTO? {
        return transaction {
            try {
                val s =
                    Subjects.select { Subjects.id eq subjectId }.first()

                SubjectDTO(
                    id = s[Subjects.id],
                    name = s[name],
                    isActive = s[isActive]
                )
            } catch (e: Throwable) {
                println(e)
                null
            }
        }
    }
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

    fun fetchAllSubjectsAsMap(): Map<Int, String> {
        return transaction {
            Subjects.selectAll().associate {
                it[Subjects.id] to it[name]
            }
        }
    }


    fun fetchAllSubjects(): List<SubjectDTO> {
        return transaction {
            Subjects.selectAll().map {
                SubjectDTO(name = it[name], id = it[Subjects.id], isActive = it[isActive])
            }
        }
    }

    fun fetchName(id: Int): String {
        return transaction {
            try {

                Subjects.select { Subjects.id eq id }.first()[Subjects.name]
            } catch (_: Throwable) {
                "null"
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