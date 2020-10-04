package com.a65apps.changelog.domain.entity

import com.a65apps.changelog.LogOrder

data class Request(
    val currentVersion: String,
    val currentReleaseBranch: String,
    val lastReleaseBranch: String,
    val characterLimit: Int,
    val entryDash: String,
    val templateExtraCharactersLength: Int,
    val order: LogOrder = LogOrder.FIRST_TO_LAST
)
