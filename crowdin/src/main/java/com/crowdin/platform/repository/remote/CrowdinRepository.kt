package com.crowdin.platform.repository.remote

internal interface CrowdinRepository : RemoteRepository {

    fun getMapping(mappingCallback: MappingCallback) {}
}
