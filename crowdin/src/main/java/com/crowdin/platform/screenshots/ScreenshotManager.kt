package com.crowdin.platform.screenshots

import android.graphics.Bitmap
import com.crowdin.platform.repository.model.LanguageData
import com.crowdin.platform.repository.model.ViewData
import com.crowdin.platform.repository.remote.api.CrowdinApi

internal object ScreenshotManager {

    private lateinit var crowdinApi: CrowdinApi
    private lateinit var bitmap: Bitmap
    private var mappingData: LanguageData? = null
    private lateinit var resourceKeys: List<ViewData>

    operator fun invoke(crowdinApi: CrowdinApi, bitmap: Bitmap, mappingData: LanguageData?, resourceKeys: List<ViewData>) {
        this.crowdinApi = crowdinApi
        this.bitmap = bitmap
        this.mappingData = mappingData
        this.resourceKeys = resourceKeys
    }

    // TODO: add network request. Find mapping IDs.
    fun sendScreenshot() {

    }
}