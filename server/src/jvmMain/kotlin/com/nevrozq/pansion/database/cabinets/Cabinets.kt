package com.nevrozq.pansion.database.cabinets

import com.nevrozq.pansion.database.groups.GroupDTO
import com.nevrozq.pansion.database.groups.Groups
import com.nevrozq.pansion.database.studentsInForm.StudentsInForm
import com.nevrozq.pansion.database.studentsInForm.StudentsInForm.uniqueIndex
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object Cabinets : Table() {
    private val login = Cabinets.varchar("login", 30).uniqueIndex()
    private val cabinet = Cabinets.integer("cabinet")


//    private fun deleteLogin(login: String) {
//        try {
//            transaction {
//                 {
//                    (Cabinets.login eq login)
//                }
//            }
//        } catch (e: Throwable) {
//            println(e)
//        }
//    }

    fun insertList(cabinetDTOs: List<CabinetsDTO>) {
        try {
            transaction {
                Cabinets.deleteAll()
                cabinetDTOs.forEach { cabinetDTO ->
//                    deleteLogin(cabinetDTO.login)
                    insert {
                        it[login] = cabinetDTO.login
                        it[cabinet] = cabinetDTO.cabinet
                    }
                }
            }
        } catch (e: Throwable) {
            println(e)
        }
    }

    fun getAllCabinets(): List<CabinetsDTO> {
        return transaction {
            Cabinets.selectAll().map {
                CabinetsDTO(
                    login = it[login],
                    cabinet = it[cabinet]
                )

            }
        }
    }

//    fun delete(formGroupsDTO: CabinetsDTO) {
//        try {
//            transaction {
//               Cabinets.deleteWhere { (groupId eq formGroupsDTO.groupId) and
//                        (subjectId eq formGroupsDTO.subjectId) and
//                        (formId eq formGroupsDTO.formId) }
//            }
//        } catch (e: Throwable) {
//            println(e)
//        }
//    }
//
//    fun getGroupsOfThisForm(formId: Int): List<CabinetsDTO> {
//        return transaction {
//            try {
//                val groups =
//                    Cabinets.select { Cabinets.formId eq formId }
//                groups.map {
//                    CabinetsDTO(
//                        formId = it[Cabinets.formId],
//                        subjectId = it[subjectId],
//                        groupId = it[groupId]
//                    )
//                }
//            } catch (e: Throwable) {
//                println(e)
//                listOf()
//            }
//        }
//    }

    //DELETE
}