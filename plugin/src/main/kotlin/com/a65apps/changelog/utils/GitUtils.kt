package com.a65apps.changelog.utils

import org.ajoberstar.grgit.Credentials
import org.ajoberstar.grgit.Grgit
import org.gradle.api.Project

fun initGit(
    project: Project,
    accessToken: String,
    developBranch: String,
    local: Boolean
): Pair<Grgit, String> {
    val tmp = Grgit.open {
        it.dir = project.rootDir
        it.credentials = Credentials(accessToken)
    }
    val head = tmp.log().first().id
    if (local) {
        return tmp to head
    }

    val uri = tmp.remote.list().first().url

    return Grgit.clone {
        it.dir = "${project.buildDir}/sources"
        it.credentials = Credentials(accessToken)
        it.uri = uri
        it.refToCheckout = developBranch
    } to head
}
