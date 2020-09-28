package com.a65apps.changelog

import org.gradle.api.Plugin
import org.gradle.api.Project

private const val PLUGIN_NAME = "changelog"
private const val PLUGIN_GROUP = "Release Plugin"
private const val PLUGIN_DESCRIPTION = "Generate changelog.md"

@Suppress("unused")
class ChangelogPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.extensions.create(PLUGIN_NAME, ChangelogExtension::class.java)

        project.tasks.register(PLUGIN_NAME, ChangelogTask::class.java) {
            it.group = PLUGIN_GROUP
            it.description = PLUGIN_DESCRIPTION
            it.currentVersion = extension.currentVersion
            it.currentReleaseBranch = extension.currentReleaseBranch
            it.lastReleaseBranch = extension.lastReleaseBranch
            it.developBranch = extension.developBranch
            it.characterLimit = extension.characterLimit
            it.outputFile = extension.outputFile
            it.templateFile = extension.templateFile
            it.entryDash = extension.entryDash
            it.templateExtraCharactersLength = extension.templateExtraCharactersLength
            it.accessToken = extension.accessToken ?: ""
            it.local = extension.local
        }
    }
}
