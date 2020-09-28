package com.a65apps.changelog.data.repository

import com.a65apps.changelog.domain.repository.IoRepository
import java.io.File

internal class FileRepository(
    templateFile: File,
    outputFile: File
) : IoRepository {

    override val template = templateFile.reader()
    override val templateName: String = templateFile.name
    override val output = outputFile.writer()
}
