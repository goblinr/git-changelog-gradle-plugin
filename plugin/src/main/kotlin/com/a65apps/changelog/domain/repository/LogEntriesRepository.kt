package com.a65apps.changelog.domain.repository

import com.a65apps.changelog.domain.entity.ChangelogEntry

interface LogEntriesRepository {
    fun logEntries(rootEntry: ChangelogEntry): List<ChangelogEntry>
}
