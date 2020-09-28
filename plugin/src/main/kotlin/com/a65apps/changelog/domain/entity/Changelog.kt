package com.a65apps.changelog.domain.entity

data class Changelog(
    val title: String,
    val entries: List<ChangelogEntry> = listOf(),
    val shortEntries: List<ChangelogEntry> = listOf()
)
