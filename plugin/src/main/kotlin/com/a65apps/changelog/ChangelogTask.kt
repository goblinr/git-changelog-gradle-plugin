package com.a65apps.changelog

import com.a65apps.changelog.di.PluginContainer
import com.a65apps.changelog.domain.entity.Request
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File

private const val DEFAULT_OUTPUT_PATH = "/outputs/changelog.md"

open class ChangelogTask : DefaultTask() {

    @get:Input
    var currentVersion: String = DEFAULT_CURRENT_VERSION

    @get:Input
    var currentReleaseBranch: String = ""

    @get:Input
    var lastReleaseBranch: String = ""

    @get:Input
    var developBranch: String = DEVELOP_BRANCH

    @get:Input
    var characterLimit = DEFAULT_CHARACTER_LIMIT

    @get:Input
    var outputFile = ""

    @get:Input
    var templateFile = ""

    @get:Input
    var entryDash = DEFAULT_ENTRY_DASH

    @get:Input
    var templateExtraCharactersLength = 0

    @get:Input
    var accessToken = ""

    @get:Input
    @get:Optional
    var userName: String? = null

    @get:Input
    var local = false

    @get:Input
    var order = LogOrder.FIRST_TO_LAST

    @get:Input
    var minEntryCount = MIN_ENTRY_COUNT

    @OutputFile
    fun getDestination(): File = if (outputFile.isBlank()) {
        project.file("${project.buildDir.path}$DEFAULT_OUTPUT_PATH")
    } else {
        project.file(outputFile)
    }

    @TaskAction
    fun execute() {
        require(lastReleaseBranch.isNotBlank()) {
            "please specify lastReleaseBrunch"
        }
        require(templateFile.isNotBlank()) {
            "please specify templateFile for changelog renderer"
        }

        val file = getDestination()
        file.parentFile.mkdirs()
        val container = PluginContainer(
            project = project,
            accessToken = accessToken,
            userName = userName,
            developBranch = developBranch,
            template = project.file(templateFile),
            output = file,
            local = local
        )

        val interactor = container.provideChangelogInteractor()
        val renderer = container.provideRenderer()

        renderer.render(
            interactor.compute(
                Request(
                    currentVersion = currentVersion,
                    currentReleaseBranch = currentReleaseBranch,
                    lastReleaseBranch = lastReleaseBranch,
                    characterLimit = characterLimit,
                    entryDash = entryDash,
                    templateExtraCharactersLength = templateExtraCharactersLength,
                    order = order,
                    minEntryCount = minEntryCount
                )
            )
        )
    }
}
