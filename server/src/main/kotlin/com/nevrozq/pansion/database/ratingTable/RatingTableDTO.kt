package com.nevrozq.pansion.database.ratingTable

import FIO
import rating.PansionPeriod
import rating.RatingItem

data class RatingTableDTO(
    val login: String,
    val name: String,
    val surname: String,
    val praname: String?,
    val avatarId: Int,

    val stups: Int,
    val avg: String,

    val avgAlg: Float,
    val stupsAlg: Float,
    val topAvg: Int,
    val topStups: Int,

    val difficulty: Int,

    val top: Int,
    val groupName: String,
    val formNum: Int,
    val formShortTitle: String,

    val subjectId: Int,

    val period: PansionPeriod,
    val edYear: Int
)


fun RatingTableDTO.toRatingItem() =
    RatingItem(
        login = this.login,
        fio = FIO(
            name = this.name,
            surname = this.surname,
            praname = this.praname
        ),
        avatarId = this.avatarId,
        stups = this.stups,
        top = this.top,
        groupName = this.groupName,
        formNum = this.formNum,
        formShortTitle = this.formShortTitle,
        avg = this.avg,
        avgAlg = this.avgAlg,
        stupsAlg = this.stupsAlg,
        topAvg = this.topAvg,
        topStups = this.topStups,
        difficulty = this.difficulty
    )

