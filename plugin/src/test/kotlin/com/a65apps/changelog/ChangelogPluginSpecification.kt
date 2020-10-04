package com.a65apps.changelog

import org.ajoberstar.grgit.Grgit
import org.ajoberstar.grgit.Person
import org.gradle.internal.impldep.org.junit.rules.TemporaryFolder
import org.gradle.internal.impldep.org.testng.AssertJUnit.assertEquals
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import org.spekframework.spek2.style.gherkin.ScenarioBody
import java.io.File

object ChangelogPluginSpecification : Spek({
    temporaryFolder()

    Feature("плагин должен уметь генерировать changelog.md") {
        val settings = """
                rootProject.name = "changelog"
            """.trimIndent()

        val template = """
                # Changelog

                ## {{title}}
                {{#entries}}
                {{message}}
                {{/entries}}
                
                ## Folded
                {{#shortEntries}}
                {{foldId}}
                {{/shortEntries}}

            """.trimIndent()

        val expected = """
                # Changelog
                
                ## 1.1
                - 3: changes
                - 4: rc_1.1 fix
                
                ## Folded
                
            """.trimIndent()

        Scenario("успешная генерация changelog.md") {
            val build = """
                plugins {
                    id "com.a65apps.changelog"
                }
                
                changelog {
                    currentVersion = "1.1"
                    currentReleaseBranch = "rc_1.1"
                    lastReleaseBranch = "rc_1.0"
                    templateFile = "template/changelog.mustache"
                    local = true
                }
            """.trimIndent()

            test(
                settings = settings,
                template = template,
                build = build,
                expected = expected
            )
        }

        Scenario("успешная генерация changelog.md без указания текущей ветки релиза") {
            val build = """
                plugins {
                    id "com.a65apps.changelog"
                }
                
                changelog {
                    currentVersion = "1.1"
                    lastReleaseBranch = "rc_1.0"
                    templateFile = "template/changelog.mustache"
                    local = true
                }
            """.trimIndent()

            test(
                settings = settings,
                template = template,
                build = build,
                expected = expected
            )
        }
    }
})

private fun ScenarioBody.test(
    settings: String,
    template: String,
    build: String,
    expected: String
) {
    lateinit var settingsFile: File
    lateinit var buildFile: File
    lateinit var templateFile: File

    val temporaryFolder: TemporaryFolder by memoized()

    Given("подготовлены файлы проекта") {
        settingsFile = temporaryFolder.newFile("settings.gradle")
        buildFile = temporaryFolder.newFile("build.gradle")
        temporaryFolder.newFolder("template")
        templateFile = temporaryFolder.newFile("template/changelog.mustache")
        templateFile.mkdirs()
    }

    Given("содержимое settings.gradle: $settings") {
        settingsFile.writeText(settings)
    }
    And("содержимое build.gradle: $build") {
        buildFile.writeText(build)
    }
    And("содержимое changelog.mustache: $template") {
        templateFile.writeText(template)
    }

    Given("подготовлен git репозиторий") {
        val author = Person("anon", "anon@anon.com")
        val git = Grgit.init {
            it.dir = temporaryFolder.root
        }

        temporaryFolder.newFile("init").writeText("init")
        git.commit {
            it.author = author
            it.committer = author
            it.message = "init"
            it.all = true
        }

        git.checkout {
            it.branch = "develop"
            it.createBranch = true
        }

        temporaryFolder.newFile("rc_1.0").writeText("rc_1.0")
        git.commit {
            it.author = author
            it.committer = author
            it.message = "1: rc_1.0"
            it.all = true
        }

        git.checkout {
            it.branch = "rc_1.0"
            it.createBranch = true
        }

        temporaryFolder.newFile("rc_1.0_fix").writeText("rc_1.0_fix")
        git.commit {
            it.author = author
            it.committer = author
            it.message = "2: rc_1.0 fix"
            it.all = true
        }

        git.checkout { it.branch = "develop" }

        temporaryFolder.newFile("changes").writeText("changes")
        git.commit {
            it.author = author
            it.committer = author
            it.message = "3: changes"
            it.all = true
        }

        git.checkout {
            it.branch = "rc_1.1"
            it.createBranch = true
        }

        temporaryFolder.newFile("rc_1.1_fix").writeText("rc_1.1_fix")
        git.commit {
            it.author = author
            it.committer = author
            it.message = "4: rc_1.1 fix"
            it.all = true
        }
        git.log().forEach {
            println(it.shortMessage)
        }
    }

    lateinit var result: BuildResult
    When("происходит запуск задачи changelog") {
        result = GradleRunner.create()
            .withPluginClasspath()
            .withProjectDir(temporaryFolder.root)
            .forwardOutput()
            .withArguments("changelog")
            .withJaCoCo()
            .build()
    }

    Then("задача завершилась с успехом") {
        assertEquals(TaskOutcome.SUCCESS, result.task(":changelog")?.outcome)
    }
    And("содержимое changelog.md соответствует ожиданиям: $expected") {
        val changelog = File(temporaryFolder.root, "build/outputs/changelog.md")
        val text = changelog.readText()
        assertEquals(expected, text)
    }
}
