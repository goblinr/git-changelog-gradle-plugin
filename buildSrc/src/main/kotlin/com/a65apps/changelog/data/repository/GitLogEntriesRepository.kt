package com.a65apps.changelog.data.repository

import com.a65apps.changelog.domain.entity.ChangelogEntry
import com.a65apps.changelog.domain.repository.LogEntriesRepository
import org.ajoberstar.grgit.Grgit

private const val SHORT_HASH_LEN = 8

internal class GitLogEntriesRepository(
    private val git: Grgit
) : LogEntriesRepository {

    override fun logEntries(rootEntry: ChangelogEntry): List<ChangelogEntry> = git.log {
        it.range(rootEntry.hash, "HEAD")
    }.map {
        val message = it.shortMessage.trim()
        val taskId = message.substringBefore(":", "")
        ChangelogEntry(
            hash = it.id,
            message = message,
            taskId = message.substringBefore(":", ""),
            foldId = "$taskId(${it.id.take(SHORT_HASH_LEN)})"
        )
    }
}
