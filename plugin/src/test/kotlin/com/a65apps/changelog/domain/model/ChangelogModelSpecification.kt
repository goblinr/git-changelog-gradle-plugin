package com.a65apps.changelog.domain.model

import com.a65apps.changelog.domain.entity.Changelog
import com.a65apps.changelog.domain.entity.ChangelogEntry
import com.a65apps.changelog.domain.entity.Request
import com.a65apps.changelog.domain.repository.LogEntriesRepository
import com.a65apps.changelog.domain.repository.RootEntryRepository
import io.mockk.every
import io.mockk.mockk
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import org.spekframework.spek2.style.gherkin.FeatureBody
import org.spekframework.spek2.style.gherkin.ScenarioBody

object ChangelogModelSpecification : Spek({

    Feature("плагин должен уметь генерировать changelog.md") {
        val currentName = "rc_1.1"
        val rcName = "rc_1.0"
        val rootCommit = "a78bfa2d3763fd3db814b79a8aac8dcfea323ee3"

        setUp()

        Scenario("успешный сценарий генерации") {

            val allCommits = listOf(
                ChangelogEntry(
                    hash = "d2a85293746a6b11618e2bf1a68747ffdb2593a6",
                    taskId = "1",
                    message = "1: message",
                    foldId = "1(d2a85293)"
                ),
                ChangelogEntry(
                    hash = "6ea76e31602cb734e24e094403ac0dabf20b52a2",
                    taskId = "2",
                    message = "2: message",
                    foldId = "2(6ea76e31)"
                )
            )

            dataSetUp(
                currentName = currentName,
                rcName = rcName,
                rootCommit = rootCommit,
                allCommits = allCommits
            )

            test(
                request = Request(
                    currentVersion = currentName,
                    lastReleaseBranch = rcName,
                    characterLimit = 100,
                    entryDash = "-",
                    templateExtraCharactersLength = 0,
                    currentReleaseBranch = currentName
                ),
                expected = Changelog(
                    title = currentName,
                    entries = allCommits.reversed().map {
                        it.copy(message = "- ${it.message}")
                    }
                )
            )
        }

        Scenario("неуспешный сценарий, первый коммит не найден") {
            dataSetUp(
                currentName = currentName,
                rcName = rcName,
                rootCommit = "",
                allCommits = listOf()
            )

            test(
                request = Request(
                    currentVersion = currentName,
                    lastReleaseBranch = rcName,
                    characterLimit = 100,
                    entryDash = "-",
                    templateExtraCharactersLength = 0,
                    currentReleaseBranch = currentName
                ),
                expected = Changelog(
                    title = currentName
                )
            )
        }

        Scenario("неуспешный сценарий, репозиторий упал с ошибкой") {
            val rootEntryRepository: RootEntryRepository by memoized()

            Given("репозиторий падает с какой-то ошибкой") {
                every { rootEntryRepository.findRootEntry(rcName, currentName) } throws Exception()
            }

            test(
                request = Request(
                    currentVersion = currentName,
                    lastReleaseBranch = rcName,
                    characterLimit = 100,
                    entryDash = "-",
                    templateExtraCharactersLength = 0,
                    currentReleaseBranch = currentName
                ),
                expected = Changelog(
                    title = currentName
                )
            )
        }

        Scenario("есть мержевые коммиты, которые не должны попасть в changelog.md") {
            val allCommits = listOf(
                ChangelogEntry(
                    hash = "d2a85293746a6b11618e2bf1a68747ffdb2593a6",
                    taskId = "1",
                    message = "Merged PR 123",
                    foldId = "1(d2a85293)"
                ),
                ChangelogEntry(
                    hash = "d2a85293746a6b11618e2bf1a68747ffdb2593a6",
                    taskId = "1",
                    message = "Merge branch 'feature/30158' into 'develop'",
                    foldId = "1(d2a85293)"
                ),
                ChangelogEntry(
                    hash = "6ea76e31602cb734e24e094403ac0dabf20b52a2",
                    taskId = "2",
                    message = "2: message",
                    foldId = "2(6ea76e31)"
                )
            )

            dataSetUp(
                currentName = currentName,
                rcName = rcName,
                rootCommit = rootCommit,
                allCommits = allCommits
            )

            test(
                request = Request(
                    currentVersion = currentName,
                    lastReleaseBranch = rcName,
                    characterLimit = 100,
                    entryDash = "-",
                    templateExtraCharactersLength = 0,
                    currentReleaseBranch = currentName
                ),
                expected = Changelog(
                    title = currentName,
                    entries = listOf(
                        ChangelogEntry(
                            hash = "6ea76e31602cb734e24e094403ac0dabf20b52a2",
                            taskId = "2",
                            message = "- 2: message",
                            foldId = "2(6ea76e31)"
                        )
                    )
                )
            )
        }

        Scenario("есть коммиты без id, которые не должны попасть в changelog.md") {
            val allCommits = listOf(
                ChangelogEntry(
                    hash = "d2a85293746a6b11618e2bf1a68747ffdb2593a6",
                    taskId = "",
                    message = "Плохое описание без ID",
                    foldId = ""
                ),
                ChangelogEntry(
                    hash = "6ea76e31602cb734e24e094403ac0dabf20b52a2",
                    taskId = "2",
                    message = "2: message",
                    foldId = "2(6ea76e31)"
                )
            )

            dataSetUp(
                currentName = currentName,
                rcName = rcName,
                rootCommit = rootCommit,
                allCommits = allCommits
            )

            test(
                request = Request(
                    currentVersion = currentName,
                    lastReleaseBranch = rcName,
                    characterLimit = 100,
                    entryDash = "-",
                    templateExtraCharactersLength = 0,
                    currentReleaseBranch = currentName
                ),
                expected = Changelog(
                    title = currentName,
                    entries = listOf(
                        ChangelogEntry(
                            hash = "6ea76e31602cb734e24e094403ac0dabf20b52a2",
                            taskId = "2",
                            message = "- 2: message",
                            foldId = "2(6ea76e31)"
                        )
                    )
                )
            )
        }
    }

    Feature("плагин должен уметь схлопывать старые коммиты в секцию Fold " +
            "если не хватает лимита символов") {
        val currentName = "rc_1.1"
        val rcName = "rc_1.0"
        val rootCommit = "a78bfa2d3763fd3db814b79a8aac8dcfea323ee3"

        setUp()

        Scenario("не хватило лимита символов") {
            val allCommits = listOf(
                ChangelogEntry(
                    hash = "d2a85293746a6b11618e2bf1a68747ffdb2593a6",
                    taskId = "1",
                    message = "1: message message message",
                    foldId = "1(d2a85293)"
                ),
                ChangelogEntry(
                    hash = "6ea76e31602cb734e24e094403ac0dabf20b52a2",
                    taskId = "2",
                    message = "2: message message message",
                    foldId = "2(6ea76e31)"
                )
            )

            dataSetUp(
                currentName = currentName,
                rcName = rcName,
                rootCommit = rootCommit,
                allCommits = allCommits
            )

            test(
                request = Request(
                    currentVersion = currentName,
                    lastReleaseBranch = rcName,
                    characterLimit = 50,
                    entryDash = "-",
                    templateExtraCharactersLength = 0,
                    currentReleaseBranch = currentName
                ),
                expected = Changelog(
                    title = currentName,
                    entries = listOf(
                        ChangelogEntry(
                            hash = "d2a85293746a6b11618e2bf1a68747ffdb2593a6",
                            taskId = "1",
                            message = "- 1: message message message",
                            foldId = "1(d2a85293)"
                        )
                    ),
                    shortEntries = listOf(
                        ChangelogEntry(
                            hash = "6ea76e31602cb734e24e094403ac0dabf20b52a2",
                            taskId = "2",
                            message = "- 2: message message message",
                            foldId = "2(6ea76e31)"
                        )
                    )
                )
            )
        }
    }
})

