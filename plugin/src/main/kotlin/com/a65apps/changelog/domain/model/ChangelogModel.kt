package com.a65apps.changelog.domain.model

import com.a65apps.changelog.LogOrder
import com.a65apps.changelog.domain.ChangelogInteractor
import com.a65apps.changelog.domain.entity.Changelog
import com.a65apps.changelog.domain.entity.ChangelogEntry
import com.a65apps.changelog.domain.entity.Request
import com.a65apps.changelog.domain.repository.LogEntriesRepository
import com.a65apps.changelog.domain.repository.RootEntryRepository

@Suppress("TooGenericExceptionCaught")
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
            var result = logEntriesRepository.logEntries(rootEntry)
                .filter { it.taskId.isNotBlank() }
                .filter { !it.message.contains("Merged ") }
                .filter { !it.message.contains("Merge branch ") }
                .map { it.copy(message = "${request.entryDash} ${it.message}") }
            when (request.order) {
                LogOrder.FIRST_TO_LAST -> result = result.reversed()
                LogOrder.LAST_TO_FIRST -> {
                    // LogEntriesRepository already log in last to first order
                }
            }
            result
        } catch (e: Exception) {
            e.printStackTrace()
            listOf()
        }
        val (entries, shortEntries) = matchToCharacterLimit(
            log = log,
            limit = request.characterLimit - request.currentVersion.length -
                request.templateExtraCharactersLength,
            minEntryCount = request.minEntryCount,
            order = request.order
        )

        return Changelog(
            title = request.currentVersion,
            entries = entries,
            shortEntries = shortEntries
        )
    }

    private fun matchToCharacterLimit(
        log: List<ChangelogEntry>,
        limit: Int,
        minEntryCount: Int,
        order: LogOrder
    ): Pair<List<ChangelogEntry>, List<ChangelogEntry>> {
        var short = listOf<ChangelogEntry>()
        var long = log
        fun count(entries: List<ChangelogEntry>, short: Boolean = false) =
            entries.fold(0) { i, entry ->
                i + (if (!short) entry.message.length else entry.foldId.length) + 1
            }

        while (count(long) + count(short, true) > limit) {
            if (long.size <= minEntryCount && short.isNotEmpty()) {
                short = order.removeLast(short)
            } else {
                val entry = order.takeEntry(long)
                entry?.let {
                    short = order.add(short, it)
                    long = long - it
                } ?: break
            }
        }

        return long to short
    }

    private fun LogOrder.takeEntry(list: List<ChangelogEntry>) = when (this) {
        LogOrder.FIRST_TO_LAST -> list.firstOrNull()
        LogOrder.LAST_TO_FIRST -> list.lastOrNull()
    }

    private fun LogOrder.add(list: List<ChangelogEntry>, entry: ChangelogEntry) = when (this) {
        LogOrder.FIRST_TO_LAST -> list + entry
        LogOrder.LAST_TO_FIRST -> listOf(entry) + list
    }

    private fun LogOrder.removeLast(list: List<ChangelogEntry>) = when (this) {
        LogOrder.FIRST_TO_LAST -> list - list.last()
        LogOrder.LAST_TO_FIRST -> list - list.first()
    }
}
