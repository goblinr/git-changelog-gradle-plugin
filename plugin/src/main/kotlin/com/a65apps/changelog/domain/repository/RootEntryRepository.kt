package com.a65apps.changelog.domain.repository

import com.a65apps.changelog.domain.entity.ChangelogEntry

interface RootEntryRepository {
    fun findRootEntry(
        lastReleaseBranch: String,
        currentReleaseBranch: String
    ): ChangelogEntry
}
