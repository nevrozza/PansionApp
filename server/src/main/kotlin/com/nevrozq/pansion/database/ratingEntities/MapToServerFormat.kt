package com.nevrozq.pansion.database.ratingEntities

import report.ServerRatingUnit

fun List<RatingEntityDTO>.mapToServerRatingUnit() = map {
    ServerRatingUnit(
        login = it.login,
        id = it.id,
        content = it.content,
        reason = it.reason,
        isGoToAvg = it.isGoToAvg,
        deployTime = it.deployTime,
        deployDate = it.deployDate,
        deployLogin = it.deployLogin,
        custom = it.custom
    )
}