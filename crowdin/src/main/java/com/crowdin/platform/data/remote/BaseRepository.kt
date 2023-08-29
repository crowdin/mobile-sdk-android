package com.crowdin.platform.data.remote

internal abstract class BaseRepository : RemoteRepository {

    internal companion object {
        const val HEADER_ETAG = "ETag"
        const val HEADER_ETAG_EMPTY = ""
    }

    protected var eTagMap = mutableMapOf<String, String>()
}
