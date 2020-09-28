package com.a65apps.changelog.domain.entity

data class ChangelogEntry(
    val hash: String,
    val taskId: String = "",
    val message: String = "",
    val foldId: String = ""
)
