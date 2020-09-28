package com.a65apps.changelog.presentation.mustache

import com.a65apps.changelog.domain.entity.Changelog
import com.a65apps.changelog.domain.repository.IoRepository
import com.a65apps.changelog.presentation.Renderer
import com.github.mustachejava.MustacheFactory

internal class MustacheRenderer(
    factory: MustacheFactory,
    private val ioRepository: IoRepository
) : Renderer {

    private val mustache = factory.compile(ioRepository.template, ioRepository.templateName)

    override fun render(value: Changelog) {
        val writer = ioRepository.output
        mustache.execute(writer, value)
        writer.flush()
    }
}
