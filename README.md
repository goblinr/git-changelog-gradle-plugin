# Git changelog gradle plugin
Plugin for automatic generation of changelog from git repository

## Usage example

```groovy
plugins {
    id 'com.a65apps.changelog' version '1.1'
}

changelog {
    def token = System.getenv().get("TOKEN")      // PAT token for access to a Git repository if repository is private
    
    currentVersion = '1.1'                        // Current release name, default is 'Unreleased'
    currentReleaseBranch = "releases/2"           // Current release branch, required field
    lastReleaseBranch = "releases/1"              // Last release branch, required field
    templateFile = "template/changelog.mustache"  // Template for render changelog.md, required field
    accessToken = token                           // Token for access to a Git repository, default is empty
    developBranch = 'master'                      // Develop branch, default is 'develop'
}
```

#### Run task
```
./gradlew changelog
```

Output result should be in `build/outputs/changelog.md` by default

#### Other configurations

```groovy
changelog {
    local = true             // Forces to generation on a local copy of the repository
    characterLimit = 10_000  // Limit changelog to this character count
    outputFile = "$buildDir/path/to/changelog.md" // Custom output file path for generated changelog
    entryDash = "*"          // Custom log entry dash, default is '-'
    templateExtraCharactersLength = 29  // Extra character length for fine grained character limit configuration
}
```
