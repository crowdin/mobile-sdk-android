package com.crowdin.platform.screenshots

import android.graphics.Bitmap
import com.crowdin.platform.repository.StringDataManager
import com.crowdin.platform.repository.model.LanguageData
import com.crowdin.platform.repository.model.ViewData
import com.crowdin.platform.repository.remote.api.CrowdinApi

internal object ScreenshotManager {

    private lateinit var crowdinApi: CrowdinApi
    private lateinit var bitmap: Bitmap
    private lateinit var stringDataManager: StringDataManager
    private lateinit var viewDataList: List<ViewData>

    operator fun invoke(crowdinApi: CrowdinApi, bitmap: Bitmap, stringDataManager: StringDataManager, viewDataList: List<ViewData>) {
        this.crowdinApi = crowdinApi
        this.bitmap = bitmap
        this.stringDataManager = stringDataManager
        this.viewDataList = viewDataList
    }

    // TODO: add network request. Find mapping IDs.
    fun sendScreenshot() {
        val mappingData = stringDataManager.getMapping() ?: return
        val crtKeySpec = stringDataManager.getCookies()
        val mappingIds = getMappingIDs(mappingData, viewDataList)
    }

    private fun getMappingIDs(mappingData: LanguageData, viewDataList: List<ViewData>): Any {
        val list = mutableListOf<String>()

        for (viewData in viewDataList) {
            val resKey = viewData.resourceKey
            val mappingValue = getMappingValueForKey(resKey, mappingData)
            mappingValue?.let { list.add(it) }
        }

        return list
    }

    private fun getMappingValueForKey(resKey: String, mappingData: LanguageData): String? {
        val resources = mappingData.resources
        val arrays = mappingData.arrays
        val plurals = mappingData.plurals

        for (resource in resources) {
            if (resource.stringKey == resKey) {
                return resource.stringValue
            }
        }

        // TODO: array plural, nothing to compare. Check ViewData to extend
        for (array in arrays) {
        }
        for (plural in plurals) {
        }

        return null
    }
}