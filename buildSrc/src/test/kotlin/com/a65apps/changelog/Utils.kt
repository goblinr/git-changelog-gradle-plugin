package com.a65apps.changelog

import org.gradle.internal.impldep.org.junit.rules.TemporaryFolder
import org.spekframework.spek2.dsl.Root
import org.spekframework.spek2.lifecycle.CachingMode

fun Root.temporaryFolder() {
    val temporaryFolder by memoized(mode = CachingMode.EACH_GROUP) { TemporaryFolder() }

    beforeEachGroup {
        temporaryFolder.create()
    }

    afterEachGroup {
        temporaryFolder.delete()
    }
}
