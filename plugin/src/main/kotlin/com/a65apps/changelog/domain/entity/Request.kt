package com.a65apps.changelog.domain.entity

data class Request(
    val currentVersion: String,
    val currentReleaseBranch: String,
    val lastReleaseBranch: String,
    val characterLimit: Int,
    val entryDash: String,
    val templateExtraCharactersLength: Int
)
