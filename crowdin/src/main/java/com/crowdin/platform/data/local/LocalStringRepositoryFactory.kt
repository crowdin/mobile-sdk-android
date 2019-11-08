package com.crowdin.platform.data.local

import android.content.Context

import com.crowdin.platform.CrowdinConfig

internal object LocalStringRepositoryFactory {

    fun createLocalRepository(context: Context, config: CrowdinConfig): LocalRepository =
        when {
            config.isPersist -> SharedPrefLocalRepository(context, MemoryLocalRepository())
            else -> MemoryLocalRepository()
        }
}
