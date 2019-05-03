package com.crowdin.platform.data.remote

internal interface CrowdinRepository : RemoteRepository {

    fun getMapping(mappingCallback: MappingCallback) {}
}