@Suppress("UNUSED_VARIABLE")
private fun FeatureBody.setUp() {
    val rootEntryRepository by memoized { mockk<RootEntryRepository>() }
    val logEntriesRepository by memoized { mockk<LogEntriesRepository>() }
}

private fun ScenarioBody.dataSetUp(
    currentName: String,
    rcName: String,
    rootCommit: String,
    allCommits: List<ChangelogEntry>
) {
    val rootEntryRepository: RootEntryRepository by memoized()
    val logEntriesRepository: LogEntriesRepository by memoized()

    Given("первый комит в релизе $currentName: $rootCommit") {
        every { rootEntryRepository.findRootEntry(rcName, currentName) } returns ChangelogEntry(
            hash = rootCommit
        )
    }
    And("лог с комита $rootCommit возвращает: $allCommits") {
        every { logEntriesRepository.logEntries(ChangelogEntry(rootCommit)) } returns allCommits
    }
}

private fun ScenarioBody.test(
    request: Request,
    expected: Changelog
) {
    val rootEntryRepository: RootEntryRepository by memoized()
    val logEntriesRepository: LogEntriesRepository by memoized()

    lateinit var result: Changelog
    When("происходит запрос генерации с $request") {
        result = ChangelogModel(rootEntryRepository, logEntriesRepository).compute(request)
    }

    Then("результат должен соответствывать: $expected") {
        assert(expected == result) {
            "\nexpected: $expected\nactual:   $result"
        }
    }
}
