package com.a65apps.changelog.utils

import com.a65apps.changelog.domain.entity.JobInfo
import org.ajoberstar.grgit.Credentials
import org.ajoberstar.grgit.Grgit
import org.gradle.api.Project
import java.io.ByteArrayOutputStream

fun initGit(
    project: Project,
    accessToken: String,
    userName: String?,
    developBranch: String,
    local: Boolean
): Pair<Grgit, JobInfo> {
    val tmp = Grgit.open {
        it.dir = project.rootDir
        it.credentials =
            userName?.let { name -> Credentials(name, accessToken) } ?: Credentials(accessToken)
    }
    val head = tmp.log().first().id
    val branch = ByteArrayOutputStream().run {
        project.exec {
            it.commandLine = "git branch -a --contains $head".split(" ")
            it.workingDir = project.rootDir
            it.standardOutput = this
        }.assertNormalExitValue()
        String(toByteArray()).trim()
    }.findBranch() ?: ""

    if (local) {
        return tmp to JobInfo(head, branch)
    }

    val uri = tmp.remote.list().first().url

    return Grgit.clone {
        it.dir = "${project.buildDir}/sources"
        it.credentials =
            userName?.let { name -> Credentials(name, accessToken) } ?: Credentials(accessToken)
        it.uri = uri
        it.refToCheckout = developBranch
    } to JobInfo(head, branch)
}

private fun String.findBranch() = split("\n").filter {
    !it.contains("HEAD")
}.map { it.replace("*", "") }
    .map { it.replace("remotes/origin/", "") }
    .map { it.trim() }
    .toSet()
    .firstOrNull()
