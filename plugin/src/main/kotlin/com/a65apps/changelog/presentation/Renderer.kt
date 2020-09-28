package com.a65apps.changelog.presentation

import com.a65apps.changelog.domain.entity.Changelog

interface Renderer {
    fun render(value: Changelog)
}
