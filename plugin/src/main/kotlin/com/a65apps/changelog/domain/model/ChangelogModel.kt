package com.a65apps.changelog.domain.model

import com.a65apps.changelog.domain.ChangelogInteractor
import com.a65apps.changelog.domain.entity.Changelog
import com.a65apps.changelog.domain.entity.ChangelogEntry
import com.a65apps.changelog.domain.entity.Request
import com.a65apps.changelog.domain.repository.LogEntriesRepository
import com.a65apps.changelog.domain.repository.RootEntryRepository

internal class ChangelogModel(
    private val rootEntryRepository: RootEntryRepository,
    private val logEntriesRepository: LogEntriesRepository
) : ChangelogInteractor {

    override fun compute(request: Request): Changelog {
        val rootEntry: ChangelogEntry
        try {
            rootEntry = rootEntryRepository.findRootEntry(
                request.lastReleaseBranch,
                request.currentReleaseBranch
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return Changelog(
                title = request.currentVersion
            )
        }

        val log: List<ChangelogEntry>
        log = try {
            logEntriesRepository.logEntries(rootEntry)
                .filter { it.taskId.isNotBlank() }
                .filter { !it.message.contains("Merged ") }
                .filter { !it.message.contains("Merge branch ") }
                .map { it.copy(message = "${request.entryDash} ${it.message}") }
                .reversed()
        } catch (e: Exception) {
            e.printStackTrace()
            listOf()
        }
        val (entries, shortEntries) = matchToCharacterLimit(
            log = log,
            limit = request.characterLimit - request.currentVersion.length -
                    request.templateExtraCharactersLength
        )

        return Changelog(
            title = request.currentVersion,
            entries = entries,
            shortEntries = shortEntries
        )
    }

    private fun matchToCharacterLimit(
        log: List<ChangelogEntry>,
        limit: Int
    ): Pair<List<ChangelogEntry>, List<ChangelogEntry>> {
        var short = listOf<ChangelogEntry>()
        var long = log
        fun count(entries: List<ChangelogEntry>, short: Boolean = false) =
            entries.fold(0) { i, entry ->
                i + (if (!short) entry.message.length else entry.foldId.length) + 1
            }

        while (count(long) + count(short, true) > limit) {
            val entry = long.firstOrNull()
            entry?.let {
                short = short + it
                long = long - it
            } ?: break
        }

        return long to short
    }
}
