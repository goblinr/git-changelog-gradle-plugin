package com.a65apps.changelog.di

import com.a65apps.changelog.data.repository.FileRepository
import com.a65apps.changelog.data.repository.GitLogEntriesRepository
import com.a65apps.changelog.data.repository.GitRootEntryRepository
import com.a65apps.changelog.domain.ChangelogInteractor
import com.a65apps.changelog.domain.model.ChangelogModel
import com.a65apps.changelog.domain.repository.IoRepository
import com.a65apps.changelog.domain.repository.LogEntriesRepository
import com.a65apps.changelog.domain.repository.RootEntryRepository
import com.a65apps.changelog.presentation.Renderer
import com.a65apps.changelog.presentation.mustache.MustacheRenderer
import com.a65apps.changelog.utils.initGit
import com.github.mustachejava.DefaultMustacheFactory
import org.gradle.api.Project
import java.io.File

class PluginContainer(
    private val project: Project,
    private val template: File,
    private val output: File,
    accessToken: String,
    developBranch: String,
    private val local: Boolean
) {

    private val git = initGit(project, accessToken, developBranch, local)
    private val mustacheFactory = DefaultMustacheFactory()

    fun provideChangelogInteractor(): ChangelogInteractor =
        ChangelogModel(
            rootEntryRepository = provideRootEntryRepository(),
            logEntriesRepository = provideLogEntriesRepository()
        )

    fun provideRenderer(): Renderer = MustacheRenderer(
        factory = mustacheFactory,
        ioRepository = provideIoRepository()
    )

    private fun provideRootEntryRepository(): RootEntryRepository =
        GitRootEntryRepository(
            project = project,
            git = git.first,
            local = local,
            head = git.second
        )

    private fun provideLogEntriesRepository(): LogEntriesRepository =
        GitLogEntriesRepository(
            git = git.first
        )

    private fun provideIoRepository(): IoRepository = FileRepository(
        templateFile = template,
        outputFile = output
    )
}
