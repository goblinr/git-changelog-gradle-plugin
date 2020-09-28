package com.a65apps.changelog.domain.repository

import java.io.Reader
import java.io.Writer

interface IoRepository {
    val template: Reader
    val templateName: String
    val output: Writer
}
