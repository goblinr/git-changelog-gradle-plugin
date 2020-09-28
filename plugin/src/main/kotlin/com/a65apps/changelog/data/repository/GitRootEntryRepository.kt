package com.a65apps.changelog.data.repository

import com.a65apps.changelog.domain.entity.ChangelogEntry
import com.a65apps.changelog.domain.repository.RootEntryRepository
import org.ajoberstar.grgit.Grgit
import org.gradle.api.Project
import java.io.ByteArrayOutputStream

private const val HEAD = "HEAD"
private const val MERGE_BASE = "git merge-base"
private const val CHECKOUT = "git checkout"

internal class GitRootEntryRepository(
    private val project: Project,
    private val git: Grgit,
    private val local: Boolean,
    private val head: String
) : RootEntryRepository {

    override fun findRootEntry(
        lastReleaseBranch: String,
        currentReleaseBranch: String
    ): ChangelogEntry {
        if (!local) {
            fetch(lastReleaseBranch, currentReleaseBranch)
        }

        checkout(lastReleaseBranch)
        checkout(currentReleaseBranch)
        checkout(head, true)

        val hash = mergeBase(lastReleaseBranch)

        println("Root commit: $hash")
        require(hash.isNotBlank()) {
            "Root commit of last release brunch is not found"
        }

        return ChangelogEntry(
            hash = hash
        )
    }

    private fun mergeBase(lastReleaseBrunch: String) = ByteArrayOutputStream()
        .run {
            project.exec {
                it.commandLine = "$MERGE_BASE $lastReleaseBrunch $HEAD".split(" ")
                it.workingDir = git.repository.rootDir
                it.standardOutput = this
            }.assertNormalExitValue()

            String(toByteArray()).trim()
        }

    private fun checkout(ref: String, force: Boolean = false) {
        val f = if (force) {
            "--force "
        } else {
            ""
        }
        println("git checkout $f$ref")
        project.exec {
            it.commandLine = "$CHECKOUT $f$ref".split(" ")
            it.workingDir = git.repository.rootDir
        }.assertNormalExitValue()
    }

    private fun fetch(lastReleaseBrunch: String, currentReleaseBranch: String) {
        git.fetch {
            it.prune = true
            it.remote = "origin"
            it.refSpecs = listOf(
                "refs/heads/*:refs/remotes/origin/*",
                "refs/heads/$lastReleaseBrunch:refs/remotes/origin/$lastReleaseBrunch",
                "refs/heads/$currentReleaseBranch:refs/remotes/origin/$currentReleaseBranch"
            )
        }
    }
}
