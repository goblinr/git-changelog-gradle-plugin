package com.a65apps.changelog.domain

import com.a65apps.changelog.domain.entity.Changelog
import com.a65apps.changelog.domain.entity.Request

interface ChangelogInteractor {
    fun compute(request: Request): Changelog
}
