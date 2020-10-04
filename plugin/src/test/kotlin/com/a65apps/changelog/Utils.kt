package com.a65apps.changelog

import org.gradle.internal.impldep.org.junit.rules.TemporaryFolder
import org.gradle.testkit.runner.GradleRunner
import org.spekframework.spek2.dsl.Root
import org.spekframework.spek2.lifecycle.CachingMode
import java.io.File
import java.io.InputStream

fun Root.temporaryFolder() {
    val temporaryFolder by memoized(mode = CachingMode.EACH_GROUP) { TemporaryFolder() }

    beforeEachGroup {
        temporaryFolder.create()
    }

    afterEachGroup {
        temporaryFolder.delete()
    }
}

fun GradleRunner.withJaCoCo(): GradleRunner {
    javaClass.classLoader.getResourceAsStream("testkit-gradle.properties")
        ?.toFile(File(projectDir, "gradle.properties"))
    return this
}

private fun InputStream.toFile(file: File) {
    use { input ->
        file.outputStream().use { input.copyTo(it) }
    }
}
