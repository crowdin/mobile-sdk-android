package com.crowdin.platform.repository.local

import android.content.Context

import com.crowdin.platform.CrowdinConfig

internal object LocalStringRepositoryFactory {

    internal fun createLocalRepository(context: Context, config: CrowdinConfig): LocalRepository =
            when {
                config.isPersist -> SharedPrefLocalRepository(context)
                else -> MemoryLocalRepository()
            }
}
