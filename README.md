# Git changelog gradle plugin
Plugin for automatic generation of changelog from git repository

[![Actions Status](https://github.com/goblinr/git-changelog-gradle-plugin/workflows/Changelog%20generate/badge.svg)](https://github.com/goblinr/git-changelog-gradle-plugin/actions)
[![Actions Status](https://github.com/goblinr/git-changelog-gradle-plugin/workflows/Changelog%20test/badge.svg)](https://github.com/goblinr/git-changelog-gradle-plugin/actions)
[![codecov](https://codecov.io/gh/goblinr/git-changelog-gradle-plugin/branch/master/graph/badge.svg)](https://codecov.io/gh/goblinr/git-changelog-gradle-plugin)

## Usage example

```groovy
plugins {
    id 'com.a65apps.changelog' version '1.1.5'
}

changelog {
    def token = System.getenv().get("TOKEN")      // PAT token for access to a Git repository if repository is private
    
    currentVersion = '1.1'                        // Current release name, default is 'Unreleased'
    lastReleaseBranch = "releases/1"              // Last release branch, required field
    templateFile = "template/changelog.mustache"  // Template for render changelog.md, required field
    accessToken = token                           // Token for access to a Git repository, default is empty
    developBranch = 'master'                      // Develop branch, default is 'develop'
}
```

#### Template example
```handlebars
# Changelog

## {{title}}
{{#entries}}
{{message}}
{{/entries}}

## Folded
{{#shortEntries}}
{{foldId}}
{{/shortEntries}}
```
| field          | description                         |
| -------------- | ----------------------------------- |
| `title`        | currentVersion value                |
| `entries`      | log entries list                    |
| `message`      | short message from git commit       |
| `shortEntries` | folded entries list                 |
| `foldId`       | taskId(first 8 commit hash symbols) |

#### Run task
```
./gradlew changelog
```

Output result should be in `build/outputs/changelog.md` by default

#### Other configurations

```groovy
changelog {
    currentReleaseBranch = "releases/2"  // Specify current release branch
    local = true             // Forces to generation on a local copy of the repository
    characterLimit = 10_000  // Limit changelog to this character count
    outputFile = "$buildDir/path/to/changelog.md" // Custom output file path for generated changelog
    entryDash = "*"          // Custom log entry dash, default is '-'
    templateExtraCharactersLength = 29  // Extra character length for fine grained character limit configuration
    order = LogOrder.LAST_TO_FIRST  // Order of log entries. Default is LogOrder.FIRST_TO_LAST
}
```
