package com.crowdin.platform.data.remote

internal interface CrowdinRepository : RemoteRepository {

    fun getMapping(sourceLanguage: String, mappingCallback: MappingCallback) {}
}
