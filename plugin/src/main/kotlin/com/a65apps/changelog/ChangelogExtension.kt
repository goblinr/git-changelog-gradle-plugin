package com.a65apps.changelog

internal const val DEFAULT_CHARACTER_LIMIT = 5000
internal const val DEFAULT_CURRENT_VERSION = "Unreleased"
internal const val DEFAULT_ENTRY_DASH = "-"
internal const val DEVELOP_BRANCH = "develop"
internal const val MIN_ENTRY_COUNT = 10

open class ChangelogExtension {
    var currentVersion = DEFAULT_CURRENT_VERSION
    var currentReleaseBranch = ""
    var lastReleaseBranch = ""
    var developBranch = DEVELOP_BRANCH
    var characterLimit = DEFAULT_CHARACTER_LIMIT
    var outputFile = ""
    var templateFile = ""
    var entryDash = DEFAULT_ENTRY_DASH
    var templateExtraCharactersLength = 0
    var accessToken: String? = ""
    var userName: String? = null
    var local = false
    var order = LogOrder.FIRST_TO_LAST
    var minEntryCount = MIN_ENTRY_COUNT
}
